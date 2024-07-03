import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.LineTo;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EspectrometroDePruebas extends Application {

    private Pane simulationPane;
    private Circle particle;
    private Path particlePath;
    private Line particleLine;
    private Line fbArrow;
    private Line feArrow;
    private Line fbArrowRegion2;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mass Spectrometer Simulator");

        // Sliders for input parameters
        Slider electricFieldSlider = new Slider(0, 2, 1.5);
        electricFieldSlider.setShowTickLabels(true);
        electricFieldSlider.setShowTickMarks(true);
        electricFieldSlider.setMajorTickUnit(0.5);
        electricFieldSlider.setMinorTickCount(1);
        electricFieldSlider.setBlockIncrement(0.1);
        Slider magneticFieldASlider = new Slider(-1, 1, 0.5);
        magneticFieldASlider.setShowTickLabels(true);
        magneticFieldASlider.setShowTickMarks(true);
        magneticFieldASlider.setMajorTickUnit(0.5);
        magneticFieldASlider.setMinorTickCount(1);
        magneticFieldASlider.setBlockIncrement(0.1);
        Slider magneticFieldBSlider = new Slider( -0.27, 0.27, 0.1);
        magneticFieldBSlider.setShowTickLabels(true);
        magneticFieldBSlider.setShowTickMarks(true);
        magneticFieldBSlider.setMajorTickUnit(0.05);
        magneticFieldBSlider.setMinorTickCount(5);
        magneticFieldBSlider.setBlockIncrement(0.01);
        Slider initialVelocitySlider = new Slider(2, 5, 3);
        initialVelocitySlider.setShowTickLabels(true);
        initialVelocitySlider.setShowTickMarks(true);
        initialVelocitySlider.setMajorTickUnit(0.5);
        initialVelocitySlider.setMinorTickCount(1);
        initialVelocitySlider.setBlockIncrement(0.25);

        TextField electricFieldValue = createTextField(electricFieldSlider);
        TextField magneticFieldAValue = createTextField(magneticFieldASlider);
        TextField magneticFieldBValue = createTextField(magneticFieldBSlider);
        TextField initialVelocityValue = createTextField(initialVelocitySlider);

        // Labels for sliders
        Label electricFieldLabel = new Label("Campo Eléctrico");
        Label magneticFieldALabel = new Label("Campo Magnético A");
        Label magneticFieldBLabel = new Label("Campo Magnético B'");
        Label initialVelocityLabel = new Label("Velocidad Inicial");

        // Button to start simulation
        Button simulateButton = new Button("Simular");
        Button resetButton = new Button("Restaurar Valores");

        // Layout for sliders
        GridPane slidersGrid = new GridPane();
        slidersGrid.setVgap(10);
        slidersGrid.setHgap(10);

        slidersGrid.add(electricFieldLabel, 0, 0);
        slidersGrid.add(electricFieldSlider, 1, 0);
        slidersGrid.add(electricFieldValue, 2, 0);

        slidersGrid.add(magneticFieldALabel, 0, 1);
        slidersGrid.add(magneticFieldASlider, 1, 1);
        slidersGrid.add(magneticFieldAValue, 2, 1);

        slidersGrid.add(magneticFieldBLabel, 0, 2);
        slidersGrid.add(magneticFieldBSlider, 1, 2);
        slidersGrid.add(magneticFieldBValue, 2, 2);

        slidersGrid.add(initialVelocityLabel, 0, 3);
        slidersGrid.add(initialVelocitySlider, 1, 3);
        slidersGrid.add(initialVelocityValue, 2, 3);

        VBox buttonsBox = new VBox(10);
        buttonsBox.getChildren().addAll(simulateButton, resetButton);

        // Pane for simulation
        simulationPane = new Pane();
        simulationPane.setPrefSize(600, 600);

        // Draw the mass spectrometer diagram
        drawMassSpectrometer(electricFieldSlider.getValue(), magneticFieldASlider.getValue(), magneticFieldBSlider.getValue());

        // Layout for equations
        VBox equationsBox = new VBox(10);
        equationsBox.getChildren().addAll(
                new Label("Ecuaciones:"),
                new Label("FbA = q * v * bA"),
                new Label("Fe = q * E"),
                new Label("FbB = q * v * bB"),
                new Label("r = (m * v) / (|q| * bB)")
        );
        // Main layout
        BorderPane root = new BorderPane();
        root.setLeft(equationsBox);
        root.setRight(slidersGrid);
        root.setBottom(buttonsBox);
        root.setCenter(simulationPane);

        // Set the scene
        Scene scene = new Scene(root, 800, 600); //antes 800x600
        primaryStage.setScene(scene);
        primaryStage.show();

        // Add simulation logic here
        simulateButton.setOnAction(event -> {
            // Get values from sliders
            double electricField = electricFieldSlider.getValue();
            double magneticFieldA = magneticFieldASlider.getValue();
            double magneticFieldB = magneticFieldBSlider.getValue();
            double initialVelocity = initialVelocitySlider.getValue();

            // Redraw the mass spectrometer with the new field values
            drawMassSpectrometer(electricField, magneticFieldA, magneticFieldB);

            // Simulate the particle motion
            simulateParticleMotion(electricField, magneticFieldA, magneticFieldB, initialVelocity);
        });

        resetButton.setOnAction(event -> {
            electricFieldSlider.setValue(1.5);
            magneticFieldASlider.setValue(0.5);
            magneticFieldBSlider.setValue(0.1);
            initialVelocitySlider.setValue(3);

            // Redraw the mass spectrometer with the reset values
            drawMassSpectrometer(electricFieldSlider.getValue(), magneticFieldASlider.getValue(), magneticFieldBSlider.getValue());
        });
    }

    private Slider createSlider(double min, double max, double value) {
        Slider slider = new Slider(min, max, value);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit((max - min) / 4);
        slider.setMinorTickCount(5);
        slider.setBlockIncrement(1);
        return slider;
    }

    private TextField createTextField(Slider slider) {
        TextField textField = new TextField(String.valueOf(slider.getValue()));
        textField.setPrefWidth(50);

        slider.valueProperty().addListener((observable, oldValue, newValue) ->
                textField.setText(String.format("%.2f", newValue.doubleValue()))
        );

        textField.setOnAction(event -> {
            try {
                double value = Double.parseDouble(textField.getText());
                slider.setValue(value);
            } catch (NumberFormatException e) {
                textField.setText(String.format("%.2f", slider.getValue()));
            }
        });

        return textField;
    }

    private void drawMassSpectrometer(double electricField, double magneticFieldA, double magneticFieldB) {
        // Clear the previous drawings
        simulationPane.getChildren().clear();

        // Draw the selector of velocity region
        Line leftBoundary = new Line(200, 50, 200, 300);
        Line rightBoundary = new Line(300, 50, 300, 300);

        // Draw electric field arrows
        if (electricField > 0) {
            for (int y = 60; y < 300; y += 35) {
                double arrowLength = electricField / 2 * 60;  // Scale the arrow length
                Line arrow = new Line(250 - arrowLength / 2, y, 250 + arrowLength / 2, y);
                Line arrowHead1 = new Line(250 - arrowLength / 2 + 10, y - 5, 250 - arrowLength / 2, y);
                Line arrowHead2 = new Line(250 - arrowLength / 2 + 10, y + 5, 250 - arrowLength / 2, y);
                simulationPane.getChildren().addAll(arrow, arrowHead1, arrowHead2);
            }
        }

        // Draw magnetic field crosses or points
        if (magneticFieldA != 0) {
            for (int y = 60; y < 300; y += 30) {
                for (int x = 220; x < 310; x += 30) {
                    if (magneticFieldA > 0) {
                        double crossSize = magneticFieldA * 10;  // Scale the cross size
                        Line line1 = new Line(x - crossSize / 2, y - crossSize / 2, x + crossSize / 2, y + crossSize / 2);
                        Line line2 = new Line(x - crossSize / 2, y + crossSize / 2, x + crossSize / 2, y - crossSize / 2);
                        simulationPane.getChildren().addAll(line1, line2);
                    } else {
                        double pointRadius = -magneticFieldA * 5;  // Scale the point radius
                        Circle point = new Circle(x, y, pointRadius, Color.BLACK);
                        simulationPane.getChildren().add(point);
                    }
                }
            }
        }

        // Draw the region with magnetic field B'
        Line leftBoundary2 = new Line(350, 50, 350, 300);
        Line rightBoundary2 = new Line(450, 50, 450, 300);

        // Draw magnetic field B' crosses
        if (magneticFieldB > 0) {
            for (int y = 60; y < 300; y += 30) {
                for (int x = 370; x < 460; x += 30) {
                    double crossSize = magneticFieldB * 60;  // Scale the cross size
                    Line line1 = new Line(x - crossSize / 2, y - crossSize / 2, x + crossSize / 2, y + crossSize / 2);
                    Line line2 = new Line(x - crossSize / 2, y + crossSize / 2, x + crossSize / 2, y - crossSize / 2);
                    simulationPane.getChildren().addAll(line1, line2);
                }
            }
        }

        simulationPane.getChildren().addAll(leftBoundary, rightBoundary, leftBoundary2, rightBoundary2);
    }

    private void simulateParticleMotion(double electricField, double magneticFieldA, double magneticFieldB, double initialVelocity) {
        // Clear previous simulation
        simulationPane.getChildren().removeIf(node -> node instanceof Circle || node instanceof Path);

        // Initial particle position and velocity
        final double[] particleX = {150};
        final double[] particleY = {175};
        final double[] velocityX = {initialVelocity};
        final double[] velocityY = {0};
        final double[] accelerationX = {0};
        final double[] accelerationY = {0};

        // Create particle
        particle = new Circle(particleX[0], particleY[0], 5, Color.RED);
        particlePath = new Path();
        particlePath.getElements().add(new MoveTo(particleX[0], particleY[0]));
        particlePath.setStroke(Color.RED);

        simulationPane.getChildren().addAll(particle, particlePath);

        // Simulation timeline
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            // Determine the region the particle is in and apply appropriate forces
            if (particleX[0] >= 200 && particleX[0] <= 300) {
                // In the velocity selector region
                double fe = electricField;
                double fb = velocityX[0] * magneticFieldA;

                accelerationX[0] = fb - fe;
                accelerationY[0] = 0;
            } else if (particleX[0] >= 350 && particleX[0] <= 450) {
                // In the region with only magnetic field B'
                double fb = velocityX[0] * magneticFieldB;

                accelerationX[0] = 0;
                accelerationY[0] = fb;
            } else {
                // Outside the regions
                accelerationX[0] = 0;
                accelerationY[0] = 0;
            }

            // Update velocity and position
            velocityX[0] += accelerationX[0] * 0.01;
            velocityY[0] += accelerationY[0] * 0.01;

            particleX[0] += velocityX[0] * 0.01;
            particleY[0] += velocityY[0] * 0.01;

            // Update particle position
            particle.setCenterX(particleX[0]);
            particle.setCenterY(particleY[0]);
            particlePath.getElements().add(new LineTo(particleX[0], particleY[0]));

            // Stop simulation if particle goes out of bounds
            if (particleX[0] > 600 || particleX[0] < 0 || particleY[0] > 600 || particleY[0] < 0) {
                ((Timeline) event.getSource()).stop();
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
