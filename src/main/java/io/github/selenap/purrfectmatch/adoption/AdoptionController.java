package io.github.selenap.purrfectmatch.adoption;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AdoptionController {

    private final AdoptionService service;

    public AdoptionController(AdoptionService service) {
        this.service = service;
    }

    @PostMapping("/cats/{catId}/adoption-requests")
    public ResponseEntity<AdoptionRequestResponse> submit(@PathVariable Long catId,
            @Valid @RequestBody AdoptionRequestCreate request) {
        AdoptionRequestResponse created = service.submit(catId, request);
        return ResponseEntity.created(URI.create("/api/v1/adoption-requests/" + created.id())).body(created);
    }

    @GetMapping("/cats/{catId}/adoption-requests")
    public Page<AdoptionRequestResponse> listForCat(@PathVariable Long catId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return service.findByCat(catId, pageable);
    }

    @GetMapping("/adoption-requests")
    public Page<AdoptionRequestResponse> listByStatus(
            @RequestParam(defaultValue = "SUBMITTED") AdoptionStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return service.findByStatus(status, pageable);
    }

    @PostMapping("/adoption-requests/{id}/approve")
    public AdoptionRequestResponse approve(@PathVariable Long id) {
        return service.approve(id);
    }

    @PostMapping("/adoption-requests/{id}/reject")
    public AdoptionRequestResponse reject(@PathVariable Long id) {
        return service.reject(id);
    }

    @PostMapping("/adoption-requests/{id}/complete")
    public AdoptionRequestResponse complete(@PathVariable Long id) {
        return service.complete(id);
    }
}
