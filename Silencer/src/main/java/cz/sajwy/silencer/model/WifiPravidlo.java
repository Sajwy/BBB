package cz.sajwy.silencer.model;

public class WifiPravidlo extends Pravidlo {
    private String nazev_wifi;

    public WifiPravidlo() {
    }

    public WifiPravidlo(int id_pravidlo, String nazev, int stav, int vibrace, Kategorie kategorie, String nazev_wifi) {
        setId_pravidlo(id_pravidlo);
        setNazev(nazev);
        setVibrace(vibrace);
        setStav(stav);
        setKategorie(kategorie);
        this.nazev_wifi = nazev_wifi;

    }

    public String getNazev_wifi() {
        return nazev_wifi;
    }

    public void setNazev_wifi(String nazev_wifi) {
        this.nazev_wifi = nazev_wifi;
    }
}