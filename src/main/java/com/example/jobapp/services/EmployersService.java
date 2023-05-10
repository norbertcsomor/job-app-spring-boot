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
import com.example.jobapp.requests.StoreEmployerRequest;
import com.example.jobapp.services.interfaces.IEmployersServices;

/**
 * A munkaadók kezelésére szolgáló műveleteket
 * az adatbázis vonatkozásában mevalósító osztály.
 * 
 * @author Norbert Csomor
 */
@Service
public class EmployersService implements IEmployersServices {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    UsersRepository usersRepository;

    /**
     * Új munkaadó létrehozása az adatbázisban.
     *
     * @param storeEmployerRequest a létrehozandó munkaadó adatai.
     * @return a létrehozott munkaadó adatai.
     */
    public User createEmployer(StoreEmployerRequest storeEmployerRequest) {

        User employer = new User();
        employer.setName(storeEmployerRequest.getName());
        employer.setAddress(storeEmployerRequest.getAddress());
        employer.setTelephone(storeEmployerRequest.getTelephone());
        employer.setEmail(storeEmployerRequest.getEmail());
        employer.setPassword(passwordEncoder.encode(storeEmployerRequest.getPassword()));
        Role role = rolesRepository.findByName("Employer");
        employer.setRole(role);
        return usersRepository.save(employer);
    }

    /**
     * Létező munkaadó lekérdezése az adatbázisból az azonosítója alapján.
     *
     * @param id a lekérdezendő munkaadó azonosítója.
     * @return a lekérdezett munkaadó adatai.
     */
    public Optional<User> getEmployerById(Long id) {
        return usersRepository.findById(id);
    }

    /**
     * Létező munkaadó módosítása az adatbázisban.
     *
     * @param employer a módosítandó munkaadó adatai.
     * @return a módosított munkaadó adatai.
     */
    public User updateEmployer(User employer) {
        return usersRepository.save(employer);
    }

    /**
     * Létező munkaadó törlése az adatbázisból.
     *
     * @param id a törlendő munkaadó azonosítója.
     * @return a törölt munkaadó azonosítója.
     */
    public Long deleteEmployer(Long id) {
        usersRepository.deleteById(id);
        return id;
    }

    /**
     * Az összes létező munkaadó lekérdezése az adatbázisból.
     *
     * @return a munkaadók listája.
     */
    public List<User> getAllEmployers() {
        Role role = rolesRepository.findByName("Employer");
        List<User> employers = usersRepository.findByRole(role.getId());
        return employers;
    }

    /**
     * A munkaadó email-cím használtságának ellenőrzése az adatbázisban.
     *
     * @param email a keresett email-cím.
     * @return az email-cím létezését jelző állapotváltozó.
     */
    public boolean employerExistsByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }
}
