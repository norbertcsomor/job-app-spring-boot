package com.example.jobapp.constrollers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.jobapp.requests.LoginRequest;

/**
 * A bejelentkeztetésre szolgáló vezérlő osztálya.
 * 
 * @author Norbert Csomor
 */
@Controller
public class LoginController {

    /**
     * 
     * 
     * @param loginRequest a bejelentkezés adatait tartalmazó változó
     * @return a bejelentkezésre szolgáló nézetmodell.
     */
    @GetMapping("/login")
    public ModelAndView login(LoginRequest loginRequest) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Bejelentkezés");
        modelAndView.setViewName("login");
        return modelAndView;
    }
}