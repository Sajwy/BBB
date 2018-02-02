package cz.sajwy.silencer.model;

public class Pravidlo {
    private int id_pravidlo;
    private String nazev;
    private int stav;
    private int vibrace;
    private Kategorie kategorie;

    public Pravidlo() {
    }

    public Pravidlo(int id_pravidlo, String nazev, int stav, int vibrace, Kategorie kategorie) {
        this.id_pravidlo = id_pravidlo;
        this.nazev = nazev;
        this.stav = stav;
        this.vibrace = vibrace;
        this.kategorie = kategorie;
    }

    public Pravidlo(String nazev, int stav, int vibrace, Kategorie kategorie) {
        this.nazev = nazev;
        this.stav = stav;
        this.vibrace = vibrace;
        this.kategorie = kategorie;
    }

    public void setId_pravidlo(int id_pravidlo) {
        this.id_pravidlo = id_pravidlo;
    }

    public int getId_pravidlo() {
        return id_pravidlo;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public int getStav() {
        return stav;
    }

    public void setStav(int stav) {
        this.stav = stav;
    }

    public int getVibrace() {
        return vibrace;
    }

    public void setVibrace(int vibrace) {
        this.vibrace = vibrace;
    }

    public Kategorie getKategorie() {
        return kategorie;
    }

    public void setKategorie(Kategorie kategorie) {
        this.kategorie = kategorie;
    }
}