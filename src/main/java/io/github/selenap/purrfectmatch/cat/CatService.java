package io.github.selenap.purrfectmatch.cat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.selenap.purrfectmatch.common.error.BusinessRuleException;
import io.github.selenap.purrfectmatch.common.error.ResourceNotFoundException;

@Service
@Transactional(readOnly = true)
public class CatService {

    private final CatRepository repository;

    public CatService(CatRepository repository) {
        this.repository = repository;
    }

    public Page<CatResponse> findAll(CatStatus status, Pageable pageable) {
        Page<Cat> cats = status == null
                ? repository.findAll(pageable)
                : repository.findByStatus(status, pageable);
        return cats.map(CatResponse::from);
    }

    public CatResponse findById(Long id) {
        return CatResponse.from(getOrThrow(id));
    }

    @Transactional
    public CatResponse register(CatRequest request) {
        Cat cat = new Cat(request.name(), request.sex(), request.birthDate(), request.description());
        return CatResponse.from(repository.save(cat));
    }

    @Transactional
    public CatResponse update(Long id, CatRequest request) {
        Cat cat = getOrThrow(id);
        if (cat.isAdopted()) {
            throw new BusinessRuleException("An adopted cat's profile can no longer be edited");
        }
        cat.updateProfile(request.name(), request.sex(), request.birthDate(), request.description());
        return CatResponse.from(repository.saveAndFlush(cat));
    }

    /** Used by other modules (adoption) to load the entity. */
    public Cat getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cat " + id + " not found"));
    }
}
