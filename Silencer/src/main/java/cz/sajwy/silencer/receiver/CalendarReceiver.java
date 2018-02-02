package cz.sajwy.silencer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import cz.sajwy.silencer.dao.PravidloDao;
import cz.sajwy.silencer.daoImpl.PravidloDaoImpl;
import cz.sajwy.silencer.utils.Utils;

/**
 * Created by Sajwy on 18.08.2017.
 */
public class CalendarReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean zapnoutZvuky = Utils.potrebaZapnoutZvuky(context, Calendar.getInstance(), 1);
        PravidloDao pravidloDao = new PravidloDaoImpl();

        if(zapnoutZvuky && pravidloDao.existujiAktivniPravidlaKategorie("Wifi pravidlo")) {
            if(Utils.wifiZapnuta(context)) {
                zapnoutZvuky = Utils.velkyWifiTest(context);
            }
            Utils.enableDisableComponent(context, WifiStateReceiver.class, true);
        }
        Utils.zrusCasovace(context, zapnoutZvuky);
        Utils.nastavCasovace(context, Calendar.getInstance());
    }
}