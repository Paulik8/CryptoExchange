package models;

import java.util.HashMap;

public class PairListBITFINEX {

    private HashMap<String, PairBITFINEX> listPair;

    public PairListBITFINEX(HashMap<String, PairBITFINEX> pairList) {
        this.listPair = pairList;
    }

    public HashMap<String, PairBITFINEX> getListPair() {
        return listPair;
    }
}
