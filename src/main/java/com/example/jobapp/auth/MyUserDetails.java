package com.example.jobapp.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.jobapp.models.Role;
import com.example.jobapp.models.User;

/**
 * A {@link UserDetails} interfész saját implementációja.
 * Extra információk elérését teszi lehetővé a 
 * regisztrált/bejelentkezett felhasználóról.
 * 
 * @author Norbert Csomor
 */
public class MyUserDetails implements UserDetails {

    private User user;

    public MyUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        Role role = user.getRole();
        authorities.add(new SimpleGrantedAuthority(role.getName()));

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // EXTRA

    public Long getId() {
        return user.getId();
    }

    public void setId(Long id) {
        this.user.setId(id);
    }

    public String getName() {
        return user.getName();
    }

    public void setName(String name) {
        this.user.setName(name);
    }

    public String getEmail() {
        return user.getEmail();
    }
}
