package Controllers;

import Objects.Score;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        labelScore.setAlignment(Pos.CENTER);

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
            //Creates instance of PrintWriter
            PrintWriter write = new PrintWriter(new BufferedWriter(new FileWriter("results.txt", true)));

            //Writes player's score to file
            Score scoreToWrite = new Score(name, score, tries, position, wind, rested);
            write.write(scoreToWrite.toString());
            write.close();
        } catch (IOException ignored) {}
    }

    //Sets data from MainController
    public void setData(String name, int score, int tries, int position, int wind, int rested) {
        this.name = name;
        this.score = score;
        this.tries = tries;
        this.position = position;
        this.wind = wind;
        this.rested = rested;

        labelScore.setText(String.valueOf(score));
    }
}
