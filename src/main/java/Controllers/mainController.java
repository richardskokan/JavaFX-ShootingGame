package Controllers;

import com.jfoenix.controls.JFXRadioButton;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ResourceBundle;

public class mainController implements Initializable {
    public JFXRadioButton posStand, posProne, windNone, windLight, windStrong, rested, heavyBreathing;
    ToggleGroup inputPosition = new ToggleGroup();
    ToggleGroup inputWind = new ToggleGroup();
    ToggleGroup inputComfort = new ToggleGroup();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Setting ToggleGroups and default values for radio buttons
        posStand.setToggleGroup(inputPosition); posProne.setToggleGroup(inputPosition);
        windNone.setToggleGroup(inputWind); windLight.setToggleGroup(inputWind); windStrong.setToggleGroup(inputWind);
        rested.setToggleGroup(inputComfort); heavyBreathing.setToggleGroup(inputComfort);
        posStand.setSelected(true); windNone.setSelected(true); rested.setSelected(true);
    }
}
