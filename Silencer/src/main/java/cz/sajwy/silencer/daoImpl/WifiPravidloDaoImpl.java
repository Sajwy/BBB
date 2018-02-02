package cz.sajwy.silencer.daoImpl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cz.sajwy.silencer.dao.PravidloDao;
import cz.sajwy.silencer.dao.WifiPravidloDao;
import cz.sajwy.silencer.db.DBManager;
import cz.sajwy.silencer.model.Pravidlo;
import cz.sajwy.silencer.model.WifiPravidlo;

public class WifiPravidloDaoImpl implements WifiPravidloDao {
    private static final String TABLE = "WifiPravidlo";
    private static final String KEY_FK_PRAVIDLO = "ID_pravidlo";
    private static final String KEY_WIFI = "Wifi";
    private static final String[] COLUMNS = {KEY_FK_PRAVIDLO, KEY_WIFI};

    @Override
    public String createTable(){
        return "CREATE TABLE " + TABLE  +
                "(" +
                    KEY_FK_PRAVIDLO  + " INTEGER REFERENCES Pravidlo," +
                    KEY_WIFI + " TEXT " +
                ");";
    }

    @Override
    public String createIndex() {
        return "CREATE INDEX ix_id_wp ON " + TABLE + "(" + KEY_FK_PRAVIDLO + ");"+
                "CREATE INDEX ix_wifi ON " + TABLE + "(" + KEY_WIFI + ");";
    }

    @Override
    public WifiPravidlo getWifiPravidlo(int id) {
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
        WifiPravidlo wifiPravidlo = null;
        if (cursor.moveToFirst())
            wifiPravidlo = new WifiPravidlo(id,pravidlo.getNazev(),pravidlo.getStav(),
                pravidlo.getVibrace(),pravidlo.getKategorie(),cursor.getString(cursor.getColumnIndex(KEY_WIFI)));
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return wifiPravidlo;
    }

    @Override
    public WifiPravidlo getAktivniWifiPravidloByWifi(String wifi) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT wp.* FROM " + TABLE + " wp JOIN Pravidlo p ON wp." + KEY_FK_PRAVIDLO +
                        " = p." + KEY_FK_PRAVIDLO +
                        " WHERE p.Stav = 1 AND wp." + KEY_WIFI + " = '" + wifi + "'";
        Cursor cursor = db.rawQuery(query, null);
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(KEY_FK_PRAVIDLO));
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        if(id != -1)
            return getWifiPravidlo(id);
        else
            return null;
    }

    @Override
    public boolean obsazenoVAktivnich(String ssid) {
        if(getWifiNazvy().contains(ssid))
            return true;
        else
            return false;
    }

    @Override
    public void insertWifiPravidlo(WifiPravidlo wifiPravidlo){
        PravidloDao pravidloDao = new PravidloDaoImpl();
        int id = pravidloDao.insertPravidlo(new Pravidlo(wifiPravidlo.getNazev(), wifiPravidlo.getStav(),
                wifiPravidlo.getVibrace(), wifiPravidlo.getKategorie()));
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "INSERT INTO " + TABLE + " (" + KEY_FK_PRAVIDLO + ", " + KEY_WIFI + ") " +
                "VALUES " + "(" + id + " ,'" + wifiPravidlo.getNazev_wifi() + "');";
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
    }

    @Override
    public void updateWifiPravidlo(WifiPravidlo novePravidlo) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "UPDATE " + TABLE + " SET " + KEY_WIFI + " = '" + novePravidlo.getNazev_wifi() +
                "' WHERE " + KEY_FK_PRAVIDLO + " = " + novePravidlo.getId_pravidlo();
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
        Pravidlo pravidlo = new Pravidlo(novePravidlo.getId_pravidlo(), novePravidlo.getNazev(), novePravidlo.getStav(),
                novePravidlo.getVibrace(),novePravidlo.getKategorie());
        PravidloDao pravidloDao = new PravidloDaoImpl();
        pravidloDao.updatePravidlo(pravidlo);
    }

    @Override
    public void deleteWifiPravidlo(int id) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "DELETE FROM " + TABLE + " WHERE " + KEY_FK_PRAVIDLO + " = " + id;
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
        PravidloDao pravidloDao = new PravidloDaoImpl();
        pravidloDao.deletePravidlo(id);
    }

    @Override
    public List<String> getWifiNazvy() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT " + KEY_WIFI + " FROM " + TABLE + " wp JOIN Pravidlo p ON wp." + KEY_FK_PRAVIDLO +
                                " = p." + KEY_FK_PRAVIDLO +
                        " WHERE p.Stav = 1" +
                        " ORDER BY " + KEY_WIFI;
        Cursor cursor = db.rawQuery(query, null);
        List<String> wifiNazvy = new ArrayList<>();
        while (cursor.moveToNext()) {
            wifiNazvy.add(cursor.getString(cursor.getColumnIndex(KEY_WIFI)));
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return wifiNazvy;
    }

    @Override
    public boolean lzeNazevWifiPouzit(String nazev, int id) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT * FROM " + TABLE + " WHERE " + KEY_FK_PRAVIDLO + "<>" + id + " AND lower(" + KEY_WIFI + ") = '" + nazev.toLowerCase() + "'";
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