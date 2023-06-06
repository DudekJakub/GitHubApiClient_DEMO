package com.demo.githubapi.service;

import com.demo.githubapi.exception.GitHubServiceException;
import com.demo.githubapi.model.dto.RepositoryResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GitHubServiceUnitTests {

    @Mock
    private GitHub gitHub;

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
}