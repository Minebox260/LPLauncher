package fr.ncnetwork.nclauncher;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class saveURLImage {

	public saveURLImage(String imageURL, String destinationFile)throws Exception {

		saveImage(imageURL, destinationFile);
	}

	public static void saveImage(String imageURL, String destinationFile) throws IOException {
		URL url = new URL(imageURL);
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(Launcher.NC_DIR + "/" + destinationFile);
		LauncherPanel.launchLog("info", "Enregistrement de " + imageURL + " dans " + (Launcher.NC_DIR + "/" + destinationFile));
		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();
	}

}