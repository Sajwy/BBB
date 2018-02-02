package cz.sajwy.silencer.dao;

import java.util.List;

import cz.sajwy.silencer.model.WifiPravidlo;

public interface WifiPravidloDao {
    String createTable();
    String createIndex();
    WifiPravidlo getWifiPravidlo(int id);
    WifiPravidlo getAktivniWifiPravidloByWifi(String wifi);
    boolean obsazenoVAktivnich(String ssid);
    void insertWifiPravidlo(WifiPravidlo wifiPravidlo);
    void updateWifiPravidlo(WifiPravidlo novePravidlo);
    void deleteWifiPravidlo(int id);
    List<String> getWifiNazvy();
    boolean lzeNazevWifiPouzit(String nazev, int id);
}