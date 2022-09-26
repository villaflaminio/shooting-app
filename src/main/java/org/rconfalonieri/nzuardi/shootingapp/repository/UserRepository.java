package org.rconfalonieri.nzuardi.shootingapp.repository;

import org.rconfalonieri.nzuardi.shootingapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByActualTesserinoId(String tesserinoId);
    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByEmail(String email);
    Boolean existsByActualTesserinoId(String tesserinoId);
    //    @Formula("(select t.id from tesserino t where t.utente_id = ID and t.data_rilascio = (select max(t2.data_rilascio) from tesserino t2 ))")

    @Query("select u from User u where u.actualTesserinoId = ?1")
    Optional<User> findByActualTesserinoId(Long tesserinoId);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String userEmail);

    @Query("select u from User u where u.email = ?1 and u.id not in (select t.utente.id from Tesserino t )")
    boolean isOnlyUser(String email);
}
