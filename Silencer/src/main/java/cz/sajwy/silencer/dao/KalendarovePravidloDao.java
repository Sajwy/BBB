package cz.sajwy.silencer.dao;

import java.util.List;

import cz.sajwy.silencer.model.KalendarovePravidlo;

public interface KalendarovePravidloDao {
    String createTable();
    String createIndex();
    List<String> getNazvyAktivnichUdalosti();
    int getVibrace(String udalost);
    KalendarovePravidlo getKalendarovePravidlo(int id);
    String getNazevPravidlaByUdalost(String udalost);
    void insertKalendarovePravidlo(KalendarovePravidlo kalendarovePravidlo);
    void updateKalendarovePravidlo(KalendarovePravidlo novePravidlo);
    void deleteKalendarovePravidlo(int id);
    boolean lzeNazevUdalostiPouzit(String nazev, int id);
}