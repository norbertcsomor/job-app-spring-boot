package com.example.jobapp.services;

import com.example.jobapp.models.Cv;
import com.example.jobapp.repositories.CvsRepository;
import com.example.jobapp.services.interfaces.ICvsServices;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Az önéletrajzok kezelésére szolgáló műveleteket
 * az adatbázis vonatkozásában mevalósító osztály.
 * 
 * @author Norbert Csomor
 */
@Service
public class CvsService implements ICvsServices {

    @Autowired
    CvsRepository cvsRepository;

    /**
     * Új önéletrajz létrehozása az adatbázisban.
     *
     * @param cv a létrehozandó önéletrajz adatai.
     * @return a létrehozott önéletrajz adatai.
     */
    public Cv createCv(Cv cv) {
        return cvsRepository.save(cv);
    }

    /**
     * Létező önéletrajz adatainak lekérdezése az adatbázisból.
     *
     * @param id a lekérdezendő önéletrajz azonosítója.
     * @return a lekérdezett önéletrajz adatai.
     */
    public Optional<Cv> getCvById(Long id) {
        return cvsRepository.findById(id);
    }

    /**
     * Létező álláskereső önéletrajzainak lekérdezése az adatbázisból.
     *
     * @param id a lekérdezendő önéletrajzok álláskeresőjének azonosítója.
     * @return a lekérdezett önéletrajzok listája.
     */
    public List<Cv> getUserCvs(Long id) {
        return cvsRepository.findByUser(id);
    }

    /**
     * Létező önéletrajz törlése az adatbázisból.
     *
     * @param id a törlendő önéletrajz azonosítója.
     * @return a törölt önéletrajz azonosítója.
     */
    public Long deleteCv(Long id) {
        cvsRepository.deleteById(id);
        return id;
    }

}
