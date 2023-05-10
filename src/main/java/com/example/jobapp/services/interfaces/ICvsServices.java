package com.example.jobapp.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.example.jobapp.models.Cv;

/**
 * Az önéletrajzok kezelésére szolgáló műveletek körét leíró általános
 * interfész.
 * 
 * @author Norbert Csomor
 */
public interface ICvsServices {
    public Cv createCv(Cv cv);

    public Optional<Cv> getCvById(Long id);

    public List<Cv> getUserCvs(Long id);

    public Long deleteCv(Long id);
}
