package com.example.jobapp.requests;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A jelentkezés módosításának kérését leíró validációs változó.
 * 
 * @author Norbert Csomor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateJobapplicationRequest {

    @NotBlank(message = "Az állapot mező kitöltése kötelező!")
    private String status;
}
