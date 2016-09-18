package engine.geom2d;

import java.io.Serializable;

/**
 * A semi-mutable implementation of a 2 element-tuple represented by double precision floating point numbers
 * <p>
 * By semi-mutable, what I mean is that methods have a mutable and immutable counterpart, which can choose to
 * return either a new instance or transform the old one
 * 
 * @author Kevin
 */
public abstract class Tuple2 implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3727829010660698728L;
	
	/**
	 * The X element
	 */
	protected double x;
	
	/**
	 * The Y element
	 */
	protected double y;
	
	/**
	 * Initializes a new {@code Tuple2} to (0, 0)
	 */
	protected Tuple2() {
		this(0, 0);
	}
	
	/**
	 * Initializes a new {@code Tuple2} with the given elements
	 * 
	 * @param x
	 *            The X element
	 * @param y
	 *            The Y element
	 */
	protected Tuple2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Initializes the given {@code Tuple2} with parameters based on the ones given in {@code base}
	 * 
	 * @param base
	 *            The {@code Tuple2} to base the parameters on
	 */
	protected Tuple2(Tuple2 base) {
		this(base.x, base.y);
	}
	
	// Transformation methods
	
	/**
	 * Adds the given {@code Tuple2} to {@code this}
	 * <p>
	 * This method has the effect of {@code this += addend}
	 * 
	 * @param addend
	 *            The {@code Tuple2} to add
	 * @return {@code this} to allow for easy chaining of operations
	 */
	public Tuple2 add(Tuple2 addend) {
		this.x += addend.x;
		this.y += addend.y;
		return this;
	}
	
	/**
	 * Subtracts the given {@code Tuple2} from {@code this}
	 * <p>
	 * This method has the effect of {@code this -= subtrahend}
	 * 
	 * @param subtrahend
	 *            The {@code Tuple2} to subtract
	 * @return {@code this} to allow for easy chaining of operations
	 */
	public Tuple2 subtract(Tuple2 subtrahend) {
		this.x -= subtrahend.x;
		this.y -= subtrahend.y;
		return this;
	}
	
	/**
	 * Scales this {@code Tuple2} by the given {@code factor}
	 * <p>
	 * This method has the effect of {@code this *= scale}
	 * 
	 * @param scale
	 *            The factor to scale by
	 * @return {@code this} to allow for easy chaining of operations
	 */
	public Tuple2 scale(double scale) {
		this.x *= scale;
		this.y *= scale;
		return this;
	}
	
	// Immutable "transformation" methods
	
	/**
	 * Adds the given {@code Tuple2} to {@code this} and returns a new instance
	 * <p>
	 * This method has the effect of {@code return this + addend}
	 * 
	 * @param addend
	 *            The {@code Tuple2} to add
	 * @return A new {@code Tuple2} instance
	 */
	public abstract Tuple2 addImmutable(Tuple2 addend);
	
	/**
	 * Subtracts the given {@code Tuple2} from {@code this} and returns a new instance
	 * <p>
	 * This method has the effect of {@code return this - subtrahend}
	 * 
	 * @param subtrahend
	 *            The {@code Tuple2} to subtract
	 * @return A new {@code Tuple2} instance
	 */
	public abstract Tuple2 subtractImmutable(Tuple2 subtrahend);
	
	/**
	 * Scales this {@code Tuple2} by the given {@code factor} and returns a new instance
	 * <p>
	 * This method has the effect of {@code return this * scale}
	 * 
	 * @param scale
	 *            The factor to scale by
	 * @return A new {@code Tuple2} instance
	 */
	public abstract Tuple2 scaleImmutable(double scale);
	
	// Other methods
	
	/**
	 * Transforms this {@code Tuple2} into a {@code Point2}
	 * 
	 * @return A new {@code Point2} based off this {@code Tuple2}'s elements
	 */
	public Point2 toPoint() {
		return Point2.of(this.x, this.y);
	}
	
	/**
	 * Transforms this {@code Tuple2} into a {@code Vector2}
	 * 
	 * @return A new {@code Vector2} based off this {@code Tuple2}'s elements
	 */
	public Vector2 toVector() {
		return Vector2.of(this.x, this.y);
	}
	
	/**
	 * Retrieves this {@code Tuple2}'s X element
	 * 
	 * @return The X
	 */
	public double getX() {
		return this.x;
	}
	
	/**
	 * Retrieves this {@code Tuple2}'s Y element
	 * 
	 * @return
	 */
	public double getY() {
		return this.y;
	}
}
