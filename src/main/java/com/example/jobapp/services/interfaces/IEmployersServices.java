package com.example.jobapp.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.example.jobapp.models.User;
import com.example.jobapp.requests.StoreEmployerRequest;

/**
 * A munkaadók kezelésére szolgáló műveletek körét leíró általános interfész.
 * 
 * @author Norbert Csomor
 */
public interface IEmployersServices {
    public User createEmployer(StoreEmployerRequest storeEmployerRequest);

    public Optional<User> getEmployerById(Long id);

    public User updateEmployer(User employer);

    public Long deleteEmployer(Long id);

    public List<User> getAllEmployers();

    public boolean employerExistsByEmail(String email);
}
