package cz.sajwy.silencer.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cz.sajwy.silencer.R;
import cz.sajwy.silencer.callback.ButtonCallback;
import cz.sajwy.silencer.dao.KonfiguraceDao;
import cz.sajwy.silencer.dao.PravidloDao;
import cz.sajwy.silencer.daoImpl.KonfiguraceDaoImpl;
import cz.sajwy.silencer.daoImpl.PravidloDaoImpl;
import cz.sajwy.silencer.dialog.KategoriePravidelDialog;
import cz.sajwy.silencer.service.ObsluhaPravidelService;
import cz.sajwy.silencer.utils.Utils;

public class MainActivity extends AppCompatActivity implements ButtonCallback, OnClickListener {
    private ImageView ivObsluha;
    private TextView tvObsluha;
    private static Activity activity;
    private boolean ukoncit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ukoncit = false;

        ImageView ivNovePravidlo = (ImageView) findViewById(R.id.ivNovePravidlo);
        ImageView ivSeznamPravidel = (ImageView) findViewById(R.id.ivSeznamPravidel);
        ivObsluha = (ImageView) findViewById(R.id.ivZapnoutVypnoutObsluhu);
        tvObsluha = (TextView) findViewById(R.id.tvZapnouVypnoutObsluhu);
        ImageView ivNapoveda = (ImageView) findViewById(R.id.ivNapoveda);
        ImageView ivExit = (ImageView) findViewById(R.id.ivExit);

        ivNovePravidlo.setOnClickListener(this);
        ivSeznamPravidel.setOnClickListener(this);
        ivObsluha.setOnClickListener(this);
        ivNapoveda.setOnClickListener(this);
        ivExit.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        if(konfiguraceDao.getObsluhaPravidel() == 1) {
            ivObsluha.setImageResource(R.drawable.off);
            tvObsluha.setText(R.string.vypnout_obsluhu);
        } else {
            ivObsluha.setImageResource(R.drawable.on);
            tvObsluha.setText(R.string.zapnout_obsluhu);
        }
        activity = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activity = null;
    }

    @Override
    public void onBackPressed() {
        if (ukoncit) {
            finish();
        } else {
            Toast.makeText(this, R.string.ukonceniAplikace, Toast.LENGTH_SHORT).show();
            ukoncit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ukoncit = false;
                }
            }, 3 * 1000);
        }
    }

    public static Activity getActivity() {
        return activity;
    }

    @Override
    public void setButtonView() {
        ivObsluha.setImageResource(R.drawable.on);
        tvObsluha.setText(R.string.zapnout_obsluhu);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        if(konfiguraceDao.getObsluhaPravidel() == 1)
            stopService(new Intent(getApplicationContext(), ObsluhaPravidelService.class));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.ivNovePravidlo:
                FragmentManager manager = getFragmentManager();
                KategoriePravidelDialog dialog = new KategoriePravidelDialog();
                dialog.show(manager, "KategoriePravidelDialog");
                break;
            case R.id.ivSeznamPravidel:
                startActivity(new Intent(getApplicationContext(), SeznamPravidelActivity.class));
                break;
            case R.id.ivZapnoutVypnoutObsluhu:
                PravidloDao pravidloDao = new PravidloDaoImpl();
                if(pravidloDao.existujiAktivniPravidla()) {
                    KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
                    if (konfiguraceDao.getObsluhaPravidel() == 1) {
                        ivObsluha.setImageResource(R.drawable.on);
                        tvObsluha.setText(R.string.zapnout_obsluhu);
                        Utils.vypnoutObsluhuPravidel(getApplicationContext());
                    } else {
                        ivObsluha.setImageResource(R.drawable.off);
                        tvObsluha.setText(R.string.vypnout_obsluhu);
                        Utils.zapnoutObsluhuPravidel(getApplicationContext());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.neexistenceAktivnichPravidel, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ivNapoveda:
                startActivity(new Intent(getApplicationContext(), NapovedaActivity.class));
                break;
            case R.id.ivExit:
                finish();
                break;
        }
    }
}