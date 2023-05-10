package com.example.jobapp.constrollers;

import com.example.jobapp.auth.MyUserDetails;
import com.example.jobapp.models.Cv;
import com.example.jobapp.models.User;
import com.example.jobapp.requests.StoreCvRequest;
import com.example.jobapp.services.CvsService;
import com.example.jobapp.services.FilesStorageService;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Az önéletrajzok kezelésére szolgáló vezérlők osztálya.
 *
 * @author Norbert Csomor
 */
@Controller
public class CvsController {

        @Autowired
        CvsService cvsService;

        @Autowired
        FilesStorageService filesStorageService;

        Logger logger = LoggerFactory.getLogger(CvsController.class);

        /**
         * Új önéletrajz létrehozása az adatbázisban.
         *
         * @param multipartFile      a feltöltendő önéletrajz.
         * @param storeCvRequest     a létrehozandó önéletrajz adatai.
         * @param bindingResult      a validációs eredényeket tartalmazó állapotváltozó.
         * @param redirectAttributes az átirányítás tulajdonságai.
         * @param model              a visszaadandó nézetmodell injektálása.
         * @return a szerver válasza.
         */
        @RequestMapping(value = "/cvs", method = RequestMethod.POST)
        public String storeCv(
                        @ModelAttribute("storeCvRequest") @Valid StoreCvRequest storeCvRequest,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes,
                        @RequestParam("path") MultipartFile multipartFile,
                        Model model) {

                if (bindingResult.hasErrors()) {
                        logger.error("Hiba volt az önéletrajz elküldött adataiban!");
                        redirectAttributes.addFlashAttribute("message",
                                        "Érvénytelen adatok!");
                        redirectAttributes.addFlashAttribute("alertClass",
                                        "alert-danger");
                        redirectAttributes.addFlashAttribute(
                                        "org.springframework.validation.BindingResult.storeCvRequest",
                                        bindingResult);
                        redirectAttributes.addFlashAttribute("storeCvRequest",
                                        storeCvRequest);
                        redirectAttributes.addAttribute("id",
                                        storeCvRequest.getUserId());
                        return "redirect:/jobseekers/edit/{id}";
                } else {

                        // Fájl mentése rész
                        String fileName = storeCvRequest.getUserId().toString() + "_"
                                        + storeCvRequest.getTitle() + ".pdf";

                        boolean isExistingFileOverwritten = false;

                        try {
                                isExistingFileOverwritten = filesStorageService.saveFile(
                                                fileName,
                                                multipartFile);
                        } catch (Exception e) {
                                logger.info("Hiba történt a fájl feltöltésekor:" + e.getMessage());
                                isExistingFileOverwritten = false;
                        }

                        if (!isExistingFileOverwritten) {
                                Cv cv = new Cv();
                                User user = new User();
                                user.setId(storeCvRequest.getUserId());
                                cv.setUser(user);
                                cv.setTitle(storeCvRequest.getTitle());

                                cvsService.createCv(cv);

                                logger.info("A fájl nem létezett a létrehozás előtt! ;)");

                                logger.info("Sikerült az önéletrajz létrehozása!");

                                redirectAttributes.addFlashAttribute("message",
                                                "Sikerült az önéletrajz létrehozása!");
                                redirectAttributes.addFlashAttribute("alertClass",
                                                "alert-success");
                        }

                        redirectAttributes.addAttribute("id",
                                        storeCvRequest.getUserId());
                        return "redirect:/jobseekers/edit/{id}";
                }
        }

        /**
         * Létező önéletrajz letöltése a szerverről.
         *
         * @param filename a letöltendő fájl neve.
         * @return a letöltendő fájl.
         */
        @RequestMapping(value = "/cvs/{filename:.+}", method = RequestMethod.GET)
        public ResponseEntity<Resource> getCv(@PathVariable String filename) {
                Resource file = filesStorageService.loadFile(filename);

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=\"" + file.getFilename() + "\"")
                                .body(file);
        }

        /**
         * Létező önéletrajz törlése a szerverről.
         *
         * @param id                 a törlendő önéletrajz azonosítója.
         * @param redirectAttributes az átirányítás tulajdonságai.
         * @param loggedUser         a bejelentkezett felhasználó adatai.
         * @param model              a visszaadandó nézetmodell injektálása.
         * @return a törlés eredményességének megfelelő nézetmodell.
         */
        @GetMapping("/cvs/delete/{id}")
        public String deleteCv(
                        @PathVariable("id") Long id,
                        RedirectAttributes redirectAttributes,
                        @AuthenticationPrincipal MyUserDetails loggedUser,
                        Model model) {
                // az önéletrajz keresése az adatbázisban
                Optional<Cv> cv = cvsService.getCvById(id);
                // az önéletrajz létezik az adatbázisban
                if (cv.isPresent()) {
                        // próbáljuk törölni az önéletrajzot a lemezen
                        try {
                                boolean cvDeletedFromDisk = filesStorageService
                                                .deleteFile(cv.get().getUser().getId() + "_"
                                                                + cv.get().getTitle() + ".pdf");
                                // ha sikerült törölni az önéletrajzot a lemezen
                                if (cvDeletedFromDisk) {
                                        // próbáljuk törölni az önéletrajzot az adatbázisból
                                        Long deletedId = cvsService.deleteCv(id);
                                        // ha sikerült törölni az önéletrajzot a lemezen
                                        if (deletedId > 0) {
                                                logger.info("Sikerült az önéletrajz törlése!");
                                                redirectAttributes.addFlashAttribute("message",
                                                                "Sikerült az önéletrajz törlése!");
                                                redirectAttributes.addFlashAttribute("alertClass",
                                                                "alert-success");
                                        } else {
                                                logger.error("Nem sikerült az önéletrajz törlése!");
                                                redirectAttributes.addFlashAttribute("message",
                                                                "Nem sikerült az önéletrajz törlése!");
                                                redirectAttributes.addFlashAttribute("alertClass",
                                                                "alert-danger");
                                        }
                                } else {
                                        logger.error("Nem sikerült az önéletrajz törlése!");
                                        redirectAttributes.addFlashAttribute("message",
                                                        "Nem sikerült az önéletrajz törlése!");
                                        redirectAttributes.addFlashAttribute("alertClass",
                                                        "alert-danger");
                                }
                        } catch (Exception e) {
                                logger.error("Nem sikerült az önéletrajz törlése!");
                                redirectAttributes.addFlashAttribute("message",
                                                "Nem sikerült az önéletrajz törlése!");
                                redirectAttributes.addFlashAttribute("alertClass",
                                                "alert-danger");
                        }
                        redirectAttributes.addAttribute("id",
                                        cv.get().getUser().getId().toString());
                        return "redirect:/jobseekers/edit/{id}";
                }

                logger.error("Nem sikerült az önéletrajz lekérdezése!");
                redirectAttributes.addFlashAttribute("message",
                                "Nem sikerült az önéletrajz lekérdezése!");
                redirectAttributes.addFlashAttribute("alertClass",
                                "alert-danger");
                return "redirect:/";
        }
}
