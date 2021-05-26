package fr.minebox260.lplauncher;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FxApplication extends Application {

    @Override
    public void start(Stage stage) {
        Main.logger.log("Initialisation de l'interface");
        stage.getIcons().add(new Image(Main.class.getResource("/icon.png").toExternalForm()));
        new LPLauncher().init(stage);
    }
}
