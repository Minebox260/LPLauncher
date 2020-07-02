package fr.ncnetwork.nclauncher;

import static fr.theshark34.swinger.Swinger.getResource;
import static fr.theshark34.swinger.Swinger.getTransparentWhite;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fr.ncnetwork.nclauncher.Launcher.authID;

import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import fr.theshark34.swinger.animation.Animator;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;

@SuppressWarnings("serial")
public class LauncherPanel extends JPanel implements SwingerEventListener {

	private File bgFolder = new File(Launcher.NC_DIR, "Launcher/backgrounds");
	private File[] backgrounds = bgFolder.listFiles();

	Random rand = new Random();
	private File background = backgrounds[rand.nextInt(backgrounds.length)];
	private Image backgroundHUD = getResource("backgroundHUD.png");
	private Image logo = getResource("logo.png");
	private Image head = getResource("defaultHead.png");
	private File propertiesFile = new File(Launcher.NC_DIR, "launcher.properties");
	Saver saver = new Saver(propertiesFile);
	private static String windowName = "NC Network - Launcher LP  - Version " + LauncherFrame.launcherVersion;
	public static boolean isPremium = true;
	File ramConfig = new File(Launcher.NC_DIR, "ram.properties");
	private RamSelector ramSelector = new RamSelector(ramConfig);

	private JTextField usernameField = null;
	private JPasswordField passwordField = new JPasswordField();

	private static STexturedButton playButton = new STexturedButton(getResource("playButton.png"), getResource("playButtonHover.png"));
	private static STexturedButton closeButton = new STexturedButton(getResource("close.png"));
	private static STexturedButton hideButton = new STexturedButton(getResource("reduce.png"));
	private static STexturedButton paramButton = new STexturedButton(getResource("settings.png") ,getResource("settingsHover.png"));
	private static STexturedButton serverButton = new STexturedButton(getResource("selectServer.png"));
	private static STexturedButton crackButton = new STexturedButton(getResource("crack.png"));
	private static STexturedButton premiumButton = new STexturedButton(getResource("premium.png"));

	private static STexturedButton errorPanel = new STexturedButton(getResource("Error.png"), getResource("Error.png"));
	private static STexturedButton errorButton = new STexturedButton(getResource("errorButton.png"), getResource("errorButtonHover.png"));

	private static STexturedButton versionPanel = new STexturedButton(getResource("selectVersion.png"), getResource("selectVersion.png"));
	private static STexturedButton versionButtonLeft = new STexturedButton(getResource("selectVersionLeft.png"));
	private static STexturedButton versionButtonRight = new STexturedButton(getResource("selectVersionRight.png"));

	private JLabel versionLabel = null;
	private JLabel serverLabel = new JLabel("Party", SwingConstants.CENTER);
	private JLabel windowLabel = new JLabel(windowName, SwingConstants.LEFT);
	private JLabel infoLabel = new JLabel("En attente...", SwingConstants.CENTER);
	private JLabel playerLabel = new JLabel("Nouveau Joueur", SwingConstants.CENTER);
	private static JLabel errorLabel = new JLabel("");

	private SColoredBar progressBar = new SColoredBar(getTransparentWhite(50), getTransparentWhite(125));

	static String[][] versionsList = { { "1.12.2", "O" }, { "1.13.2", "O" }, { "1.14.3", "O" }, { "1.15.2", "O" }, { "1.16.1", "V" } };
	static String[][] versionsListM = { { "LM3", "1.12.2" }};
	static String lastVer = versionsList[versionsList.length - 1][0];
	
	private void deleteProperties() throws InterruptedException {
		saver = null;
		Runtime.getRuntime().gc();
		System.gc();
		TimeUnit.SECONDS.sleep((long) 0.5);
	    if (propertiesFile.delete()) {
			launchLog("info","Fichier launcher.properties supprimé avec succès");
		} else {
			launchLog("info","Impossible de supprimer launcher.properties");
		}
	    TimeUnit.SECONDS.sleep((long) 0.5);
		saver = new Saver(propertiesFile);
	}
	
