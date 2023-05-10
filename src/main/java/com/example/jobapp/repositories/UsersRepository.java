package com.example.jobapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.jobapp.models.User;

/**
 * A felhasználók adatbázis-műveleteit tartalmazó interfész, amely
 * a keretrendszer beépített {@link JpaRepository} interfészét bővíti ki.
 * 
 * @author Norbert Csomor
 */
@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    /**
     * A felhasználó email-cím használtságának ellenőrzése az adatbázisban.
     * 
     * @param email a keresett email-cím.
     * @return az email-cím létezését jelző állapotváltozó.
     */
    boolean existsByEmail(String email);

    /**
     * Létező felhasználók lekérdezése az adatbázisból a szerepük alapján.
     * 
     * @param role a lekérdezendő felhasználó szerepe.
     * @return a lekérdezett felhasználók adatainak listája.
     */
    @Query(value = "SELECT u FROM User u WHERE u.role.id = :roleId")
    List<User> findByRole(Long roleId);

    /**
     * Létező felhasználó lekérdezése az adatbázisból az email-címe alapján.
     * 
     * @param email a lekérdezendő felhasználó email-címe.
     * @return a lekérdezett felhasználó adatai.
     */
    @Query(value = "SELECT u FROM User u WHERE u.email = :email")
    User findByEmail(@Param("email") String email);

}
