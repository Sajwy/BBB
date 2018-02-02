package cz.sajwy.silencer.daoImpl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cz.sajwy.silencer.dao.KonfiguraceDao;
import cz.sajwy.silencer.db.DBManager;

/**
 * Created by Sajwy on 04.09.2017.
 */
public class KonfiguraceDaoImpl implements KonfiguraceDao {
    private static final String TABLE = "Konfigurace";
    private static final String KEY_OBSLUHA_PRAVIDEL = "Obsluha";
    private static final String KEY_VYKONAVA_SE_PRAVIDLO = "VykonavaSePravidlo";
    private static final String KEY_CASOVE_NEBO_KALENDAROVE_PRAVIDLO = "CasoveKalendarove";
    private static final String KEY_WIFI_PRAVIDLO = "Wifi";
    private static final String KEY_UDALOST = "Udalost";
    private static final String KEY_DOBA_OBNOVY = "DobaObnovy";
    private static final String KEY_ZMENA_APLIKACI = "ZmenaRezimuAplikaci";
    private static final String[] COLUMNS = {KEY_OBSLUHA_PRAVIDEL, KEY_VYKONAVA_SE_PRAVIDLO, KEY_CASOVE_NEBO_KALENDAROVE_PRAVIDLO,
                                            KEY_WIFI_PRAVIDLO, KEY_UDALOST, KEY_DOBA_OBNOVY, KEY_ZMENA_APLIKACI};

    @Override
    public String createTable(){
        return "CREATE TABLE " + TABLE  +
                "(" +
                    KEY_OBSLUHA_PRAVIDEL + " INTEGER," +
                    KEY_VYKONAVA_SE_PRAVIDLO + " INTEGER," +
                    KEY_CASOVE_NEBO_KALENDAROVE_PRAVIDLO + " INTEGER," +
                    KEY_WIFI_PRAVIDLO + " INTEGER," +
                    KEY_UDALOST + " TEXT, " +
                    KEY_DOBA_OBNOVY + " INTEGER, " +
                    KEY_ZMENA_APLIKACI + " INTEGER " +
                ");";
    }

    @Override
    public String insertData(){
        return "INSERT INTO " + TABLE + " (" +
                        KEY_OBSLUHA_PRAVIDEL + "," +
                        KEY_VYKONAVA_SE_PRAVIDLO + "," +
                        KEY_CASOVE_NEBO_KALENDAROVE_PRAVIDLO + "," +
                        KEY_WIFI_PRAVIDLO + "," +
                        KEY_UDALOST + ", " +
                        KEY_DOBA_OBNOVY + ", " +
                        KEY_ZMENA_APLIKACI +
                ") VALUES (0,0,0,0,'',0,0);";
    }

