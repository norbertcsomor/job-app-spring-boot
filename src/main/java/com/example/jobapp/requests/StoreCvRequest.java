package com.example.jobapp.requests;

import javax.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Az önéletrajz létrehozásának kérését leíró validációs változó.
 * 
 * @author Norbert Csomor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreCvRequest {

    private Long userId;
    @NotBlank(message = "A cím mező kötelező!")
    private String title;
    private MultipartFile path;
}
