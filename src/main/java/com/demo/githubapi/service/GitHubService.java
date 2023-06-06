package com.demo.githubapi.service;

import com.demo.githubapi.exception.GitHubServiceException;
import com.demo.githubapi.model.dto.BranchResponseDto;
import com.demo.githubapi.model.dto.RepositoryResponseDto;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class GitHubService {

    private final GitHub gitHub;

    @Autowired
    public GitHubService(final GitHub gitHub) {
        this.gitHub = gitHub;
    }

    public List<RepositoryResponseDto> getUserNotForkedRepositoriesByUserLogin(final String login) throws GHFileNotFoundException {
        try {
            GHUser user = gitHub.getMyself().getLogin().equals(login) ? gitHub.getMyself() : gitHub.getUser(login);
            Map<String, GHRepository> ghRepositoryMap = user.getRepositories();

            List<RepositoryResponseDto> repositories = ghRepositoryMap.values()
                    .parallelStream()
                    .filter(ghRepository -> !ghRepository.isFork())
                    .filter(ghRepository -> getOwnerLogin(ghRepository).equals(login))
                    .map(this::prepareRepositoryResponseDto)
                    .toList();

            log.info("[{}] not forked repositories found for login [{}]", repositories.size(), login);

            return repositories;
        } catch (IOException e) {
            if (e.getMessage().toUpperCase().contains("NOT FOUND")) {
                throw new GHFileNotFoundException("Given login [" + login + "] does not exist.");
            }
            throw new GitHubServiceException("There was a problem reading user.");
        }
    }

    private RepositoryResponseDto prepareRepositoryResponseDto(final GHRepository ghRepository) {
        return RepositoryResponseDto.builder()
                .repositoryName(ghRepository.getName())
                .ownerLogin(getOwnerLogin(ghRepository))
                .branches(prepareBranchResponseDtos(ghRepository))
                .build();
    }

    private List<BranchResponseDto> prepareBranchResponseDtos(final GHRepository ghRepository) {
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

    private String getOwnerLogin(final GHRepository ghRepository) {
        try {
            return ghRepository.getOwner().getLogin();
        } catch (IOException e) {
            throw new GitHubServiceException("There was a problem reading repository owner.");
        }
    }
}
