package com.example.jobapp.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.example.jobapp.models.User;

/**
 * Az álláskeresők kezelésére szolgáló műveletek körét leíró általános
 * interfész.
 * 
 * @author Norbert Csomor
 */
public interface IJobseekersService {
    public User createJobseeker(User jobseeker);

    public Optional<User> getJobseekerById(Long id);

    public User updateJobseeker(User jobseeker);

    public Long deleteJobseeker(Long id);

    public List<User> getAllJobseekers();

    public boolean jobseekerExistsByEmail(String email);

}
