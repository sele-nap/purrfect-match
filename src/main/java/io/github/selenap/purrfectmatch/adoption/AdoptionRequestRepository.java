package io.github.selenap.purrfectmatch.adoption;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, Long> {

    @EntityGraph(attributePaths = "cat")
    Page<AdoptionRequest> findByStatus(AdoptionStatus status, Pageable pageable);

    @EntityGraph(attributePaths = "cat")
    Page<AdoptionRequest> findByCatId(Long catId, Pageable pageable);

    List<AdoptionRequest> findByCatIdAndStatus(Long catId, AdoptionStatus status);

    boolean existsByCatIdAndApplicantEmailIgnoreCaseAndStatusIn(
            Long catId, String applicantEmail, List<AdoptionStatus> statuses);
}
