
import constants.URL;
import models.ComplexValue;

import java.io.IOException;

public class MainApplication {

    Thread threadEXMO;
    Thread threadBITFINEX;

    public static void main(String[] args) throws IOException {

        Coordinator coordinator = new Coordinator();
        coordinator.run();

//        final Controller controllerEXMO = new Controller(URL.URL_EXMO);
//        final Controller controllerBITFINEX = new Controller(URL.URL_BITFINEX);
//        final ComplexValue complexValue = new ComplexValue();
//
//        threadEXMO = new Thread(new Runnable() {
//            public void run() {
//                try {
//                    controllerEXMO.setEXMO(true);
//                    complexValue.setPairListEXMO(controllerEXMO.run().getPairListEXMO());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        Thread threadBITFINEX = new Thread(new Runnable() {
//            public void run() {
//                try {
//                    controllerBITFINEX.setBITFINEX(true);
//                    complexValue.setPairListBITFINEX(controllerBITFINEX.run().getPairListBITFINEX());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        threadEXMO.start();
//        threadBITFINEX.start();
//
//        try {
//            threadEXMO.join();
//            threadBITFINEX.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        //System.out.println("complexValue " + complexValue);
//
//        Analyst analyst = new Analyst();
//
//        analyst.findSamePairs(complexValue.getPairListEXMO().getListPair(), complexValue.getPairListBITFINEX().getListPair());
//        analyst.comparePrice();
//
//        System.out.println("exmoSize: " + analyst.getSamePairsEXMO().size());
//        System.out.println("bitfinexSize: " + analyst.getSamePairsBITFINEX().size());
//        System.out.println("resultSize: " + analyst.getResultMap().size());
//
//        Painter painter = new Painter();
//        painter.setVisible(true);

    }

    private void timer() {
    }
}