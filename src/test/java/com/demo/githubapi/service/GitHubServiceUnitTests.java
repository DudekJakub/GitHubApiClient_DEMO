package com.demo.githubapi.service;

import com.demo.githubapi.exception.GitHubServiceException;
import com.demo.githubapi.model.dto.BranchResponseDto;
import com.demo.githubapi.model.dto.RepositoryResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GitHubServiceUnitTests {

    @Mock
    private GitHub gitHub;

    @Mock
    private GHRepository ghRepository;

    @InjectMocks
    private GitHubService service;

    @Test
    void getUserNotForkedRepositoriesByUserLogin_shouldReturnRepositoryResponseDtoList() throws IOException {
        //Given
        String providedLogin = "mockUserLogin";
        String myselfLogin = "mockMyselfLogin";
        GHUser mockUser = mock(GHUser.class);
        GHMyself mockMyself = mock(GHMyself.class);
        GHRepository mockNotForkedRepository1 = mock(GHRepository.class);
        GHRepository mockForkedRepository2 = mock(GHRepository.class);
        Map<String, GHRepository> mockRepositoryMap = new HashMap<>();
        mockRepositoryMap.put("repo1", mockNotForkedRepository1);
        mockRepositoryMap.put("repo2", mockForkedRepository2);

        when(gitHub.getMyself()).thenReturn(mockMyself);
        when(gitHub.getMyself().getLogin()).thenReturn(myselfLogin);
        when(gitHub.getUser(providedLogin)).thenReturn(mockUser);
        when(mockUser.getRepositories()).thenReturn(mockRepositoryMap);
        when(mockNotForkedRepository1.isFork()).thenReturn(false);
        when(mockForkedRepository2.isFork()).thenReturn(true);
        when(mockNotForkedRepository1.getOwner()).thenReturn(mockUser);

        //When
        List<RepositoryResponseDto> result = service.getUserNotForkedRepositoriesByUserLogin(providedLogin);

        //Then
        assertEquals(1, result.size());
        verify(gitHub, times(1)).getUser(providedLogin);
        verify(mockNotForkedRepository1, times(1)).isFork();
        verify(mockForkedRepository2, times(1)).isFork();
    }

    @Test
    void getUserNotForkedRepositoriesByUserLogin_shouldThrowGHFileNotFoundException() throws IOException {
        //Given
        String providedLogin = "nonExistentUserLogin";
        String myselfLogin = "mockMyselfLogin";
        GHUser mockUser = mock(GHUser.class);
        GHMyself mockMyself = mock(GHMyself.class);
        GHFileNotFoundException mockException = mock(GHFileNotFoundException.class);

        when(gitHub.getMyself()).thenReturn(mockMyself);
        when(gitHub.getMyself().getLogin()).thenReturn(myselfLogin);
        when(gitHub.getUser(providedLogin)).thenThrow(mockException);
        when(mockException.getMessage()).thenReturn("NOT FOUND");

        //When/Then
        assertThrows(GHFileNotFoundException.class, () -> service.getUserNotForkedRepositoriesByUserLogin(providedLogin));
        verify(gitHub, times(2)).getMyself();
        verify(gitHub, times(1)).getUser(providedLogin);
        verify(gitHub.getMyself(), times(1)).getLogin();
        verify(mockUser, times(0)).getRepositories();
        verify(mockException, times(1)).getMessage();
    }

    @Test
    void getUserNotForkedRepositoriesByUserLogin_shouldThrowGitHubServiceException() throws IOException {
        //Given
        String providedLogin = "mockUserLogin";
        String myselfLogin = "mockMyselfLogin";
        GHMyself mockMyself = mock(GHMyself.class);

        when(gitHub.getMyself()).thenReturn(mockMyself);
        when(gitHub.getMyself().getLogin()).thenReturn(myselfLogin);
        when(gitHub.getUser(providedLogin)).thenThrow(new IOException("Some error occurred"));

        //When/Then
        GitHubServiceException exception = assertThrows(GitHubServiceException.class,
                () -> service.getUserNotForkedRepositoriesByUserLogin(providedLogin));
        assertEquals("There was a problem reading user.", exception.getMessage());
    }

    @Test
    void prepareRepositoryResponseDto_shouldReturnsDtoWithCorrectValues() throws IOException {
        //Given
        GHUser mockOwner = mock(GHUser.class);
        GHBranch mockBranch = mock(GHBranch.class);

        String repositoryName = "test-repo";
        String ownerLogin = "test-owner";
        String branchName = "branch1";
        String lastCommitSha = "12345";

        when(ghRepository.getName()).thenReturn(repositoryName);
        when(ghRepository.getOwner()).thenReturn(mockOwner);
        when(mockOwner.getLogin()).thenReturn(ownerLogin);
        when(ghRepository.getBranches()).thenReturn(Map.of("branch1", mockBranch));
        when(mockBranch.getSHA1()).thenReturn(lastCommitSha);

        //When
        RepositoryResponseDto resultDto = service.prepareRepositoryResponseDto(ghRepository);

        //Then
        assertNotNull(resultDto);
        assertEquals(repositoryName, resultDto.getRepositoryName());
        assertEquals(ownerLogin, resultDto.getOwnerLogin());

        List<BranchResponseDto> branches = resultDto.getBranches();
        assertNotNull(branches);
        assertEquals(1, branches.size());

        BranchResponseDto branchDto = branches.get(0);
        assertEquals(branchName, branchDto.getBranchName());
        assertEquals(lastCommitSha, branchDto.getLastCommitSha());
    }

    @Test
    void prepareRepositoryResponseDto_shouldThrowsGitHubServiceException() throws IOException {
        //Given
        when(ghRepository.getOwner()).thenThrow(new IOException());

        //When/Then
        GitHubServiceException exception = assertThrows(GitHubServiceException.class, () -> service.prepareRepositoryResponseDto(ghRepository));
        assertEquals("There was a problem reading repository owner.", exception.getMessage());
        verify(ghRepository, times(1)).getOwner();
    }

    @Test
    void prepareBranchResponseDtos_shouldFinishSuccessfully() throws IOException {
        //Given
        GHBranch branch1 = mock(GHBranch.class);
        GHBranch branch2 = mock(GHBranch.class);

        Map<String, GHBranch> branchesMap = new LinkedHashMap<>();
        branchesMap.put("branch1", branch1);
        branchesMap.put("branch2", branch2);

        when(branch1.getSHA1()).thenReturn("commit1");
        when(branch2.getSHA1()).thenReturn("commit2");
        when(ghRepository.getBranches()).thenReturn(branchesMap);

        //When
        List<BranchResponseDto> branchResponseDtos = service.prepareBranchResponseDtos(ghRepository);

        //Then
        assertEquals(2, branchResponseDtos.size());

        BranchResponseDto branchResponseDto1 = branchResponseDtos.get(0);
        assertEquals("branch1", branchResponseDto1.getBranchName());
        assertEquals("commit1", branchResponseDto1.getLastCommitSha());

        BranchResponseDto branchResponseDto2 = branchResponseDtos.get(1);
        assertEquals("branch2", branchResponseDto2.getBranchName());
        assertEquals("commit2", branchResponseDto2.getLastCommitSha());

        verify(ghRepository, times(1)).getBranches();
    }

    @Test
    void prepareBranchResponseDtos_shouldThrowGitHubServiceException() throws IOException {
        //Given
        when(ghRepository.getBranches()).thenThrow(IOException.class);

        //When/Then
        GitHubServiceException exception = assertThrows(GitHubServiceException.class, () -> service.prepareBranchResponseDtos(ghRepository));
        assertEquals("There was a problem reading branches.", exception.getMessage());
        verify(ghRepository, times(1)).getBranches();
    }
}