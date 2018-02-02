package cz.sajwy.silencer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Calendar;

import cz.sajwy.silencer.dao.KonfiguraceDao;
import cz.sajwy.silencer.daoImpl.KonfiguraceDaoImpl;
import cz.sajwy.silencer.utils.Utils;

public class ObsluhaPravidelService extends Service {

    public ObsluhaPravidelService() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        if(konfiguraceDao.getDobaObnovy() == 0)
            Utils.provedTestPravidel(getApplicationContext());
        else if(konfiguraceDao.getDobaObnovy() <= Calendar.getInstance().getTimeInMillis())
            Utils.obnovitObsluhuPravidel(getApplicationContext());
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        if(konfiguraceDao.getObsluhaPravidel() == 1)
            sendBroadcast(new Intent("RESTART_SERVICE"));
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        if(konfiguraceDao.getObsluhaPravidel() == 1)
            sendBroadcast(new Intent("RESTART_SERVICE"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}