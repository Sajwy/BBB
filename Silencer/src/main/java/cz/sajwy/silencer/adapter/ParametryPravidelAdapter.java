package cz.sajwy.silencer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import cz.sajwy.silencer.R;
import cz.sajwy.silencer.dao.CasovePravidloDao;
import cz.sajwy.silencer.dao.KalendarovePravidloDao;
import cz.sajwy.silencer.dao.PravidloDao;
import cz.sajwy.silencer.dao.WifiPravidloDao;
import cz.sajwy.silencer.daoImpl.CasovePravidloDaoImpl;
import cz.sajwy.silencer.daoImpl.KalendarovePravidloDaoImpl;
import cz.sajwy.silencer.daoImpl.PravidloDaoImpl;
import cz.sajwy.silencer.daoImpl.WifiPravidloDaoImpl;
import cz.sajwy.silencer.model.CasovePravidlo;
import cz.sajwy.silencer.model.KalendarovePravidlo;
import cz.sajwy.silencer.model.Pravidlo;
import cz.sajwy.silencer.model.WifiPravidlo;

public class ParametryPravidelAdapter extends ArrayAdapter<String> {
    public static final String NAZEV = "nazev";
    public static final String STAV = "stav";
    public static final String VIBRACE = "vibrace";
    public static final String DNY = "dny";
    public static final String CAS_OD = "cas_od";
    public static final String CAS_DO = "cas_do";
    public static final String UDALOST = "udalost";
    public static final String WIFI = "nazev_wifi";

    private int idPravidla;
    private TextView tvNadpis;
    private TextView tvObsah;
    private Switch prepinac;
    private Pravidlo pravidlo;
    private CasovePravidlo casovePravidlo;
    private KalendarovePravidlo kalendarovePravidlo;
    private WifiPravidlo wifiPravidlo;

    public ParametryPravidelAdapter(Context context, List<String> parametry, int idPravidla) {
        super(context, 0, parametry);
        this.idPravidla = idPravidla;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            if(getItemViewType(position) == 0) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.lv_nove_pravidlo_tv, parent, false);
                tvNadpis = (TextView) convertView.findViewById(R.id.tvNadpis);
                tvObsah = (TextView) convertView.findViewById(R.id.tvObsah);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.lv_nove_pravidlo_switch, parent, false);
                tvNadpis = (TextView) convertView.findViewById(R.id.tvNadpis);
                tvObsah = (TextView) convertView.findViewById(R.id.tvObsah);
                prepinac = (Switch) convertView.findViewById(R.id.swID);
            }

            if(idPravidla > 0) {
                PravidloDao pravidloDao = new PravidloDaoImpl();
                pravidlo = pravidloDao.getPravidlo(idPravidla);
                switch (pravidlo.getKategorie().getNazev()) {
                    case "Časové pravidlo":
                        CasovePravidloDao casovePravidloDao = new CasovePravidloDaoImpl();
                        casovePravidlo = casovePravidloDao.getCasovePravidlo(idPravidla);
                        break;
                    case "Kalendářové pravidlo":
                        KalendarovePravidloDao kalendarovePravidloDao = new KalendarovePravidloDaoImpl();
                        kalendarovePravidlo = kalendarovePravidloDao.getKalendarovePravidlo(idPravidla);
                        break;
                    case "Wifi pravidlo":
                        WifiPravidloDao wifiPravidloDao = new WifiPravidloDaoImpl();
                        wifiPravidlo = wifiPravidloDao.getWifiPravidlo(idPravidla);
                        break;
                }
                switch (getItem(position)) {
                    case NAZEV:
                        tvNadpis.setText(R.string.nazevNadpis);
                        tvObsah.setText(pravidlo.getNazev());
                        break;
                    case VIBRACE:
                        tvNadpis.setText(R.string.vibraceNadpis);
                        if(pravidlo.getVibrace() == 1) {
                            tvObsah.setText(R.string.ano);
                            prepinac.setChecked(true);
                        } else {
                            tvObsah.setText(R.string.ne);
                            prepinac.setChecked(false);
                        }
                        break;
                    case STAV:
                        tvNadpis.setText(R.string.stavNadpis);
                        if(pravidlo.getStav() == 1) {
                            tvObsah.setText(R.string.aktiv);
                            prepinac.setChecked(true);
                        } else {
                            tvObsah.setText(R.string.neaktiv);
                            prepinac.setChecked(false);
                        }
                        break;
                    case DNY:
                        tvNadpis.setText(R.string.denNadpis);
                        CasovePravidloDao casovePravidloDao = new CasovePravidloDaoImpl();
                        tvObsah.setText(casovePravidloDao.getVypisDnuNazvy(casovePravidlo.getId_pravidlo()));
                        break;
                    case CAS_OD:
                        tvNadpis.setText(R.string.casOdNadpis);
                        tvObsah.setText(casovePravidlo.getCas_od());
                        break;
                    case CAS_DO:
                        tvNadpis.setText(R.string.casDoNadpis);
                        tvObsah.setText(casovePravidlo.getCas_do());
                        break;
                    case UDALOST:
                        tvNadpis.setText(R.string.udalostNadpis);
                        tvObsah.setText(kalendarovePravidlo.getUdalost());
                        break;
                    case WIFI:
                        tvNadpis.setText(R.string.wifiNadpis);
                        tvObsah.setText(wifiPravidlo.getNazev_wifi());
                        break;
                }
            } else {
                switch (getItem(position)) {
                    case NAZEV:
                        tvNadpis.setText(R.string.nazevNadpis);
                        tvObsah.setText(R.string.nazevObsah);
                        break;
                    case VIBRACE:
                        tvNadpis.setText(R.string.vibraceNadpis);
                        tvObsah.setText(R.string.ano);
                        prepinac.setChecked(true);
                        break;
                    case STAV:
                        tvNadpis.setText(R.string.stavNadpis);
                        tvObsah.setText(R.string.aktiv);
                        prepinac.setChecked(true);
                        break;
                    case DNY:
                        tvNadpis.setText(R.string.denNadpis);
                        tvObsah.setText(R.string.denObsah);
                        break;
                    case CAS_OD:
                        tvNadpis.setText(R.string.casOdNadpis);
                        tvObsah.setText(R.string.casOdObsah);
                        break;
                    case CAS_DO:
                        tvNadpis.setText(R.string.casDoNadpis);
                        tvObsah.setText(R.string.casDoObsah);
                        break;
                    case UDALOST:
                        tvNadpis.setText(R.string.udalostNadpis);
                        tvObsah.setText(R.string.udalostObsah);
                        break;
                    case WIFI:
                        tvNadpis.setText(R.string.wifiNadpis);
                        tvObsah.setText(R.string.wifiObsah);
                        break;
                }
            }
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        String parametr = getItem(position);
        if(parametr.equals(STAV) || parametr.equals(VIBRACE))
            return 1;
        else
            return 0;
    }
}