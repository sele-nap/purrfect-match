package io.github.selenap.purrfectmatch.cat;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/cats")
public class CatController {

    private final CatService service;

    public CatController(CatService service) {
        this.service = service;
    }

    @GetMapping
    public Page<CatResponse> list(@RequestParam(required = false) CatStatus status,
            @PageableDefault(size = 20, sort = "arrivedAt") Pageable pageable) {
        return service.findAll(status, pageable);
    }

    @GetMapping("/{id}")
    public CatResponse get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<CatResponse> register(@Valid @RequestBody CatRequest request) {
        CatResponse created = service.register(request);
        return ResponseEntity.created(URI.create("/api/v1/cats/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public CatResponse update(@PathVariable Long id, @Valid @RequestBody CatRequest request) {
        return service.update(id, request);
    }
}
