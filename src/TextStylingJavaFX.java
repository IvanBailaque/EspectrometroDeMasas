import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class TextStylingJavaFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        Text text1 = new Text("Texto en tamaño y estilo diferentes ");
        text1.setFont(Font.font("Arial", 20));

        Text text2 = new Text("JavaFX ");
        text2.setFont(Font.font("Verdana", 30));

        Text text3 = new Text("es genial!");
        text3.setFont(Font.font("Courier New", 25));

        TextFlow textFlow = new TextFlow(text1, text2, text3);

        StackPane root = new StackPane();
        root.getChildren().add(textFlow);

        Scene scene = new Scene(root, 400, 200);

        primaryStage.setTitle("Estilo de texto en JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
