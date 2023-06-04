package com.demo.githubapi.service;

import com.demo.githubapi.exception.GitHubServiceException;
import com.demo.githubapi.model.dto.BranchResponseDto;
import com.demo.githubapi.model.dto.RepositoryResponseDto;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GitHubService {

    private final GitHub gitHub;

    @Autowired
    public GitHubService(GitHub gitHub) {
        this.gitHub = gitHub;
    }

    public List<RepositoryResponseDto> getUserNotForkedRepositoriesByUserLogin(final String login) throws GHFileNotFoundException {
        try {
            GHUser user = gitHub.getMyself().getLogin().equals(login) ? gitHub.getMyself() : gitHub.getUser(login);
            Map<String, GHRepository> ghRepositoryMap = user.getRepositories();
            return ghRepositoryMap.values()
                    .parallelStream()
                    .filter(ghRepository -> !ghRepository.isFork())
                    .map(this::prepareRepositoryResponseDto)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            if (e.getMessage().toUpperCase().contains("NOT FOUND")) {
                throw new GHFileNotFoundException("Given login [" + login + "] does not exist.");
            }
            throw new GitHubServiceException("There was a problem reading user.");
        }
    }

    RepositoryResponseDto prepareRepositoryResponseDto(final GHRepository ghRepository) {
        try {
            return RepositoryResponseDto.builder()
                    .repositoryName(ghRepository.getName())
                    .ownerLogin(ghRepository.getOwner().getLogin())
                    .branches(prepareBranchResponseDtos(ghRepository))
                    .build();
        } catch (IOException e) {
            throw new GitHubServiceException("There was a problem reading repository owner.");
        }
    }

    List<BranchResponseDto> prepareBranchResponseDtos(final GHRepository ghRepository) {
        try {
            return ghRepository.getBranches().entrySet()
                    .stream()
                    .map(entry ->
                            BranchResponseDto.builder()
                                    .branchName(entry.getKey())
                                    .lastCommitSha(entry.getValue().getSHA1())
                                    .build())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new GitHubServiceException("There was a problem reading branches.");
        }
    }
}
