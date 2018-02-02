package cz.sajwy.silencer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cz.sajwy.silencer.activity.MainActivity;
import cz.sajwy.silencer.callback.ButtonCallback;
import cz.sajwy.silencer.utils.Utils;

/**
 * Created by Sajwy on 19.10.2017.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case "OBSLUHA_STOP":
                Utils.vypnoutObsluhuPravidel(context);
                if(MainActivity.getActivity() != null) {
                    ButtonCallback activity = (ButtonCallback) MainActivity.getActivity();
                    activity.setButtonView();
                }
                break;
            case "OBSLUHA_POKRACOVAT":
                Utils.obnovitObsluhuPravidel(context);
                break;
        }
    }
}