package projekat;

import javafx.application.Application;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class server {

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

                    Application.launch(Graph.class, arguments_for_graph);
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
}
