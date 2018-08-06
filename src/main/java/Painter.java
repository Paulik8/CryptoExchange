import constants.Size;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Painter extends Application {

    private HashMap<String, Double> pairsEXMO = new HashMap<>();
    private HashMap<String, Double> lastPairsEXMO = new HashMap<>();

    private HashMap<String, ObservableList<XYChart.Data<String, Double>>> historyEXMO = new HashMap<>();
    private HashMap<String, ObservableList<XYChart.Data<String, Double>>> historyBITFINEX = new HashMap<>();

    private HashMap<String, Double> pairsBITFINEX = new HashMap<>();
    //private HashMap<String, Double> lastPairsBITFINEX = new HashMap<>();

    private ObservableList<XYChart.Data<String, Double>> xyList1 = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<String, Double>> xyList2 = FXCollections.observableArrayList();

    private ObservableList<String> xAxisCategories = FXCollections.observableArrayList();

    private int isLoad;
    private Task<Date> task;
    private LineChart<String,Number> lineChart;
    private CategoryAxis xAxis;

    public void setPairsEXMO(HashMap<String, Double> pairsEXMO) {
        this.pairsEXMO = pairsEXMO;
    }

    public void setPairsBITFINEX(HashMap<String, Double> pairsBITFINEX) {
        this.pairsBITFINEX = pairsBITFINEX;
    }

    private void updateMaps(String strDate, boolean isFullEXMO) {

        HashMap<String, Double> mapBuf;
        HashMap<String, ObservableList<XYChart.Data<String, Double>>> historyBuf;

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

        stage.setTitle("Line Chart Coins");

        ComboBox<String> comboBox = new ComboBox<>();

        xAxis = new CategoryAxis();
        xAxis.setLabel("Date");

        final NumberAxis yAxis = new NumberAxis();
        lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.setAnimated(false);

        task = new Task<Date>() {

            @Override
            protected Date call() {
                while (true) {

                    if (lastPairsEXMO.size() == 0 ||
                            !(Objects.equals(lastPairsEXMO.get("BTCUSD"), pairsEXMO.get("BTCUSD")))) {
                        comboBox.getItems().addAll(pairsEXMO.keySet());

                        lastPairsEXMO.put("BTCUSD", pairsEXMO.get("BTCUSD"));
                        //lastPairsBITFINEX.put("BTCUSD", pairsBITFINEX.get("BTCUSD"));

                        if (isCancelled()) {
                            break;
                        }

                        updateValue(new Date());
                    }

                    try {
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
                lineChart.setTitle(comboBox.getValue());
                xAxisCategories.add(strDate);

                updateMaps(strDate, false);

                xyList1.add(new XYChart.Data<>(strDate, pairsEXMO.get(comboBox.getValue())));
                xyList2.add(new XYChart.Data<>(strDate, pairsBITFINEX.get(comboBox.getValue())));

                if (xyList1.size() > 10) {
                    xAxis.getCategories().remove(0);
                    xyList1.remove(0);
                    xyList2.remove(0);
                }
            }
        });

        comboBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {

            if (isLoad > 0) {
                xyList1.remove(0, xyList1.size());
                xyList1.addAll(historyEXMO.get(comboBox.getValue()));
                xyList2.remove(0, xyList2.size());
                xyList2.addAll(historyBITFINEX.get(comboBox.getValue()));

                if (xyList1.size() > 10) {
                    xyList1.remove(0, xyList1.size() - 10);
                    xyList2.remove(0, xyList2.size() - 10);
                }

                yAxis.setForceZeroInRange(false);
                lineChart.setTitle(comboBox.getValue());
            }
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(comboBox, lineChart);
        comboBox.getSelectionModel().selectFirst();
        HBox.setMargin(comboBox, new Insets(70, 0,0,5));
        lineChart.setMinSize(Size.lineChartMinWidth, Size.lineChartMinHeight);

        Scene scene  = new Scene(hBox, Size.SceneWidth,Size.SceneHeight);

        xAxis.setCategories(xAxisCategories);
        xAxis.setAutoRanging(false);

        XYChart.Series xySeries1 = new XYChart.Series<>(xyList1);
        xySeries1.setName("EXMO");

        XYChart.Series xySeries2 = new XYChart.Series<>(xyList2);
        xySeries2.setName("BITFINEX");

        yAxis.setForceZeroInRange(false);
        lineChart.getData().addAll(xySeries1, xySeries2);

        isLoad = 1;

        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(windowEvent -> task.cancel());
    }

    public static void main(String[] args) {
        launch(args);
    }
}