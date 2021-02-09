package Controllers;

import Objects.Target;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class mainController implements Initializable {
    public JFXRadioButton posStand, posProne, windNone, windLight, windStrong, rested, heavyBreathing;
    ToggleGroup inputPosition = new ToggleGroup();
    ToggleGroup inputWind = new ToggleGroup();
    ToggleGroup inputComfort = new ToggleGroup();

    public ImageView windImage;
    public Label statusRest;
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

    boolean running = false;
    int WIND = 0;
    int REST = 0;
    int windOffset = 0;
    int restOffsetX = 0;
    int restOffsetY = 0;
    int gravityOffset = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Setting ToggleGroups and default values for radio buttons
        posStand.setToggleGroup(inputPosition); posProne.setToggleGroup(inputPosition);
        windNone.setToggleGroup(inputWind); windLight.setToggleGroup(inputWind); windStrong.setToggleGroup(inputWind);
        rested.setToggleGroup(inputComfort); heavyBreathing.setToggleGroup(inputComfort);
        posStand.setSelected(true); windNone.setSelected(true); rested.setSelected(true);

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
                if (targets [finalI].getInnerTarget().contains(shotLocation)) {
                    targets [finalI].targetHit();
                }
            });

            targets [i].getOuterTarget().setOnMouseClicked(event -> {
                Point2D shotLocation = new Point2D(event.getX() + windOffset + restOffsetX, event.getY() + restOffsetY - gravityOffset);
                if (targets [finalI].getOuterTarget().contains(shotLocation)) {
                    targets [finalI].targetHit();
                }
            });

        }
    }

    public void start(ActionEvent actionEvent) {
        resetTargets();

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

    void resetTargets() {
        for (int i = 0; i < 5; i++) {
            targets [i].resetTarget();
        }
    }
}
