package com.example.jobapp.constrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.jobapp.auth.MyUserDetails;
import com.example.jobapp.models.Jobadvertisement;
import com.example.jobapp.models.Jobapplication;
import com.example.jobapp.models.User;
import com.example.jobapp.requests.StoreJobadvertisementRequest;
import com.example.jobapp.requests.UpdateJobadvertisementRequest;
import com.example.jobapp.services.JobadvertisementsService;
import com.example.jobapp.services.JobapplicationsService;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Az álláshirdetések kezelésére szolgáló verérlők osztálya.
 * 
 * @author user Norbert Csomor
 */
@Controller
public class JobadvertisementsController {

    @Autowired
    JobadvertisementsService jobadvertisementsService;

    @Autowired
    JobapplicationsService jobapplicationsService;

    /**
     * Új álláshirdetés létrehozása az adatbázisban.
     *
     * @param storeJobadvertisementRequest a létrehozandó álláshirdetés adatai.
     * @param bindingResult                a validációs eredmények.
     * @param loggedUser                   a bejelentkezett felhaswználó adatai.
     * @param redirectAttributes           az átirányítás tulajdonságai.
     * @param model                        a visszaadandó nézetmodell injektálása.
     * @return a létrehozás eredményeit tartalmazó nézetmodell.
     */
    @RequestMapping(value = "/jobadvertisements", method = RequestMethod.POST)
    public String storeJobadvertisement(
            @ModelAttribute("storeJobadvertisementRequest") @Valid StoreJobadvertisementRequest storeJobadvertisementRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal MyUserDetails loggedUser,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "Nem sikerült az álláshirdetés létrehozása!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.storeJobadvertisementRequest", bindingResult);
            redirectAttributes.addFlashAttribute("storeJobadvertisementRequest", storeJobadvertisementRequest);
            redirectAttributes.addAttribute("id", loggedUser.getId());
            return "redirect:/employers/jobadvertisements/{id}";
        } else {

            Jobadvertisement jobadvertisement = new Jobadvertisement();
            User user = new User();
            user.setId(loggedUser.getId());
            jobadvertisement.setUser(user);
            jobadvertisement.setTitle(storeJobadvertisementRequest.getTitle());
            jobadvertisement.setLocation(storeJobadvertisementRequest.getLocation());
            jobadvertisement.setDescription(storeJobadvertisementRequest.getDescription());

            try {
                jobadvertisementsService.createJobadvertisement(jobadvertisement);

                redirectAttributes.addFlashAttribute("message", "Sikerült az álláshirdetés létrehozása!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-success");
                redirectAttributes.addAttribute("id", loggedUser.getId());
                return "redirect:/employers/jobadvertisements/{id}";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "Nem sikerült az álláshirdetés létrehozása!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                return "redirect:/employers/jobadvertisements/{id}";
            }
        }
    }

    /**
     * Létező álláshirdetés megjelenítése.
     *
     * @param id                 a lekérdezendő álláshirdetés azonosítója.
     * @param loggedUser         a bejelentkezett felhasználó adatai.
     * @param redirectAttributes az átirányítás tulajdonságai.
     * @param model              a visszaadandó nézetmodell injektálása.
     * @return az álláshirdetés megjelenítésére szolgáló nézetmodell.
     */
    @RequestMapping(value = "/jobadvertisements/show/{id}", method = RequestMethod.GET)
    public String showJobadvertisement(
            @PathVariable("id") @Min(1) Long id,
            @AuthenticationPrincipal MyUserDetails loggedUser,
            RedirectAttributes redirectAttributes,
            Model model) {
        Optional<Jobadvertisement> jobadvertisement = jobadvertisementsService
                .getJobadvertisementById(id);

        if (jobadvertisement.isPresent()) {

            List<Jobapplication> jobapplications = jobapplicationsService
                    .getJobadvertisementJobapplications(id);

            if (loggedUser != null) {
                model.addAttribute("loggedUserId", loggedUser.getId());
                model.addAttribute("loggedUser", loggedUser);
            } else {
                model.addAttribute("loggedUserId", 0);
            }

            model.addAttribute("title", "Állás részletei");
            model.addAttribute("jobapplications", jobapplications);
            model.addAttribute("jobadvertisement", jobadvertisement.get());
            return "jobadvertisements/show";
        } else {
            redirectAttributes.addFlashAttribute("message", "Nem sikerült az álláshirdetés lekérdezése!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/";
        }
    }

    /**
     * Létező álláshirdetés módosítására szolgáló űrlap megjelenítése.
     *
     * @param id                 a módosítandó álláshirdetés azonosítója.
     * @param loggedUser         a bejelentkezett felhasználó adatai.
     * @param redirectAttributes az átirányításhoz szükséges adatok.
     * @param model              a nézetmodell beállításához szükséges adatok.
     * @return az álláshirdetés módosítására szolgáló nézetmodell.
     */
    @RequestMapping(value = "/jobadvertisements/edit/{id}", method = RequestMethod.GET)
    public String editJobadvertisement(
            @PathVariable("id") @Min(1) Long id,
            @AuthenticationPrincipal MyUserDetails loggedUser,
            RedirectAttributes redirectAttributes,
            Model model) {

        Optional<Jobadvertisement> jobadvertisement = jobadvertisementsService.getJobadvertisementById(id);

        if (jobadvertisement.isPresent()) {
            UpdateJobadvertisementRequest updateJobadvertisementRequest = new UpdateJobadvertisementRequest();
            updateJobadvertisementRequest.setId(jobadvertisement.get().getUser().getId());
            updateJobadvertisementRequest.setTitle(jobadvertisement.get().getTitle());
            updateJobadvertisementRequest.setLocation(jobadvertisement.get().getLocation());
            updateJobadvertisementRequest.setDescription(jobadvertisement.get().getDescription());

            model.addAttribute("title", "Álláshirdetés szerkesztése");
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("updateJobadvertisementRequest", updateJobadvertisementRequest);
            return "/jobadvertisements/edit";
        } else {
            redirectAttributes.addFlashAttribute("message", "Nem sikerült az álláshirdetés lekérdezése!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addAttribute("id", loggedUser.getId());
            return "redirect:/employers/jobadvertisements/{id}";
        }
    }

    /**
     * Létező álláshirdetés módosítása az adatbázisban.
     *
     * @param id                            a módosítandó álláshirdetés azonosítója.
     * @param updateJobadvertisementRequest a módosítandó álláshirdetés adatai.
     * @param loggedUser                    a bejelentkezett felhasználó adatai.
     * @param bindingResult                 a validációs eredmények.
     * @param redirectAttributes            az átirányítás tulajdonságai.
     * @param model                         a visszaadandó nézetmodell injektálása.
     * @return a módosítás eredményeit tartalmazó nézetmodell.
     */
    @RequestMapping(value = "/jobadvertisements/update/{id}", method = RequestMethod.POST)
    public String updateJobadvertisement(
            @PathVariable("id") @Min(1) Long id,
            @Valid UpdateJobadvertisementRequest updateJobadvertisementRequest,
            @AuthenticationPrincipal MyUserDetails loggedUser,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "employers/edit";
        } else {

            Jobadvertisement jobadvertisement = new Jobadvertisement();
            jobadvertisement.setId(updateJobadvertisementRequest.getId());
            User user = new User();
            user.setId(loggedUser.getId());
            jobadvertisement.setUser(user);
            jobadvertisement.setTitle(updateJobadvertisementRequest.getTitle());
            jobadvertisement.setLocation(updateJobadvertisementRequest.getLocation());
            jobadvertisement.setDescription(updateJobadvertisementRequest.getDescription());

            try {
                jobadvertisementsService.updateJobadvertisement(jobadvertisement);
                redirectAttributes.addFlashAttribute("message", "Sikerült az álláshirdetés módosítása!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-success");
                redirectAttributes.addAttribute("id", loggedUser.getId());
                return "redirect:/employers/jobadvertisements/{id}";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "Nem sikerült az álláshirdetés módosítása!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                redirectAttributes.addAttribute("id", loggedUser.getId());
                return "redirect:/employers/jobadvertisements/{id}";
            }
        }
    }

    /**
     * Létező álláshirdetés törlése az adatbázisból művelet vezérlője.
     *
     * @param id                 a törlendő álláshirdetés azonosítója.
     * @param loggedUser         a bejelentkezett felhasználó adatai.
     * @param redirectAttributes az átirányítás tulajdonságai.
     * @param model              a visszaadandó nézetmodell injektálása.
     * @return az álláshirdetés törlésének sikerességét figyelembe vevő nézetmodell.
     */
    @RequestMapping(value = "/jobadvertisements/delete/{id}", method = RequestMethod.GET)
    public String deleteJobadvertisement(
            @PathVariable("id") @Min(1) Long id,
            @AuthenticationPrincipal MyUserDetails loggedUser,
            RedirectAttributes redirectAttributes,
            Model model) {
        Optional<Jobadvertisement> jobadvertisement = jobadvertisementsService.getJobadvertisementById(id);
        if (jobadvertisement.isPresent()) {

            try {
                jobadvertisementsService.deleteJobadvertisement(jobadvertisement.get().getId());
                redirectAttributes.addFlashAttribute("message", "Sikerült az álláshirdetés törlése!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-success");
                redirectAttributes.addAttribute("id", loggedUser.getId());
                return "redirect:/employers/jobadvertisements/{id}";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "Nem sikerült az álláshirdetés törlése!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                redirectAttributes.addAttribute("id", loggedUser.getId());
                return "redirect:/employers/jobadvertisements/{id}";
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Nem sikerült az álláshirdetés lekérdezése!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/";
        }
    }

    /**
     * Létező álláshirdetés lekérdezése az adatbázisból.
     * 
     * @return a lekérdezés eredményeit tartalmazó nézetmodell.
     */
    @RequestMapping(value = "/jobadvertisements/{id}", method = RequestMethod.GET)
    public ModelAndView showJobadvertisement() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("jobadvertisements/show");
        return modelAndView;
    }
}
