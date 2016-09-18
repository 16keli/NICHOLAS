package engine.geom2d;

/**
 * An immutable representation of a point in 2D space
 * 
 * @author Kevin
 */
public class Point2 extends Tuple2 {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -110064119166492271L;
	
	public static final Point2 ORIGIN = new Point2();
	
	/**
	 * Constructs the origin point
	 */
	protected Point2() {
		super();
	}
	
	/**
	 * Constructs a new {@code Point2} at the given coordinates
	 * 
	 * @param x
	 * @param y
	 */
	protected Point2(double x, double y) {
		super(x, y);
	}
	
	// Point-specific operations
	
	/**
	 * Calculates the displacement between {@code this} and {@code other}
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Point2.displacement(this, other);
	 * </pre>
	 * 
	 * @param other
	 *            The {@code Vector2} to calculate displacement from
	 * @return The displacement between {@code this} and {@code other}
	 */
	public double displacement(Point2 other) {
		return Point2.displacement(this, other);
	}
	
	/**
	 * Calculates the {@code Vector2} with {@code this} as the tail and {@code other} as the head, and returns
	 * it
	 * <p>
	 * This method is a convenience method and is functionally equivalent to calling
	 * 
	 * <pre>
	 * Point2.vectorBetween(this, other);
	 * </pre>
	 * 
	 * @param other
	 *            The Head of the {@code Vector2}
	 * @return The {@code Vector2} between the two points
	 */
	public Vector2 vectorBetween(Point2 other) {
		return Point2.vectorBetween(this, other);
	}
	
	/**
	 * Calculates the displacement between the two {@code Vector2}s
	 * 
	 * @param point1
	 *            The first {@code Point2}
	 * @param point2
	 *            The second {@code Point2}
	 * @return The displacement between the two
	 */
	public static double displacement(Point2 point1, Point2 point2) {
		Tuple2 diff = point1.subtractImmutable(point2);
		return Math.sqrt(Math.pow(diff.x, 2) + Math.pow(diff.y, 2));
	}
	
	/**
	 * Calculates the {@code Vector2} with {@code origin} as the tail and {@code point} as the head, and
	 * returns it
	 * 
	 * @param origin
	 *            The Tail of the {@code Vector2}
	 * @param point
	 *            The Head of the {@code Vector2}
	 * @return The {@code Vector2} between the two points
	 */
	public static Vector2 vectorBetween(Point2 origin, Point2 point) {
		return Vector2.of(point.x - origin.x, point.y - origin.y);
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
	
	// Static constructor methods
	
	/**
	 * Creates a new {@code Point2} based on the given Cartesian coordinates
	 * <p>
	 * For use of Polar coordinates, use {@link #ofPolar(double, double)}
	 * 
	 * @param x
	 *            The X component of the {@code Point2}
	 * @param y
	 *            The Y component of the {@code Point2}
	 * @return A new {@code Point2}
	 */
	public static Point2 of(double x, double y) {
		return new Point2(x, y);
	}
	
	/**
	 * Creates a new {@code Point} based on the given Polar coordinates
	 * <p>
	 * For use of Cartesian coordinates, use {@link #of(double, double)}
	 * 
	 * @param angle
	 *            The angle of the {@code Point2} in Radians
	 * @param magnitude
	 *            The magnitude of the {@code Point2}
	 * @return A new {@code Point2}
	 */
	public static Point2 ofPolar(double angle, double magnitude) {
		return new Point2(Math.cos(angle) * magnitude, Math.sin(angle) * magnitude);
	}
	
	// Other methods
	
	/**
	 * Clones the given {@code Point2}
	 */
	public Point2 clone() {
		return of(this.x, this.y);
	}
}
