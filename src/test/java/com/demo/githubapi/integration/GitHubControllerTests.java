package com.demo.githubapi.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** For the purpose of integration tests, a special GitHub test user was created.
 * Only the author of the application - Dudek Jakub has access to the test user.
 * The test user is not subject to any modifications.*/

@SpringBootTest
@AutoConfigureMockMvc
class GitHubControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private static final String realTestUserLogin = "TestAccountUser1";

    @Test
    void testGetUserNotForkedRepositoriesByUserLogin_withAcceptableMediaTypeJSON_shouldReturnOk() throws Exception {
        //When/Then
        mockMvc.perform(get("/api/github/repositories/user/" + realTestUserLogin)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].repositoryName").value("testRepo1"))
                .andExpect(jsonPath("$[0].ownerLogin").value(realTestUserLogin))
                .andExpect(jsonPath("$[0].branches").isArray())
                .andExpect(jsonPath("$[0].branches[0].branchName").value("master"))
                .andExpect(jsonPath("$[0].branches[0].lastCommitSha").value("59913eaea6daa83d2a9339b96005cdea060450ef"))
                .andExpect(jsonPath("$[0].branches[1].branchName").value("testBranch"))
                .andExpect(jsonPath("$[0].branches[1].lastCommitSha").value("fd5822b08901c2f1dd85a405882ec5907b2295d4"));
    }

    @Test
    void testGetUserNotForkedRepositoriesByUserLogin_withUnacceptableMediaTypeXML_shouldReturnNotAcceptable() throws Exception {
        //When/Then
        mockMvc.perform(get("/api/github/repositories/user/" + realTestUserLogin)
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_ACCEPTABLE.value()))
                .andExpect(jsonPath("$.message").value("No acceptable representation"));
    }
}