package com.example.jobapp.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A jelentkezés adatait/tulajdonságait tartalmazó entitás.
 * 
 * @author Norbert Csomor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jobapplications")
public class Jobapplication implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "jobadvertisementId", referencedColumnName = "id")
    private Jobadvertisement jobadvertisement;
    @ManyToOne
    @JoinColumn(name = "cvId", referencedColumnName = "id")
    private Cv cv;
    private String status;

}
