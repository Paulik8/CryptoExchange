import constants.URL;
import models.ComplexValue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;

public class Coordinator {

    private Thread threadEXMO;
    private Painter painter;
    private Thread threadBITFINEX;
    private Controller controllerEXMO = new Controller(URL.URL_EXMO);
    private Controller controllerBITFINEX = new Controller(URL.URL_BITFINEX);
    private ComplexValue complexValue = new ComplexValue();
    private Analyst analyst;

    public Thread getThreadEXMO() {
        return threadEXMO;
    }

    public Thread getThreadBITFINEX() {
        return threadBITFINEX;
    }

    public void run(Painter painter) {
        this.painter = painter;

        initThreads();

        startThreads();

        analyst = new Analyst();
        analyzePairs();

        System.out.println("exmoSize: " + analyst.getSamePairsEXMO().size());
        System.out.println("bitfinexSize: " + analyst.getSamePairsBITFINEX().size());
        System.out.println("resultSize: " + analyst.getSameMapEXMO().size());

        this.painter.setPairsEXMO(analyst.getSameMapEXMO());
        System.out.println("setter");
        this.painter.setPairsBITFINEX(analyst.getSameMapBITFINEX());

        //painter.setVisible(true);
        Timer timer = new Timer();
        MyTimerTask timerTask = new MyTimerTask(this);
        timer.schedule(timerTask, 5000, 5000);



        //Application.launch(painter.getClass(), "");
    }

    private void analyzePairs() {
        analyst.findSamePairs(complexValue.getPairListEXMO().getListPair(), complexValue.getPairListBITFINEX().getListPair());
        analyst.comparePrice();
    }

    private void initThreads() {

        threadEXMO = new Thread(() -> {
            try {
                controllerEXMO.setEXMO(true);
                complexValue.setPairListEXMO(controllerEXMO.run().getPairListEXMO());
                controllerEXMO.setEXMO(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        threadBITFINEX = new Thread(() -> {
            try {
                controllerBITFINEX.setBITFINEX(true);
                complexValue.setPairListBITFINEX(controllerBITFINEX.run().getPairListBITFINEX());
                controllerBITFINEX.setBITFINEX(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void startThreads() {

        threadEXMO.start();
        threadBITFINEX.start();

        try {
            threadEXMO.join();
            threadBITFINEX.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadEXMO.interrupt();
        threadBITFINEX.interrupt();
    }

    public void repeat() {

        initThreads();

        startThreads();

        analyzePairs();

        painter.setPairsEXMO(analyst.getSameMapEXMO());
        painter.setPairsBITFINEX(analyst.getSameMapBITFINEX());

        System.out.println("exmoSize1: " + analyst.getSamePairsEXMO().size());
        System.out.println("bitfinexSize: " + analyst.getSamePairsBITFINEX().size());
        System.out.println("resultSize: " + analyst.getSameMapEXMO().size());
    }
}
