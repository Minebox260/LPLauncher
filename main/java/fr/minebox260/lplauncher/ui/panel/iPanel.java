package fr.minebox260.lplauncher.ui.panel;

import fr.minebox260.lplauncher.ui.PanelManager;
import javafx.scene.layout.GridPane;

public interface iPanel {
    void init(PanelManager panelManager);
    GridPane getLayout();
    void onShow();
}
