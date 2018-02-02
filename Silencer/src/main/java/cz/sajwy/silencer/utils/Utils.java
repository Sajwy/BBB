package cz.sajwy.silencer.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.NetworkInfo.DetailedState;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.CalendarContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.sajwy.silencer.R;
import cz.sajwy.silencer.activity.SeznamPravidelActivity;
import cz.sajwy.silencer.activity.TransparentniActivity;
import cz.sajwy.silencer.dao.CasovePravidloDao;
import cz.sajwy.silencer.dao.DenDao;
import cz.sajwy.silencer.dao.IntentDao;
import cz.sajwy.silencer.dao.KalendarovePravidloDao;
import cz.sajwy.silencer.dao.KategorieDao;
import cz.sajwy.silencer.dao.KonfiguraceDao;
import cz.sajwy.silencer.dao.PravidloDao;
import cz.sajwy.silencer.dao.WifiPravidloDao;
import cz.sajwy.silencer.daoImpl.CasovePravidloDaoImpl;
import cz.sajwy.silencer.daoImpl.DenDaoImpl;
import cz.sajwy.silencer.daoImpl.IntentDaoImpl;
import cz.sajwy.silencer.daoImpl.KalendarovePravidloDaoImpl;
import cz.sajwy.silencer.daoImpl.KategorieDaoImpl;
import cz.sajwy.silencer.daoImpl.KonfiguraceDaoImpl;
import cz.sajwy.silencer.daoImpl.PravidloDaoImpl;
import cz.sajwy.silencer.daoImpl.WifiPravidloDaoImpl;
import cz.sajwy.silencer.model.CasovePravidlo;
import cz.sajwy.silencer.model.Den;
import cz.sajwy.silencer.model.Kategorie;
import cz.sajwy.silencer.model.Udalost;
import cz.sajwy.silencer.model.WifiPravidlo;
import cz.sajwy.silencer.receiver.AlarmReceiver;
import cz.sajwy.silencer.receiver.CalendarReceiver;
import cz.sajwy.silencer.receiver.NotificationReceiver;
import cz.sajwy.silencer.receiver.RingerModeReceiver;
import cz.sajwy.silencer.receiver.WifiStateReceiver;
import cz.sajwy.silencer.service.ObsluhaPravidelService;

public class Utils {
    //vytáhnutí událostí z kalendáře
    public static List<Udalost> zjistiVsechnyUdalosti(Context context, Calendar begin, String zjistovanaUdalost)
    {
        String[] CALENDAR_PROJECTION = new String[] { CalendarContract.Calendars.ACCOUNT_NAME };
        int PROJECTION_ACCOUNT_NAME_INDEX = 0;

        List<String> nazvyUdalosti;
        if(zjistovanaUdalost.length() > 0) {
            nazvyUdalosti = new ArrayList<>();
            nazvyUdalosti.add(zjistovanaUdalost);
        } else {
            KalendarovePravidloDao kalendarovePravidloDao = new KalendarovePravidloDaoImpl();
            nazvyUdalosti = kalendarovePravidloDao.getNazvyAktivnichUdalosti();
        }

        List<Udalost> vysledneUdalosti = new ArrayList<>();
        List<Udalost> kalendaroveUdalosti;

        Cursor calendarCursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"), CALENDAR_PROJECTION, null, null, null);
        while (calendarCursor.moveToNext())
        {
            String accountName = calendarCursor.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            kalendaroveUdalosti = zjistiKalendaroveUdalosti(context, accountName, begin, nazvyUdalosti);
            if(!kalendaroveUdalosti.isEmpty())
                vysledneUdalosti = vyfiltrujUdalosti(kalendaroveUdalosti, vysledneUdalosti);
        }
        calendarCursor.close();

        if(!vysledneUdalosti.isEmpty()) {
            vysledneUdalosti = seradUdalosti(vysledneUdalosti);
            vysledneUdalosti = vyfiltrujUdalosti(vysledneUdalosti, new ArrayList<Udalost>());
        }

        return vysledneUdalosti;
    }

    private static List<Udalost> vyfiltrujUdalosti(List<Udalost> kalendaroveUdalosti, List<Udalost> vysledneUdalosti) {
        boolean vlozit = true;
        Udalost testovana;
        Udalost testujici;
        for(int i = 0;i < kalendaroveUdalosti.size();i++) {
            testovana = kalendaroveUdalosti.get(i);
            if(!vysledneUdalosti.contains(testovana)) {
                testloop:
                for (int j = 0; j < vysledneUdalosti.size(); j++) {
                    testujici = vysledneUdalosti.get(j);

                    if (testovana.getZacatek() >= testujici.getZacatek() && testovana.getKonec() <= testujici.getKonec()) {
                        vlozit = false;
                        break testloop;
                    }
                }
                if (vlozit) {
                    vysledneUdalosti.add(testovana);
                } else {
                    vlozit = true;
                }
            }
        }
        return vysledneUdalosti;
    }

