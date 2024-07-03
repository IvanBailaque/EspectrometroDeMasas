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
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.geom.Line2D;

public class MassSpectrometerSimulator extends Application {

    private Pane simulationPane;
    private Circle particle;
    private Path particlePath;
    private Line vectorFuerzaElectrica;
    private Line vectorFuerzaElectricaFlechaUno;
    private Line vectorFuerzaElectricaFlechaDos;
    private Line vectorFuerzaMagnetica;
    private Line vectorFuerzaMagneticaFlechaUno;
    private Line vectorFuerzaMagneticaFlechaDos;
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

        // Draw the mass spectrometer diagram
        drawMassSpectrometerDiagram();

        // Add simulation logic here
        simulateButton.setOnAction(event -> {
            // Get values from sliders
            double electricField = electricFieldSlider.getValue();
            double magneticFieldA = magneticFieldASlider.getValue();
            double magneticFieldB = magneticFieldBSlider.getValue();
            double initialVelocity = initialVelocitySlider.getValue();

            // Simulate the particle motion
            simulateParticleMotion(electricField, magneticFieldA, magneticFieldB, initialVelocity);

            drawMassSpectrometer(electricField, magneticFieldA, magneticFieldB);
            drawMassSpectrometerDiagram(); //rari
        });

        resetButton.setOnAction(event -> {
            electricFieldSlider.setValue(1.5);
            magneticFieldASlider.setValue(0.5);
            magneticFieldBSlider.setValue(0.1);
            initialVelocitySlider.setValue(3);
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

    private void drawMassSpectrometerDiagram(){
        // Draw the selector of velocity region
        Line leftBoundary = new Line(200, 50, 200, 300);
        Line rightBoundary = new Line(300, 50, 300, 300);

        leftBoundary.setStrokeWidth(2);
        rightBoundary.setStrokeWidth(2);

        // Draw the region with only magnetic field B'
        Line topBoundary = new Line(100, 500, 400, 500);
        Line bottomBoundaryLeft = new Line(100, 300, 243, 300);
        Line bottomBoundaryRight = new Line(256, 300, 400, 300);
        Line leftRegion2 = new Line(100, 300, 100, 500);
        Line rightRegion2 = new Line(400, 300, 400, 500);
        Line gapLeft = new Line(243, 300, 243, 305);
        Line gapRight = new Line(256, 300, 256, 305);

        topBoundary.setStrokeWidth(2);
        bottomBoundaryLeft.setStrokeWidth(2);
        bottomBoundaryRight.setStrokeWidth(2);
        leftRegion2.setStrokeWidth(2);
        rightRegion2.setStrokeWidth(2);
        gapLeft.setStrokeWidth(2);
        gapRight.setStrokeWidth(2);

        simulationPane.getChildren().addAll(leftBoundary, rightBoundary, topBoundary, bottomBoundaryLeft, bottomBoundaryRight, leftRegion2, rightRegion2,gapLeft,gapRight);
        drawAxes();
    }

    private void drawMassSpectrometer(double electricField, double magneticFieldA, double magneticFieldB) {

        if (electricField > 0) {
            for (int y = 60; y < 300; y += 35) {
                double arrowLength = electricField  * 40;  // Scale the arrow length
                Line arrow = new Line(240 - arrowLength / 2, y, 260 + arrowLength / 2, y);
                Line arrowHead1 = new Line(240 - arrowLength / 2 + 10, y - 5, 240 - arrowLength / 2, y);
                Line arrowHead2 = new Line(240 - arrowLength / 2 + 10, y + 5, 240 - arrowLength / 2, y);

                arrow.setStroke(Color.BLUE);
                arrowHead1.setStroke(Color.BLUE);
                arrowHead2.setStroke(Color.BLUE);
                simulationPane.getChildren().addAll(arrow, arrowHead1, arrowHead2);
            }
        }

        // Draw magnetic field crosses or points
        if (magneticFieldA != 0) {
            for (int y = 60; y < 300; y += 30) {
                for (int x = 220; x < 310; x += 30) {
                    if (magneticFieldA > 0) {
                        double crossSize = magneticFieldA * 15;  // Scale the cross size
                        Line line1 = new Line(x - crossSize / 2, y - crossSize / 2, x + crossSize / 2, y + crossSize / 2);
                        Line line2 = new Line(x - crossSize / 2, y + crossSize / 2, x + crossSize / 2, y - crossSize / 2);
                        line1.setStroke(Color.GREEN);
                        line2.setStroke(Color.GREEN);
                        simulationPane.getChildren().addAll(line1, line2);
                    } else {
                        double pointRadius = -magneticFieldA * 5;  // Scale the point radius
                        Circle point = new Circle(x, y, pointRadius, Color.BLACK);
                        simulationPane.getChildren().add(point);
                    }
                }
            }
        }

        if (magneticFieldB != 0) {
            for (int y = 310; y < 500; y += 15) {
                for (int x = 108; x < 400; x += 15) {
                    if (magneticFieldB > 0) {
                        double crossSize = magneticFieldB * 40;  // Scale the cross size
                        Line line1 = new Line(x - crossSize / 2, y - crossSize / 2, x + crossSize / 2, y + crossSize / 2);
                        Line line2 = new Line(x - crossSize / 2, y + crossSize / 2, x + crossSize / 2, y - crossSize / 2);
                        simulationPane.getChildren().addAll(line1, line2);
                    } else {
                        double pointRadius = -magneticFieldB * 5;  // Scale the point radius
                        Circle point = new Circle(x, y, pointRadius, Color.BLACK);
                        simulationPane.getChildren().add(point);
                    }
                }
            }
        }



    }

    private void drawAxes() {
        // Eje X
        Line ejeX = new Line(50, 50, 110, 50);
        Line flechaX1 = new Line(110, 50, 100, 60);
        Line flechaX2 = new Line(110, 50, 100, 40);
        Line cruzX1 = new Line( 120 - 5,  50 - 5,  120 + 5,  50 + 5);
        Line cruzX2 = new Line(120 + 5, 50 - 5, 120 - 5, 50 + 5);

        simulationPane.getChildren().addAll(ejeX, flechaX1, flechaX2, cruzX1, cruzX2);

        // Eje Y
        Line ejeY = new Line(50, 110, 50, 50);
        Line flechaY1 = new Line(50, 110, 40, 100);
        Line flechaY2 = new Line(50, 110, 60, 100);

        Line letraYIzq = new Line(50, 120, 45, 115);
        Line letraYDer = new Line(50, 120, 55, 115);
        Line LetraEjeY = new Line(50, 120, 50, 127);
        simulationPane.getChildren().addAll(ejeY, flechaY1, flechaY2, letraYIzq, letraYDer, LetraEjeY);
    }
    private void simulateParticleMotion(double electricField, double magneticFieldA, double magneticFieldB, double initialVelocity) {
        // Clear the previous simulation
        simulationPane.getChildren().clear();
        drawMassSpectrometer(electricField, magneticFieldA, magneticFieldB);

        // Create the particle
        particle = new Circle(5, Color.RED);
        particle.setCenterX(250);
        particle.setCenterY(40);

        simulationPane.getChildren().add(particle);

        // Path for the particle
        particlePath = new Path();
        particlePath.setStroke(Color.GRAY);
        particlePath.setStrokeWidth(1.5);
        particlePath.getElements().add(new MoveTo(particle.getCenterX(), particle.getCenterY()));

        simulationPane.getChildren().add(particlePath);


        //Vector fuerza electrica a particula
        if (electricField >0) {
            vectorFuerzaElectrica = new Line(particle.getCenterX(), particle.getCenterY(), particle.getCenterX() - (electricField * 25), particle.getCenterY());
            vectorFuerzaElectrica.setStroke(Color.BLUE);

            vectorFuerzaElectricaFlechaUno = new Line(5 + (particle.getCenterX() - (electricField * 25)), particle.getCenterY() + 5, (particle.getCenterX() - (electricField * 25)), particle.getCenterY());
            vectorFuerzaElectricaFlechaDos = new Line(5 + (particle.getCenterX() - (electricField * 25)), particle.getCenterY() - 5, (particle.getCenterX() - (electricField * 25)), particle.getCenterY());
            vectorFuerzaElectricaFlechaDos.setStroke(Color.BLUE);
            vectorFuerzaElectricaFlechaUno.setStroke(Color.BLUE);

            simulationPane.getChildren().addAll(vectorFuerzaElectrica, vectorFuerzaElectricaFlechaUno, vectorFuerzaElectricaFlechaDos);
        }

        //Vector fuerza electrica a particula
        if (magneticFieldA !=0) {
            vectorFuerzaMagnetica = new Line(particle.getCenterX(), particle.getCenterY(), particle.getCenterX() + (magneticFieldA * initialVelocity * 25), particle.getCenterY());
            vectorFuerzaMagnetica.setStroke(Color.GREEN);
            if (magneticFieldA > 0) {
                vectorFuerzaMagneticaFlechaUno = new Line(5+ (particle.getCenterX() + (magneticFieldA * initialVelocity * 25)), particle.getCenterY(), (particle.getCenterX() + (magneticFieldA * initialVelocity * 25)), particle.getCenterY()+ 5);
                vectorFuerzaMagneticaFlechaDos = new Line((5+ particle.getCenterX() + (magneticFieldA * initialVelocity * 25)), particle.getCenterY(),  (particle.getCenterX() + (magneticFieldA * initialVelocity * 25)), particle.getCenterY()-5);
                vectorFuerzaMagneticaFlechaUno.setStroke(Color.GREEN);
                vectorFuerzaMagneticaFlechaDos.setStroke(Color.GREEN);

                simulationPane.getChildren().addAll(vectorFuerzaMagnetica, vectorFuerzaMagneticaFlechaUno, vectorFuerzaMagneticaFlechaDos);
            }
            else {
                vectorFuerzaMagneticaFlechaUno = new Line(5 + (particle.getCenterX() + (magneticFieldA * initialVelocity * 25)), particle.getCenterY() +5, (particle.getCenterX() + (magneticFieldA * initialVelocity * 25)), particle.getCenterY());
                vectorFuerzaMagneticaFlechaDos = new Line(5 + (particle.getCenterX() + (magneticFieldA * initialVelocity * 25)), particle.getCenterY() -5, (particle.getCenterX() + (magneticFieldA * initialVelocity * 25)), particle.getCenterY());
                vectorFuerzaMagneticaFlechaUno.setStroke(Color.GREEN);
                vectorFuerzaMagneticaFlechaDos.setStroke(Color.GREEN);

                simulationPane.getChildren().addAll(vectorFuerzaMagnetica, vectorFuerzaMagneticaFlechaUno, vectorFuerzaMagneticaFlechaDos);
            }
        }
        // Constants
        double charge = 1.0;  // Charge of the particle
        double mass = 1.0;    // Mass of the particle

        // Compute the velocity in the x direction from the electric field and the initial velocity
        double[] velocityX = {0};
        double[] velocityY = {initialVelocity};

        // Animation timeline
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        // KeyFrame for animation
        final boolean[] regionUno = {true};
        KeyFrame keyFrame = new KeyFrame(Duration.millis(20), event -> {
            // Update position based on velocity
            double newX = particle.getCenterX() + velocityX[0] * 0.5;
            double newY = particle.getCenterY() + velocityY[0] * 0.5;

            // Update the path
            particlePath.getElements().add(new LineTo(newX, newY));

            // In region 1 (selector de velocidad)
            if (newY > 40 && newY < 300 && newX > 200 && newX < 300) {
                double forceElectric = charge * electricField;
                double forceMagnetic = charge * velocityY[0] * magneticFieldA;
                double netForce = forceMagnetic - forceElectric;
                double accelerationX = netForce / mass;
                velocityX[0] += accelerationX * 0.5;

                if (electricField >0){
                    vectorFuerzaElectrica.setStartY(particle.getCenterY());
                    vectorFuerzaElectrica.setEndY(particle.getCenterY());
                    vectorFuerzaElectrica.setStartX(particle.getCenterX());
                    vectorFuerzaElectrica.setEndX(particle.getCenterX() - (electricField  * 25));

                    vectorFuerzaElectricaFlechaUno.setStartY(particle.getCenterY() + 5);
                    vectorFuerzaElectricaFlechaUno.setEndY(particle.getCenterY());
                    vectorFuerzaElectricaFlechaUno.setStartX(5 + (particle.getCenterX() - (electricField  * 25)));
                    vectorFuerzaElectricaFlechaUno.setEndX(particle.getCenterX() - (electricField  * 25));

                    vectorFuerzaElectricaFlechaDos.setStartY(particle.getCenterY() - 5);
                    vectorFuerzaElectricaFlechaDos.setEndY(particle.getCenterY());
                    vectorFuerzaElectricaFlechaDos.setStartX(5 + (particle.getCenterX() - (electricField  * 25)));
                    vectorFuerzaElectricaFlechaDos.setEndX(particle.getCenterX() - (electricField  * 25));
                }

                if (magneticFieldA!= 0){
                    vectorFuerzaMagnetica.setStartY(particle.getCenterY());
                    vectorFuerzaMagnetica.setEndY(particle.getCenterY());
                    vectorFuerzaMagnetica.setStartX(particle.getCenterX());
                    vectorFuerzaMagnetica.setEndX(particle.getCenterX() + (magneticFieldA * initialVelocity * 25));
                    if (magneticFieldA >0){

                        vectorFuerzaMagneticaFlechaUno.setStartY(particle.getCenterY());
                        vectorFuerzaMagneticaFlechaUno.setEndY(particle.getCenterY()+5);
                        vectorFuerzaMagneticaFlechaUno.setStartX( particle.getCenterX() + (magneticFieldA * initialVelocity * 25));
                        vectorFuerzaMagneticaFlechaUno.setEndX(-5 + particle.getCenterX() + (magneticFieldA * initialVelocity * 25));

                        vectorFuerzaMagneticaFlechaDos.setStartY(particle.getCenterY() );
                        vectorFuerzaMagneticaFlechaDos.setEndY(particle.getCenterY()- 5);
                        vectorFuerzaMagneticaFlechaDos.setStartX(particle.getCenterX() + (magneticFieldA * initialVelocity * 25));
                        vectorFuerzaMagneticaFlechaDos.setEndX(- 5 + particle.getCenterX() + (magneticFieldA * initialVelocity * 25));
                    }
                    else{
                        vectorFuerzaMagneticaFlechaUno.setStartY(particle.getCenterY()+5);
                        vectorFuerzaMagneticaFlechaUno.setEndY(particle.getCenterY());
                        vectorFuerzaMagneticaFlechaUno.setStartX(5 + (particle.getCenterX() + (magneticFieldA * initialVelocity * 25)));
                        vectorFuerzaMagneticaFlechaUno.setEndX(particle.getCenterX() + (magneticFieldA * initialVelocity * 25));

                        vectorFuerzaMagneticaFlechaDos.setStartY(particle.getCenterY() -5);
                        vectorFuerzaMagneticaFlechaDos.setEndY(particle.getCenterY());
                        vectorFuerzaMagneticaFlechaDos.setStartX(5 + (particle.getCenterX() + (magneticFieldA * initialVelocity * 25)));
                        vectorFuerzaMagneticaFlechaDos.setEndX(particle.getCenterX() + (magneticFieldA * initialVelocity * 25));
                    }
                }
            }

            // In region 2 (only magnetic field B')
            if (newY >= 300 && newY <= 500 && newX >= 100 && newX <= 400) {
                regionUno[0] = false;
                vectorFuerzaElectrica.setVisible(false);
                vectorFuerzaElectricaFlechaDos.setVisible(false);
                vectorFuerzaElectricaFlechaUno.setVisible(false);
//                vectorFuerzaMagnetica.setVisible(false);
                vectorFuerzaMagneticaFlechaUno.setVisible(false);
                vectorFuerzaMagneticaFlechaDos.setVisible(false);

                Vector3D vectorVelocidad = new Vector3D(velocityX[0], velocityY[0], 0);
                Vector3D vectorCampoMagnetico = new Vector3D(0, 0, magneticFieldB);
                Vector3D vectorFuerzaMagneticaV = vectorVelocidad.crossProduct(vectorCampoMagnetico);
                double accelerationY = vectorFuerzaMagneticaV.getY() / mass;
                double accelerationX = vectorFuerzaMagneticaV.getX() / mass;
                velocityY[0] += accelerationY * 0.5;
                velocityX[0] += accelerationX * 0.5;

                if (magneticFieldB!= 0){
                    vectorFuerzaMagnetica.setStartY(particle.getCenterY());
                    vectorFuerzaMagnetica.setStartX(particle.getCenterX());

                    double posFinalX = particle.getCenterX() + (vectorFuerzaMagneticaV.getX() * 70);
                    double posFinalY = particle.getCenterY() + (vectorFuerzaMagneticaV.getY() * 70);
                    vectorFuerzaMagnetica.setEndY(posFinalY);
                    vectorFuerzaMagnetica.setEndX(posFinalX);
                    }
                }


            // Update the particle position
            particle.setCenterX(newX);
            particle.setCenterY(newY);



//            Line vectorFuerzaElectricaFlechaUno = new Line(5 + (particle.getCenterX() - (electricField  * 25)), particle.getCenterY() + 5, (particle.getCenterX() - (electricField  * 25)), particle.getCenterY());
//            Line vectorFuerzaElectricaFlechaDos = new Line(5+(particle.getCenterX() - (electricField  * 25)), particle.getCenterY() - 5, (particle.getCenterX() - (electricField  * 25)), particle.getCenterY());

            // Stop the animation if the particle goes out of bounds
            if (regionUno[0]) {
                if (newY <296){
                    if (newX >=300 || newX <=200 || newY <=30){
                        System.out.println("Pare en Region 1, IF 1");
                        timeline.stop();
                    }
                }
                else if (newY >= 296 && newY <=300) {
                    System.out.println(newX + ", " + newY);
                    if (newX <= 248 || newX >= 252) {
                        System.out.println("Pare en Region 1, IF 2");
                        timeline.stop();
                    }
                }
            }
            else if (newX >= 400 || newY >= 500 || newY < 300 || newX <= 100){
                System.out.println("Pare en REgion 2");
                timeline.stop();
            }

        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
