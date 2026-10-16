package io.github.selenap.purrfectmatch.adoption;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;

/** Boots the whole application (H2 + Flyway migrations) and runs the full adoption flow. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdoptionFlowIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Test
    void fullAdoptionFlow() throws Exception {
        long catId = idOf(mvc.perform(post("/api/v1/cats").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Pistache\",\"sex\":\"MALE\"}"))
                .andExpect(status().isCreated()));

        long alex = submit(catId, "alex@example.com");
        long sam = submit(catId, "sam@example.com");

        // same person, same cat: rejected
        mvc.perform(post("/api/v1/cats/{id}/adoption-requests", catId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"applicantName\":\"Alex\",\"applicantEmail\":\"ALEX@example.com\"}"))
                .andExpect(status().isConflict());

        mvc.perform(post("/api/v1/adoption-requests/{id}/approve", alex))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
        mvc.perform(get("/api/v1/cats/{id}", catId))
                .andExpect(jsonPath("$.status").value("RESERVED"));

        // the cat is reserved: the second request cannot be approved
        mvc.perform(post("/api/v1/adoption-requests/{id}/approve", sam))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Operation not allowed"));

        mvc.perform(post("/api/v1/adoption-requests/{id}/complete", alex))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
        mvc.perform(get("/api/v1/cats/{id}", catId))
                .andExpect(jsonPath("$.status").value("ADOPTED"));

        // the other requests were rejected automatically
        mvc.perform(get("/api/v1/cats/{id}/adoption-requests", catId))
                .andExpect(jsonPath("$.content[?(@.id == %d)].status", sam).value("REJECTED"));
    }

    private long submit(long catId, String email) throws Exception {
        return idOf(mvc.perform(post("/api/v1/cats/{id}/adoption-requests", catId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"applicantName\":\"Test\",\"applicantEmail\":\"" + email + "\"}"))
                .andExpect(status().isCreated()));
    }

    private static long idOf(org.springframework.test.web.servlet.ResultActions result) throws Exception {
        return ((Number) JsonPath.read(result.andReturn().getResponse().getContentAsString(), "$.id")).longValue();
    }
}
