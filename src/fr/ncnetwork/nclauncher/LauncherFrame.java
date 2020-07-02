package fr.ncnetwork.nclauncher;

import java.awt.FontFormatException;
import java.io.IOException;

import java.io.PrintStream;

import javax.swing.JFrame;


import fr.theshark34.openlauncherlib.LanguageManager;
import fr.theshark34.openlauncherlib.util.CrashReporter;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.animation.Animator;
import fr.theshark34.swinger.util.WindowMover;



@SuppressWarnings("serial")
public class LauncherFrame extends JFrame{
	
	private static LauncherFrame instance;
	private LauncherPanel launcherPanel;
	private static CrashReporter crashReporter;
	public static String launcherVersion = "2.3.2";
	private static String launcherInfos = "  LP : Crack - Premium - Optifine Vanilla (no forge version) LM : Forge";
	private static String supportedVersions = "      1.12.2 - 1.13.2 - 1.14.3 - 1.15.2 - 1.16.1 - 1.12.2(LM3)     ";
	private static String buildNumber = "                         Build number : 2.7.3     ";
	public LauncherFrame() throws  IOException, FontFormatException, InterruptedException {
		this.setTitle("NC LP Launcher ");
		this.setSize(975, 516);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setUndecorated(true);
		this.setIconImage(Swinger.getResource("icon.png"));

		this.setContentPane(launcherPanel = new LauncherPanel());
		
		
		
		WindowMover mover = new WindowMover(this);
		this.addMouseListener(mover);
		this.addMouseMotionListener(mover);
		
		this.setVisible(true);
		
		Animator.fadeInFrame(this, Animator.FAST);
	}
	public static void main(String[] args) throws IOException, FontFormatException, InterruptedException {
			
		LanguageManager.setLang(LanguageManager.FRENCH);
		Swinger.setSystemLookNFeel();
		Swinger.setResourcePath("/fr/ncnetwork/nclauncher/ressources");
		
		Launcher.NC_DIR.mkdir();
		Launcher.NC_LOGS_DIR.mkdir();
		
		PrintStream launcherLogs = new PrintStream(Launcher.NC_LOGS_LAUNCHER);
		System.setOut(launcherLogs);
		
		LauncherPanel.launchLog("info", "                   -- Démarrage NC Launcher --");
		LauncherPanel.launchLog("info", "                    Licorne Party/Modded        ");
		String versionLog = "                          Version " + launcherVersion + "     ";
		LauncherPanel.launchLog("info", versionLog);
		LauncherPanel.launchLog("info", launcherInfos);
		LauncherPanel.launchLog("info", supportedVersions);
		LauncherPanel.launchLog("info", buildNumber);
		LauncherPanel.launchLog("info", "                           © 2014-2020        ");
		LauncherPanel.launchLog("info", "                           NC Network        ");
		LauncherPanel.launchLog("info", "                 ----https://news-craft.fr----");
		LauncherPanel.launchLog("info", " ");
		LauncherPanel.launchLog("debug", "Initialisation, affichage des spécificités matérielles : ");
		LauncherPanel.launchLog("debug", "*Processeur : " + System.getenv("PROCESSOR_IDENTIFIER"));
		LauncherPanel.launchLog("debug", "*Architecture du processeur : " + System.getenv("PROCESSOR_ARCHITECTURE"));
		LauncherPanel.launchLog("debug", "*Coeurs disponibles : " + System.getenv("NUMBER_OF_PROCESSORS"));
		LauncherPanel.launchLog("debug", "");
		LauncherPanel.launchLog("info","En attente");
		
		Launcher.NC_CRASHES_DIR.mkdirs();
		crashReporter = new CrashReporter("NC Launcher Crash Reporter", Launcher.NC_CRASHES_DIR);
		instance = new LauncherFrame();
	}
	
	
	public static LauncherFrame getInstance() {
		return instance;
	}
	
	
	public LauncherPanel getLauncherPanel() {
		return this.launcherPanel;
	}
	
	public static CrashReporter getCrashReporter() {
		return crashReporter;
	}

}
