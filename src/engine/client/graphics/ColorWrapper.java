package engine.client.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * A simple wrapper class that gets rid of all the obscurity of creating new {@code java.awt.Color} instances
 * <p>
 * Also provides handy static methods having to deal with Color. Also see
 * {@link engine.client.graphics.Screen}
 * 
 * @author Kevin
 */
//TrENtON Is the best person ever.
//<3
public class ColorWrapper {
	
	public static final int COL_16 = get(16, 16, 16);
	
	public static final int COL_48 = get(48, 48, 48);
	
	public static final int COL_80 = get(80, 80, 80);
	
	public static final int COL_112 = get(112, 112, 112);
	
	public static final int COL_144 = get(144, 144, 144);
	
	public static final int COL_176 = get(176, 176, 176);
	
	public static final int COL_208 = get(208, 208, 208);
	
	public static final int COL_240 = get(240, 240, 240);
	
	/**
	 * The default array of colors that will be replaced. There are 4 different shades, thus 4 different
	 * possible colors
	 * <p>
	 * The values are grayscale (thus have equal R, G, and B values). They are:
	 * <p>
	 * <b>0:</b> 16 (Quite Black)
	 * <p>
	 * <b>1:</b> 80 (Dark Grey)
	 * <p>
	 * <b>2:</b> 176 (Light Grey)
	 * <p>
	 * <b>3:</b> 240 (Quite White)
	 */
	public static final int[] DEFAULT_FOUR = { COL_16, COL_80, COL_176, COL_240 };
	
	/**
	 * The default array of colors that will be replaced. There are 4 different shades, thus 4 different
	 * possible colors
	 * <p>
	 * The values are grayscale (thus have equal R, G, and B values). They are:
	 * <p>
	 * <b>0:</b> 16 (Quite Black)
	 * <p>
	 * <b>1:</b> 48 (Dark Grey)
	 * <p>
	 * <b>2:</b> 80 (Dark Grey)
	 * <p>
	 * <b>3:</b> 112 (Grey)
	 * <p>
	 * <b>4:</b> 144 (Grey)
	 * <p>
	 * <b>5:</b> 176 (Light Grey)
	 * <p>
	 * <b>6:</b> 208 (Light Grey)
	 * <p>
	 * <b>7:</b> 240 (Quite White)
	 */
	public static final int[] DEFAULT_EIGHT = { COL_16, COL_48, COL_80, COL_112, COL_144, COL_176, COL_208, COL_240 };
	
	/**
	 * The default array of colors that will be replaced. Can be set with
	 */
	public static int[] DEFAULT = DEFAULT_EIGHT;
	
	/**
	 * Gets the {@code int} representation of the given RGB color code
	 * 
	 * @param r
	 *            The red component
	 * @param g
	 *            The green component
	 * @param b
	 *            The blue component
	 * @return The {@code int} representation of the given RGB color code
	 */
	public static int get(int r, int g, int b) {
		return new Color(r, g, b).getRGB();
	}
	
	/**
	 * Gets the {@code int} representation of the given RGB color code
	 * 
	 * @param r
	 *            The red component
	 * @param g
	 *            The green component
	 * @param b
	 *            The blue component
	 * @param a
	 *            The alpha component (Opacity)
	 * @return The {@code int} representation of the given RGB color code
	 */
	public static int get(int r, int g, int b, int a) {
		return new Color(r, g, b, a).getRGB();
	}
	
	/**
	 * Replaces the colors in a given {@code File} with the default colors
	 * 
	 * @param f
	 *            The {@code File} in question
	 * @param source
	 *            The colors to search for
	 */
	public static void replaceColors(File f, int[] source) {
		try {
			BufferedImage img = ImageIO.read(f);
			img = checkARGB(img);
			File output = new File(f.getPath() + "color_adj.png");
			if (!output.exists()) {
				output.createNewFile();
			}
			System.out.println(img.getType());
			Screen.colorAdjust(img, source, DEFAULT);
//			Screen.colorAdjust(img, source);
			ImageIO.write(img, "png", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Scans the colors in a given {@code File}
	 * 
	 * @param f
	 *            The {@code File}
	 */
	public static void scanColors(File f) {
		List<Integer> rgbs = new ArrayList<Integer>();
		try {
			BufferedImage img = ImageIO.read(f);
			for (int x = 0; x < img.getWidth(); x++) {
				for (int y = 0; y < img.getHeight(); y++) {
					if (!rgbs.contains(img.getRGB(x, y))) {
						rgbs.add(img.getRGB(x, y));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Integer i : rgbs) {
			Color c = new Color(i);
			System.out.println(c.getRed() + " " + c.getGreen() + " " + c.getBlue());
		}
	}
	
	/**
	 * Sets the default color replacement array
	 * 
	 * @param def
	 */
	public static void setDefault(int[] def) {
		DEFAULT = def;
	}
	
	/**
	 * Checks to see whether a given {@code BufferedImage} is of type ARGB, and if not, makes it so.
	 * <p>
	 * Be sure to set the returned value of this function to the {@code BufferedImage} in case the given
	 * parameter was not of type {@link java.awt.image.BufferedImage#TYPE_INT_ARGB TYPE_INT_ARGB}
	 * 
	 * @param img
	 *            The {@code BufferedImage} to check
	 * @return The {@code BufferedImage}, in {@code TYPE_INT_ARGB} form if necessary
	 */
	public static BufferedImage checkARGB(BufferedImage img) {
		if (img.getType() == BufferedImage.TYPE_INT_ARGB) {
			return img;
		} else {
			BufferedImage bi = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.drawImage(img, 0, 0, null);
			g.dispose();
			return bi;
		}
	}
	
	/**
	 * Displays the given image
	 * 
	 * @param img
	 */
	public static void displayImage(Image img) {
		JOptionPane.showMessageDialog(null, null, "Image", JOptionPane.YES_NO_OPTION, new ImageIcon(img));
	}
	
	public static void main(String[] args) {
		/*
		 * int[] colors = { get(135, 7, 0), get(159, 0, 28), get(199, 7, 0), get(231, 79, 23), get(236, 0,
		 * 20), get(255, 77, 0), get(255, 151, 63), get(255, 183, 87) }; // setDefault(DEFAULT_EIGHT);
		 * JFileChooser fc = new JFileChooser(new File("res")); fc.showOpenDialog(null);
		 * replaceColors(fc.getSelectedFile(), colors); // scanColors(fc.getSelectedFile());
		 */
		
	}
	
}
