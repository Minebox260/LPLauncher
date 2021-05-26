package fr.minebox260.lplauncher.ui.panels;

import fr.minebox260.lplauncher.Main;
import fr.minebox260.lplauncher.ui.PanelManager;
import fr.minebox260.lplauncher.ui.panel.Panel;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ErrorPanel extends Panel {

    public void init(PanelManager panelManager) {
        super.init(panelManager);
        GridPane errorPanel = new GridPane();

        errorPanel.setMaxWidth(500);
        errorPanel.setMinWidth(500);
        errorPanel.setMaxHeight(300);
        errorPanel.setMinHeight(300);

        GridPane.setVgrow(errorPanel, Priority.ALWAYS);
        GridPane.setHgrow(errorPanel, Priority.ALWAYS);
        GridPane.setValignment(errorPanel, VPos.CENTER);
        GridPane.setHalignment(errorPanel, HPos.CENTER);

        errorPanel.setStyle("-fx-background-color: #181818;");

        Label errorLabel = new Label("UNE ERREUR EST SURVENUE");
        GridPane.setVgrow(errorLabel, Priority.ALWAYS);
        GridPane.setHgrow(errorLabel, Priority.ALWAYS);
        GridPane.setValignment(errorLabel, VPos.TOP);
        errorLabel.setTranslateY(27);
        errorLabel.setTranslateX(37.5);
        errorLabel.setStyle("-fx-text-fill: #bcc6e7; -fx-font-size: 18px;");

        Separator errorSeparator = new Separator();
        GridPane.setVgrow(errorSeparator, Priority.ALWAYS);
        GridPane.setHgrow(errorSeparator, Priority.ALWAYS);
        GridPane.setValignment(errorSeparator, VPos.TOP);
        GridPane.setHalignment(errorSeparator, HPos.LEFT);
        errorSeparator.setTranslateY(60);
        errorSeparator.setTranslateX(37.5);
        errorSeparator.setMinWidth(325);
        errorSeparator.setMaxWidth(325);
        errorSeparator.setStyle("-fx-background-color: #fff; -fx-opacity: 50%;");

        Exception exception = Main.getErrorPanelException().get();
        String error = Main.getErrorPanelName().get();

        Main.logger.warn(error);
        exception.printStackTrace();

        Label errorName = new Label(error);
        GridPane.setVgrow(errorName, Priority.ALWAYS);
        GridPane.setHgrow(errorName, Priority.ALWAYS);
        GridPane.setValignment(errorName, VPos.TOP);
        errorName.setTranslateY(70);
        errorName.setTranslateX(37.5);
        errorName.setMaxWidth(400);
        errorName.setMaxHeight(50);
        errorName.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        String exceptionTrace = Stream
                .of(exception.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
        Label exceptionLabel = new Label(exceptionTrace);
        GridPane.setVgrow(exceptionLabel, Priority.ALWAYS);
        GridPane.setHgrow(exceptionLabel, Priority.ALWAYS);
        GridPane.setValignment(exceptionLabel, VPos.TOP);
        exceptionLabel.setTranslateY(130);
        exceptionLabel.setTranslateX(37.5);
        exceptionLabel.setMaxWidth(400);
        exceptionLabel.setMaxHeight(122);
        exceptionLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button returnButton = new Button("Retour");
        GridPane.setVgrow(returnButton, Priority.ALWAYS);
        GridPane.setHgrow(returnButton, Priority.ALWAYS);
        GridPane.setValignment(returnButton, VPos.BOTTOM);
        GridPane.setHalignment(returnButton, HPos.RIGHT);
        returnButton.setTranslateY(-20);
        returnButton.setTranslateX(-37.5);
        returnButton.setMinWidth(80);
        returnButton.setMinHeight(30);
        returnButton.getStylesheets().addAll(Main.class.getResource("/css/button.css").toExternalForm());
        returnButton.setOnMouseEntered(e-> this.layout.setCursor(Cursor.HAND));
        returnButton.setOnMouseExited(e-> this.layout.setCursor(Cursor.DEFAULT));
        returnButton.setOnMouseClicked(e-> {
            Main.setErrorPanelName(null);
            Main.setErrorPanelException(null);
            this.panelManager.showPanel(new HomePanel());
        });

        errorPanel.getChildren().addAll(errorLabel, errorSeparator, errorName, exceptionLabel, returnButton);
        this.layout.getChildren().add(errorPanel);






    }
}

