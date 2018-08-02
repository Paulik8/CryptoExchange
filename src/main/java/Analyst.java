import models.PairBITFINEX;
import models.PairEXMO;

import java.util.HashMap;
import java.util.Map;

public class Analyst {

    private HashMap<String, PairEXMO> samePairsEXMO = new HashMap<String, PairEXMO>();
    private HashMap<String, PairBITFINEX> samePairsBITFINEX = new HashMap<String, PairBITFINEX>();
    private HashMap<String, Double> resultMap = new HashMap<String, Double>();
    private HashMap<String, PairEXMO> resultPairsEXMO = new HashMap<String, PairEXMO>();
    private HashMap<String, PairBITFINEX> resultPairsBITFINEX = new HashMap<String, PairBITFINEX>();


    public HashMap<String, PairEXMO> getSamePairsEXMO() {
        return samePairsEXMO;
    }

    public HashMap<String, PairBITFINEX> getSamePairsBITFINEX() {
        return samePairsBITFINEX;
    }

    public HashMap<String, Double> getResultMap() {
        return resultMap;
    }

    public HashMap<String, PairEXMO> getResultPairsEXMO() {
        return resultPairsEXMO;
    }

    public HashMap<String, PairBITFINEX> getResultPairsBITFINEX() {
        return resultPairsBITFINEX;
    }


    public void findSamePairs(HashMap<String, PairEXMO> mapEXMO, HashMap<String, PairBITFINEX> mapBITFINEX) {
        for (Map.Entry<String, PairEXMO> entry : mapEXMO.entrySet()) {
            String key = entry.getKey();
            if (mapBITFINEX.containsKey(key)) {
                samePairsEXMO.put(key, entry.getValue());// map пар с EXMO, которые есть и на BITFINEX
                samePairsBITFINEX.put(entry.getKey(), mapBITFINEX.get(key)); // map пар с BITFINEX, которые есть и на EXMO
            }
        }
    }

    public void comparePrice() {

        for (Map.Entry<String, PairEXMO> entry : samePairsEXMO.entrySet()) {

            String key = entry.getKey();
            Double exmoMid = (Double.parseDouble(entry.getValue().getBuyPrice())
                    + Double.parseDouble(entry.getValue().getSellPrice())) / 2;
            Double onePercentDifference = exmoMid * 0.01;
            Double bitfinexMid = Double.parseDouble(samePairsBITFINEX.get(key).getMid());
            if (Math.abs(bitfinexMid - exmoMid)
                    > onePercentDifference) {
                resultPairsEXMO.put(key, entry.getValue());// финальный map пар с EXMO
                resultPairsBITFINEX.put(key, samePairsBITFINEX.get(key));// финальный map пар с BITFINEX
                resultMap.put(key, bitfinexMid);//финальный map (пара, цена)
            }
        }
    }
}
