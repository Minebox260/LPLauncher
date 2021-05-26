package fr.minebox260.lplauncher.ui.panels.includes;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import fr.minebox260.lplauncher.Main;
import fr.minebox260.lplauncher.ui.PanelManager;
import fr.minebox260.lplauncher.ui.panel.Panel;
import fr.minebox260.lplauncher.ui.panels.HomePanel;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class TopPanel extends Panel {

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);
        GridPane topBar = this.layout;
        this.layout.setStyle("-fx-background-color: rgb(31,35,37);");
        GridPane topBarButton = new GridPane();

        this.layout.getChildren().add(topBarButton);
        Label title = new Label(Main.launcherName + " Launcher Version " + Main.version);
        this.layout.getChildren().add(title);
        title.setFont(Font.font("Consolas", FontWeight.THIN, FontPosture.REGULAR, 18.0f));
        title.setStyle("-fx-text-fill: white;");
        GridPane.setHalignment(title, HPos.CENTER);
        topBarButton.setMinWidth(100.0d);
        topBarButton.setMaxWidth(100.0d);
        GridPane.setHgrow(topBarButton, Priority.ALWAYS);
        GridPane.setVgrow(topBarButton, Priority.ALWAYS);
        GridPane.setHalignment(topBarButton, HPos.RIGHT);

        MaterialDesignIconView close = new MaterialDesignIconView(MaterialDesignIcon.WINDOW_CLOSE);
        MaterialDesignIconView maximize = new MaterialDesignIconView(MaterialDesignIcon.WINDOW_MAXIMIZE);
        MaterialDesignIconView hide = new MaterialDesignIconView(MaterialDesignIcon.WINDOW_MINIMIZE);
        GridPane.setVgrow(close, Priority.ALWAYS);
        GridPane.setVgrow(maximize, Priority.ALWAYS);
        GridPane.setVgrow(hide, Priority.ALWAYS);

        close.setFill(Color.WHITE);
        close.setOpacity(0.80f);
        close.setSize("18.0px");
        close.setOnMouseEntered(e->close.setOpacity(1.0f));
        close.setOnMouseExited(e->close.setOpacity(0.70f));
        close.setOnMouseClicked(e-> {
            HomePanel.isUpdating.set(false);
            System.exit(0);
        });
        close.setTranslateX(70.0d);

        maximize.setFill(Color.WHITE);
        maximize.setOpacity(0.80f);
        maximize.setSize("16.0px");
        maximize.setOnMouseEntered(e->maximize.setOpacity(1.0f));
        maximize.setOnMouseExited(e->maximize.setOpacity(0.70f));
        maximize.setOnMouseClicked(e->this.panelManager.getStage().setMaximized(!this.panelManager.getStage().isMaximized()));
        maximize.setTranslateX(50.0d);

        hide.setFill(Color.WHITE);
        hide.setOpacity(0.80f);
        hide.setSize("18.0px");
        hide.setOnMouseEntered(e->hide.setOpacity(1.0f));
        hide.setOnMouseExited(e->hide.setOpacity(0.70f));
        hide.setOnMouseClicked(e->this.panelManager.getStage().setIconified(true));
        hide.setTranslateX(25.0d);
        topBarButton.getChildren().addAll(close, maximize, hide);
    }
}
