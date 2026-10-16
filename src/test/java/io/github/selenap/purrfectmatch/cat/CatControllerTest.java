package io.github.selenap.purrfectmatch.cat;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.selenap.purrfectmatch.common.error.ResourceNotFoundException;

/** Tests the web layer only (serialization, validation, errors); the service is mocked. */
@WebMvcTest(CatController.class)
class CatControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    CatService service;

    @Test
    void getReturnsCat() throws Exception {
        given(service.findById(1L)).willReturn(new CatResponse(
                1L, "Miso", Sex.FEMALE, null, null, CatStatus.AVAILABLE, Instant.now()));

        mvc.perform(get("/api/v1/cats/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Miso"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void getUnknownReturns404ProblemDetail() throws Exception {
        given(service.findById(42L)).willThrow(new ResourceNotFoundException("Cat 42 not found"));

        mvc.perform(get("/api/v1/cats/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail").value("Cat 42 not found"));
    }

    @Test
    void registerWithInvalidBodyReturns400WithFieldErrors() throws Exception {
        mvc.perform(post("/api/v1/cats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"birthDate\":\"2999-01-01\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.sex").exists())
                .andExpect(jsonPath("$.errors.birthDate").exists());
    }
}
