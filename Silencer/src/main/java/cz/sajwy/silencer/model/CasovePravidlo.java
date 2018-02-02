package cz.sajwy.silencer.model;

import java.util.List;

public class CasovePravidlo extends Pravidlo {
    private List<Den> dny;
    private String cas_od;
    private String cas_do;
    private long cas_od_long;
    private long cas_do_long;
    private String vypisDnuNazvy;
    private String vypisDnuZkratky;

    public CasovePravidlo() {
    }

    public CasovePravidlo(int id_pravidlo, String nazev, int stav, int vibrace, Kategorie kategorie, List<Den> dny, String cas_od, String cas_do, String vypisDnuNazvy, String vypisDnuZkratky) {
        setId_pravidlo(id_pravidlo);
        setNazev(nazev);
        setVibrace(vibrace);
        setStav(stav);
        setKategorie(kategorie);
        this.dny = dny;
        this.cas_od = cas_od;
        this.cas_do = cas_do;
        this.vypisDnuNazvy = vypisDnuNazvy;
        this.vypisDnuZkratky = vypisDnuZkratky;
    }

    public CasovePravidlo(int id_pravidlo, String nazev, int stav, int vibrace, Kategorie kategorie, List<Den> dny, long cas_od_long, long cas_do_long) {
        setId_pravidlo(id_pravidlo);
        setNazev(nazev);
        setVibrace(vibrace);
        setStav(stav);
        setKategorie(kategorie);
        this.dny = dny;
        this.cas_od_long = cas_od_long;
        this.cas_do_long = cas_do_long;
    }

    public List<Den> getDny() {
        return dny;
    }

    public void setDny(List<Den> dny) {
        this.dny = dny;
    }

    public String getCas_od() {
        return cas_od;
    }

    public void setCas_od(String cas_od) {
        this.cas_od = cas_od;
    }

    public String getCas_do() {
        return cas_do;
    }

    public void setCas_do(String cas_do) {
        this.cas_do = cas_do;
    }

    public long getCas_do_long() {
        return cas_do_long;
    }

    public void setCas_do_long(long cas_do_long) {
        this.cas_do_long = cas_do_long;
    }

    public long getCas_od_long() {
        return cas_od_long;
    }

    public void setCas_od_long(long cas_od_long) {
        this.cas_od_long = cas_od_long;
    }

    public String getVypisDnuNazvy() {
        return vypisDnuNazvy;
    }

    public void setVypisDnuNazvy(String vypisDnuNazvy) {
        this.vypisDnuNazvy = vypisDnuNazvy;
    }

    public String getVypisDnuZkratky() {
        return vypisDnuZkratky;
    }

    public void setVypisDnuZkratky(String vypisDnuZkratky) {
        this.vypisDnuZkratky = vypisDnuZkratky;
    }
}