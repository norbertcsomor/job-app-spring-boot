package com.example.jobapp.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.example.jobapp.models.Jobadvertisement;

/**
 * Az álláshirdetések kezelésére szolgáló műveletek körét leíró általános
 * interfész.
 * 
 * @author Norbert Csomor
 */
public interface IJobadvertismentsServices {
    public List<Jobadvertisement> getAllJobadvertisements();

    public Jobadvertisement createJobadvertisement(Jobadvertisement jobadvertisement);

    public Optional<Jobadvertisement> getJobadvertisementById(Long id);

    public Jobadvertisement updateJobadvertisement(Jobadvertisement jobadvertisement);

    public Long deleteJobadvertisement(Long id);

    public List<Jobadvertisement> getEmployerJobadvertisments(Long id);
}
