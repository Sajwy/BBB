package cz.sajwy.silencer.activity;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import cz.sajwy.silencer.R;
import cz.sajwy.silencer.adapter.ParametryPravidelAdapter;
import cz.sajwy.silencer.callback.CasCallback;
import cz.sajwy.silencer.callback.DnyCallback;
import cz.sajwy.silencer.callback.EditTextCallback;
import cz.sajwy.silencer.callback.SmazatPravidloCallback;
import cz.sajwy.silencer.callback.SwitchButtonCallback;
import cz.sajwy.silencer.callback.WifiCallback;
import cz.sajwy.silencer.dao.CasovePravidloDao;
import cz.sajwy.silencer.dao.DenDao;
import cz.sajwy.silencer.dao.KalendarovePravidloDao;
import cz.sajwy.silencer.dao.KategorieDao;
import cz.sajwy.silencer.dao.KonfiguraceDao;
import cz.sajwy.silencer.dao.PravidloDao;
import cz.sajwy.silencer.dao.WifiPravidloDao;
import cz.sajwy.silencer.daoImpl.CasovePravidloDaoImpl;
import cz.sajwy.silencer.daoImpl.DenDaoImpl;
import cz.sajwy.silencer.daoImpl.KalendarovePravidloDaoImpl;
import cz.sajwy.silencer.daoImpl.KategorieDaoImpl;
import cz.sajwy.silencer.daoImpl.KonfiguraceDaoImpl;
import cz.sajwy.silencer.daoImpl.PravidloDaoImpl;
import cz.sajwy.silencer.daoImpl.WifiPravidloDaoImpl;
import cz.sajwy.silencer.dialog.CasDialog;
import cz.sajwy.silencer.dialog.DnyDialog;
import cz.sajwy.silencer.dialog.EditTextDialog;
import cz.sajwy.silencer.dialog.SmazPravidloDialog;
import cz.sajwy.silencer.dialog.WifiDialog;
import cz.sajwy.silencer.model.CasovePravidlo;
import cz.sajwy.silencer.model.Den;
import cz.sajwy.silencer.model.KalendarovePravidlo;
import cz.sajwy.silencer.model.Pravidlo;
import cz.sajwy.silencer.model.WifiPravidlo;
import cz.sajwy.silencer.receiver.CalendarReceiver;
import cz.sajwy.silencer.receiver.WifiStateReceiver;
import cz.sajwy.silencer.utils.Utils;

