package com.example.jobapp.constrollers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.jobapp.auth.MyUserDetails;
import com.example.jobapp.models.Cv;
import com.example.jobapp.models.Jobadvertisement;
import com.example.jobapp.models.Jobapplication;
import com.example.jobapp.requests.StoreCvRequest;
import com.example.jobapp.requests.StoreJobapplicationRequest;
import com.example.jobapp.requests.UpdateJobapplicationRequest;
import com.example.jobapp.services.CvsService;
import com.example.jobapp.services.JobadvertisementsService;
import com.example.jobapp.services.JobapplicationsService;

/**
 * Az álláshirdetésre történő jelentkezés vezérlőinek osztálya.
 * 
 * @author Norbert Csomor
 */
@Controller
@RequestMapping("/jobapplications")
public class JobapplicationsController {

        @Autowired
        JobadvertisementsService jobadvertisementsService;

        @Autowired
        JobapplicationsService jobapplicationsService;

        @Autowired
        CvsService cvsService;

        /**
         * Jelentkezés létrehozása az adatbázisban művelet vezérlője.
         *
         * @param jobadvertisementId az álláshirdetés azonosítója.
         * @param loggedUser         a bejelentkezett felhasználó adatai.
         * @param redirectAttributes az átirányítás tulajdonságai.
         * @param model              a visszaadandó nézetmodell injektálása.
         * @return
         */
        @GetMapping("/create/{jobadvertisementId}")
        public String createJobapplication(
                        @PathVariable("jobadvertisementId") @Min(1) Long jobadvertisementId,
                        @AuthenticationPrincipal MyUserDetails loggedUser,
                        RedirectAttributes redirectAttributes,
                        Model model) {

                List<Jobapplication> jobadvertisementAndJobseekerJobapplications = jobapplicationsService
                                .getJobadvertisementAndJobseekerJobapplications(
                                                jobadvertisementId, loggedUser.getId());

                if (jobadvertisementAndJobseekerJobapplications.isEmpty()) {

                        Optional<Jobadvertisement> jobadvertisement = jobadvertisementsService
                                        .getJobadvertisementById(jobadvertisementId);

                        if (jobadvertisement.isPresent()) {

                                List<Cv> cvs = cvsService.getUserCvs(loggedUser.getId());
                                model.addAttribute("loggedUserId",
                                                loggedUser.getId());
                                model.addAttribute("loggedUser",
                                                loggedUser);
                                model.addAttribute("title",
                                                "Jelentkezés");
                                model.addAttribute("storeCvRequest",
                                                new StoreCvRequest());
                                model.addAttribute("storeJobapplicationRequest",
                                                new StoreJobapplicationRequest());
                                model.addAttribute("jobadvertisement",
                                                jobadvertisement.get());
                                model.addAttribute("cvs", cvs);
                                return "jobapplications/create";

                        } else {
                                redirectAttributes.addFlashAttribute("message",
                                                "Nem sikerült az álláshirdetés lekérdezése!");
                                redirectAttributes.addFlashAttribute("alertClass",
                                                "alert-danger");
                                return "redirect:/";
                        }
                } else {
                        redirectAttributes.addFlashAttribute("message",
                                        "Már jelentkezett az állásra!");
                        redirectAttributes.addFlashAttribute("alertClass",
                                        "alert-danger");
                        return "redirect:/";
                }
        }

