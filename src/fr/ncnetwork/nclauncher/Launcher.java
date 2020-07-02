package fr.ncnetwork.nclauncher;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import fr.theshark34.openauth.AuthPoints;
import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openauth.Authenticator;
import fr.theshark34.openauth.model.AuthAgent;
import fr.theshark34.openauth.model.response.AuthResponse;
import fr.theshark34.openlauncherlib.LanguageManager;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.minecraft.GameInfos;
import fr.theshark34.openlauncherlib.minecraft.GameTweak;
import fr.theshark34.openlauncherlib.minecraft.GameType;
import fr.theshark34.openlauncherlib.minecraft.GameVersion;
import fr.theshark34.openlauncherlib.util.ProcessLogManager;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.theshark34.openlauncherlib.minecraft.MinecraftLauncher;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.integrated.FileDeleter;
import fr.theshark34.swinger.Swinger;


public class Launcher {
	
	public static GameVersion NC_VERSION = new GameVersion(LauncherPanel.versionsList[LauncherPanel.versionsList.length-1][0], GameType.V1_8_HIGHER);
	public static GameVersion NCM_VERSION = new GameVersion("1.12.2", GameType.V1_8_HIGHER);
	public static GameInfos NC_INFOS = new GameInfos("NC Network", NC_VERSION, new GameTweak[] {GameTweak.OPTIFINE});
	public static GameInfos NCM_INFOS = new GameInfos("LP Modded 3", NCM_VERSION, new GameTweak[] {GameTweak.FORGE});
	public static final File NC_DIR = NC_INFOS.getGameDir();
	public static final File NCM_DIR = NCM_INFOS.getGameDir();
	public static final File NC_VER_DIR = new File(NC_DIR, "versions");
	public static final File NC_CRASHES_DIR = new File(NC_DIR, "crash-reports");
	public static GameFolder NC_FOLDER = null;
	public static GameFolder NCM_FOLDER = null;
	public static final File NC_LAUNCHER_PROPERTIES = new File(NC_DIR, "launcher.properties");
	public static final File NC_LOGS_DIR = new File(NC_DIR, "logs");
	public static  File NC_LOGS_LAUNCHER = new File(NC_LOGS_DIR, "launcher.log");
	public static File NC_DIR_CONFIG = new File(NC_DIR, "config");
	
	private static AuthInfos authInfos;
	private static Thread updateThread;
	
	
	static class authID {
		private final String name;
		private final String id;
		
		public authID(String name, String id) {
			this.name = name;
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public String getID() {
			return id;
		}
	}
			 static authID auth(String username, String password, boolean isPremium) throws AuthenticationException {
				String name = null;
				String id = null;
				if (isPremium) {
					LauncherFrame.getInstance().getLauncherPanel().setInfoText("Connexion à Mojang...");
					Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
					AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, "");
					authInfos = new AuthInfos(response.getSelectedProfile().getName(), response.getAccessToken(), response.getSelectedProfile().getId());
					LauncherFrame.getInstance().getLauncherPanel().setInfoText("Authentification terminée !");
					name = response.getSelectedProfile().getName();
					id = response.getSelectedProfile().getId();
				} else {
				authInfos = new AuthInfos(username, "sry", "crack"); 
				name = username;
				id = "crack"; }
				return new authID(name, id); 
				
			}
			
	public static void update(String version, String server) throws Exception {
	LauncherPanel.launchLog("info","Mise a jour des fichiers du jeu...");
	SUpdate su = null;
	switch(server) {
	
	
	case "Party":
		String suURL = "https://files.news-craft.fr/LaunchLicNoFOpV/" + version;
		String assetsDir = "versions/" + version + "/assets";
		String libsDir = "versions/" + version + "/libs";
		String nativesDir = "versions/" + version + "/natives";
		String jarDir = "versions/" + version + "/minecraft.jar";
		String logVersion = "Mise à jour de la version :" + version;
		final File NC_DEST_DIR = new File(NC_VER_DIR, version + "");
		su = new SUpdate(suURL, NC_DEST_DIR);
		NC_FOLDER = new GameFolder(assetsDir, libsDir, nativesDir, jarDir);
		LauncherPanel.launchLog("info", logVersion);
		NC_VERSION = new GameVersion(version, GameType.V1_8_HIGHER);
		String versionType = "Vanilla";
		for (String[] i : LauncherPanel.versionsList ) {
			if (i[0].equals(version)) {
				if (i[1].equals("O")) {
					versionType = "Optifine";
				}
			}
		}
		if (versionType.equals("Optifine")) {
			NC_INFOS = new GameInfos("NC Network", NC_VERSION, new GameTweak[] {GameTweak.OPTIFINE});
		} else {
			NC_INFOS = new GameInfos("NC Network", NC_VERSION, null);
		}
		
		
		LauncherFrame.getInstance().getLauncherPanel().setInfoText("Acquisition de la liste des fichiers...");
		
			su.addApplication(new FileDeleter());
		break;
		
		
	case "Modded":
		
		su = new SUpdate("https://files.news-craft.fr/LicModded3/Launcher", NCM_DIR);
		NCM_FOLDER = new GameFolder("assets", "libs", "natives", "minecraft.jar");
		LauncherPanel.launchLog("info","Mise à jour de la version : 1.12.2 - Licorne Modded III");
	LauncherFrame.getInstance().getLauncherPanel().setInfoText("Acquisition de la liste des fichiers...");
	
		su.addApplication(new FileDeleter());
		
		break;
	}
	Thread updateThread = new Thread() {
		private int val;
		private int max;
		
		@Override 
		public void run() {
			while(!this.isInterrupted()) {
				
				if(BarAPI.getNumberOfFileToDownload() == 0) {
					LauncherFrame.getInstance().getLauncherPanel().setInfoText("Verification des fichiers en cours...");
					continue;
				}
				
				val = (int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000);
				max = (int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000);
				
				LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setMaximum(max);
				LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setValue(val);
				
				LauncherFrame.getInstance().getLauncherPanel().setInfoText("Telechargement des fichiers... " +
						BarAPI.getNumberOfDownloadedFiles() + "/" + BarAPI.getNumberOfFileToDownload() + " " +
						Swinger.percentage(val, max) + "%");
				
			}
		}
	};
	updateThread.start();
	
	su.start();
	updateThread.interrupt();
	
	}
	
public static void launch(String server) throws LaunchException 
	
