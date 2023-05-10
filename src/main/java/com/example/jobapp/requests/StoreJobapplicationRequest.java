package com.example.jobapp.requests;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A jelentkezés létrehozásának kérését leíró validációs változó.
 * 
 * @author Norbert Csomor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreJobapplicationRequest {
    @NotNull(message = "Az álláshirdetést ki kell választani!")
    private Long jobadvertisementId;
    @NotNull(message = "Az önéletrajzot ki kell választani!")
    private Long cvId;
}
