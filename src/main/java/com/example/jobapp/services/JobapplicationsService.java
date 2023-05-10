package com.example.jobapp.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.jobapp.models.Jobapplication;
import com.example.jobapp.repositories.JobapplicationsRepository;
import com.example.jobapp.services.interfaces.IJobapplicationsService;

/**
 * A jelentkezések kezelésére szolgáló műveleteket
 * az adatbázis vonatkozásában mevalósító osztály.
 * 
 * @author Norbert Csomor
 */
@Service
public class JobapplicationsService implements IJobapplicationsService {

    @Autowired
    JobapplicationsRepository jobapplicationsRepository;

    /**
     * Új jelentkezés létrehozása az adatbázisban.
     *
     * @param jobapplication a létrehozandó jelentkezés adatai.
     * @return a létrehozott jelentkezés adatai.
     */
    public Jobapplication createJobapplication(Jobapplication jobapplication) {
        return jobapplicationsRepository.save(jobapplication);
    }

    /**
     * Létező jelentkezés módosítása az adatbázisban.
     *
     * @param jobapplication a módosítandó jelentkezés adatai.
     * @return a módosított jelentkezés adatai.
     */
    public Jobapplication updateJobapplication(Jobapplication jobapplication) {
        return jobapplicationsRepository.save(jobapplication);
    }

    /**
     * Létező jelentkezés törlése az adatbázisból.
     *
     * @param id a törlendő jelentkezés azonosítója.
     * @return a törölt jelentkezés azonosítója.
     */
    public Long deleteJobapplication(Long id) {
        jobapplicationsRepository.deleteById(id);
        return id;
    }

    /**
     * Létező álláshirdetés jelentkezéseinek lekérdezése az adatbázisból.
     *
     * @param jobadvertisementId az álláshirdetés azonosítója.
     * @return a jelentkezések listája.
     */
    public List<Jobapplication> getJobadvertisementJobapplications(Long jobadvertisementId) {
        return jobapplicationsRepository.findByJobadvertisement(jobadvertisementId);
    }

    /**
     * Létező álláskereső összes jelentkezésének lekérdezése az adatbázisból.
     *
     * @param jobseekerId az álláskereső azonosítója.
     * @return a jelentkezések listája.
     */
    public List<Jobapplication> getJobseekerJobapplications(Long jobseekerId) {
        return jobapplicationsRepository.findByJobseeker(jobseekerId);
    }

    /**
     * Létező álláskereső létező álláshirdetésre történt jelentkezéseinek
     * lekérdezése az adatbázisból.
     *
     * @param jobadvertisementId az álláshirdetés azonosítója.
     * @param jobseekerId        az álláskereső azonosítója.
     * @return a jelentkezések listája.
     */
    public List<Jobapplication> getJobadvertisementAndJobseekerJobapplications(
            Long jobadvertisementId, Long jobseekerId) {
        return jobapplicationsRepository.findByJobadvertisementAndJobseeker(
                jobadvertisementId, jobseekerId);
    }

    /**
     * Létező jelentkezés lekérdezése az adatbázisból.
     *
     * @param id a lekérdezendő jelentkezés azonosítója.
     * @return a lekérdezett jelentkezés adatai.
     */
    public Optional<Jobapplication> getJobapplicationById(Long id) {
        return jobapplicationsRepository.findById(id);
    }

}
