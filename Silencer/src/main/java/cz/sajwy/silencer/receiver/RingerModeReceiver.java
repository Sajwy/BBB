package cz.sajwy.silencer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import cz.sajwy.silencer.R;
import cz.sajwy.silencer.dao.KonfiguraceDao;
import cz.sajwy.silencer.daoImpl.KonfiguraceDaoImpl;
import cz.sajwy.silencer.utils.Utils;

/**
 * Created by Sajwy on 03.09.2017.
 */
public class RingerModeReceiver extends BroadcastReceiver {
    AudioManager audioManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(audioManager.RINGER_MODE_CHANGED_ACTION)) {
            KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
            if (konfiguraceDao.getZmenaRezimuAplikaci() == 0) {
                if (audioManager == null)
                    audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);

                Utils.pozastavitObsluhuPravidel(context, Long.MAX_VALUE);
                Utils.vytvorJednorazovouNotifikaci(context, context.getString(R.string.autopauznuti_obsluhy));
            } else {
                konfiguraceDao.updateZmenaRezimuAplikaci(0);
            }
        }
    }
}