package com.example.jobapp.constrollers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.jobapp.models.User;
import com.example.jobapp.services.EmployersService;
import com.example.jobapp.services.FilesStorageService;
import com.example.jobapp.services.JobadvertisementsService;
import com.example.jobapp.services.JobseekersService;

/**
 * Az ügyintézők kezelésére szolgáló vezérlők osztálya.
 *
 * @author Norbert Csomor
 */
@Controller
public class AdminsController {

    @Autowired
    EmployersService employersService;

    @Autowired
    FilesStorageService filesStorageService;

    @Autowired
    JobseekersService jobseekersService;

    @Autowired
    JobadvertisementsService jobadvertisementsService;

    Logger logger = LoggerFactory.getLogger(AdminsController.class);

    /**
     * Az összes létező munkaadó lekérdezése az adatbázisból.
     * 
     * @return a munkaadók megjelenítésére szolgáló nézetmodell.
     */
    @RequestMapping(value = "/employers", method = RequestMethod.GET)
    public ModelAndView getEmployers() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Munkaadók");
        modelAndView.addObject("employers", employersService.getAllEmployers());
        modelAndView.setViewName("employers/index");
        return modelAndView;
    }

    /**
     * Az adatbázisban létező munkaadók PDF állományba történő mentése
     * az ügyintéző részére.
     */
    @RequestMapping(value = "/createEmployerPdf", method = RequestMethod.GET)
    public void createEmployerPdf() {

        List<User> employers = employersService.getAllEmployers();

        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        AccessPermission accessPermission = new AccessPermission();
        accessPermission.setCanPrint(true);
        accessPermission.setCanModify(false);
        accessPermission.setReadOnly();

        StandardProtectionPolicy standardProtectionPolicy = new StandardProtectionPolicy("ownerpass", "userpass",
                accessPermission);
        try {
            document.protect(standardProtectionPolicy);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        PDPageContentStream contentStream;

        try {
            contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
            contentStream.beginText();
            contentStream.showText("Munkaadók:");
            contentStream.newLine();
            for (User employer : employers) {
                contentStream
                        .showText(employer.getName() + " - " + employer.getEmail() + " - " + employer.getTelephone());
                contentStream.newLine();
            }
            contentStream.endText();
            contentStream.close();

            String filename = "munkaadok_" + LocalDate.now() + ".pdf";
            document.save(filename);
            document.close();

            // filesStorageService.loadFile(filename);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Az összes létező álláskereső lekérdezése az adatbázisból.
     * 
     * @return az álláskeresők megjelenítésére szolgáló nézetmodell.
     */
    @RequestMapping(value = "/jobseekers", method = RequestMethod.GET)
    public ModelAndView getJobseekers() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Álláskeresők");
        modelAndView.addObject("jobseekers", jobseekersService.getAllJobseekers());
        modelAndView.setViewName("jobseekers/index");
        return modelAndView;
    }

    /**
     * Az adatbázisban létező álláskeresők Excel állományba történő mentése
     * az ügyintéző részére.
     */
    @RequestMapping(value = "/createJobseekerExcelSheet", method = RequestMethod.GET)
    public void createJobseekerExcelSheet() {

        List<User> jobseekers = jobseekersService.getAllJobseekers();

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Álláskeresők");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Név");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Email cím");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Telefonszám");
        headerCell.setCellStyle(headerStyle);

        //

        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        int i = 2;

        Row row = sheet.createRow(i);

        for (User jobseeker : jobseekers) {            

            Cell cell = row.createCell(0);
            cell.setCellValue(jobseeker.getName());
            cell.setCellStyle(style);
            
            cell = row.createCell(1);
            cell.setCellValue(jobseeker.getEmail());
            cell.setCellStyle(style);
            
            cell = row.createCell(2);
            cell.setCellValue(jobseeker.getTelephone());
            cell.setCellStyle(style);

            row = sheet.createRow(i++);
        }
        //

        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "allaskeresok_" + LocalDate.now() + ".xlsx";

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
            workbook.close();

            filesStorageService.loadFile(fileLocation);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Az összes létező álláshirdetés lekérdezése az adatbázisból.
     * 
     * @return az álláshirdetések megjelenítésére szolgáló nézetmodell.
     */
    @RequestMapping(value = "/jobadvertisements", method = RequestMethod.GET)
    public ModelAndView getJobadvertisements() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Álláshirdetések");
        modelAndView.addObject("jobadvertisements", jobadvertisementsService.getAllJobadvertisements());
        modelAndView.setViewName("jobadvertisements/index");
        return modelAndView;
    }

    /**
     * 
     */
    @RequestMapping(value = "/sendJobadvertisementsByMail", method = RequestMethod.GET)
    public void sendJobadvertisementsByMail() {

    }
}
