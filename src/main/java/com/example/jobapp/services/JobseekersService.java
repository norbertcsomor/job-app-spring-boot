package com.example.jobapp.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jobapp.models.Role;
import com.example.jobapp.models.User;
import com.example.jobapp.repositories.RolesRepository;
import com.example.jobapp.repositories.UsersRepository;
import com.example.jobapp.services.interfaces.IJobseekersService;

/**
 * Az álláskeresők kezelésére szolgáló műveleteket
 * az adatbázis vonatkozásában mevalósító osztály.
 * 
 * @author Norbert Csomor
 */
@Service
public class JobseekersService implements IJobseekersService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    UsersRepository usersRepository;

    /**
     * Új álláskereső létrehozása az adatbázisban.
     *
     * @param jobseeker a létrehozandó álláskereső adatai.
     * @return a létrehozott álláskereső adatai.
     */
    public User createJobseeker(User jobseeker) {
        jobseeker.setPassword(passwordEncoder.encode(jobseeker.getPassword()));
        Role role = rolesRepository.findByName("Jobseeker");
        jobseeker.setRole(role);
        return usersRepository.save(jobseeker);
    }

    /**
     * Létező álláskereső lekérdezése az adatbázisból az azonosítója alapján.
     *
     * @param id a lekérdezendő álláskereső azonosítója.
     * @return a lekérdezett álláskereső adatai.
     */
    public Optional<User> getJobseekerById(Long id) {
        return usersRepository.findById(id);
    }

    /**
     * Létező álláskereső módosítása az adatbázisban.
     *
     * @param jobseeker a módosítandó álláskereső adatai.
     * @return a módosított álláskereső adatai.
     */
    public User updateJobseeker(User jobseeker) {
        return usersRepository.save(jobseeker);
    }

    /**
     * Létező álláskereső törlése az adatbázisból.
     *
     * @param id a törlendő álláskereső azonosítója.
     * @return a törölt álláskereső azonosítója.
     */
    public Long deleteJobseeker(Long id) {
        usersRepository.deleteById(id);
        return id;
    }

    /**
     * Az összes létező álláskereső lekérdezése az adatbázisból.
     *
     * @return az álláskeresők listája.
     */
    public List<User> getAllJobseekers() {
        Role role = rolesRepository.findByName("Jobseeker");
        List<User> jobseekers = usersRepository.findByRole(role.getId());
        return jobseekers;
    }

    /**
     * Az álláskereső email-cím használtságának ellenőrzése az adatbázisban.
     *
     * @param email a keresett email-cím.
     * @return az email-cím létezését jelző állapotváltozó.
     */
    public boolean jobseekerExistsByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }

}