    //pomocná metoda ke zjistiVsechnyUdalosti..vytahuje události pro jednotlivé kalendáře
    private static List<Udalost> zjistiKalendaroveUdalosti(Context context, String accountName, Calendar begin, List<String> nazvyUdalosti)
    {
        String[] INSTANCE_PROJECTION = new String[] { CalendarContract.Instances.TITLE, CalendarContract.Instances.BEGIN,CalendarContract.Instances.END, CalendarContract.Instances.ALL_DAY};
        int PROJECTION_TITLE_INDEX = 0;
        int PROJECTION_BEGIN_INDEX = 1;
        int PROJECTION_END_INDEX = 2;
        int PROJECTION_ALL_DAY_INDEX = 3;

        List<Udalost> udalosti = new ArrayList<>();
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        beginTime.set(beginTime.get(Calendar.YEAR), beginTime.get(Calendar.MONTH), beginTime.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        endTime.set(beginTime.get(Calendar.YEAR), beginTime.get(Calendar.MONTH), beginTime.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        long startMillis = beginTime.getTimeInMillis();
        long endMillis = endTime.getTimeInMillis();

        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);

        String selection = "(((" + CalendarContract.Instances.END + " > ? AND " +
                                CalendarContract.Instances.START_DAY + " = ? AND " +
                                CalendarContract.Instances.BEGIN + " <= ? )" +
                                " OR (" + CalendarContract.Instances.START_DAY + " = ? AND " +
                                CalendarContract.Instances.END_DAY + " = ? AND " +
                                CalendarContract.Instances.END + " > ? AND " +
                                CalendarContract.Instances.END + " <= ? )) AND " +
                                CalendarContract.Calendars.ACCOUNT_NAME + " = ?)";

        Calendar predchoziDen = Calendar.getInstance();
        predchoziDen.set(predchoziDen.get(Calendar.YEAR), predchoziDen.get(Calendar.MONTH), predchoziDen.get(Calendar.DAY_OF_MONTH) - 1, 1, 1, 1);

        String[] selectionArgs = new String[] { begin.getTimeInMillis()+"", julianDay(Calendar.getInstance()), endMillis+"", julianDay(predchoziDen), julianDay(Calendar.getInstance()), begin.getTimeInMillis()+"", endMillis+"", accountName };
        Cursor cursor = context.getContentResolver().query(builder.build(), INSTANCE_PROJECTION, selection, selectionArgs, CalendarContract.Instances.BEGIN + " ASC, " + CalendarContract.Instances.END + " DESC ");

        while (cursor.moveToNext()) {
            Udalost u;
            String title;
            boolean vlozit = false;
            if(cursor.getString(PROJECTION_END_INDEX) != null && cursor.getString(PROJECTION_BEGIN_INDEX) != null) {
                title = cursor.getString(PROJECTION_TITLE_INDEX);
                testloop:
                for(int i = 0;i < nazvyUdalosti.size();i++) {
                    if(title.toLowerCase().contains(nazvyUdalosti.get(i).toLowerCase())) {
                        vlozit = true;
                        break testloop;
                    }
                }
                if (vlozit) {
                    if(cursor.getInt(PROJECTION_ALL_DAY_INDEX) != 1)
                        u = new Udalost(cursor.getString(PROJECTION_TITLE_INDEX), cursor.getLong(PROJECTION_BEGIN_INDEX), cursor.getLong(PROJECTION_END_INDEX));
                    else
                        u = new Udalost(cursor.getString(PROJECTION_TITLE_INDEX), startMillis, endMillis);

                    if(u.getZacatek() < begin.getTimeInMillis())
                        u.setZacatek(begin.getTimeInMillis());

                    udalosti.add(u);
                }
            }
        }
        cursor.close();

        if(!udalosti.isEmpty())
            udalosti = seradUdalosti(udalosti);

        return udalosti;
    }

    private static List<Udalost> seradUdalosti(List<Udalost> udalosti) {
        Collections.sort(udalosti, new Comparator<Udalost>() {
            @Override
            public int compare(Udalost u1, Udalost u2) {
                return new CompareToBuilder().append(u1.getZacatek(), u2.getZacatek()).append(u2.getKonec(), u1.getKonec()).toComparison();
            }
        });
        return udalosti;
    }

    //pomocná metoda ke zjistiKalendaroveUdalosti, která vrací juliánský den
    private static String julianDay(Calendar date) {
        int a = (14 - date.get(Calendar.MONTH) + 1) / 12;
        int y = date.get(Calendar.YEAR) + 4800 - a;
        int m = date.get(Calendar.MONTH) + 1 + 12 * a - 3;
        int jdn = date.get(Calendar.DAY_OF_MONTH) + (153 * m + 2)/5 + 365*y + y/4 - y/100 + y/400 - 32045;
        return jdn+"";
    }

    //převod událostí na časová pravidla
    public static List<CasovePravidlo> prevedUdalostiNaCasovaPravidla(List<Udalost> udalosti) {
        List<CasovePravidlo> casovaPravidla = new ArrayList<>();
        Den den = zjistiAktualniDen();
        List<Den> dny = new ArrayList<>();
        dny.add(den);
        CasovePravidlo pravidlo;
        Udalost u;
        KategorieDao kategorieDao = new KategorieDaoImpl();
        for(int i = 0;i < udalosti.size();i++) {
            u = udalosti.get(i);
            Kategorie k = kategorieDao.getKategorieByNazev("Kalendářové pravidlo");
            int vibrace = zjistiVibrace(u.getNazev());
            pravidlo = new CasovePravidlo(0,u.getNazev(),1,vibrace,k,dny,u.getZacatek(),u.getKonec());
            casovaPravidla.add(pravidlo);
        }
        return casovaPravidla;
    }

    //test při CRUD operacích kal. pravidla, zda bude toto pravidlo použito ještě dnes
    public static boolean jeUdalostDnes(Context context, String nazevUdalosti) {
        List<Udalost> udalosti = zjistiVsechnyUdalosti(context, Calendar.getInstance(), nazevUdalosti);
        boolean vysledek = false;
        if(udalosti.size() > 0) {
            Udalost u;
            testloop:
            for(int i = 0;i < udalosti.size();i++) {
                u = udalosti.get(i);
                if(u.getNazev().toLowerCase().contains(nazevUdalosti.toLowerCase())) {
                    vysledek = true;
                    break testloop;
                }
            }
        } else {
            vysledek = false;
        }
        return vysledek;
    }

    private static int zjistiVibrace(String titulekUdalosti) {
        int vibrace = 0;
        KalendarovePravidloDao kalendarovePravidloDao = new KalendarovePravidloDaoImpl();
        List<String> nazvyUdalosti = kalendarovePravidloDao.getNazvyAktivnichUdalosti();

        testloop:
        for(int i = 0;i < nazvyUdalosti.size();i++) {
            if(titulekUdalosti.toLowerCase().contains(nazvyUdalosti.get(i).toLowerCase())) {
                vibrace = kalendarovePravidloDao.getVibrace(nazvyUdalosti.get(i));
                break testloop;
            }
        }
        return vibrace;
    }

//**************************************************************************************************************
    //vytáhnutí časových pravidel
    public static List<CasovePravidlo> zjistiCasovaPravidlaDanehoDne(Calendar begin) {
        Den den = zjistiAktualniDen();
        CasovePravidlo predchoziCasovePravidlo = zjistiCasovePravidloZMinulehoDne(den, begin);
        CasovePravidloDao casovePravidloDao = new CasovePravidloDaoImpl();
        List<CasovePravidlo> casovaPravidla = casovePravidloDao.getCasovaPravidlaByDenAktualni(den);
        if(predchoziCasovePravidlo != null) {
            casovaPravidla.add(0, predchoziCasovePravidlo);
        }
        return vyfiltrujCasovaPravidla(casovaPravidla, begin);
    }

    private static List<CasovePravidlo> vyfiltrujCasovaPravidla(List<CasovePravidlo> pravidla, Calendar begin) {
        int pocetPravidel = pravidla.size();
        int pruchod = 0;
        int index = 0;
        while(pruchod < pocetPravidel){
            if(pravidla.get(index).getCas_do_long() > begin.getTimeInMillis()) {
                if(pravidla.get(index).getCas_od_long() < begin.getTimeInMillis())
                    pravidla.get(index).setCas_od_long(begin.getTimeInMillis());
                index++;
            } else {
                pravidla.remove(index);
            }
            pruchod++;
        }

        pravidla = seradCasovaPravidla(pravidla);

        List<CasovePravidlo> vyslednaPravidla = new ArrayList<>();
        boolean vlozit = true;
        CasovePravidlo testovane;
        CasovePravidlo testujici;
        for(int i = 0;i < pravidla.size();i++) {
            testovane = pravidla.get(i);
                testloop:
                for (int j = 0; j < vyslednaPravidla.size(); j++) {
                    testujici = vyslednaPravidla.get(j);
                    if (testovane.getCas_od_long() >= testujici.getCas_od_long() && testovane.getCas_do_long() <= testujici.getCas_do_long()) {
                        vlozit = false;
                        break testloop;
                    }
                }
                if (vlozit) {
                    vyslednaPravidla.add(testovane);
                } else {
                    vlozit = true;
                }
        }
        return vyslednaPravidla;
    }

    private static CasovePravidlo zjistiCasovePravidloZMinulehoDne(Den aktualniDen, Calendar begin) {
        List<CasovePravidlo> predchoziPravidla = new ArrayList<>();
        CasovePravidloDao casovePravidloDao = new CasovePravidloDaoImpl();
        List<CasovePravidlo> vsechnaPredchoziPravidla = casovePravidloDao.getCasovaPravidlaByDenPredchozi(aktualniDen);
        Calendar pulnoc = Calendar.getInstance();
        pulnoc.set(pulnoc.get(Calendar.YEAR), pulnoc.get(Calendar.MONTH), pulnoc.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        CasovePravidlo p;
        for(int i = 0;i < vsechnaPredchoziPravidla.size();i++) {
            p = vsechnaPredchoziPravidla.get(i);
            if(p.getCas_od_long() < pulnoc.getTimeInMillis() && pulnoc.getTimeInMillis() <= p.getCas_do_long() && begin.getTimeInMillis() < p.getCas_do_long()) {
                p.setCas_od_long(begin.getTimeInMillis());
                predchoziPravidla.add(p);
            }
        }
        if(predchoziPravidla.isEmpty()) {
            return null;
        } else {
            predchoziPravidla = seradCasovaPravidla(predchoziPravidla);
            return predchoziPravidla.get(0);
        }
    }

    private static List<CasovePravidlo> seradCasovaPravidla(List<CasovePravidlo> pravidla) {
        Collections.sort(pravidla, new Comparator<CasovePravidlo>() {
            @Override
            public int compare(CasovePravidlo cp1, CasovePravidlo cp2) {
                return new CompareToBuilder().append(cp1.getCas_od_long(), cp2.getCas_od_long()).append(cp2.getCas_do_long(), cp1.getCas_do_long()).toComparison();
            }
        });
        return pravidla;
    }

    public static long[] prevedTimeStringyNaMilisekundy(String cas_od, String cas_do, boolean predchoziDen) {
        long[] milisekundyPole = new long[2];

        int[] casOdPole = rozdelTimeString(cas_od);
        int hodZacatek = casOdPole[0];
        int minZacatek = casOdPole[1];
        Calendar zacatek = Calendar.getInstance();
        if(predchoziDen)
            zacatek.set(zacatek.get(Calendar.YEAR), zacatek.get(Calendar.MONTH), zacatek.get(Calendar.DAY_OF_MONTH) - 1, hodZacatek, minZacatek, 0);
        else
            zacatek.set(zacatek.get(Calendar.YEAR), zacatek.get(Calendar.MONTH), zacatek.get(Calendar.DAY_OF_MONTH), hodZacatek, minZacatek, 0);

        milisekundyPole[0] = zacatek.getTimeInMillis();
        milisekundyPole[1] = urciCasKonce(cas_do, zacatek);
        return milisekundyPole;
    }

    public static long urciCasKonce(String cas, Calendar zacatek) {
        int hodZacatek = zacatek.get(Calendar.HOUR_OF_DAY);
        int minZacatek = zacatek.get(Calendar.MINUTE);

        int[] casDoPole = rozdelTimeString(cas);

        int hodKonec = casDoPole[0];
        int minKonec = casDoPole[1];

        int preteceniDne = 0;
        if(hodKonec < hodZacatek || (hodKonec == hodZacatek && (minKonec < minZacatek || minKonec == minZacatek)))
            preteceniDne = 1;

        Calendar calendar = Calendar.getInstance();
        calendar.set(zacatek.get(Calendar.YEAR), zacatek.get(Calendar.MONTH),zacatek.get(Calendar.DAY_OF_MONTH) + preteceniDne,
                hodKonec, minKonec, 0);

        return calendar.getTimeInMillis();
    }

    public static int[] rozdelTimeString(String timeString) {
        String[] castiRetezce = timeString.split("\\:");
        int hodiny = Integer.parseInt(castiRetezce[0]);
        int minuty = Integer.parseInt(castiRetezce[1]);
        int rozdelenyCas[] = {hodiny, minuty};
        return rozdelenyCas;
    }

    public static Den zjistiAktualniDen() {
        SimpleDateFormat czDenFormat = new SimpleDateFormat("EEEE", new Locale("cs", "CZ"));
        String denLowerString = czDenFormat.format(new Date());
        String denString = denLowerString.substring(0,2).toUpperCase();
        DenDao denDao = new DenDaoImpl();
        return denDao.getDenByZkratka(denString);
    }

    public static boolean jeDenObsazen(List<Den> dny, Den den) {
        boolean obsazen = false;
        Den d;
        for(int i = 0;i < dny.size();i++) {
            d = dny.get(i);
            if(d.getId_den() == den.getId_den()) {
                obsazen = true;
            }
        }
        return obsazen;
    }

    //******************************************************************************************************************
    //spojení časových pravidel s převedenými událostmi na časová pravidla
    private static List<CasovePravidlo> spojKolekcePravidel(List<CasovePravidlo> casovaPravidla, List<CasovePravidlo> udalosti) {
        List<CasovePravidlo> vyslednaKolekce = new ArrayList<>();
        vyslednaKolekce.addAll(casovaPravidla);
        vyslednaKolekce.addAll(udalosti);
        return vyslednaKolekce;
    }

    //******************************************************************************************************************
    //vrácení výsledné spojené kolekce podle které se bude obsluhovat servica
    public static List<CasovePravidlo> vratVsechnaCasovaPravidla(Context context, Calendar zacatek) {
        List<CasovePravidlo> vyslednaPravidla = new ArrayList<>();
        PravidloDao pravidloDao = new PravidloDaoImpl();
        boolean existujiCP = pravidloDao.existujiAktivniPravidlaKategorie("Časové pravidlo");
        boolean existujiKP = pravidloDao.existujiAktivniPravidlaKategorie("Kalendářové pravidlo");

        if(existujiCP && existujiKP) {
            List<CasovePravidlo> cp = zjistiCasovaPravidlaDanehoDne(zacatek);
            List<CasovePravidlo> kp = prevedUdalostiNaCasovaPravidla(zjistiVsechnyUdalosti(context, zacatek, ""));

            if(!cp.isEmpty() && !kp.isEmpty()) {
                List<CasovePravidlo> pravidla = spojKolekcePravidel(cp, kp);
                vyslednaPravidla = vyfiltrujCasovaPravidla(seradCasovaPravidla(pravidla), zacatek);
            } else if(!cp.isEmpty()) {
                vyslednaPravidla = cp;
            } else if(!kp.isEmpty()) {
                vyslednaPravidla = kp;
            }
        } else if(existujiCP) {
            vyslednaPravidla = zjistiCasovaPravidlaDanehoDne(zacatek);
        } else if(existujiKP) {
            vyslednaPravidla = prevedUdalostiNaCasovaPravidla(zjistiVsechnyUdalosti(context, zacatek, ""));
        }
        return vyslednaPravidla;
    }

    //****************************************************************************************
    //nastavení časovačů
    public static void nastavCasovace(Context context, Calendar dobaKdy) {
        List<CasovePravidlo> pravidla = vratVsechnaCasovaPravidla(context, dobaKdy);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int id = 2;
        CasovePravidlo casovePravidlo;
        Intent intent;
        PendingIntent pendingIntent;

        Calendar pulnoc = urciCas(1,0,0,0);
        intent = new Intent("ALARM");
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.putExtra("id", id);
        pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, pulnoc.getTimeInMillis(), pendingIntent);
        IntentDao intentDao = new IntentDaoImpl();
        intentDao.insert(id);

        if(!pravidla.isEmpty()) {
            id++;

            String nazev;
            for (int i = 0; i < pravidla.size(); i++) {
                casovePravidlo = pravidla.get(i);

                if (casovePravidlo.getCas_od_long() <= new Date().getTime()) {
                    KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
                    konfiguraceDao.updateCasoveNeboKalendarovePravidlo(1);
                    if (casovePravidlo.getId_pravidlo() == 0) {
                        konfiguraceDao.updateNazevObsluhovaneUdalosti(casovePravidlo.getNazev());
                        KalendarovePravidloDao kalendarovePravidloDao = new KalendarovePravidloDaoImpl();
                        nazev = kalendarovePravidloDao.getNazevPravidlaByUdalost(casovePravidlo.getNazev());
                    } else {
                        konfiguraceDao.updateNazevObsluhovaneUdalosti("");
                        nazev = casovePravidlo.getNazev();
                    }
                    if (casovePravidlo.getVibrace() == 1)
                        nastavRezimZvuku(context, "vibrace", nazev);
                    else
                        nastavRezimZvuku(context, "ticho", nazev);
                } else {
                    intent = new Intent("ALARM");
                    intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                    intent.putExtra("id", id);
                    if (casovePravidlo.getVibrace() == 1)
                        intent.putExtra("rezim", "vibrace");
                    else
                        intent.putExtra("rezim", "ticho");
                    if (casovePravidlo.getId_pravidlo() == 0) {
                        KalendarovePravidloDao kalendarovePravidloDao = new KalendarovePravidloDaoImpl();
                        intent.putExtra("nazev", kalendarovePravidloDao.getNazevPravidlaByUdalost(casovePravidlo.getNazev()));
                        intent.putExtra("udalost", casovePravidlo.getNazev());
                    } else {
                        intent.putExtra("nazev", casovePravidlo.getNazev());
                        intent.putExtra("udalost", "");
                    }
                    pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_ONE_SHOT);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, casovePravidlo.getCas_od_long(), pendingIntent);
                    intentDao.insert(id);
                }

                id++;

                intent = new Intent("ALARM");
                intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                intent.putExtra("id", id);
                intent.putExtra("nazev", context.getString(R.string.obsluha_spustena));
                intent.putExtra("rezim", "normal");
                pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_ONE_SHOT);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, casovePravidlo.getCas_do_long(), pendingIntent);
                intentDao.insert(id);
                id++;
            }
        }
    }

    //cancel pendingIntentu
    public static void zrusCasovace(Context context, boolean zapnoutZvuky) {
        zapnoutZvuky(context, zapnoutZvuky);
        
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        if(konfiguraceDao.getCasoveNeboKalendarovePravidlo() == 1)
            konfiguraceDao.updateVykonavaSePravidlo(0);

        IntentDao intentDao = new IntentDaoImpl();
        List<Integer> ids = intentDao.getAllCpIntents();

        if(!ids.isEmpty()) {
            int id;
            for(int i = 0;i < ids.size();i++) {
                id = ids.get(i);
                zrusAlarmPendingIntent(context, id);
            }
            intentDao.deleteAllCpIntents();
        }
    }

    public static Calendar urciCas(int prirustekDnu, int hod, int min, int sek) {
        Calendar zacatek = Calendar.getInstance();
        zacatek.set(zacatek.get(Calendar.YEAR), zacatek.get(Calendar.MONTH), zacatek.get(Calendar.DAY_OF_MONTH) + prirustekDnu, hod, min, sek);
        return zacatek;
    }

    public static boolean potrebaZapnoutZvuky(Context context, Calendar kdy, int pocetProPodminku) {
        List<CasovePravidlo> pravidla = vratVsechnaCasovaPravidla(context, kdy);
        int pocet = 0;
        for(int i = 0;i < pravidla.size();i++) {
            CasovePravidlo cp = pravidla.get(i);
            if(cp.getCas_od_long() <= kdy.getTimeInMillis() && kdy.getTimeInMillis() < cp.getCas_do_long())
                pocet++;
        }

        if(pocet < pocetProPodminku)
            return true;
        else
            return false;
    }

    public static void zapnoutZvuky(Context context, boolean zapnout) {
        if(zapnout && !zvukyZapnuty(context)) {
            enableDisableComponent(context, RingerModeReceiver.class, false);
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            Utils.enableDisableComponent(context, RingerModeReceiver.class, true);
            vytvorStickyNotifikaci(context, "pause", context.getString(R.string.obsluha_spustena));
        }
    }

    public static void zapnoutZvuky(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    public static boolean zvukyZapnuty(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
            return true;
        else
            return false;
    }

    public static List<String> vratNazvyWifinList(WifiManager wifiManager) {
        List<String> vyslednaKolekce = new ArrayList<>();

        List<ScanResult> listScan = wifiManager.getScanResults();
        if(!listScan.isEmpty()) {
            List<String> dostupne = vratListSSIDStringu(listScan);

            for(String nazev : dostupne) {
                vyslednaKolekce.add(nazev);
            }
        }

        List<WifiConfiguration> listConfig = wifiManager.getConfiguredNetworks();
        if(!listConfig.isEmpty()) {

            List<String> ulozene = vratListSSIDStringu(listConfig);

            for (String nazev : ulozene) {
                if (!vyslednaKolekce.contains(nazev)) {
                    vyslednaKolekce.add(nazev);
                }
            }
        }

        return vyslednaKolekce;
    }

    private static List<String> vratListSSIDStringu(List<?> list) {
        List<String> vyslednaKolekce = new ArrayList<>();

        if(zjistiTridu(list).equals(ScanResult.class.toString())) {
            for(int i = 0; i < list.size();i++) {
                vyslednaKolekce.add(((ScanResult)list.get(i)).SSID);
            }
        } else if (zjistiTridu(list).equals(WifiConfiguration.class.toString())) {
            String s;
            for(int i =0; i < list.size();i++) {
                s = vratSpravneSSID(((WifiConfiguration)list.get(i)).SSID);
                vyslednaKolekce.add(s);
            }
        }

        Collections.sort(vyslednaKolekce, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        return vyslednaKolekce;
    }

    private static String zjistiTridu(List<?> list) {
        return list.get(0).getClass().toString();
    }

    //obsluha wifi***************************************************************
    public static WifiPravidlo nachazimSeVDosahuWifi(Context context) {
        List<ScanResult> dostupneWifi = vratVysledkyScanuPodleSilySignalu(context);

        WifiPravidloDao wifiPravidloDao = new WifiPravidloDaoImpl();
        List<String> dbWifi = wifiPravidloDao.getWifiNazvy();

        WifiPravidlo wifiPravidlo = null;

        if(!dostupneWifi.isEmpty() && !dbWifi.isEmpty()) {
            testloop:
            for (int i = 0; i < dostupneWifi.size(); i++) {
                if(dbWifi.contains(dostupneWifi.get(i).SSID)) {
                    wifiPravidlo = wifiPravidloDao.getAktivniWifiPravidloByWifi(dostupneWifi.get(i).SSID);
                    break testloop;
                }
            }
        }

        return wifiPravidlo;
    }

    public static List<ScanResult> vratVysledkyScanuPodleSilySignalu(Context context) {
        final WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

        Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return wifiManager.compareSignalLevel(rhs.level, lhs.level);
            }
        };

        List<ScanResult> vysledkyScanu = wifiManager.getScanResults();

        Collections.sort(vysledkyScanu, comparator);

        return vysledkyScanu;
    }

    public static boolean wifiZapnuta(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public static void nastavWifiCasovac(Context context, double intervalVMinutach) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("ALARM");
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.putExtra("id", 1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        int prirustekIntervalu = (int) (intervalVMinutach * 1000 * 60);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + prirustekIntervalu, pendingIntent);
        IntentDao intentDao = new IntentDaoImpl();
        intentDao.insert(1);
    }

    public static void zrusWifiCasovac(Context context, boolean zapnoutZvuky) {
        zapnoutZvuky(context, zapnoutZvuky);
        
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        if(konfiguraceDao.getWifiPravidlo() == 1)
            konfiguraceDao.updateVykonavaSePravidlo(0);

        zrusAlarmPendingIntent(context, 1);
    }

    public static void zrusAlarmPendingIntent(Context context, int id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("ALARM");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,id,intent,PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
        IntentDao intentDao = new IntentDaoImpl();
        intentDao.delete(id);
    }

    //----------------------------------------------------------------------------------
    public static void nastavRezimZvuku(Context context, String rezim, String popisekNotifikace) {
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        konfiguraceDao.updateZmenaRezimuAplikaci(1);

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        String puvodniRezim = "";
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_VIBRATE:
                puvodniRezim = "vibrace";
                break;
            case AudioManager.RINGER_MODE_SILENT:
                puvodniRezim = "ticho";
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                puvodniRezim = "normal";
                break;
        }

        switch (rezim) {
            case "vibrace":
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                konfiguraceDao.updateVykonavaSePravidlo(1);
                break;
            case "ticho":
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                konfiguraceDao.updateVykonavaSePravidlo(1);
                break;
            case "normal":
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                konfiguraceDao.updateVykonavaSePravidlo(0);
                break;
        }

        vytvorStickyNotifikaci(context, "pause", popisekNotifikace);

        if(puvodniRezim.equals(rezim))
            konfiguraceDao.updateZmenaRezimuAplikaci(0);
    }

    public static WifiInfo pripojenoKWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        DetailedState state = wifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
        if(state == DetailedState.CONNECTED || state == DetailedState.OBTAINING_IPADDR) {
            return wifiInfo;
        } else {
            return null;
        }
    }

    public static String vratSpravneSSID(String spatneSSID) {
        if(spatneSSID.startsWith("\"") && spatneSSID.endsWith("\"")) {
            return spatneSSID.substring(1 ,spatneSSID.length() - 1);
        }
        return spatneSSID;
    }

    public static void enableDisableComponent(Context context, Class<?> trida, boolean enable) {
        ComponentName komponenta = new ComponentName(context, trida);
        PackageManager pm = context.getPackageManager();
        if(enable)
            pm.setComponentEnabledSetting(komponenta,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
        else
            pm.setComponentEnabledSetting(komponenta,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
    }

    public static boolean malyWifiTest(Context context) {
        WifiPravidlo wifiPravidlo = nachazimSeVDosahuWifi(context);
        if (wifiPravidlo != null) {
            zrusWifiCasovac(context, false);
            nastavWifiCasovac(context, 5);
            KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
            konfiguraceDao.updateWifiPravidlo(1);
            String rezim;
            if (wifiPravidlo.getVibrace() == 1)
                rezim = "vibrace";
            else
                rezim = "ticho";
            nastavRezimZvuku(context, rezim, wifiPravidlo.getNazev());
        } else {
            if(!zvukyZapnuty(context)) {
                nastavRezimZvuku(context, "normal", context.getString(R.string.obsluha_spustena));
            }
            zrusWifiCasovac(context, false);
            nastavWifiCasovac(context, 15);
        }

        return false;
    }

    public static boolean velkyWifiTest(Context context) {
        WifiInfo info = pripojenoKWifi(context);
        WifiPravidlo wifiPravidlo;
        WifiPravidloDao wifiPravidloDao = new WifiPravidloDaoImpl();
        if(info != null && wifiPravidloDao.obsazenoVAktivnich(vratSpravneSSID(info.getSSID()))) {
            zrusWifiCasovac(context, false);
            KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
            konfiguraceDao.updateWifiPravidlo(1);
            wifiPravidlo = wifiPravidloDao.getAktivniWifiPravidloByWifi(vratSpravneSSID(info.getSSID()));
            String rezim;
            if (wifiPravidlo.getVibrace() == 1)
                rezim = "vibrace";
            else
                rezim = "ticho";
            nastavRezimZvuku(context, rezim, wifiPravidlo.getNazev());
        } else {
            malyWifiTest(context);
        }

        return false;
    }

    public static void zapnoutObsluhuPravidel(Context context) {
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        konfiguraceDao.updateObsluhaPravidel(1);

        zrusNotifikaci(context, JEDNORAZOVA_NOTIFIKACE_ID);
        vytvorStickyNotifikaci(context, "pause", context.getString(R.string.obsluha_spustena));

        enableDisableComponent(context, AlarmReceiver.class, true);

        context.startService(new Intent(context, ObsluhaPravidelService.class));
    }

    public static void obnovitObsluhuPravidel(Context context) {
        zrusAlarmPendingIntent(context, 0);
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        konfiguraceDao.updateDobaObnovy(0);

        vytvorStickyNotifikaci(context, "pause", context.getString(R.string.obsluha_spustena));
        zrusNotifikaci(context, JEDNORAZOVA_NOTIFIKACE_ID);

        context.startService(new Intent(context, ObsluhaPravidelService.class));
    }

    public static void provedTestPravidel(Context context) {
        enableDisableComponent(context, RingerModeReceiver.class, true);
        nastavCasovace(context, Calendar.getInstance());
        PravidloDao pravidloDao = new PravidloDaoImpl();
        if(pravidloDao.existujiAktivniPravidlaKategorie("Kalendářové pravidlo"))
            enableDisableComponent(context, CalendarReceiver.class, true);
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        if(konfiguraceDao.getVykonavaSePravidlo() == 0 && pravidloDao.existujiAktivniPravidlaKategorie("Wifi pravidlo")) {
            if(wifiZapnuta(context)) {
                velkyWifiTest(context);
            }
            //tady ještě zaregistrování wifistatereceiveru
            enableDisableComponent(context, WifiStateReceiver.class, true);
        }
        if(konfiguraceDao.getVykonavaSePravidlo() == 0 && !zvukyZapnuty(context))
            zapnoutZvuky(context, true);
    }

    public static void vypnoutObsluhuPravidel(Context context) {
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        konfiguraceDao.updateObsluhaPravidel(0);

        context.stopService(new Intent(context, ObsluhaPravidelService.class));

        zrusCasovace(context, false);
        zrusWifiCasovac(context, false);

        //odregistrování receiveru
        enableDisableComponent(context, WifiStateReceiver.class, false);
        enableDisableComponent(context, CalendarReceiver.class, false);
        enableDisableComponent(context, RingerModeReceiver.class, false);
        enableDisableComponent(context, AlarmReceiver.class, false);

        zapnoutZvuky(context);

        zrusNotifikaci(context, JEDNORAZOVA_NOTIFIKACE_ID);
        zrusNotifikaci(context, STICKY_NOTIFIKACE_ID);
    }

    public static void pozastavitObsluhuPravidel(Context context, long dobaObnovy) {
        vytvorStickyNotifikaci(context, "play", context.getString(R.string.obsluha_pozastavena));

        enableDisableComponent(context, WifiStateReceiver.class, false);
        enableDisableComponent(context, CalendarReceiver.class, false);
        enableDisableComponent(context, RingerModeReceiver.class, false);

        zrusCasovace(context, false);
        zrusWifiCasovac(context, false);

        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        if(konfiguraceDao.getVykonavaSePravidlo() == 1)
            konfiguraceDao.updateVykonavaSePravidlo(0);

        nastavDobuObnovy(context, dobaObnovy);
    }

    private static void nastavDobuObnovy(Context context, long dobaObnovy) {
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        konfiguraceDao.updateDobaObnovy(dobaObnovy);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("ALARM");
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.putExtra("id", 0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, dobaObnovy, pendingIntent);
        IntentDao intentDao = new IntentDaoImpl();
        intentDao.insert(0);
    }

    public static void automatickeVypnutiObsluhy(Context context) {
        KonfiguraceDao konfiguraceDao = new KonfiguraceDaoImpl();
        PravidloDao pravidloDao = new PravidloDaoImpl();
        if(konfiguraceDao.getObsluhaPravidel() == 1 && !pravidloDao.existujiAktivniPravidla()) {
            vypnoutObsluhuPravidel(context);
            vytvorJednorazovouNotifikaci(context, context.getString(R.string.autovypnuti_obsluhy));
        }
    }

    public static final int STICKY_NOTIFIKACE_ID = 1;
    public static void vytvorStickyNotifikaci(Context context, String pausePlay, String popisek) {
        enableDisableComponent(context, NotificationReceiver.class, true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.custom_notification_action);
        remoteViews.setImageViewResource(R.id.ivIkona, R.mipmap.ic_launcher);
        remoteViews.setTextViewText(R.id.tvNadpis, context.getString(R.string.app_name));
        remoteViews.setTextViewText(R.id.tvPopisek, popisek);
        remoteViews.setImageViewResource(R.id.ivStop, R.drawable.ic_stop);

        Intent intentPausePlay;
        PendingIntent piPausePlay;
        if(pausePlay.equals("pause")) {
            remoteViews.setImageViewResource(R.id.ivPausePlay, R.drawable.ic_pause);//pause
            intentPausePlay = new Intent(context, TransparentniActivity.class);
            intentPausePlay.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            piPausePlay = PendingIntent.getActivity(context, STICKY_NOTIFIKACE_ID, intentPausePlay, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            remoteViews.setImageViewResource(R.id.ivPausePlay, R.drawable.ic_play);
            intentPausePlay = new Intent("OBSLUHA_POKRACOVAT");
            piPausePlay = PendingIntent.getBroadcast(context, STICKY_NOTIFIKACE_ID, intentPausePlay, PendingIntent.FLAG_ONE_SHOT);
        }
        remoteViews.setOnClickPendingIntent(R.id.ivPausePlay, piPausePlay);

        Intent intentStop = new Intent("OBSLUHA_STOP");
        PendingIntent piStop = PendingIntent.getBroadcast(context, STICKY_NOTIFIKACE_ID, intentStop, PendingIntent.FLAG_ONE_SHOT);
        remoteViews.setOnClickPendingIntent(R.id.ivStop, piStop);

        Intent intentSeznam = new Intent(context, SeznamPravidelActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, STICKY_NOTIFIKACE_ID, intentSeznam, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent);

        Notification n = builder.build();
        n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        notificationManager.notify(STICKY_NOTIFIKACE_ID, n);
    }

    public static void zrusNotifikaci(Context context, int id) {
        if(id == STICKY_NOTIFIKACE_ID)
            enableDisableComponent(context, NotificationReceiver.class, false);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.cancel(id);
    }

    public static final int JEDNORAZOVA_NOTIFIKACE_ID = 0;
    public static void vytvorJednorazovouNotifikaci(Context context, String popisek) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.custom_notification);
        remoteViews.setImageViewResource(R.id.ivIkona, R.mipmap.ic_launcher);
        remoteViews.setTextViewText(R.id.tvNadpis, context.getString(R.string.app_name));
        remoteViews.setTextViewText(R.id.tvPopisek, popisek);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContent(remoteViews);
        builder.setDefaults(Notification.DEFAULT_SOUND);

        if(popisek.equals(context.getString(R.string.autopauznuti_obsluhy))) {
            Intent intent = new Intent(context, TransparentniActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, JEDNORAZOVA_NOTIFIKACE_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
        }

        notificationManager.notify(JEDNORAZOVA_NOTIFIKACE_ID, builder.build());
    }
}