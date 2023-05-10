package com.example.jobapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.jobapp.models.Jobapplication;

/**
 * A jelentkezések adatbázis-műveleteit tartalmazó interfész, amely
 * a keretrendszer beépített {@link JpaRepository} interfészét bővíti ki.
 * 
 * @author Norbert Csomor
 */
@Repository
public interface JobapplicationsRepository extends JpaRepository<Jobapplication, Long> {

    /**
     * Létező álláshirdetés összes jelentkezésének lekérdezése az adatbázisból.
     *
     * @param jobadvertisementId az álláshirdetés azonosítója.
     * @return a jelentkezések listája.
     */
    @Query(value = "SELECT j FROM Jobapplication j WHERE j.jobadvertisement.id = :jobadvertisementId")
    List<Jobapplication> findByJobadvertisement(@Param("jobadvertisementId") Long jobadvertisementId);

    /**
     * Létező álláskereső összes jelentkezésének lekérdezése az adatbázisból.
     *
     * @param jobseekerId az álláskereső azonosítója.
     * @return a jelentkezések listája.
     */
    @Query(value = "SELECT j FROM Jobapplication j WHERE j.cv.user.id = :jobseekerId")
    List<Jobapplication> findByJobseeker(@Param("jobseekerId") Long jobseekerId);

    /**
     * Létező álláskereső létező álláshirdetésre történt összes jelentkezésének
     * lekérdezése az adatbázisból.
     *
     * @param jobadvertisementId az álláshirdetés azonosítója.
     * @param jobseekerId        az álláskereső azonosítója.
     * @return a jelentkezések listája.
     */
    @Query(value = "SELECT j FROM Jobapplication j WHERE j.jobadvertisement.id = :jobadvertisementId and j.cv.user.id = :jobseekerId")
    List<Jobapplication> findByJobadvertisementAndJobseeker(
            @Param("jobadvertisementId") Long jobadvertisementId,
            @Param("jobseekerId") Long jobseekerId);
}
