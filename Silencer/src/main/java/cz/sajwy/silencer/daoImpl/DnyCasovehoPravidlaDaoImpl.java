package cz.sajwy.silencer.daoImpl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cz.sajwy.silencer.dao.DnyCasovehoPravidlaDao;
import cz.sajwy.silencer.db.DBManager;
import cz.sajwy.silencer.model.Den;

public class DnyCasovehoPravidlaDaoImpl implements DnyCasovehoPravidlaDao {
    private static final String TABLE = "DnyCasovehoPravidla";
    private static final String KEY_FK_PRAVIDLO = "ID_pravidlo";
    private static final String KEY_FK_DEN = "ID_den";
    private static final String[] COLUMNS = {KEY_FK_PRAVIDLO, KEY_FK_DEN};

    @Override
    public String createTable(){
        return "CREATE TABLE " + TABLE  +
                "(" +
                    KEY_FK_PRAVIDLO  + " INTEGER REFERENCES CasovePravidlo," +
                    KEY_FK_DEN + " INTEGER REFERENCES Den " +
                ");";
    }

    @Override
    public String createIndex() {
        return "CREATE INDEX ix_id_den ON " + TABLE + "(" + KEY_FK_DEN + ");";
    }

    @Override
    public void insert(int idPravidla, List<Den> dnyPravidla) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query;
        for(int i = 0;i < dnyPravidla.size();i++) {
            query = "INSERT INTO " + TABLE + " (" + KEY_FK_PRAVIDLO + ", " + KEY_FK_DEN + ") " +
                    "VALUES " + "(" + idPravidla + " ,'" + dnyPravidla.get(i).getId_den() + "');";
            db.execSQL(query);
        }
        DBManager.getInstance().closeDatabase();
    }

    @Override
    public void update(int idPravidla, List<Den> dnyPravidla) {
        delete(idPravidla);
        insert(idPravidla, dnyPravidla);
    }

    @Override
    public void delete(int idPravidla) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "DELETE FROM " + TABLE + " WHERE " + KEY_FK_PRAVIDLO + " = " + idPravidla;
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
    }

    @Override
    public ArrayList<Integer> getIdDnu(int idPravidla) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT " + KEY_FK_DEN + " FROM " + TABLE +
                        " WHERE " + KEY_FK_PRAVIDLO + " = " + idPravidla +
                        " ORDER BY " + KEY_FK_DEN;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Integer> idDnu = new ArrayList<>();
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(KEY_FK_DEN));
            idDnu.add(id);
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return idDnu;
    }
}