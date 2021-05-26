package fr.minebox260.lplauncher.ui;

import fr.arinonia.arilibfx.AriLibFX;
import fr.arinonia.arilibfx.ui.utils.ResizeHelper;
import fr.minebox260.lplauncher.LPLauncher;
import fr.minebox260.lplauncher.Main;
import fr.minebox260.lplauncher.ui.panel.iPanel;
import fr.minebox260.lplauncher.ui.panels.includes.TopPanel;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.net.MalformedURLException;

public class PanelManager {



    private final LPLauncher LPLauncher;
    private final Stage stage;
    private final TopPanel topPanel = new TopPanel();
    private final GridPane centerPanel = new GridPane();

    public PanelManager(fr.minebox260.lplauncher.LPLauncher LPLauncher, Stage stage) {
        this.LPLauncher = LPLauncher;
        this.stage = stage;
    }

    public void init() {
        this.stage.setTitle(Main.title);
        this.stage.setMinWidth(1280);
        this.stage.setWidth(1280);
        this.stage.setMinHeight(720);
        this.stage.setHeight(720);
        this.stage.initStyle(StageStyle.UNDECORATED);
        this.stage.centerOnScreen();
        this.stage.show();

        GridPane layout = new GridPane();
        if (Main.getSaver().get("backgroundName")!=null) {
            if (new File(Main.LP_DIR, Main.getSaver().get("backgroundName")).exists()) {
                try {
                    layout.setStyle(AriLibFX.setResponsiveBackground(new File(Main.LP_DIR, Main.getSaver().get("backgroundName")).toURI().toURL().toExternalForm()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                Main.getSaver().remove("backgroundName");
                layout.setStyle(AriLibFX.setResponsiveBackground(Main.class.getResource("/background.png").toExternalForm()));
            }
        } else {
            layout.setStyle(AriLibFX.setResponsiveBackground(Main.class.getResource("/background.png").toExternalForm()));
        }

        this.stage.setScene(new Scene(layout));

        RowConstraints topPanelConstraints = new RowConstraints();
        topPanelConstraints.setValignment(VPos.TOP);
        topPanelConstraints.setMinHeight(25);
        topPanelConstraints.setMaxHeight(25);
        layout.getRowConstraints().addAll(topPanelConstraints, new RowConstraints());
        layout.add(this.topPanel.getLayout(),0,0);
        this.topPanel.init(this);

        layout.add(this.centerPanel,0,1);
        GridPane.setVgrow(this.centerPanel, Priority.ALWAYS);
        GridPane.setHgrow(this.centerPanel, Priority.ALWAYS);
        ResizeHelper.addResizeListener(this.stage);

        Main.logger.log("Initialisation du PanelManager terminée");
    }

    public void showPanel(iPanel panel) {
        this.centerPanel.getChildren().clear();
        this.centerPanel.getChildren().add(panel.getLayout());
        panel.init(this);
        panel.onShow();
    }

    public Stage getStage() {
        return stage;
    }

    public LPLauncher getLPLauncher() {
        return LPLauncher;
    }

    public TopPanel getTopPanel() {
        return topPanel;
    }
}
