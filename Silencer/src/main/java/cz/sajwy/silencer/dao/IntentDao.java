package cz.sajwy.silencer.dao;

import java.util.List;

public interface IntentDao {
    String createTable();
    void insert(int id);
    void delete(int id);
    void deleteAllCpIntents();
    List<Integer> getAllCpIntents();

}