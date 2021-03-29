package Controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class endGameController implements Initializable {
    public JFXButton btnGameEndOK;
    public Label labelScore;

    private String name;
    private int score;
    private int tries;
    private int position;
    private int wind;
    private int rested;

    private final File results = new File("results.txt");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnGameEndOK.setOnAction(event -> {
            //Saves users score
            saveScore();

            //Closes the popup window
            Stage stage = (Stage) btnGameEndOK.getScene().getWindow();
            stage.close();
        });
    }

    //Saves players score to file
    private void saveScore() {
        try {
            //Checks if file exists
            checkForResults();

            //Creates instance of PrintWriter
            PrintWriter write = new PrintWriter(new BufferedWriter(new FileWriter(results, true)));

            //Writes player's score to file
            write.write(String.format("%15s %10s %5s %3s %3s %3s \r\n", name, score, tries, position, wind, rested));
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Checks if file exists, if not creates it and adds a header
    private void checkForResults() throws IOException {
        if (!results.isFile()) {
            results.createNewFile();

            PrintWriter write = new PrintWriter(new BufferedWriter(new FileWriter(results)));
            write.write(String.format("%15s %10s %5s %3s %3s %3s \n", "NAME", "SCORE", "SHOTS", "POS", "WIND", "REST"));
            write.close();
        }
    }

    //Sets data from MainController
    public void setData(String name, int score, int tries, int position, int wind, int rested) {
        this.name = name;
        this.score = score;
        this.tries = tries;
        this.position = position;
        this.wind = wind;
        this.rested = rested;
    }
}
