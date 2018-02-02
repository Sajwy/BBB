package cz.sajwy.silencer.dao;

import java.util.List;

import cz.sajwy.silencer.model.Den;

public interface DenDao {
    String createTable();
    String createIndex();
    String insertData();
    List<Den> getDny();
    String[] getNazvyDnuArray();
    Den getDenByID(int id);
    Den getDenByZkratka(String zkratka);
}