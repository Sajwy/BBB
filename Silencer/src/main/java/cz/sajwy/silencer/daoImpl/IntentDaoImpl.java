package cz.sajwy.silencer.daoImpl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cz.sajwy.silencer.dao.IntentDao;
import cz.sajwy.silencer.db.DBManager;

/**
 * Created by Sajwy on 06.06.2017.
 */
public class IntentDaoImpl implements IntentDao {
    private static final String TABLE = "Intent";
    private static final String KEY_ID_INTENT = "ID_intent";
    private static final String[] COLUMNS = {KEY_ID_INTENT};

    public String createTable(){
        return "CREATE TABLE " + TABLE  +
                "(" +
                    KEY_ID_INTENT  + " INTEGER" +
                ");";
    }

    @Override
    public void insert(int id){
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "INSERT INTO " + TABLE + " (" + KEY_ID_INTENT + ") VALUES (" + id + ");";
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
    }

    @Override
    public void delete(int id) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "DELETE FROM " + TABLE + " WHERE " + KEY_ID_INTENT + " = " + id;
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
    }

    @Override
    public void deleteAllCpIntents() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "DELETE FROM " + TABLE + " WHERE " + KEY_ID_INTENT + " > 1";
        db.execSQL(query);
        DBManager.getInstance().closeDatabase();
    }

    @Override
    public List<Integer> getAllCpIntents() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String query = "SELECT " + KEY_ID_INTENT + " FROM " + TABLE + " WHERE " + KEY_ID_INTENT + " > 1 " + " ORDER BY " + KEY_ID_INTENT;
        Cursor cursor = db.rawQuery(query, null);
        List<Integer> ids = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(KEY_ID_INTENT));
            ids.add(id);
        }
        cursor.close();
        DBManager.getInstance().closeDatabase();
        return ids;
    }
}