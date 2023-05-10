package com.example.jobapp.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * A bejelentkezés kérést leíró validációs változó.
 * 
 * @author Norbert Csomor
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Az email-cím mező kitöltése kötelező!")
    @Email(message = "Az email-cím formátuma nem megfelelő!")
    private String email;
    @NotBlank(message = "A jelszó mező kitöltése kötelező!")
    private String password;
}
