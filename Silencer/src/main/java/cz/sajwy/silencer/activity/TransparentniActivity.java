package cz.sajwy.silencer.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

import java.util.Calendar;

import cz.sajwy.silencer.R;
import cz.sajwy.silencer.callback.CasCallback;
import cz.sajwy.silencer.dialog.CasDialog;
import cz.sajwy.silencer.utils.Utils;

public class TransparentniActivity extends Activity implements CasCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparentni);

        FragmentManager fragmentManager = getFragmentManager();
        CasDialog casObnovyDialog = CasDialog.newInstance(getString(R.string.potlaceni_obsluhy), "CAS_OBNOVY");
        casObnovyDialog.show(fragmentManager, "casObnovyDialog");
    }

    @Override
    public void setCas(String cas, String parametr) {
        Utils.pozastavitObsluhuPravidel(getApplicationContext(), Utils.urciCasKonce(cas, Calendar.getInstance()));
        Utils.zrusNotifikaci(this, Utils.JEDNORAZOVA_NOTIFIKACE_ID);
    }
}