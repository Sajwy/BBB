package cz.sajwy.silencer.dao;

import java.util.List;

import cz.sajwy.silencer.model.Pravidlo;

public interface PravidloDao {
    String createTable();
    String createIndex();
    int insertPravidlo(Pravidlo pravidlo);
    void updatePravidlo(Pravidlo novePravidlo);
    void updateStavPravidla(int id, int novyStav);
    void deletePravidlo(int id);
    Pravidlo getPravidlo(int id);
    List<Pravidlo> getPravidlaByKategorie(int idKategorie);
    int vratPocetAktivnichPravidelKategorie(int idKategorie);
    int vratPocetAktivnichPravidelKategorie(String kategorie);
    boolean existujiAktivniPravidlaKategorie(String kategorie);
    boolean existujiAktivniPravidla();
}