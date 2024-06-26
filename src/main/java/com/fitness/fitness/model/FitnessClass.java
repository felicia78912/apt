package com.fitness.fitness.model;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class FitnessClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String fitnessclassname;
    private String classdescription;
    private String classduration;

    public FitnessClass(){
        
    }
    
    
    public FitnessClass(int id, String fitnessclassname, String classdescription, String classduration) {
        this.id = id;
        this.fitnessclassname = fitnessclassname;
        this.classdescription = classdescription;
        this.classduration = classduration;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getFitnessclassname() {
        return fitnessclassname;
    }


    public void setFitnessclassname(String fitnessclassname) {
        this.fitnessclassname = fitnessclassname;
    }


    public String getClassdescription() {
        return classdescription;
    }


    public void setClassdescription(String classdescription) {
        this.classdescription = classdescription;
    }


    public String getClassduration() {
        return classduration;
    }


    public void setClassduration(String classduration) {
        this.classduration = classduration;
    }


    
}

