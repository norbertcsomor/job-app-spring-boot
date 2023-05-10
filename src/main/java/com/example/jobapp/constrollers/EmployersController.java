package com.example.jobapp.constrollers;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.jobapp.auth.MyUserDetails;
import com.example.jobapp.models.Jobadvertisement;
import com.example.jobapp.models.User;
import com.example.jobapp.requests.StoreEmployerRequest;
import com.example.jobapp.requests.StoreJobadvertisementRequest;
import com.example.jobapp.requests.UpdateEmployerRequest;
import com.example.jobapp.services.EmployersService;
import com.example.jobapp.services.JobadvertisementsService;

/**
 * A munkaadók kezelésére szolgáló vezérlők osztálya.
 *
 * @author Norbert Csomor
 */
@Controller
public class EmployersController {

        @Autowired
        EmployersService employersService;

        @Autowired
        JobadvertisementsService jobadvertisementsService;

        Logger logger = LoggerFactory.getLogger(EmployersController.class);

        // @Autowired
        // AuthenticationManager authenticationManager;

        /**
         * Új munkaadó létrehozására szolgáló űrlap megjelenítése.
         *
         * @param model a visszaadandó nézetmodell injektálása.
         * @return a munkaadó létrehozására szolgáló nézetmodell.
         */
        @RequestMapping(value = "/employers/create", method = RequestMethod.GET)
        public String createEmployer(
                        Model model) {
                // a nézetmodell tulajdonságainak beállítása
                model.addAttribute("title",
                                "Munkaadó létrehozása");
                model.addAttribute("storeEmployerRequest",
                                new StoreEmployerRequest());
                // a nézetmodell visszaadása a hívónak
                return "employers/create";
        }

        /**
         * Új munkaadó létrehozása az adatbázisban.
         *
         * @param storeEmployerRequest a létrehozandó munkaadó adatai.
         * @param bindingResult        a validációs eredmények.
         * @param redirectAttributes   az átirányítás tulajdonságai.
         * @param model                a visszaadandó nézetmodell injektálása.
         * @return a létrehozás eredményeit tartalmazó nézetmodell.
         */
        @RequestMapping(value = "/employers", method = RequestMethod.POST)
        public String storeEmployer(
                        @Valid StoreEmployerRequest storeEmployerRequest,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes,
                        Model model) {
                // a validációs hibák kezelése
                if (bindingResult.hasErrors()) {
                        // a nézetmodell visszaadása a hívónak
                        return "/employers/create";
                } else {
                        // az adatbázisban már létező email-cím esetének kezelése
                        if (employersService.employerExistsByEmail(
                                        storeEmployerRequest.getEmail())) {
                                // a nézetmodell tulajdonságainak beállítása
                                redirectAttributes.addFlashAttribute("message",
                                                "Az email-cím már használatban van!");
                                redirectAttributes.addFlashAttribute("alertClass",
                                                "alert-danger");
                                // a nézetmodell visszaadása a hívónak
                                return "redirect:/employers/create";
                        }
                        // a jelszó és a jelszó megerősítése mezők nem egyezőségének kezelése
                        if (!storeEmployerRequest.getPassword().equals(
                                        storeEmployerRequest.getPassword_confirmation())) {
                                // nézetmodell tulajdonságainak beállítása
                                redirectAttributes.addFlashAttribute("message",
                                                "A jelszavak nem egyeznek meg!");
                                redirectAttributes.addFlashAttribute("alertClass",
                                                "alert-danger");
                                // nézetmodell visszaadása a hívónak
                                return "redirect:/employers/create";
                        }
                        // új felhasználó létrehozása a szolgáltatás segítségével
                        try {
                                employersService.createEmployer(storeEmployerRequest);
                                // LOGIN megvalósítása
                                // UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                // new UsernamePasswordAuthenticationToken(storeEmployerRequest.getEmail(),
                                // storeEmployerRequest.getPassword());
                                // Authentication authentication = authenticationManager.authenticate(
                                // usernamePasswordAuthenticationToken);
                                // SecurityContextHolder.getContext().setAuthentication(authentication);
                                // a naplózás használata
                                logger.info("Sikerült a munkaadó létrehozása!");
                                // a nézetmodell tulajdonságainak beállítása
                                redirectAttributes.addFlashAttribute("message",
                                                "Sikerült a munkaadó létrehozása!");
                                redirectAttributes.addFlashAttribute("alertClass",
                                                "alert-success");
                                // a nézetmodell visszaadása a hívónak
                                return "redirect:/";
                        } catch (AuthenticationException e) {
                                // a naplózás használata
                                logger.error("Hiba történt a munkaadó létrehozásakor: "
                                                + e.getMessage());
                                // a nézetmodell tulajdonságainak beállítása
                                redirectAttributes.addFlashAttribute("message",
                                                "Nem sikerült a munkaadó létrehozása!");
                                redirectAttributes.addFlashAttribute("alertClass",
                                                "alert-danger");
                                // a nézetmodell visszaadása a hívónak
                                return "redirect:/employers/create";
                        }
                }
        }

