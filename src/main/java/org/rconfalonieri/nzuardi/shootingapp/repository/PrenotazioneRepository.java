package org.rconfalonieri.nzuardi.shootingapp.repository;

import org.rconfalonieri.nzuardi.shootingapp.model.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {

    //find by user prenotazione of today
    List<Prenotazione> findByUtentePrenIdAndDataInizio(Long id, Date dataInizio);

    @Query("select p from Prenotazione p  where p.utentePren.id = :id and year(p.dataInizio)=year(current_date) and month(p.dataInizio)=month(current_date) and day(p.dataInizio)=day(current_date)")
    List<Prenotazione> getByUserForToday(Long id);
}