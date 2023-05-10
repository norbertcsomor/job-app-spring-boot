package com.example.jobapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.jobapp.models.Jobadvertisement;

/**
 * Az álláshirdetések adatbázis-műveleteit tartalmazó interfész, amely
 * a keretrendszer beépített {@link JpaRepository} interfészét bővíti ki.
 * 
 * @author Norbert Csomor
 */
@Repository
public interface JobadvertisementsRepository extends JpaRepository<Jobadvertisement, Long>{

    @Query(value = "SELECT j FROM Jobadvertisement j WHERE j.user.id = :id")
    List<Jobadvertisement> findByUser(@Param("id") Long id);
}