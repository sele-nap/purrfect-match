package io.github.selenap.purrfectmatch.adoption;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.selenap.purrfectmatch.cat.Cat;
import io.github.selenap.purrfectmatch.cat.CatService;
import io.github.selenap.purrfectmatch.common.error.BusinessRuleException;
import io.github.selenap.purrfectmatch.common.error.ResourceNotFoundException;

@Service
@Transactional(readOnly = true)
public class AdoptionService {

    private static final List<AdoptionStatus> OPEN = List.of(AdoptionStatus.SUBMITTED, AdoptionStatus.APPROVED);

    private final AdoptionRequestRepository repository;
    private final CatService catService;

    public AdoptionService(AdoptionRequestRepository repository, CatService catService) {
        this.repository = repository;
        this.catService = catService;
    }

    public Page<AdoptionRequestResponse> findByStatus(AdoptionStatus status, Pageable pageable) {
        return repository.findByStatus(status, pageable).map(AdoptionRequestResponse::from);
    }

    public Page<AdoptionRequestResponse> findByCat(Long catId, Pageable pageable) {
        catService.getOrThrow(catId);
        return repository.findByCatId(catId, pageable).map(AdoptionRequestResponse::from);
    }

    @Transactional
    public AdoptionRequestResponse submit(Long catId, AdoptionRequestCreate request) {
        Cat cat = catService.getOrThrow(catId);
        if (repository.existsByCatIdAndApplicantEmailIgnoreCaseAndStatusIn(catId, request.applicantEmail(), OPEN)) {
            throw new BusinessRuleException("An open request already exists for this cat with this email");
        }
        AdoptionRequest created = new AdoptionRequest(
                cat, request.applicantName(), request.applicantEmail(), request.message());
        return AdoptionRequestResponse.from(repository.save(created));
    }

    @Transactional
    public AdoptionRequestResponse approve(Long id) {
        AdoptionRequest request = getOrThrow(id);
        request.approve();
        return AdoptionRequestResponse.from(repository.saveAndFlush(request));
    }

    @Transactional
    public AdoptionRequestResponse reject(Long id) {
        AdoptionRequest request = getOrThrow(id);
        request.reject();
        return AdoptionRequestResponse.from(repository.saveAndFlush(request));
    }

    /** Completes the adoption and automatically rejects the other open requests for this cat. */
    @Transactional
    public AdoptionRequestResponse complete(Long id) {
        AdoptionRequest request = getOrThrow(id);
        request.complete();
        repository.findByCatIdAndStatus(request.getCat().getId(), AdoptionStatus.SUBMITTED)
                .forEach(AdoptionRequest::reject);
        return AdoptionRequestResponse.from(repository.saveAndFlush(request));
    }

    private AdoptionRequest getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adoption request " + id + " not found"));
    }
}
