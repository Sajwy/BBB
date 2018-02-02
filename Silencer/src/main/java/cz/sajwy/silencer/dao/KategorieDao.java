package cz.sajwy.silencer.dao;

import java.util.List;

import cz.sajwy.silencer.model.Kategorie;

public interface KategorieDao {
    String createTable();
    String createIndex();
    String insertData();
    Kategorie getKategorieByID(int id);
    Kategorie getKategorieByIDPravidla(int id);
    Kategorie getKategorieByNazev(String nazev);
    List<Kategorie> getAllKategorie();
    List<String> getAllKategorieNazvy();
    List<Kategorie> getKategorieSPravidly();
    int getIdKategorieByNazev(String nazevKategorie);
}