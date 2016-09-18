package engine.geom2d;

/**
 * A utility class that facilitates {@code Tuple2} math operations by providing a mutable instance to operate
 * on, thus removing the need to allocate new memory as much as by other mathematical operations
 * 
 * @author Kevin
 */
public class Tuple2Mutable extends Tuple2 {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6119348522080815597L;
	
	public static final Tuple2Mutable ZERO = new Tuple2Mutable();
	
	protected Tuple2Mutable() {
		super();
	}
	
	protected Tuple2Mutable(double x, double y) {
		super(x, y);
	}
	
	protected Tuple2Mutable(Tuple2 base) {
		super(base);
	}
	
	@Override
	public Tuple2 addImmutable(Tuple2 addend) {
		return this.add(addend);
	}
	
	@Override
	public Tuple2 subtractImmutable(Tuple2 subtrahend) {
		return this.subtract(subtrahend);
	}
	
	@Override
	public Tuple2 scaleImmutable(double scale) {
		return this.scale(scale);
	}
	
	public static Tuple2Mutable getMutable() {
		return Tuple2Mutable.getMutable(ZERO);
	}
	
	public static Tuple2Mutable getMutable(Tuple2 base) {
		return new Tuple2Mutable(base);
	}
}