        @PostMapping
        public String storeJobapplication(
                        @Valid StoreJobapplicationRequest storeJobapplicationRequest,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal MyUserDetails loggedUser,
                        RedirectAttributes redirectAttributes,
                        Model model) {
                if (bindingResult.hasErrors()) {
                        redirectAttributes.addFlashAttribute("message",
                                        "Nem sikerült a jelentkezés létrehozása!");
                        redirectAttributes.addFlashAttribute("alertClass",
                                        "alert-danger");
                        redirectAttributes.addFlashAttribute(
                                        "org.springframework.validation.BindingResult.storeJobapplicationRequest",
                                        bindingResult);
                        redirectAttributes.addFlashAttribute("storeJobapplicationRequest",
                                        storeJobapplicationRequest);
                        return "redirect:/jobadvertisements/show";
                } else {

                        Jobapplication jobapplication = new Jobapplication();

                        Jobadvertisement jobadvertisement = new Jobadvertisement();
                        jobadvertisement.setId(storeJobapplicationRequest.getJobadvertisementId());
                        jobapplication.setJobadvertisement(jobadvertisement);

                        Cv cv = new Cv();
                        cv.setId(storeJobapplicationRequest.getCvId());
                        jobapplication.setCv(cv);

                        try {
                                jobapplicationsService.createJobapplication(jobapplication);
                                System.out.println(jobapplication);

                                redirectAttributes.addFlashAttribute("message",
                                                "Sikerült a jelentkezés létrehozása!");
                                redirectAttributes.addFlashAttribute("alertClass",
                                                "alert-success");
                                return "redirect:/";
                        } catch (Exception e) {
                                redirectAttributes.addFlashAttribute("message",
                                                "Nem sikerült a jelentkezés létrehozása!");
                                redirectAttributes.addFlashAttribute("alertClass",
                                                "alert-danger");
                                return "redirect:/";
                        }
                }
        }

        @PostMapping("/update/{id}")
        public String updateJobapplication(
                        @PathVariable("id") @Min(1) Long id,
                        @Valid UpdateJobapplicationRequest updateJobapplicationRequest,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal MyUserDetails loggedUser,
                        RedirectAttributes redirectAttributes,
                        Model model) {
                if (updateJobapplicationRequest.getStatus().equals("Elfogadva")
                                || updateJobapplicationRequest.getStatus().equals("Elutasítva")) {

                        Optional<Jobapplication> jobapplicationInDb = jobapplicationsService.getJobapplicationById(id);

                        if (jobapplicationInDb.isPresent()) {
                                Jobapplication jobapplication = jobapplicationInDb.get();
                                jobapplication.setStatus(updateJobapplicationRequest.getStatus());
                                try {
                                        jobapplicationsService.updateJobapplication(jobapplication);

                                        redirectAttributes.addFlashAttribute("message",
                                                        "Sikerült a jelentkezés állapotának módosítása!");
                                        redirectAttributes.addFlashAttribute("alertClass",
                                                        "alert-success");
                                        redirectAttributes.addAttribute("id",
                                                        jobapplication.getJobadvertisement().getId());
                                        return "redirect:/jobadvertisements/show/{id}";
                                } catch (Exception e) {
                                        redirectAttributes.addFlashAttribute("message",
                                                        "Nem sikerült a jelentkezés állapotának módosítása!");
                                        redirectAttributes.addFlashAttribute("alertClass",
                                                        "alert-danger");
                                        redirectAttributes.addAttribute("id",
                                                        jobapplication.getJobadvertisement().getId());
                                        return "redirect:/jobadvertisements/{id}";
                                }
                        } else {
                                redirectAttributes.addFlashAttribute("message",
                                                "Nem található a módosítandó jelentkezés!");
                                redirectAttributes.addFlashAttribute("alertClass",
                                                "alert-danger");
                                return "redirect:/";
                        }
                }
                return "redirect:/";
        }

        @GetMapping("/delete/{id}")
        public String deleteJobapplication(
                        @PathVariable("id") @Min(1) Long id,
                        RedirectAttributes redirectAttributes,
                        Model model) {
                Optional<Jobapplication> jobapplication = jobapplicationsService.getJobapplicationById(id);
                if (jobapplication.isPresent()) {
                        try {
                                jobapplicationsService.deleteJobapplication(jobapplication.get().getId());
                                redirectAttributes.addFlashAttribute("message", "Sikerült a jelentkezés törlése!");
                                redirectAttributes.addFlashAttribute("alertClass", "alert-success");
                                return "redirect:/";
                        } catch (Exception e) {
                                redirectAttributes.addFlashAttribute("message", "Nem sikerült a jelentkezés törlése!");
                                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                                return "redirect:/";
                        }
                } else {
                        redirectAttributes.addFlashAttribute("message", "Nem sikerült a jelentkezés lekérdezése!");
                        redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                        return "redirect:/";
                }
        }
}
