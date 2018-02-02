package cz.sajwy.silencer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import cz.sajwy.silencer.R;
import cz.sajwy.silencer.dao.KonfiguraceDao;
import cz.sajwy.silencer.daoImpl.KonfiguraceDaoImpl;
import cz.sajwy.silencer.service.ObsluhaPravidelService;
import cz.sajwy.silencer.utils.Utils;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        if(konfiguraceDao.getObsluhaPravidel() == 1) {
            switch(intent.getAction()) {
                case Intent.ACTION_BOOT_COMPLETED:
                    Utils.enableDisableComponent(context, AlarmReceiver.class, true);
                    long dobaObnovy = konfiguraceDao.getDobaObnovy();
                    if(dobaObnovy == 0 || dobaObnovy <= Calendar.getInstance().getTimeInMillis() || dobaObnovy == Long.MAX_VALUE) {
                        if(dobaObnovy > 0)
                            konfiguraceDao.updateDobaObnovy(0);

                        Utils.vytvorStickyNotifikaci(context, "pause", context.getString(R.string.obsluha_spustena));
                    } else {
                        Utils.vytvorStickyNotifikaci(context, "play", context.getString(R.string.obsluha_pozastavena));
                    }
                    context.startService(new Intent(context, ObsluhaPravidelService.class));
                    break;
                case Intent.ACTION_SHUTDOWN:
                case "android.intent.action.QUICKBOOT_POWEROFF":
                    Utils.enableDisableComponent(context, WifiStateReceiver.class, false);
                    Utils.enableDisableComponent(context, CalendarReceiver.class, false);
                    Utils.enableDisableComponent(context, NotificationReceiver.class, false);
                    Utils.enableDisableComponent(context, RingerModeReceiver.class, false);
                    Utils.enableDisableComponent(context, AlarmReceiver.class, false);
                    Utils.zrusWifiCasovac(context, true);
                    Utils.zrusCasovace(context, false);
                    if(konfiguraceDao.getDobaObnovy() > 0)
                        Utils.zrusAlarmPendingIntent(context, 0);
                    break;
            }
        } else {
            System.exit(0);
        }
    }
}