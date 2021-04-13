package Controllers;

import Objects.Score;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class EndGameController extends Stage implements Initializable {
    public JFXButton btnGameEndOK;
    public Label labelScore;

    private String name;
    private int score;
    private int tries;
    private int position;
    private int wind;
    private int rested;
    private final OnFinish listener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        labelScore.setAlignment(Pos.CENTER);

        //Resets targets before game end
        setOnCloseRequest(event -> listener.onFinish());

        btnGameEndOK.setOnAction(event -> {
            //Saves users score
            saveScore();
            //Resets targets after window closure
            listener.onFinish();
            //Closes the popup window
            close();
        });
    }

    //Creates end game window, sets its attributes
    public EndGameController(Window window, OnFinish listener) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/endGame.fxml"));
        loader.setController(this);
        Pane parent = new Pane();
        loader.setRoot(parent);
        initModality(Modality.APPLICATION_MODAL);
        initOwner(window);
        setTitle("Shooting Game");
        getIcons().add(new Image("/img/icon.png"));
        setScene(new Scene(loader.load()));
        setResizable(false);
        setAlwaysOnTop(true);
        this.listener = listener;
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

    public interface OnFinish {
        void onFinish();
    }
}
