package com.example.jobapp.services.interfaces;

import com.example.jobapp.models.User;

/**
 * A felhasználók kezelésére szolgáló műveletek körét leíró általános
 * interfész.
 * 
 * @author Norbert Csomor
 */
public interface IUsersService {
    public User getUserByEmail(String email);
}
