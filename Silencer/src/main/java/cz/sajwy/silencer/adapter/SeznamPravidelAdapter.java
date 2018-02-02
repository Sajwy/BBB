package cz.sajwy.silencer.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import cz.sajwy.silencer.R;
import cz.sajwy.silencer.callback.ExpLVCallback;
import cz.sajwy.silencer.callback.SmazatPravidloCallback;
import cz.sajwy.silencer.dao.CasovePravidloDao;
import cz.sajwy.silencer.dao.KalendarovePravidloDao;
import cz.sajwy.silencer.dao.KonfiguraceDao;
import cz.sajwy.silencer.dao.PravidloDao;
import cz.sajwy.silencer.dao.WifiPravidloDao;
import cz.sajwy.silencer.daoImpl.CasovePravidloDaoImpl;
import cz.sajwy.silencer.daoImpl.KalendarovePravidloDaoImpl;
import cz.sajwy.silencer.daoImpl.KonfiguraceDaoImpl;
import cz.sajwy.silencer.daoImpl.PravidloDaoImpl;
import cz.sajwy.silencer.daoImpl.WifiPravidloDaoImpl;
import cz.sajwy.silencer.dialog.SmazPravidloDialog;
import cz.sajwy.silencer.model.CasovePravidlo;
import cz.sajwy.silencer.model.Den;
import cz.sajwy.silencer.model.KalendarovePravidlo;
import cz.sajwy.silencer.model.Kategorie;
import cz.sajwy.silencer.model.Pravidlo;
import cz.sajwy.silencer.model.WifiPravidlo;
import cz.sajwy.silencer.receiver.CalendarReceiver;
import cz.sajwy.silencer.receiver.WifiStateReceiver;
import cz.sajwy.silencer.utils.Utils;

public class SeznamPravidelAdapter extends BaseExpandableListAdapter implements OnClickListener, OnCheckedChangeListener,SmazatPravidloCallback {
    private ExpLVCallback callback;
    private Activity context;
    private List<Kategorie> kategorie;
    private LayoutInflater inf;

    public SeznamPravidelAdapter(Activity context, List<Kategorie> kategorie, ExpLVCallback callback) {
        this.context = context;
        this.kategorie = kategorie;
        this.callback = callback;
        inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return kategorie.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<Pravidlo> pravidla = kategorie.get(groupPosition).getPravidla();
        return pravidla.size();
    }

    @Override
    public Kategorie getGroup(int groupPosition) {
        return kategorie.get(groupPosition);
    }

