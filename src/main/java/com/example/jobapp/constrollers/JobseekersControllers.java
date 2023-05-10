package com.example.jobapp.constrollers;

import com.example.jobapp.auth.MyUserDetails;
import com.example.jobapp.models.Cv;
import com.example.jobapp.models.Jobapplication;
import com.example.jobapp.models.User;
import com.example.jobapp.requests.StoreJobseekerRequest;
import com.example.jobapp.requests.StoreCvRequest;
import com.example.jobapp.requests.UpdateJobseekerRequest;
import com.example.jobapp.services.JobseekersService;
import com.example.jobapp.services.CvsService;
import com.example.jobapp.services.JobapplicationsService;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Az álláskeresők kezelésére szolgáló vezérlők osztálya.
 *
 * @author user Norbert Csomor
 */
@Controller
public class JobseekersControllers {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JobseekersService jobseekersService;

    @Autowired
    CvsService cvsService;
    
    @Autowired
    JobapplicationsService jobapplicationsService;

    Logger logger = LoggerFactory.getLogger(JobseekersControllers.class);

    /**
     * Új álláskereső létrehozására szolgáló űrlap megjelenítése.
     *
     * @param model a visszaadandó nézetmodell inicializálása.
     * @return a álláskereső létrehozására szolgáló nézetmodell.
     */
    @RequestMapping(
        value = "/jobseekers/create",
        method = RequestMethod.GET
    )
    public String createJobseeker(
        Model model) {
        model.addAttribute("title", 
        "Álláskereső létrehozása");
        model.addAttribute("storeJobseekerRequest",
         new StoreJobseekerRequest());
        return "jobseekers/create";
    }

