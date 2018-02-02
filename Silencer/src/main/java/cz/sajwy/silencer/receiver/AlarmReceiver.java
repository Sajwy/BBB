package cz.sajwy.silencer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import cz.sajwy.silencer.dao.IntentDao;
import cz.sajwy.silencer.dao.KonfiguraceDao;
import cz.sajwy.silencer.dao.PravidloDao;
import cz.sajwy.silencer.daoImpl.IntentDaoImpl;
import cz.sajwy.silencer.daoImpl.KonfiguraceDaoImpl;
import cz.sajwy.silencer.daoImpl.PravidloDaoImpl;
import cz.sajwy.silencer.utils.Utils;

/**
 * Created by Sajwy on 05.06.2017.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id", -1);

        if (id != -1 && intent.getAction().equals("ALARM")) {
            switch (id) {
                case 0:
                    Utils.obnovitObsluhuPravidel(context);
                    break;
                case 1:
                    Utils.malyWifiTest(context);
                    break;
                case 2:
                    Utils.zrusCasovace(context, false);
                    Utils.nastavCasovace(context, Calendar.getInstance());
                    break;
                default:
                    String rezim = intent.getStringExtra("rezim");
                    KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
                    IntentDao intentDao = new IntentDaoImpl();

                    if (konfiguraceDao.getCasoveNeboKalendarovePravidlo() == 1 && !rezim.equals("normal")) {
                        Utils.zrusAlarmPendingIntent(context, id - 1);
                        intentDao.delete(id);
                        konfiguraceDao.updateNazevObsluhovaneUdalosti(intent.getStringExtra("udalost"));
                        Utils.nastavRezimZvuku(context, rezim, intent.getStringExtra("nazev"));
                    } else {
                        PravidloDao pravidloDao = new PravidloDaoImpl();
                        if (rezim.equals("normal")) {
                            intentDao.delete(id);
                            konfiguraceDao.updateCasoveNeboKalendarovePravidlo(0);

                            if (pravidloDao.existujiAktivniPravidlaKategorie("Wifi pravidlo")) {
                                if (Utils.wifiZapnuta(context)) {
                                    Utils.velkyWifiTest(context);
                                }
                                Utils.enableDisableComponent(context, WifiStateReceiver.class, true);
                            } else {
                                Utils.nastavRezimZvuku(context, rezim, intent.getStringExtra("nazev"));
                            }
                        } else {
                            konfiguraceDao.updateCasoveNeboKalendarovePravidlo(1);
                            intentDao.delete(id);
                            konfiguraceDao.updateNazevObsluhovaneUdalosti(intent.getStringExtra("udalost"));
                            Utils.nastavRezimZvuku(context, rezim, intent.getStringExtra("nazev"));

                            if (pravidloDao.existujiAktivniPravidlaKategorie("Wifi pravidlo")) {
                                Utils.enableDisableComponent(context, WifiStateReceiver.class, false);
                                Utils.zrusWifiCasovac(context, false);
                            }
                        }
                    }
            }
        }
    }
}