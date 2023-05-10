package com.example.jobapp.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.jobapp.models.Jobadvertisement;
import com.example.jobapp.repositories.JobadvertisementsRepository;
import com.example.jobapp.services.interfaces.IJobadvertismentsServices;

/**
 * Az álláshirdetések kezelésére szolgáló műveleteket
 * az adatbázis vonatkozásában mevalósító osztály.
 * 
 * @author Norbert Csomor
 */
@Service
public class JobadvertisementsService implements IJobadvertismentsServices {

    @Autowired
    JobadvertisementsRepository jobadvertisementsRepository;

    /**
     * Az összes létező álláshirdetés lekérdezése az adatbázisból.
     *
     * @return az álláshirdetések listája.
     */
    public List<Jobadvertisement> getAllJobadvertisements() {
        return jobadvertisementsRepository.findAll();
    }

    /**
     * Új álláshirdetés létrehozása az adatbázisban.
     *
     * @param jobadvertisement a létrehozandó álláshirdetés adatai.
     * @return a létrehozott álláshirdetés adatai.
     */
    public Jobadvertisement createJobadvertisement(Jobadvertisement jobadvertisement) {
        return jobadvertisementsRepository.save(jobadvertisement);
    }

    /**
     * Létező álláshirdetés lekérdezése az adatbázisból.
     *
     * @param id a lekérdezendő álláshirdetés azonosítója.
     * @return a lekérdezett álláshirdetés adatai.
     */
    public Optional<Jobadvertisement> getJobadvertisementById(Long id) {
        return jobadvertisementsRepository.findById(id);
    }

    /**
     * Létező álláshirdetés módosítása az adatbázisban.
     *
     * @param jobadvertisement a módosítandó álláshirdetés adatai.
     * @return a módosított álláshirdetés adatai.
     */
    public Jobadvertisement updateJobadvertisement(Jobadvertisement jobadvertisement) {
        return jobadvertisementsRepository.save(jobadvertisement);
    }

    /**
     * Létező álláshirdetés törlése az adatbázisban.
     *
     * @param id a törlendő álláshirdetés azonosítója.
     * @return a törölt álláshirdetés azonosítója.
     */
    public Long deleteJobadvertisement(Long id) {
        jobadvertisementsRepository.deleteById(id);
        return id;
    }

    /**
     * Létező munkaadó összes létező álláshirdetésének lekérdezése az
     * adatbázisból.
     *
     * @param id a munkaadó azonosítója.
     * @return az álláshirdetések listája.
     */
    public List<Jobadvertisement> getEmployerJobadvertisments(Long id) {
        return jobadvertisementsRepository.findByUser(id);
    }
}
