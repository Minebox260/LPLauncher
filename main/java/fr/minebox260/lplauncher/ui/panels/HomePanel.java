package fr.minebox260.lplauncher.ui.panels;

import ch.jamiete.mcping.MinecraftPing;
import ch.jamiete.mcping.MinecraftPingOptions;
import ch.jamiete.mcping.MinecraftPingReply;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import fr.arinonia.arilibfx.ui.component.AProgressBar;
import fr.litarvan.openauth.AuthenticationException;
import fr.minebox260.lplauncher.Main;
import fr.minebox260.lplauncher.SaverUtil;
import fr.minebox260.lplauncher.ui.PanelManager;
import fr.minebox260.lplauncher.ui.panel.Panel;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.*;
import fr.theshark34.openlauncherlib.util.ProcessLogManager;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.integrated.FileDeleter;
import fr.theshark34.supdate.exception.BadServerResponseException;
import fr.theshark34.supdate.exception.BadServerVersionException;
import fr.theshark34.supdate.exception.ServerDisabledException;
import fr.theshark34.supdate.exception.ServerMissingSomethingException;
import fr.theshark34.swinger.Swinger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class HomePanel extends Panel {
    private final GridPane centerPane = new GridPane();
    private static final Label serverTitle = new Label(Main.launcherName);
    private static final Label versionsLabel = new Label("Versions : " + Main.LPVersionsListText);
    private static final ComboBox<String> versionSelectMenu = new ComboBox<>(FXCollections.observableArrayList(Main.getVersionList("party")));

    private final AtomicBoolean survivalOnline = new AtomicBoolean(false);
    private int survivalPlayers = 0;

    private static final Label survivalStatusLabel = new Label("Statut : ");
    private static final Label survivalOnlineLabel = new Label("Hors ligne");
    private static final Label survivalPlayersLabel = new Label("");
    private static final Rectangle survivalSeparator = new Rectangle(2, 64);
    private static final Rectangle survivalRectangle = new Rectangle(500, 80);
    private static final Label survivalLabel = new Label("Licorne Party Groundia  | Version : 1.16.4");
    private static final Label survivalIPLabel = new Label("IP : lpgroundia.news-craft.fr");
    private static final Image survivalImage = new Image(Main.class.getResource("/faviconServers/lpg.png").toExternalForm());

    private final AtomicBoolean moddedOnline = new AtomicBoolean(false);
    private int moddedPlayers = 0;

    private static final Label moddedStatusLabel = new Label("Statut : ");
    private static final Label moddedOnlineLabel = new Label("Hors ligne");
    private static final Label moddedPlayersLabel = new Label("");
    private static final Rectangle moddedSeparator = new Rectangle(2, 64);
    private static final Rectangle moddedRectangle = new Rectangle(500, 80);
    private static final Label moddedLabel = new Label("LM Fantasia  | Version : LM5 1.12.2");
    private static final Label moddedIPLabel = new Label("IP : fantasia.licparty.fr");
    private static final Image moddedImage = new Image(Main.class.getResource("/faviconServers/lmf.png").toExternalForm());

    public static AtomicBoolean disableLogos = new AtomicBoolean(false);
    public static AtomicBoolean isLaunched = new AtomicBoolean(false);
    public Button launchButton = new Button("Lancer le jeu");
    public AProgressBar downloadBar;

    public Button disconnectButton = new Button("Déconnexion");

    private final Label downloadLabel = new Label("En attente");
    ImageView survivalImageView = new ImageView(survivalImage);
    ImageView moddedImageView = new ImageView(moddedImage);
    public void setDownloadLabel(String text) {
        this.downloadLabel.setText(text);
    }

    public static AtomicBoolean isUpdating = new AtomicBoolean(true);

    public Thread barUpdateThread = new Thread(() -> {
        BarAPI.setNumberOfFileToDownload(0);
        BarAPI.setNumberOfTotalBytesToDownload(0);
        BarAPI.setNumberOfDownloadedFiles(0);
        Main.logger.log("Lancement du thread de mise à jour de l'updateBar");
        while (isUpdating.get()) {
            try {
                if (BarAPI.getNumberOfFileToDownload() == 0) {
                    Platform.runLater(() -> downloadLabel.setText("Vérification des fichiers en cours..."));
                } else {
                    AtomicLong val = new AtomicLong(BarAPI.getNumberOfTotalDownloadedBytes() / 1000);
                    AtomicLong max = new AtomicLong(BarAPI.getNumberOfTotalBytesToDownload() / 1000);
                    float floatVal = (float) val.get();
                    float floatMax = (float) max.get();
                    Platform.runLater(() -> downloadBar.setProgress(floatVal, floatMax));
                    Platform.runLater(() -> downloadLabel.setText("Téléchargement des fichiers... " +
                            BarAPI.getNumberOfDownloadedFiles() + "/" + BarAPI.getNumberOfFileToDownload() + " " +
                            Swinger.percentage((int) val.get(), (int) max.get()) + "%"));
                }
            } catch (Exception e) {
                Platform.runLater((() -> downloadLabel.setText("ERREUR")));
                Main.logger.warn("Une erreur est survenue durant la mise à jour de l'updateBar :");
                e.printStackTrace();
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (Main.getErrorPanelException().get()==null) {
            Platform.runLater(() -> {
                downloadLabel.setTranslateX(150);
                downloadLabel.setText("Lancement en cours...");
            });

            while (!isLaunched.get()) {
            }
            System.exit(0);
        } else {
            this.interruptBarUpdateThread();
        }

    });

    public Thread checkErrorThread = new Thread(() -> {
        while (isUpdating.get()) {
            if (Main.getErrorPanelException().get()!=null) {
                Platform.runLater(()->this.panelManager.showPanel((new ErrorPanel())));
                HomePanel.disableLogos.set(false);
                HomePanel.isUpdating.set(false);
                HomePanel.isLaunched.set(false);
                versionSelectMenu.setDisable(false);
                this.interruptUpdateThread();
                this.interruptCheckErrorThread();
            }
        }
    });


    public void interruptCheckErrorThread() {
        this.checkErrorThread.interrupt();
    }
    public void interruptBarUpdateThread() {
        this.barUpdateThread.interrupt();
    }

    public void init(PanelManager panelManager) {
        Main.logger.log("Initialisation de l'interface d'accueil");
        super.init(panelManager);

        ColumnConstraints menuPainConstraints = new ColumnConstraints();
        menuPainConstraints.setHalignment(HPos.LEFT);
        menuPainConstraints.setMinWidth(300);
        menuPainConstraints.setMaxWidth(300);
        this.layout.getColumnConstraints().addAll(menuPainConstraints, new ColumnConstraints());
        GridPane leftBarPane = new GridPane();
        GridPane.setVgrow(leftBarPane, Priority.ALWAYS);
        GridPane.setHgrow(leftBarPane, Priority.ALWAYS);
        GridPane.setVgrow(centerPane, Priority.ALWAYS);
        GridPane.setHgrow(centerPane, Priority.ALWAYS);

        Separator rightSeparator = new Separator();
        GridPane.setHgrow(rightSeparator, Priority.ALWAYS);
        GridPane.setVgrow(rightSeparator, Priority.ALWAYS);
        GridPane.setHalignment(rightSeparator, HPos.RIGHT);
        rightSeparator.setOrientation(Orientation.VERTICAL);
        rightSeparator.setTranslateY(1);
        rightSeparator.setTranslateX(4);
        rightSeparator.setMinWidth(2);
        rightSeparator.setMaxWidth(2);
        rightSeparator.setOpacity(0.30D);

        GridPane bottomGridPane = new GridPane();
        GridPane.setVgrow(bottomGridPane, Priority.ALWAYS);
        GridPane.setHgrow(bottomGridPane, Priority.ALWAYS);
        GridPane.setHalignment(bottomGridPane, HPos.LEFT);
        GridPane.setValignment(bottomGridPane, VPos.TOP);
        bottomGridPane.setTranslateY(30);
        bottomGridPane.setMinHeight(40);
        bottomGridPane.setMaxHeight(40);
        bottomGridPane.setMinWidth(300);
        bottomGridPane.setMaxWidth(300);

        Main.logger.log("Affichage de la barre latérale");
        showLeftBar(bottomGridPane);
        leftBarPane.getChildren().addAll(rightSeparator, bottomGridPane);
        this.layout.add(leftBarPane, 0, 0);
        this.layout.add(this.centerPane, 1, 0);

        Main.logger.log("Affichage du panneau central");
        this.centerPane.setTranslateX(30);
        addCenterPanel();

    }

    private void showLeftBar(GridPane pane) {
        Separator blueLeftLPSeparator = new Separator();
        GridPane.setVgrow(blueLeftLPSeparator, Priority.ALWAYS);
        GridPane.setHgrow(blueLeftLPSeparator, Priority.ALWAYS);
        blueLeftLPSeparator.setMinHeight(80);
        blueLeftLPSeparator.setMaxHeight(80);
        blueLeftLPSeparator.setMinWidth(3);
        blueLeftLPSeparator.setMaxWidth(3);
        blueLeftLPSeparator.setOrientation(Orientation.VERTICAL);
        blueLeftLPSeparator.setStyle("-fx-background-color: rgb(5,179,242); -fx-border-width:3 3 3 0; -fx-border-color: rgb(5,179,242);");

        Separator blueLeftLPMSeparator = new Separator();
        GridPane.setVgrow(blueLeftLPMSeparator, Priority.ALWAYS);
        GridPane.setHgrow(blueLeftLPMSeparator, Priority.ALWAYS);
        blueLeftLPMSeparator.setMinHeight(80);
        blueLeftLPMSeparator.setMaxHeight(80);
        blueLeftLPMSeparator.setMinWidth(3);
        blueLeftLPMSeparator.setMaxWidth(3);
        blueLeftLPMSeparator.setOrientation(Orientation.VERTICAL);
        blueLeftLPMSeparator.setTranslateY(80);
        blueLeftLPMSeparator.setStyle("-fx-background-color: rgb(5,179,242); -fx-border-width:3 3 3 0; -fx-border-color: rgb(5,179,242);");

        Image logoImageLParty = new Image(Main.class.getResource("/logos/LParty.png").toExternalForm());
        ImageView imageViewLParty = new ImageView(logoImageLParty);
        GridPane.setVgrow(imageViewLParty, Priority.ALWAYS);
        GridPane.setHgrow(imageViewLParty, Priority.ALWAYS);
        GridPane.setValignment(imageViewLParty, VPos.CENTER);

        imageViewLParty.setTranslateX(17);
        imageViewLParty.setFitHeight(56);
        imageViewLParty.setFitWidth(173);

        Image logoImageLPModded = new Image(Main.class.getResource("/logos/LPModded.png").toExternalForm());
        ImageView imageViewLPModded = new ImageView(logoImageLPModded);
        GridPane.setVgrow(imageViewLPModded, Priority.ALWAYS);
        GridPane.setHgrow(imageViewLPModded, Priority.ALWAYS);
        GridPane.setValignment(imageViewLPModded, VPos.CENTER);
        imageViewLPModded.setTranslateX(17);
        imageViewLPModded.setTranslateY(80);
        imageViewLPModded.setFitHeight(56);
        imageViewLPModded.setFitWidth(173);

        imageViewLParty.setOnMouseEntered(e -> {
            if (Main.getSaver().get("selectedServer").equals("party")) {
                this.layout.setCursor(Cursor.DEFAULT);
            } else {
                imageViewLParty.setOpacity(1.0D);
                blueLeftLPSeparator.setOpacity(1.0D);
                this.layout.setCursor(Cursor.HAND);
            }
        });
        imageViewLParty.setOnMouseExited(e -> {

            if (!Main.getSaver().get("selectedServer").equals("party")) {
                imageViewLParty.setOpacity(0.5D);
                blueLeftLPSeparator.setOpacity(0.5D);
            }
            this.layout.setCursor(Cursor.DEFAULT);
        });
        imageViewLParty.setOnMouseClicked(e -> {
            if (Main.getSaver().get("selectedServer").equals("modded") && !disableLogos.get()) {
                Main.logger.log("Passage en mode Licorne Party");
                Main.getSaver().set("selectedServer","party");
                serverTitle.setText("Licorne Party");
                versionsLabel.setText("Versions : " + Main.LPVersionsListText);
                versionSelectMenu.setItems(FXCollections.observableArrayList(Main.getVersionList("party")));
                if (Main.getSaver().get("partyVersion")!=null) {
                    versionSelectMenu.getSelectionModel().select(Main.getSavedVersion()[0] + " (" + Main.getSavedVersion()[1] + ")");
                } else {
                    versionSelectMenu.getSelectionModel().selectLast();
                }
                if (!new File(Main.LP_VER_DIR, Main.LPVersionsList[versionSelectMenu.getSelectionModel().getSelectedIndex()][0]).exists()) {
                    launchButton.setText("Installer");
                } else {
                    launchButton.setText("Lancer le jeu");
                }
                imageViewLParty.setOpacity(1.0D);
                blueLeftLPSeparator.setOpacity(1.0D);
                imageViewLPModded.setOpacity(0.5D);
                blueLeftLPMSeparator.setOpacity(0.5D);
                survivalOnlineLabel.setVisible(true);
                survivalPlayersLabel.setVisible(true);
                survivalStatusLabel.setVisible(true);
                survivalImageView.setVisible(true);
                survivalIPLabel.setVisible(true);
                survivalLabel.setVisible(true);
                survivalRectangle.setVisible(true);
                survivalSeparator.setVisible(true);
                moddedOnlineLabel.setVisible(false);
                moddedPlayersLabel.setVisible(false);
                moddedStatusLabel.setVisible(false);
                moddedImageView.setVisible(false);
                moddedIPLabel.setVisible(false);
                moddedLabel.setVisible(false);
                moddedRectangle.setVisible(false);
                moddedSeparator.setVisible(false);
                updateSurvivalInfos();
                if (survivalOnline.get()) {
                    survivalOnlineLabel.setText("En ligne");
                    survivalOnlineLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: lightgreen;");
                    survivalPlayersLabel.setText(survivalPlayers + "  Joueur(s) en ligne");
                } else {
                    survivalOnlineLabel.setText("Hors Ligne");
                    survivalOnlineLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: darkred;");
                    survivalPlayersLabel.setText("");
                }

            }
        });

        imageViewLPModded.setOnMouseEntered(e -> {
            if (Main.getSaver().get("selectedServer").equals("modded")) {
                this.layout.setCursor(Cursor.DEFAULT);
            } else {
                imageViewLPModded.setOpacity(1.0D);
                blueLeftLPMSeparator.setOpacity(1.0D);
                this.layout.setCursor(Cursor.HAND);
            }
        });
        imageViewLPModded.setOnMouseExited(e -> {
            if (!Main.getSaver().get("selectedServer").equals("modded")) {
                imageViewLPModded.setOpacity(0.5D);
                blueLeftLPMSeparator.setOpacity(0.5D);
            }
            this.layout.setCursor(Cursor.DEFAULT);
        });
        imageViewLPModded.setOnMouseClicked(e -> {
            if (Main.getSaver().get("selectedServer").equals("party") && !disableLogos.get()) {
                Main.logger.log("Passage en mode Licorne Modded");
                Main.getSaver().set("selectedServer", "modded");
                serverTitle.setText("Licorne Modded");
                versionsLabel.setText("Versions : " + Main.LPMVersionsListText);
                versionSelectMenu.setItems(FXCollections.observableArrayList(Main.getVersionList("modded")));
                if (Main.getSaver().get("moddedVersion")!=null) {
                    versionSelectMenu.getSelectionModel().select(Main.getSavedVersion()[0] + " (" + Main.getSavedVersion()[1] + ")");
                } else {
                    versionSelectMenu.getSelectionModel().selectLast();
                }
                if (!Main.LPM_DIR.exists()) {
                    launchButton.setText("Installer");
                } else {
                    launchButton.setText("Lancer le jeu");
                }
                imageViewLPModded.setOpacity(1.0D);
                blueLeftLPMSeparator.setOpacity(1.0D);
                imageViewLParty.setOpacity(0.5D);
                blueLeftLPSeparator.setOpacity(0.5D);
                survivalOnlineLabel.setVisible(false);
                survivalPlayersLabel.setVisible(false);
                survivalStatusLabel.setVisible(false);
                survivalImageView.setVisible(false);
                survivalIPLabel.setVisible(false);
                survivalLabel.setVisible(false);
                survivalRectangle.setVisible(false);
                survivalSeparator.setVisible(false);
                moddedOnlineLabel.setVisible(true);
                moddedPlayersLabel.setVisible(true);
                moddedStatusLabel.setVisible(true);
                moddedImageView.setVisible(true);
                moddedIPLabel.setVisible(true);
                moddedLabel.setVisible(true);
                moddedRectangle.setVisible(true);
                moddedSeparator.setVisible(true);
                updateModdedInfos();
                if (moddedOnline.get()) {
                    moddedOnlineLabel.setText("En ligne");
                    moddedOnlineLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: lightgreen;");
                    moddedPlayersLabel.setText(moddedPlayers + "  Joueur(s) en ligne");
                } else {
                    moddedOnlineLabel.setText("Hors Ligne");
                    moddedOnlineLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: darkred;");
                    moddedPlayersLabel.setText("");
                }
            }
        });

        Main.logger.log("Vérification du serveur précédemment choisi");
        if (Main.getSaver().get("selectedServer").equals("party")) {
            Main.logger.log("Serveur sélectionné : Licorne Party");
            imageViewLPModded.setOpacity(0.5D);
            blueLeftLPMSeparator.setOpacity(0.5D);
            serverTitle.setText("Licorne Party");
            versionsLabel.setText("Versions : " + Main.LPVersionsListText);
        } else {
            Main.logger.log("Serveur sélectionné : Licorne Modded");
            imageViewLParty.setOpacity(0.5D);
            blueLeftLPSeparator.setOpacity(0.5D);
            serverTitle.setText("Licorne Modded");
            versionsLabel.setText("Versions : " + Main.LPMVersionsListText);
        }

        Label welcomeLabel = new Label("BIENVENUE");
        GridPane.setVgrow(welcomeLabel, Priority.ALWAYS);
        GridPane.setHgrow(welcomeLabel, Priority.ALWAYS);
        GridPane.setValignment(welcomeLabel, VPos.TOP);
        welcomeLabel.setTranslateY(195);
        welcomeLabel.setTranslateX(15);
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 26px; -fx-font-weight: bold;");

        Separator welcomeSeparator = new Separator();
        GridPane.setVgrow(welcomeSeparator, Priority.ALWAYS);
        GridPane.setHgrow(welcomeSeparator, Priority.ALWAYS);
        GridPane.setHalignment(welcomeSeparator, HPos.LEFT);
        GridPane.setValignment(welcomeSeparator, VPos.TOP);

        welcomeSeparator.setMinHeight(3);
        welcomeSeparator.setMaxHeight(3);
        welcomeSeparator.setMinWidth(175);
        welcomeSeparator.setMaxWidth(175);
        welcomeSeparator.setTranslateY(235);
        welcomeSeparator.setTranslateX(15);
        welcomeSeparator.setStyle("-fx-background-color: #fff; -fx-opacity: 50%;");

        Separator welcomeBarSeparator = new Separator();
        GridPane.setVgrow(welcomeBarSeparator, Priority.ALWAYS);
        GridPane.setHgrow(welcomeBarSeparator, Priority.ALWAYS);
        GridPane.setHalignment(welcomeBarSeparator, HPos.LEFT);
        GridPane.setValignment(welcomeBarSeparator, VPos.TOP);
        welcomeBarSeparator.setMinHeight(40);
        welcomeBarSeparator.setMaxHeight(40);
        welcomeBarSeparator.setMinWidth(300);
        welcomeBarSeparator.setMaxWidth(300);
        welcomeBarSeparator.setTranslateY(430);
        welcomeBarSeparator.setStyle("-fx-background-color: #fff; -fx-opacity: 25%;");
        Image playerHead = new Image(Main.class.getResource("/defaultHead.png").toExternalForm());
        if (Main.getSaver().get("connectWithMojang").equals("true")) {
            Main.logger.log("Récupération de la tête du joueur premium...");
            playerHead = new Image("https://crafatar.com/avatars/" + Main.getSaver().get("uuid") +  "?overlay=true");
        } else if (Main.getSaver().get("loadCrackSkin").equals("true")) {
            String url = "https://api.mojang.com/users/profiles/minecraft/"+Main.getSaver().get("username");
            Main.logger.log("Récupération de la tête du joueur crack...");
            try {
                String UUIDJson = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
                Main.logger.log(UUIDJson);
                if(UUIDJson.isEmpty())throw new Exception("Invalid Query");
                JSONObject UUIDObject = (JSONObject)JSONValue.parseWithException(UUIDJson);
                String uuid = UUIDObject.get("id").toString();
                playerHead = new Image("https://crafatar.com/avatars/" + uuid +  "?overlay=true");
            } catch (Exception e) {
                Main.logger.warn("Impossible de récupérer l'UUID correspondant au pseudo crack");
                e.printStackTrace();
            }
        }
        ImageView playerHeadView = new ImageView(playerHead);
        GridPane.setVgrow(playerHeadView, Priority.ALWAYS);
        GridPane.setHgrow(playerHeadView, Priority.ALWAYS);
        GridPane.setHalignment(playerHeadView, HPos.CENTER);
        playerHeadView.setTranslateY(270);
        playerHeadView.setFitHeight(128);
        playerHeadView.setFitWidth(128);

        Label loggedIn = new Label("Connecté(e) en tant que");
        GridPane.setVgrow(loggedIn, Priority.ALWAYS);
        GridPane.setHgrow(loggedIn, Priority.ALWAYS);
        GridPane.setHalignment(loggedIn, HPos.CENTER);
        loggedIn.setTranslateY(360);
        loggedIn.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        Label playerName = new Label(Main.getSaver().get("username"));
        Label loggedType = new Label();
        if (Main.getSaver().get("connectWithMojang").equals("true")) {
            loggedType.setText("PREMIUM");
        } else {
            loggedType.setText("CRACK");
        }
        GridPane.setVgrow(playerName, Priority.ALWAYS);
        GridPane.setHgrow(playerName, Priority.ALWAYS);
        GridPane.setHalignment(playerName, HPos.CENTER);
        playerName.setTranslateY(390);
        playerName.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        GridPane.setVgrow(loggedType, Priority.ALWAYS);
        GridPane.setHgrow(loggedType, Priority.ALWAYS);
        GridPane.setHalignment(loggedType, HPos.CENTER);
        loggedType.setTranslateY(430);
        loggedType.setStyle("-fx-text-fill: white; -fx-font-size: 22px;");

        Separator loggedTypeSeparator = new Separator();
        GridPane.setVgrow(loggedTypeSeparator, Priority.ALWAYS);
        GridPane.setHgrow(loggedTypeSeparator, Priority.ALWAYS);
        GridPane.setHalignment(loggedTypeSeparator, HPos.LEFT);
        loggedTypeSeparator.setMinHeight(40);
        loggedTypeSeparator.setMaxHeight(40);
        loggedTypeSeparator.setMinWidth(300);
        loggedTypeSeparator.setMaxWidth(300);
        loggedTypeSeparator.setTranslateY(430);
        loggedTypeSeparator.setStyle("-fx-background-color: #fff; -fx-opacity: 25%;");


        GridPane.setVgrow(disconnectButton, Priority.ALWAYS);
        GridPane.setHgrow(disconnectButton, Priority.ALWAYS);
        GridPane.setValignment(disconnectButton, VPos.TOP);
        GridPane.setHalignment(disconnectButton, HPos.CENTER);
        disconnectButton.setTranslateY(550);
        disconnectButton.setMinWidth(175);
        disconnectButton.setMinHeight(50);
        disconnectButton.getStylesheets().addAll(Main.class.getResource("/css/button.css").toExternalForm());
        disconnectButton.setOnMouseEntered(e -> this.layout.setCursor(Cursor.HAND));
        disconnectButton.setOnMouseExited(e -> this.layout.setCursor(Cursor.DEFAULT));
        disconnectButton.setOnMouseClicked(e -> {
            if (Main.getSaver().get("connectWithMojang").equals("true")) {
                Main.logger.log("Déconnexion du mode premium...");
                try {
                    LoginPanel.authenticator.invalidate(Main.getSaver().get("accessToken"), Main.getSaver().get("clientToken"));
                    Main.logger.log("Token de connexion invalidé");
                } catch (AuthenticationException authenticationException) {
                    authenticationException.printStackTrace();
                    Main.logger.warn("Impossible d'invalider le token de connexion !");
                }
                Main.getSaver().remove("accessToken");
                Main.logger.log("Token de connexion supprimé du disque local");

            } else {
                Main.logger.log("Déconnexion du mode crack...");
                Main.getSaver().set("doNotReconnectCrack", "true");

            }
            Main.logger.log("Déconnecté");
            this.panelManager.showPanel(new LoginPanel());
        });
        pane.getChildren().addAll(blueLeftLPSeparator, imageViewLParty, blueLeftLPMSeparator, imageViewLPModded, welcomeLabel, welcomeSeparator, playerHeadView,
                loggedIn, playerName, loggedType, loggedTypeSeparator, disconnectButton);

    }

    private void addCenterPanel() {

        GridPane.setVgrow(serverTitle, Priority.ALWAYS);
        GridPane.setHgrow(serverTitle, Priority.ALWAYS);
        GridPane.setValignment(serverTitle, VPos.TOP);
        serverTitle.setStyle("-fx-font-size: 28px; -fx-text-fill: #fff; -fx-font-weight: bold;");
        serverTitle.setTranslateY(20);

        GridPane.setVgrow(versionsLabel, Priority.ALWAYS);
        GridPane.setHgrow(versionsLabel, Priority.ALWAYS);
        GridPane.setValignment(versionsLabel, VPos.TOP);
        versionsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #fff;");
        versionsLabel.setTranslateY(55);

        Separator versionSeparator = new Separator();
        GridPane.setVgrow(versionSeparator, Priority.ALWAYS);
        GridPane.setHgrow(versionSeparator, Priority.ALWAYS);
        GridPane.setHalignment(versionSeparator, HPos.LEFT);
        GridPane.setValignment(versionSeparator, VPos.TOP);

        versionSeparator.setMinHeight(3);
        versionSeparator.setMaxHeight(3);
        versionSeparator.setMinWidth(350);
        versionSeparator.setMaxWidth(350);
        versionSeparator.setTranslateY(90);
        versionSeparator.setStyle("-fx-background-color: #fff;");

        versionSelectMenu.getSelectionModel().selectLast();
        GridPane.setVgrow(versionSelectMenu, Priority.ALWAYS);
        GridPane.setHgrow(versionSelectMenu, Priority.ALWAYS);
        GridPane.setValignment(versionSelectMenu, VPos.TOP);
        GridPane.setHalignment(versionSelectMenu, HPos.LEFT);
        versionSelectMenu.setTranslateY(120);
        versionSelectMenu.setMinWidth(130);
        versionSelectMenu.setMinHeight(40);
        versionSelectMenu.getStylesheets().add(Main.class.getResource("/css/comboBox.css").toExternalForm());
        versionSelectMenu.setOnMouseEntered(e -> this.layout.setCursor(Cursor.HAND));
        versionSelectMenu.setOnMouseExited(e -> this.layout.setCursor(Cursor.DEFAULT));
        versionSelectMenu.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {
            if ((int)newValue>=0) {
                if (Main.getSaver().get("selectedServer").equals("party")) {
                    Main.getSaver().set("partyVersion", Main.LPVersionsList[(int) newValue][0]);
                    Main.logger.log("Version (Party) sélectionnée : " + Main.getSaver().get("partyVersion"));
                    if (!new File(Main.LP_VER_DIR, Main.LPVersionsList[versionSelectMenu.getSelectionModel().getSelectedIndex()][0]).exists()) {
                        launchButton.setText("Installer");
                    } else {
                        launchButton.setText("Lancer le jeu");
                    }
                } else {
                    Main.getSaver().set("moddedVersion", Main.LPMVersionsList[(int) newValue][0]);
                    Main.logger.log("Version (Modded) sélectionnée : " + Main.getSaver().get("moddedVersion"));
                    if (!new GameInfos("LP-" + Main.LPMVersionsList[(int) newValue][0], new GameVersion(Main.LPMVersionsList[(int) newValue][1], GameType.V1_8_HIGHER), null).getGameDir().exists()) {
                        launchButton.setText("Installer");
                    } else {
                        launchButton.setText("Lancer le jeu");
                    }
                }
            }
        });


        GridPane.setVgrow(survivalRectangle, Priority.ALWAYS);
        GridPane.setHgrow(survivalRectangle, Priority.ALWAYS);
        GridPane.setHalignment(survivalRectangle, HPos.LEFT);
        GridPane.setValignment(survivalRectangle, VPos.TOP);
        survivalRectangle.setTranslateY(180);
        survivalRectangle.setStyle("-fx-opacity: 40%; -fx-background-insets:0;");
        survivalRectangle.setArcHeight(10);
        survivalRectangle.setArcWidth(10);
        survivalRectangle.setFill(Color.valueOf("#007dbe"));

        GridPane.setVgrow(moddedRectangle, Priority.ALWAYS);
        GridPane.setHgrow(moddedRectangle, Priority.ALWAYS);
        GridPane.setHalignment(moddedRectangle, HPos.LEFT);
        GridPane.setValignment(moddedRectangle, VPos.TOP);
        moddedRectangle.setTranslateY(180);
        moddedRectangle.setStyle("-fx-opacity: 40%; -fx-background-insets:0;");
        moddedRectangle.setArcHeight(10);
        moddedRectangle.setArcWidth(10);
        moddedRectangle.setFill(Color.valueOf("#007dbe"));

        if (Main.getSaver().get("selectedServer").equals("party")) updateSurvivalInfos();
        else updateModdedInfos();

        GridPane.setVgrow(survivalLabel, Priority.ALWAYS);
        GridPane.setHgrow(survivalLabel, Priority.ALWAYS);
        GridPane.setValignment(survivalLabel, VPos.TOP);
        survivalLabel.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: white;");
        survivalLabel.setTranslateY(185);
        survivalLabel.setTranslateX(100);

        GridPane.setVgrow(moddedLabel, Priority.ALWAYS);
        GridPane.setHgrow(moddedLabel, Priority.ALWAYS);
        GridPane.setValignment(moddedLabel, VPos.TOP);
        moddedLabel.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: white;");
        moddedLabel.setTranslateY(185);
        moddedLabel.setTranslateX(100);


        GridPane.setVgrow(survivalIPLabel, Priority.ALWAYS);
        GridPane.setHgrow(survivalIPLabel, Priority.ALWAYS);
        GridPane.setValignment(survivalIPLabel, VPos.TOP);
        survivalIPLabel.setStyle("-fx-font-size: 17px; -fx-text-fill: white;");
        survivalIPLabel.setTranslateY(207.5);
        survivalIPLabel.setTranslateX(100);

        GridPane.setVgrow(moddedIPLabel, Priority.ALWAYS);
        GridPane.setHgrow(moddedIPLabel, Priority.ALWAYS);
        GridPane.setValignment(moddedIPLabel, VPos.TOP);
        moddedIPLabel.setStyle("-fx-font-size: 17px; -fx-text-fill: white;");
        moddedIPLabel.setTranslateY(207.5);
        moddedIPLabel.setTranslateX(100);

        GridPane.setVgrow(survivalStatusLabel, Priority.ALWAYS);
        GridPane.setHgrow(survivalStatusLabel, Priority.ALWAYS);
        GridPane.setValignment(survivalStatusLabel, VPos.TOP);
        survivalStatusLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: white;");
        survivalStatusLabel.setTranslateY(230);
        survivalStatusLabel.setTranslateX(100);

        GridPane.setVgrow(moddedStatusLabel, Priority.ALWAYS);
        GridPane.setHgrow(moddedStatusLabel, Priority.ALWAYS);
        GridPane.setValignment(moddedStatusLabel, VPos.TOP);
        moddedStatusLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: white;");
        moddedStatusLabel.setTranslateY(230);
        moddedStatusLabel.setTranslateX(100);


        GridPane.setVgrow(survivalOnlineLabel, Priority.ALWAYS);
        GridPane.setHgrow(survivalOnlineLabel, Priority.ALWAYS);
        GridPane.setValignment(survivalOnlineLabel, VPos.TOP);
        survivalOnlineLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: darkred;");
        survivalOnlineLabel.setTranslateY(230);
        survivalOnlineLabel.setTranslateX(160);

        GridPane.setVgrow(moddedOnlineLabel, Priority.ALWAYS);
        GridPane.setHgrow(moddedOnlineLabel, Priority.ALWAYS);
        GridPane.setValignment(moddedOnlineLabel, VPos.TOP);
        moddedOnlineLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: darkred;");
        moddedOnlineLabel.setTranslateY(230);
        moddedOnlineLabel.setTranslateX(160);

        GridPane.setVgrow(survivalPlayersLabel, Priority.ALWAYS);
        GridPane.setHgrow(survivalPlayersLabel, Priority.ALWAYS);
        GridPane.setValignment(survivalPlayersLabel, VPos.TOP);
        survivalPlayersLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: white;");
        survivalPlayersLabel.setTranslateY(230);
        survivalPlayersLabel.setTranslateX(340);

        GridPane.setVgrow(moddedPlayersLabel, Priority.ALWAYS);
        GridPane.setHgrow(moddedPlayersLabel, Priority.ALWAYS);
        GridPane.setValignment(moddedPlayersLabel, VPos.TOP);
        moddedPlayersLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: white;");
        moddedPlayersLabel.setTranslateY(230);
        moddedPlayersLabel.setTranslateX(340);


        if (survivalOnline.get()) {
            survivalOnlineLabel.setText("En ligne");
            survivalOnlineLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: lightgreen;");
            survivalPlayersLabel.setText(survivalPlayers + "  Joueur(s) en ligne");
        }
        moddedOnlineLabel.setVisible(false);
        moddedPlayersLabel.setVisible(false);
        moddedStatusLabel.setVisible(false);
        moddedImageView.setVisible(false);
        moddedIPLabel.setVisible(false);
        moddedLabel.setVisible(false);
        moddedRectangle.setVisible(false);
        moddedSeparator.setVisible(false);
        if (Main.getSaver().get("selectedServer")!=null) {
            if (Main.getSaver().get("selectedServer").equals("modded")) {
                if (moddedOnline.get()) {
                    moddedOnlineLabel.setText("En ligne");
                    moddedOnlineLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: lightgreen;");
                    moddedPlayersLabel.setText(survivalPlayers + "  Joueur(s) en ligne");
                    survivalOnlineLabel.setVisible(false);
                    survivalPlayersLabel.setVisible(false);
                    survivalStatusLabel.setVisible(false);
                    survivalImageView.setVisible(false);
                    survivalIPLabel.setVisible(false);
                    survivalLabel.setVisible(false);
                    survivalRectangle.setVisible(false);
                    survivalSeparator.setVisible(false);
                    moddedOnlineLabel.setVisible(true);
                    moddedPlayersLabel.setVisible(true);
                    moddedStatusLabel.setVisible(true);
                    moddedImageView.setVisible(true);
                    moddedIPLabel.setVisible(true);
                    moddedLabel.setVisible(true);
                    moddedRectangle.setVisible(true);
                    moddedSeparator.setVisible(true);
                }
            }
        }


        GridPane.setVgrow(survivalLabel, Priority.ALWAYS);
        GridPane.setHgrow(survivalLabel, Priority.ALWAYS);
        GridPane.setValignment(survivalLabel, VPos.TOP);
        survivalLabel.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: white;");
        survivalLabel.setTranslateY(185);
        survivalLabel.setTranslateX(100);

        GridPane.setVgrow(moddedLabel, Priority.ALWAYS);
        GridPane.setHgrow(moddedLabel, Priority.ALWAYS);
        GridPane.setValignment(moddedLabel, VPos.TOP);
        moddedLabel.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: white;");
        moddedLabel.setTranslateY(185);
        moddedLabel.setTranslateX(100);


        GridPane.setVgrow(survivalImageView, Priority.ALWAYS);
        GridPane.setHgrow(survivalImageView, Priority.ALWAYS);
        GridPane.setValignment(survivalImageView, VPos.TOP);
        GridPane.setHalignment(survivalImageView, HPos.LEFT);
        survivalImageView.setTranslateX(17);
        survivalImageView.setTranslateY(185);
        survivalImageView.setFitHeight(64);
        survivalImageView.setFitWidth(64);

        GridPane.setVgrow(moddedImageView, Priority.ALWAYS);
        GridPane.setHgrow(moddedImageView, Priority.ALWAYS);
        GridPane.setValignment(moddedImageView, VPos.TOP);
        GridPane.setHalignment(moddedImageView, HPos.LEFT);
        moddedImageView.setTranslateX(17);
        moddedImageView.setTranslateY(185);
        moddedImageView.setFitHeight(64);
        moddedImageView.setFitWidth(64);



        GridPane.setVgrow(survivalSeparator, Priority.ALWAYS);
        GridPane.setHgrow(survivalSeparator, Priority.ALWAYS);
        GridPane.setHalignment(survivalSeparator, HPos.LEFT);
        GridPane.setValignment(survivalSeparator, VPos.TOP);
        survivalSeparator.setTranslateX(85);
        survivalSeparator.setTranslateY(188);
        survivalSeparator.setFill(Color.WHITE);


        Button settingsButton = new Button("");

        GridPane.setVgrow(moddedSeparator, Priority.ALWAYS);
        GridPane.setHgrow(moddedSeparator, Priority.ALWAYS);
        GridPane.setHalignment(moddedSeparator, HPos.LEFT);
        GridPane.setValignment(moddedSeparator, VPos.TOP);
        moddedSeparator.setTranslateX(85);
        moddedSeparator.setTranslateY(188);
        moddedSeparator.setFill(Color.WHITE);

        if (!Main.getSaver().get("selectedServer").equals("party")) {
            survivalOnlineLabel.setVisible(false);
            survivalPlayersLabel.setVisible(false);
            survivalStatusLabel.setVisible(false);
            survivalImageView.setVisible(false);
            survivalIPLabel.setVisible(false);
            survivalLabel.setVisible(false);
            survivalRectangle.setVisible(false);
            survivalSeparator.setVisible(false);
            updateModdedInfos();
            moddedOnlineLabel.setVisible(true);
            moddedPlayersLabel.setVisible(true);
            moddedStatusLabel.setVisible(true);
            moddedImageView.setVisible(true);
            moddedIPLabel.setVisible(true);
            moddedLabel.setVisible(true);
            moddedRectangle.setVisible(true);
            moddedSeparator.setVisible(true);
            versionSelectMenu.setItems(FXCollections.observableArrayList(Main.getVersionList("modded")));
            Main.logger.log("Vérification de la version précédemment sélectionnée");
            if (Main.getSaver().get("moddedVersion")!=null) {
                Main.logger.log("Version (Modded) sélectionnée : " + Main.getSaver().get("moddedVersion"));
                versionSelectMenu.getSelectionModel().select(Main.getSavedVersion()[0] + " (" + Main.getSavedVersion()[1] + ")");
            } else {
                Main.logger.log("Dernière version (Modded) sélectionnée par défaut");
                versionSelectMenu.getSelectionModel().selectLast();
            }
        } else {
            if (Main.getSaver().get("partyVersion")!=null) {
                Main.logger.log("Version (Party) sélectionnée : " + Main.getSaver().get("partyVersion"));
                versionSelectMenu.getSelectionModel().select(Main.getSavedVersion()[0] + " (" + Main.getSavedVersion()[1] + ")");
            } else {
                Main.logger.log("Dernière version (Party) sélectionnée par défaut");
                versionSelectMenu.getSelectionModel().selectLast();
            }
        }


        GridPane.setVgrow(launchButton, Priority.ALWAYS);
        GridPane.setHgrow(launchButton, Priority.ALWAYS);
        GridPane.setValignment(launchButton, VPos.BOTTOM);
        GridPane.setHalignment(launchButton, HPos.LEFT);
        launchButton.setTranslateY(-50);
        launchButton.setTranslateX(225);
        launchButton.setMinWidth(250);
        launchButton.setMaxWidth(250);
        launchButton.setMinHeight(70);
        launchButton.setMaxHeight(70);
        launchButton.getStylesheets().addAll(Main.class.getResource("/css/button.css").toExternalForm());
        launchButton.setStyle("-fx-font-size: 24pt;");
        launchButton.setOnMouseEntered(e -> this.layout.setCursor(Cursor.HAND));
        launchButton.setOnMouseExited(e -> this.layout.setCursor(Cursor.DEFAULT));
        launchButton.setOnMouseClicked(e -> {
            launchButton.getStylesheets().addAll(Main.class.getResource("/css/button_disabled.css").toExternalForm());
            launchButton.setStyle("-fx-font-size: 24pt;");
            if (Main.getSaver().get("selectedServer").equals("party")) {
                Main.logger.log("Lancement du jeu en version (Party) " + Main.getSaver().get("partyVersion"));
            } else {
                Main.logger.log("Lancement du jeu en version (Modded) " + Main.getSaver().get("moddedVersion"));
            }
            HomePanel.isUpdating.set(true);
                update(Main.getSaver().get("selectedServer"));
                launchButton.setDisable(true);
                settingsButton.setDisable(true);
                this.disconnectButton.setDisable(true);
                versionSelectMenu.setDisable(true);
                disableLogos.set(true);

        });
        MaterialDesignIconView settingsIcon = new MaterialDesignIconView(MaterialDesignIcon.SETTINGS);
        settingsIcon.setSize("25px");
        settingsIcon.setFill(Color.WHITE);

        GridPane.setVgrow(settingsButton, Priority.ALWAYS);
        GridPane.setHgrow(settingsButton, Priority.ALWAYS);
        GridPane.setValignment(settingsButton, VPos.TOP);
        GridPane.setHalignment(settingsButton, HPos.LEFT);
        settingsButton.setTranslateY(124);
        settingsButton.setTranslateX(160);
        settingsButton.setMinWidth(32);
        settingsButton.setMaxWidth(32);
        settingsButton.setMinHeight(32);
        settingsButton.setMaxHeight(32);
        settingsButton.getStylesheets().addAll(Main.class.getResource("/css/button.css").toExternalForm());
        settingsButton.setOnMouseEntered(e -> {
            this.layout.setCursor(Cursor.HAND);
            settingsIcon.setFill(Color.valueOf("#007dbe"));
            settingsButton.setGraphic(settingsIcon);
        });
        settingsButton.setOnMouseExited(e -> {
            this.layout.setCursor(Cursor.DEFAULT);
            settingsIcon.setFill(Color.WHITE);
            settingsButton.setGraphic(settingsIcon);
        });
        settingsButton.setOnMouseClicked(e -> {
            Main.logger.log("Affichage des paramètres");
            this.panelManager.showPanel(new SettingsPanel());
        });


        settingsButton.setGraphic(settingsIcon);

        downloadBar = new AProgressBar(500, 10);
        downloadBar.setBackgroundColor(Color.rgb(222, 222, 222, 0.45d));
        downloadBar.setProgress(0, 100);
        GridPane.setVgrow(downloadBar, Priority.ALWAYS);
        GridPane.setHgrow(downloadBar, Priority.ALWAYS);
        GridPane.setHalignment(downloadBar, HPos.LEFT);
        GridPane.setValignment(downloadBar, VPos.BOTTOM);
        downloadBar.setTranslateY(-150);
        Stop[] stop = new Stop[]{new Stop(0, Color.valueOf("#004266")), new Stop(1, Color.valueOf("#0085cc"))};
        LinearGradient linearGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stop);
        downloadBar.setForegroundColor(linearGradient);
        downloadBar.getStylesheets().addAll(Main.class.getResource("/css/progressBar.css").toExternalForm());

        GridPane.setVgrow(downloadLabel, Priority.ALWAYS);
        GridPane.setHgrow(downloadLabel, Priority.ALWAYS);
        GridPane.setValignment(downloadLabel, VPos.BOTTOM);
        GridPane.setHalignment(downloadLabel, downloadBar.getAlignment().getHpos());
        downloadLabel.setStyle("-fx-font-size: 19px; -fx-text-fill: white;");
        downloadLabel.setTranslateY(-170);
        downloadLabel.setTranslateX(200);
        downloadLabel.setText("");
        this.centerPane.getChildren().addAll(serverTitle, versionsLabel, versionSeparator, versionSelectMenu, launchButton, survivalRectangle, survivalImageView, survivalLabel,
                survivalSeparator, survivalIPLabel, survivalOnlineLabel, survivalStatusLabel, survivalPlayersLabel, moddedRectangle, moddedImageView, moddedLabel,
                moddedSeparator, moddedIPLabel, moddedOnlineLabel, moddedStatusLabel, moddedPlayersLabel, downloadBar, settingsButton, downloadLabel);
    }

    public static Thread updateThread;
    public void update(String server) {
        Main.logger.log("Mise a jour des fichiers du jeu...");
        downloadLabel.setTranslateX(75);
        SUpdate su = null;
        String version;
        switch (server) {
            case "party":
                version = Main.LPVersionsList[Main.LPVersionsList.length - 1][0];
                if (Main.getSaver().get("partyVersion") != null) {
                    version = Main.getSaver().get("partyVersion");
                }
                String suURL = "https://files.news-craft.fr/LaunchLicNoFOpV/" + version;
                String assetsDir = "versions/" + version + "/assets";
                String libsDir = "versions/" + version + "/libs";
                String nativesDir = "versions/" + version + "/natives";
                String jarDir = "versions/" + version + "/minecraft.jar";
                String logVersion = "Mise à jour de la version :" + version;
                final File LP_DEST_DIR = new File(Main.LP_VER_DIR, version);
                su = new SUpdate(suURL, LP_DEST_DIR);
                Main.LP_FOLDER = new GameFolder(assetsDir, libsDir, nativesDir, jarDir);
                Main.logger.log(logVersion);
                Main.LP_VERSION = new GameVersion(version, GameType.V1_8_HIGHER);
                String versionType = "Vanilla";
                for (String[] i : Main.LPVersionsList) {
                    if (i[0].equals(version)) {
                        versionType = i[1];
                    }
                }
                if (versionType.equals("Optifine")) {
                    Main.LP_INFOS = new GameInfos(Main.title, Main.LP_VERSION, new GameTweak[]{GameTweak.OPTIFINE});
                } else {
                    Main.LP_INFOS = new GameInfos(Main.title, Main.LP_VERSION, null);
                }


                this.setDownloadLabel("Acquisition de la liste des fichiers...");

                su.addApplication(new FileDeleter());
                break;


            case "modded":
                version = Main.LPMVersionsList[Main.LPMVersionsList.length - 1][0];
                if (Main.getSaver().get("moddedVersion") != null) {
                    version = Main.getSaver().get("moddedVersion");
                }
                Main.updateInfos();
                Main.LPM_INFOS = new GameInfos(Main.title, Main.LPM_VERSION, new GameTweak[]{GameTweak.FORGE});
                su = new SUpdate("https://files.news-craft.fr/LauncherLicModded/" + version, Main.LPM_DIR);
                Main.LPM_FOLDER = new GameFolder("assets", "libs", "natives", "minecraft.jar");
                Main.logger.log("Mise à jour de la version : " + version);
                this.setDownloadLabel("Acquisition de la liste des fichiers...");

                su.addApplication(new FileDeleter());

                break;
        }

        SUpdate finalSu = su;

        ExecutorService executorService = Executors.newFixedThreadPool(1);


        barUpdateThread.start();
        checkErrorThread.start();
        UpdateTask updateTask = new UpdateTask(finalSu);
        updateThread = new Thread(new Runnable() {
            ExecutorService executorService;
            UpdateTask updateTask;

            public void run() {
                executorService.execute(updateTask);
            }

            public Runnable pass(ExecutorService executorService, UpdateTask updateTask) {
                this.executorService = executorService;
                this.updateTask = updateTask;
                return this;
            }
        }.pass(executorService, updateTask));
        updateThread.start();
    }
    public void interruptUpdateThread() {
        HomePanel.updateThread.interrupt();
    }


    public void updateSurvivalInfos() {
        Main.logger.log("Mise à jour des informations du serveur survie");
        MinecraftPingReply survivalData;
        try {
            survivalData = new MinecraftPing().getPing(new MinecraftPingOptions().setHostname("news-craft.fr").setPort(29063).setTimeout(250));
            survivalOnline.set(true);
            survivalPlayers = survivalData.getPlayers().getOnline();
            Main.logger.log("Le serveur survie est en ligne ; Joueur(s) connecté(s) : " + survivalPlayers);
        } catch (IOException e) {
            survivalOnline.set(false);
            survivalPlayers = 0;
            Main.logger.warn("Le serveur survie est hors ligne");
        }
    }

    public void updateModdedInfos() {
        Main.logger.log("Mise à jour des informations du serveur modded");
        MinecraftPingReply moddedData;
        try {
            moddedData = new MinecraftPing().getPing(new MinecraftPingOptions().setHostname("licparty.fr").setPort(25566).setTimeout(250));
            moddedOnline.set(true);
            moddedPlayers = moddedData.getPlayers().getOnline();
            Main.logger.log("Le serveur modded est en ligne ; Joueur(s) connecté(s) : " + moddedPlayers);
        } catch (IOException e) {
            moddedOnline.set(false);
            moddedPlayers = 0;
            Main.logger.warn("Le serveur modded est hors ligne");
        }
    }

    public static void launch(String server) {

        Date date2 = new Date() ;
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
        Main.logger.log("Préparation au lancement du jeu...");
        Main.updateInfos();
        AuthInfos authInfos;
        if (Main.getSaver().get("connectWithMojang").equals("true")) {
            authInfos = new AuthInfos(Main.getSaver().get("username"), Main.getSaver().get("accessToken"), Main.getSaver().get("clientToken"), Main.getSaver().get("uuid"));
        } else {
            authInfos = new AuthInfos(Main.getSaver().get("username"),"none", "cracked_player");
        }

        switch(server) {
            case "party":
                Main.logger.log("Création d'un profil de lancement externe |  Version " + Main.getSaver().get("Version"));

                ExternalLaunchProfile profile = null;
                try {
                    profile = MinecraftLauncher.createExternalProfile(Main.LP_INFOS,  Main.LP_FOLDER, authInfos);
                } catch (Exception e) {
                    Main.setErrorPanelException(e);
                    Main.setErrorPanelName("Impossible de créer le profil de lancement du jeu !");
                    HomePanel.updateThread.interrupt();
                }
                SaverUtil versionSaver = new SaverUtil(new File(Main.LP_DIR_CONFIG, Main.getSavedVersion()[0] + ".properties"));
                if (versionSaver.get("ram")!=null) {
                    Main.logger.log("RAM Sélectionnée :" + (Integer.parseInt(versionSaver.get("ram"))+1) + " Go");
                    assert profile != null;
                    profile.getVmArgs().addAll(Collections.singletonList("-Xmx" + (Integer.parseInt(versionSaver.get("ram")) + 1) + "G"));
                } else {
                    versionSaver.set("ram", "1");
                    Main.logger.log("RAM Sélectionnée (défaut) : 2 Go");
                    assert profile != null;
                    profile.getVmArgs().addAll(Collections.singletonList("-Xmx2G"));
                }
                if (versionSaver.get("args")!=null) {
                    Main.logger.log("Arguments supplémentaires : " + versionSaver.get("args"));
                    profile.getVmArgs().addAll(Arrays.asList(versionSaver.get("args").split(" ")));
                }


                ExternalLauncher launcher = new ExternalLauncher(profile);

                Process p = null;
                try {
                    p = launcher.launch();
                    Main.logger.log("Démarrage du système de logs");

                    ProcessLogManager manager = new ProcessLogManager(p.getInputStream(), new File(Main.LP_LOGS_DIR, "[LParty, " + Main.getSavedVersion()[0] + "] - " + dateFormat2.format(date2) + ".log") );
                    manager.start();
                    Main.logger.log("Lancement terminé ");
                    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
                } catch (LaunchException e) {
                    Main.setErrorPanelException(e);
                    Main.setErrorPanelName("Impossible de lancer le jeu !");
                    HomePanel.updateThread.interrupt();
                } catch (Exception e) {
                    Main.setErrorPanelException(e);
                    Main.setErrorPanelName("Une erreur est survenue lors du lancement du jeu !");
                    HomePanel.updateThread.interrupt();
                }




                try {
                    Thread.sleep(5000L);
                    HomePanel.isLaunched.set(true);
                    assert p != null;
                    p.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                System.exit(0);
                break;




            case "modded":
                Main.logger.log( "Création d'un profil de lancement externe |  Version " + Main.getSavedVersion()[1] + " " + Main.getSavedVersion()[0]);

                ExternalLaunchProfile profileM = null;
                try {
                    profileM = MinecraftLauncher.createExternalProfile(Main.LPM_INFOS,  Main.LPM_FOLDER, authInfos);
                } catch (Exception e) {
                    Main.setErrorPanelException(e);
                    Main.setErrorPanelName("Impossible de créer le profil de lancement du jeu !");
                    HomePanel.updateThread.interrupt();
                }
                SaverUtil versionSaverM = new SaverUtil(new File(Main.LP_DIR_CONFIG, Main.getSavedVersion()[0] + ".properties"));
                if (versionSaverM.get("ram")!=null) {
                    Main.logger.log("RAM Sélectionnée :" + (Integer.parseInt(versionSaverM.get("ram"))+1) + " Go");
                    assert profileM != null;
                    profileM.getVmArgs().addAll(Collections.singletonList("-Xmx" + (Integer.parseInt(versionSaverM.get("ram")) + 1) + "G"));
                } else {
                    versionSaverM.set("ram", "3");
                    Main.logger.log("RAM Sélectionnée (défaut) : 4 Go");
                    assert profileM != null;
                    profileM.getVmArgs().addAll(Collections.singletonList("-Xmx4G"));
                }
                if (versionSaverM.get("args")!=null) {
                    Main.logger.log("Arguments supplémentaires : " + versionSaverM.get("args"));
                    profileM.getVmArgs().addAll(Arrays.asList(versionSaverM.get("args").split(" ")));
                }
                ExternalLauncher launcherM = new ExternalLauncher(profileM);

                Process pM = null;
                try {
                    pM = launcherM.launch();
                    Main.logger.log("Démarrage du système de logs");

                    ProcessLogManager managerM = new ProcessLogManager(pM.getInputStream(), new File(Main.LP_LOGS_DIR, Arrays.toString(Main.getSavedVersion()) + " - " + dateFormat2.format(date2) + ".log") );
                    managerM.start();
                } catch (LaunchException e) {
                    Main.setErrorPanelException(e);
                    Main.setErrorPanelName("Impossible de créer le profil de lancement du jeu !");
                    HomePanel.updateThread.interrupt();
                } catch (Exception e) {
                    Main.setErrorPanelException(e);
                    Main.setErrorPanelName("Une erreur est survenue lors du lancement du jeu !");
                    HomePanel.updateThread.interrupt();
                }






                try {
                    Thread.sleep(5000L);
                    HomePanel.isLaunched.set(true);
                    assert pM != null;
                    pM.waitFor();
                    System.exit(0);
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



        }


    }

}


class UpdateTask extends Task<Void> {
    private final SUpdate su;
    public UpdateTask(SUpdate su) {
        this.su = su;
    }

    @Override
    protected Void call() {
        try {
            su.start();
        } catch (BadServerResponseException e) {
            Main.setErrorPanelException(e);
            Main.setErrorPanelName("Impossible de contacter le serveur de fichiers !");
            this.cancel();
        } catch (ServerDisabledException e) {
            Main.setErrorPanelException(e);
            Main.setErrorPanelName("Le serveur de fichier est désactivé (Maintenance en cours ?),\nréessayez plus tard !");
            Main.logger.warn("DISABLED");
            this.cancel();
        } catch (BadServerVersionException e) {
            Main.setErrorPanelException(e);
            Main.setErrorPanelName("La version du serveur de fichier ne correspond pas à celle \nutilisée par le launcher !");
            this.cancel();
        } catch (ServerMissingSomethingException e) {
            Main.setErrorPanelException(e);
            Main.setErrorPanelName("Le serveur de fichier a rencontré une erreur, \nvérifiez son intégrité !");
            this.cancel();
        } catch (IOException e) {
            Main.setErrorPanelException(e);
            Main.setErrorPanelName("Impossible d'accéder au dossier du jeu pour \nle mettre à jour !");
            this.cancel();
        }

        HomePanel.isUpdating.set(false);
            if (Main.getErrorPanelException().get()==null) {
            HomePanel.launch(Main.getSaver().get("selectedServer")); }
        return null;
    }
}



