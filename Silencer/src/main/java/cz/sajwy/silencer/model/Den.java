package cz.sajwy.silencer.model;

public class Den {
    private int id_den;
    private String nazev;
    private String zkratka;

    public Den(int id_den, String nazev, String zkratka) {
        this.id_den = id_den;
        this.nazev = nazev;
        this.zkratka = zkratka;
    }

    public int getId_den() {
        return id_den;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public String getZkratka() {
        return zkratka;
    }

    public void setZkratka(String zkratka) {
        this.zkratka = zkratka;
    }
}