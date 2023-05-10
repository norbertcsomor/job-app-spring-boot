package com.example.jobapp.constrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.jobapp.auth.MyUserDetails;
import com.example.jobapp.services.JobadvertisementsService;

/**
 * A kezdőoldal vezérlőjét tartalmazó osztály.
 * 
 * @author Norbert Csomor
 */
@Controller
public class IndexController {

    @Autowired
    JobadvertisementsService jobadvertisementsService;
    
    /**
     * A kezdőoldal megjelenítésére alkalmazott vezélő.
     * 
     * @param loggedUser a bejelentkezett felhasználó adatai.
     * @param model a megjelenítendő nézetmodell injektálása.
     * @return a megjelenítendő nézetmodell.
     */
    @GetMapping("/")
    public String index(@AuthenticationPrincipal MyUserDetails loggedUser, Model model) {
        model.addAttribute("title", "Kezdőoldal");
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("jobadvertisements", jobadvertisementsService.getAllJobadvertisements());
        return "index";   
    }
}
