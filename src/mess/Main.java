package mess;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import mess.view.FensterController;

import java.io.IOException;

public class Main extends Application {
    private Stage primaryStage;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("MessApp");
        this.primaryStage.getIcons().add(new Image("mess/view/ruler.png"));
        initRootlayout();
    }


    private void initRootlayout() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/FensterLayout.fxml"));
        BorderPane rootLayout = loader.load();
        FensterController rootcontroller = loader.getController();
        rootcontroller.setMain(this);
        primaryStage.setTitle("MessApp");
        primaryStage.setScene(new Scene(rootLayout));
        primaryStage.setX(50);
        primaryStage.setY(10);

        primaryStage.show();
    }

    public Stage getStage() {
        return primaryStage;
    }

}
