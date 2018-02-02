package cz.sajwy.silencer.model;

public class Udalost {
    private String nazev;
    private long zacatek;
    private long konec;

    public Udalost(String nazev, long zacatek, long konec) {
        this.nazev = nazev;
        this.zacatek = zacatek;
        this.konec = konec;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public long getZacatek() {
        return zacatek;
    }

    public void setZacatek(long zacatek) {
        this.zacatek = zacatek;
    }

    public long getKonec() {
        return konec;
    }

    public void setKonec(long konec) {
        this.konec = konec;
    }
}