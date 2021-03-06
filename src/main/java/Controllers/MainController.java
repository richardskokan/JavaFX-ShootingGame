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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    //Toggles and toggle groups
    public JFXRadioButton posStand, posProne, windNone, windLight, windStrong, rested, heavyBreathing;
    ToggleGroup inputPosition = new ToggleGroup();
    ToggleGroup inputWind = new ToggleGroup();
    ToggleGroup inputComfort = new ToggleGroup();

    //Other UI elements
    public ImageView windImage;
    public Label statusRest;
    public Label bulletCounter;
    public JFXTextField numTries;
    public JFXTextField playerName;
    public JFXButton btnStart;
    public JFXButton btnScore;

    //Pane containing targets and hitmarks with targets and hitmarks
    public Pane gamePane;
    Target target0 = new Target(137, 183);
    Target target1 = new Target(337, 183);
    Target target2 = new Target(537, 183);
    Target target3 = new Target(737, 183);
    Target target4 = new Target(937, 183);
    Target [] targets = {target0, target1, target2, target3, target4};
    ArrayList<Circle> hitmarks = new ArrayList<>();

    //Simulation and UI refreshing threads
    WindSimulator windSimulator;
    FatigueSimulator fatigueSimulator;
    Timeline uiChange = new Timeline();

    //Other parameters
    boolean running = false;
    int numShots = 0;
    int remainingShots = 0;
    String playerNameString = null;
    int WIND = 0;
    int REST = 0;
    int POSITION = 0;
    double windOffset = 0;
    double restOffsetX = 0;
    double restOffsetY = 0;
    int gravityOffset = -10;
    int score = 0;

    //Mouse movement parameters
    double mouseX = 0;
    double mouseY = 0;
    private boolean isInside;

    //File with scores
    private final File results = new File("results.txt");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Checks if file with scores exists
        try {
            checkForResults();
        } catch (IOException ignored) {}

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

        //Sets custom cursor (sniper sights)
        Image sniperSights = new Image("img/sights.png");
        ImageCursor sights = new ImageCursor(sniperSights, 16, 16);
        gamePane.setCursor(sights);

        //Start and Stop button (changes and effects of button push)
        btnStart.setOnAction(event -> {
            if (running) {
                btnStart.setText("Start");
                btnStart.setDisable(true);

                stop();
                resetTargets();
            } else {
                btnStart.setText("Stop");
                btnStart.setDisable(false);

                start();
            }
        });

        //Controls responses to mouse while shooting and checks its position
        gamePane.setOnMouseClicked(event -> {
            if (running) {
                //"Removes used bullet"
                remainingShots--;

                //Lowers level of "comfort"
                fatigueSimulator.playerFired();

                //Location where the shot landed
                Point2D shotLocation = new Point2D(event.getX() + windOffset, event.getY() - gravityOffset);

                //Adds hitmarker
                Circle hitmarker = new Circle(5, Color.GREEN);
                hitmarker.setCenterX(shotLocation.getX());
                hitmarker.setCenterY(shotLocation.getY());
                hitmarks.add(hitmarker);
                gamePane.getChildren().addAll(hitmarks.get(hitmarks.size() - 1));

                //Checks if target was hit
                for (int i = 0; i < 5; i++) {
                    if (targets [i].wasHit(shotLocation.getX(), shotLocation.getY(), POSITION)) targets [i].targetHit();
                }
            }
        });
        gamePane.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
        });
        gamePane.setOnMouseEntered(event -> isInside = true);
        gamePane.setOnMouseExited(event -> isInside = false);

        //Checking for name and number of tries emptiness and values
        playerName.setOnKeyTyped(event -> btnStart.setDisable(playerName.getText().length() <= 1 || !numTries.getText().matches("\\d\\d*") || playerName.getText().length() > 15  || Integer.parseInt(numTries.getText()) < 5));

        numTries.setOnKeyTyped(event -> btnStart.setDisable(!numTries.getText().matches("\\d\\d*") || playerName.getText().length() <= 1 || numTries.getText().length() > 5 || Integer.parseInt(numTries.getText()) < 5));
    }

    //Sets everything up for game start
    public synchronized void start() {
        //Gets values of settings and disables changes to them; allows click listeners
        if (inputWind.getSelectedToggle() == windNone) WIND = 0;
        else if (inputWind.getSelectedToggle() == windLight) WIND = 1;
        else if (inputWind.getSelectedToggle() == windStrong) WIND = 2;

        if (inputComfort.getSelectedToggle() == rested) REST = 1;
        else if (inputComfort.getSelectedToggle() == heavyBreathing) REST = 2;

        if (inputPosition.getSelectedToggle() == posStand) POSITION = 2;
        else POSITION = 1;

        numShots = remainingShots = Integer.parseInt(numTries.getText());
        playerNameString = playerName.getText();

        numTries.setDisable(true); playerName.setDisable(true); btnScore.setDisable(true); posStand.setDisable(true); posProne.setDisable(true);
            windNone.setDisable(true); windLight.setDisable(true); windStrong.setDisable(true); rested.setDisable(true); heavyBreathing.setDisable(true);

        running = true;

        //Creates an instance of WindSimulator and starts it to simulate wind
        windSimulator = new WindSimulator(WIND);
        windSimulator.start();

        //Creates an instance of FatigueSimulator and starts it to simulate fatigue
        fatigueSimulator = new FatigueSimulator(REST);
        fatigueSimulator.start();

        //Creates Robot for "fatigue cursor movement" and moves cursor to play area (gamePane)
        Robot cursorMover = new Robot();
        cursorMover.mouseMove(cursorMover.getMouseX() - 450, cursorMover.getMouseY() + 250);

        //Value updates & UI updates of wind direction & strength and fatigue level; checks if player is finished (out of shots/hit all targets)
        uiChange = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            //Sets values for cursor movement (offsets) and fatigue information
            DecimalFormat numberFormat = new DecimalFormat("#0.00");
            statusRest.setText(String.valueOf(numberFormat.format(fatigueSimulator.getFatigueLevel())));
            restOffsetX = fatigueSimulator.getOffsetX();
            restOffsetY = fatigueSimulator.getOffsetY();

            //Checks if the cursor could move more on the X axis (disables "fatigue cursor movement" if out of gamePane)
            if (gamePane.contains(mouseX, mouseY) && isInside) {
                cursorMover.mouseMove(cursorMover.getMouseX() + restOffsetX, cursorMover.getMouseY());
            }
            //Checks if the cursor could move more on the Y axis (disables "fatigue cursor movement" if out of gamePane)
            if (gamePane.contains(mouseX, mouseY) && isInside) {
                cursorMover.mouseMove(cursorMover.getMouseX(), cursorMover.getMouseY() + restOffsetY);
            }

            //Updates labels in UI
            bulletCounter.setText(remainingShots +"/" +numShots);

            //Updates values for wind offset and wind indication
            windOffset = windSimulator.getWindX();
            if (WIND != 0) {
                if (windOffset > 0) {
                    if (windOffset > 15) {
                        if (windOffset > 35) {
                            if (windOffset > 60) {
                                windImage.setImage(new Image("img/windIndicators/right-strong.png"));
                            } else {
                                windImage.setImage(new Image("img/windIndicators/right-mild.png"));
                            }
                        } else {
                            windImage.setImage(new Image("img/windIndicators/right-gentle.png"));
                        }
                    } else {
                        windImage.setImage(new Image("img/windIndicators/right-light.png"));
                    }
                } else {
                    if (windOffset < -15) {
                        if (windOffset < -35) {
                            if (windOffset < -60) {
                                windImage.setImage(new Image("img/windIndicators/left-strong.png"));
                            } else {
                                windImage.setImage(new Image("img/windIndicators/left-mild.png"));
                            }
                        } else {
                            windImage.setImage(new Image("img/windIndicators/left-gentle.png"));
                        }
                    } else {
                        windImage.setImage(new Image("img/windIndicators/left-light.png"));
                    }
                }
            }

            //Checks if the game ended
            if (remainingShots == 0 || targetsRemaining() == 0) {
                //Calculates score
                score = (5000 + 2500 * WIND + 2500 * REST + 2500 * POSITION) / numShots;
                if (targetsRemaining() > 0) score /= targetsRemaining();

                //Shows score info and saves score
                endGame();

                //Resets the whole simulation/game
                resetTargets();
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

    //Stops all threads and UI updates on window closure if the game is still running
    public void stop() {
        //Stops other threads and UI refreshes
        windSimulator.stop();
        fatigueSimulator.stop();
        uiChange.stop();
    }

    //Opens a window when game ends
    private void endGame() {
        try {
            //Loads .fxml file, gets its controller and sends data to controller to save player's result
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/endGame.fxml"));
            Parent gameEnd = loader.load();
            endGameController endGameController = loader.getController();
            endGameController.setData(playerNameString, score, numShots, POSITION, WIND, REST);

            //Creates popup window stage, sets needed parameters and shows it
            Stage gameEndWindow = new Stage();
            gameEndWindow.initModality(Modality.APPLICATION_MODAL);
            gameEndWindow.initOwner(gamePane.getScene().getWindow());
            gameEndWindow.setTitle("Shooting Game");
            gameEndWindow.getIcons().add(new Image("/img/icon.png"));
            gameEndWindow.setScene(new Scene(gameEnd));
            gameEndWindow.setResizable(false);
            gameEndWindow.setAlwaysOnTop(true);
            gameEndWindow.show();
        } catch (IOException ignored) {}
    }

    //Checks if file with results exists, if not creates it and adds a header
    private void checkForResults() throws IOException {
        if (!results.isFile()) {
            results.createNewFile();

            PrintWriter write = new PrintWriter(new BufferedWriter(new FileWriter(results)));
            write.write(String.format("%15s %10s %5s %3s %4s %4s\n", "NAME", "SCORE", "SHOTS", "POS", "WIND", "REST"));
            write.close();
        }
    }

    //Opens a window with top scores
    public void scores() {
        try {
            Parent scores = FXMLLoader.load(getClass().getResource("/layouts/scores.fxml"));
            Stage scoreWindow = new Stage();
            scoreWindow.initModality(Modality.APPLICATION_MODAL);
            scoreWindow.initOwner(gamePane.getScene().getWindow());
            scoreWindow.setTitle("Shooting Game - Najvyššie skóre");
            scoreWindow.getIcons().add(new Image("/img/icon.png"));
            scoreWindow.setScene(new Scene(scores));
            scoreWindow.setResizable(false);
            scoreWindow.setAlwaysOnTop(true);
            scoreWindow.show();
        } catch (IOException ignored) {}
    }

    //Resets all targets, makes settings available and stops wind and fatigue simulation
    private void resetTargets() {
        //Stops other threads and UI refreshes
        windSimulator.interrupt();
        fatigueSimulator.interrupt();
        uiChange.stop();

        //Stops click listeners
        running = false;

        //Removes hitmarkers
        gamePane.getChildren().removeAll(hitmarks);

        //Makes settings available
        numTries.setDisable(false); playerName.setDisable(false); btnStart.setDisable(true); btnScore.setDisable(false); posStand.setDisable(false); posProne.setDisable(false);
            windNone.setDisable(false); windLight.setDisable(false); windStrong.setDisable(false); rested.setDisable(false); heavyBreathing.setDisable(false);
        playerName.setText("");
        numTries.setText("");
        statusRest.setText("Unavenosť");
        bulletCounter.setText("Počet nábojov");
        windImage.setImage(null);

        //Resets targets
        for (int i = 0; i < 5; i++) {
            targets [i].resetTarget();
        }

        isInside = false;
    }

    //Checks how many targets are left
    private int targetsRemaining() {
        int remaining = 5;

        for (Target target : targets) {
            if (target.isShot()) remaining--;
        }
        return remaining;
    }
}