package engine.level;

import java.io.Serializable;

/**
 * An Immutable <strike>(in principle)</strike> <b>for good now</b> class representing a 2 dimensional vector
 * <p>
 * Creation of an {@code Vector2} can be done in two ways:
 * <p>
 * Using {@link #of(double, double) Cartesian} coordinates, and {@link #ofPolar(double, double) Polar}
 * coordinates.
 * <p>
 * Contains values for both (x, y) in the Cartesian coordinate system, and (r, theta) in the Polar coordinate
 * system, as well as some helpful methods to manipulate them.
 * <p>
 * All manipulation of {@code Vector2} results in a new {@code Vector2} instance being created. I'm still
 * unsure whether or not this design is the best, but I'm working on it.
 * 
 * @author Kevin
 */
public class Vector2 implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * A Zero {@code Vector2}
	 */
	public static final Vector2 ZERO = new Vector2();
	
	/**
	 * The X Component of the {@code Vector2}
	 */
	public final double x;
	
	/**
	 * The Y Component of the {@code Vector2}
	 */
	public final double y;
	
	/**
	 * The angle of the {@code Vector2}, between -pi and pi
	 */
	public final double angle;
	
	/**
	 * The magnitude of the {@code Vector2}
	 */
	public final double magnitude;
	
	private Vector2() {
		this.x = 0;
		this.y = 0;
		this.magnitude = 0;
		this.angle = 0;
	}
	
	/**
	 * A private constructor that creates a new {@code Vector2}
	 * <p>
	 * There are two ways that this constructor is used, for the sake of (slight) efficiency boosts
	 * <p>
	 * Of course, this is not publicly visible. Facade pattern, anyone?
	 * 
	 * @param a1
	 *            X in Cartesian mode, angle in Polar mode
	 * @param a2
	 *            Y in Cartesian mode, magnitude in Polar mode
	 * @param cart
	 *            Whether the mode is Cartesian or Polar
	 */
	private Vector2(double a1, double a2, boolean cart) {
		if (cart) {
			this.x = a1;
			this.y = a2;
			this.magnitude = Math.sqrt(a1 * a1 + a2 * a2);
			this.angle = Math.atan2(a2, a1);
		} else {
			this.angle = a1;
			this.magnitude = a2;
			this.x = Math.cos(a1) * a2;
			this.y = Math.sin(a1) * a2;
		}
	}
	
	/**
	 * Subtracts {@code subtrahend} from {@code this} to create a new {@code Vector2}
	 * <p>
	 * This method is a convenience method and is functionally equivalent to
	 * 
	 * <pre>
	 * Vector2.subtract(this, subtrahend)
	 * </pre>
	 * 
	 * @param subtrahend
	 *            The subtrahend
	 * @return The difference between {@code this} and {@code subtrahend}
	 */
	public Vector2 minus(Vector2 subtrahend) {
		return Vector2.subtract(this, subtrahend);
	}
	
	/**
	 * Adds {@code addend} to {@code this} to create a new {@code Vector2}
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Vector2.add(this, addend);
	 * </pre>
	 * 
	 * @param addend
	 *            The addend
	 * @return The sum of {@code this} and {@code addend}
	 */
	public Vector2 plus(Vector2 addend) {
		return Vector2.add(this, addend);
	}
	
	/**
	 * Calculates the displacement between {@code this} and {@code other}
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Vector2.displacement(this, other);
	 * </pre>
	 * 
	 * @param other
	 *            The {@code Vector2} to calculate displacement from
	 * @return The displacement between {@code this} and {@code other}
	 */
	public double displacement(Vector2 other) {
		return Vector2.displacement(this, other);
	}
	
	/**
	 * Creates a new {@code Vector2} scaled from {@code this}
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Vector2.scale(this, factor)
	 * </pre>
	 * 
	 * @param factor
	 *            The factor to scale {@code this} by
	 * @return A scaled {@code Vector2}
	 */
	public Vector2 scale(double factor) {
		return Vector2.scale(this, factor);
	}
	
	/**
	 * Subtracts the subtrahend from the minuend to create a new {@code Vector2}
	 * <p>
	 * As subtraction is not commutative, it does matter which {@code Vector2} is used as the minuend and
	 * subtrahend
	 * 
	 * @param minuend
	 *            The minuend
	 * @param subtrahend
	 *            The subtrahend
	 * @return The difference between the two {@code Vector2}s
	 */
	public static Vector2 subtract(Vector2 minuend, Vector2 subtrahend) {
		return of(minuend.x - subtrahend.x, minuend.y - subtrahend.y);
	}
	
	/**
	 * Adds the {@code Vector2}s together to create a new {@code Vector2}
	 * <p>
	 * By the commutative property, there is really no difference which {@code Vector2} is the augend and
	 * which is the addend.
	 * 
	 * @param augend
	 *            The augend
	 * @param addend
	 *            The addend
	 * @return The sum of the two {@code Vector2}s
	 */
	public static Vector2 add(Vector2 augend, Vector2 addend) {
		return of(augend.x + addend.x, augend.y + addend.y);
	}
	
	/**
	 * Calculates the displacement between the two {@code Vector2}s
	 * 
	 * @param vector1
	 *            The first {@code Vector2}
	 * @param vector2
	 *            The second {@code Vector2}
	 * @return The displacement between the two
	 */
	public static double displacement(Vector2 vector1, Vector2 vector2) {
		Vector2 diff = subtract(vector1, vector2);
		return Math.sqrt(Math.pow(diff.x, 2) + Math.pow(diff.y, 2));
	}
	
	/**
	 * Creates a new {@code Vector2} that is scaled from the existing {@code Vector2}
	 * 
	 * @param source
	 *            The source {@code Vector2}
	 * @param factor
	 *            The factor to scale the source by
	 * @return A scaled {@code Vector2}
	 */
	public static Vector2 scale(Vector2 source, double factor) {
		return of(source.x * factor, source.y * factor);
	}
	
	/**
	 * Creates a new {@code Vector2} based on the given Cartesian coordinates
	 * <p>
	 * For use of Polar coordinates, use {@link #ofPolar(double, double)}
	 * 
	 * @param x
	 *            The X component of the {@code Vector2}
	 * @param y
	 *            The Y component of the {@code Vector2}
	 * @return A new {@code Vector2}
	 */
	public static Vector2 of(double x, double y) {
		return new Vector2(x, y, true);
	}
	
	/**
	 * Creates a new {@code Vector2} based on the given Polar coordinates
	 * <p>
	 * For use of Cartesian coordinates, use {@link #of(double, double)}
	 * 
	 * @param angle
	 *            The angle of the {@code Vector2} in Radians
	 * @param magnitude
	 *            The magnitude of the {@code Vector2}
	 * @return A new {@code Vector2}
	 */
	public static Vector2 ofPolar(double angle, double magnitude) {
		return new Vector2(angle, magnitude, false);
	}
	
	@Override
	public String toString() {
		return this.getCartesian();
	}
	
	/**
	 * Returns the String representation of this {@code Vector2} in the Cartesian coordinate system
	 * 
	 * @return
	 */
	public String getCartesian() {
		return "(" + this.x + ", " + this.y + ")";
	}
	
	/**
	 * Returns the String representation of this {@code Vector2} in the Polar coordinate system
	 * 
	 * @return
	 */
	public String getPolar() {
		return "[" + this.magnitude + ", " + this.angle + "]";
	}
	
	/**
	 * Returns a new copy of this {@code Vector2}
	 */
	@Override
	public Vector2 clone() {
		return of(this.x, this.y);
	}
	
}
