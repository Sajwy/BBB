package cz.sajwy.silencer.daoImpl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cz.sajwy.silencer.dao.KategorieDao;
import cz.sajwy.silencer.dao.PravidloDao;
import cz.sajwy.silencer.db.DBManager;
import cz.sajwy.silencer.model.Kategorie;

public class KategorieDaoImpl implements KategorieDao {
    private static final String TABLE = "Kategorie";
    private static final String KEY_ID_KATEGORIE = "ID_kategorie";
    private static final String KEY_NAZEV = "Nazev";
    private static final String[] COLUMNS = {KEY_ID_KATEGORIE,KEY_NAZEV};

    @Override
    public String createTable(){
        return "CREATE TABLE " + TABLE  +
                "(" +
                KEY_ID_KATEGORIE  + " INTEGER PRIMARY KEY," +
                KEY_NAZEV + " TEXT " +
                ");";
    }

    @Override
    public String createIndex() {
        return "CREATE INDEX ix_id_kategorie ON " + TABLE + "(" + KEY_ID_KATEGORIE + ");"+
                "CREATE INDEX ix_nazev_kategorie ON " + TABLE + "(" + KEY_NAZEV + ");";
    }

    @Override
    public String insertData(){
        return  "INSERT INTO " + TABLE + " (" + KEY_NAZEV + ") VALUES " +
                "('Časové pravidlo'), " +
                "('Kalendářové pravidlo'), " +
                "('Wifi pravidlo');";
    }

    @Override
    public Kategorie getKategorieByID(int id) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor cursor =
                db.query(TABLE, // a. table
                        COLUMNS, // b. column names
                        KEY_ID_KATEGORIE + " = ?", // c. selections
                        new String[]{String.valueOf(id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        Kategorie kategorie = null;
        if (cursor.moveToFirst()) {
            kategorie = new Kategorie(id, cursor.getString(cursor.getColumnIndex(KEY_NAZEV)));
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return kategorie;
    }

    @Override
    public Kategorie getKategorieByIDPravidla(int id) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT FK_kategorie FROM Pravidlo WHERE ID_pravidlo = " + id;
        Cursor cursor = db.rawQuery(query, null);
        int idKategorie = -1;
        if (cursor.moveToFirst()) {
            idKategorie = cursor.getInt(cursor.getColumnIndex("FK_kategorie"));
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return getKategorieByID(idKategorie);
    }

    @Override
    public Kategorie getKategorieByNazev(String nazev) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor cursor =
                db.query(TABLE, // a. table
                        COLUMNS, // b. column names
                        KEY_NAZEV + " = ?", // c. selections
                        new String[]{String.valueOf(nazev)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        Kategorie kategorie = null;
        if (cursor.moveToFirst()) {
            kategorie = new Kategorie(cursor.getInt(cursor.getColumnIndex(KEY_ID_KATEGORIE)), cursor.getString(cursor.getColumnIndex(KEY_NAZEV)));
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return kategorie;
    }

    @Override
    public List<Kategorie> getAllKategorie() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT * FROM " + TABLE;
        Cursor cursor = db.rawQuery(query, null);
        List<Kategorie> kategorie = new ArrayList<>();
        while(cursor.moveToNext()) {
            Kategorie k = new Kategorie(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID_KATEGORIE))),
                    cursor.getString(cursor.getColumnIndex(KEY_NAZEV)));
            kategorie.add(k);
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return kategorie;
    }

    @Override
    public List<String> getAllKategorieNazvy() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT " + KEY_NAZEV + " FROM " + TABLE;
        Cursor cursor = db.rawQuery(query, null);
        List<String> kategorieNazvy = new ArrayList<>();
        while (cursor.moveToNext()) {
            kategorieNazvy.add(cursor.getString(cursor.getColumnIndex(KEY_NAZEV)));
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return kategorieNazvy;
    }

    @Override
    public List<Kategorie> getKategorieSPravidly() {
        PravidloDao pravidloDao = new PravidloDaoImpl();
        List<Kategorie> kategorie = getAllKategorie();
        for(int i = 0;i < kategorie.size();i++) {
            Kategorie k = kategorie.get(i);
            k.setPravidla(pravidloDao.getPravidlaByKategorie(k.getId_kategorie()));
        }
        return kategorie;
    }

    @Override
    public int getIdKategorieByNazev(String nazevKategorie) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT " + KEY_ID_KATEGORIE + " FROM " + TABLE + " WHERE " + KEY_NAZEV + " = '" + nazevKategorie + "'";
        Cursor cursor = db.rawQuery(query, null);
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(KEY_ID_KATEGORIE));
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return id;
    }
}