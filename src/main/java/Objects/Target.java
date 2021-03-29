package Objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.awt.geom.Point2D;

public class Target {
    Circle innerTarget = new Circle(40);
    Circle outerTarget = new Circle(80);

    double x;
    double y;

    boolean shot = false;

    public Target(double x, double y) {
        innerTarget.setFill(new Color(0.212, 0.212, 0.212, 1));
        innerTarget.setStroke(new Color(0, 0, 0, 1));
        innerTarget.setStrokeWidth(2);

        outerTarget.setFill(new Color(0.275, 0.275, 0.275, 1));
        outerTarget.setStroke(new Color(0, 0, 0, 1));
        outerTarget.setStrokeWidth(2);

        innerTarget.setLayoutX(x);
        innerTarget.setLayoutY(y);
        this.x = x;

        outerTarget.setLayoutX(x);
        outerTarget.setLayoutY(y);
        this.y = y;
    }

    public Circle getInnerTarget() {
        return innerTarget;
    }

    public Circle getOuterTarget() {
        return outerTarget;
    }

    public void targetHit() {
        innerTarget.setFill(new Color(1, 1, 1, 1));
        innerTarget.setStroke(new Color(1, 1, 1, 1));
        innerTarget.setStrokeWidth(2);

        outerTarget.setFill(new Color(1, 1, 1, 1));

        innerTarget.setDisable(true);
        outerTarget.setDisable(true);

        shot = true;
    }

    public void resetTarget() {
        innerTarget.setFill(new Color(0.212, 0.212, 0.212, 1));
        innerTarget.setStroke(new Color(0, 0, 0, 1));
        innerTarget.setStrokeWidth(2);

        outerTarget.setFill(new Color(0.275, 0.275, 0.275, 1));

        innerTarget.setDisable(false);
        outerTarget.setDisable(false);

        shot = false;
    }

    public boolean isShot() {
        return shot;
    }

    public boolean wasHit(double x, double y, int position) {
        if (position == 0) {
            return Point2D.distance(this.x, this.y, x, y) < innerTarget.getRadius() + innerTarget.getStrokeWidth();
        } else {
            return Point2D.distance(this.x, this.y, x, y) < outerTarget.getRadius() + outerTarget.getStrokeWidth();
        }
    }
}
