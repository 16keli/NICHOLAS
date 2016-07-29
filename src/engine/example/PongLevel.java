package engine.example;

import java.awt.Color;

import engine.Game;
import engine.client.graphics.FontWrapper;
import engine.client.graphics.Screen;
import engine.event.SubscribeEvent;
import engine.level.Vector2;
import engine.physics.entity.Hitbox.HitboxCircle;
import engine.physics.level.LevelPhysics;

public class PongLevel extends LevelPhysics {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EntityBall ball;
	
	public EntityPaddle[] paddles = new EntityPaddle[2];
	
	public int score[] = new int[2];
	
	public PongLevel(Game game) {
		super(game, 320, 180);
		this.ball = new EntityBall(this);
		this.paddles[0] = new EntityPaddle(this, 0);
		this.paddles[1] = new EntityPaddle(this, 1);
	}
	
	@Override
	public void tickLevel() {
//		System.out.println("Position:\tServer:\t" + paddles[0].posX + ", " + paddles[0].posY);
		if (ball.pos.y < 0 || ball.pos.y + ((HitboxCircle) ball.hitbox).rad > this.height) {
			ball.vel = Vector2.of(ball.vel.x, ball.vel.y * -1);
		}
		if (ball.pos.x < 0) {
			this.game.events.post(new EventPlayerScore((short) 1, ++score[1]));
			this.reset();
		}
		if (ball.pos.x + ((HitboxCircle) ball.hitbox).rad > this.width) {
			this.game.events.post(new EventPlayerScore((short) 0, ++score[0]));
			this.reset();
		}
	}
	
	@Override
	public void render(Screen s) {
		s.clear(Color.GRAY.getRGB());
		ball.render(s);
		paddles[0].render(s);
		paddles[1].render(s);
//		Font.draw(paddles[0].pos.x + ", " + paddles[0].pos.y, s, 16, 16, Color.get(0, 555, 555, 555));
//		System.out.println("Position:\tClient:\t" + paddles[0].posX + ", " + paddles[0].posY);
		FontWrapper.draw(score[0] + "", s, 128, 16, Color.WHITE.getRGB());
		FontWrapper.draw(score[1] + "", s, 192, 16, Color.WHITE.getRGB());
		FontWrapper.renderFrame(s, "", 0, 0, 320, 180, Color.WHITE.getRGB(), Color.WHITE.getRGB());
	}
	
	@Override
	public void reset() {
		this.ball.pos = Vector2.of(this.width / 2, this.height / 2);
		this.ball.reset();
	}
	
	@SubscribeEvent
	public void playerScore(EventPlayerScore e) {
		score[e.pnum] = e.score;
	}
	
	@SubscribeEvent
	public void playerInput(EventInput e) {
//		System.out.println("Moving the paddle");
		paddles[e.pnum].vel = Vector2.of(0, e.dir * 5);
	}
	
}