    @Override
    public Pravidlo getChild(int groupPosition, int childPosition) {
        List<Pravidlo> pravidla = kategorie.get(groupPosition).getPravidla();
        return pravidla.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private class GroupViewHolder
    {
        ImageView ivKategorie;
        TextView tvKategorie;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        Kategorie kategorie = getGroup(groupPosition);
        if (convertView == null) {
            convertView = inf.inflate(R.layout.explv_seznam_pravidel_group, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tvKategorie = (TextView) convertView.findViewById(R.id.tvKategorie);
            groupViewHolder.ivKategorie = (ImageView) convertView.findViewById(R.id.ivKategorie);

            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.tvKategorie.setText(kategorie.getNazev());
        switch (kategorie.getNazev()) {
            case "Časové pravidlo":
                groupViewHolder.ivKategorie.setImageResource(R.drawable.ic_clock);
                break;
            case "Kalendářové pravidlo":
                groupViewHolder.ivKategorie.setImageResource(R.drawable.ic_calendar);
                break;
            case "Wifi pravidlo":
                groupViewHolder.ivKategorie.setImageResource(R.drawable.ic_wifi);
                break;
        }

        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Pravidlo pravidlo = getChild(groupPosition, childPosition);

        convertView = inf.inflate(R.layout.explv_seznam_pravidel_child, null);

        TextView tvPravidlo = (TextView) convertView.findViewById(R.id.tvPravidlo);
        tvPravidlo.setText(pravidlo.getNazev());

        TextView tvHodnoty = (TextView) convertView.findViewById(R.id.tvHodnotyPravidla);
        tvHodnoty.setText(vratHodnotyPravidla(kategorie.get(groupPosition).getNazev(),kategorie.get(groupPosition).getPravidla().get(childPosition).getId_pravidlo()));

        int pozice[] = {groupPosition, childPosition};

        Switch switchStav = (Switch) convertView.findViewById(R.id.switchStav);
        switchStav.setTag(pozice);
        if (pravidlo.getStav() == 1)
            switchStav.setChecked(true);
        else
            switchStav.setChecked(false);
        switchStav.setOnCheckedChangeListener(this);

        ImageView delete = (ImageView) convertView.findViewById(R.id.icSmaz);
        delete.setTag(pozice);
        delete.setOnClickListener(this);

        return convertView;
    }

    private String vratHodnotyPravidla(String kategorie, int idPravidla) {
        String hodnoty = "";
        switch (kategorie) {
            case "Časové pravidlo":
                CasovePravidloDao casovePravidloDao = new CasovePravidloDaoImpl();
                CasovePravidlo cp = casovePravidloDao.getCasovePravidlo(idPravidla);
                hodnoty = cp.getVypisDnuZkratky() + "   " + cp.getCas_od() + " - " + cp.getCas_do();
                break;
            case "Kalendářové pravidlo":
                KalendarovePravidloDao kalendarovePravidloDao = new KalendarovePravidloDaoImpl();
                KalendarovePravidlo kp = kalendarovePravidloDao.getKalendarovePravidlo(idPravidla);
                hodnoty = kp.getUdalost();
                break;
            case "Wifi pravidlo":
                WifiPravidloDao wifiPravidloDao = new WifiPravidloDaoImpl();
                WifiPravidlo wp = wifiPravidloDao.getWifiPravidlo(idPravidla);
                hodnoty = wp.getNazev_wifi();
                break;
        }
        return hodnoty;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pozice[] = (int[]) buttonView.getTag();
        final int groupPosition = pozice[0];
        final int childPosition = pozice[1];

        Pravidlo pravidlo = getChild(groupPosition, childPosition);

        if (isChecked) {
            pravidlo.setStav(1);
        } else {
            pravidlo.setStav(0);
        }

        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        PravidloDao pravidloDao = new PravidloDaoImpl();
        pravidloDao.updateStavPravidla(pravidlo.getId_pravidlo(), pravidlo.getStav());
        Kategorie k = kategorie.get(groupPosition);
        switch (k.getNazev()) {
            case "Časové pravidlo":
                CasovePravidloDao casovePravidloDao = new CasovePravidloDaoImpl();
                CasovePravidlo casovePravidlo = casovePravidloDao.getCasovePravidlo(pravidlo.getId_pravidlo());
                if (konfiguraceDao.getObsluhaPravidel() == 1 && konfiguraceDao.getDobaObnovy() == 0) {
                    long[] casoveUdaje = Utils.prevedTimeStringyNaMilisekundy(casovePravidlo.getCas_od(), casovePravidlo.getCas_do(), false);
                    long nyni = Calendar.getInstance().getTimeInMillis();
                    List<Den> dny = casovePravidlo.getDny();
                    Den den = Utils.zjistiAktualniDen();
                    if (Utils.jeDenObsazen(dny, den) && casoveUdaje[1] > nyni) {
                        boolean zapnoutZvuky;
                        if(casoveUdaje[0] <= nyni && casovePravidlo.getStav() == 0) {
                            zapnoutZvuky = Utils.potrebaZapnoutZvuky(context, Calendar.getInstance(), 1);
                            if (zapnoutZvuky && pravidloDao.existujiAktivniPravidlaKategorie("Wifi pravidlo")) {
                                if (Utils.wifiZapnuta(context)) {
                                    zapnoutZvuky = Utils.velkyWifiTest(context);
                                }
                                Utils.enableDisableComponent(context, WifiStateReceiver.class, true);
                            }
                        } else {
                            zapnoutZvuky = false;
                        }
                        Utils.zrusCasovace(context, zapnoutZvuky);
                        Utils.nastavCasovace(context, Calendar.getInstance());
                    }
                }
                break;
            case "Kalendářové pravidlo":
                KalendarovePravidloDao kalendarovePravidloDao = new KalendarovePravidloDaoImpl();
                KalendarovePravidlo kalendarovePravidlo = kalendarovePravidloDao.getKalendarovePravidlo(pravidlo.getId_pravidlo());
                if (konfiguraceDao.getObsluhaPravidel() == 1 && konfiguraceDao.getDobaObnovy() == 0) {
                    int pocetAKP = pravidloDao.vratPocetAktivnichPravidelKategorie(k.getId_kategorie());
                    if (kalendarovePravidlo.getStav() == 0 && pocetAKP == 0) {
                        Utils.enableDisableComponent(context, CalendarReceiver.class, false);
                    } else if (kalendarovePravidlo.getStav() == 1 && pocetAKP == 1) {
                        Utils.enableDisableComponent(context, CalendarReceiver.class, true);
                    }

                    if(Utils.jeUdalostDnes(context, kalendarovePravidlo.getUdalost())) {
                        boolean zapnoutZvuky;
                        if(kalendarovePravidlo.getStav() == 0 && !konfiguraceDao.getNazevObsluhovaneUdalosti().equals("") &&
                                konfiguraceDao.getNazevObsluhovaneUdalosti().toLowerCase().contains(kalendarovePravidlo.getUdalost().toLowerCase())) {
                            zapnoutZvuky = Utils.potrebaZapnoutZvuky(context, Calendar.getInstance(), 1);
                            if (zapnoutZvuky && pravidloDao.existujiAktivniPravidlaKategorie("Wifi pravidlo")) {
                                if (Utils.wifiZapnuta(context)) {
                                    zapnoutZvuky = Utils.velkyWifiTest(context);
                                }
                                Utils.enableDisableComponent(context, WifiStateReceiver.class, true);
                            }
                        } else {
                            zapnoutZvuky = false;
                        }
                        Utils.zrusCasovace(context, zapnoutZvuky);
                        Utils.nastavCasovace(context, Calendar.getInstance());
                    }
                }
                break;
            case "Wifi pravidlo":
                if(konfiguraceDao.getObsluhaPravidel() == 1 && konfiguraceDao.getDobaObnovy() == 0 && konfiguraceDao.getCasoveNeboKalendarovePravidlo() == 0) {
                    WifiPravidloDao wifiPravidloDao = new WifiPravidloDaoImpl();
                    WifiPravidlo wifiPravidlo = wifiPravidloDao.getWifiPravidlo(pravidlo.getId_pravidlo());
                    int pocetAWP = pravidloDao.vratPocetAktivnichPravidelKategorie(k.getId_kategorie());

                    if(wifiPravidlo.getStav() == 0 && pocetAWP == 0) {
                        if(konfiguraceDao.getWifiPravidlo() == 1)
                            konfiguraceDao.updateVykonavaSePravidlo(0);
                        Utils.zrusWifiCasovac(context, true);
                        Utils.enableDisableComponent(context, WifiStateReceiver.class, false);
                    } else if(((wifiPravidlo.getStav() == 0 && pocetAWP > 0) || wifiPravidlo.getStav() == 1)) {
                        if(Utils.wifiZapnuta(context)) {
                            Utils.velkyWifiTest(context);
                        }

                        if(wifiPravidlo.getStav() == 1 && pocetAWP == 1) {
                            Utils.enableDisableComponent(context, WifiStateReceiver.class, true);
                        }
                    }
                }
                break;
        }

        k.setPravidla(pravidloDao.getPravidlaByKategorie(k.getId_kategorie()));
        notifyDataSetChanged();

        Utils.automatickeVypnutiObsluhy(context);
    }

    @Override
    public void onClick(View v) {
        int pozice[] = (int[]) v.getTag();
        final int groupPosition = pozice[0];
        final int childPosition = pozice[1];

        SmazPravidloDialog smazPravidloDialog = SmazPravidloDialog.newInstance(groupPosition, childPosition);
        smazPravidloDialog.setListener(SeznamPravidelAdapter.this);
        smazPravidloDialog.show(context.getFragmentManager(), "smazPravidloDialog");
    }

    @Override
    public void smazPravidlo(int kat, int prav) {
        Kategorie k = kategorie.get(kat);
        Pravidlo p = k.getPravidla().get(prav);
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        PravidloDao pravidloDao = new PravidloDaoImpl();

        switch (k.getNazev()) {
            case "Časové pravidlo":
                CasovePravidloDao casovePravidloDao = new CasovePravidloDaoImpl();
                CasovePravidlo casovePravidlo = casovePravidloDao.getCasovePravidlo(p.getId_pravidlo());
                casovePravidloDao.deleteCasovePravidlo(p.getId_pravidlo());
                if (konfiguraceDao.getObsluhaPravidel() == 1 && konfiguraceDao.getDobaObnovy() == 0) {
                    long[] casoveUdaje = Utils.prevedTimeStringyNaMilisekundy(casovePravidlo.getCas_od(), casovePravidlo.getCas_do(), false);
                    long nyni = Calendar.getInstance().getTimeInMillis();
                    List<Den> dny = casovePravidlo.getDny();
                    Den den = Utils.zjistiAktualniDen();
                    if (Utils.jeDenObsazen(dny, den) && casoveUdaje[1] > nyni && casovePravidlo.getStav() == 1) {
                        boolean zapnoutZvuky;
                        if(casoveUdaje[0] <= nyni) {
                            zapnoutZvuky = Utils.potrebaZapnoutZvuky(context, Calendar.getInstance(), 1);
                            if (zapnoutZvuky && pravidloDao.existujiAktivniPravidlaKategorie("Wifi pravidlo")) {
                                if (Utils.wifiZapnuta(context)) {
                                    zapnoutZvuky = Utils.velkyWifiTest(context);
                                }
                                Utils.enableDisableComponent(context, WifiStateReceiver.class, true);
                            }
                        } else {
                            zapnoutZvuky = false;
                        }
                        Utils.zrusCasovace(context, zapnoutZvuky);
                        Utils.nastavCasovace(context, Calendar.getInstance());
                    }
                }
                break;
            case "Kalendářové pravidlo":
                KalendarovePravidloDao kalendarovePravidloDao = new KalendarovePravidloDaoImpl();
                KalendarovePravidlo kp = kalendarovePravidloDao.getKalendarovePravidlo(p.getId_pravidlo());
                kalendarovePravidloDao.deleteKalendarovePravidlo(p.getId_pravidlo());
                if (konfiguraceDao.getObsluhaPravidel() == 1 && konfiguraceDao.getDobaObnovy() == 0 && kp.getStav() == 1) {
                    if (pravidloDao.vratPocetAktivnichPravidelKategorie(k.getId_kategorie()) == 0) {
                        Utils.enableDisableComponent(context, CalendarReceiver.class, false);
                    }

                    if(Utils.jeUdalostDnes(context, kp.getUdalost())) {
                        boolean zapnoutZvuky;
                        if(!konfiguraceDao.getNazevObsluhovaneUdalosti().equals("") &&
                                konfiguraceDao.getNazevObsluhovaneUdalosti().toLowerCase().contains(kp.getUdalost().toLowerCase())) {
                            zapnoutZvuky = Utils.potrebaZapnoutZvuky(context, Calendar.getInstance(), 1);
                            if (zapnoutZvuky && pravidloDao.existujiAktivniPravidlaKategorie("Wifi pravidlo")) {
                                if (Utils.wifiZapnuta(context)) {
                                    zapnoutZvuky = Utils.velkyWifiTest(context);
                                }
                                Utils.enableDisableComponent(context, WifiStateReceiver.class, true);
                            }
                        } else {
                            zapnoutZvuky = false;
                        }
                        Utils.zrusCasovace(context, zapnoutZvuky);
                        Utils.nastavCasovace(context, Calendar.getInstance());
                    }
                }
                break;
            case "Wifi pravidlo":
                WifiPravidloDao wifiPravidloDao = new WifiPravidloDaoImpl();
                WifiPravidlo wifiPravidlo = wifiPravidloDao.getWifiPravidlo(p.getId_pravidlo());
                wifiPravidloDao.deleteWifiPravidlo(p.getId_pravidlo());
                if (konfiguraceDao.getObsluhaPravidel() == 1 && konfiguraceDao.getDobaObnovy() == 0 && konfiguraceDao.getCasoveNeboKalendarovePravidlo() == 0 && wifiPravidlo.getStav() == 1) {
                    int pocetAWP = pravidloDao.vratPocetAktivnichPravidelKategorie(k.getId_kategorie());

                    if (pocetAWP == 0) {
                        if (konfiguraceDao.getWifiPravidlo() == 1)
                            konfiguraceDao.updateVykonavaSePravidlo(0);
                        Utils.zrusWifiCasovac(context, true);
                        Utils.enableDisableComponent(context, WifiStateReceiver.class, false);
                    } else if (pocetAWP != 0 && Utils.wifiZapnuta(context)) {
                        Utils.velkyWifiTest(context);
                    }
                }
                break;
        }

        k.getPravidla().remove(prav);

        if (k.getPravidla().isEmpty())
            callback.collapseGroup(kat);
        else
            notifyDataSetChanged();

        Toast.makeText(context, R.string.pravidloOdstraneno, Toast.LENGTH_SHORT).show();

        Utils.automatickeVypnutiObsluhy(context);
    }
}