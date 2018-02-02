package cz.sajwy.silencer.daoImpl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cz.sajwy.silencer.dao.KalendarovePravidloDao;
import cz.sajwy.silencer.dao.PravidloDao;
import cz.sajwy.silencer.db.DBManager;
import cz.sajwy.silencer.model.KalendarovePravidlo;
import cz.sajwy.silencer.model.Pravidlo;

public class KalendarovePravidloDaoImpl implements KalendarovePravidloDao {
    private static final String TABLE = "KalendarovePravidlo";
    private static final String KEY_FK_PRAVIDLO = "ID_pravidlo";
    private static final String KEY_UDALOST = "Udalost";
    private static final String[] COLUMNS = {KEY_FK_PRAVIDLO, KEY_UDALOST};

    @Override
    public String createTable(){
        return "CREATE TABLE " + TABLE  +
                "(" +
                    KEY_FK_PRAVIDLO  + " INTEGER REFERENCES Pravidlo," +
                    KEY_UDALOST + " TEXT" +
                ");";
    }

    @Override
    public String createIndex() {
        return "CREATE INDEX ix_id_kp ON " + TABLE + "(" + KEY_FK_PRAVIDLO + ");"+
                "CREATE INDEX ix_udalost ON " + TABLE + "(" + KEY_UDALOST + ");";
    }

    @Override
    public List<String> getNazvyAktivnichUdalosti() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT " + KEY_UDALOST + " FROM " + TABLE + " kp JOIN Pravidlo p ON kp." + KEY_FK_PRAVIDLO + " = p.ID_pravidlo " +
                "WHERE p.Stav = 1";
        Cursor cursor = db.rawQuery(query, null);
        List<String> udalosti = new ArrayList<>();
        while (cursor.moveToNext()) {
            udalosti.add(cursor.getString(cursor.getColumnIndex(KEY_UDALOST)));
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return udalosti;
    }

    @Override
    public int getVibrace(String udalost) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT Vibrace FROM " + TABLE + " kp JOIN Pravidlo p ON kp." + KEY_FK_PRAVIDLO + " = p.ID_pravidlo " +
                        "WHERE " + KEY_UDALOST + " = '" + udalost + "'";
        Cursor cursor = db.rawQuery(query, null);
        int vibrace = -1;
        if (cursor.moveToFirst())
            vibrace = cursor.getInt(cursor.getColumnIndex("Vibrace"));
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return vibrace;
    }

    @Override
    public KalendarovePravidlo getKalendarovePravidlo(int id) {
        PravidloDao pravidloDao = new PravidloDaoImpl();
        Pravidlo pravidlo = pravidloDao.getPravidlo(id);
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor cursor = db.query(TABLE, // a. table
                COLUMNS, // b. column names
                KEY_FK_PRAVIDLO + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        KalendarovePravidlo kalendarovePravidlo = null;
        if (cursor.moveToFirst())
            kalendarovePravidlo = new KalendarovePravidlo(id,pravidlo.getNazev(),pravidlo.getStav(),
                    pravidlo.getVibrace(),pravidlo.getKategorie(),cursor.getString(cursor.getColumnIndex(KEY_UDALOST)));
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return kalendarovePravidlo;
    }

    @Override
    public String getNazevPravidlaByUdalost(String udalost) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT Nazev FROM " + TABLE + " kp JOIN Pravidlo p ON kp." + KEY_FK_PRAVIDLO + " = p.ID_pravidlo " +
                "WHERE p.Stav = 1 AND instr(lower('" + udalost + "'), lower(" + KEY_UDALOST + ")) > 0";
        Cursor cursor = db.rawQuery(query, null);
        String nazev = "";
        if(cursor.moveToFirst())
            nazev = cursor.getString(cursor.getColumnIndex("Nazev"));
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return nazev;
    }

    @Override
    public void insertKalendarovePravidlo(KalendarovePravidlo kalendarovePravidlo){
        PravidloDao pravidloDao = new PravidloDaoImpl();
        int id = pravidloDao.insertPravidlo(new Pravidlo(kalendarovePravidlo.getNazev(), kalendarovePravidlo.getStav(),
                kalendarovePravidlo.getVibrace(), kalendarovePravidlo.getKategorie()));
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "INSERT INTO " + TABLE + " (" + KEY_FK_PRAVIDLO + ", " + KEY_UDALOST + ") " +
                        "VALUES " + "(" + id + " ,'" + kalendarovePravidlo.getUdalost() + "');";
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
    }

    @Override
    public void updateKalendarovePravidlo(KalendarovePravidlo novePravidlo) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "UPDATE " + TABLE + " SET " + KEY_UDALOST + " = '" + novePravidlo.getUdalost() +
                        "' WHERE " + KEY_FK_PRAVIDLO + " = " + novePravidlo.getId_pravidlo();
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
        Pravidlo pravidlo = new Pravidlo(novePravidlo.getId_pravidlo(), novePravidlo.getNazev(), novePravidlo.getStav(),
                                            novePravidlo.getVibrace(),novePravidlo.getKategorie());
        PravidloDao pravidloDao = new PravidloDaoImpl();
        pravidloDao.updatePravidlo(pravidlo);
    }

    @Override
    public void deleteKalendarovePravidlo(int id) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "DELETE FROM " + TABLE + " WHERE " + KEY_FK_PRAVIDLO + " = " + id;
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
        PravidloDao pravidloDao = new PravidloDaoImpl();
        pravidloDao.deletePravidlo(id);
    }

    @Override
    public boolean lzeNazevUdalostiPouzit(String nazev, int id) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT * FROM " + TABLE + " WHERE " + KEY_FK_PRAVIDLO + "<>" + id + " AND lower(" + KEY_UDALOST + ") = '" + nazev.toLowerCase() + "'";
        Cursor cursor = db.rawQuery(query, null);
        boolean lze;
        if (cursor.moveToFirst())
            lze = false;
        else
            lze = true;
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return lze;
    }
}