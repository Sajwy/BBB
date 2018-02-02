package cz.sajwy.silencer.daoImpl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cz.sajwy.silencer.dao.KategorieDao;
import cz.sajwy.silencer.dao.PravidloDao;
import cz.sajwy.silencer.db.DBManager;
import cz.sajwy.silencer.model.Pravidlo;

public class PravidloDaoImpl implements PravidloDao {
    private static final String TABLE = "Pravidlo";
    private static final String KEY_ID_PRAVIDLO = "ID_pravidlo";
    private static final String KEY_NAZEV = "Nazev";
    private static final String KEY_STAV = "Stav";
    private static final String KEY_VIBRACE = "Vibrace";
    private static final String KEY_FK_KATEGORIE = "FK_kategorie";
    private static final String[] COLUMNS = {KEY_ID_PRAVIDLO, KEY_NAZEV, KEY_STAV, KEY_VIBRACE, KEY_FK_KATEGORIE};

    @Override
    public String createTable(){
        return "CREATE TABLE " + TABLE  +
                "(" +
                    KEY_ID_PRAVIDLO  + " INTEGER PRIMARY KEY," +
                    KEY_NAZEV + " TEXT, " +
                    KEY_STAV + " INTEGER, " +
                    KEY_VIBRACE + " INTEGER, " +
                    KEY_FK_KATEGORIE + " INTEGER REFERENCES Kategorie" +
                ");";
    }

    @Override
    public String createIndex() {
        return "CREATE INDEX ix_id_pravidlo ON " + TABLE + "(" + KEY_ID_PRAVIDLO + ");"+
                "CREATE INDEX ix_nazev_pravidla ON " + TABLE + "(" + KEY_NAZEV + ");";
    }

