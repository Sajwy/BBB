package cz.sajwy.silencer.dao;

import java.util.ArrayList;
import java.util.List;

import cz.sajwy.silencer.model.CasovePravidlo;
import cz.sajwy.silencer.model.Den;

public interface CasovePravidloDao {
    String createTable();
    String createIndex();
    void insertCasovePravidlo(CasovePravidlo casovePravidlo);
    String getVypisDnuNazvy(int id);
    String getVypisDnuZkratky(int id);
    void updateVypisyDnu(int id, String vypisNazvy, String vypisZkratky);
    void updateCasovePravidlo(CasovePravidlo novePravidlo);
    void deleteCasovePravidlo(int id);
    CasovePravidlo getCasovePravidlo(int id);
    List<Den> getDnyCasovehoPravidla(List<Integer> idDnu);
    ArrayList<Integer> getIntListDnuCasovehoPravidla(int idPravidla);
    List<CasovePravidlo> getCasovaPravidlaByDenAktualni(Den den);
    List<CasovePravidlo> getCasovaPravidlaByDenPredchozi(Den aktualniDen);

}