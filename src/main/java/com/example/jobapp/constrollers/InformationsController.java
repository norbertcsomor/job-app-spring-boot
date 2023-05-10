package com.example.jobapp.constrollers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.jobapp.auth.MyUserDetails;

/**
 * Az információs oldal vezérlőjének osztálya.
 * 
 * @author Norbert Csomor
 */
@Controller
public class InformationsController {

    /**
     * Az oldallal kapcsolatos információk megjelenítése.
     * 
     * @param loggedUser a bejelentkezett felhasználó adatai.
     * @param model      a visszaadandó nézetmodell injektálása.
     * @return az információkat tartalmazó nézetmodell.
     */
    @GetMapping("/informations")
    public String informations(
            @AuthenticationPrincipal MyUserDetails loggedUser,
            Model model) {
        model.addAttribute("title", "Információk");
        model.addAttribute("loggedUser", loggedUser);
        return "informations";
    }
}
