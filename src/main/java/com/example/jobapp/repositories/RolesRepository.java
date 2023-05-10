package com.example.jobapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jobapp.models.Role;

/**
 * A szerepek adatbázis-műveleteit tartalmazó interfész, amely
 * a keretrendszer beépített {@link JpaRepository} interfészét bővíti ki.
 * 
 * @author Norbert Csomor
 */
@Repository
public interface RolesRepository extends JpaRepository<Role, Long> {

    /**
     * A szerep lekérdezése a szerep elnevezése megadásával.
     * 
     * @param string a szerep elnevezése.
     * @return a szerep adatai.
     */
    Role findByName(String string);
}