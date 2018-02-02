package cz.sajwy.silencer.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cz.sajwy.silencer.app.App;
import cz.sajwy.silencer.dao.CasovePravidloDao;
import cz.sajwy.silencer.dao.DenDao;
import cz.sajwy.silencer.dao.DnyCasovehoPravidlaDao;
import cz.sajwy.silencer.dao.IntentDao;
import cz.sajwy.silencer.dao.KalendarovePravidloDao;
import cz.sajwy.silencer.dao.KategorieDao;
import cz.sajwy.silencer.dao.KonfiguraceDao;
import cz.sajwy.silencer.dao.PravidloDao;
import cz.sajwy.silencer.dao.WifiPravidloDao;
import cz.sajwy.silencer.daoImpl.CasovePravidloDaoImpl;
import cz.sajwy.silencer.daoImpl.DenDaoImpl;
import cz.sajwy.silencer.daoImpl.DnyCasovehoPravidlaDaoImpl;
import cz.sajwy.silencer.daoImpl.IntentDaoImpl;
import cz.sajwy.silencer.daoImpl.KalendarovePravidloDaoImpl;
import cz.sajwy.silencer.daoImpl.KategorieDaoImpl;
import cz.sajwy.silencer.daoImpl.KonfiguraceDaoImpl;
import cz.sajwy.silencer.daoImpl.PravidloDaoImpl;
import cz.sajwy.silencer.daoImpl.WifiPravidloDaoImpl;
import cz.sajwy.silencer.model.CasovePravidlo;
import cz.sajwy.silencer.model.Den;
import cz.sajwy.silencer.model.KalendarovePravidlo;
import cz.sajwy.silencer.model.Kategorie;
import cz.sajwy.silencer.model.Pravidlo;
import cz.sajwy.silencer.model.WifiPravidlo;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Silencer";
    private static final int DATABASE_VERSION = 1;

    public DBHelper() {
        super(App.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DenDao denDao = new DenDaoImpl();
        KategorieDao kategorieDao = new KategorieDaoImpl();
        PravidloDao pravidloDao = new PravidloDaoImpl();
        KalendarovePravidloDao kalendarovePravidloDao = new KalendarovePravidloDaoImpl();
        WifiPravidloDao wifiPravidloDao = new WifiPravidloDaoImpl();
        CasovePravidloDao casovePravidloDao = new CasovePravidloDaoImpl();
        IntentDao intentDao = new IntentDaoImpl();
        DnyCasovehoPravidlaDao dnyCasovehoPravidlaDao = new DnyCasovehoPravidlaDaoImpl();
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();

        db.execSQL(kategorieDao.createTable());
        db.execSQL(kategorieDao.insertData());
        db.execSQL(pravidloDao.createTable());
        db.execSQL(denDao.createTable());
        db.execSQL(denDao.insertData());
        db.execSQL(kalendarovePravidloDao.createTable());
        db.execSQL(wifiPravidloDao.createTable());
        db.execSQL(casovePravidloDao.createTable());
        db.execSQL(dnyCasovehoPravidlaDao.createTable());
        db.execSQL(konfiguraceDao.createTable());
        db.execSQL(konfiguraceDao.insertData());
        db.execSQL(intentDao.createTable());
        db.execSQL(dnyCasovehoPravidlaDao.createIndex());
        db.execSQL(casovePravidloDao.createIndex());
        db.execSQL(denDao.createIndex());
        db.execSQL(kalendarovePravidloDao.createIndex());
        db.execSQL(kategorieDao.createIndex());
        db.execSQL(pravidloDao.createIndex());
        db.execSQL(wifiPravidloDao.createIndex());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Kategorie.class.getSimpleName());
        db.execSQL("DROP TABLE IF EXISTS " + Pravidlo.class.getSimpleName());
        db.execSQL("DROP TABLE IF EXISTS " + Den.class.getSimpleName());
        db.execSQL("DROP TABLE IF EXISTS " + KalendarovePravidlo.class.getSimpleName());
        db.execSQL("DROP TABLE IF EXISTS " + WifiPravidlo.class.getSimpleName());
        db.execSQL("DROP TABLE IF EXISTS " + CasovePravidlo.class.getSimpleName());
        db.execSQL("DROP TABLE IF EXISTS " + "DnyCasovehoPravidla");
        db.execSQL("DROP TABLE IF EXISTS " + "Konfigurace");
        db.execSQL("DROP TABLE IF EXISTS " + "Intent");
        onCreate(db);
    }
}