	public LauncherPanel() throws MalformedURLException, IOException, FontFormatException, InterruptedException {
		
		// CONFIG CHECK
		launchLog("info","Vérification de la configuration...");
		if (!(saver.get("Config") == null)) {
			if (!(saver.get("Config").equals(LauncherFrame.launcherVersion))) {
				launchLog("info","La configuration date d'une version antérieure (" + saver.get("Congig") + " Suppression de launcher.properties...");
				deleteProperties();
			}
		} else {
			launchLog("info","La configuration date d'une version inconnue ; Suppression de launcher.properties...");
			deleteProperties();
		}
		saver.set("Config",LauncherFrame.launcherVersion);
		
		// VERSION CHECK
		launchLog("info","Vérification de la version de minecraft précédemment sélectionnée...");
		if (!(saver.get("Version") == null)) {

			String Ver = saver.get("Version");
			String newString = lastVer;
				for (String[] i : versionsList) {
					if (i[0].equals(Ver)) {
						newString = Ver;
						launchLog("info","Version sélectionnée : " + newString);
						break;
					}
			}
			versionLabel = new JLabel(newString, SwingConstants.CENTER);
		} else {
			launchLog("info","Pas de version trouvée, sélection de la plus récente par défaut (" + lastVer + ")");
			versionLabel = new JLabel(lastVer, SwingConstants.CENTER);
			saver.set("Version", lastVer);
		}

		// SERVER CHECK
		launchLog("info","Vérification du serveur précédemment sélectionné...");
		if ((saver.get("Server") == null)) {
			launchLog("info","Pas de serveur trouvé, sélection du serveur Party par défaut");
			serverLabel = new JLabel("Party", SwingConstants.CENTER);
			saver.set("Server", "Party");
		} else {
			String serv = saver.get("Server");
			switch (serv) {
			case "Modded":
				launchLog("info","Serveur sélectionné : Modded");
				versionLabel.setVisible(false);
				versionButtonLeft.setVisible(false);
				versionButtonRight.setVisible(false);
				versionPanel.setVisible(false);
				break;
			case "Party":
				launchLog("info","Serveur sélectionné : Party");
				versionLabel.setVisible(true);
				versionButtonLeft.setVisible(true);
				versionButtonRight.setVisible(true);
				versionPanel.setVisible(true);
			}

		}
		launchLog("info","Vérification du dernier pseudo utilisé...");
		//USERNAME CHECK
		if (!(saver.get("MCUsername") == null)) {
			launchLog("info","Pseudo : " + saver.get("MCUsername"));
			playerLabel.setText(saver.get("MCUsername"));
		}
		// ACCOUNT TYPE CHECK	
		launchLog("info","Vérification du type de compte précédemment sélectionné...");
		if ((saver.get("Premium") == null)) {
			launchLog("info","Aucun type trouvé, sélection du mode Premium par défaut");
			premiumButton.setEnabled(false);
			saver.set("Premium", "true");
			if (!(saver.get("UUID") == null)) {
				saveURLImage.saveImage("https://crafatar.com/avatars/" + saver.get("UUID"), "head.png");
				launchLog("info","Récupération de l'image : https://crafatar.com/avatars/" + saver.get("UUID"));
				head = ImageIO.read(new File(Launcher.NC_DIR, "head.png"));
			}
		} else {
			if (saver.get("Premium").equals("true")) {
				launchLog("info","Mode sélectionné : Premium");
				premiumButton.setEnabled(false);
				if (!(saver.get("UUID") == null)) {
					saveURLImage.saveImage("https://crafatar.com/avatars/" + saver.get("UUID"), "head.png");
					launchLog("info","Récupération de l'image : https://crafatar.com/avatars/" + saver.get("UUID"));
					head = ImageIO.read(new File(Launcher.NC_DIR, "head.png"));
				}
			} else {
				launchLog("info","Mode sélectionné : Crack");
				crackButton.setEnabled(false);
				setPwdFieldState(false);
			}
		}

		this.setLayout(null);

		Font police2 = Font.createFont(Font.TRUETYPE_FONT, LauncherPanel.class.getClassLoader()
				.getResourceAsStream("fr/ncnetwork/nclauncher/ressources/fixedsystem.ttf"));

		launchLog("info", "Création de l'interface...");
		
		usernameField = new JTextField(saver.get("Username"));
		usernameField.setOpaque(false);
		usernameField.setBorder(null);
		usernameField.setForeground(Color.WHITE);
		usernameField.setCaretColor(Color.WHITE);
		usernameField.setHorizontalAlignment(JTextField.CENTER);
		usernameField.setFont(police2.deriveFont(Font.PLAIN, 25));
		usernameField.setBounds(28, 424, 201, 26);
		usernameField.setToolTipText("Adresse E-Mail");
		this.add(usernameField);

		passwordField.setOpaque(false);
		passwordField.setBorder(null);
		passwordField.setForeground(Color.WHITE);
		passwordField.setCaretColor(Color.WHITE);
		passwordField.setHorizontalAlignment(JPasswordField.CENTER);
		passwordField.setFont(police2.deriveFont(Font.PLAIN, 25));
		passwordField.setBounds(28, 465, 201, 26);
		passwordField.setToolTipText("Mot de passe");
		this.add(passwordField);

		playButton.setBounds(262, 425);
		playButton.addEventListener(this);
		this.add(playButton);

		crackButton.setBounds(262, 391);
		crackButton.addEventListener(this);
		this.add(crackButton);

		premiumButton.setBounds(329, 391);
		premiumButton.addEventListener(this);
		this.add(premiumButton);

		versionLabel.setForeground(Color.WHITE);
		versionLabel.setFont(usernameField.getFont().deriveFont(Font.BOLD, 35));
		versionLabel.setBounds(830, 325, 101, 53);
		this.add(versionLabel);

		serverLabel.setForeground(Color.WHITE);
		serverLabel.setFont(usernameField.getFont().deriveFont(Font.BOLD, 35));
		serverLabel.setBounds(882, 166, 93, 36);
		serverLabel.setVisible(true);
		serverLabel.setText(saver.get("Server"));
		this.add(serverLabel);

		windowLabel.setForeground(Color.BLACK);
		windowLabel.setFont(usernameField.getFont().deriveFont(Font.BOLD, 55));
		windowLabel.setBounds(140, 0, 700, 35);
		windowLabel.setVisible(true);
		this.add(windowLabel);

		versionButtonLeft.setBounds(788, 326);
		versionButtonLeft.addEventListener(this);
		this.add(versionButtonLeft);

		versionButtonRight.setBounds(932, 326);
		versionButtonRight.addEventListener(this);
		this.add(versionButtonRight);

		versionPanel.setBounds(788, 278);
		this.add(versionPanel);

		serverButton.setBounds(882, 118);
		serverButton.addEventListener(this);
		this.add(serverButton);

		closeButton.setBounds(921, 0);
		closeButton.addEventListener(this);
		this.add(closeButton);

		paramButton.setBounds(1, 0);
		paramButton.addEventListener(this);
		this.add(paramButton);

		hideButton.setBounds(866, 0);
		hideButton.addEventListener(this);
		this.add(hideButton);

		errorLabel.setFont(usernameField.getFont().deriveFont(Font.BOLD, 25));
		errorLabel.setBounds(388, 174, 478, 56);
		errorLabel.setVisible(false);
		errorLabel.setEnabled(false);
		this.add(errorLabel);

		errorButton.setBounds(391, 241, 87, 26);
		errorButton.addEventListener(this);
		errorButton.setVisible(false);
		this.add(errorButton);

		errorPanel.setBounds(378, 122);
		errorPanel.setVisible(false);
		this.add(errorPanel);

		progressBar.setBounds(415, 401, 548, 25);
		this.add(progressBar);

		infoLabel.setForeground(Color.BLACK);
		infoLabel.setFont(usernameField.getFont().deriveFont(Font.BOLD, 35));
		infoLabel.setBounds(415, 401, 548, 25);
		this.add(infoLabel);

		playerLabel.setForeground(Color.WHITE);
		playerLabel.setFont(police2.deriveFont(Font.BOLD, 50));
		playerLabel.setBounds(705, 450, 237, 43);
		this.add(playerLabel);

		printPropertiesFile();
	}

