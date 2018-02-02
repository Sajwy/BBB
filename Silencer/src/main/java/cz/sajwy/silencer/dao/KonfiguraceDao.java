package cz.sajwy.silencer.dao;

public interface KonfiguraceDao {
    String createTable();
    String insertData();
    void updateObsluhaPravidel(int obsluha);
    int getObsluhaPravidel();
    void updateVykonavaSePravidlo(int vykonavaSePravidlo);
    int getVykonavaSePravidlo();
    void updateCasoveNeboKalendarovePravidlo(int hodnota);
    int getCasoveNeboKalendarovePravidlo();
    void updateWifiPravidlo(int hodnota);
    int getWifiPravidlo();
    void updateNazevObsluhovaneUdalosti(String hodnota);
    String getNazevObsluhovaneUdalosti();
    void updateZmenaRezimuAplikaci(int hodnota);
    int getZmenaRezimuAplikaci();
    void updateDobaObnovy(long hodnota);
    long getDobaObnovy();
}