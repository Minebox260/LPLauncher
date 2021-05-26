package fr.minebox260.lplauncher;

import fr.minebox260.lplauncher.ui.PanelManager;
import fr.minebox260.lplauncher.ui.panels.LoginPanel;
import javafx.stage.Stage;

public class LPLauncher {

    public void init(Stage stage) {
        PanelManager panelManager = new PanelManager(this, stage);
        Main.logger.log("Initialisation du PanelManager");
        panelManager.init();
        Main.logger.log("Affichage de l'interface de connexion");
        panelManager.showPanel(new LoginPanel());
    }
}
