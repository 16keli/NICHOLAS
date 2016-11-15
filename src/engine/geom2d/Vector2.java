package engine.geom2d;

/**
 * An Immutable implementation class of {@code Tuple2} representing a 2 dimensional vector
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
public class Vector2 extends Tuple2 {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8639485492109982709L;
	
	/**
	 * A Zero {@code Vector2}
	 */
	public static final Vector2 ZERO = new Vector2();
	
	/**
	 * The angle of the {@code Vector2}, between -pi and pi
	 */
	protected final double angle;
	
	/**
	 * The magnitude of the {@code Vector2}
	 */
	protected final double magnitude;
	
	private Vector2() {
		super();
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
	
	// Overridden Tuple2 operations
	
	@Override
	public Tuple2 add(Tuple2 addend) {
		return addImmutable(addend);
	}
	
	@Override
	public Tuple2 subtract(Tuple2 subtrahend) {
		return subtractImmutable(subtrahend);
	}
	
	@Override
	public Tuple2 scale(double scale) {
		return scaleImmutable(scale);
	}
	
	@Override
	public Tuple2 addImmutable(Tuple2 addend) {
		return of(this.x + addend.x, this.y + addend.y);
	}
	
	@Override
	public Tuple2 subtractImmutable(Tuple2 subtrahend) {
		return of(this.x - subtrahend.x, this.y - subtrahend.y);
	}
	
	@Override
	public Tuple2 scaleImmutable(double scale) {
		return of(this.x * scale, this.y * scale);
	}
	
	// Vector-specific (Kinda) Transformation methods (Kinda because Vector2 is immutable)
	
	/**
	 * Subtracts {@code subtrahend} from {@code this} to create a new {@code Vector2}
	 * <p>
	 * This method is a convenience method and is functionally equivalent to
	 * 
	 * <pre>
	 * Vector2.subtract(this, subtrahend);
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
	 * Creates a new {@code Vector2} scaled from {@code this}
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Vector2.scale(this, factor);
	 * </pre>
	 * 
	 * @param factor
	 *            The factor to scale {@code this} by
	 * @return A scaled {@code Vector2}
	 */
	public Vector2 scaleVector(double factor) {
		return Vector2.scaleVector(this, factor);
	}
	
	/**
	 * Creates a new {@code Vector2} that is the unit vector for this vector's given angle
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Vector2.unit(this);
	 * </pre>
	 * 
	 * @return A unit {@code Vector2}
	 */
	public Vector2 unit() {
		return Vector2.unit(this);
	}
	
	/**
	 * Computes the dot product of the given multiplicand and multiplier
	 * <p>
	 * By definition, the Dot product is commutative, thus it does not matter which {@code Vector2} is the
	 * {@code multiplicand} and which is the {@code multiplier}
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Vector2.dot(this, multiplier);
	 * </pre>
	 * 
	 * @param multiplier
	 *            The {@code Vector2} to multiply by
	 * @return The dot product of the two {@code Vector2}s
	 */
	public double dot(Vector2 multiplier) {
		return Vector2.dot(this, multiplier);
	}
	
	/**
	 * Computes the angle between the {@code this} and {@code other} using the definitions of the Dot Product
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Vector2.angleBetween(this, other);
	 * </pre>
	 * 
	 * @param other
	 *            The {@code Vector2} to compute the angle with
	 * @return The angle between the two {@code Vector2}s
	 */
	public double angleBetween(Vector2 other) {
		return Vector2.angleBetween(this, other);
	}
	
	/**
	 * Computes the component of {@code this} on {@code on}, also known as the scalar projection
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Vector2.component(this, on);
	 * </pre>
	 * 
	 * @param on
	 *            The {@code Vector2} to use as a reference
	 * @return The component of {@code this} on {@code on}
	 */
	public double component(Vector2 on) {
		return Vector2.component(this, on);
	}
	
	/**
	 * Computes the projection of {@code this} on {@code on}, also known as the vector projection
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Vector2.projection(this, on);
	 * </pre>
	 * 
	 * @param on
	 *            The {@code Vector2} to use as a reference
	 * @return The projection of {@code this} on {@code code}
	 */
	public Vector2 projection(Vector2 on) {
		return Vector2.projection(this, on);
	}
	
	// (Kinda) Transformation Methods' static equivalents and mathematical operations
	
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
	 * Creates a new {@code Vector2} that is scaled from the existing {@code Vector2}
	 * 
	 * @param source
	 *            The source {@code Vector2}
	 * @param factor
	 *            The factor to scale the source by
	 * @return A scaled {@code Vector2}
	 */
	public static Vector2 scaleVector(Vector2 source, double factor) {
		return of(source.x * factor, source.y * factor);
	}
	
	/**
	 * Creates a new {@code Vector2} that is the unit vector for the given vector's angle
	 * 
	 * @param source
	 *            The source {@code Vector2}
	 * @return A unit {@code Vector2}
	 */
	public static Vector2 unit(Vector2 source) {
		return ofPolar(source.angle, 1);
	}
	
	/**
	 * Computes the dot product of the given multiplicand and multiplier
	 * <p>
	 * By definition, the Dot product is commutative, thus it does not matter which {@code Vector2} is the
	 * {@code multiplicand} and which is the {@code multiplier}
	 * 
	 * @param multiplicand
	 *            The {@code Vector2} to multiply
	 * @param multiplier
	 *            The {@code Vector2} to multiply by
	 * @return The dot product of the two {@code Vector2}s
	 */
	public static double dot(Vector2 multiplicand, Vector2 multiplier) {
		return multiplicand.x * multiplier.x + multiplicand.y * multiplier.y;
	}
	
	/**
	 * Computes the angle between the two {@code Vector2}s using the definitions of the Dot Product
	 * 
	 * @param vector1
	 *            The first {@code Vector2}
	 * @param vector2
	 *            The second {@code Vector2}
	 * @return The angle between the two {@code Vector2}s
	 */
	public static double angleBetween(Vector2 vector1, Vector2 vector2) {
		return Math.acos(Vector2.dot(vector1, vector2) / (vector1.magnitude * vector2.magnitude));
	}
	
	/**
	 * Computes the component of {@code of} on {@code on}, also known as the scalar projection
	 * <p>
	 * If the component of {@code of} would be larger than {@code on}, the component is still returned.
	 * 
	 * @param of
	 *            The {@code Vector2} to compute the component of
	 * @param on
	 *            The {@code Vector2} to use as a reference
	 * @return The component of {@code of} on {@code on}
	 */
	public static double component(Vector2 of, Vector2 on) {
		return Vector2.dot(of, on) / on.magnitude;
	}
	
	/**
	 * Computes the projection of {@code of} on {@code on}, also known as the vector projection
	 * 
	 * @param of
	 *            The {@code Vector2} to compute the projection of
	 * @param on
	 *            The {@code Vector2} to use as a reference
	 * @return The projection of {@code of} on {@code code}
	 */
	public static Vector2 projection(Vector2 of, Vector2 on) {
		return Vector2.scaleVector(on.unit(), component(of, on));
	}
	
	// Static constructor methods because seeing new Vector2() everywhere is tedious, and the boolean in the
	// constructor is annoying so it is prime for facading it away
	
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
	
	// Other utility methods
	
	/**
	 * Retrieves this {@code Vector2}'s magnitude
	 * 
	 * @return
	 */
	public double getMagnitude() {
		return this.magnitude;
	}
	
	/**
	 * Retrieves this {@code Vector2}'s angle
	 * 
	 * @return
	 */
	public double getAngle() {
		return this.angle;
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