        /**
         * Létező munkaadó módosítására szolgáló űrlap megjelenítése.
         *
         * @param id                 a módosítandó munkaadó azonosítója.
         * @param loggedUser         a bejelentkezett felhasználó adatai.
         * @param redirectAttributes az átirányítás tulajdonságai.
         * @param model              a visszaadandó nézetmodell injektálása.
         * @return a munkaadó módosítására szolgáló nézetmodell.
         */
        @RequestMapping(value = "/employers/edit/{id}", method = RequestMethod.GET)
        public String editEmployer(
                        @PathVariable("id") @Min(1) Long id,
                        @AuthenticationPrincipal MyUserDetails loggedUser,
                        RedirectAttributes redirectAttributes,
                        Model model) {
                // a módosítandó munkaadó megkeresése az adatbázisban
                Optional<User> employerInDb = employersService.getEmployerById(id);
                // a módosítandó munkaadó létezik
                if (employerInDb.isPresent()) {
                        // a megtalált munkaadó nézetmodell-kompatibilissé tétele
                        User employer = employerInDb.get();
                        UpdateEmployerRequest updateEmployerRequest = new UpdateEmployerRequest();
                        updateEmployerRequest.setId(employer.getId());
                        updateEmployerRequest.setName(employer.getName());
                        updateEmployerRequest.setAddress(employer.getAddress());
                        updateEmployerRequest.setTelephone(employer.getTelephone());
                        // a nézetmodell tulajdonságainak beállítása
                        model.addAttribute("title",
                                        "Munkaadó szerkesztése");
                        model.addAttribute("loggedUser",
                                        loggedUser);
                        model.addAttribute("updateEmployerRequest",
                                        updateEmployerRequest);
                        // a nézetmodell visszaadása a hívónak
                        return "employers/edit";
                } else {
                        // a módosítandó munkaadó nem létezik
                        // a nézetmodell tulajdonságainak beállítása
                        redirectAttributes.addFlashAttribute("message",
                                        "Nem sikerült a munkaadó lekérdezése!");
                        redirectAttributes.addFlashAttribute("alertClass",
                                        "alert-danger");
                        // a nézetmodell visszaadása a hívónak
                        return "redirect:/";
                }
        }

