package fr.minebox260.lplauncher;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class utils {

    public static void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            Main.logger.warn("Impossible d'ouvrir l'URL (" + url + ") \n Erreur : \n" + e.getMessage());
        }
    }

    public static BufferedReader connectToURL(String url) {
        try {
            URLConnection connect = (new URL(url).openConnection());
            connect.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11(KHTML, like Gecko) Chrome/23/0/1271.95 Safari/537.11");
            connect.connect();
            InputStream is = connect.getInputStream();
            return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        } catch (Exception e) {
            Main.logger.warn(e.getMessage());
            return null;
        }
    }


    public static URL getRequestUrl(String request) {
        try {
            return new URL("https://authserver.mojang.com/" + request);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

}
