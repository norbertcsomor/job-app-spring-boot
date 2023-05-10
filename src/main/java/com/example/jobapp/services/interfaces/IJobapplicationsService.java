package com.example.jobapp.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.example.jobapp.models.Jobapplication;

/**
 * A jelentkezések kezelésére szolgáló műveletek körét leíró általános
 * interfész.
 * 
 * @author Norbert Csomor
 */
public interface IJobapplicationsService {
    public Jobapplication createJobapplication(Jobapplication jobapplication);

    public Jobapplication updateJobapplication(Jobapplication jobapplication);

    public Long deleteJobapplication(Long id);

    public List<Jobapplication> getJobadvertisementJobapplications(Long jobadvertisementId);

    public List<Jobapplication> getJobseekerJobapplications(Long jobseekerId);

    public List<Jobapplication> getJobadvertisementAndJobseekerJobapplications(Long jobadvertisementId,
            Long jobseekerId);

    public Optional<Jobapplication> getJobapplicationById(Long id);

}
