package cz.sajwy.silencer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import cz.sajwy.silencer.dao.KonfiguraceDao;
import cz.sajwy.silencer.dao.WifiPravidloDao;
import cz.sajwy.silencer.daoImpl.KonfiguraceDaoImpl;
import cz.sajwy.silencer.daoImpl.WifiPravidloDaoImpl;
import cz.sajwy.silencer.model.WifiPravidlo;
import cz.sajwy.silencer.utils.Utils;

/**
 * Created by Sajwy on 08.09.2017.
 */
public class WifiStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        switch (intent.getAction()) {
            case "android.net.wifi.WIFI_STATE_CHANGED":
                if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                    Utils.enableDisableComponent(context, WifiScanReceiver.class, true);
                } else if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
                    Utils.zrusWifiCasovac(context, true);
                    KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
                    konfiguraceDao.updateVykonavaSePravidlo(0);
                }
                break;
            case "android.net.conn.CONNECTIVITY_CHANGE":
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                DetailedState state = wifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if(state == DetailedState.CONNECTED || state == DetailedState.OBTAINING_IPADDR) {
                    WifiPravidloDao wifiPravidloDao = new WifiPravidloDaoImpl();
                    String ssid = Utils.vratSpravneSSID(wifiInfo.getSSID());
                    if(wifiPravidloDao.obsazenoVAktivnich(ssid)) {
                        Utils.zrusWifiCasovac(context, false);
                        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
                        konfiguraceDao.updateWifiPravidlo(1);
                        WifiPravidlo wifiPravidlo = wifiPravidloDao.getAktivniWifiPravidloByWifi(ssid);
                        String rezim;
                        if (wifiPravidlo.getVibrace() == 1)
                            rezim = "vibrace";
                        else
                            rezim = "ticho";
                        Utils.nastavRezimZvuku(context, rezim, wifiPravidlo.getNazev());
                    }
                } else if(state == DetailedState.DISCONNECTED || state == DetailedState.SCANNING) {
                    Utils.nastavWifiCasovac(context, 0.5);
                }
                break;
        }
    }
}