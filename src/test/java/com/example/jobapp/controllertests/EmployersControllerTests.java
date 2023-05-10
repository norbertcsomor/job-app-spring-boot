package com.example.jobapp.controllertests;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.jobapp.auth.MySecurityConfiguration;
import com.example.jobapp.constrollers.EmployersController;
import com.example.jobapp.repositories.UsersRepository;
import com.example.jobapp.requests.StoreEmployerRequest;
import com.example.jobapp.services.EmployersService;
import com.example.jobapp.services.JobadvertisementsService;

@Import(MySecurityConfiguration.class)
@WebMvcTest(EmployersController.class)
public class EmployersControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployersService employersService;

    @MockBean
    private JobadvertisementsService jobadvertisementsService;

    @MockBean
    private UsersRepository usersRepository;

    @Test
    void shouldCreateEmployerWorkForAnonymousUser() throws Exception {
        this.mockMvc
                .perform(get("/employers/create"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("title"))
                .andExpect(model().attributeExists("storeEmployerRequest"))
                .andExpect(view().name("employers/create"));
    }

    @Test
    void shouldStoreEmployerWorkForAnonymousUser() throws Exception {

        StoreEmployerRequest storeEmployerRequest = new StoreEmployerRequest();
        storeEmployerRequest.setName("Teszt Munkaadó");
        storeEmployerRequest.setAddress("Teszt Cím");
        storeEmployerRequest.setTelephone("1231231231");
        storeEmployerRequest.setEmail("example@email.com");
        storeEmployerRequest.setPassword("123123123123");
        storeEmployerRequest.setPassword_confirmation("123123123123");
        storeEmployerRequest.setTerms("true");

        this.mockMvc
                .perform(post("/employers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", storeEmployerRequest.getName())
                        .param("address", storeEmployerRequest.getAddress())
                        .param("telephone", storeEmployerRequest.getTelephone())
                        .param("email", storeEmployerRequest.getEmail())
                        .param("password", storeEmployerRequest.getPassword())
                        .param("password_confirmation", storeEmployerRequest.getPassword_confirmation())
                        .param("terms", storeEmployerRequest.getTerms()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/"));
    }

    @Test
    void shouldEditEmployerRedirectAnonymousUserToLogin() throws Exception {
        this.mockMvc
                .perform(get("/employers/edit"))
                .andExpect(status().is3xxRedirection());
    }

}
