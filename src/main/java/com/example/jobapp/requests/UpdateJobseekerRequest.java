package com.example.jobapp.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Az álláskereső módosításának kérését leíró validációs változó.
 * 
 * @author Norbert Csomor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateJobseekerRequest {

    private Long id;
    @NotBlank(message = "A név mező kitöltése kötelező!")
    private String name;
    @NotBlank(message = "A cím mező kitöltése kötelező!")
    private String address;
    @NotBlank(message = "A telefonszám mező kitöltése kötelező!")
    @Size(max = 10, min = 10, message = "A telefonszámnak 10 karakterből kell állnia!")
    @Pattern(regexp = "[0-9][0-9]{9}", message = "A telefonszám formátuma nem megfelelő!!")
    private String telephone;
}
