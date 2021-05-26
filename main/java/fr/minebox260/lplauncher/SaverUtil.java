//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package fr.minebox260.lplauncher;

import fr.theshark34.openlauncherlib.FailException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

public class SaverUtil {
    private  final File file;
    private final Properties properties;

    public SaverUtil(File file) {
        this.file = file;
        this.properties = new Properties();
        if (file.exists()) {
            this.load();
            this.checkConfig();
        } else {
            this.set("config", Main.version);
        }

    }

    private void deleteProperties() {
        System.gc();
        try {
            this.clear();
            Main.logger.log("launcher.properties vidé avec succès");
        } catch(Exception e) {
            Main.logger.log("info","Impossible de vider launcher.properties");
        }
        this.set("config", Main.version);
    }

    public void checkConfig() {
        Main.logger.log("Vérification de la configuration");
        if (this.get("config")!=null) {
            if (this.get("config").equals(Main.version)) {
                Main.logger.log("La configuration est à jour !");
            } else {
                Main.logger.log("Le fichier de configuration n'est pas à jour; Suppression...");
                deleteProperties();
            }

        } else {
            Main.logger.log("Impossible de récupérer la version du fichier de configuration ! Suppression...");
            deleteProperties();
        }
    }

    public void set(String key, String value) {
        this.properties.setProperty(key, value);
        this.save();
    }
    public void remove(String key) {
        this.properties.remove(key);
        this.save();
    }
    public void clear() {
        this.properties.clear();
        this.save();
    }

    public String get(String key) {
        return this.properties.getProperty(key);
    }

    public String get(String key, String def) {
        String value = this.properties.getProperty(key);
        return value == null ? def : value;
    }

    public void save() {
        try {
            this.properties.store(new BufferedWriter(new FileWriter(this.file)), "NC Licorne Party Launcher Configuration");
        } catch (Throwable var2) {
            throw new FailException("Can't save the properties", var2);
        }
    }

    public void load() {
        try {
            this.properties.load(new FileInputStream(this.file));
        } catch (Throwable var2) {
            throw new FailException("Can't load the properties", var2);
        }
    }
}