public class NovePravidloActivity extends AppCompatActivity implements OnItemClickListener,WifiCallback, EditTextCallback, CasCallback, SwitchButtonCallback, DnyCallback, SmazatPravidloCallback {
    private CasovePravidlo casovePravidlo;
    private KalendarovePravidlo kalendarovePravidlo;
    private WifiPravidlo wifiPravidlo;
    private List<String> parametryVse;
    private TextView tvNadpis, tvObsah;
    private Switch prepinac;
    private String kategorie;
    private ParametryPravidelAdapter adapter;
    private int id;
    private String nazev;
    private int stav;
    private int vibrace;
    private ArrayList<Integer> vybraneDny;
    private String cas_od;
    private String cas_do;
    private String vypisNazvy,vypisZkratky;
    private String nazev_wifi;
    private String udalost;
    private boolean lze;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nove_pravidlo);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        id = getIntent().getIntExtra("id", 0);
        kategorie = getIntent().getStringExtra("kategorie");

        if(id == 0) {
            setTitle("Nové " + kategorie.toLowerCase());
            nazev = "";
            stav = 1;
            vibrace = 1;
        } else {
            setTitle(R.string.DP_name);
            PravidloDao pravidloDao = new PravidloDaoImpl();
            Pravidlo pravidlo = pravidloDao.getPravidlo(id);
            nazev = pravidlo.getNazev();
            stav = pravidlo.getStav();
            vibrace = pravidlo.getVibrace();
        }

        switch (kategorie) {
            case "Časové pravidlo":
                if(id == 0) {
                    casovePravidlo = new CasovePravidlo();
                    parametryVse = zjistiParametryPravidla(casovePravidlo, kategorie);
                    KategorieDao kategorieDao = new KategorieDaoImpl();
                    casovePravidlo.setKategorie(kategorieDao.getKategorieByNazev(kategorie));
                    vybraneDny = new ArrayList<>();
                    cas_od = "";
                    cas_do = "";
                    vypisNazvy = "";
                    vypisZkratky = "";
                } else {
                    CasovePravidloDao casovePravidloDao = new CasovePravidloDaoImpl();
                    casovePravidlo = casovePravidloDao.getCasovePravidlo(id);
                    parametryVse = zjistiParametryPravidla(casovePravidlo, kategorie);
                    cas_od = casovePravidlo.getCas_od();
                    cas_do = casovePravidlo.getCas_do();
                    vybraneDny = casovePravidloDao.getIntListDnuCasovehoPravidla(casovePravidlo.getId_pravidlo());
                    vypisNazvy = casovePravidloDao.getVypisDnuNazvy(id);
                    vypisZkratky = "";
                }
                break;
            case "Kalendářové pravidlo":
                if(id == 0) {
                    kalendarovePravidlo = new KalendarovePravidlo();
                    parametryVse = zjistiParametryPravidla(kalendarovePravidlo, kategorie);
                    KategorieDao kategorieDao = new KategorieDaoImpl();
                    kalendarovePravidlo.setKategorie(kategorieDao.getKategorieByNazev(kategorie));
                    udalost = "";
                } else {
                    KalendarovePravidloDao kalendarovePravidloDao = new KalendarovePravidloDaoImpl();
                    kalendarovePravidlo = kalendarovePravidloDao.getKalendarovePravidlo(id);
                    parametryVse = zjistiParametryPravidla(kalendarovePravidlo, kategorie);
                    udalost = kalendarovePravidlo.getUdalost();
                }
                break;
            case "Wifi pravidlo":
                if(id == 0) {
                    wifiPravidlo = new WifiPravidlo();
                    parametryVse = zjistiParametryPravidla(wifiPravidlo, kategorie);
                    KategorieDao kategorieDao = new KategorieDaoImpl();
                    wifiPravidlo.setKategorie(kategorieDao.getKategorieByNazev(kategorie));
                    nazev_wifi = "";
                } else {
                    WifiPravidloDao wifiPravidloDao = new WifiPravidloDaoImpl();
                    wifiPravidlo = wifiPravidloDao.getWifiPravidlo(id);
                    parametryVse = zjistiParametryPravidla(wifiPravidlo, kategorie);
                    nazev_wifi = wifiPravidlo.getNazev_wifi();
                }
                break;
        }

        ListView lvNovePravidlo = (ListView) findViewById(R.id.lvNovePravidlo);
        adapter = new ParametryPravidelAdapter(getApplicationContext(), parametryVse, id);
        lvNovePravidlo.setAdapter(adapter);
        lvNovePravidlo.setOnItemClickListener(this);
        lze = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(id == 0)
            getMenuInflater().inflate(R.menu.menu_nove_pravidlo, menu);
        else
            getMenuInflater().inflate(R.menu.menu_detail_pravidla, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if(id == 0)
                    ulozPravidlo();
                else
                    upravPravidlo();
                break;
            case R.id.action_delete:
                SmazPravidloDialog smazPravidloDialog = SmazPravidloDialog.newInstance(-1 , -1);
                smazPravidloDialog.show(getFragmentManager(), "smazPravidloDialog");
                break;
            case android.R.id.home:
                Intent upIntent;
                if(id == 0) {
                    upIntent = new Intent(this, MainActivity.class);
                } else {
                    upIntent = new Intent(this, SeznamPravidelActivity.class);
                }
                NavUtils.navigateUpTo(this, upIntent);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent upIntent;
        if(id == 0) {
            upIntent = new Intent(this, MainActivity.class);
        } else {
            upIntent = new Intent(this, SeznamPravidelActivity.class);
        }
        NavUtils.navigateUpTo(this, upIntent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        tvNadpis = (TextView) view.findViewById(R.id.tvNadpis);
        tvObsah = (TextView) view.findViewById(R.id.tvObsah);

        switch (parametryVse.get(position)) {
            case ParametryPravidelAdapter.NAZEV:
                EditTextDialog nazevDialog = EditTextDialog.newInstance(tvNadpis.getText().toString(), tvObsah.getText().toString(), ParametryPravidelAdapter.NAZEV);
                nazevDialog.show(getFragmentManager(), "nazevDialog");
                break;
            case ParametryPravidelAdapter.UDALOST:
                EditTextDialog udalostDialog = EditTextDialog.newInstance(tvNadpis.getText().toString(), tvObsah.getText().toString(), ParametryPravidelAdapter.UDALOST);
                udalostDialog.show(getFragmentManager(), "udalostDialog");
                break;
            case ParametryPravidelAdapter.CAS_OD:
                CasDialog casOdDialog = CasDialog.newInstance(tvNadpis.getText().toString(), tvObsah.getText().toString(), ParametryPravidelAdapter.CAS_OD);
                casOdDialog.show(getFragmentManager(), "casOdDialog");
                break;
            case ParametryPravidelAdapter.CAS_DO:
                CasDialog casDoDialog = CasDialog.newInstance(tvNadpis.getText().toString(), tvObsah.getText().toString(), ParametryPravidelAdapter.CAS_DO);
                casDoDialog.show(getFragmentManager(), "casDoDialog");
                break;
            case ParametryPravidelAdapter.DNY:
                DnyDialog dnyDialog = DnyDialog.newInstance(tvNadpis.getText().toString(), vybraneDny);
                dnyDialog.show(getFragmentManager(), "dnyDialog");
                break;
            case ParametryPravidelAdapter.VIBRACE:
                prepinac = (Switch) view.findViewById(R.id.swID);
                setVibrace();
                break;
            case ParametryPravidelAdapter.STAV:
                prepinac = (Switch) view.findViewById(R.id.swID);
                setStav();
                break;
            case ParametryPravidelAdapter.WIFI:
                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                if(wifiManager != null) {
                    WifiDialog wifiDialog = WifiDialog.newInstance(tvNadpis.getText().toString(), tvObsah.getText().toString());
                    wifiDialog.show(getFragmentManager(), "wifiDialog");
                } else {
                    Toast.makeText(getApplicationContext(), R.string.wifiNepodporovano, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void ulozPravidlo() {
        String validace = zvalidujVstupy();

        if(!validace.equals("") || !lze) {
            if(!validace.equals("")) {
                validace = "Nevyplněn parametr: " + validace.substring(0, validace.length() - 2) + "!!!";
            }

            if(!lze) {
                if(kategorie.equals("Kalendářové pravidlo")) {
                    if(validace.equals(""))
                        validace = "Název události je již použit!!!";
                    else
                        validace += "\n" + "Název události je již použit!!!";
                } else if(kategorie.equals("Wifi pravidlo")) {
                    if(validace.equals(""))
                        validace = "Název wifi je již použit!!!";
                    else
                        validace += "\n" + "Název wifi je již použit!!!";
                }
            }

            Toast.makeText(getApplicationContext(), validace, Toast.LENGTH_LONG).show();
        } else {
            KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
            switch (kategorie) {
                case "Časové pravidlo":
                    CasovePravidloDao casovePravidloDao = new CasovePravidloDaoImpl();
                    casovePravidloDao.insertCasovePravidlo(casovePravidlo);
                    if(konfiguraceDao.getObsluhaPravidel() == 1 && konfiguraceDao.getDobaObnovy() == 0) {
                        long[] casoveUdaje = Utils.prevedTimeStringyNaMilisekundy(casovePravidlo.getCas_od(), casovePravidlo.getCas_do(), false);
                        long nyni = Calendar.getInstance().getTimeInMillis();
                        List<Den> dny = casovePravidlo.getDny();
                        Den den = Utils.zjistiAktualniDen();
                        if (Utils.jeDenObsazen(dny, den) && casoveUdaje[1] > nyni && casovePravidlo.getStav() == 1) {
                            Utils.zrusCasovace(getApplicationContext(), false);
                            Utils.nastavCasovace(getApplicationContext(), Calendar.getInstance());
                        }
                    }
                    break;
                case "Kalendářové pravidlo":
                    KalendarovePravidloDao kalendarovePravidloDao = new KalendarovePravidloDaoImpl();
                    kalendarovePravidloDao.insertKalendarovePravidlo(kalendarovePravidlo);
                    if(konfiguraceDao.getObsluhaPravidel() == 1 && konfiguraceDao.getDobaObnovy() == 0 && kalendarovePravidlo.getStav() == 1) {
                        if(Utils.jeUdalostDnes(getApplicationContext(), kalendarovePravidlo.getUdalost())) {
                            Utils.zrusCasovace(getApplicationContext(), false);
                            Utils.nastavCasovace(getApplicationContext(), Calendar.getInstance());
                        }
                        
                        PravidloDao pravidloDao = new PravidloDaoImpl();
                        if(pravidloDao.vratPocetAktivnichPravidelKategorie(kategorie) == 1) {
                            Utils.enableDisableComponent(getApplicationContext(), CalendarReceiver.class, true);
                        }
                    }
                    break;
                case "Wifi pravidlo":
                    WifiPravidloDao wifiPravidloDao = new WifiPravidloDaoImpl();
                    wifiPravidloDao.insertWifiPravidlo(wifiPravidlo);
                    if(konfiguraceDao.getObsluhaPravidel() == 1 && konfiguraceDao.getDobaObnovy() == 0 && konfiguraceDao.getCasoveNeboKalendarovePravidlo() == 0 && wifiPravidlo.getStav() == 1) {
                        if(Utils.wifiZapnuta(NovePravidloActivity.this)) {
                            Utils.velkyWifiTest(getApplicationContext());
                        }

                        PravidloDao pravidloDao = new PravidloDaoImpl();
                        if(pravidloDao.vratPocetAktivnichPravidelKategorie(kategorie) == 1) {
                            Utils.enableDisableComponent(getApplicationContext(), WifiStateReceiver.class, true);
                        }
                    }
                    break;
            }
            Toast.makeText(getApplicationContext(), R.string.ulozeno, Toast.LENGTH_SHORT).show();
        }
    }

    private void upravPravidlo() {
        String validace = zvalidujVstupy();

        if(!validace.equals("")) {
            validace = "Nevyplněn parametr: " + validace.substring(0, validace.length() - 2) + "!!!";
            Toast.makeText(getApplicationContext(), validace, Toast.LENGTH_LONG).show();
        } else {
            KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
            switch (kategorie) {
                case "Časové pravidlo":
                    CasovePravidloDao casovePravidloDao = new CasovePravidloDaoImpl();
                    CasovePravidlo puvodni = casovePravidloDao.getCasovePravidlo(casovePravidlo.getId_pravidlo());
                    casovePravidloDao.updateCasovePravidlo(casovePravidlo);
                    if(konfiguraceDao.getObsluhaPravidel() == 1 && konfiguraceDao.getDobaObnovy() == 0) {
                        long[] casoveUdajePuvodni = Utils.prevedTimeStringyNaMilisekundy(puvodni.getCas_od(),puvodni.getCas_do(), false);
                        long[] casoveUdaje = Utils.prevedTimeStringyNaMilisekundy(casovePravidlo.getCas_od(), casovePravidlo.getCas_do(), false);
                        long nyni = Calendar.getInstance().getTimeInMillis();
                        List<Den> dnyPuvodni = puvodni.getDny();
                        List<Den> dny = casovePravidlo.getDny();
                        Den den = Utils.zjistiAktualniDen();
                        if(!(puvodni.getStav() == 0 && casovePravidlo.getStav() == 0) && ((Utils.jeDenObsazen(dnyPuvodni, den) && casoveUdajePuvodni[1] > nyni) || (Utils.jeDenObsazen(dny, den) && casoveUdaje[1] > nyni))) {
                            boolean zapnoutZvuky;
                            if(Utils.jeDenObsazen(dnyPuvodni, den) && casoveUdajePuvodni[0] <= nyni && casoveUdajePuvodni[1] > nyni &&
                                    ((casoveUdaje[0] > nyni || casoveUdaje[1] <= nyni) || (puvodni.getStav() == 1 && casovePravidlo.getStav() == 0)) ||
                                    !Utils.jeDenObsazen(dny, den)) {
                                zapnoutZvuky = Utils.potrebaZapnoutZvuky(getApplicationContext(), Calendar.getInstance(), 1);
                                PravidloDao pravidloDao = new PravidloDaoImpl();
                                if (zapnoutZvuky && pravidloDao.existujiAktivniPravidlaKategorie("Wifi pravidlo")) {
                                    if (Utils.wifiZapnuta(getApplicationContext())) {
                                        zapnoutZvuky = Utils.velkyWifiTest(getApplicationContext());
                                    }
                                    Utils.enableDisableComponent(getApplicationContext(), WifiStateReceiver.class, true);
                                }
                            } else {
                                zapnoutZvuky = false;
                            }
                            Utils.zrusCasovace(getApplicationContext(), zapnoutZvuky);
                            Utils.nastavCasovace(getApplicationContext(), Calendar.getInstance());
                        }
                    }
                    break;
                case "Kalendářové pravidlo":
                    KalendarovePravidloDao kalendarovePravidloDao = new KalendarovePravidloDaoImpl();
                    KalendarovePravidlo puvodniKP = kalendarovePravidloDao.getKalendarovePravidlo(kalendarovePravidlo.getId_pravidlo());
                    kalendarovePravidloDao.updateKalendarovePravidlo(kalendarovePravidlo);
                    if(konfiguraceDao.getObsluhaPravidel() == 1 && konfiguraceDao.getDobaObnovy() == 0) {
                        PravidloDao pravidloDao = new PravidloDaoImpl();
                        int pocetAKP = pravidloDao.vratPocetAktivnichPravidelKategorie(kategorie);
                        if (kalendarovePravidlo.getStav() == 0 && pocetAKP == 0) {
                            Utils.enableDisableComponent(getApplicationContext(), CalendarReceiver.class, false);
                        } else if (kalendarovePravidlo.getStav() == 1 && pocetAKP == 1) {
                            Utils.enableDisableComponent(getApplicationContext(), CalendarReceiver.class, true);
                        }

                        if(!(puvodniKP.getStav() == 0 && kalendarovePravidlo.getStav() == 0) && (Utils.jeUdalostDnes(getApplicationContext(), puvodniKP.getUdalost()) || Utils.jeUdalostDnes(getApplicationContext(), kalendarovePravidlo.getUdalost()))) {
                            boolean zapnoutZvuky;
                            if(Utils.jeUdalostDnes(getApplicationContext(), puvodniKP.getUdalost()) && !konfiguraceDao.getNazevObsluhovaneUdalosti().equals("") &&
                                    konfiguraceDao.getNazevObsluhovaneUdalosti().toLowerCase().contains(puvodniKP.getUdalost().toLowerCase()) &&
                                    (!puvodniKP.getUdalost().equals(kalendarovePravidlo.getUdalost()) || (puvodniKP.getStav() == 1 && kalendarovePravidlo.getStav() == 0))) {
                                zapnoutZvuky = Utils.potrebaZapnoutZvuky(getApplicationContext(), Calendar.getInstance(), 1);
                                if (zapnoutZvuky && pravidloDao.existujiAktivniPravidlaKategorie("Wifi pravidlo")) {
                                    if (Utils.wifiZapnuta(getApplicationContext())) {
                                        zapnoutZvuky = Utils.velkyWifiTest(getApplicationContext());
                                    }
                                    Utils.enableDisableComponent(getApplicationContext(), WifiStateReceiver.class, true);
                                }
                            } else {
                                zapnoutZvuky = false;
                            }
                            Utils.zrusCasovace(getApplicationContext(), zapnoutZvuky);
                            Utils.nastavCasovace(getApplicationContext(), Calendar.getInstance());
                        }
                    }
                    break;
                case "Wifi pravidlo":
                    WifiPravidloDao wifiPravidloDao = new WifiPravidloDaoImpl();
                    WifiPravidlo puvodniWP = wifiPravidloDao.getWifiPravidlo(wifiPravidlo.getId_pravidlo());
                    wifiPravidloDao.updateWifiPravidlo(wifiPravidlo);
                    if(konfiguraceDao.getObsluhaPravidel() == 1 && konfiguraceDao.getDobaObnovy() == 0 && konfiguraceDao.getCasoveNeboKalendarovePravidlo() == 0 && ((puvodniWP.getStav() != wifiPravidlo.getStav()) || (!puvodniWP.getNazev_wifi().equals(wifiPravidlo.getNazev_wifi()) && puvodniWP.getStav() == 1))) {
                        PravidloDao pravidloDao = new PravidloDaoImpl();
                        int pocetAWP = pravidloDao.vratPocetAktivnichPravidelKategorie(kategorie);

                        if(pocetAWP == 0) {
                            if(konfiguraceDao.getWifiPravidlo() == 1)
                                konfiguraceDao.updateVykonavaSePravidlo(0);
                            Utils.zrusWifiCasovac(getApplicationContext(), true);
                            Utils.enableDisableComponent(getApplicationContext(), WifiStateReceiver.class, false);
                        } else if(((wifiPravidlo.getStav() == 0 && pocetAWP > 0) || wifiPravidlo.getStav() == 1)) {
                            if(Utils.wifiZapnuta(getApplicationContext())) {
                                Utils.velkyWifiTest(getApplicationContext());
                            }

                            if(wifiPravidlo.getStav() == 1 && pocetAWP == 1) {
                                Utils.enableDisableComponent(getApplicationContext(), WifiStateReceiver.class, true);
                            }
                        }
                    }
                    break;
            }
            Toast.makeText(getApplicationContext(), R.string.zmeny, Toast.LENGTH_SHORT).show();
            Utils.automatickeVypnutiObsluhy(getApplicationContext());
        }
    }

    private String zvalidujVstupy() {
        String validace = "";

        if(nazev.equals(""))
            validace += "název pravidla, ";

        switch (kategorie) {
            case "Časové pravidlo":
                casovePravidlo.setNazev(nazev);
                casovePravidlo.setStav(stav);
                casovePravidlo.setVibrace(vibrace);
                if(vybraneDny.size() != 0) {
                    casovePravidlo.setDny(vratDnyPravidla(vybraneDny));
                    casovePravidlo.setVypisDnuNazvy(vypisNazvy);
                    casovePravidlo.setVypisDnuZkratky(vypisZkratky);
                } else
                    validace += "den/dny opakování, ";
                if(!cas_od.equals(""))
                    casovePravidlo.setCas_od(cas_od);
                else
                    validace += "čas od, ";
                if(!cas_do.equals(""))
                    casovePravidlo.setCas_do(cas_do);
                else
                    validace += "čas do, ";
                break;
            case "Kalendářové pravidlo":
                kalendarovePravidlo.setNazev(nazev);
                kalendarovePravidlo.setStav(stav);
                kalendarovePravidlo.setVibrace(vibrace);
                if(!udalost.equals("")) {
                    KalendarovePravidloDao kalendarovePravidloDao = new KalendarovePravidloDaoImpl();
                    if(kalendarovePravidloDao.lzeNazevUdalostiPouzit(udalost, id))
                        kalendarovePravidlo.setUdalost(udalost);
                    else
                        lze = false;
                } else
                    validace += "událost, ";
                break;
            case "Wifi pravidlo":
                wifiPravidlo.setNazev(nazev);
                wifiPravidlo.setStav(stav);
                wifiPravidlo.setVibrace(vibrace);
                if(!nazev_wifi.equals("")) {
                    WifiPravidloDao wifiPravidloDao = new WifiPravidloDaoImpl();
                    if (wifiPravidloDao.lzeNazevWifiPouzit(nazev_wifi, id))
                        wifiPravidlo.setNazev_wifi(nazev_wifi);
                    else
                        lze = false;
                } else
                    validace += "název wifi, ";
                break;
        }

        return validace;
    }

    @Override
    public void setNazevWifi(String wifi) {
        WifiPravidloDao wifiPravidloDao = new WifiPravidloDaoImpl();
        if(wifi.length() != 0 && wifiPravidloDao.lzeNazevWifiPouzit(wifi, id)) {
            tvObsah.setText(wifi);
            nazev_wifi = wifi;
            lze = true;
        } else {
            if(id == 0) {
                tvObsah.setText(R.string.wifiObsah);
                nazev_wifi = "";
                lze = true;
            }
            if(wifi.length() != 0)
                Toast.makeText(getApplicationContext(), R.string.wifi_pouzita, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setNazevPravidla(String nazevPravidla) {
        if (nazevPravidla.length() != 0) {
            tvObsah.setText(nazevPravidla);
            nazev = nazevPravidla;
        } else {
            tvObsah.setText(R.string.nazevObsah);
            nazev = "";
        }
    }

    @Override
    public void setNazevUdalosti(String nazevUdalosti) {
        KalendarovePravidloDao kalendarovePravidloDao = new KalendarovePravidloDaoImpl();
        if (nazevUdalosti.length() != 0 && kalendarovePravidloDao.lzeNazevUdalostiPouzit(nazevUdalosti, id)) {
            tvObsah.setText(nazevUdalosti);
            udalost = nazevUdalosti;
            lze = true;
        } else {
            if(id == 0) {
                tvObsah.setText(R.string.udalostObsah);
                udalost = "";
                lze = true;
            }
            if(nazevUdalosti.length() != 0)
                Toast.makeText(getApplicationContext(), R.string.udalost_pouzita, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setCas(String cas, String parametr) {
        if(parametr.equals(ParametryPravidelAdapter.CAS_OD)) {
            tvObsah.setText(cas);
            cas_od = cas;
        } else {
            tvObsah.setText(cas);
            cas_do = cas;
        }
    }

    @Override
    public void setStav() {
        if(prepinac.isChecked()) {
            prepinac.setChecked(false);
            tvObsah.setText(R.string.neaktiv);
            stav = 0;
        } else {
            prepinac.setChecked(true);
            tvObsah.setText(R.string.aktiv);
            stav = 1;
        }
    }

    @Override
    public void setVibrace() {
        if(prepinac.isChecked()) {
            prepinac.setChecked(false);
            tvObsah.setText(R.string.ne);
            vibrace = 0;
        } else {
            prepinac.setChecked(true);
            tvObsah.setText(R.string.ano);
            vibrace = 1;
        }
    }

    @Override
    public void setDny(ArrayList<Integer> dnyPravidla) {
        vybraneDny = dnyPravidla;
        Collections.sort(vybraneDny);
        if (!vybraneDny.isEmpty()) {
            vypisNazvy = vypisDnuNazvy(vybraneDny);
            vypisZkratky = vypisDnuZkratky(vybraneDny);
            tvObsah.setText(vypisNazvy);
        } else {
            tvObsah.setText(R.string.denObsah);
        }
    }

    @Override
    public void smazPravidlo(int kat, int prav) {
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        switch (kategorie) {
            case "Časové pravidlo":
                CasovePravidloDao casovePravidloDao = new CasovePravidloDaoImpl();
                CasovePravidlo casovePravidlo = casovePravidloDao.getCasovePravidlo(id);
                casovePravidloDao.deleteCasovePravidlo(id);
                if (konfiguraceDao.getObsluhaPravidel() == 1 && konfiguraceDao.getDobaObnovy() == 0) {
                    long[] casoveUdaje = Utils.prevedTimeStringyNaMilisekundy(casovePravidlo.getCas_od(), casovePravidlo.getCas_do(), false);
                    long nyni = Calendar.getInstance().getTimeInMillis();
                    List<Den> dny = casovePravidlo.getDny();
                    Den den = Utils.zjistiAktualniDen();
                    if (Utils.jeDenObsazen(dny, den) && casoveUdaje[1] > nyni && casovePravidlo.getStav() == 1) {
                        boolean zapnoutZvuky;
                        if(casoveUdaje[0] <= nyni) {
                            zapnoutZvuky = Utils.potrebaZapnoutZvuky(getApplicationContext(), Calendar.getInstance(), 1);
                            PravidloDao pravidloDao = new PravidloDaoImpl();
                            if (zapnoutZvuky && pravidloDao.existujiAktivniPravidlaKategorie("Wifi pravidlo")) {
                                if (Utils.wifiZapnuta(getApplicationContext())) {
                                    zapnoutZvuky = Utils.velkyWifiTest(getApplicationContext());
                                }
                                Utils.enableDisableComponent(getApplicationContext(), WifiStateReceiver.class, true);
                            }
                        } else {
                            zapnoutZvuky = false;
                        }
                        Utils.zrusCasovace(getApplicationContext(), zapnoutZvuky);
                        Utils.nastavCasovace(getApplicationContext(), Calendar.getInstance());
                    }
                }
                break;
            case "Kalendářové pravidlo":
                KalendarovePravidloDao kalendarovePravidloDao = new KalendarovePravidloDaoImpl();
                KalendarovePravidlo kp = kalendarovePravidloDao.getKalendarovePravidlo(id);
                kalendarovePravidloDao.deleteKalendarovePravidlo(id);
                if (konfiguraceDao.getObsluhaPravidel() == 1 && konfiguraceDao.getDobaObnovy() == 0 && kp.getStav() == 1) {
                    PravidloDao pravidloDao = new PravidloDaoImpl();
                    if (pravidloDao.vratPocetAktivnichPravidelKategorie(kategorie) == 0) {
                        Utils.enableDisableComponent(getApplicationContext(), CalendarReceiver.class, false);
                    }

                    if(Utils.jeUdalostDnes(getApplicationContext(), kp.getUdalost())) {
                        boolean zapnoutZvuky;
                        if(!konfiguraceDao.getNazevObsluhovaneUdalosti().equals("") &&
                                konfiguraceDao.getNazevObsluhovaneUdalosti().toLowerCase().contains(kp.getUdalost().toLowerCase())) {
                            zapnoutZvuky = Utils.potrebaZapnoutZvuky(getApplicationContext(), Calendar.getInstance(), 1);
                            if (zapnoutZvuky && pravidloDao.existujiAktivniPravidlaKategorie("Wifi pravidlo")) {
                                if (Utils.wifiZapnuta(getApplicationContext())) {
                                    zapnoutZvuky = Utils.velkyWifiTest(getApplicationContext());
                                }
                                Utils.enableDisableComponent(getApplicationContext(), WifiStateReceiver.class, true);
                            }
                        } else {
                            zapnoutZvuky = false;
                        }
                        Utils.zrusCasovace(getApplicationContext(), zapnoutZvuky);
                        Utils.nastavCasovace(getApplicationContext(), Calendar.getInstance());
                    }
                }
                break;
            case "Wifi pravidlo":
                WifiPravidloDao wifiPravidloDao = new WifiPravidloDaoImpl();
                WifiPravidlo wifiPravidlo = wifiPravidloDao.getWifiPravidlo(id);
                wifiPravidloDao.deleteWifiPravidlo(id);
                if (konfiguraceDao.getObsluhaPravidel() == 1 && konfiguraceDao.getDobaObnovy() == 0 && konfiguraceDao.getCasoveNeboKalendarovePravidlo() == 0 && wifiPravidlo.getStav() == 1) {
                    PravidloDao pravidloDao = new PravidloDaoImpl();
                    int pocetAWP = pravidloDao.vratPocetAktivnichPravidelKategorie(kategorie);

                    if (pocetAWP == 0) {
                        if (konfiguraceDao.getWifiPravidlo() == 1)
                            konfiguraceDao.updateVykonavaSePravidlo(0);
                        Utils.zrusWifiCasovac(getApplicationContext(), true);
                        Utils.enableDisableComponent(getApplicationContext(), WifiStateReceiver.class, false);
                    } else if (pocetAWP != 0 && Utils.wifiZapnuta(getApplicationContext())) {
                        Utils.velkyWifiTest(getApplicationContext());
                    }
                }
                break;
        }

        Intent upIntent = new Intent(NovePravidloActivity.this, SeznamPravidelActivity.class);
        NavUtils.navigateUpTo(NovePravidloActivity.this, upIntent);
    }

    private List<String> zjistiParametryPravidla(Pravidlo pravidlo, String kategorie) {
        Field parametrySpolecne[] = pravidlo.getClass().getSuperclass().getDeclaredFields();
        Field parametryRuzne[] = pravidlo.getClass().getDeclaredFields();

        ArrayList<String> parametryVse = new ArrayList<>();
        int poziceStav = 0;
        int poziceVibrace = 0;

        for(int i=0;i<parametrySpolecne.length;i++){
            if(!parametrySpolecne[i].getName().equals("$change") & !parametrySpolecne[i].getName().equals("serialVersionUID")
                    & !parametrySpolecne[i].getName().equals("id_pravidlo") & !parametrySpolecne[i].getName().equals("kategorie")) {
                if(parametrySpolecne[i].getName().equals("stav"))
                    poziceStav = i;
                else if(parametrySpolecne[i].getName().equals("vibrace"))
                    poziceVibrace = i;
                else
                    parametryVse.add(parametrySpolecne[i].getName());
            }
        }

        if(kategorie.equals("Časové pravidlo")) {
            for (int i = 0; i < parametryRuzne.length; i++) {
                if (!parametryRuzne[i].getName().equals("$change") & !parametryRuzne[i].getName().equals("serialVersionUID")
                        & !parametryRuzne[i].getName().equals("cas_od_long") & !parametryRuzne[i].getName().equals("cas_do_long")
                        & !parametryRuzne[i].getName().equals("vypisDnuNazvy") & !parametryRuzne[i].getName().equals("vypisDnuZkratky")) {
                    if(parametryRuzne[i].getName().equals("dny")) {
                        parametryVse.add(1,parametryRuzne[i].getName());
                    } else if(parametryRuzne[i].getName().equals("cas_od")) {
                        parametryVse.add(1,parametryRuzne[i].getName());
                    } else
                        parametryVse.add(parametryRuzne[i].getName());
                }
            }
        } else {
            for (int i = 0; i < parametryRuzne.length; i++) {
                if (!parametryRuzne[i].getName().equals("$change") & !parametryRuzne[i].getName().equals("serialVersionUID")) {
                    parametryVse.add(parametryRuzne[i].getName());
                }
            }
        }

        parametryVse.add(parametrySpolecne[poziceVibrace].getName());
        parametryVse.add(parametrySpolecne[poziceStav].getName());

        return parametryVse;
    }

    private String vypisDnuNazvy(ArrayList<Integer> vybraneDny) {
        DenDao denDao = new DenDaoImpl();
        List<Den> dny = denDao.getDny();
        StringBuilder stringBuilder = new StringBuilder();
        if (vybraneDny.size() == 7) {
            stringBuilder = stringBuilder.append("Každý den");
        } else if (vybraneDny.size() == 5 & !vybraneDny.contains(5) & !vybraneDny.contains(6)) {
            stringBuilder = stringBuilder.append("Pracovní dny");
        } else if (vybraneDny.size() == 2 & vybraneDny.contains(5) & vybraneDny.contains(6)) {
            stringBuilder = stringBuilder.append("Víkend");
        } else {
            if(vybraneDny.size() > 2) {
                List<Integer> rozsahy = getRozsahy(vybraneDny);
                for (int i = 0; i < rozsahy.size(); i += 2) {
                    String rozsah;
                    if (rozsahy.get(i) == rozsahy.get(i + 1)) {
                        rozsah = dny.get(rozsahy.get(i)).getNazev();
                        stringBuilder = stringBuilder.append(rozsah + ", ");
                    } else if (rozsahy.get(i + 1) - rozsahy.get(i) > 1) {
                        rozsah = dny.get(rozsahy.get(i)).getNazev();
                        stringBuilder = stringBuilder.append(rozsah + " - ");
                        rozsah = dny.get(rozsahy.get(i + 1)).getNazev();
                        stringBuilder = stringBuilder.append(rozsah + ", ");
                    } else {
                        rozsah = dny.get(rozsahy.get(i)).getNazev();
                        stringBuilder = stringBuilder.append(rozsah + ", ");
                        rozsah = dny.get(rozsahy.get(i + 1)).getNazev();
                        stringBuilder = stringBuilder.append(rozsah + ", ");
                    }
                }
                stringBuilder = new StringBuilder().append(stringBuilder.substring(0,stringBuilder.length() - 2));
            } else {
                for (int i = 0; i < vybraneDny.size(); i++) {
                    String den = dny.get(vybraneDny.get(i)).getNazev();
                    if (i < vybraneDny.size() - 1)
                        stringBuilder = stringBuilder.append(den + ", ");
                    else
                        stringBuilder = stringBuilder.append(den);
                }
            }
        }
        return stringBuilder.toString();
    }

    private String vypisDnuZkratky(ArrayList<Integer> vybraneDny) {
        DenDao denDao = new DenDaoImpl();
        List<Den> dny = denDao.getDny();
        StringBuilder stringBuilder = new StringBuilder();
        if (vybraneDny.size() == 7) {
            stringBuilder = stringBuilder.append("Každý den");
        } else if (vybraneDny.size() == 5 & !vybraneDny.contains(5) & !vybraneDny.contains(6)) {
            stringBuilder = stringBuilder.append("Pracovní dny");
        } else if (vybraneDny.size() == 2 & vybraneDny.contains(5) & vybraneDny.contains(6)) {
            stringBuilder = stringBuilder.append("Víkend");
        } else {
            if(vybraneDny.size() > 2) {
                List<Integer> rozsahy = getRozsahy(vybraneDny);
                for(int i = 0;i < rozsahy.size();i += 2) {
                    String rozsah;
                    if(rozsahy.get(i) == rozsahy.get(i + 1)) {
                        rozsah = dny.get(rozsahy.get(i)).getZkratka();
                        stringBuilder = stringBuilder.append(rozsah + ", ");
                    } else if(rozsahy.get(i + 1) - rozsahy.get(i) > 1){
                        rozsah = dny.get(rozsahy.get(i)).getZkratka();
                        stringBuilder = stringBuilder.append(rozsah + " - ");
                        rozsah = dny.get(rozsahy.get(i + 1)).getZkratka();
                        stringBuilder = stringBuilder.append(rozsah + ", ");
                    } else {
                        rozsah = dny.get(rozsahy.get(i)).getZkratka();
                        stringBuilder = stringBuilder.append(rozsah + ", ");
                        rozsah = dny.get(rozsahy.get(i + 1)).getZkratka();
                        stringBuilder = stringBuilder.append(rozsah + ", ");
                    }
                }
                stringBuilder = new StringBuilder().append(stringBuilder.substring(0,stringBuilder.length() - 2));
            } else {
                for (int i = 0; i < vybraneDny.size(); i++) {
                    String den = dny.get(vybraneDny.get(i)).getZkratka();
                    if (i < vybraneDny.size() - 1)
                        stringBuilder = stringBuilder.append(den + ", ");
                    else
                        stringBuilder = stringBuilder.append(den);
                }
            }
        }
        return stringBuilder.toString();
    }

    private List<Integer> getRozsahy(List<Integer> dny) {
        List<Integer> rozsahy = new ArrayList<>();
        int start = dny.get(0);
        int konec = dny.get(0);
        for (int den : dny) {
            if (den - konec > 1) {
                rozsahy.add(start);
                rozsahy.add(konec);
                start = den;
            }
            konec = den;
        }
        rozsahy.add(start);
        rozsahy.add(konec);
        return rozsahy;
    }

    public List<Den> vratDnyPravidla(List<Integer> vybraneDny) {
        List<Integer> kolekceID = new ArrayList<>();

        for(int i = 0; i < vybraneDny.size(); i++) {
            kolekceID.add(vybraneDny.get(i) + 1);
        }

        List<Den> dnyPravidla = new ArrayList<>();
        Den den;
        DenDao denDao = new DenDaoImpl();
        for(int i = 0;i < kolekceID.size();i++) {
            den = denDao.getDenByID(kolekceID.get(i));
            dnyPravidla.add(den);
        }

        return dnyPravidla;
    }
}