package engine.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import engine.Engine;
import engine.Game;
import engine.launcher.LaunchConfig;
import engine.networknio.ConnectionNIO;
import engine.physics.Physics;

/**
 * A wrapper around a {@code File} used to store configuration data for the {@code Engine} and any subsequent
 * {@code Game}s
 * 
 * @author Kevin
 */
public class Configuration {
	
	/**
	 * The config instance of {@code Logger}
	 */
	public static final Logger logger = Logger.getLogger("engine.config");
	
	/**
	 * The separator between name and value
	 */
	public static final String DELIMITER = "=";
	
	public File file;
	
	private List<Property> properties = new ArrayList<Property>();
	
	// Default Engine Properties @formatter:off
	public Property tickRate = new Property("TickRate", Engine.DEFAULT_TICK_RATE, Integer.class);
	public Property frameRate = new Property("FrameRate", Engine.DEFAULT_FRAME_RATE, Integer.class);
	public Property vSync = new Property("VSync", Engine.DEFAULT_VSYNC, Boolean.class);
	public Property tcpBuff = new Property("TCPBufferSize", ConnectionNIO.DEFAULT_TCP_BUFFER_SIZE, Integer.class);
	public Property udpBuff = new Property("UDPBufferSize", ConnectionNIO.DEFAULT_UDP_BUFFER_SIZE, Integer.class);
	public Property cfgLog = new Property("LogConfig", Engine.DEFAULT_LOG_CONFIG, Boolean.class);
	public Property allLog = new Property("LogAll", Engine.DEFAULT_LOG_ALL, Boolean.class);
	public Property physTicks = new Property("PhysicsTicks", Physics.DEFAULT_SUBTICKS, Integer.class);
	//@formatter:on
	
	public Configuration(File file) {
		this.file = file;
		addProperty(tickRate);
		addProperty(frameRate);
		addProperty(vSync);
		addProperty(tcpBuff);
		addProperty(udpBuff);
		addProperty(cfgLog);
		addProperty(allLog);
	}
	
	public Configuration(LaunchConfig lcfg) {
		this(getSuggestedConfigFile(lcfg));
	}
	
	/**
	 * Gets the suggested Configuration file
	 * 
	 * @return
	 */
	public static File getSuggestedConfigFile(LaunchConfig lcfg) {
		return new File("cfg/" + Game.getName(lcfg.getGameClass()) + ".cfg");
	}
	
	/**
	 * Attempts to load the configuration file, if one exists. If not, then a new file is created and
	 * populated.
	 */
	public void load() {
		try {
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
					logger.config("Parent Directories of config file did not exist, creating");
				}
				file.createNewFile();
				logger.config("Config file " + file.getAbsolutePath() + " did not exist, creating");
				save();
				return;
			}
			read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the Configuration from the file
	 */
	public void read() {
		logger.fine("Attempting Read of Config file");
		// Fatal error which will require reloading of the file
		boolean fatal = false;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			int lineNum = 0;
			while ((line = reader.readLine()) != null) {
				lineNum++;
				String[] strs = line.split(DELIMITER);
				if (strs.length != 2) {
					logger.severe("Configuration File line " + lineNum
							+ " is errored! Expected two arguments separated by \"" + DELIMITER + "\", got "
							+ strs.length);
					fatal = true;
					continue;
				}
				Property prop = getMatchingProperty(strs[0]);
				if (prop == null) {
					logger.warning("Property with name " + strs[0]
							+ " was not found! Did you add it to the configuration object?");
					continue;
				}
				prop.setValue(strs[1]);
				if (!prop.checkValue()) {
					logger.warning("Property with name " + strs[0] + " failed type check! Expected type "
							+ prop.getCheckClass().getSimpleName() + " but failed!");
					fatal = true;
					prop.setValue(null);
					continue;
				}
				logger.config(prop.internalName + DELIMITER + prop.getValue());
			}
			reader.close();
			for (Property prop : properties) {
				if (!prop.hasValue()) {
					logger.warning("Property " + prop.internalName + " was not assigned a value!");
					fatal = true;
				}
			}
			if (fatal) {
				logger.info("A fatal error was encountered while reading from " + file.getName()
						+ ". A copy of this file has been saved, and a new file has been generated.");
				Files.move(file.toPath(), Paths.get(file.getPath() + ".fatal"),
						StandardCopyOption.REPLACE_EXISTING);
				load();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves the configuration to the file
	 */
	public void save() {
		logger.fine("Attempting save of Config file");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			for (Property prop : properties) {
				writer.write(prop.internalName);
				writer.write(DELIMITER);
				writer.write(prop.getValue());
				writer.newLine();
				logger.config(prop.internalName + DELIMITER + prop.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the {@code Property} with the name matching, or null if one is not found
	 * 
	 * @param name
	 * @return
	 */
	private Property getMatchingProperty(String name) {
		for (Property prop : properties) {
			if (prop.internalName.equals(name)) {
				return prop;
			}
		}
		return null;
	}
	
	/**
	 * Adds a {@code Property} to the internal list of Properties
	 * 
	 * @param prop
	 *            The {@code Property} to add
	 * @return This {@code Configuration}, to allow for chaining
	 */
	public Configuration addProperty(Property prop) {
		this.properties.add(prop);
		logger.config("Adding property " + prop.internalName);
		return this;
	}
}
