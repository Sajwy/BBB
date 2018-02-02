package cz.sajwy.silencer.app;

import android.app.Application;
import android.content.Context;

import cz.sajwy.silencer.db.DBHelper;
import cz.sajwy.silencer.db.DBManager;

public class App extends Application {
    private static Context context;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = this.getApplicationContext();
        DBManager.initializeInstance(new DBHelper());
    }

    public static Context getContext(){
        return context;
    }
}