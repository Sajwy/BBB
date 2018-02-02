package cz.sajwy.silencer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cz.sajwy.silencer.service.ObsluhaPravidelService;

public class ServiceRestarterReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() == "RESTART_SERVICE")
            context.startService(new Intent(context, ObsluhaPravidelService.class));
    }
}