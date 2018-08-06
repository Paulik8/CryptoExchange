package models;

import java.util.HashMap;

public class PairListEXMO {

    private HashMap<String, PairEXMO> listPair;

    public PairListEXMO(HashMap<String, PairEXMO> pairList) {
        this.listPair = pairList;
    }

    public HashMap<String, PairEXMO> getListPair() {
        return listPair;
    }
}
