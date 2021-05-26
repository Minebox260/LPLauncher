package fr.minebox260.lplauncher;

import fr.arinonia.arilibfx.utils.AriLogger;
import fr.theshark34.openlauncherlib.minecraft.*;
import fr.theshark34.openlauncherlib.util.CrashReporter;
import javafx.application.Application;

import javax.swing.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;


public class Main {

    public static AtomicReference<Exception> getErrorPanelException() {
        return errorPanelException;
    }

    public static AtomicReference<String> getErrorPanelName() {
        return errorPanelName;
    }

    public static void setErrorPanelException(Exception e) {
        Main.errorPanelException.set(e);
    }

    public static void setErrorPanelName(String name) {
        Main.errorPanelName.set(name);
    }

    public static String launcherName = "Licorne Party";
    public static AtomicReference<Exception> errorPanelException = new AtomicReference<>(null);
    public static AtomicReference<String> errorPanelName = new AtomicReference<>("Erreur Inconnue");

    public static String version = "BETA 3.8.0";
    public static final String title = launcherName;
    public static AriLogger logger = new AriLogger(title);
    public static String[][] LPVersionsList = { { "1.12.2", "Optifine" }, { "1.13.2", "Optifine" }, { "1.14.3", "Optifine" }, { "1.15.2", "Optifine" }, { "1.16.4", "Optifine" } };
    public static String LPVersionsListText = Arrays.deepToString(LPVersionsList).replaceAll("[\"{}, ]","").replace("Optifine"," (O) - ")
            .replace("Vanilla", " (V) - ").replace("[","").replace("]", "");
    public static String[][] LPMVersionsList = { { "LM3", "1.12.2" }, {"LM4", "1.12.2"}, {"LM5", "1.12.2"}};
    public static String LPMVersionsListText = Arrays.deepToString(LPMVersionsList).replaceAll("[\"{} ]","")
            .replace(",", " ").replace("[","").replace("]", " (F) -");

    public static GameVersion LP_VERSION = new GameVersion(LPVersionsList[LPVersionsList.length-1][0], GameType.V1_8_HIGHER);
    public static GameVersion LPM_VERSION = new GameVersion(LPMVersionsList[LPMVersionsList.length-1][1], GameType.V1_8_HIGHER);
    public static GameInfos LP_INFOS = new GameInfos(launcherName, LP_VERSION, new GameTweak[] {GameTweak.OPTIFINE});
    public static GameInfos LPM_INFOS = new GameInfos("LP-" +LPMVersionsList[LPMVersionsList.length-1][0], LPM_VERSION, new GameTweak[] {GameTweak.FORGE});
    public static final File LP_DIR = LP_INFOS.getGameDir();
    public static final File LPB_DIR = new File(LP_DIR, "Launcher");
    public static File LPM_DIR = LPM_INFOS.getGameDir();

    public static final File LP_VER_DIR = new File(LP_DIR, "versions");
    public static final File LP_CRASHES_DIR = new File(LP_DIR, "crash-reports");
    public static File LP_DIR_CONFIG = new File(LPB_DIR, "config");
    public static GameFolder LP_FOLDER = null;
    public static GameFolder LPM_FOLDER = null;
    public static final File LP_LAUNCHER_PROPERTIES = new File(LP_DIR_CONFIG, "launcher.properties");
    public static final File LP_LOGS_DIR = new File(LP_DIR, "logs");
    public static  File LP_LOGS_LAUNCHER = new File(LP_LOGS_DIR, "launcher.log");

    public static SaverUtil saver = null;
    public static CrashReporter crashReporter;
    public static SaverUtil getSaver() {
        return saver;
    }

