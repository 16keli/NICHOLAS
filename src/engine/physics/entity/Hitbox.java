package engine.physics.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import engine.client.graphics.sprite.ISpriteProvider;
import engine.geom2d.Vector2;

/**
 * The Hitbox of an {@code EntityPhysics} used in collision detection and determination
 * 
 * @author Kevin
 */
public abstract class Hitbox implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The radius of a circle that surrounds this {@code Hitbox}. If the distance between two {@code Hitbox}es
	 * are less than the sum of these circles, then we should evaluate the two {@code EntityPhysics} for
	 * collisions. This should take a lot of burden off the CPU by not evaluating collisions that would never
	 * happen.
	 */
	public double circleRadius;
	
	/**
	 * Creates a new {@code Hitbox} with the given circle radius parameter
	 * 
	 * @param circleRadius
	 */
	public Hitbox(double circleRadius) {
		this.circleRadius = circleRadius;
	}
	
	/**
	 * Clamps the given angle into the range of [0, 2pi)
	 * 
	 * @param angle
	 *            The angle to clamp
	 * @return The angle in the given range
	 */
	public static double clampAngle(double angle) {
		angle %= 2 * Math.PI;
		if (angle < 0) {
			angle += 2 * Math.PI;
		}
		return angle;
	}
	
	/**
	 * Clamps the given angle into the range of [0, pi)
	 * 
	 * @param angle
	 *            The angle to clamp
	 * @return The angle in the given range
	 */
	public static double clampAngle2(double angle) {
		angle %= Math.PI;
		if (angle < 0) {
			angle += Math.PI;
		}
		return angle;
	}
	
	/**
	 * Gets the {@code Vector2} of the furthest point of this hitbox based on the angle
	 * 
	 * @param angle
	 *            The angle in Radians. Values will automatically get clamped into the range [0, 2pi)
	 * @return
	 */
	public Vector2 getFurthestAtAngle(double angle) {
		return this.getHitboxVectorAtAngle(clampAngle(angle)).plus(this.getCenterDisplacement());
	}
	
	/**
	 * Gets the angle of the surface of the {@code Hitbox} at the given angle
	 * 
	 * @param angle
	 *            The angle in Radians. Values will automatically get clamped into the range [0, 2pi)
	 * @return
	 */
	public double getTangentAtAngle(double angle) {
		return this.getHitboxTangentAtAngle(clampAngle(angle));
	}
	
	/**
	 * Gets the gradient of the {@code Hitbox} surface at the given angle
	 * 
	 * @param angle
	 *            The angle in Radians. Values will automatically get clamped into the range [0, 2pi)
	 * @return
	 */
	public double getGradientAtAngle(double angle) {
		return this.getHitboxGradientAtAngle(clampAngle(angle));
	}
	
	/**
	 * Should get the {@code Vector2} representing the furthest reach of the {@code Hitbox} at a given angle
	 * <p>
	 * This should be in relation to the center of the {@code Hitbox}, i.e. A circle will simply return the
	 * trig definitions of its points
	 * 
	 * @param angle
	 *            The angle in Radians. The angle will be in the range of [0, 2pi)
	 * @return The {@code Vector2} that is the furthest that this {@code Hitbox} reaches
	 */
	public abstract Vector2 getHitboxVectorAtAngle(double angle);
	
	/**
	 * Should get the angle of the Gradient vector to the surface of the {@code Hitbox} at the given angle
	 * <p>
	 * This should be in relation to the center of the {@code Hitbox}, i.e. A circle will simply return the
	 * angle itself.
	 * 
	 * @param angle
	 *            The angle in Radians. The angle will be in the range of [0, 2pi)
	 * @return The angle of the Gradient vector to the surface of the {@code Hitbox}
	 */
	public abstract double getHitboxGradientAtAngle(double angle);
	
	/**
	 * Gets the displacement of the center of the {@code Hitbox} relative to its upper-left corner
	 * <p>
	 * For example, the Circular Hitbox simply returns {@code Vector2.of(rad, rad)}
	 * 
	 * @return The displacement between the center and upper-left corner
	 */
	public abstract Vector2 getCenterDisplacement();
	
	/**
	 * Retrieves the angle of the surface (tangent to the angle) at the given angle
	 * <p>
	 * This should be in relation to the center of the {@code Hitbox}, i.e. A circle will simply return the
	 * angle + PI/2
	 * 
	 * @param angle
	 *            The angle in Radians. The angle should be in the range of [0, 2pi)
	 * @return The angle of the surface at the given angle, in the range of [0, pi)
	 */
	public abstract double getHitboxTangentAtAngle(double angle);
	
	/**
	 * Checks whether a {@code Vector2} lies within the confines of this {@code Hitbox}.
	 * 
	 * @param va2
	 *            The {@code Vector2} to check. Will be adjusted to account for position, in this case
	 *            {@code Vector2.ZERO} is equal to the position of this {@code EntityPhysics}
	 * @return Whether or not the given point lies within the {@code Hitbox}
	 */
	public abstract boolean pointLiesInsideHitbox(Vector2 va2);
	
	/**
	 * Checks whether the two {@code EntityPhysics} collide at all
	 * 
	 * @param e1
	 *            An {@code EntityPhysics}
	 * @param e2
	 *            An {@code EntityPhysics}
	 * @return Whether the two hitboxes of the {@code EntityPhysics}s collide
	 */
	public static boolean collides(EntityPhysics e1, EntityPhysics e2) {
		Vector2 vda = e1.newp.plus(e1.hitbox.getCenterDisplacement())
				.minus(e2.newp.plus(e2.hitbox.getCenterDisplacement()));
		double aa1 = Math.atan2(vda.getY(), vda.getX());
		double aa2 = aa1 + Math.PI;
		Vector2 va1 = e1.hitbox.getFurthestAtAngle(aa2).plus(e1.newp).minus(e2.newp);
		Vector2 va2 = e2.hitbox.getFurthestAtAngle(aa1).plus(e2.newp).minus(e1.newp);
		
//		System.out.println(e1 + "\t" + e2);
//		System.out.println(aa1);

//		boolean collides = overlaps(vb1.minus(vb2), va1.minus(va2));
		boolean collides = e1.hitbox.pointLiesInsideHitbox(va2) && e2.hitbox.pointLiesInsideHitbox(va1);
		return collides;
	}
	
	/**
	 * Checks whether the {@code Vector2}s overlap in their X and Y components
	 * 
	 * @param before
	 *            The {@code Vector2} of position difference before
	 * @param after
	 *            The {@code Vector2} of position difference after
	 * @return Whether the {@code Vector2}s overlap
	 */
	public static boolean overlaps(Vector2 before, Vector2 after) {
		boolean crossX = Math.signum(before.getX()) != Math.signum(after.getX());
		boolean crossY = Math.signum(before.getY()) != Math.signum(before.getY());
		return crossX && crossY;
	}
	
	/**
	 * Renders the hitbox as well as the upper-left point of the {@code EntityPhysics}
	 * 
	 * @param g
	 *            A {@code Graphics2D} object to use to draw the hitbox
	 * @param pos
	 *            The Position of the {@code EntityPhysics}
	 */
	public void renderHitbox(Graphics2D g, Vector2 pos) {
		for (int i = 0; i < 360; i++) {
			Vector2 p = this.getFurthestAtAngle(Math.toRadians(i)).plus(pos);
			g.drawRect((int) p.getX(), (int) p.getY(), 1, 1);
		}
		g.setColor(Color.RED);
		g.drawRect((int) pos.getX(), (int) pos.getY(), 2, 2);
	}
	
	/**
	 * A Rectangular/Square Hitbox
	 * 
	 * @author Kevin
	 */
	public static class HitboxRectangle extends Hitbox {
		
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * The Size of the Rectangle
		 */
		public double sizeX, sizeY;
		
		/**
		 * Half the size of the Rectangle
		 */
		public double radX, radY;
		
		/**
		 * Constructs a square hitbox
		 * 
		 * @param len
		 *            The side length of the square
		 */
		public HitboxRectangle(double len) {
			this(len, len);
		}
		
		public HitboxRectangle(double sizeX, double sizeY) {
			super(Math.sqrt(sizeX * sizeX + sizeY * sizeY) / 2);
			this.sizeX = sizeX;
			this.sizeY = sizeY;
			this.radX = sizeX / 2;
			this.radY = sizeY / 2;
		}
		
		@Override
		public Vector2 getHitboxVectorAtAngle(double angle) {
			double critical = Math.atan(this.radY / this.radX);
			double tan = Math.tan(angle);
			// Be dumb and check chase by case
			if (angle == critical) {
				return Vector2.of(this.radX, this.radY);
			} else if (angle == Math.PI - critical) {
				return Vector2.of(-this.radX, this.radY);
			} else if (angle == Math.PI + critical) {
				return Vector2.of(-this.radX, -this.radY);
			} else if (angle == 2 * Math.PI - critical) {
				return Vector2.of(this.radX, -this.radY);
			}
			if (angle > critical && angle < Math.PI - critical) {
				return Vector2.of(this.radY / tan, this.radY);
			} else if (angle > Math.PI - critical && angle < Math.PI + critical) {
				return Vector2.of(-this.radX, -this.radX * tan);
			} else if (angle > Math.PI + critical && angle < 2 * Math.PI - critical) {
				return Vector2.of(-this.radY / tan, -this.radY);
			} else if (angle > 2 * Math.PI - critical || angle < critical) {
				return Vector2.of(this.radX, this.radX * tan);
			}
			return Vector2.of(0, 0);
		}
		
		@Override
		public Vector2 getCenterDisplacement() {
			return Vector2.of(this.radX, this.radY);
		}
		
		@Override
		public boolean pointLiesInsideHitbox(Vector2 point) {
			return (point.getX() <= this.sizeX && point.getX() >= 0)
					&& (point.getY() <= this.sizeY && point.getY() >= 0);
		}
		
		@Override
		public double getHitboxTangentAtAngle(double angle) {
			double critical = Math.atan(this.radY / this.radX);
			// Be dumb and check chase by case
			if (angle >= critical && angle < Math.PI - critical) {
				return 0;
			} else if (angle >= Math.PI - critical && angle < Math.PI + critical) {
				return Math.PI / 2;
			} else if (angle >= Math.PI + critical && angle < 2 * Math.PI - critical) {
				return 0;
			} else if (angle >= 2 * Math.PI - critical || angle < critical) {
				return Math.PI / 2;
			}
			return 0;
		}
		
		@Override
		public double getHitboxGradientAtAngle(double angle) {
			double critical = Math.atan(this.radY / this.radX);
			// Be dumb and check chase by case
			if (angle >= critical && angle < Math.PI - critical) {
				return Math.PI / 2;
			} else if (angle >= Math.PI - critical && angle < Math.PI + critical) {
				return Math.PI;
			} else if (angle >= Math.PI + critical && angle < 2 * Math.PI - critical) {
				return 3 * Math.PI / 2;
			} else if (angle >= 2 * Math.PI - critical || angle < critical) {
				return 0;
			}
			return 0;
		}
	}
	
	/**
	 * A Circular Hitbox
	 * 
	 * @author Kevin
	 */
	public static class HitboxCircle extends Hitbox {
		
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public HitboxCircle(double rad) {
			super(rad);
			this.circleRadius = rad;
		}
		
		@Override
		public Vector2 getHitboxVectorAtAngle(double angle) {
			return Vector2.ofPolar(angle, this.circleRadius);
		}
		
		@Override
		public Vector2 getCenterDisplacement() {
			return Vector2.of(this.circleRadius, this.circleRadius);
		}
		
		@Override
		public boolean pointLiesInsideHitbox(Vector2 point) {
			double dist = point.displacement(this.getCenterDisplacement());
			return dist <= this.circleRadius && dist >= 0;
		}
		
		@Override
		public double getHitboxTangentAtAngle(double angle) {
			return clampAngle2(angle + Math.PI / 2);
		}
		
		@Override
		public double getHitboxGradientAtAngle(double angle) {
			return angle;
		}
	}
	
	/**
	 * A {@code Hitbox} based on an {@code ISpriteProvider}
	 * 
	 * @author Kevin
	 */
	public static class HitboxSprite extends Hitbox {
		
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public ISpriteProvider sprite;
		
		public HitboxSprite(ISpriteProvider sprite) {
			super(Math.sqrt(sprite.getSprite().height * sprite.getSprite().height
					+ sprite.getSprite().width * sprite.getSprite().width) / 2);
			this.sprite = sprite;
		}
		
		@Override
		public Vector2 getHitboxVectorAtAngle(double angle) {
			BufferedImage img = this.sprite.getSprite().getImage();
			Vector2 base = new HitboxRectangle(img.getWidth(), img.getHeight()).getFurthestAtAngle(angle);
			Vector2 check = base;
			double maxDisp = check.displacement(Vector2.ZERO);
			for (double d = maxDisp; d >= -maxDisp; d--) {
				check = Vector2.ofPolar(angle, d);
				if (this.pointLiesInsideHitbox(check)) {
					return check;
				}
			}
			return check;
		}
		
		@Override
		public Vector2 getCenterDisplacement() {
			BufferedImage img = this.sprite.getSprite().getImage();
			return Vector2.of(img.getWidth() / 2, img.getHeight() / 2);
		}
		
		@Override
		public boolean pointLiesInsideHitbox(Vector2 point) {
			BufferedImage img = this.sprite.getSprite().getImage();
			if (!new HitboxRectangle(img.getWidth(), img.getHeight()).pointLiesInsideHitbox(point)) {
				return false;
			}
			return img.getRGB((int) point.getX(), (int) point.getY()) != 0;
		}
		
		@Override
		public double getHitboxTangentAtAngle(double angle) {
			return 0;
		}
		
		@Override
		public double getHitboxGradientAtAngle(double angle) {
			return 0;
		}
		
	}
}