        /**
         * Létező munkaadó módosítása az adatbázisban.
         *
         * @param id                    a módosítandó munkaadó azonosítója.
         * @param updateEmployerRequest a módosítandó munkaadó adatai.
         * @param loggedUser            a bejelentkezett fehasználó adatai.
         * @param bindingResult         a validációs eredmények.
         * @param redirectAttributes    az átirányítás tulajdonságai.
         * @return a módosítás eredményeit tartalmazó nézetmodell.
         */
        @RequestMapping(value = "/employers/update/{id}", method = RequestMethod.POST)
        public String updateEmployer(
                        @PathVariable("id") @Min(1) Long id,
                        @Valid @ModelAttribute("updateEmployerRequest") UpdateEmployerRequest updateEmployerRequest,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal MyUserDetails loggedUser,
                        RedirectAttributes redirectAttributes) {
                // a validációs hibák kezelése
                if (bindingResult.hasErrors()) {
                        // a nézetmodell tulajdonságainak beállítása
                        redirectAttributes.addFlashAttribute(
                                        "org.springframework.validation.BindingResult.updateEmployerRequest",
                                        bindingResult);
                        redirectAttributes.addFlashAttribute("updateEmployerRequest",
                                        updateEmployerRequest);
                        redirectAttributes.addFlashAttribute("message",
                                        "Érvénytelen adatok!");
                        redirectAttributes.addFlashAttribute("alertClass",
                                        "alert-danger");
                        redirectAttributes.addAttribute("id",
                                        updateEmployerRequest.getId());
                        // a nézetmodell visszaadása a hívónak
                        return "redirect:/employers/edit/{id}";
                } else {
                        // a módosítandó munkaadó megkeresése az adatbázisban
                        Optional<User> employerInDb = employersService.getEmployerById(id);
                        // a módosítandó munkaadó létezik
                        if (employerInDb.isPresent()) {
                                // módosítandó munkaadó létrehozása a kapott validált adatokból
                                User employerToUpdate = employerInDb.get();
                                employerToUpdate.setId(updateEmployerRequest.getId());
                                employerToUpdate.setName(updateEmployerRequest.getName());
                                employerToUpdate.setAddress(updateEmployerRequest.getAddress());
                                employerToUpdate.setTelephone(updateEmployerRequest.getTelephone());
                                // módosítandó munkaadó létrehozása a szolgáltatás használatával
                                try {
                                        User updatedEmployer = employersService.updateEmployer(employerToUpdate);
                                        // a bejelentkezett felhasználó frissítése a módosított adatokkal
                                        loggedUser.setId(updatedEmployer.getId());
                                        loggedUser.setName(updatedEmployer.getName());
                                        // a naplózás használata
                                        logger.info("Sikerült a munkaadó módosítása!");
                                        // a nézetmodell tulajdonságainak beállítása
                                        redirectAttributes.addFlashAttribute("message",
                                                        "Sikerült a munkaadó módosítása!");
                                        redirectAttributes.addFlashAttribute("alertClass",
                                                        "alert-success");
                                        // a nézetmodell visszaadása a hívónak
                                        return "redirect:/";
                                } catch (Exception e) {
                                        // a naplózás használata
                                        logger.error("Hiba történt: " + e.getMessage());
                                        // a nézetmodell tulajdonságainak beállítása
                                        redirectAttributes.addFlashAttribute("message",
                                                        "Nem sikerült a munkaadó módosítása!");
                                        redirectAttributes.addFlashAttribute("alertClass",
                                                        "alert-danger");
                                        redirectAttributes.addAttribute("id", id);
                                        // a nézetmodell visszaadása a hívónak
                                        return "redirect:/employers/edit/{id}";
                                }
                        } else {
                                // a naplózás használata
                                logger.error("Nem sikerült a munkaadó módosítása!");
                                // a nézetmodell tulajdonságainak beállítása
                                redirectAttributes.addFlashAttribute("message",
                                                "Nem sikerült a munkaadó módosítása!");
                                redirectAttributes.addFlashAttribute("alertClass",
                                                "alert-danger");
                                redirectAttributes.addAttribute("id", id);
                                // a nézetmodell visszaadása a hívónak
                                return "redirect:/employers/edit/{id}";
                        }
                }
        }

