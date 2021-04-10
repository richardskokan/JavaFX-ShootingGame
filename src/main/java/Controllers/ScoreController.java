package Controllers;

import Objects.Score;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ScoreController implements Initializable {

    public TableView<Score> scoresTable;
    private ObservableList<Score> scores = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            getData();
        } catch (IOException ignored) {}

        scoresTable.setItems(scores);

        //Creates columns for table
        TableColumn<Score, String> name = new TableColumn<>("Meno");
        TableColumn<Score, Integer> score = new TableColumn<>("Skóre");
        TableColumn<Score, Integer> tries = new TableColumn<>("P. pokusov");
        TableColumn<Score, Integer> position = new TableColumn<>("Poloha");
        TableColumn<Score, Integer> wind = new TableColumn<>("Sila vetra");
        TableColumn<Score, Integer> rested = new TableColumn<>("Unavenosť");

        System.out.println(scores.isEmpty() +" " +scores.size());

        //Sets from where will the columns get data if there is any
        if (!scores.isEmpty()) {
            name.setCellValueFactory(new PropertyValueFactory<>(scores.get(0).nameProperty().getName()));
            score.setCellValueFactory(new PropertyValueFactory<>(String.valueOf(scores.get(0).scoreProperty().getName())));
            tries.setCellValueFactory(new PropertyValueFactory<>(String.valueOf(scores.get(0).triesProperty().getName())));
            position.setCellValueFactory(new PropertyValueFactory<>(String.valueOf(scores.get(0).positionProperty().getName())));
            wind.setCellValueFactory(new PropertyValueFactory<>(String.valueOf(scores.get(0).windProperty().getName())));
            rested.setCellValueFactory(new PropertyValueFactory<>(String.valueOf(scores.get(0).restedProperty().getName())));
        }

        //Sets table attributes
        scoresTable.getColumns().setAll(name, score, tries, position, wind, rested);
        scoresTable.columnResizePolicyProperty().setValue(param -> false);
    }

    //Gets data from file
    private void getData() throws IOException {
        BufferedReader read = new BufferedReader(new FileReader("results.txt"));

        //Skips the first line with header
        read.readLine();

        //Reads first line with score
        String temp = read.readLine();

        while ((temp = read.readLine()) != null) {
            //Separates data from a string into an instance of the Score object and adds it to a list with scores
            scores.add(Score.fromString(temp));
        }
    }
}
