package com.example.marketplaceandroid.models;
//import java.time.LocalDate;
import java.util.Date;
public class Annonce {
    private Long id;

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Long getId() {
        return id;
    }

//    public void setId(Long id) {
//        this.id = id;
//    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public Date getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Date datePublication) {
        this.datePublication = datePublication;
    }

    public boolean isApprouvee() {
        return approuvee;
    }

    public void setApprouvee(boolean approuvee) {
        this.approuvee = approuvee;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    private String titre;

    private String description;

    private double prix;

    public Annonce(String localisation, String titre, String description, double prix, Date datePublication, boolean approuvee, String categorie) {
        this.localisation = localisation;
        this.titre = titre;
        this.description = description;
        this.prix = prix;
        this.datePublication = datePublication;
        this.approuvee = approuvee;
        this.categorie = categorie;
    }

    private Date datePublication = new Date();

    private boolean approuvee = false;

    private String categorie;

    private String localisation;
}
