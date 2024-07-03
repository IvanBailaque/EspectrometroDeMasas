import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class ParticleAnimation {

    private Timeline timeline;
    private double[] position = {0, 0}; // initial position
    private double[] velocity = {0, 0}; // initial velocity

    public ParticleAnimation(double initialVelocity) {
        velocity[1] = initialVelocity; // set initial velocity upwards
    }

    public void startAnimation() {
        timeline = new Timeline(new KeyFrame(Duration.millis(50), event -> updatePosition()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updatePosition() {
        // Update the particle's position based on current velocity
        position = Physics.calculateNextPosition(position, velocity, 0.05);
        // Add logic to update the UI with the new position
    }

    public void stopAnimation() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    // Method to update velocity based on external forces
    public void applyForces(double electricField, double magneticField) {
        double forceElectric = Physics.calculateForce(Physics.CHARGE, electricField);
        double forceMagnetic = Physics.calculateForce(Physics.CHARGE, velocity[1] * magneticField);
        double totalForce = forceElectric + forceMagnetic;
        double acceleration = Physics.calculateAcceleration(totalForce, Physics.MASS);

        // Update velocity based on acceleration
        velocity[0] += acceleration * 0.05;
    }
}