	public void onEvent(SwingerEvent e) {
		
		if (e.getSource() == playButton) {
			launchGame();
			
		} else if (e.getSource() == closeButton) {
			Animator.fadeOutFrame(LauncherFrame.getInstance(), 2, new Runnable() {
				public void run() {
					launchLog("info", "Fermeture");
					System.exit(0);
				}
			});
			
		} else if (e.getSource() == hideButton) {
			LauncherFrame.getInstance().setState(JFrame.ICONIFIED);
			
		} else if (e.getSource() == paramButton) {
			ramSelector.display();
			
		} else if (e.getSource() == errorButton) {
			errorButton.setVisible(false);
			errorPanel.setVisible(false);
			errorLabel.setVisible(false);
			setFieldsEnabled(true);
			Launcher.interruptThread();
			LauncherFrame.getInstance().getLauncherPanel().setInfoText("En attente...");
			
		} else if (e.getSource() == versionButtonLeft) {

			String actVer = saver.get("Version");
			String newVer = versionsList[(versionsList.length - 1)][0];
			int actArray = versionsList.length - 1;
			int count = 0;
			for (String[] i : versionsList) {
				if (i[0].equals(actVer)) {
					actArray = count;
				}
				count++;
			}
			int newArray = actArray - 1;
			if (newArray < 0) {
				newArray = versionsList.length - 1;
			}

			newVer = versionsList[newArray][0];

			versionLabel.setText(newVer);
			saver.set("Version", newVer);
			
		} else if (e.getSource() == versionButtonRight) {

			String actVer = saver.get("Version");
			String newVer = versionsList[(versionsList.length - 1)][0];
			int actArray = versionsList.length - 1;
			int count = 0;
			for (String[] i : versionsList) {
				if (i[0].equals(actVer)) {
					actArray = count;
				}
				count++;
			}
			int newArray = actArray + 1;
			if (newArray > (versionsList.length - 1)) {
				newArray = 0;
			}

			newVer = versionsList[newArray][0];

			versionLabel.setText(newVer);
			saver.set("Version", newVer);
			
		} else if (e.getSource() == crackButton) {
			saver.set("Premium", "false");
			setPwdFieldState(false);
			crackButton.setEnabled(false);
			premiumButton.setEnabled(true);

		} else if (e.getSource() == premiumButton) {
			saver.set("Premium", "true");
			setPwdFieldState(true);
			crackButton.setEnabled(true);
			premiumButton.setEnabled(false);

		} else if (e.getSource() == serverButton) {

			String actServ = saver.get("Server");
			String newServ = "Party";

			switch (actServ) {
			case "Party":
				newServ = "Modded";
				versionLabel.setVisible(false);
				versionButtonLeft.setVisible(false);
				versionButtonRight.setVisible(false);
				versionPanel.setVisible(false);
				break;
			case "Modded":
				newServ = "Party";
				versionLabel.setVisible(true);
				versionButtonRight.setVisible(true);
				versionButtonLeft.setVisible(true);
				versionPanel.setVisible(true);
				break;
			}
			serverLabel.setText(newServ);
			saver.set("Server", newServ);
		}
	}