        /**
         * Létező munkaadó törlése az adatbázisból.
         *
         * @param id                 a törlendő munkaadó azonosítója.
         * @param httpServletRequest a beérkező kérés felhasználása a
         *                           kijelentkeztetéshez.
         * @param redirectAttributes az átirányítás tulajdonságai.
         * @return a törlés eredményeit tartalmazó nézetmodell.
         */
        @RequestMapping(value = "/employers/delete/{id}", method = RequestMethod.GET)
        public String deleteEmployer(
                        @PathVariable("id") @Min(1) Long id,
                        HttpServletRequest httpServletRequest,
                        RedirectAttributes redirectAttributes) {
                // a törlendő munkaadó megkeresése az adatbázisban
                Optional<User> employer = employersService.getEmployerById(id);
                // a törlendő munkaadó létezik
                if (employer.isPresent()) {
                        // a munkaadó törlése a szolgáltatás használatával
                        try {
                                employersService.deleteEmployer(id);
                                // LOGOUT a Spring Security függvényének segítségével
                                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                                if (auth.getName().equals(employer.get().getEmail())) {
                                        new SecurityContextLogoutHandler()
                                                        .logout(httpServletRequest, null, auth);
                                        SecurityContextHolder.getContext().setAuthentication(null);
                                }
                                // a naplózás használata
                                logger.info("Sikerült a munkaadó törlése!");
                                // a nézetmodell tulajdonságainak beállítása
                                redirectAttributes.addFlashAttribute("message",
                                                "Sikerült a munkaadó törlése!");
                                redirectAttributes.addFlashAttribute("alertClass",
                                                "alert-success");
                                // a nézetmodell visszaadása a hívónak
                                return "redirect:/";
                        } catch (Exception e) {
                                // a naplózás használata
                                logger.error("Hiba történt: " + e.getMessage());
                                // a nézetmodell tulajdonságainak beállítása
                                redirectAttributes.addFlashAttribute("message",
                                                "Nem sikerült a munkaadó törlése!");
                                redirectAttributes.addFlashAttribute("alertClass",
                                                "alert-danger");
                                // a nézetmodell visszaadása a hívónak
                                return "redirect:/";
                        }
                } else {
                        // a naplózás használata
                        logger.error("Nem sikerült a munkaadó lekérdezése!");
                        // a nézetmodell tulajdonságainak beállítása
                        redirectAttributes.addFlashAttribute("message",
                                        "Nem sikerült a munkaadó lekérdezése!");
                        redirectAttributes.addFlashAttribute("alertClass",
                                        "alert-danger");
                        // a nézetmodell visszaadása a hívónak
                        return "redirect:/";
                }
        }

        /**
         * Létező munkaadó összes létező álláshirdetésének lekérdezése az
         * adatbázisból.
         *
         * @param id         a lekérdezendő álláshirdetések munkaadójának azonosítója.
         * @param loggedUser a bejelentkezett felhasználó adatai.
         * @param model      a visszaadandó nézetmodell injektálása.
         * @return a keresett álláshirdetések listája.
         */
        @RequestMapping(value = "/employers/jobadvertisements/{id}", method = RequestMethod.GET)
        public String getEmployerJobadvertisments(
                        @PathVariable("id") @Min(1) Long id,
                        @AuthenticationPrincipal MyUserDetails loggedUser,
                        Model model) {
                // a keresett álláshirdetések listája
                List<Jobadvertisement> jobadvertisements = jobadvertisementsService
                                .getEmployerJobadvertisments(loggedUser.getId());
                // a nézetmodell tulajdonságainak beállítása
                model.addAttribute("title",
                                "Munkaadó álláshirdetései");
                model.addAttribute("storeJobadvertisementRequest",
                                new StoreJobadvertisementRequest());
                model.addAttribute("jobadvertisements",
                                jobadvertisements);
                model.addAttribute("loggedUser",
                                loggedUser);
                // a nézetmodell visszaadása a hívónak
                return "employers/jobadvertisements";
        }
}
