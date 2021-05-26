package fr.minebox260.lplauncher.ui.panels;

import fr.litarvan.openauth.AuthPoints;
import fr.litarvan.openauth.AuthenticationException;
import fr.litarvan.openauth.Authenticator;
import fr.litarvan.openauth.model.AuthAgent;
import fr.litarvan.openauth.model.response.AuthResponse;
import fr.litarvan.openauth.model.response.RefreshResponse;
import fr.minebox260.lplauncher.Main;
import fr.minebox260.lplauncher.auth.crack.AuthCrack;
import fr.minebox260.lplauncher.ui.PanelManager;
import fr.minebox260.lplauncher.ui.panel.Panel;
import fr.minebox260.lplauncher.utils;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
public class LoginPanel extends Panel {


    public static AuthPoints points = AuthPoints.NORMAL_AUTH_POINTS;
    public static Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, points);

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);
        GridPane loginPanel = new GridPane();
        GridPane mainPanel = new GridPane();
        GridPane bottomPanel = new GridPane();

        if (Main.getSaver().get("connectWithMojang")!=null) {
            if (Main.getSaver().get("connectWithMojang").equals("true") && Main.getSaver().get("accessToken") != null) {
                String clientToken = null;
                if (Main.getSaver().get("clientToken") != null) {
                   clientToken = Main.getSaver().get("clientToken");
                }
                if (Main.getSaver().get("autoConnect").equals("true")) {
                    Main.logger.log("Tentative d'auto-connexion avec Minecraft Premium");
                    try {
                        RefreshResponse response = authenticator.refresh(Main.getSaver().get("accessToken"), clientToken);
                        Main.getSaver().set("uuid", response.getSelectedProfile().getId());
                        Main.getSaver().set("accessToken", response.getAccessToken());
                        Main.getSaver().set("username", response.getSelectedProfile().getName());
                        Main.getSaver().set("clientToken", response.getClientToken());
                        Main.logger.log("Connexion réussie");
                        Main.logger.log("Affichage de l'interface d'accueil");
                        this.panelManager.showPanel(new HomePanel());
                    } catch (AuthenticationException e) {
                        Main.logger.log("Impossible de se connecter à Minecraft Premium avec le token précédent ; Veuillez vous connecter.");
                        Main.getSaver().remove("accessToken");
                    }
                } else {
                    Main.logger.log("Auto-connexion désactivée");
                }
            } else if (Main.getSaver().get("connectWithMojang").equals("false") && Main.getSaver().get("username")!=null) {
                if (Main.getSaver().get("doNotReconnectCrack")==null) {
                    if (Main.getSaver().get("autoConnect").equals("true")) {
                        Main.logger.log("Tentative d'auto-connexion en mode crack");
                        AuthCrack authCrack = new AuthCrack(Main.getSaver().get("username"));
                        if (authCrack.isConnected()) {
                            Main.logger.log("Connexion réussie");
                            Main.logger.log("Affichage de l'interface d'accueil");
                            this.panelManager.showPanel(new HomePanel());
                        } else {
                            Main.logger.warn("Impossible de se connecter ; pseudonyme invalide.");
                            Main.getSaver().set("username", "");
                        }
                    } else {
                        Main.logger.log("Auto-connexion désactivée");
                    }
                } else {
                    Main.getSaver().remove("doNotReconnectCrack");
                }
            }
        } else {
            Main.getSaver().set("connectWithMojang", "true");
        }
        if (Main.getSaver().get("username")==null) {
            Main.getSaver().set("username", "");
        }
        if (Main.getSaver().get("mail")==null) {
            Main.getSaver().set("mail", "");
        }

        loginPanel.setMaxWidth(600);
        loginPanel.setMinWidth(600);
        loginPanel.setMaxHeight(300);
        loginPanel.setMinHeight(300);

        GridPane.setVgrow(loginPanel, Priority.ALWAYS);
        GridPane.setHgrow(loginPanel, Priority.ALWAYS);
        GridPane.setValignment(loginPanel, VPos.CENTER);
        GridPane.setHalignment(loginPanel, HPos.CENTER);

        RowConstraints bottomConstraints = new RowConstraints();
        bottomConstraints.setValignment(VPos.BOTTOM);
        bottomConstraints.setMaxHeight(55);
        loginPanel.getRowConstraints().addAll(new RowConstraints(), bottomConstraints);
        loginPanel.add(mainPanel, 0,0);
        loginPanel.add(bottomPanel, 0,1);

        GridPane.setVgrow(mainPanel, Priority.ALWAYS);
        GridPane.setHgrow(mainPanel, Priority.ALWAYS);
        GridPane.setVgrow(bottomPanel, Priority.ALWAYS);
        GridPane.setHgrow(bottomPanel, Priority.ALWAYS);

        mainPanel.setStyle("-fx-background-color: #181818;");
        bottomPanel.setStyle("-fx-background-color: #181818; -fx-opacity: 50%;");

        Label copyright = new Label( "© NC Network 2014-2020");
        Label website = new Label("https://news-craft.fr");

        GridPane.setVgrow(copyright, Priority.ALWAYS);
        GridPane.setHgrow(copyright, Priority.ALWAYS);
        GridPane.setValignment(copyright, VPos.TOP);
        GridPane.setHalignment(copyright, HPos.CENTER);

        GridPane.setVgrow(website, Priority.ALWAYS);
        GridPane.setHgrow(website, Priority.ALWAYS);
        GridPane.setValignment(website, VPos.BOTTOM);
        GridPane.setHalignment(website, HPos.CENTER);

        copyright.setStyle("-fx-text-fill: #bcc6e7; -fx-font-size: 14px;");
        copyright.setTranslateY(10);
        website.setStyle("-fx-text-fill: #69a7ed; -fx-font-size: 14px;");
        website.setUnderline(true);
        website.setTranslateY(-10);
        website.setOnMouseEntered(e->this.layout.setCursor(Cursor.HAND));
        website.setOnMouseExited(e->this.layout.setCursor(Cursor.DEFAULT));
        website.setOnMouseClicked(e-> utils.openUrl("https://news-craft.fr"));

        bottomPanel.getChildren().addAll(copyright, website);
        this.layout.getChildren().add(loginPanel);

        Label connectLabel = new Label("CONNEXION");
        GridPane.setVgrow(connectLabel, Priority.ALWAYS);
        GridPane.setHgrow(connectLabel, Priority.ALWAYS);
        GridPane.setValignment(connectLabel, VPos.TOP);
        connectLabel.setTranslateY(27);
        connectLabel.setTranslateX(37.5);
        connectLabel.setStyle("-fx-text-fill: #bcc6e7; -fx-font-size: 18px;");

        Separator connectSeparator = new Separator();
        GridPane.setVgrow(connectSeparator, Priority.ALWAYS);
        GridPane.setHgrow(connectSeparator, Priority.ALWAYS);
        GridPane.setValignment(connectSeparator, VPos.TOP);
        GridPane.setHalignment(connectSeparator, HPos.LEFT);
        connectSeparator.setTranslateY(60);
        connectSeparator.setTranslateX(37.5);
        connectSeparator.setMinWidth(325);
        connectSeparator.setMaxWidth(325);
        connectSeparator.setStyle("-fx-background-color: #fff; -fx-opacity: 50%;");

        Label usernameLabel = new Label("Adresse e-mail");
        GridPane.setVgrow(usernameLabel, Priority.ALWAYS);
        GridPane.setHgrow(usernameLabel, Priority.ALWAYS);
        GridPane.setValignment(usernameLabel, VPos.TOP);
        GridPane.setHalignment(usernameLabel, HPos.LEFT);
        usernameLabel.setStyle("-fx-text-fill: #95bad3; -fx-font-size: 16px;");
        usernameLabel.setTranslateY(75);
        usernameLabel.setTranslateX(37.5);

        TextField usernameField = new TextField();
        GridPane.setVgrow(usernameField, Priority.ALWAYS);
        GridPane.setHgrow(usernameField, Priority.ALWAYS);
        GridPane.setValignment(usernameField, VPos.TOP);
        GridPane.setHalignment(usernameField, HPos.LEFT);
        usernameField.setMaxWidth(325);
        usernameField.setMaxHeight(40);
        usernameField.setTranslateX(37.5);
        usernameField.setTranslateY(105);
        usernameField.setStyle("-fx-background-color: #1e1e1e; -fx-font-size: 16px; -fx-text-fill: #e5e5e5");



        Separator usernameSeparator = new Separator();
        GridPane.setVgrow(usernameSeparator, Priority.ALWAYS);
        GridPane.setHgrow(usernameSeparator, Priority.ALWAYS);
        GridPane.setValignment(usernameSeparator, VPos.TOP);
        GridPane.setHalignment(usernameSeparator, HPos.LEFT);
        usernameSeparator.setTranslateY(146);
        usernameSeparator.setTranslateX(37.5);
        usernameSeparator.setMinWidth(325);
        usernameSeparator.setMaxWidth(325);
        usernameSeparator.setMaxHeight(1);
        usernameSeparator.setStyle("-fx-background-color: #fff; -fx-opacity: 10%;");

        Label passwordLabel = new Label("Mot de passe");
        GridPane.setVgrow(passwordLabel, Priority.ALWAYS);
        GridPane.setHgrow(passwordLabel, Priority.ALWAYS);
        GridPane.setValignment(passwordLabel, VPos.TOP);
        GridPane.setHalignment(passwordLabel, HPos.LEFT);
        passwordLabel.setStyle("-fx-text-fill: #95bad3; -fx-font-size: 16px;");
        passwordLabel.setTranslateY(151);
        passwordLabel.setTranslateX(37.5);

        Label errorLabel = new Label("Impossible de se connecter");
        GridPane.setVgrow(errorLabel, Priority.ALWAYS);
        GridPane.setHgrow(errorLabel, Priority.ALWAYS);
        GridPane.setValignment(errorLabel, VPos.TOP);
        errorLabel.setStyle("-fx-text-fill: darkred; -fx-font-size: 13px;");
        errorLabel.setTranslateY(40);
        errorLabel.setTranslateX(210);
        errorLabel.setVisible(false);

        PasswordField passwordField = new PasswordField();
        GridPane.setVgrow(passwordField, Priority.ALWAYS);
        GridPane.setHgrow(passwordField, Priority.ALWAYS);
        GridPane.setValignment(passwordField, VPos.TOP);
        GridPane.setHalignment(passwordField, HPos.LEFT);
        passwordField.setMaxWidth(325);
        passwordField.setMaxHeight(40);
        passwordField.setTranslateX(37.5);
        passwordField.setTranslateY(181);
        passwordField.setStyle("-fx-background-color: #1e1e1e; -fx-font-size: 16px; -fx-text-fill: #e5e5e5");



        Separator passwordSeparator = new Separator();
        GridPane.setVgrow(passwordSeparator, Priority.ALWAYS);
        GridPane.setHgrow(passwordSeparator, Priority.ALWAYS);
        GridPane.setValignment(passwordSeparator, VPos.TOP);
        GridPane.setHalignment(passwordSeparator, HPos.LEFT);
        passwordSeparator.setTranslateY(222);
        passwordSeparator.setTranslateX(37.5);
        passwordSeparator.setMinWidth(325);
        passwordSeparator.setMaxWidth(325);
        passwordSeparator.setMaxHeight(1);
        passwordSeparator.setStyle("-fx-background-color: #fff; -fx-opacity: 10%;");

        Rectangle crackRectangle = new Rectangle(175,40);
        GridPane.setVgrow(crackRectangle, Priority.ALWAYS);
        GridPane.setHgrow(crackRectangle, Priority.ALWAYS);
        GridPane.setValignment(crackRectangle, VPos.TOP);
        GridPane.setHalignment(crackRectangle, HPos.RIGHT);
        crackRectangle.setTranslateX(-35);
        crackRectangle.setTranslateY(72.5);
        crackRectangle.setArcHeight(10);
        crackRectangle.setArcWidth(10);
        crackRectangle.setFill(Color.valueOf("#1e1e1e"));

        CheckBox isCracked = new CheckBox("Mode Crack");
        isCracked.setAllowIndeterminate(false);
        GridPane.setVgrow(isCracked, Priority.ALWAYS);
        GridPane.setHgrow(isCracked, Priority.ALWAYS);
        GridPane.setValignment(isCracked, VPos.TOP);
        GridPane.setHalignment(isCracked, HPos.RIGHT);
        isCracked.setTranslateX(-75);
        isCracked.setTranslateY(82);
        isCracked.setStyle("-fx-background-color: #1e1e1e; -fx-font-size: 15px; -fx-text-fill: #e5e5e5");
        isCracked.setOnMouseClicked(e->{
            if (isCracked.isSelected()) {
                Main.logger.log("Passage en mode crack");
                passwordField.setText("");
                usernameLabel.setText("Pseudonyme");
                setFieldEnabled(false,passwordField,passwordLabel);
                errorLabel.setVisible(false);
                usernameField.setText(Main.getSaver().get("username"));
                Main.getSaver().set("connectWithMojang", "false");
            } else  {
                Main.logger.log("Passage en mode premium");
                usernameLabel.setText("Adresse e-mail");
                Main.getSaver().set("connectWithMojang", "true");
                errorLabel.setVisible(false);
                usernameField.setText(Main.getSaver().get("mail"));
                setFieldEnabled(true,passwordField,passwordLabel);
            }
        });
        Main.logger.log("Vérification du mode de connexion précédemment choisi");
        if (Main.getSaver().get("connectWithMojang").equals("true")) {
            Main.logger.log("Mode Premium sélectionné");
            usernameField.setText(Main.getSaver().get("mail"));
        } else {
            Main.logger.log("Mode Crack sélectionné");
            usernameLabel.setText("Pseudonyme");
            usernameField.setText(Main.getSaver().get("username"));
            setFieldEnabled(false,passwordField,passwordLabel);
            isCracked.setSelected(true);
        }

        Button loginButton = new Button("Se connecter");
        GridPane.setVgrow(loginButton, Priority.ALWAYS);
        GridPane.setHgrow(loginButton, Priority.ALWAYS);
        GridPane.setValignment(loginButton, VPos.TOP);
        GridPane.setHalignment(loginButton, HPos.RIGHT);
        loginButton.setTranslateY(160);
        loginButton.setTranslateX(-35);
        loginButton.setMinWidth(175);
        loginButton.setMinHeight(50);
        loginButton.getStylesheets().addAll(Main.class.getResource("/css/button.css").toExternalForm());
        loginButton.setOnMouseEntered(e-> this.layout.setCursor(Cursor.HAND));
        loginButton.setOnMouseExited(e-> this.layout.setCursor(Cursor.DEFAULT));
        loginButton.setOnMouseClicked(e-> {
            if (Main.getSaver().get("connectWithMojang").equals("true")) {
                Main.logger.log("Tentative de connexion à mojang...");
                Main.getSaver().set("mail", usernameField.getText());
                setFieldEnabled(false,passwordField,passwordLabel);
                setFieldEnabled(false,usernameField,usernameLabel);
                String clientToken = null;
                if (Main.getSaver().get("clientToken")!=null) {
                    clientToken = Main.getSaver().get("clientToken");
                }
                try {
                    AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, usernameField.getText(), passwordField.getText(), clientToken);
                    String accessToken = response.getAccessToken();
                    String username = response.getSelectedProfile().getName();
                    Main.getSaver().set("username", username);
                    String uuid = response.getSelectedProfile().getId();
                    Main.getSaver().set("uuid", uuid);
                    clientToken = response.getClientToken();
                    Main.getSaver().set("clientToken", clientToken);
                    Main.getSaver().set("accessToken", accessToken);
                    Main.logger.log("Connexion réussie en tant que " + Main.getSaver().get("username"));
                    Main.logger.log("Affichage de l'interface d'accueil");
                    this.panelManager.showPanel(new HomePanel());
                } catch (AuthenticationException  ice) {
                    Main.logger.warn("Impossible de se connecter !");
                    errorLabel.setText("Impossible de se connecter");
                    errorLabel.setVisible(true);
                    passwordField.setText("");
                    setFieldEnabled(true,passwordField,passwordLabel);
                    setFieldEnabled(true,usernameField,usernameLabel);
                }


            } else {
                Main.logger.log("Tentative de connexion en mode crack");
                setFieldEnabled(false,usernameField,usernameLabel);
                    AuthCrack authCrack = new AuthCrack((usernameField.getText()));
                    if (authCrack.isConnected()) {
                        Main.logger.log("Connexion réussie en tant que " + authCrack.getPseudo());
                        Main.getSaver().set("username", authCrack.getPseudo());
                        Main.logger.log("Affichage de l'interface d'accueil");
                        this.panelManager.showPanel(new HomePanel());
                    } else {
                        Main.logger.warn("Impossible de se connecter ; Pseudo invalide");
                        errorLabel.setText("Pseudo invalide");
                        errorLabel.setVisible(true);
                        setFieldEnabled(true,usernameField,usernameLabel);
                    }

            }


        });
        mainPanel.getChildren().addAll(connectLabel, connectSeparator, usernameLabel, usernameField,
                usernameSeparator, passwordLabel, passwordField, passwordSeparator, loginButton,
                crackRectangle, isCracked, errorLabel);
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
