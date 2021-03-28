package Controllers;

import Objects.Target;
import Threads.FatigueSimulator;
import Threads.WindSimulator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    public JFXRadioButton posStand, posProne, windNone, windLight, windStrong, rested, heavyBreathing;
    ToggleGroup inputPosition = new ToggleGroup();
    ToggleGroup inputWind = new ToggleGroup();
    ToggleGroup inputComfort = new ToggleGroup();

    public ImageView windImage;
    public Label statusRest;
    public Label bulletCounter;
    public JFXTextField numTries;
    public JFXTextField playerName;
    public JFXButton btnStart;
    public JFXButton btnScore;

    public Pane gamePane;
    Target target0 = new Target(137, 183);
    Target target1 = new Target(337, 183);
    Target target2 = new Target(537, 183);
    Target target3 = new Target(737, 183);
    Target target4 = new Target(937, 183);
    Target [] targets = {target0, target1, target2, target3, target4};

    Timeline uiChange = new Timeline();

    boolean running = false;
    int numShots = 0;
    int remainingShots = 0;
    String playerNameString = null;
    int WIND = 0;
    int REST = 0;
    int windOffset = 0;
    int restOffsetX = 0;
    int restOffsetY = 0;
    int gravityOffset = -10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Setting ToggleGroups and default values for radio buttons and start button
        posStand.setToggleGroup(inputPosition); posProne.setToggleGroup(inputPosition);
        windNone.setToggleGroup(inputWind); windLight.setToggleGroup(inputWind); windStrong.setToggleGroup(inputWind);
        rested.setToggleGroup(inputComfort); heavyBreathing.setToggleGroup(inputComfort);
        posStand.setSelected(true); windNone.setSelected(true); rested.setSelected(true); btnStart.setDisable(true);

        //Adding targets to pane
        gamePane.getChildren().addAll(target0.getOuterTarget(), target0.getInnerTarget());
        gamePane.getChildren().addAll(target1.getOuterTarget(), target1.getInnerTarget());
        gamePane.getChildren().addAll(target2.getOuterTarget(), target2.getInnerTarget());
        gamePane.getChildren().addAll(target3.getOuterTarget(), target3.getInnerTarget());
        gamePane.getChildren().addAll(target4.getOuterTarget(), target4.getInnerTarget());

        //Setting onclick-checkers
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            targets [i].getInnerTarget().setOnMouseClicked(event -> {
                Point2D shotLocation = new Point2D(event.getX() + windOffset + restOffsetX, event.getY() + restOffsetY - gravityOffset);
                if (targets [finalI].getInnerTarget().contains(shotLocation) && running) {
                    targets [finalI].targetHit();
                }
            });

            targets [i].getOuterTarget().setOnMouseClicked(event -> {
                Point2D shotLocation = new Point2D(event.getX() + windOffset + restOffsetX, event.getY() + restOffsetY - gravityOffset);
                if (targets [finalI].getOuterTarget().contains(shotLocation) && running) {
                    targets [finalI].targetHit();
                }
            });

        }
        gamePane.setOnMouseClicked(event -> remainingShots--);

        //Checking for name and number of tries emptiness
        playerName.setOnKeyTyped(event -> {
            if (playerName.getText().length() > 1 && numTries.getText().matches("\\d\\d*")) btnStart.setDisable(false);
            else btnStart.setDisable(true);
        });

        numTries.setOnKeyTyped(event -> {
            if (numTries.getText().matches("\\d\\d*") && playerName.getText().length() > 1) btnStart.setDisable(false);
            else btnStart.setDisable(true);
        });
    }

    public void start(ActionEvent actionEvent) {
        //Get values of settings and disables changes to them; allows click listeners
        if (inputWind.getSelectedToggle() == windNone) WIND = 0;
        else if (inputWind.getSelectedToggle() == windLight) WIND = 1;
        else if (inputWind.getSelectedToggle() == windStrong) WIND = 2;

        if (inputComfort.getSelectedToggle() == rested) REST = 0;
        else if (inputComfort.getSelectedToggle() == heavyBreathing) REST = 1;

        numShots = remainingShots = Integer.parseInt(numTries.getText());
        playerNameString = playerName.getText();

        numTries.setDisable(true); playerName.setDisable(true); btnStart.setDisable(true); btnScore.setDisable(true); posStand.setDisable(true); posProne.setDisable(true);
            windNone.setDisable(true); windLight.setDisable(true); windStrong.setDisable(true); rested.setDisable(true); heavyBreathing.setDisable(true);

        running = true;

        //Creates an instance of WindSimulator and starts it to simulate wind
        WindSimulator windSimulator = new WindSimulator(WIND);
        windSimulator.start();

        //Creates an instance of FatigueSimulator and starts it to simulate fatigue
        FatigueSimulator fatigueSimulator = new FatigueSimulator(REST);
        fatigueSimulator.start();

        //Value updates & UI updates of wind direction & strength and fatigue level; checks if player is finished (out of shots/hit all targets)
        uiChange = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            //Updates labels in UI
            bulletCounter.setText(remainingShots +"/" +numShots);
            statusRest.setText(String.valueOf(fatigueSimulator.getFatigueLevel()));

            //Updates values for wind offset

            //Checks if the game ended
            if (remainingShots == 0 || checkAllShot()) {
                //Shows score info and saves score
                writeScore();

                //Resets the whole simulation/game
                resetTargets(windSimulator, fatigueSimulator, uiChange);
            }
        }));
        uiChange.setCycleCount(Animation.INDEFINITE);
        uiChange.play();

        //Disabling/Enabling outer targets depending on "player position"
        if (inputPosition.getSelectedToggle() == posStand) {
            for (int i = 0; i < 5; i++) {
                targets [i].getOuterTarget().setDisable(false);
            }
        } else {
            for (int i = 0; i < 5; i++) {
                targets [i].getOuterTarget().setDisable(true);
            }
        }
    }

    private void writeScore() {
    }

    //Resets all targets, makes settings available and stops wind and fatigue simulation
    private void resetTargets(WindSimulator windSimulator, FatigueSimulator fatigueSimulator, Timeline uiChange) {
        //Stops other threads and UI refreshes
        windSimulator.stop();
        fatigueSimulator.stop();
        uiChange.stop();

        //Makes settings available
        numTries.setDisable(false); playerName.setDisable(false); btnStart.setDisable(false); btnScore.setDisable(false); posStand.setDisable(false); posProne.setDisable(false);
            windNone.setDisable(false); windLight.setDisable(false); windStrong.setDisable(false); rested.setDisable(false); heavyBreathing.setDisable(false);
        playerName.setText("");
        numTries.setText("");
        statusRest.setText("Unavenosť");
        bulletCounter.setText("Počet nábojov");

        //Resets targets
        for (int i = 0; i < 5; i++) {
            targets [i].resetTarget();
        }
    }

    //Checks if all targets were hit
    private boolean checkAllShot() {
        for (int i = 0; i < targets.length; i++) {
            if (targets[i].isShot()) {}
            else return false;
        }
        return true;
    }
}