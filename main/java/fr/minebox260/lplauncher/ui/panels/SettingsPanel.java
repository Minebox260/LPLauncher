package fr.minebox260.lplauncher.ui.panels;

import fr.arinonia.arilibfx.AriLibFX;
import fr.minebox260.lplauncher.Main;
import fr.minebox260.lplauncher.ui.PanelManager;
import fr.minebox260.lplauncher.ui.panel.Panel;
import fr.minebox260.lplauncher.SaverUtil;
import fr.theshark34.openlauncherlib.minecraft.GameInfos;
import fr.theshark34.openlauncherlib.minecraft.GameType;
import fr.theshark34.openlauncherlib.minecraft.GameVersion;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class SettingsPanel extends Panel {

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);
        GridPane settingsPanel = new GridPane();
        GridPane mainSettingsPanel = new GridPane();
        GridPane versionSettingsPanel = new GridPane();

        settingsPanel.setMaxWidth(450);
        settingsPanel.setMinWidth(450);
        settingsPanel.setMaxHeight(625);
        settingsPanel.setMinHeight(625);

        GridPane.setVgrow(settingsPanel, Priority.ALWAYS);
        GridPane.setHgrow(settingsPanel, Priority.ALWAYS);
        GridPane.setValignment(settingsPanel, VPos.CENTER);
        GridPane.setHalignment(settingsPanel, HPos.CENTER);

        RowConstraints bottomConstraints = new RowConstraints();
        bottomConstraints.setValignment(VPos.BOTTOM);
        bottomConstraints.setMaxHeight(250);
        settingsPanel.getRowConstraints().addAll(new RowConstraints(), bottomConstraints);
        settingsPanel.add(mainSettingsPanel, 0,0);
        settingsPanel.add(versionSettingsPanel, 0,1);

        GridPane.setVgrow(mainSettingsPanel, Priority.ALWAYS);
        GridPane.setHgrow(mainSettingsPanel, Priority.ALWAYS);
        GridPane.setVgrow(versionSettingsPanel, Priority.ALWAYS);
        GridPane.setHgrow(versionSettingsPanel, Priority.ALWAYS);

        mainSettingsPanel.setStyle("-fx-background-color: #181818;");
        versionSettingsPanel.setStyle("-fx-background-color: #181818;");


        this.layout.getChildren().add(settingsPanel);

        SaverUtil versionSaver = new SaverUtil(new File(Main.LP_DIR_CONFIG, Main.getSavedVersion()[0] + ".properties"));
        Label versionSettingsLabel = new Label("PARAMÈTRES DE LA VERSION : " + Main.getSavedVersion()[0]);
        GridPane.setVgrow(versionSettingsLabel, Priority.ALWAYS);
        GridPane.setHgrow(versionSettingsLabel, Priority.ALWAYS);
        GridPane.setValignment(versionSettingsLabel, VPos.TOP);
        versionSettingsLabel.setTranslateY(27);
        versionSettingsLabel.setTranslateX(37.5);
        versionSettingsLabel.setStyle("-fx-text-fill: #bcc6e7; -fx-font-size: 18px;");

        Separator versionSettingsSeparator = new Separator();
        GridPane.setVgrow(versionSettingsSeparator, Priority.ALWAYS);
        GridPane.setHgrow(versionSettingsSeparator, Priority.ALWAYS);
        GridPane.setValignment(versionSettingsSeparator, VPos.TOP);
        GridPane.setHalignment(versionSettingsSeparator, HPos.LEFT);
        versionSettingsSeparator.setTranslateY(60);
        versionSettingsSeparator.setTranslateX(37.5);
        versionSettingsSeparator.setMinWidth(325);
        versionSettingsSeparator.setMaxWidth(325);
        versionSettingsSeparator.setStyle("-fx-background-color: #fff; -fx-opacity: 50%;");

        Label argsLabel = new Label("Arguments à rajouter");
        GridPane.setVgrow(argsLabel, Priority.ALWAYS);
        GridPane.setHgrow(argsLabel, Priority.ALWAYS);
        GridPane.setValignment(argsLabel, VPos.TOP);
        GridPane.setHalignment(argsLabel, HPos.LEFT);
        argsLabel.setStyle("-fx-text-fill: #95bad3; -fx-font-size: 16px;");
        argsLabel.setTranslateY(75);
        argsLabel.setTranslateX(37.5);

        TextField argsField = new TextField();
        GridPane.setVgrow(argsField, Priority.ALWAYS);
        GridPane.setHgrow(argsField, Priority.ALWAYS);
        GridPane.setValignment(argsField, VPos.TOP);
        GridPane.setHalignment(argsField, HPos.LEFT);
        argsField.setMaxWidth(325);
        argsField.setMaxHeight(40);
        argsField.setTranslateX(37.5);
        argsField.setTranslateY(105);
        argsField.setStyle("-fx-background-color: #1e1e1e; -fx-font-size: 16px; -fx-text-fill: #e5e5e5");



        Separator argsSeparator = new Separator();
        GridPane.setVgrow(argsSeparator, Priority.ALWAYS);
        GridPane.setHgrow(argsSeparator, Priority.ALWAYS);
        GridPane.setValignment(argsSeparator, VPos.TOP);
        GridPane.setHalignment(argsSeparator, HPos.LEFT);
        argsSeparator.setTranslateY(146);
        argsSeparator.setTranslateX(37.5);
        argsSeparator.setMinWidth(325);
        argsSeparator.setMaxWidth(325);
        argsSeparator.setMaxHeight(1);
        argsSeparator.setStyle("-fx-background-color: #fff; -fx-opacity: 10%;");

        String[] ramArray = {"1 Go", "2 Go", "3 Go", "4 Go", "5 Go", "6 Go", "7 Go","8 Go","9 Go","10 Go"};
        List<String> ramCheckList = Arrays.asList("0","1","2","3","4","5","6","7","8","9");
        final ComboBox<String> ramSelectMenu = new ComboBox<>(FXCollections.observableArrayList(ramArray));

        if (versionSaver.get("ram")!=null) {
            if (ramCheckList.contains(versionSaver.get("ram"))) {
                ramSelectMenu.getSelectionModel().select(ramArray[Integer.parseInt(versionSaver.get("ram"))]);
            } else {
                versionSaver.remove("ram");
                if (Main.getSaver().get("selectedServer").equals("modded")) ramSelectMenu.getSelectionModel().select("4 Go");
                else ramSelectMenu.getSelectionModel().select("2 Go");
            }
        } else {
            if (Main.getSaver().get("selectedServer").equals("modded")) ramSelectMenu.getSelectionModel().select("4 Go");
            else ramSelectMenu.getSelectionModel().select("2 Go");
        }

        if (versionSaver.get("args")!=null) {
            argsField.setText(versionSaver.get("args"));
        }

        Label ramLabel = new Label("RAM");
        GridPane.setVgrow(ramLabel, Priority.ALWAYS);
        GridPane.setHgrow(ramLabel, Priority.ALWAYS);
        GridPane.setValignment(ramLabel, VPos.TOP);
        GridPane.setHalignment(ramLabel, HPos.LEFT);
        ramLabel.setStyle("-fx-text-fill: #95bad3; -fx-font-size: 16px;");
        ramLabel.setTranslateY(155);
        ramLabel.setTranslateX(37.5);

        GridPane.setVgrow(ramSelectMenu, Priority.ALWAYS);
        GridPane.setHgrow(ramSelectMenu, Priority.ALWAYS);
        GridPane.setValignment(ramSelectMenu, VPos.TOP);
        GridPane.setHalignment(ramSelectMenu, HPos.LEFT);
        ramSelectMenu.setTranslateY(180);
        ramSelectMenu.setTranslateX(37.5);
        ramSelectMenu.setMinWidth(130);
        ramSelectMenu.setMinHeight(40);
        ramSelectMenu.getStylesheets().add(Main.class.getResource("/css/comboBox.css").toExternalForm());
        ramSelectMenu.setOnMouseEntered(e -> this.layout.setCursor(Cursor.HAND));
        ramSelectMenu.setOnMouseExited(e -> this.layout.setCursor(Cursor.DEFAULT));

        Button backToHome = new Button("Retour");
        GridPane.setVgrow(backToHome, Priority.ALWAYS);
        GridPane.setHgrow(backToHome, Priority.ALWAYS);
        GridPane.setValignment(backToHome, VPos.BOTTOM);
        GridPane.setHalignment(backToHome, HPos.RIGHT);
        backToHome.setTranslateY(-20);
        backToHome.setTranslateX(-37.5);
        backToHome.setMinWidth(150);
        backToHome.setMinHeight(40);
        backToHome.getStylesheets().addAll(Main.class.getResource("/css/button.css").toExternalForm());
        backToHome.setOnMouseEntered(e-> {
            this.layout.setCursor(Cursor.HAND);
        });
        backToHome.setOnMouseExited(e-> {
            this.layout.setCursor(Cursor.DEFAULT);
        });
        backToHome.setOnMouseClicked(e-> {
            versionSaver.set("args", argsField.getText());
            versionSaver.set("ram", String.valueOf(ramSelectMenu.getSelectionModel().getSelectedIndex()));
            this.panelManager.showPanel(new HomePanel());
        });

        versionSettingsPanel.getChildren().addAll(versionSettingsLabel, versionSettingsSeparator, argsLabel, argsField, argsSeparator, backToHome, ramLabel, ramSelectMenu);








        Button defaultBG = new Button("Par défaut");
        Label selectedFileLabel = new Label("");

        Label mainSettingsLabel = new Label("PARAMÈTRES DU LAUNCHER");
        GridPane.setVgrow(mainSettingsLabel, Priority.ALWAYS);
        GridPane.setHgrow(mainSettingsLabel, Priority.ALWAYS);
        GridPane.setValignment(mainSettingsLabel, VPos.TOP);
        mainSettingsLabel.setTranslateY(27);
        mainSettingsLabel.setTranslateX(37.5);
        mainSettingsLabel.setStyle("-fx-text-fill: #bcc6e7; -fx-font-size: 18px;");

        Separator mainSettingsSeparator = new Separator();
        GridPane.setVgrow(mainSettingsSeparator, Priority.ALWAYS);
        GridPane.setHgrow(mainSettingsSeparator, Priority.ALWAYS);
        GridPane.setValignment(mainSettingsSeparator, VPos.TOP);
        GridPane.setHalignment(mainSettingsSeparator, HPos.LEFT);
        mainSettingsSeparator.setTranslateY(60);
        mainSettingsSeparator.setTranslateX(37.5);
        mainSettingsSeparator.setMinWidth(325);
        mainSettingsSeparator.setMaxWidth(325);
        mainSettingsSeparator.setStyle("-fx-background-color: #fff; -fx-opacity: 50%;");


        FileChooser bgChooser = new FileChooser();
        bgChooser.setTitle("Choisir un fond d'écran");
        bgChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif"));

        Label selectBGLabel = new Label("Changer le fond d'écran");
        GridPane.setVgrow(selectBGLabel, Priority.ALWAYS);
        GridPane.setHgrow(selectBGLabel, Priority.ALWAYS);
        GridPane.setValignment(selectBGLabel, VPos.TOP);
        GridPane.setHalignment(selectBGLabel, HPos.LEFT);
        selectBGLabel.setStyle("-fx-text-fill: #95bad3; -fx-font-size: 16px;");
        selectBGLabel.setTranslateY(270);
        selectBGLabel.setTranslateX(37.5);

        Label selectBGRestartLabel = new Label("(Preview ; Effectif après redémarrage du launcher)");
        GridPane.setVgrow(selectBGRestartLabel, Priority.ALWAYS);
        GridPane.setHgrow(selectBGRestartLabel, Priority.ALWAYS);
        GridPane.setValignment(selectBGRestartLabel, VPos.TOP);
        GridPane.setHalignment(selectBGRestartLabel, HPos.LEFT);
        selectBGRestartLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        selectBGRestartLabel.setTranslateY(360);
        selectBGRestartLabel.setTranslateX(37.5);
        selectBGRestartLabel.setVisible(false);

        Button selectBG = new Button("Choisir un fichier");
        GridPane.setVgrow(selectBG, Priority.ALWAYS);
        GridPane.setHgrow(selectBG, Priority.ALWAYS);
        GridPane.setValignment(selectBG, VPos.TOP);
        GridPane.setHalignment(selectBG, HPos.LEFT);
        selectBG.setTranslateY(300);
        selectBG.setTranslateX(37.5);
        selectBG.setMinWidth(150);
        selectBG.setMinHeight(40);
        selectBG.getStylesheets().addAll(Main.class.getResource("/css/button.css").toExternalForm());
        selectBG.setOnMouseEntered(e-> {
            this.layout.setCursor(Cursor.HAND);
        });
        selectBG.setOnMouseExited(e-> {
            this.layout.setCursor(Cursor.DEFAULT);
        });
        selectBG.setOnMouseClicked(e-> {
            File newBG = bgChooser.showOpenDialog(this.panelManager.getStage());
            if (newBG!=null) {
                File savedBG = new File(Main.LP_DIR, newBG.getName());
                try {
                    Main.logger.log("Copie du nouveau fond d'écran personnalisé...");
                    Files.copy(newBG.toPath(), savedBG.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    if (Main.getSaver().get("backgroundName")!=null) {
                        if (new File(Main.LP_DIR, Main.getSaver().get("backgroundName")).exists()) {
                            Main.logger.log("Suppression de l'ancien fond d'écran personnalisé...");
                            this.layout.setStyle(AriLibFX.setResponsiveBackground(Main.class.getResource("/background.png").toExternalForm()));
                            System.gc();
                            new File(Main.LP_DIR, Main.getSaver().get("backgroundName")).delete();
                        }
                     }
                    Main.logger.log("Enregistrement et mise à jour du nouveau fond d'écran...");
                    Main.getSaver().set("backgroundName", newBG.getName());
                    this.layout.setStyle(AriLibFX.setResponsiveBackground(new File(Main.LP_DIR, Main.getSaver().get("backgroundName")).toURI().toURL().toExternalForm()));
                    defaultBG.setVisible(true);
                    selectedFileLabel.setText("Fichier sélectionné : " + Main.getSaver().get("backgroundName"));
                    selectBGRestartLabel.setVisible(true);

                } catch (IOException ioException) {
                    Main.logger.warn("Impossible de copier le fichier du nouveau fond d'écran personnalisé !");
                    selectBGRestartLabel.setVisible(false);
                }

            }
        });

        Button openJavaPath = new Button("Ouvrir le dossier d'installation de Java");
        GridPane.setVgrow(openJavaPath, Priority.ALWAYS);
        GridPane.setHgrow(openJavaPath, Priority.ALWAYS);
        GridPane.setValignment(openJavaPath, VPos.TOP);
        GridPane.setHalignment(openJavaPath, HPos.LEFT);
        openJavaPath.setTranslateY(220);
        openJavaPath.setTranslateX(37.5);
        openJavaPath.setMinWidth(300);
        openJavaPath.setMinHeight(40);
        openJavaPath.getStylesheets().addAll(Main.class.getResource("/css/button.css").toExternalForm());
        openJavaPath.setOnMouseEntered(e-> {
            this.layout.setCursor(Cursor.HAND);
        });
        openJavaPath.setOnMouseExited(e-> {
            this.layout.setCursor(Cursor.DEFAULT);
        });
        openJavaPath.setOnMouseClicked(e-> {
            String path = System.getProperty("java.home");
            path = path + "\\bin";
            Main.logger.log(path);
            try {
                Desktop.getDesktop().open(new File(path));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        });

        GridPane.setVgrow(defaultBG, Priority.ALWAYS);
        GridPane.setHgrow(defaultBG, Priority.ALWAYS);
        GridPane.setValignment(defaultBG, VPos.TOP);
        GridPane.setHalignment(defaultBG, HPos.LEFT);
        defaultBG.setTranslateY(305);
        defaultBG.setTranslateX(200);
        defaultBG.setMinWidth(130);
        defaultBG.setMinHeight(30);
        defaultBG.getStylesheets().addAll(Main.class.getResource("/css/button.css").toExternalForm());
        defaultBG.setVisible(false);
        if (Main.getSaver().get("backgroundName")!=null) {
            if (new File(Main.LP_DIR, Main.getSaver().get("backgroundName")).exists()) {
            defaultBG.setVisible(true);
            }
        }

        defaultBG.setOnMouseEntered(e-> {
            this.layout.setCursor(Cursor.HAND);
        });
        defaultBG.setOnMouseExited(e-> {
            this.layout.setCursor(Cursor.DEFAULT);
        });
        defaultBG.setOnMouseClicked(e-> {
            if (Main.getSaver().get("backgroundName")!=null) {
                if (new File(Main.LP_DIR, Main.getSaver().get("backgroundName")).exists()) {
                    this.layout.setStyle(AriLibFX.setResponsiveBackground(Main.class.getResource("/background.png").toExternalForm()));
                    System.gc();
                    if (!new File(Main.LP_DIR, Main.getSaver().get("backgroundName")).delete()) {
                        Main.logger.warn("Impossible de supprimer l'ancien fond d'écran !");
                    }
                    defaultBG.setVisible(false);
                    selectedFileLabel.setText("");
                }
                Main.getSaver().remove("backgroundName");
                selectBGRestartLabel.setVisible(false);
            }
        });


        GridPane.setVgrow(selectedFileLabel, Priority.ALWAYS);
        GridPane.setHgrow(selectedFileLabel, Priority.ALWAYS);
        GridPane.setValignment(selectedFileLabel, VPos.TOP);
        GridPane.setHalignment(selectedFileLabel, HPos.LEFT);
        selectedFileLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        selectedFileLabel.setTranslateY(345);
        selectedFileLabel.setTranslateX(37.5);
        selectedFileLabel.setMaxWidth(350);
        if (Main.getSaver().get("backgroundName")!=null) {
            if (new File(Main.LP_DIR, Main.getSaver().get("backgroundName")).exists()) {
                selectedFileLabel.setText("Fichier sélectionné : " + Main.getSaver().get("backgroundName"));
            }
        }




        Rectangle autoConnectRectangle = new Rectangle(325,40);
        GridPane.setVgrow(autoConnectRectangle, Priority.ALWAYS);
        GridPane.setHgrow(autoConnectRectangle, Priority.ALWAYS);
        GridPane.setValignment(autoConnectRectangle, VPos.TOP);
        GridPane.setHalignment(autoConnectRectangle, HPos.LEFT);
        autoConnectRectangle.setTranslateX(35);
        autoConnectRectangle.setTranslateY(100);
        autoConnectRectangle.setArcHeight(10);
        autoConnectRectangle.setArcWidth(10);
        autoConnectRectangle.setFill(Color.valueOf("#1e1e1e"));

        CheckBox isAutoConnectOn = new CheckBox("Connexion automatique au démarrage");
        isAutoConnectOn.setAllowIndeterminate(false);
        GridPane.setVgrow(isAutoConnectOn, Priority.ALWAYS);
        GridPane.setHgrow(isAutoConnectOn, Priority.ALWAYS);
        GridPane.setValignment(isAutoConnectOn, VPos.TOP);
        GridPane.setHalignment(isAutoConnectOn, HPos.LEFT);
        isAutoConnectOn.setTranslateX(60);
        isAutoConnectOn.setTranslateY(109.5);
        isAutoConnectOn.setStyle("-fx-background-color: #1e1e1e; -fx-font-size: 15px; -fx-text-fill: #e5e5e5");
        isAutoConnectOn.setOnMouseClicked(e->{
            if (isAutoConnectOn.isSelected()) {
                Main.getSaver().set("autoConnect", "true");
                Main.logger.log("Connexion automatique activée");
            } else {
                Main.getSaver().set("autoConnect", "false");
                Main.logger.log("Connexion automatique désactivée");
            }
        });

        Rectangle loadCrackSkinRectangle = new Rectangle(325,60);
        GridPane.setVgrow(loadCrackSkinRectangle, Priority.ALWAYS);
        GridPane.setHgrow(loadCrackSkinRectangle, Priority.ALWAYS);
        GridPane.setValignment(loadCrackSkinRectangle, VPos.TOP);
        GridPane.setHalignment(loadCrackSkinRectangle, HPos.LEFT);
        loadCrackSkinRectangle.setTranslateX(35);
        loadCrackSkinRectangle.setTranslateY(150);
        loadCrackSkinRectangle.setArcHeight(10);
        loadCrackSkinRectangle.setArcWidth(10);
        loadCrackSkinRectangle.setFill(Color.valueOf("#1e1e1e"));

        CheckBox isLoadCrackSkinOn = new CheckBox("Tenter d'afficher le skin premium \n lié au pseudo en mode crack");
        isLoadCrackSkinOn.setAllowIndeterminate(false);
        GridPane.setVgrow(isLoadCrackSkinOn, Priority.ALWAYS);
        GridPane.setHgrow(isLoadCrackSkinOn, Priority.ALWAYS);
        GridPane.setValignment(isLoadCrackSkinOn, VPos.TOP);
        GridPane.setHalignment(isLoadCrackSkinOn, HPos.LEFT);
        isLoadCrackSkinOn.setTranslateX(60);
        isLoadCrackSkinOn.setTranslateY(loadCrackSkinRectangle.getTranslateY()+7.5);
        isLoadCrackSkinOn.setStyle("-fx-background-color: #1e1e1e; -fx-font-size: 15px; -fx-text-fill: #e5e5e5");
        isLoadCrackSkinOn.setOnMouseClicked(e->{
            if (isLoadCrackSkinOn.isSelected()) {
                Main.getSaver().set("loadCrackSkin", "true");
                Main.logger.log("Chargement des skins en mode crack activé");
            } else {
                Main.getSaver().set("loadCrackSkin", "false");
                Main.logger.log("Chargement des skins en mode crack désactivé");
            }
        });

        if (Main.getSaver().get("autoConnect")!=null) {
            if (Main.getSaver().get("autoConnect").equals("true")) {
                isAutoConnectOn.setSelected(true);
            }
        }

        if (Main.getSaver().get("loadCrackSkin")!=null) {
            if (Main.getSaver().get("loadCrackSkin").equals("true")) {
                isLoadCrackSkinOn.setSelected(true);
            }
        }
        mainSettingsPanel.getChildren().addAll(mainSettingsLabel, mainSettingsSeparator, autoConnectRectangle, isAutoConnectOn,
                loadCrackSkinRectangle, isLoadCrackSkinOn, selectBGLabel, selectBGRestartLabel, selectBG, defaultBG, selectedFileLabel, openJavaPath);
    }

    private void setFieldEnabled(boolean enabled, TextField field, Label label) {
        if (enabled) {
            field.setStyle("-fx-background-color: #1e1e1e; -fx-font-size: 16px; -fx-text-fill: #e5e5e5");
            label.setStyle("-fx-text-fill: #95bad3; -fx-font-size: 16px;");
        } else {
            field.setStyle("-fx-background-color: #383838; -fx-font-size: 16px; -fx-text-fill: #e5e5e5");
            label.setStyle("-fx-text-fill: #383838; -fx-font-size: 16px;");
        }
        field.setEditable(enabled);
    }

}
