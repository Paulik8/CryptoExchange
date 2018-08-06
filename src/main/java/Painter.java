import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
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
    HashMap<String, ObservableList<XYChart.Data<String, Double>>> historyBITFINEX = new HashMap<>();

    public void setPairsBITFINEX(HashMap<String, Double> pairsBITFINEX) {
        this.pairsBITFINEX = pairsBITFINEX;
    }

    HashMap<String, Double> pairsBITFINEX = new HashMap<>();
    HashMap<String, Double> lastPairsBITFINEX = new HashMap<>();

    ObservableList<XYChart.Data<String, Double>> xyList1 = FXCollections.observableArrayList();
    ObservableList<XYChart.Data<String, Double>> xyList2 = FXCollections.observableArrayList();

    ObservableList<String> myXaxisCategories = FXCollections.observableArrayList();
    ObservableList<String> listPairs = FXCollections.observableArrayList();



    int i;
    int a;
    private Task<Date> task;
    private Task<String> task2;
    private LineChart<String,Number> lineChart;
    private XYChart.Series xySeries1;
    private XYChart.Series xySeries2;
    private CategoryAxis xAxis;
    private int lastObservedSize;


    private void updateMaps(String strDate, boolean isFullEXMO) {
        HashMap<String, Double> mapBuf = new HashMap<>();
        HashMap<String, ObservableList<XYChart.Data<String, Double>>> historyBuf = new HashMap<>();
        if (!isFullEXMO) {
            mapBuf = pairsEXMO;
            historyBuf = historyEXMO;
        } else {
            mapBuf = pairsBITFINEX;
            historyBuf = historyBITFINEX;
        }
        for (Map.Entry<String, Double> entry : mapBuf.entrySet()) {
            ObservableList<XYChart.Data<String, Double>> listBuf = FXCollections.observableArrayList();
            if (historyBuf.get(entry.getKey()) != null) {
                listBuf = historyBuf.get(entry.getKey());
            }
            listBuf.add(new XYChart.Data<>(strDate, entry.getValue()));
            historyBuf.put(entry.getKey(), listBuf);
        }

        if (!isFullEXMO) {
            pairsEXMO = mapBuf;
            historyEXMO = historyBuf;
            updateMaps(strDate, true);
        } else  {
            pairsBITFINEX = mapBuf;
            historyBITFINEX = historyBuf;
        }
    }

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

        ComboBox comboBox = new ComboBox();

        xAxis = new CategoryAxis();
        xAxis.setLabel("Date");


        final NumberAxis yAxis = new NumberAxis();
        lineChart = new LineChart<>(xAxis,yAxis);

        lineChart.setAnimated(false);

        task = new Task<Date>() {
            @Override
            protected Date call() throws Exception {
                while (true) {

                        if (lastPairsEXMO.size() == 0 ||  !(Objects.equals(lastPairsEXMO.get("BTCUSD"), pairsEXMO.get("BTCUSD")))) {
                            comboBox.getItems().addAll(pairsEXMO.keySet());//

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
                        Thread.sleep(10000);
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
                lineChart.setTitle(comboBox.getValue().toString());
                myXaxisCategories.add(strDate);

                System.out.println("value: " + comboBox.getValue());
                updateMaps(strDate, false);//
                Iterator<XYChart.Data<String, Double>> iterator = xyList1.iterator();//TODO ценнейшая информация
                while (iterator.hasNext()) {
                    System.out.println("iter " + iterator.next().getXValue());
                }//для изменения списка при перекликивании с одной пары на другую(время будет одно и то же, а цена разная)
                xyList1.add(new XYChart.Data<>(strDate, pairsEXMO.get(comboBox.getValue().toString())));
                System.out.println("xylist " + xyList1.size());
                xyList2.add(new XYChart.Data<>(strDate, pairsBITFINEX.get(comboBox.getValue().toString())));
                if (xyList1.size() > 10) {
                    xAxis.getCategories().remove(0);
                    xyList1.remove(0);
                    xyList2.remove(0);
                }

            }
        });


        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (i > 0) {
                xyList1.remove(0, xyList1.size());
                xyList1.addAll(historyEXMO.get(comboBox.getValue().toString()));
                xyList2.remove(0, xyList2.size());
                xyList2.addAll(historyBITFINEX.get(comboBox.getValue().toString()));
                if (xyList1.size() > 10) {
                    //xAxis.getCategories().remove(0);
                    xyList1.remove(0, xyList1.size() - 10);
                    xyList2.remove(0, xyList2.size() - 10);
                }
                yAxis.setForceZeroInRange(false);
                lineChart.setTitle(comboBox.getValue().toString());
                //lineChart.setLegendVisible(false);
                System.out.println("change " + xyList1.size() + newValue);

            }
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(comboBox, lineChart);
        comboBox.getSelectionModel().selectFirst();
        HBox.setMargin(comboBox, new Insets(70, 0,0,5));
        lineChart.setMinSize(800, 600);

        Scene scene  = new Scene(hBox, 1000,700);

        xAxis.setCategories(myXaxisCategories);
        xAxis.setAutoRanging(false);

        xySeries1 = new XYChart.Series<>(xyList1);
        xySeries1.setName("EXMO");

        xySeries2 = new XYChart.Series<>(xyList2);
        xySeries2.setName("BITFINEX");

        yAxis.setForceZeroInRange(false);
        lineChart.getData().addAll(xySeries1, xySeries2);

        i = 1;

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