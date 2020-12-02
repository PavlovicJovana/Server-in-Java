package projekat;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

public class Graph extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        HBox root = new HBox();
        Scene scene = new Scene(root, 500, 400);

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("linearized_pick");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("f(linearized_pick)");

        LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
        lineChart.setTitle("Graph");

        Parameters parameters = getParameters();
        List<String> list_of_parametres = parameters.getRaw();

        XYChart.Series<String, Number> data = new XYChart.Series<>();
        for (int i = 1; i <= Integer.parseInt(list_of_parametres.get(0)) * 2; i = i + 2)
            data.getData().add(new XYChart.Data<String, Number>(list_of_parametres.get(i), Double.parseDouble(list_of_parametres.get(i+1))));

        lineChart.getData().add(data);
        root.getChildren().add(lineChart);

        primaryStage.setTitle("Graph");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}