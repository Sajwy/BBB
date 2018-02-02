package cz.sajwy.silencer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import cz.sajwy.silencer.dao.KonfiguraceDao;
import cz.sajwy.silencer.daoImpl.KonfiguraceDaoImpl;
import cz.sajwy.silencer.utils.Utils;

public class WifiScanReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            Utils.enableDisableComponent(context, WifiScanReceiver.class, false);
            KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
            if(konfiguraceDao.getWifiPravidlo() == 0) {
                Utils.velkyWifiTest(context);
            }
        }
    }
}