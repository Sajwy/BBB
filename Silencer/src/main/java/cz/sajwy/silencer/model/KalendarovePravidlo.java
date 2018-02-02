package cz.sajwy.silencer.model;

public class KalendarovePravidlo extends Pravidlo {
    private String udalost;

    public KalendarovePravidlo() {
    }

    public KalendarovePravidlo(int id_pravidlo, String nazev, int stav, int vibrace, Kategorie kategorie, String udalost) {
        setId_pravidlo(id_pravidlo);
        setNazev(nazev);
        setVibrace(vibrace);
        setStav(stav);
        setKategorie(kategorie);
        this.udalost = udalost;
    }

    public String getUdalost() {
        return udalost;
    }

    public void setUdalost(String udalost) {
        this.udalost = udalost;
    }
}