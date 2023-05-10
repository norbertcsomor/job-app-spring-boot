package com.example.jobapp.utils;

import com.example.jobapp.models.Cv;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.jobapp.models.Jobadvertisement;
import com.example.jobapp.models.Jobapplication;
import com.example.jobapp.models.Role;
import com.example.jobapp.models.User;
import com.example.jobapp.repositories.CvsRepository;
import com.example.jobapp.repositories.JobadvertisementsRepository;
import com.example.jobapp.repositories.JobapplicationsRepository;
import com.example.jobapp.repositories.RolesRepository;
import com.example.jobapp.repositories.UsersRepository;

/**
 * A beágyazott H2 adatbázis alapadatokkkal való feltöltését végző művelet osztálya.
 * 
 * @author Norbert Csomor
 */
@Component
class DbSeeder {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JobadvertisementsRepository jobadvertisementsRepository;

    @Autowired
    private CvsRepository cvsRepository;
    
    @Autowired
    private JobapplicationsRepository jobapplicationsRepository;

    @PostConstruct
    public void init() {

        // Szerepek létrehozása
        Role role1 = new Role();
        role1.setName("Admin");
        rolesRepository.save(role1);
        Role role2 = new Role();
        role2.setName("Employer");
        rolesRepository.save(role2);
        Role role3 = new Role();
        role3.setName("Jobseeker");
        rolesRepository.save(role3);

        // Teszt Munkaadó létrehozása
        User employer = new User();
        employer.setName("Teszt Munkaadó");
        employer.setAddress("Teszt Cím");
        employer.setTelephone("1231231231");
        employer.setEmail("asd@asd.com");
        employer.setPassword(passwordEncoder.encode("asd"));
        Role employerRole = rolesRepository.findByName("Employer");
        employer.setRole(employerRole);
        usersRepository.save(employer);

        // Teszt Álláshirdetés létrehozása
        Jobadvertisement jobadvertisement = new Jobadvertisement();
        jobadvertisement.setUser(employer);
        jobadvertisement.setTitle("Teszt Álláshirdetés");
        jobadvertisement.setLocation("Teszt Hely");
        jobadvertisement.setDescription("<b>Teszt Leírás</b>");
        jobadvertisementsRepository.save(jobadvertisement);

        // Teszt Álláskereső létrehozása
        User jobseeker = new User();
        jobseeker.setName("Teszt Álláskereső");
        jobseeker.setAddress("Teszt Cím");
        jobseeker.setTelephone("1231231231");
        jobseeker.setEmail("qwe@qwe.com");
        jobseeker.setPassword(passwordEncoder.encode("qwe"));
        Role jobseekerRole = rolesRepository.findByName("Jobseeker");
        jobseeker.setRole(jobseekerRole);
        usersRepository.save(jobseeker);

        // Teszt Álláskereső létrehozása
        User jobseeker2 = new User();
        jobseeker2.setName("Teszt Álláskereső 2");
        jobseeker2.setAddress("Teszt Cím");
        jobseeker2.setTelephone("1231231231");
        jobseeker2.setEmail("pop@pop.com");
        jobseeker2.setPassword(passwordEncoder.encode("pop"));
        jobseeker2.setRole(jobseekerRole);
        usersRepository.save(jobseeker2);

        // Teszt Önéletrajz létrehozása
        Cv cv = new Cv();
        cv.setUser(jobseeker);
        cv.setTitle("Teszt Önéletrajz");
        // cv.setPath(jobseeker.getId() + "_" + "Teszt Önéletrajz" + ".pdf");
        cvsRepository.save(cv);
        
        Cv cv2 = new Cv();
        cv2.setUser(jobseeker2);
        cv2.setTitle("Teszt Önéletrajz 2");
        // cv.setPath(jobseeker.getId() + "_" + "Teszt Önéletrajz" + ".pdf");
        cvsRepository.save(cv2);
        
        Cv cv3 = new Cv();
        cv3.setUser(jobseeker2);
        cv3.setTitle("Teszt Önéletrajz 3");
        // cv.setPath(jobseeker.getId() + "_" + "Teszt Önéletrajz" + ".pdf");
        cvsRepository.save(cv3);

        // Teszt Jelentkezés létrehozása
        Jobapplication jobapplication = new Jobapplication();
        jobapplication.setCv(cv);
        jobapplication.setJobadvertisement(jobadvertisement);
        jobapplication.setStatus("Nincs megnézve");
        jobapplicationsRepository.save(jobapplication);

        // Teszt Ügyintéző létrehozása
        User admin = new User();
        admin.setName("Teszt Ügyintéző");
        admin.setAddress("Teszt Cím");
        admin.setTelephone("1231231231");
        admin.setEmail("www@www.com");
        admin.setPassword(passwordEncoder.encode("www"));
        Role adminRole = rolesRepository.findByName("Admin");
        admin.setRole(adminRole);
        usersRepository.save(admin);
    }
}