    @Override
    public void updateObsluhaPravidel(int obsluha) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "UPDATE " + TABLE + " SET " + KEY_OBSLUHA_PRAVIDEL + " = " + obsluha;
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
        if(obsluha == 0) {
            updateVykonavaSePravidlo(0);
            updateDobaObnovy(0);
            updateZmenaRezimuAplikaci(0);
        }
    }

    @Override
    public int getObsluhaPravidel() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT " + KEY_OBSLUHA_PRAVIDEL + " FROM " + TABLE;
        Cursor cursor = db.rawQuery(query, null);
        int obsluha = -1;
        if (cursor.moveToFirst()) {
            obsluha = cursor.getInt(cursor.getColumnIndex(KEY_OBSLUHA_PRAVIDEL));
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return obsluha;
    }

    @Override
    public void updateVykonavaSePravidlo(int vykonavaSePravidlo) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "UPDATE " + TABLE + " SET " + KEY_VYKONAVA_SE_PRAVIDLO + " = " + vykonavaSePravidlo;
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
        if(vykonavaSePravidlo == 0) {
            updateCasoveNeboKalendarovePravidlo(0);
            updateWifiPravidlo(0);
            updateNazevObsluhovaneUdalosti("");
        }
    }

    @Override
    public int getVykonavaSePravidlo() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT " + KEY_VYKONAVA_SE_PRAVIDLO + " FROM " + TABLE;
        Cursor cursor = db.rawQuery(query, null);
        int vykonavaSe = -1;
        if (cursor.moveToFirst())
            vykonavaSe = cursor.getInt(cursor.getColumnIndex(KEY_VYKONAVA_SE_PRAVIDLO));
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return vykonavaSe;
    }

    @Override
    public void updateCasoveNeboKalendarovePravidlo(int hodnota) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "UPDATE " + TABLE + " SET " + KEY_CASOVE_NEBO_KALENDAROVE_PRAVIDLO + " = " + hodnota;
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
        if(hodnota == 1) {
            updateWifiPravidlo(0);
        } else {
            updateNazevObsluhovaneUdalosti("");
        }
    }

    @Override
    public int getCasoveNeboKalendarovePravidlo() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT " + KEY_CASOVE_NEBO_KALENDAROVE_PRAVIDLO + " FROM " + TABLE;
        Cursor cursor = db.rawQuery(query, null);
        int vykonavaSe = -1;
        if (cursor.moveToFirst())
            vykonavaSe = cursor.getInt(cursor.getColumnIndex(KEY_CASOVE_NEBO_KALENDAROVE_PRAVIDLO));
        cursor.close();
        DBManager.getInstance().closeDatabase();

        return vykonavaSe;
    }

    @Override
    public void updateWifiPravidlo(int hodnota) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "UPDATE " + TABLE + " SET " + KEY_WIFI_PRAVIDLO + " = " + hodnota;
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
        if(hodnota == 1) {
            updateCasoveNeboKalendarovePravidlo(0);
            updateNazevObsluhovaneUdalosti("");
        }
    }

    @Override
    public int getWifiPravidlo() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT " + KEY_WIFI_PRAVIDLO + " FROM " + TABLE;
        Cursor cursor = db.rawQuery(query, null);
        int vykonavaSe = -1;
        if (cursor.moveToFirst())
            vykonavaSe = cursor.getInt(cursor.getColumnIndex(KEY_WIFI_PRAVIDLO));
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return vykonavaSe;
    }

    @Override
    public void updateNazevObsluhovaneUdalosti(String hodnota) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query;
        if(hodnota.equals(""))
            query = "UPDATE " + TABLE + " SET " + KEY_UDALOST + " = ''";
        else
            query = "UPDATE " + TABLE + " SET " + KEY_UDALOST + " = '" + hodnota + "'";
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
    }

    @Override
    public String getNazevObsluhovaneUdalosti() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT " + KEY_UDALOST + " FROM " + TABLE;
        Cursor cursor = db.rawQuery(query, null);
        String udalost = "";
        if (cursor.moveToFirst())
            udalost = cursor.getString(cursor.getColumnIndex(KEY_UDALOST));
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return udalost;
    }

    @Override
    public void updateZmenaRezimuAplikaci(int hodnota) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "UPDATE " + TABLE + " SET " + KEY_ZMENA_APLIKACI + " = " + hodnota;
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
    }

    @Override
    public int getZmenaRezimuAplikaci() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT " + KEY_ZMENA_APLIKACI + " FROM " + TABLE;
        Cursor cursor = db.rawQuery(query, null);
        int hodnota = -1;
        if (cursor.moveToFirst())
            hodnota = cursor.getInt(cursor.getColumnIndex(KEY_ZMENA_APLIKACI));
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return hodnota;
    }

    @Override
    public void updateDobaObnovy(long hodnota) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "UPDATE " + TABLE + " SET " + KEY_DOBA_OBNOVY + " = " + hodnota;
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
    }

    @Override
    public long getDobaObnovy() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT " + KEY_DOBA_OBNOVY + " FROM " + TABLE;
        Cursor cursor = db.rawQuery(query, null);
        long hodnota = -1;
        if (cursor.moveToFirst())
            hodnota = cursor.getLong(cursor.getColumnIndex(KEY_DOBA_OBNOVY));
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return hodnota;
    }
}