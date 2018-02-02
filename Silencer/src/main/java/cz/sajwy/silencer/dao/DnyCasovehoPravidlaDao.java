package cz.sajwy.silencer.dao;

import java.util.ArrayList;
import java.util.List;

import cz.sajwy.silencer.model.Den;

public interface DnyCasovehoPravidlaDao {
    String createTable();
    String createIndex();
    void insert(int idPravidla, List<Den> dnyPravidla);
    void update(int idPravidla, List<Den> dnyPravidla);
    void delete(int idPravidla);
    ArrayList<Integer> getIdDnu(int idPravidla);
}