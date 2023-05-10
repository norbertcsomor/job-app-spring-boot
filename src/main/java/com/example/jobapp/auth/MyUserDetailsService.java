package com.example.jobapp.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.jobapp.models.Role;
import com.example.jobapp.repositories.UsersRepository;

/**
 * A bejelentkezéshez és authentikációhoz használható
 * műveletek a {@link UserDetailsService} interfész implementációjával.
 * 
 * @author Norbert Csomor
 */
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    public Collection<? extends GrantedAuthority> getAuthorities(Role role) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getName()));
        return authorities;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        com.example.jobapp.models.User user = usersRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("A felhasználó nem található!");
        }

        return new MyUserDetails(user);
    }
}
