package cz.sajwy.silencer.daoImpl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cz.sajwy.silencer.dao.CasovePravidloDao;
import cz.sajwy.silencer.dao.DenDao;
import cz.sajwy.silencer.dao.DnyCasovehoPravidlaDao;
import cz.sajwy.silencer.dao.KategorieDao;
import cz.sajwy.silencer.dao.PravidloDao;
import cz.sajwy.silencer.db.DBManager;
import cz.sajwy.silencer.model.CasovePravidlo;
import cz.sajwy.silencer.model.Den;
import cz.sajwy.silencer.model.Pravidlo;
import cz.sajwy.silencer.utils.Utils;

/**
 * Created by Sajwy on 27.05.2017.
 */
public class CasovePravidloDaoImpl implements CasovePravidloDao {
    private static final String TABLE = "CasovePravidlo";
    private static final String KEY_FK_PRAVIDLO = "ID_pravidlo";
    private static final String KEY_CAS_OD = "CasOd";
    private static final String KEY_CAS_DO = "CasDo";
    private static final String KEY_VYPIS_NAZVY = "VypisNazvy";
    private static final String KEY_VYPIS_ZKRATKY = "VypisZkratky";
    private static final String[] COLUMNS = {KEY_FK_PRAVIDLO, KEY_CAS_OD, KEY_CAS_DO, KEY_VYPIS_NAZVY, KEY_VYPIS_ZKRATKY};

    @Override
    public String createTable(){
        return "CREATE TABLE " + TABLE  +
                "(" +
                    KEY_FK_PRAVIDLO  + " INTEGER REFERENCES Pravidlo," +
                    KEY_CAS_OD + " TEXT, " +
                    KEY_CAS_DO + " TEXT, " +
                    KEY_VYPIS_NAZVY + " TEXT, " +
                    KEY_VYPIS_ZKRATKY + " TEXT " +
                ");";
    }

    @Override
    public String createIndex() {
        return "CREATE INDEX ix_id_cp ON " + TABLE + "(" + KEY_FK_PRAVIDLO + ");";
    }

    public void insertCasovePravidlo(CasovePravidlo casovePravidlo){
        PravidloDao pravidloDao = new PravidloDaoImpl();
        int id = pravidloDao.insertPravidlo(new Pravidlo(casovePravidlo.getNazev(), casovePravidlo.getStav(),
                casovePravidlo.getVibrace(), casovePravidlo.getKategorie()));
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "INSERT INTO " + TABLE + " (" + KEY_FK_PRAVIDLO + ", " + KEY_CAS_OD + ", " + KEY_CAS_DO + ", " + KEY_VYPIS_NAZVY + ", " + KEY_VYPIS_ZKRATKY + ") " +
                " VALUES " + "(" + id + " ,'" + casovePravidlo.getCas_od() + "', '" + casovePravidlo.getCas_do() + "', '" + casovePravidlo.getVypisDnuNazvy() + "', '" + casovePravidlo.getVypisDnuZkratky() + "');";
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
        DnyCasovehoPravidlaDao dnyCasovehoPravidlaDao = new DnyCasovehoPravidlaDaoImpl();
        dnyCasovehoPravidlaDao.insert(id, casovePravidlo.getDny());
    }

