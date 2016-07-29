package engine.level.tiled;

import java.util.ArrayList;
import java.util.List;

import engine.Game;

public abstract class LevelTiledSquare extends LevelTiled {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LevelTiledSquare(Game game, int w, int h, int tw, int th) {
		super(game, w, h, tw, th);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Tile> adjacentTiles(Tile t) {
		List<Tile> list = new ArrayList<Tile>();
		int x = t.tileCoords.x;
		int y = t.tileCoords.y;
		if (x > 0) {
			list.add(tiles[x + 1][y]);
		}
		if (y > 0) {
			list.add(tiles[x][y + 1]);
		}
		if (x < this.getTileWidth() - 1) {
			list.add(tiles[x - 1][y]);
		}
		if (y < this.getTileHeight() - 1) {
			list.add(tiles[x][y - 1]);
		}
		return list;
	}

}
