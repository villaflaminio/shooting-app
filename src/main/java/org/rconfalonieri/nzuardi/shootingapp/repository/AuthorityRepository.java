package org.rconfalonieri.nzuardi.shootingapp.repository;

import org.rconfalonieri.nzuardi.shootingapp.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Component
@RepositoryRestResource(exported = false)
public interface AuthorityRepository extends JpaRepository<Authority, String> {
    Authority getByName(String name);
}
