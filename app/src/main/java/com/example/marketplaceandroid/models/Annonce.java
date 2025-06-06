package com.example.marketplaceandroid.models;

import java.util.Date;
import java.util.List;

public class Annonce {
    private Long id;
    private String titre;
    private String description;
    private double prix;
    private Date datePublication = new Date();
    private boolean approuvee = false;
    private String categorie;
    private String localisation;

    // Nouvelles propriétés nécessaires pour l'adapter
    private List<String> images;
    private Vendeur vendeur;
    private boolean favorite = false;

    // Constructeur
    public Annonce(String localisation, String titre, String description, double prix, Date datePublication, boolean approuvee, String categorie) {
        this.localisation = localisation;
        this.titre = titre;
        this.description = description;
        this.prix = prix;
        this.datePublication = datePublication;
        this.approuvee = approuvee;
        this.categorie = categorie;
    }

    // Constructeur par défaut (nécessaire pour la désérialisation JSON)
    public Annonce() {}

    // Getters et Setters existants
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    // Nouveaux getters et setters pour la compatibilité avec l'adapter
    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Vendeur getVendeur() {
        return vendeur;
    }

    public void setVendeur(Vendeur vendeur) {
        this.vendeur = vendeur;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    // Classe interne Vendeur (ou créez une classe séparée si nécessaire)
    public static class Vendeur {
        private String nomComplet;
        private Long id;

        public Vendeur() {}

        public Vendeur(String nomComplet) {
            this.nomComplet = nomComplet;
        }

        public String getNomComplet() {
            return nomComplet;
        }

        public void setNomComplet(String nomComplet) {
            this.nomComplet = nomComplet;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}