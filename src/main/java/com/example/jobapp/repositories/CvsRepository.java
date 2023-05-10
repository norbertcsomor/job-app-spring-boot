package com.example.jobapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.jobapp.models.Cv;

/**
 * Az önéletrajzok adatbázis-műveleteit tartalmazó interfész, amely
 * a keretrendszer beépített {@link JpaRepository} interfészét bővíti ki.
 * 
 * @author Norbert Csomor
 */
@Repository
public interface CvsRepository extends JpaRepository<Cv, Long> {

    @Query(value = "SELECT c FROM Cv c WHERE c.user.id = :id")
    List<Cv> findByUser(@Param("id") Long id);
}
