package com.demo.githubapi.controller;

import com.demo.githubapi.model.dto.RepositoryResponseDto;
import com.demo.githubapi.service.GitHubService;
import org.kohsuke.github.GHFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubService gitHubService;

    @Autowired
    public GitHubController(final GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping(value = "/user/{login}/repositories", produces = "!application/xml")
    public ResponseEntity<List<RepositoryResponseDto>> userNotForkedRepositoriesByUserLogin(final @PathVariable String login) throws GHFileNotFoundException {
        return ResponseEntity.ok(gitHubService.getUserNotForkedRepositoriesByUserLogin(login));
    }
}
