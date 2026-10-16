package io.github.selenap.purrfectmatch.cat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatRepository extends JpaRepository<Cat, Long> {

    Page<Cat> findByStatus(CatStatus status, Pageable pageable);
}