    @Override
    public String getVypisDnuNazvy(int id) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT " + KEY_VYPIS_NAZVY + " FROM " + TABLE + " WHERE " + KEY_FK_PRAVIDLO + " = " + id;
        Cursor cursor = db.rawQuery(query, null);
        String vypis = "";
        if (cursor.moveToFirst())
            vypis = cursor.getString(cursor.getColumnIndex(KEY_VYPIS_NAZVY));
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return vypis;
    }

    @Override
    public String getVypisDnuZkratky(int id) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT " + KEY_VYPIS_ZKRATKY + " FROM " + TABLE + " WHERE " + KEY_FK_PRAVIDLO + " = " + id;
        Cursor cursor = db.rawQuery(query, null);
        String vypis = "";
        if (cursor.moveToFirst())
            vypis = cursor.getString(cursor.getColumnIndex(KEY_VYPIS_ZKRATKY));
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return vypis;
    }

    @Override
    public void updateVypisyDnu(int id, String vypisNazvy, String vypisZkratky) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query;
        if(vypisZkratky.equals(""))
            query = "UPDATE " + TABLE + " SET " + KEY_VYPIS_NAZVY + " = '" + vypisNazvy + "'WHERE " + KEY_FK_PRAVIDLO + " = " + id;
        else
            query = "UPDATE " + TABLE + " SET " + KEY_VYPIS_NAZVY + " = '" + vypisNazvy + "', " +
                KEY_VYPIS_ZKRATKY + " = '" + vypisZkratky +
                "' WHERE " + KEY_FK_PRAVIDLO + " = " + id;
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
    }

    @Override
    public void updateCasovePravidlo(CasovePravidlo novePravidlo) {
        DnyCasovehoPravidlaDao dnyCasovehoPravidlaDao = new DnyCasovehoPravidlaDaoImpl();
        dnyCasovehoPravidlaDao.update(novePravidlo.getId_pravidlo(), novePravidlo.getDny());
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "UPDATE " + TABLE + " SET " + KEY_CAS_OD + " = '" + novePravidlo.getCas_od() + "', " +
                KEY_CAS_DO + " = '" + novePravidlo.getCas_do() +
                "' WHERE " + KEY_FK_PRAVIDLO + " = " + novePravidlo.getId_pravidlo();
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
        Pravidlo pravidlo = new Pravidlo(novePravidlo.getId_pravidlo(), novePravidlo.getNazev(), novePravidlo.getStav(),
                novePravidlo.getVibrace(),novePravidlo.getKategorie());
        PravidloDao pravidloDao = new PravidloDaoImpl();
        pravidloDao.updatePravidlo(pravidlo);
        updateVypisyDnu(novePravidlo.getId_pravidlo(), novePravidlo.getVypisDnuNazvy(), novePravidlo.getVypisDnuZkratky());
    }

    @Override
    public void deleteCasovePravidlo(int id) {
        DnyCasovehoPravidlaDao dnyCasovehoPravidlaDao = new DnyCasovehoPravidlaDaoImpl();
        dnyCasovehoPravidlaDao.delete(id);
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "DELETE FROM " + TABLE + " WHERE " + KEY_FK_PRAVIDLO + " = " + id;
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
        PravidloDao pravidloDao = new PravidloDaoImpl();
        pravidloDao.deletePravidlo(id);
    }

    @Override
    public CasovePravidlo getCasovePravidlo(int id) {
        PravidloDao pravidloDao = new PravidloDaoImpl();
        Pravidlo pravidlo = pravidloDao.getPravidlo(id);
        DnyCasovehoPravidlaDao dnyCasovehoPravidlaDao = new DnyCasovehoPravidlaDaoImpl();
        List<Den> dny = getDnyCasovehoPravidla(dnyCasovehoPravidlaDao.getIdDnu(id));
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor cursor = db.query(TABLE, // a. table
                COLUMNS, // b. column names
                KEY_FK_PRAVIDLO + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        CasovePravidlo casovePravidlo = null;
        if (cursor.moveToFirst())
            casovePravidlo = new CasovePravidlo(id,pravidlo.getNazev(),pravidlo.getStav(),
                pravidlo.getVibrace(),pravidlo.getKategorie(),dny,cursor.getString(cursor.getColumnIndex(KEY_CAS_OD)),
                    cursor.getString(cursor.getColumnIndex(KEY_CAS_DO)),cursor.getString(cursor.getColumnIndex(KEY_VYPIS_NAZVY)),cursor.getString(cursor.getColumnIndex(KEY_VYPIS_ZKRATKY)));
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return casovePravidlo;
    }

    @Override
    public List<Den> getDnyCasovehoPravidla(List<Integer> idDnu) {
        DenDao denDao = new DenDaoImpl();
        List<Den> dnyPravidla = new ArrayList<>();
        Den den;
        for(int i = 0;i < idDnu.size();i++) {
            den = denDao.getDenByID(idDnu.get(i));
            dnyPravidla.add(den);
        }
        return dnyPravidla;
    }

    @Override
    public ArrayList<Integer> getIntListDnuCasovehoPravidla(int idPravidla) {
        DnyCasovehoPravidlaDao dnyCasovehoPravidlaDao = new DnyCasovehoPravidlaDaoImpl();
        ArrayList<Integer> intList = dnyCasovehoPravidlaDao.getIdDnu(idPravidla);
        for(int i = 0;i < intList.size();i++)
            intList.set(i, intList.get(i) - 1);
        return intList;
    }

    @Override
    public List<CasovePravidlo> getCasovaPravidlaByDenAktualni(Den den) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT * FROM " + TABLE + " cp JOIN Pravidlo p ON cp." + KEY_FK_PRAVIDLO + " = p." + KEY_FK_PRAVIDLO +
                " JOIN DnyCasovehoPravidla dcp ON p." + KEY_FK_PRAVIDLO + " = dcp." + KEY_FK_PRAVIDLO +
                " WHERE dcp.ID_den = " + den.getId_den() + " AND p.Stav = 1" +
                " ORDER BY cp." + KEY_CAS_OD + ", " + KEY_CAS_DO + " DESC";
        Cursor cursor = db.rawQuery(query, null);
        DnyCasovehoPravidlaDao dnyCasovehoPravidlaDao = new DnyCasovehoPravidlaDaoImpl();
        KategorieDao kategorieDao = new KategorieDaoImpl();
        List<CasovePravidlo> pravidla = new ArrayList<>();
        CasovePravidlo casovePravidlo;
        long[] casoveUdaje;
        while (cursor.moveToNext()) {
            casoveUdaje = Utils.prevedTimeStringyNaMilisekundy(cursor.getString(cursor.getColumnIndex(KEY_CAS_OD)), cursor.getString(cursor.getColumnIndex(KEY_CAS_DO)), false);
            casovePravidlo = new CasovePravidlo(cursor.getInt(cursor.getColumnIndex("ID_pravidlo")),cursor.getString(cursor.getColumnIndex("Nazev")),
                    cursor.getInt(cursor.getColumnIndex("Stav")),cursor.getInt(cursor.getColumnIndex("Vibrace")),
                    kategorieDao.getKategorieByID(cursor.getInt(cursor.getColumnIndex("FK_kategorie"))),
                    getDnyCasovehoPravidla(dnyCasovehoPravidlaDao.getIdDnu(cursor.getInt(cursor.getColumnIndex("ID_pravidlo")))),
                    casoveUdaje[0], casoveUdaje[1]);
            pravidla.add(casovePravidlo);
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return pravidla;
    }

    @Override
    public List<CasovePravidlo> getCasovaPravidlaByDenPredchozi(Den aktualniDen) {
        int idPredchozi = aktualniDen.getId_den() - 1;
        if(idPredchozi == 0)
            idPredchozi = 7;
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT * FROM Pravidlo p JOIN " + TABLE + " cp ON p." + KEY_FK_PRAVIDLO + " = cp." + KEY_FK_PRAVIDLO +
                " JOIN DnyCasovehoPravidla dcp ON cp." + KEY_FK_PRAVIDLO + " = dcp." + KEY_FK_PRAVIDLO +
                " WHERE dcp.ID_den = " + idPredchozi + " AND p.Stav = 1" +
                " ORDER BY cp." + KEY_CAS_OD + ", cp." + KEY_CAS_DO + " DESC";
        Cursor cursor = db.rawQuery(query, null);
        DnyCasovehoPravidlaDao dnyCasovehoPravidlaDao = new DnyCasovehoPravidlaDaoImpl();
        KategorieDao kategorieDao = new KategorieDaoImpl();
        List<CasovePravidlo> pravidla = new ArrayList<>();
        CasovePravidlo casovePravidlo;
        long[] casoveUdaje;
        while (cursor.moveToNext()) {
            casoveUdaje = Utils.prevedTimeStringyNaMilisekundy(cursor.getString(cursor.getColumnIndex(KEY_CAS_OD)), cursor.getString(cursor.getColumnIndex(KEY_CAS_DO)), true);
            casovePravidlo = new CasovePravidlo(cursor.getInt(cursor.getColumnIndex("ID_pravidlo")),cursor.getString(cursor.getColumnIndex("Nazev")),
                    cursor.getInt(cursor.getColumnIndex("Stav")),cursor.getInt(cursor.getColumnIndex("Vibrace")),
                    kategorieDao.getKategorieByID(cursor.getInt(cursor.getColumnIndex("FK_kategorie"))),
                    getDnyCasovehoPravidla(dnyCasovehoPravidlaDao.getIdDnu(cursor.getInt(cursor.getColumnIndex("ID_pravidlo")))),
                    casoveUdaje[0], casoveUdaje[1]);
            pravidla.add(casovePravidlo);
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return pravidla;
    }
}
