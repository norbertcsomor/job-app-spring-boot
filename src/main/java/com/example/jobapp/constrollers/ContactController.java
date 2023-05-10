package com.example.jobapp.constrollers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.jobapp.auth.MyUserDetails;

/**
 * A kapcsolatfelvételre szolgáló műveleteket tartalmazó osztály.
 * 
 * @author Norbert Csomor
 */
@Controller
public class ContactController {

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public String contact(
            @AuthenticationPrincipal MyUserDetails loggedUser,
            Model model) {
        model.addAttribute("title", "Információk");
        model.addAttribute("loggedUser", loggedUser);
        return "contact";
    }
}
