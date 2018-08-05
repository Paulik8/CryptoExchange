import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Painter extends Application {

    public void setPairsEXMO(HashMap<String, Double> pairsEXMO) {
        this.pairsEXMO = pairsEXMO;
    }

    HashMap<String, Double> pairsEXMO = new HashMap<>();
    HashMap<String, Double> lastPairsEXMO = new HashMap<>();

    HashMap<String, ObservableList<XYChart.Data<String, Double>>> historyEXMO = new HashMap<>();

    public void setPairsBITFINEX(HashMap<String, Double> pairsBITFINEX) {
        this.pairsBITFINEX = pairsBITFINEX;
    }

    HashMap<String, Double> pairsBITFINEX = new HashMap<>();
    HashMap<String, Double> lastPairsBITFINEX = new HashMap<>();

    ObservableList<XYChart.Data<String, Double>> xyList1 = FXCollections.observableArrayList();
    ObservableList<XYChart.Data<String, Double>> xyList2 = FXCollections.observableArrayList();

    ObservableList<String> myXaxisCategories = FXCollections.observableArrayList();
    ObservableList<String> listPairs;

    int i;
    int a;
    private Task<Date> task;
    private LineChart<String,Number> lineChart;
    private XYChart.Series xySeries1;
    private XYChart.Series xySeries2;
    private CategoryAxis xAxis;
    private int lastObservedSize;

    @Override
    public void start(Stage stage) {

        Coordinator coordinator = new Coordinator();
        coordinator.run(this);

//        xyList1.addListener((ListChangeListener<XYChart.Data<String, Double>>) change -> {
//            if (change.getList().size() - lastObservedSize > 10) {
//                lastObservedSize += 1;
//                xAxis.getCategories().remove(0);
//            }
//        });

        //lastPairsEXMO = pairsEXMO;
        stage.setTitle("Line Chart Sample");
        xAxis = new CategoryAxis();
        xAxis.setLabel("Date");

        final NumberAxis yAxis = new NumberAxis();
        lineChart = new LineChart<>(xAxis,yAxis);

        lineChart.setTitle("EOSUSD");
        lineChart.setAnimated(false);

        task = new Task<Date>() {
            @Override
            protected Date call() throws Exception {
                while (true) {

                    for (Map.Entry<String, Double> entry : pairsEXMO.entrySet()) {
                        if (entry.getValue().equals(lastPairsEXMO.get(entry.getKey()))) {
                            a++;

                        }
                    }
                    System.out.print(a % 29);
                        if (lastPairsEXMO.size() == 0 ||  !(Objects.equals(lastPairsEXMO.get("BTCUSD"), pairsEXMO.get("BTCUSD")))) {
                            System.out.println("newinfo " + lastPairsEXMO.get("BTCUSD") + " " + pairsEXMO.get("BTCUSD") + " " + lastPairsEXMO.size());
                            lastPairsEXMO.put("BTCUSD", pairsEXMO.get("BTCUSD"));
                            lastPairsBITFINEX.put("BTCUSD", pairsBITFINEX.get("BTCUSD"));

                            if (isCancelled()) {
                                break;
                            }
                            //System.out.println(Thread.currentThread().getName());

                            updateValue(new Date());
                        }

                    try {
                        System.out.println("oldinfo");
                        Thread.sleep(5000);
                    } catch (InterruptedException iex) {
                        Thread.currentThread().interrupt();
                    }
                }
                return new Date();
            }
        };

        task.valueProperty().addListener(new ChangeListener<Date>() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

            @Override
            public void changed(ObservableValue<? extends Date> observableValue, Date oldDate, Date newDate) {

                String strDate = dateFormat.format(newDate);
                myXaxisCategories.add(strDate);

                xyList1.add(new XYChart.Data(strDate, pairsEXMO.get("BTCUSD")));
                if (xyList1.size() - lastObservedSize > 10) {
                    //lastObservedSize = 1;
                    xAxis.getCategories().remove(0);
                    xyList1.remove(0);
                }
                System.out.println("xylist " + xyList1.size());
                xyList2.add(new XYChart.Data(strDate, pairsBITFINEX.get("BTCUSD")));

            }
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);

        Scene scene  = new Scene(lineChart,800,600);

        xAxis.setCategories(myXaxisCategories);
        xAxis.setAutoRanging(false);

        xySeries1 = new XYChart.Series(xyList1);
        xySeries1.setName("EXMO");

        xySeries2 = new XYChart.Series(xyList2);
        xySeries2.setName("BITFINEX");

        lineChart.getData().addAll(xySeries1, xySeries2);

        //i = 0;

        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(windowEvent -> {
            task.cancel();
        });
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
}