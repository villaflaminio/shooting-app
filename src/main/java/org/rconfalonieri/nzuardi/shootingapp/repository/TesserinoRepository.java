package org.rconfalonieri.nzuardi.shootingapp.repository;

import org.rconfalonieri.nzuardi.shootingapp.model.Tesserino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TesserinoRepository extends JpaRepository<Tesserino, Long> {
}