package cz.sajwy.silencer.model;

import java.util.List;

public class Kategorie {
    private int id_kategorie;
    private String nazev;
    private List<Pravidlo> pravidla;

    public Kategorie() {
    }

    public Kategorie(int id_kategorie, String nazev) {
        this.id_kategorie = id_kategorie;
        this.nazev = nazev;
    }

    public Kategorie(int id_kategorie, String nazev, List<Pravidlo> pravidla) {
        this.id_kategorie = id_kategorie;
        this.nazev = nazev;
        this.pravidla = pravidla;
    }

    public Kategorie(String nazev) {
        this.nazev = nazev;
    }

    public int getId_kategorie() {
        return id_kategorie;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public List<Pravidlo> getPravidla() {
        return pravidla;
    }

    public void setPravidla(List<Pravidlo> pravidla) {
        this.pravidla = pravidla;
    }
}