    /**
     * Új álláskereső létrehozása az adatbázisban.
     *
     * @param storeJobseekerRequest a létrehozandó álláskereső adatai.
     * @param bindingResult a validációs eredményeket tartalmazó változó.
     * @param redirectAttributes
     * @param model a visszaadandó nézetmodell inicializálása.
     * @return a létrehozás eredményeit tartalmazó nézetmodell.
     */
    @PostMapping
    public String storeJobseeker(
            @Valid StoreJobseekerRequest storeJobseekerRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "/jobseekers/create";
        } else {

            if (jobseekersService.jobseekerExistsByEmail(storeJobseekerRequest.getEmail())) {
                redirectAttributes.addFlashAttribute("message", "Az email-cím már használatban van!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                return "redirect:/jobseekers/create";
            }

            if (!storeJobseekerRequest.getPassword().equals(storeJobseekerRequest.getPassword_confirmation())) {
                redirectAttributes.addFlashAttribute("message", "A jelszavak nem egyeznek meg!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                return "redirect:/jobseekers/create";
            }

            User jobseeker = new User();
            jobseeker.setName(storeJobseekerRequest.getName());
            jobseeker.setAddress(storeJobseekerRequest.getAddress());
            jobseeker.setTelephone(storeJobseekerRequest.getTelephone());
            jobseeker.setEmail(storeJobseekerRequest.getEmail());
            jobseeker.setPassword(storeJobseekerRequest.getPassword());

            try {
                jobseekersService.createJobseeker(jobseeker);
                System.out.println(jobseeker);
                // login
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(storeJobseekerRequest.getEmail(),
                        storeJobseekerRequest.getPassword());
                Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                redirectAttributes.addFlashAttribute("message", "Sikerült az álláskereső létrehozása!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-success");
                return "redirect:/";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "Nem sikerült az álláskereső létrehozása!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                return "redirect:/jobseekers/create";
            }
        }
    }

    /**
     * Létező álláskereső módosítására szolgáló űrlap megjelenítése.
     *
     * @param id
     * @param loggedUser
     * @param redirectAttributes
     * @param model
     * @return a álláskereső módosítására szolgáló nézetmodell.
     */
    @GetMapping("/edit/{id}")
    public String editJobseeker(
            @PathVariable("id") @Min(1) Long id,
            @AuthenticationPrincipal MyUserDetails loggedUser,
            RedirectAttributes redirectAttributes,
            Model model) {

        Optional<User> jobseeker = jobseekersService.getJobseekerById(id);

        if (jobseeker.isPresent()) {

            List<Cv> cvs = cvsService.getUserCvs(id);

            UpdateJobseekerRequest updateJobseekerRequest = new UpdateJobseekerRequest();
            updateJobseekerRequest.setId(jobseeker.get().getId());
            updateJobseekerRequest.setName(jobseeker.get().getName());
            updateJobseekerRequest.setAddress(jobseeker.get().getAddress());
            updateJobseekerRequest.setTelephone(jobseeker.get().getTelephone());

            model.addAttribute("title", "Álláskereső szerkesztése");
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("storeCvRequest", new StoreCvRequest());
            model.addAttribute("cvs", cvs);
            model.addAttribute("updateJobseekerRequest", updateJobseekerRequest);
            return "jobseekers/edit";
        } else {
            redirectAttributes.addFlashAttribute("message", "Nem sikerült a álláskereső lekérdezése!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/";
        }
    }

    /**
     * Létező álláskereső módosítása az adatbázisban.
     *
     * @param id
     * @param updateJobseekerRequest a módosítandó álláskereső adatai.
     * @param loggedUser
     * @param bindingResult
     * @param redirectAttributes
     * @param model
     * @return a módosítás eredményeit tartalmazó nézetmodell.
     */
    @PostMapping("/update/{id}")
    public String updateJobseeker(
            @PathVariable("id") @Min(1) Long id,
            @Valid @ModelAttribute("updateJobseekerRequest") UpdateJobseekerRequest updateJobseekerRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal MyUserDetails loggedUser,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateJobseekerRequest", bindingResult);
            redirectAttributes.addFlashAttribute("updateJobseekerRequest", updateJobseekerRequest);
            redirectAttributes.addFlashAttribute("message", "Érvénytelen adatok!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addAttribute("id", loggedUser.getId());
            return "redirect:/jobseekers/edit/{id}";
        } else {

            Optional<User> jobseeker = jobseekersService.getJobseekerById(id);
            if (jobseeker.isPresent()) {
                User jobseekerInDb = jobseeker.get();
                jobseekerInDb.setId(updateJobseekerRequest.getId());
                jobseekerInDb.setName(updateJobseekerRequest.getName());
                jobseekerInDb.setAddress(updateJobseekerRequest.getAddress());
                jobseekerInDb.setTelephone(updateJobseekerRequest.getTelephone());

                try {
                    User updatedJobseeker = jobseekersService.updateJobseeker(jobseekerInDb);
                    loggedUser.setId(updatedJobseeker.getId());
                    loggedUser.setName(updatedJobseeker.getName());
                    redirectAttributes.addFlashAttribute("message", "Sikerült a álláskereső módosítása!");
                    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
                    return "redirect:/";
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("message", "Nem sikerült a álláskereső módosítása!");
                    redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                    redirectAttributes.addAttribute("id", loggedUser.getId());
                    return "redirect:/jobseekers/edit/{id}";
                }
            } else {
                redirectAttributes.addFlashAttribute("message", "Nem sikerült a álláskereső módosítása!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                redirectAttributes.addAttribute("id", loggedUser.getId());
                return "redirect:/jobseekers/edit/{id}";
            }
        }
    }

    /**
     * Létező álláskereső törlése az adatbázisból.
     *
     * @param id a törlendő álláskereső azonosítója.
     * @param httpServletRequest a beérkező kérés felhasználása a
     * kijelentkeztetéshez.
     * @param redirectAttributes
     * @param model a
     * @return a törlés eredményeit tartalmazó nézetmodell.
     */
    @GetMapping("/delete/{id}")
    public String deleteJobseeker(
            @PathVariable("id") @Min(1) Long id,
            HttpServletRequest httpServletRequest,
            RedirectAttributes redirectAttributes,
            Model model) {
        Optional<User> jobseeker = jobseekersService.getJobseekerById(id);
        if (jobseeker.isPresent()) {
            try {
                jobseekersService.deleteJobseeker(jobseeker.get().getId());
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String loggedUserEmail = auth.getName();
                // logout
                if (loggedUserEmail.equals(jobseeker.get().getEmail())) {
                    new SecurityContextLogoutHandler().logout(httpServletRequest, null, auth);
                    SecurityContextHolder.getContext().setAuthentication(null);
                }
                redirectAttributes.addFlashAttribute("message", "Sikerült a álláskereső törlése!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-success");
                return "redirect:/";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "Nem sikerült a álláskereső törlése!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                return "redirect:/";
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Nem sikerült a álláskereső lekérdezése!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/";
        }
    }

    /**
     * Létező álláskereső összes létező jelentkezésének lekérdezése az
     * adatbázisból.
     *
     * @param id
     * @param loggedUser
     * @param model
     * @return
     */
    @GetMapping("/jobapplications/{id}")
    public String getJobseekerJobapplications(
            @PathVariable("id") @Min(1) Long id,
            @AuthenticationPrincipal MyUserDetails loggedUser,
            Model model) {

        List<Jobapplication> jobapplications = jobapplicationsService
                .getJobseekerJobapplications(loggedUser.getId());

        model.addAttribute("title", "Álláskereső jelentkezései");
        model.addAttribute("jobapplications", jobapplications);
        model.addAttribute("loggedUser", loggedUser);

        return "jobseekers/jobapplications";
    }
}
