package com.demo.githubapi.exception;

public class GitHubServiceException extends RuntimeException {

    public GitHubServiceException(final String message) {
        super(message);
    }
}