    @Override
    public int insertPravidlo(Pravidlo pravidlo) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "INSERT INTO " + TABLE + "(" + KEY_NAZEV + ", " + KEY_STAV + ", " + KEY_VIBRACE + ", " + KEY_FK_KATEGORIE + ") " +
                        "VALUES('" + pravidlo.getNazev() + "'," + pravidlo.getStav() + "," +
                                    pravidlo.getVibrace() + "," + pravidlo.getKategorie().getId_kategorie() + ");";
        db.execSQL(query);
        String selectID = "select last_insert_rowid();";
        Cursor cursor = db.rawQuery(selectID, null);
        int id = -1;
        if (cursor.moveToFirst())
            id = cursor.getInt(0);
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return id;
    }

    @Override
    public void updatePravidlo(Pravidlo novePravidlo) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "UPDATE " + TABLE + " SET " + KEY_NAZEV + " = '" + novePravidlo.getNazev() + "', " +
                                                    KEY_STAV + " = " + novePravidlo.getStav() + ", " +
                                                    KEY_VIBRACE + " = " + novePravidlo.getVibrace() + ", " +
                                                    KEY_FK_KATEGORIE + " = " + novePravidlo.getKategorie().getId_kategorie() +
                        " WHERE " + KEY_ID_PRAVIDLO + " = " + novePravidlo.getId_pravidlo();
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
    }

    @Override
    public void updateStavPravidla(int id, int novyStav) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "UPDATE " + TABLE + " SET " + KEY_STAV + " = " + novyStav +
                " WHERE " + KEY_ID_PRAVIDLO + " = " + id;
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
    }

    @Override
    public void deletePravidlo(int id) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "DELETE FROM " + TABLE + " WHERE " + KEY_ID_PRAVIDLO + " = " + id;
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
    }

    @Override
    public Pravidlo getPravidlo(int id) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor cursor = db.query(TABLE, // a. table
                COLUMNS, // b. column names
                KEY_ID_PRAVIDLO + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        Pravidlo pravidlo = null;
        if (cursor.moveToFirst()) {
            KategorieDao kategorieDao = new KategorieDaoImpl();
            pravidlo = new Pravidlo(id, cursor.getString(cursor.getColumnIndex(KEY_NAZEV)),
                    cursor.getInt(cursor.getColumnIndex(KEY_STAV)),
                    cursor.getInt(cursor.getColumnIndex(KEY_VIBRACE)),
                    kategorieDao.getKategorieByID(cursor.getInt(cursor.getColumnIndex(KEY_FK_KATEGORIE))));
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return pravidlo;
    }

    @Override
    public List<Pravidlo> getPravidlaByKategorie(int idKategorie) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT * FROM " + TABLE + " WHERE " + KEY_FK_KATEGORIE + " = " + idKategorie + " ORDER BY " + KEY_STAV + " DESC, " + KEY_NAZEV + " COLLATE LOCALIZED";
        Cursor cursor = db.rawQuery(query, null);
        List<Pravidlo> pravidla = new ArrayList<>();
        KategorieDao kategorieDao = new KategorieDaoImpl();
        while(cursor.moveToNext()) {
            Pravidlo pravidlo = new Pravidlo(cursor.getInt(cursor.getColumnIndex(KEY_ID_PRAVIDLO)),
                    cursor.getString(cursor.getColumnIndex(KEY_NAZEV)),
                    cursor.getInt(cursor.getColumnIndex(KEY_STAV)),
                    cursor.getInt(cursor.getColumnIndex(KEY_VIBRACE)),
                    kategorieDao.getKategorieByID(cursor.getInt(cursor.getColumnIndex(KEY_FK_KATEGORIE))));
            pravidla.add(pravidlo);
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return pravidla;
    }

    @Override
    public int vratPocetAktivnichPravidelKategorie(int idKategorie) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT COUNT(*) AS Pocet FROM " + TABLE + " WHERE " + KEY_FK_KATEGORIE + " = " + idKategorie + " AND " + KEY_STAV + " = 1 ";
        Cursor cursor = db.rawQuery(query, null);
        int pocet = 0;
        if (cursor.moveToFirst()) {
            pocet = cursor.getInt(cursor.getColumnIndex("Pocet"));
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return pocet;
    }

    @Override
    public int vratPocetAktivnichPravidelKategorie(String kategorie) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        KategorieDao kategorieDao = new KategorieDaoImpl();
        String query = "SELECT COUNT(*) AS Pocet FROM " + TABLE + " WHERE " + KEY_FK_KATEGORIE + " = " + kategorieDao.getIdKategorieByNazev(kategorie) + " AND " + KEY_STAV + " = 1 ";
        Cursor cursor = db.rawQuery(query, null);
        int pocet = 0;
        if (cursor.moveToFirst()) {
            pocet = cursor.getInt(cursor.getColumnIndex("Pocet"));
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return pocet;
    }

    @Override
    public boolean existujiAktivniPravidlaKategorie(String kategorie) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        KategorieDao kategorieDao = new KategorieDaoImpl();
        String query = "SELECT COUNT(*) AS Pocet FROM " + TABLE + " WHERE " + KEY_FK_KATEGORIE + " = " + kategorieDao.getIdKategorieByNazev(kategorie) + " AND " + KEY_STAV + " = 1";
        Cursor cursor = db.rawQuery(query, null);
        int pocet = 0;
        if (cursor.moveToFirst()) {
            pocet = cursor.getInt(cursor.getColumnIndex("Pocet"));
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        if(pocet == 0)
            return false;
        else
            return true;
    }

    @Override
    public boolean existujiAktivniPravidla() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT COUNT(*) AS Pocet FROM " + TABLE + " WHERE " + KEY_STAV + " = 1";
        Cursor cursor = db.rawQuery(query, null);
        int pocet = 0;
        if (cursor.moveToFirst()) {
            pocet = cursor.getInt(cursor.getColumnIndex("Pocet"));
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        if(pocet == 0)
            return false;
        else
            return true;
    }
}