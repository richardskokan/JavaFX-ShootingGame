package Objects;

import javafx.beans.property.*;

public class Score {
    private StringProperty name;
    public StringProperty nameProperty() {
        if (name == null) name = new SimpleStringProperty(this, "name");
        return name;
    }
    public void setName(String name) {
        nameProperty().setValue(name);
    }
    public String getName() {
        return nameProperty().get();
    }

    private IntegerProperty score;
    public IntegerProperty scoreProperty() {
        if (score == null) score = new SimpleIntegerProperty(this, "score");
        return score;
    }
    public void setScore(int score) {
        scoreProperty().setValue(score);
    }
    public int getScore() {
        return scoreProperty().get();
    }

    private IntegerProperty tries;
    public IntegerProperty triesProperty() {
        if (tries == null) tries = new SimpleIntegerProperty(this, "tries");
        return tries;
    }
    public void setTries(int tries) {
        triesProperty().setValue(tries);
    }
    public int getTries() {
        return triesProperty().get();
    }

    private IntegerProperty position;
    public IntegerProperty positionProperty() {
        if (position == null) position = new SimpleIntegerProperty(this, "position");
        return position;
    }
    public void setPosition(int position) {
        positionProperty().setValue(position);
    }
    public int getPosition() {
        return positionProperty().get();
    }

    private IntegerProperty wind;
    public IntegerProperty windProperty() {
        if (wind == null) wind = new SimpleIntegerProperty(this, "wind");
        return wind;
    }
    public void setWind(int wind) {
        windProperty().setValue(wind);
    }
    public int getWind() {
        return windProperty().get();
    }

    private IntegerProperty rested;
    public IntegerProperty restedProperty() {
        if (rested == null) rested = new SimpleIntegerProperty(this, "rested");
        return rested;
    }
    public void setRested(int rested) {
        restedProperty().setValue(rested);
    }
    public int getRested() {
        return restedProperty().get();
    }

    public Score(String name, int score, int tries, int position, int wind, int rested) {
        setName(name);
        setScore(score);
        setTries(tries);
        setPosition(position);
        setWind(wind);
        setRested(rested);
    }

    @Override
    public String toString() {
        return String.format("%15s %10s %5s %3s %4s %4s\r\n", getName(), getScore(), getTries(), getPosition(), getWind(), getRested());
    }

    public static Score fromString(String line) {
        return new Score(line.substring(0, 15).trim(), Integer.parseInt(line.substring(16, 26).trim()), Integer.parseInt(line.substring(27, 32).trim()),
                Integer.parseInt(line.substring(33, 36).trim()), Integer.parseInt(line.substring(37, 41).trim()), Integer.parseInt(line.substring(42, 46).trim()));
    }
}
