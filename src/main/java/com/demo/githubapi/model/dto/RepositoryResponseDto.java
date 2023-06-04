package com.demo.githubapi.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RepositoryResponseDto {

    @NotBlank
    private String repositoryName;

    @NotBlank
    private String ownerLogin;

    private List<BranchResponseDto> branches;
}