    public static void main(String[] args) throws IOException {
        if (LP_DIR.mkdir()) logger.log("Génération du dossier : " + LP_DIR.getPath());
        if (LPB_DIR.mkdir()) logger.log("Génération du dossier : " + LPB_DIR.getPath());
        if (LP_LOGS_DIR.mkdir()) logger.log("Génération du dossier : " + LP_LOGS_DIR.getPath());
        if (LP_DIR_CONFIG.mkdir()) logger.log("Génération du dossier : " + LP_DIR_CONFIG.getPath());
        PrintStream launcherLogs = new PrintStream(LP_LOGS_LAUNCHER);
        System.setOut(launcherLogs);
        System.setErr(launcherLogs);
        saver = new SaverUtil(LP_LAUNCHER_PROPERTIES);
        LPVersionsListText = LPVersionsListText.substring(0,LPVersionsListText.length() - 3);
        LPMVersionsListText = LPMVersionsListText.substring(0,LPMVersionsListText.length() - 7);
        if (LP_CRASHES_DIR.mkdirs()) logger.log("Génération du dossier : " + LP_CRASHES_DIR.getPath());
        crashReporter = new CrashReporter("LP Launcher Crash Reporter", LP_CRASHES_DIR);
        boolean doesPartyVersionExist = false;
        if (saver.get("partyVersion")!=null) {
            for (String[] vers : LPVersionsList) {
                doesPartyVersionExist = vers[0].equals(saver.get("partyVersion"));
                if (doesPartyVersionExist)break;
            }
            if (!doesPartyVersionExist)saver.set("partyVersion", LPVersionsList[LPVersionsList.length-1][0]);
        } else {
            saver.set("partyVersion", LPVersionsList[LPVersionsList.length-1][0]);
        }
        boolean doesModdedVersionExist = false;
        if (saver.get("moddedVersion")!=null) {
            for (String[] vers : LPVersionsList) {
                doesModdedVersionExist = vers[1].equals(saver.get("moddedVersion"));
                if (doesModdedVersionExist)break;
            }
            if (!doesModdedVersionExist)saver.set("moddedVersion", LPMVersionsList[LPMVersionsList.length-1][0]);
        } else {
            saver.set("moddedVersion", LPMVersionsList[LPMVersionsList.length-1][0]);
        }
        if (saver.get("selectedServer")!=null) {
            if (!(saver.get("selectedServer").equals("party") ||saver.get("selectedServer").equals("modded"))) saver.set("selectedServer", "party");
        } else {
            saver.set("selectedServer", "party");
        }
        if (saver.get("autoConnect")==null) {
            saver.set("autoConnect","true");
        } else if(!saver.get("autoConnect").equals("false")) {
            saver.set("autoConnect","true");
        }

        if (saver.get("loadCrackSkin")==null) {
            saver.set("loadCrackSkin","false");
        } else if(!saver.get("loadCrackSkin").equals("true")) {
            saver.set("loadCrackSkin","false");
        }

        logger.log("-- Démarrage NC Launcher --");
        logger.log("Licorne Party/Modded        ");
        String versionLog = "Version " +  version + "     ";
        logger.log(versionLog);
        String launcherInfos = "  LP : Crack - Premium - Optifine Vanilla (no forge version) LM : Forge";
        logger.log(launcherInfos);
        logger.log(LPVersionsListText);
        logger.log(LPMVersionsListText);
        logger.log("© 2014-2021        ");
        logger.log("NC Network        ");
        logger.log("----https://news-craft.fr----");
        logger.log(" ");
        logger.log("Initialisation, affichage des spécificités matérielles : ");
        logger.log("*Processeur : " + System.getenv("PROCESSOR_IDENTIFIER"));
        logger.log("*Architecture du processeur : " + System.getenv("PROCESSOR_ARCHITECTURE"));
        logger.log("*Coeurs disponibles : " + System.getenv("NUMBER_OF_PROCESSORS"));
        logger.log("");
        logger.log("Architecture de l'OS : " + System.getProperty("os.arch"));
        logger.log("Nom de l'OS : " + System.getProperty("os.name"));
        logger.log("Version de l'OS : " + System.getProperty("os.version"));
        logger.log("");
        logger.log("Version de java : " + System.getProperty("java.version"));
        logger.log("Modèle de l'architecture de la JVM : " + System.getProperty("sun.arch.data.model"));
        logger.log("");

        logger.log("Vérification de la présence de JavaFX...");
        try {
            Class.forName("javafx.application.Application");
            logger.log("La classe JavaFX a été trouvée !");
            logger.log("Lancement de l'application JavaFX...");
            if (System.getProperty("sun.arch.data.model").equals("32")) {
                logger.warn("/=================================\\");
                logger.warn("LA JVM INSTALLEE EST UNE VERSION 32 BITS !");
                logger.warn("/=================================\\");
                JOptionPane.showMessageDialog(null, "ATTENTION ! La version de java installée est \n prévue pour les systèmes 32 bits ; Cela risque \n d'empêcher le lancement de minecraft !", "Avertissement (JVM 32Bits)", JOptionPane.WARNING_MESSAGE);
            }
            Application.launch(FxApplication.class, args);
        } catch (ClassNotFoundException e) {
            logger.warn("Impossible de trouver JavaFX ! :cry:");
            logger.warn("Fermeture");
            JOptionPane.showMessageDialog(null, "Une erreur avec JavaFX est survenue :\n\n"+ e.getMessage() + "\n Classe introuvable", "Erreur JavaFX", JOptionPane.ERROR_MESSAGE);
        }

    }

    public static String[] getVersionList(String server) {
        String[] versions;
        int count = 0;
        if ("modded".equals(server)) {
            versions = new String[LPMVersionsList.length];
            for (String[] i : LPMVersionsList) {
                versions[count] = i[0] + " (" + i[1] + ")";
                count++;
            }
        } else {
            versions = new String[LPVersionsList.length];
            for (String[] i : LPVersionsList) {
                String type;
                if (i[1].equals("Optifine")) type = " (Optifine)";
                else type = " (Vanilla)";
                versions[count] = i[0] + type;
                count++;
            }
        }
        return versions;
    }

    public static String[] getSavedVersion() {
        String server = saver.get("selectedServer");
        if (server.equals("modded")) {
            for (String[] vers : LPMVersionsList) {
                if (vers[0].equals(saver.get("moddedVersion"))) {
                    return vers;
                }
            }

        } else {
            for (String[] vers : LPVersionsList) {
                if (vers[0].equals(saver.get("partyVersion"))) {
                    return vers;
                }
            }
        }
        return new String[] {"null", "null"};
    }
    public static void updateInfos() {
        if (saver.get("selectedServer").equals("modded")) {
            if ( Integer.parseInt(getSavedVersion()[1].split("\\.")[1]) >= 13 ){
                LPM_VERSION = new GameVersion(getSavedVersion()[1], GameType.V1_13_HIGHER_FORGE);
                LPM_INFOS = new GameInfos("LP-" +getSavedVersion()[0], LPM_VERSION, null);
            } else {
                LPM_VERSION = new GameVersion(getSavedVersion()[1], GameType.V1_8_HIGHER);
                LPM_INFOS = new GameInfos("LP-" +getSavedVersion()[0], LPM_VERSION, new GameTweak[] {GameTweak.FORGE});
            }


            LPM_DIR =  LPM_INFOS.getGameDir();
        } else {
            LP_VERSION = new GameVersion(getSavedVersion()[0], GameType.V1_8_HIGHER);
            if (getSavedVersion()[1].equals("Optifine")) {
                LP_INFOS = new GameInfos(title, LP_VERSION, new GameTweak[]{GameTweak.OPTIFINE});
            } else {
                LP_INFOS = new GameInfos(title, LP_VERSION, null);
            }
        }
    }
}
