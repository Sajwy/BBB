package cz.sajwy.silencer.daoImpl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cz.sajwy.silencer.dao.DenDao;
import cz.sajwy.silencer.db.DBManager;
import cz.sajwy.silencer.model.Den;

public class DenDaoImpl implements DenDao {
    private static final String TABLE = "Den";
    private static final String KEY_ID_DEN = "ID_den";
    private static final String KEY_NAZEV = "Nazev";
    private static final String KEY_ZKRATKA = "Zkratka";
    private static final String[] COLUMNS = {KEY_ID_DEN, KEY_NAZEV, KEY_ZKRATKA};

    @Override
    public String createTable(){
        return "CREATE TABLE " + TABLE  +
                "(" +
                    KEY_ID_DEN  + " INTEGER PRIMARY KEY," +
                    KEY_NAZEV + " TEXT, " +
                    KEY_ZKRATKA + " TEXT " +
                ");";
    }

    @Override
    public String createIndex() {
        return "CREATE INDEX ix_nazevDne ON " + TABLE + "(" + KEY_NAZEV + ");"+
                "CREATE INDEX ix_idDne ON " + TABLE + "(" + KEY_ID_DEN + ");";
    }

    @Override
    public String insertData(){
        return  "INSERT INTO " + TABLE + " (" + KEY_NAZEV + ", " + KEY_ZKRATKA + ") VALUES " +
                    "('Pondělí', 'PO'), " +
                    "('Úterý', 'ÚT'), " +
                    "('Středa', 'ST'), " +
                    "('Čtvrtek', 'ČT'), " +
                    "('Pátek', 'PÁ'), " +
                    "('Sobota', 'SO'), " +
                    "('Neděle', 'NE'); ";
    }

    @Override
    public List<Den> getDny() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT * FROM " + TABLE + " ORDER BY " + KEY_ID_DEN;
        Cursor cursor = db.rawQuery(query, null);
        List<Den> dny = new ArrayList<>();
        while (cursor.moveToNext()) {
            Den den = new Den(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID_DEN))),
                    cursor.getString(cursor.getColumnIndex(KEY_NAZEV)),cursor.getString(cursor.getColumnIndex(KEY_ZKRATKA)));
            dny.add(den);
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return dny;
    }

    @Override
    public String[] getNazvyDnuArray() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT " + KEY_NAZEV + " FROM " + TABLE + " ORDER BY " + KEY_ID_DEN;
        Cursor cursor = db.rawQuery(query, null);
        String[] dnyArray = new String[7];
        int i = 0;
        while(cursor.moveToNext()) {
            dnyArray[i] = cursor.getString(cursor.getColumnIndex(KEY_NAZEV));
            i++;
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return dnyArray;
    }

    @Override
    public Den getDenByID(int id) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor cursor =
                db.query(TABLE, // a. table
                        COLUMNS, // b. column names
                        KEY_ID_DEN + " = ?", // c. selections
                        new String[]{String.valueOf(id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        Den den = null;
        if (cursor.moveToFirst()) {
            den = new Den(id, cursor.getString(cursor.getColumnIndex(KEY_NAZEV)),cursor.getString(cursor.getColumnIndex(KEY_ZKRATKA)));
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return den;
    }

    @Override
    public Den getDenByZkratka(String zkratka) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor cursor =
                db.query(TABLE, // a. table
                        COLUMNS, // b. column names
                        KEY_ZKRATKA + " = ?", // c. selections
                        new String[]{String.valueOf(zkratka)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        Den den = null;
        if (cursor.moveToFirst()) {
            den = new Den(cursor.getInt(cursor.getColumnIndex(KEY_ID_DEN)), cursor.getString(cursor.getColumnIndex(KEY_NAZEV)),cursor.getString(cursor.getColumnIndex(KEY_ZKRATKA)));
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return den;
    }
}