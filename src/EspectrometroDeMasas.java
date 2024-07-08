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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EspectrometroDeMasas extends Application {

    // Componentes gráficos de la simulación.
    private Pane panelDeSimulacion;
    private Circle particula;
    private Path rastroDeParticula;
    private Line vectorFuerzaElectrica;
    private Line puntaIzqVectorFuerzaElectrica;
    private Line puntaDerVectorFuerzaElectrica;
    private Line vectorFuerzaMagnetica;
    private Line puntaIzqVectorFuerzaMagnetica;
    private Line puntaDerVectorFuerzaMagnetica;

    private int nivel = 0;              // nivel de juego (0,1,2,3,4)
    private double carga = 1.0;   // Carga de la particula (C)
    private final double masa = 20.0;   // Masa de la particula (g) = 0,02 Kg

    @Override
    public void start(Stage escenarioPrincipal) {
        escenarioPrincipal.setTitle("Simulador de espectrometro de masas");

        // Sliders para la entrada de parametros.
        Slider sliderCampoElectrico = new Slider(0, 2, 1.5);
        Slider sliderCampoMagneticoA = new Slider(-1, 1, 0.5);
        Slider sliderCampoMagneticoB = new Slider( -1, 1, 0.3);
        Slider sliderVelocidadInicial = new Slider(2, 5, 3);

        // Seteo de valores de configuracion para sliders.
        setearValoresSlider(sliderCampoElectrico, 0.1 );
        setearValoresSlider(sliderCampoMagneticoA, 0.1 );
        setearValoresSlider(sliderCampoMagneticoB, 0.1 );
        setearValoresSlider(sliderVelocidadInicial, 0.25);

        // Campos de entrada de texto relacinado con cada slider.
        TextField valorCampoElectrico = crearCampoDeTexto(sliderCampoElectrico);
        TextField valorCampoMagneticoA = crearCampoDeTexto(sliderCampoMagneticoA);
        TextField valorCampoMagneticoB = crearCampoDeTexto(sliderCampoMagneticoB);
        TextField valorVelocidadInicial = crearCampoDeTexto(sliderVelocidadInicial);

        // Desabilitacion inicial de campos de entrada.
        valorCampoElectrico.setDisable(true);
        valorCampoMagneticoA.setDisable(true);
        valorCampoMagneticoB.setDisable(true);
        valorVelocidadInicial.setDisable(true);

        // Desabilitacion inicial de sliders.
        sliderVelocidadInicial.setDisable(true);
        sliderCampoMagneticoA.setDisable(true);
        sliderCampoMagneticoB.setDisable(true);
        sliderCampoElectrico.setDisable(true);

        // Etiquetas para sliders.
        Label etiquetaCampoElectrico = new Label("Campo Eléctrico (N / C)");
        Label etiquetaCampoMagneticoA = new Label("Campo Magnético A (T)");
        Label etiquetaCampoMagneticoB = new Label("Campo Magnético B (T)");
        Label etiquetaVelocidadInicial = new Label("Velocidad Inicial (m / s)");

        // Botones.
        Button botonSimular = new Button("Simular");
        Button reiniciarNiveles = new Button("Reiniciar niveles");
        Button botonSiguienteNivel = new Button("Siguiente nivel");

        // Contenedor de botones.
        VBox contenedorBotones = new VBox(10);
        contenedorBotones.getChildren().addAll(botonSimular, reiniciarNiveles, botonSiguienteNivel);

        // Panel de simulación.
        panelDeSimulacion = new Pane();
        panelDeSimulacion.setPrefSize(600, 600);

        // Dieño principal.
        BorderPane raiz = new BorderPane();

        raiz.setBottom(contenedorBotones);
        raiz.setCenter(panelDeSimulacion);

        // Seteo de escena.
        Scene escena = new Scene(raiz, 1100, 600);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.show();

        // Dibujo inicial de diagrama de espectrometro de masas.
        dibujarDiagramaEspectrometroDeMasas();

        // Dibujo inicial de grilla con ecuaciones, sliders y campos de texto.
        actualizarGrilla(etiquetaCampoElectrico, sliderCampoElectrico, valorCampoElectrico,
                etiquetaCampoMagneticoA, sliderCampoMagneticoA, valorCampoMagneticoA,
                etiquetaCampoMagneticoB, sliderCampoMagneticoB, valorCampoMagneticoB,
                etiquetaVelocidadInicial, sliderVelocidadInicial, valorVelocidadInicial, raiz, nivel);

        // Acción del botón simular.
        botonSimular.setOnAction(_ -> {
            // Toma los valores de cada slider.
            double campoElectrico = sliderCampoElectrico.getValue();
            double campoMagneticoA = sliderCampoMagneticoA.getValue();
            double campoMagneticoB = sliderCampoMagneticoB.getValue();
            double velocidadInicial = sliderVelocidadInicial.getValue();

            actualizarGrilla(etiquetaCampoElectrico, sliderCampoElectrico, valorCampoElectrico,
                    etiquetaCampoMagneticoA, sliderCampoMagneticoA, valorCampoMagneticoA,
                    etiquetaCampoMagneticoB, sliderCampoMagneticoB, valorCampoMagneticoB,
                    etiquetaVelocidadInicial, sliderVelocidadInicial, valorVelocidadInicial, raiz, nivel);

            // Simulación del movimiento de la particula.
            simularMovivientoDeParticula(campoElectrico, campoMagneticoA, campoMagneticoB, velocidadInicial);
            // Dibujar el diagrama restante en base a los valores de campo electrico y magnetico.
            dibujarEspectrometroDeMasas(campoElectrico, campoMagneticoA, campoMagneticoB);
            // Actualizar diagrama de espectrometro de masas.
            dibujarDiagramaEspectrometroDeMasas();

        });

        // Acción del botón reiniciar niveles.
        reiniciarNiveles.setOnAction(_ -> {
            sliderCampoElectrico.setValue(1);
            sliderCampoMagneticoA.setValue(0.5);
            sliderCampoMagneticoB.setValue(0.5);
            sliderVelocidadInicial.setValue(3);
            nivel = 0;
            actualizarGrilla(etiquetaCampoElectrico, sliderCampoElectrico, valorCampoElectrico,
                    etiquetaCampoMagneticoA, sliderCampoMagneticoA, valorCampoMagneticoA,
                    etiquetaCampoMagneticoB, sliderCampoMagneticoB, valorCampoMagneticoB,
                    etiquetaVelocidadInicial, sliderVelocidadInicial, valorVelocidadInicial, raiz, nivel);
        });

        // Acción del boton siguiente nivel.
        botonSiguienteNivel.setOnAction(_ -> {
            nivel +=1;
            if (nivel == 1){
                sliderCampoMagneticoA.setDisable(false);
                valorCampoMagneticoA.setDisable(false);

                valorCampoElectrico.setDisable(true);
                valorCampoMagneticoB.setDisable(true);
                valorVelocidadInicial.setDisable(true);

                sliderVelocidadInicial.setDisable(true);
                sliderCampoMagneticoB.setDisable(true);
                sliderCampoElectrico.setDisable(true);

                sliderCampoElectrico.setValue(1.6);
                sliderCampoMagneticoA.setValue(0.6);
                sliderCampoMagneticoB.setValue(0);
                sliderVelocidadInicial.setValue(2.0);
                carga = 2;

                actualizarGrilla(etiquetaCampoElectrico, sliderCampoElectrico, valorCampoElectrico,
                        etiquetaCampoMagneticoA, sliderCampoMagneticoA, valorCampoMagneticoA,
                        etiquetaCampoMagneticoB, sliderCampoMagneticoB, valorCampoMagneticoB,
                        etiquetaVelocidadInicial, sliderVelocidadInicial, valorVelocidadInicial, raiz, nivel);
            }
            else if (nivel == 2){
                valorCampoMagneticoB.setDisable(false);
                sliderCampoMagneticoB.setDisable(false);

                sliderCampoElectrico.setValue(0.78);
                sliderCampoMagneticoA.setValue(1);
                sliderCampoMagneticoB.setValue(0.13);
                sliderVelocidadInicial.setValue(3);
                carga = 3;

                actualizarGrilla(etiquetaCampoElectrico, sliderCampoElectrico, valorCampoElectrico,
                        etiquetaCampoMagneticoA, sliderCampoMagneticoA, valorCampoMagneticoA,
                        etiquetaCampoMagneticoB, sliderCampoMagneticoB, valorCampoMagneticoB,
                        etiquetaVelocidadInicial, sliderVelocidadInicial, valorVelocidadInicial, raiz, nivel);

            }
            else if (nivel == 3){
                sliderCampoElectrico.setValue(0);
                sliderCampoMagneticoA.setValue(0.5);
                sliderCampoMagneticoB.setValue(0.4);
                sliderVelocidadInicial.setValue(5);

                sliderCampoElectrico.setDisable(false);
                valorCampoElectrico.setDisable(false);
                carga = 7;

                actualizarGrilla(etiquetaCampoElectrico, sliderCampoElectrico, valorCampoElectrico,
                        etiquetaCampoMagneticoA, sliderCampoMagneticoA, valorCampoMagneticoA,
                        etiquetaCampoMagneticoB, sliderCampoMagneticoB, valorCampoMagneticoB,
                        etiquetaVelocidadInicial, sliderVelocidadInicial, valorVelocidadInicial, raiz, nivel);
            }
            else if (nivel == 4) {
                sliderVelocidadInicial.setDisable(false);
                valorVelocidadInicial.setDisable(false);
                actualizarGrilla(etiquetaCampoElectrico, sliderCampoElectrico, valorCampoElectrico,
                        etiquetaCampoMagneticoA, sliderCampoMagneticoA, valorCampoMagneticoA,
                        etiquetaCampoMagneticoB, sliderCampoMagneticoB, valorCampoMagneticoB,
                        etiquetaVelocidadInicial, sliderVelocidadInicial, valorVelocidadInicial, raiz, nivel);
                nivel = 0;
            }
        });
    }

    private static void setearValoresSlider(Slider slider, double incremento) {
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(0.5);
        slider.setMinorTickCount(1);
        slider.setBlockIncrement(incremento);
    }

    private void actualizarGrilla(Label etiquetaCampoElectrico, Slider sliderCampoElectrico,
                                  TextField valorCampoElectrico, Label etiquetaCampoMagneticoA,
                                  Slider sliderCampoMagneticoA, TextField valorCampoMagneticoA,
                                  Label etiquetaCampoMagneticoB, Slider sliderCampoMagneticoB,
                                  TextField valorCampoMagneticoB, Label etiquetaVelocidadInicial,
                                  Slider sliderVelocidadInicial, TextField valorVelocidadInicial,
                                  BorderPane raiz, int nivel) {

        GridPane grillaDeSliders = new GridPane(10,10);

        grillaDeSliders.add(etiquetaCampoElectrico, 0, 0);
        grillaDeSliders.add(sliderCampoElectrico, 1, 0);
        grillaDeSliders.add(valorCampoElectrico, 2, 0);

        grillaDeSliders.add(etiquetaCampoMagneticoA, 0, 1);
        grillaDeSliders.add(sliderCampoMagneticoA, 1, 1);
        grillaDeSliders.add(valorCampoMagneticoA, 2, 1);

        grillaDeSliders.add(etiquetaCampoMagneticoB, 0, 2);
        grillaDeSliders.add(sliderCampoMagneticoB, 1, 2);
        grillaDeSliders.add(valorCampoMagneticoB, 2, 2);

        grillaDeSliders.add(etiquetaVelocidadInicial, 0, 3);
        grillaDeSliders.add(sliderVelocidadInicial, 1, 3);
        grillaDeSliders.add(valorVelocidadInicial, 2, 3);

        grillaDeSliders.add(new Label("--[  Ecuaciones: ]-- "), 1, 4);

        grillaDeSliders.add(new Label("FE = q • E = " + String.format("%.2f",Math.abs(carga * sliderCampoElectrico.getValue())) + " N"), 0, 5);
        grillaDeSliders.add(new Label("FBA = q • v • A = " + String.format("%.2f",Math.abs(carga * sliderVelocidadInicial.getValue()*sliderCampoMagneticoA.getValue())) + " N"   ), 1, 5);
        grillaDeSliders.add(new Label("FBB = q • v • B = " + String.format("%.2f",Math.abs(carga *sliderVelocidadInicial.getValue()*sliderCampoMagneticoB.getValue())) + " N"), 0, 6);
        double radio = Math.abs((masa * sliderVelocidadInicial.getValue())/(masa * sliderCampoMagneticoB.getValue()));
        grillaDeSliders.add(new Label("r = (m • v) ∕ (|q| • B) = " + String.format("%.2f",radio) + " cm = " + String.format("%.2f",radio*0.01)  + " m"), 1, 6);
        grillaDeSliders.add(new Label("Masa = 20 g = 0,02 Kg"), 2, 5);
        grillaDeSliders.add(new Label("Carga = " + carga + " C"), 2, 6);

        VBox titulosNiveles = new VBox(10);

        if (nivel == 0){

            titulosNiveles.getChildren().addAll(
                    new Label("--[  Espectrometro de masas: ]-- "));
        }
        else if (nivel == 1){
            titulosNiveles.getChildren().addAll(
                    new Label("--[  Espectrometro de masas: ]-- "),
                    new Label("-- [ Nivel 1 ]--"),
                    new Label("""
                            -- Hallar A (Campo Magnético) tal que
                               la particula no se desvie en el
                               selector de velocidad --"""),
                    new Label("-- Velocidad inicial = 2 m/s--"),
                    new Label("-- Campo Eléctrico = 1,6 N/C--"),
                    new Label("-- Carga = 2 C--"));
        }
        else if (nivel == 2){
            titulosNiveles.getChildren().addAll(
                    new Label("--[  Espectrometro de masas: ] -- "),
                    new Label("-- [ Nivel 2 ] --"),
                    new Label("""
                            -- Hallar A (Campo Magnético) tal que
                               la particula no se desvie en el
                               selector de velocidad --
                            -- Hallar B (Campo Magnetico) tal
                               que la particula impacte en el
                               punto P --"""),
                    new Label("-- Velocidad inicial = 3 m/s --"),
                    new Label("-- Campo Eléctrico = 0,78 N/C--"),
                    new Label("-- Carga = 3 C--"));
        }
        else if (nivel == 3){
            titulosNiveles.getChildren().addAll(
                    new Label("--[  Espectrometro de masas: ]-- "),
                    new Label("-- [ Nivel 3 ] --"),
                    new Label("""
                            -- Hallar E (Campo Eléctrico) y
                               A (Campo Magnético) tal que
                               la particula no se desvie en el
                               selector de velocidad --
                            -- Hallar B (Campo Magnetico) tal
                               que la particula impacte en el
                               punto P --"""),
                    new Label("-- Velocidad inicial = 5 m/s --"),
                    new Label("-- Carga = 7 C--"));
        } else if (nivel == 4){
            titulosNiveles.getChildren().addAll(
                    new Label("--[  Espectrometro de masas: ]-- "),
                    new Label("-- [ Nivel 4 ] --"),
                    new Label("-- Experimentacion Libre --"));
        }

        // Colocacion en la pantalla principal del texto.
        raiz.setRight(grillaDeSliders);
        raiz.setLeft(titulosNiveles);
    }

    private TextField crearCampoDeTexto(Slider slider) {
        TextField campoDeTexto = new TextField(String.valueOf(slider.getValue()));
        campoDeTexto.setPrefWidth(50);

        slider.valueProperty().addListener((_, _, newValue) ->
                campoDeTexto.setText(String.format("%.2f", newValue.doubleValue()))
        );
        campoDeTexto.setOnAction(_ -> {
            try {
                double value = Double.parseDouble(campoDeTexto.getText());
                slider.setValue(value);
            } catch (NumberFormatException e) {
                campoDeTexto.setText(String.format("%.2f", slider.getValue()));
            }
        });
        return campoDeTexto;
    }

    private void dibujarDiagramaEspectrometroDeMasas(){

        // Dibujo de región con campo magnetico B.
        Line limiteSuperiorIzq = new Line(50, 300, 240, 300);
        Line limiteSuperiorDer = new Line(259, 300, 450, 300);

        Line corteIzq = new Line(241, 300, 241, 305);
        Line corteDer = new Line(258, 300, 258, 305);

        Rectangle fondoCampoB = new Rectangle(50, 300, 400, 200);

        limiteSuperiorIzq.setStrokeWidth(5);
        limiteSuperiorDer.setStrokeWidth(5);
        limiteSuperiorDer.setStroke(Color.GRAY);
        limiteSuperiorIzq.setStroke(Color.GRAY);

        corteIzq.setStrokeWidth(3);
        corteDer.setStrokeWidth(3);
        corteDer.setStroke(Color.GRAY);
        corteIzq.setStroke(Color.GRAY);

        fondoCampoB.setStroke(Color.GRAY);
        fondoCampoB.setFill(Color.rgb(245,245,120, 0.2));

        panelDeSimulacion.getChildren().addAll( fondoCampoB, limiteSuperiorIzq, limiteSuperiorDer,corteIzq,corteDer);
        dibujarEjes();
    }

    private void dibujarEspectrometroDeMasas(double campoElectrico, double campoMagneticoA, double campoMagneticoB) {
        //Dibujo de campo electrico y selector de velocidad.
        dibujarCampoElectrico(campoElectrico);

        // Dibujo de campo magnetico A.
        dibujarCampoMagnetico(campoMagneticoA, 60,300,175,355);

        // Dibujo de campo magnetico B.
        dibujarCampoMagnetico(campoMagneticoB,312,500,70,450);

        // Dibujo de letras indicativas.
        Text letraE = new Text(277, 78, "E");
        Text letraA = new Text(340, 183, "A");
        Text letraB = new Text(460, 406, "B");

        letraE.setStroke(Color.ORANGERED);
        letraA.setStroke(Color.GREEN);
        letraB.setStroke(Color.GREEN);

        letraE.setScaleX(1.3);
        letraE.setScaleY(1.3);
        letraA.setScaleX(1.3);
        letraA.setScaleY(1.3);
        letraB.setScaleX(1.3);
        letraB.setScaleY(1.3);

        panelDeSimulacion.getChildren().addAll(letraE, letraA, letraB);

        // Dibujo de puntos degun el nivel.
        if (nivel == 2){
            Circle puntoP = new Circle(338, 297, 5, Color.BLACK);
            Text letraP = new Text(340, 290, "P");
            panelDeSimulacion.getChildren().addAll(puntoP,letraP);

        } else if (nivel == 3){
            Circle puntoP = new Circle(88, 297, 5, Color.BLACK);
            Text letraP = new Text(87, 290, "P");
            panelDeSimulacion.getChildren().addAll(puntoP, letraP);

        }

    }

    private void dibujarCampoElectrico(double campoElectrico) {
        if (campoElectrico > 0) {
            Rectangle limiteIzq = new Rectangle(193, 50, 7, 250);
            Rectangle limiteDer = new Rectangle(300, 50, 7, 250);

            limiteIzq.setStroke(Color.BLUE);
            limiteIzq.setFill(Color.CADETBLUE);
            limiteDer.setStroke(Color.RED);
            limiteDer.setFill(Color.DARKORANGE);

            panelDeSimulacion.getChildren().addAll(limiteIzq, limiteDer);

            for (int y = 60; y < 300; y += 40) {
                double longitudDeFlecha = campoElectrico * 40;  // Escala de la flecha de campo electrico.
                Line flecha = new Line(240 - longitudDeFlecha / 2, y, 260 + longitudDeFlecha / 2, y);
                Line cabezaDeFlechaIzq = new Line(240 - longitudDeFlecha / 2 + 10, y - 5, 240 - longitudDeFlecha / 2, y);
                Line cabezaDeFlechaDer = new Line(240 - longitudDeFlecha / 2 + 10, y + 5, 240 - longitudDeFlecha / 2, y);

                flecha.setStroke(Color.DARKORANGE);
                flecha.setStrokeWidth(2);
                flecha.setOpacity(10);

                cabezaDeFlechaIzq.setStroke(Color.DARKORANGE);
                cabezaDeFlechaIzq.setStrokeWidth(2);
                cabezaDeFlechaIzq.setOpacity(10);

                cabezaDeFlechaDer.setStroke(Color.DARKORANGE);
                cabezaDeFlechaDer.setStrokeWidth(2);
                cabezaDeFlechaDer.setOpacity(10);

                panelDeSimulacion.getChildren().addAll( flecha, cabezaDeFlechaIzq, cabezaDeFlechaDer);
            }

        } else {
            Rectangle limiteIzq = new Rectangle(193, 50, 5, 250);
            Rectangle limiteDer = new Rectangle(300, 50, 5, 250);

            panelDeSimulacion.getChildren().addAll(limiteIzq, limiteDer);
        }
    }

    private void dibujarCampoMagnetico(double campo, int yi, int yf, int xi, int xf){
        if (campo != 0) {
            for (int y = yi; y < yf; y += 30) {
                for (int x = xi; x < xf; x += 30) {
                    if (campo > 0) {
                        double tamanioDeCruz = campo * 10;  // Escala de la cruz
                        Line lineaUno = new Line(x - tamanioDeCruz / 2, y - tamanioDeCruz / 2, x + tamanioDeCruz / 2, y + tamanioDeCruz / 2);
                        Line lineaDos = new Line(x - tamanioDeCruz / 2, y + tamanioDeCruz / 2, x + tamanioDeCruz / 2, y - tamanioDeCruz / 2);

                        lineaUno.setStroke(Color.GREEN);
                        lineaDos.setStroke(Color.GREEN);
                        lineaUno.setStrokeWidth(1.3);
                        lineaDos.setStrokeWidth(1.3);

                        panelDeSimulacion.getChildren().addAll(lineaUno, lineaDos);
                    } else {
                        double radioDePunto = -campo * 5;  // Escala del punto
                        Circle punto = new Circle(x, y, radioDePunto, Color.GREEN);
                        panelDeSimulacion.getChildren().add(punto);
                    }
                }
            }
        }
    }

    private void dibujarEjes() {
        // Eje X
        Line ejeX = new Line(50, 50, 110, 50);
        Line flechaX1 = new Line(110, 50, 105, 55);
        Line flechaX2 = new Line(110, 50, 105, 45);

        Line cruzX1 = new Line( 120 - 2.5,  50 - 2.5,  120 + 2.5,  50 + 2.5);
        Line cruzX2 = new Line(120 + 2.5, 50 - 2.5, 120 - 2.5, 50 + 2.5);

        panelDeSimulacion.getChildren().addAll(ejeX, flechaX1, flechaX2, cruzX1, cruzX2);

        // Eje Y
        Line ejeY = new Line(50, 110, 50, 50);
        Line flechaY1 = new Line(50, 110, 45, 105);
        Line flechaY2 = new Line(50, 110, 55, 105);

        Line letraYIzq = new Line( 50 - 2.5,  120 - 2.5,  50,  120);
        Line letraYDer = new Line(50 + 2.5, 120 - 2.5, 50 - 2.5, 120 + 2.5);

        letraYIzq.setStrokeWidth(1.5);
        letraYDer.setStrokeWidth(1.5);

        panelDeSimulacion.getChildren().addAll(ejeY, flechaY1, flechaY2, letraYIzq, letraYDer);
    }

    private void simularMovivientoDeParticula(double campoElectrico, double campoMagneticoA, double campoMagneticoB, double velocidadInicial) {
        // Borrar la simulacion anterior
        panelDeSimulacion.getChildren().clear();
        dibujarEspectrometroDeMasas(campoElectrico, campoMagneticoA, campoMagneticoB);

        // Crear la particula.
        particula = new Circle(5, Color.RED);
        particula.setCenterX(250);
        particula.setCenterY(40);

        panelDeSimulacion.getChildren().add(particula);

        // Crear rastro de la particula.
        rastroDeParticula = new Path();
        rastroDeParticula.setStroke(Color.GRAY);
        rastroDeParticula.setStrokeWidth(1.5);
        rastroDeParticula.getElements().add(new MoveTo(particula.getCenterX(), particula.getCenterY()));

        panelDeSimulacion.getChildren().add(rastroDeParticula);

        // Velocidad inicial en X y en Y
        double[] velocidadX = {0};
        double[] velocidadY = {velocidadInicial};

        // Crear vector de fuerza electrica.
        if (campoElectrico >0) {
            vectorFuerzaElectrica = new Line(particula.getCenterX(), particula.getCenterY(), particula.getCenterX() - (campoElectrico * 25), particula.getCenterY());
            vectorFuerzaElectrica.setStroke(Color.ORANGERED);
            vectorFuerzaElectrica.setStrokeWidth(2);

            puntaIzqVectorFuerzaElectrica = new Line(5 + (particula.getCenterX() - (campoElectrico * 25)), particula.getCenterY() + 5, (particula.getCenterX() - (campoElectrico * 25)), particula.getCenterY());
            puntaDerVectorFuerzaElectrica = new Line(5 + (particula.getCenterX() - (campoElectrico * 25)), particula.getCenterY() - 5, (particula.getCenterX() - (campoElectrico * 25)), particula.getCenterY());
            puntaDerVectorFuerzaElectrica.setStroke(Color.ORANGERED);
            puntaIzqVectorFuerzaElectrica.setStroke(Color.ORANGERED);
            puntaDerVectorFuerzaElectrica.setStrokeWidth(2);
            puntaIzqVectorFuerzaElectrica.setStrokeWidth(2);

            panelDeSimulacion.getChildren().addAll(vectorFuerzaElectrica, puntaIzqVectorFuerzaElectrica, puntaDerVectorFuerzaElectrica);
        }

        // Crear vector de fuerza magnetica.
        if (campoMagneticoA !=0) {
            vectorFuerzaMagnetica = new Line(particula.getCenterX(), particula.getCenterY(), particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25), particula.getCenterY());
            vectorFuerzaMagnetica.setStroke(Color.GREEN);
            vectorFuerzaMagnetica.setStrokeWidth(2);
            if (campoMagneticoA > 0) {
                puntaIzqVectorFuerzaMagnetica = new Line(5+ (particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25)), particula.getCenterY(), (particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25)), particula.getCenterY()+ 5);
                puntaDerVectorFuerzaMagnetica = new Line((5+ particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25)), particula.getCenterY(),  (particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25)), particula.getCenterY()-5);

            }
            else {
                puntaIzqVectorFuerzaMagnetica = new Line(5 + (particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25)), particula.getCenterY() +5, (particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25)), particula.getCenterY());
                puntaDerVectorFuerzaMagnetica = new Line(5 + (particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25)), particula.getCenterY() -5, (particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25)), particula.getCenterY());

            }
            puntaIzqVectorFuerzaMagnetica.setStroke(Color.GREEN);
            puntaDerVectorFuerzaMagnetica.setStroke(Color.GREEN);
            puntaIzqVectorFuerzaMagnetica.setStrokeWidth(2);
            puntaDerVectorFuerzaMagnetica.setStrokeWidth(2);

            panelDeSimulacion.getChildren().addAll(vectorFuerzaMagnetica, puntaIzqVectorFuerzaMagnetica, puntaDerVectorFuerzaMagnetica);
        }

        // Linea de tiempo de la animación.
        Timeline lineaDeTiempo = new Timeline();
        lineaDeTiempo.setCycleCount(Timeline.INDEFINITE);

        // Fotograma clave
        final boolean[] regionUno = {true};
        KeyFrame fotograma = new KeyFrame(Duration.millis(20), _ -> {
            // Actualizar la posicion de la particula segun la velocidad.
            double nuevoX = particula.getCenterX() + velocidadX[0] * 0.5;
            double nuevoY = particula.getCenterY() + velocidadY[0] * 0.5;

            // Actualizar el rastro de la particula.
            rastroDeParticula.getElements().add(new LineTo(nuevoX, nuevoY));

            // En region 1: (selector de velocidad, campo magnetico A y campo electrico E)
            if (nuevoY > 40 && nuevoY < 300 && nuevoX > 200 && nuevoX < 300) {
                double fuerzaElectrica = carga * campoElectrico;
                double fuerzaMagnetica = carga * velocidadY[0] * campoMagneticoA;
                // Ftot = m * a => a = Ftot / m
                // Ftot = F.Lorentz = -qE + qVBSen(90)
                double fuerzaNeta = fuerzaMagnetica - fuerzaElectrica;
                double aceleracionEnX = fuerzaNeta / masa;
                velocidadX[0] += aceleracionEnX * 0.5;

                if (campoElectrico >0){
                    vectorFuerzaElectrica.setStartY(particula.getCenterY());
                    vectorFuerzaElectrica.setEndY(particula.getCenterY());
                    vectorFuerzaElectrica.setStartX(particula.getCenterX());
                    vectorFuerzaElectrica.setEndX(particula.getCenterX() - (campoElectrico  * 25));

                    puntaIzqVectorFuerzaElectrica.setStartY(particula.getCenterY() + 5);
                    puntaIzqVectorFuerzaElectrica.setEndY(particula.getCenterY());
                    puntaIzqVectorFuerzaElectrica.setStartX(5 + (particula.getCenterX() - (campoElectrico  * 25)));
                    puntaIzqVectorFuerzaElectrica.setEndX(particula.getCenterX() - (campoElectrico  * 25));

                    puntaDerVectorFuerzaElectrica.setStartY(particula.getCenterY() - 5);
                    puntaDerVectorFuerzaElectrica.setEndY(particula.getCenterY());
                    puntaDerVectorFuerzaElectrica.setStartX(5 + (particula.getCenterX() - (campoElectrico  * 25)));
                    puntaDerVectorFuerzaElectrica.setEndX(particula.getCenterX() - (campoElectrico  * 25));
                }

                if (campoMagneticoA!= 0){
                    vectorFuerzaMagnetica.setStartY(particula.getCenterY());
                    vectorFuerzaMagnetica.setEndY(particula.getCenterY());
                    vectorFuerzaMagnetica.setStartX(particula.getCenterX());
                    vectorFuerzaMagnetica.setEndX(particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25));
                    if (campoMagneticoA >0){

                        puntaIzqVectorFuerzaMagnetica.setStartY(particula.getCenterY());
                        puntaIzqVectorFuerzaMagnetica.setEndY(particula.getCenterY()+5);
                        puntaIzqVectorFuerzaMagnetica.setStartX( particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25));
                        puntaIzqVectorFuerzaMagnetica.setEndX(-5 + particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25));

                        puntaDerVectorFuerzaMagnetica.setStartY(particula.getCenterY() );
                        puntaDerVectorFuerzaMagnetica.setEndY(particula.getCenterY()- 5);
                        puntaDerVectorFuerzaMagnetica.setStartX(particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25));
                        puntaDerVectorFuerzaMagnetica.setEndX(- 5 + particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25));
                    }
                    else{
                        puntaIzqVectorFuerzaMagnetica.setStartY(particula.getCenterY()+5);
                        puntaIzqVectorFuerzaMagnetica.setEndY(particula.getCenterY());
                        puntaIzqVectorFuerzaMagnetica.setStartX(5 + (particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25)));
                        puntaIzqVectorFuerzaMagnetica.setEndX(particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25));

                        puntaDerVectorFuerzaMagnetica.setStartY(particula.getCenterY() -5);
                        puntaDerVectorFuerzaMagnetica.setEndY(particula.getCenterY());
                        puntaDerVectorFuerzaMagnetica.setStartX(5 + (particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25)));
                        puntaDerVectorFuerzaMagnetica.setEndX(particula.getCenterX() + (campoMagneticoA * velocidadInicial * 25));
                    }
                }
            }

            // En region 2 (Solo campo magnetico B)
            if (nuevoY >= 300 && nuevoY <= 500 && nuevoX >= 50 && nuevoX <= 450) {
                regionUno[0] = false;
                vectorFuerzaElectrica.setVisible(false);
                puntaDerVectorFuerzaElectrica.setVisible(false);
                puntaIzqVectorFuerzaElectrica.setVisible(false);
                puntaIzqVectorFuerzaMagnetica.setVisible(false);
                puntaDerVectorFuerzaMagnetica.setVisible(false);

                // Creo los vectores necesarios para realizar el producto vectorial.

                // Ftot = Fb = q (v x B) = (FbX, FbY, FbZ)
                // FtotX = FbX, FtotY = FbY, FtotZ = FbZ
                Vector3D vectorVelocidad3D = new Vector3D(carga * velocidadX[0], carga * velocidadY[0], 0);
                Vector3D vectorCampoMagnetico3D = new Vector3D(0, 0, campoMagneticoB);
                Vector3D vectorFuerzaMagnetica3D = vectorVelocidad3D.productoVectorial(vectorCampoMagnetico3D);
                // FbX = m * aX => aX = FbX / m
                // FbY = m * aY => aY = FbY / m

                double aceleracionY = vectorFuerzaMagnetica3D.getY() / masa;
                double aceleracionX = vectorFuerzaMagnetica3D.getX() / masa;
                velocidadY[0] += aceleracionY * 0.5;
                velocidadX[0] += aceleracionX * 0.5;

                if (campoMagneticoB!= 0){
                    vectorFuerzaMagnetica.setStartY(particula.getCenterY());
                    vectorFuerzaMagnetica.setStartX(particula.getCenterX());

                    double posFinalX = particula.getCenterX() + (vectorFuerzaMagnetica3D.getX() * 25);
                    double posFinalY = particula.getCenterY() + (vectorFuerzaMagnetica3D.getY() * 25);
                    vectorFuerzaMagnetica.setEndY(posFinalY);
                    vectorFuerzaMagnetica.setEndX(posFinalX);
                }
            }

            // Actualizar la posicion de la particula.
            particula.setCenterX(nuevoX);
            particula.setCenterY(nuevoY);

            // Se detiene la animacion si la particula se sale de los limites.
            if (regionUno[0]) {
                if (nuevoY <296){
                    if (nuevoX >=300 || nuevoX <=200 || nuevoY <=30){
                        lineaDeTiempo.stop();
                    }
                }
                else if (nuevoY >= 296 && nuevoY <=300) {
                    if (nuevoX <= 248 || nuevoX >= 252) {
                        lineaDeTiempo.stop();
                    }
                }
            }
            else if (nuevoX >= 450 || nuevoY >= 500 || nuevoY < 300 || nuevoX <= 50){
                lineaDeTiempo.stop();
            }
        });

        lineaDeTiempo.getKeyFrames().add(fotograma);
        lineaDeTiempo.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}