	{
	
		Saver saver = new Saver(new File(Launcher.NC_DIR, "launcher.properties"));
	    Date date2 = new Date() ;
	    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
		LauncherPanel.launchLog("info", "Préparation au lancement du jeu...");
		LauncherFrame.getInstance().getLauncherPanel().setInfoText("Lancement en cours...");
		
		LanguageManager.setLang(LanguageManager.FRENCH);
		
		switch(server) {
		case "Party":
			LauncherPanel.launchLog("info", "Création d'un profil de lancement externe |  Version " + saver.get("Version"));

			ExternalLaunchProfile profile = MinecraftLauncher.createExternalProfile(NC_INFOS,  NC_FOLDER, authInfos);
			profile.getVmArgs().addAll(Arrays.asList(LauncherFrame.getInstance().getLauncherPanel().getRamSelector().getRamArguments()));
			ExternalLauncher launcher = new ExternalLauncher(profile);

			Process p = launcher.launch();
			

			LauncherPanel.launchLog("info", "Démarrage du système de logs");
			
			ProcessLogManager manager = new ProcessLogManager(p.getInputStream(), new File(NC_LOGS_DIR, "NCNetwork - " + dateFormat2.format(date2) + ".log") );
			ProcessLogManager managerlatest = new ProcessLogManager(p.getInputStream(), new File(NC_DIR, "latest.log") );
			manager.start();
			managerlatest.start();
			
			LauncherPanel.launchLog("info", "Lancement terminé ");
		    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
			
			try {
				Thread.sleep(5000L);
				LauncherFrame.getInstance().setVisible(false);
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			

			System.exit(0);
			break;
			
			
			
			
		case "Modded":
			LauncherPanel.launchLog("info", "Création d'un profil de lancement externe |  Version 1.12.2 Licorne Modded 3");

			ExternalLaunchProfile profileM = MinecraftLauncher.createExternalProfile(NCM_INFOS,  NCM_FOLDER, authInfos);
			LauncherPanel.launchLog("info","Ram Args:" + LauncherFrame.getInstance().getLauncherPanel().getRamSelector().getRamArguments());
			LauncherPanel.launchLog("info","VM Args:" + profileM.getVmArgs());
			profileM.getVmArgs().addAll(Arrays.asList(LauncherFrame.getInstance().getLauncherPanel().getRamSelector().getRamArguments()));
			ExternalLauncher launcherM = new ExternalLauncher(profileM);

			Process pM = launcherM.launch();
			

			LauncherPanel.launchLog("info", "Démarrage du système de logs");
			
			ProcessLogManager managerM = new ProcessLogManager(pM.getInputStream(), new File(NC_LOGS_DIR, "LicModded3 - " + dateFormat2.format(date2) + ".log") );
			ProcessLogManager managerlatestM = new ProcessLogManager(pM.getInputStream(), new File(NC_DIR, "latest.log") );
			managerM.start();
			managerlatestM.start();
			
			LauncherPanel.launchLog("info", "Lancement terminé ");
		    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
			
			try {
				Thread.sleep(5000L);
				LauncherFrame.getInstance().setVisible(false);
				pM.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			

			System.exit(0);
			break;
		}
		
	
	}
	
	public static void interruptThread() {
		if(updateThread  !=  null)
		updateThread.interrupt();
	}
	

	}
	
	



