package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

public class Config {

	private Logger logger = Logger.getLogger(Config.class.getName());
	private Properties prop = new Properties();
	private String configFileUrl = "./app.conf";

	public Config() {

		File file = new File(configFileUrl);
		if (!file.exists()) {
			try {
				file.createNewFile();
				prop.load(new FileInputStream(configFileUrl));
				prop.setProperty("native_lang", "Azerbaijani");
				prop.setProperty("second_lang", "English");
				prop.store(new FileOutputStream(configFileUrl), null);
			} catch (IOException e) {
				logger.error(e);
				JOptionPane.showMessageDialog(null, "Can't create file!", "IO Error", JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	public String GET_CONFIG(String KEY) {

		try {
			prop.load(new FileInputStream(configFileUrl));
		} catch (Exception e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "Can't find " + configFileUrl + " file!", "IO Error",
					JOptionPane.ERROR_MESSAGE);
		}

		if (KEY != null && !KEY.equals(""))
			return prop.getProperty(KEY);
		else
			return "Property not found!";

	}

	public boolean SET_CONFIG(String KEY, String VALUE) {

		if (KEY != null && !KEY.equals("") && VALUE != null && !VALUE.equals("")) {
			try {
				prop.load(new FileInputStream(configFileUrl));
				prop.setProperty(KEY, VALUE);
				prop.store(new FileOutputStream(configFileUrl), null);
			} catch (Exception e) {
				logger.error(e);
			}
			return true;
		} else
			return false;
	}

}
