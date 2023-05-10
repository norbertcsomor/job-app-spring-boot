package com.example.jobapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.jobapp.models.User;
import com.example.jobapp.repositories.UsersRepository;
import com.example.jobapp.services.interfaces.IUsersService;

/**
 * A felhasználók kezelésére szolgáló műveleteket
 * az adatbázis vonatkozásában mevalósító osztály.
 * 
 * @author Norbert Csomor
 */
@Service
public class UsersService implements IUsersService {

    @Autowired
    UsersRepository usersRepository;

    /**
     * Létező felhasználó lekérdezése az adatbázisból az email-címe alapján.
     * 
     * @param email a lekérdezendő felhasználó email-címe.
     * @return a lekérdezett felhasználó adatai.
     */
    public User getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

}
