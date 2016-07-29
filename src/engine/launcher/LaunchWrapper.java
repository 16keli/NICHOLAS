package engine.launcher;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import engine.Engine;
import engine.Game;
import engine.client.Client;
import engine.server.Server;

/**
 * A launch wrapper for launching external games from Jars
 * <p>
 * In order to make use of the {@code LaunchWrapper}, it is expected that compiled Jar files are placed in the
 * appropriate directory, and that the following Constructors for subclasses are public and follow these exact
 * parameters
 * 
 * <pre>
 * public class GameSubclass extends Game {
 * 	
 * 	public GameSubclass() {
 * 		super("Game Name");
 * 		// Additional code...
 * 	}
 * }
 * 
 * public class ClientSubclass extends Client {
 * 	
 * 	public ClientSubclass(Game game, int width, int height, int scale) {
 * 		super(game, width, height, scale);
 * 		// Additional code...
 * 	}
 * 	
 * 	public ClientSubclass(Game game, int width, int height) {
 * 		super(game, width, height);
 * 		// Additional code...
 * 	}
 * }
 * 
 * public class ServerSubclass extends Server {
 * 	
 * 	public ServerSubclass(Game game, int port, int minConnects) {
 * 		super(game, port, minConnects);
 * 		// Additional code...
 * 	}
 * }
 * </pre>
 * 
 * @author Kevin
 */
public class LaunchWrapper {
	
	/**
	 * Launches the Windowed version of the game
	 * 
	 * @param width
	 *            The width of the internal image
	 * @param height
	 *            The height of the internal image
	 * @param scale
	 *            The scale of the window
	 */
	public static void launchWindowed(int width, int height, int scale) {
	
	}
	
	/**
	 * Launches the Fullscreen version of the game
	 * 
	 * @param width
	 *            The width of the internal image
	 * @param height
	 *            The height of the internal image
	 */
	public static void launchFullscreen(int width, int height) {
	
	}
	
	/**
	 * Launches the dedicated server
	 * 
	 * @param port
	 *            The port to start the server on
	 * @param minConnects
	 *            The minimum number of connections before the {@code Server} actually begins gameplay
	 */
	public static void launchServer(int port, int minConnects) {
	
	}
	
	/**
	 * A Mapping of all the classes contained in each respective Jar file
	 */
	public static Map<File, List<Class<?>>> jarFileListing = new HashMap<File, List<Class<?>>>();
	
	/**
	 * Scans the Jar file and loads every class file within the Jar
	 * 
	 * @param file
	 *            The .jar file
	 */
	public static void scanJarAndLoadAll(File file) {
		if (!file.getName().endsWith(".jar")) {
			return;
		}
		List<Class<?>> list;
		if (!jarFileListing.containsKey(file)) {
			list = new ArrayList<Class<?>>();
			jarFileListing.put(file, list);
		} else {
			list = jarFileListing.get(file);
		}
		try (JarFile jarFile = new JarFile(file)) {
			Enumeration<JarEntry> e = jarFile.entries();
			
			URL[] urls = { new URL("jar:file:" + file + "!/") };
			URLClassLoader cl = URLClassLoader.newInstance(urls, Engine.class.getClassLoader());
			
			while (e.hasMoreElements()) {
				JarEntry je = e.nextElement();
				if (je.isDirectory() || !je.getName().endsWith(".class")) {
					continue;
				}
				// -6 because of .class
				String className = je.getName().substring(0, je.getName().length() - 6);
				className = className.replace('/', '.');
				list.add(cl.loadClass(className));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets a list of all the subclasses of the given superclass within the given Jar file
	 * @param file The .jar file
	 * @param superClass The superclass
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	public static <C> List<Class<? extends C>> getAllAssignable(File file, Class<C> superClass) {
		if (!jarFileListing.containsKey(file)) {
			scanJarAndLoadAll(file);
		}
		List<Class<?>> classes = jarFileListing.get(file);
		List<Class<? extends C>> subClasses = new ArrayList<Class<? extends C>>();
		for (Class<?> cls : classes) {
			if (superClass.isAssignableFrom(cls)) {
				subClasses.add((Class<? extends C>) cls);
			}
		}
		return subClasses;
	}
	
	public static List<Class<? extends Game>> getAllGames(File file) {
		return getAllAssignable(file, Game.class);
	}
	
	/**
	 * Scans the given {@code File}, and opens a {@code JarFile} if the File is of the type. Then scans
	 * through the contents of the {@code JarFile} and loads the class that is a subclass of
	 * {@code superClass}
	 * 
	 * @param <V>
	 *            The class to search for subclasses of
	 * @param file
	 *            The .jar file
	 * @param superClass
	 *            The superclass
	 * @return The class that extends the given superClass
	 * @deprecated Because this method just gets the first result so it's dumb
	 */
	@SuppressWarnings ("unchecked")
	@Deprecated
	public static <V> Class<? extends V> scanJarAndLoad(File file, Class<V> superClass) {
		if (!file.getName().endsWith(".jar")) {
//			throw new IllegalArgumentException("Error opening file");
			return null;
		}
		try (JarFile jarFile = new JarFile(file)) {
			Enumeration<JarEntry> e = jarFile.entries();
			
			URL[] urls = { new URL("jar:file:" + file + "!/") };
			URLClassLoader cl = URLClassLoader.newInstance(urls, Engine.class.getClassLoader());
			
			while (e.hasMoreElements()) {
				JarEntry je = e.nextElement();
				if (je.isDirectory() || !je.getName().endsWith(".class")) {
					continue;
				}
				// -6 because of .class
				String className = je.getName().substring(0, je.getName().length() - 6);
				className = className.replace('/', '.');
				Class<?> c = cl.loadClass(className);
				if (superClass.isAssignableFrom(c)) {
					return ((Class<? extends V>) c);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Constructor<? extends Game> getGameInstance(Class<? extends Game> cls)
			throws NoSuchMethodException, SecurityException {
		return cls.getConstructor();
	}
	
	public static Game getGame(File file) {
		try {
			return getGameInstance(scanJarAndLoad(file, Game.class)).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Constructor<? extends Client> getWindowedConstructor(Class<? extends Client> cls)
			throws NoSuchMethodException, SecurityException {
		return cls.getConstructor(Game.class, int.class, int.class, int.class);
	}
	
	public static Constructor<? extends Client> getFullscreenConstructor(Class<? extends Client> cls)
			throws NoSuchMethodException, SecurityException {
		return cls.getConstructor(Game.class, int.class, int.class);
	}
	
	public static Constructor<? extends Server> getServerConstructor(Class<? extends Server> cls)
			throws NoSuchMethodException, SecurityException {
		return cls.getConstructor(Game.class, int.class, int.class);
	}
	
}
