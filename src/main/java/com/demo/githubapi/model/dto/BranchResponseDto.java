package com.demo.githubapi.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BranchResponseDto {

    @NotBlank
    private String branchName;

    @NotBlank
    private String lastCommitSha;
}
