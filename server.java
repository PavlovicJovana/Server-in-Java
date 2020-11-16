package projekat;

import com.sun.prism.shader.Solid_TextureYV12_AlphaTest_Loader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Properties;

public class server extends Application{

    public static void main(String []args) {

        //taking port from properties file
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            prop.load(fis);
        } catch (FileNotFoundException e) {
            System.err.println("File with the specified pathname does not exist");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int port = Integer.parseInt(prop.getProperty("port"));

        try(ServerSocket server = new ServerSocket(port)) {

            System.out.println("The server is listening");
            while (true) {
                try(Socket client = server.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

                    System.out.println("The server has accepted client");

                    //recieve number of picks from the client
                    int number_of_picks = in.read();
                    if(number_of_picks == -1)
                        System.err.println("read() failed");

                    //recieve frequency from the client
                    String freq_str = in.readLine();
                    if(freq_str == null)
                        System.err.println("read() failed");
                    int frequency = Integer.parseInt(freq_str.trim());

                    double []pick = new double[number_of_picks];
                    double []f_pick = new double[number_of_picks];
                    String []arguments_for_graph = new String[2 * number_of_picks + 1];
                    arguments_for_graph[0] = String.valueOf(number_of_picks);

                    //recieve picks from the client
                    String pick_str = null;
                    for (int i = 0, j = 1; i < number_of_picks; i++) {
                        //recieve pick from the client
                        pick_str = in.readLine();
                        if(pick_str == null)
                            System.err.println("read() failed");
                        
                        //linearization
                        pick[i] = rounding(Double.parseDouble(pick_str.trim()), 2);
                        //function calculation
                        f_pick[i] = functionPick(pick[i], frequency);

                        arguments_for_graph[j++] = String.valueOf(pick[i]);
                        arguments_for_graph[j++] = String.valueOf(f_pick[i]);
                    }

                    Application.launch(arguments_for_graph);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double rounding(double value, int places) {
        return Math.round(value * Math.pow(10, places)) / Math.pow(10, places);
    }

    public static double functionPick(double pick, int frequency) {
        return (pick * frequency) % (10 * frequency);
    }

    //podesiti: Run -> Edit Configurations -> VM options : --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml --add-exports javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED
    @Override
    public void start(Stage primaryStage) throws Exception {
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