	public void launchGame() {
		setFieldsEnabled(false);
		launchLog("info", "Vérification des champs de texte");
		if (usernameField.getText().replaceAll(" ", "").length() < 5) {
			errorMsg("Veuillez entrer un pseudo valide. (5 Caracteres minimum) ", "[CRACK] : Peudo invalide.");
			return;
		}

		if (saver.get("Premium").equals("true")) {
			isPremium = true;
		} else {
			isPremium = false;
		}
		Thread t = new Thread() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				try {
					authID auth = Launcher.auth(usernameField.getText(), passwordField.getText(), isPremium);
					saver.set("MCUsername", auth.getName());
					if (isPremium) {
						saver.set("UUID", auth.getID());
					}
					saver.set("MCUsername", auth.getName());
					launchLog("info", "Authentification terminée !");
					launchLog("info", ("Username : " + auth.getName()));
					launchLog("info", ("UUID : " + auth.getID()));
				} catch (AuthenticationException e1) {
					errorMsg("Impossible de se connecter à Mojang !", null);
					e1.printStackTrace();
					return;
				}
				ramSelector.save();
				saver.set("Username", usernameField.getText());

				String selVersion = saver.get("Version");
				String selServer = saver.get("Server");
				try {
					Launcher.update(selVersion, selServer);
				} catch (Exception e) {
					errorMsg("Serveurs de MAJ offlines, impossible de mettre a jour les fichiers !", null);
					return;
				}

				try {
					Launcher.launch(selServer);
				} catch (LaunchException e) {
					errorMsg("Erreur inconnue, impossible de lancer le jeu ! ", null);
				}

			}
		};
		t.start();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		try {
			g.drawImage(ImageIO.read(background), 0, 0, this.getWidth(), this.getHeight(), this);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		g.drawImage(backgroundHUD, 0, 0, this.getWidth(), this.getHeight(), this);
		g.drawImage(logo, 0, 0, this.getWidth(), this.getHeight(), this);
		g.drawImage(head, 627, 438, 64, 64, this);
		// * if (!(saver.get("UUID") == null)) {

	}

	private void setFieldsEnabled(boolean enabled) {
		String isPremium = saver.get("Premium");
		if (isPremium.equals("true")) {
			crackButton.setEnabled(enabled);
			passwordField.setEnabled(enabled);
		} else {
			premiumButton.setEnabled(enabled);
		}
		usernameField.setEnabled(enabled);
		playButton.setEnabled(enabled);
		
		paramButton.setEnabled(enabled);
		versionButtonLeft.setEnabled(enabled);
		versionButtonRight.setEnabled(enabled);
		versionPanel.setEnabled(enabled);
		serverButton.setEnabled(enabled);
	}

	public static void launchLog(String Type, String Text) {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (Type.equals("info")) {
			System.out.println("[" + dateFormat.format(date) + "] " + "[INFO] " + "[NC Launcher] " + Text);
		} else if (Type.equals("err")) {
			System.out
					.println("[" + dateFormat.format(date) + "] " + "[INFO] " + "[NC Launcher] " + "[ERREUR] " + Text);
		} else if (Type.equals("debug")) {
			System.out.println("[" + dateFormat.format(date) + "] " + "[DEBUG] " + "[NC Launcher] " + Text);
		}
	}

	private void setPwdFieldState(boolean enabled) {
		if (enabled) {
			passwordField.setText("");
		} else {
			passwordField.setText("--------------------");
		}
		passwordField.setEnabled(enabled);
	}

	public void errorMsg(String Error, String customLog) {

		Launcher.interruptThread();
		if (!(customLog == null))
			launchLog("err", customLog);
		else
			launchLog("err", Error);
		errorLabel.setText(Error);
		errorLabel.setVisible(true);
		errorPanel.setVisible(true);
		errorButton.setVisible(true);
	}

	public SColoredBar getProgressBar() {
		return progressBar;
	}

	public RamSelector getRamSelector() {
		return ramSelector;
	}

	public void setInfoText(String text) {
		infoLabel.setText(text);
	}

	public static void printPropertiesFile() throws IOException {
		if (Launcher.NC_LAUNCHER_PROPERTIES.exists()) {
			launchLog("debug", "Affichage du contenu de launcher.properties");
			BufferedReader in = new BufferedReader(new FileReader(Launcher.NC_LAUNCHER_PROPERTIES));
			launchLog("debug", "------  launcher.properties  ------");
			String line;
			while ((line = in.readLine()) != null) {
				launchLog("debug", ("    " + line));
			}
			in.close();
			launchLog("debug", "------  launcher.properties  ------");
		} else {
			launchLog("info", "Le fichier launcher.properties n'existe pas, création...");
		}
	}

	public String getUsername() {
		return usernameField.getText();
	}
}
