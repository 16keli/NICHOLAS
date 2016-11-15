package engine.util.math;

import java.io.Serializable;

import engine.geom2d.Vector2;

/**
 * An Immutable implementation of a 3 dimensional vector
 * <p>
 * Why even bother creating a 3d vector when the game engine can only handle 2d stuff? Well, because some math
 * depends on 3d vectors. I'm looking at you, cross products!
 * <p>
 * Creation of an {@code Vector3} can be done in three ways:
 * <p>
 * Using {@link #of(double, double, double) Cartesian} coordinates,
 * {@link #ofCylindrical(double, double, double) Cylindrical}, and {@link #ofSpherical(double, double, double)
 * Spherical) coordinates.
 * <p>
 * Contains values of (x, y, z) in the Cartesian coordinate system, (r, theta) in the Cylindrical coordinate
 * system, and (rho, phi) in the Spherical coordinate system, as well as some helpful methods to manipulate
 * them.
 * <p>
 * The Spherical representation makes use of the American convention, where phi is the angle between the north
 * pole (Z axis) and theta is the angle within the xy plane.
 * <p>
 * All manipulation of {@code Vector3} results in a new {@code Vector3} instance being created. Just like
 * {@link Vector2}.
 * 
 * @author Kevin
 */
public class Vector3 implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -944044178125802434L;
	
	/**
	 * The Cartesian X coordinate
	 */
	public final double x;
	
	/**
	 * The Cartesian Y coordinate
	 */
	public final double y;
	
	/**
	 * The Cartesian / Cylindrical Z coordinate
	 */
	public final double z;
	
	/**
	 * The Cylindrical distance r
	 */
	public final double r;
	
	/**
	 * The Cylindrical / Spherical angle theta
	 */
	public final double theta;
	
	/**
	 * The Spherical distance rho. This is also the magnitude of this {@code Vector3}
	 */
	public final double rho;
	
	/**
	 * The Spherical angle phi
	 */
	public final double phi;
	
	private Vector3(int coordSystem, double val1, double val2, double val3) {
		switch (coordSystem) {
			case 1: // Cartesian (x, y, z)
				this.x = val1;
				this.y = val2;
				this.z = val3;
				this.r = Math.sqrt(x * x + y * y);
				this.theta = Math.atan2(y, x);
				this.rho = Math.sqrt(x * x + y * y + z * z);
				this.phi = Math.acos(z / rho);
				break;
			case 2: // Cylindrical (r, theta, z)
				this.r = val1;
				this.theta = val2;
				this.z = val3;
				this.x = r * Math.cos(theta);
				this.y = r * Math.sin(theta);
				this.rho = Math.sqrt(r * r + z * z);
				this.phi = Math.acos(z / rho);
				break;
			case 3: // Spherical (rho, phi, theta)
				this.rho = val1;
				this.phi = val2;
				this.theta = val3;
				this.z = rho * Math.cos(phi);
				this.r = rho * Math.sin(phi);
				this.x = r * Math.cos(theta);
				this.y = r * Math.sin(theta);
				break;
			default:
				x = 0;
				y = 0;
				z = 0;
				r = 0;
				theta = 0;
				rho = 0;
				phi = 0;
		}
	}
	
	// Object-specific transformation methods
	
	/**
	 * Subtracts {@code subtrahend} from {@code this} to create a new {@code Vector3}
	 * <p>
	 * This method is a convenience method and is functionally equivalent to
	 * 
	 * <pre>
	 * Vector3.subtract(this, subtrahend);
	 * </pre>
	 * 
	 * @param subtrahend
	 *            The subtrahend
	 * @return The difference between {@code this} and {@code subtrahend}
	 */
	public Vector3 minus(Vector3 subtrahend) {
		return Vector3.subtract(this, subtrahend);
	}
	
	/**
	 * Adds {@code addend} to {@code this} to create a new {@code Vector3}
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Vector3.add(this, addend);
	 * </pre>
	 * 
	 * @param addend
	 *            The addend
	 * @return The sum of {@code this} and {@code addend}
	 */
	public Vector3 plus(Vector3 addend) {
		return Vector3.add(this, addend);
	}
	
	/**
	 * Creates a new {@code Vector3} scaled from {@code this}
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Vector3.scale(this, factor);
	 * </pre>
	 * 
	 * @param factor
	 *            The factor to scale {@code this} by
	 * @return A scaled {@code Vector3}
	 */
	public Vector3 scaleVector(double factor) {
		return Vector3.scaleVector(this, factor);
	}
	
	/**
	 * Creates a new {@code Vector3} that is the unit vector for this vector's given angle
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Vector3.unit(this);
	 * </pre>
	 * 
	 * @return A unit {@code Vector3}
	 */
	public Vector3 unit() {
		return Vector3.unit(this);
	}
	
	/**
	 * Computes the dot product of the given multiplicand and multiplier
	 * <p>
	 * By definition, the Dot product is commutative, thus it does not matter which {@code Vector3} is the
	 * {@code multiplicand} and which is the {@code multiplier}
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Vector3.dot(this, multiplier);
	 * </pre>
	 * 
	 * @param multiplier
	 *            The {@code Vector3} to multiply by
	 * @return The dot product of the two {@code Vector3}s
	 */
	public double dot(Vector3 multiplier) {
		return Vector3.dot(this, multiplier);
	}
	
	/**
	 * Computes the angle between the {@code this} and {@code other} using the definitions of the Dot Product
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Vector3.angleBetween(this, other);
	 * </pre>
	 * 
	 * @param other
	 *            The {@code Vector3} to compute the angle with
	 * @return The angle between the two {@code Vector3}s
	 */
	public double angleBetween(Vector3 other) {
		return Vector3.angleBetween(this, other);
	}
	
	/**
	 * Computes the component of {@code this} on {@code on}, also known as the scalar projection
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Vector3.component(this, on);
	 * </pre>
	 * 
	 * @param on
	 *            The {@code Vector3} to use as a reference
	 * @return The component of {@code this} on {@code on}
	 */
	public double component(Vector3 on) {
		return Vector3.component(this, on);
	}
	
	/**
	 * Computes the projection of {@code this} on {@code on}, also known as the vector projection
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Vector3.projection(this, on);
	 * </pre>
	 * 
	 * @param on
	 *            The {@code Vector2} to use as a reference
	 * @return The projection of {@code this} on {@code code}
	 */
	public Vector3 projection(Vector3 on) {
		return Vector3.projection(this, on);
	}
	
	/**
	 * Computes the cross product of the given multiplicand and multiplier
	 * <p>
	 * By definition, the Cross product is anticommutative, thus it does matter which {@code Vector3} is the
	 * {@code multiplicand} and which is the {@code multiplier}
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Vector3.cross(this, multiplier);
	 * </pre>
	 * 
	 * @param multiplier
	 *            The {@code Vector3} to multiply by
	 * @return The cross product of the two {@code Vector3}s
	 */
	public Vector3 cross(Vector3 multiplier) {
		return Vector3.cross(this, multiplier);
	}
	
	// (Kinda) Transformation Methods' static equivalents and mathematical operations
	
	/**
	 * Subtracts the subtrahend from the minuend to create a new {@code Vector3}
	 * <p>
	 * As subtraction is not commutative, it does matter which {@code Vector3} is used as the minuend and
	 * subtrahend
	 * 
	 * @param minuend
	 *            The minuend
	 * @param subtrahend
	 *            The subtrahend
	 * @return The difference between the two {@code Vector3}s
	 */
	public static Vector3 subtract(Vector3 minuend, Vector3 subtrahend) {
		return of(minuend.x - subtrahend.x, minuend.y - subtrahend.y, minuend.z - subtrahend.z);
	}
	
	/**
	 * Adds the {@code Vector3}s together to create a new {@code Vector3}
	 * <p>
	 * By the commutative property, there is really no difference which {@code Vector3} is the augend and
	 * which is the addend.
	 * 
	 * @param augend
	 *            The augend
	 * @param addend
	 *            The addend
	 * @return The sum of the two {@code Vector3}s
	 */
	public static Vector3 add(Vector3 augend, Vector3 addend) {
		return of(augend.x + addend.x, augend.y + addend.y, augend.z + addend.z);
	}
	
	/**
	 * Creates a new {@code Vector3} that is scaled from the existing {@code Vector3}
	 * 
	 * @param source
	 *            The source {@code Vector3}
	 * @param factor
	 *            The factor to scale the source by
	 * @return A scaled {@code Vector3}
	 */
	public static Vector3 scaleVector(Vector3 source, double factor) {
		return of(source.x * factor, source.y * factor, source.z * factor);
	}
	
	/**
	 * Creates a new {@code Vector3} that is the unit vector for the given vector's angle
	 * 
	 * @param source
	 *            The source {@code Vector3}
	 * @return A unit {@code Vector3}
	 */
	public static Vector3 unit(Vector3 source) {
		return ofSpherical(1, source.phi, source.theta);
	}
	
	/**
	 * Computes the dot product of the given multiplicand and multiplier
	 * <p>
	 * By definition, the Dot product is commutative, thus it does not matter which {@code Vector3} is the
	 * {@code multiplicand} and which is the {@code multiplier}
	 * 
	 * @param multiplicand
	 *            The {@code Vector2} to multiply
	 * @param multiplier
	 *            The {@code Vector2} to multiply by
	 * @return The dot product of the two {@code Vector3}s
	 */
	public static double dot(Vector3 multiplicand, Vector3 multiplier) {
		return multiplicand.x * multiplier.x + multiplicand.y * multiplier.y + multiplicand.z * multiplier.z;
	}
	
	/**
	 * Computes the angle between the two {@code Vector3}s using the definitions of the Dot Product
	 * 
	 * @param vector1
	 *            The first {@code Vector3}
	 * @param vector2
	 *            The second {@code Vector3}
	 * @return The angle between the two {@code Vector3}s
	 */
	public static double angleBetween(Vector3 vector1, Vector3 vector2) {
		return Math.acos(Vector3.dot(vector1, vector2) / (vector1.rho * vector2.rho));
	}
	
	/**
	 * Computes the component of {@code of} on {@code on}, also known as the scalar projection
	 * <p>
	 * If the component of {@code of} would be larger than {@code on}, the component is still returned.
	 * 
	 * @param of
	 *            The {@code Vector3} to compute the component of
	 * @param on
	 *            The {@code Vector3} to use as a reference
	 * @return The component of {@code of} on {@code on}
	 */
	public static double component(Vector3 of, Vector3 on) {
		return Vector3.dot(of, on) / on.rho;
	}
	
	/**
	 * Computes the projection of {@code of} on {@code on}, also known as the vector projection
	 * 
	 * @param of
	 *            The {@code Vector3} to compute the projection of
	 * @param on
	 *            The {@code Vector3} to use as a reference
	 * @return The projection of {@code of} on {@code code}
	 */
	public static Vector3 projection(Vector3 of, Vector3 on) {
		return Vector3.scaleVector(on.unit(), component(of, on));
	}
	
	/**
	 * Computes the cross product of the given multiplicand and multiplier
	 * <p>
	 * By definition, the Cross product is anticommutative, thus it does matter which {@code Vector3} is the
	 * {@code multiplicand} and which is the {@code multiplier}
	 * 
	 * @param multiplicand
	 *            The {@code Vector3} to multiply
	 * @param multiplier
	 *            The {@code Vector3} to multiply by
	 * @return The cross product of the two {@code Vector3}s
	 */
	public static Vector3 cross(Vector3 multiplicand, Vector3 mulitplier) {
		return of(multiplicand.y * mulitplier.z - multiplicand.z * mulitplier.y,
				multiplicand.z * mulitplier.x - multiplicand.x * mulitplier.z,
				multiplicand.x * mulitplier.y - multiplicand.y * mulitplier.x);
	}
	
	// Static constructor methods
	
	/**
	 * Creates a new {@code Vector3} based on the given Cartesian coordinates
	 * 
	 * @param x
	 *            The X Coordinate
	 * @param y
	 *            The Y Coordinate
	 * @param z
	 *            The Z Coordinate
	 * @return A new {@code Vector3}
	 */
	public static Vector3 of(double x, double y, double z) {
		return new Vector3(1, x, y, z);
	}
	
	/**
	 * Creates a new {@code Vector3} based on the given Cylindrical coordinates
	 * 
	 * @param r
	 *            The distance r
	 * @param theta
	 *            The angle theta
	 * @param z
	 *            The height Z
	 * @return A new {@code Vector3}
	 */
	public static Vector3 ofCylindrical(double r, double theta, double z) {
		return new Vector3(2, r, theta, z);
	}
	
	/**
	 * Creates a new {@code Vector3} based on the given Spherical coordinates
	 * 
	 * @param rho
	 *            The distance rho
	 * @param phi
	 *            The angle phi
	 * @param theta
	 *            The angle theta
	 * @return A new {@code Vector3}
	 */
	public static Vector3 ofSpherical(double rho, double phi, double theta) {
		return new Vector3(3, rho, phi, theta);
	}
	
	/**
	 * Converts the given {@code Vector2} into a {@code Vector3} with the given Z coordinate
	 * 
	 * @param source
	 *            The {@code Vector2} source
	 * @param z
	 *            The Z coordinate
	 * @return The {@code Vector3} from this {@code Vector2}
	 */
	public static Vector3 convert(Vector2 source, double z) {
		return of(source.getX(), source.getY(), z);
	}
	
	/**
	 * Converts the given {@code Vector2} into a {@code Vector3} with a Z coordinate of 0
	 * 
	 * @param source
	 *            The {@code Vector2} source
	 * @return The {@code Vector3} from this {@code Vector2}
	 */
	public static Vector3 convert(Vector2 source) {
		return convert(source, 0);
	}
	
}
