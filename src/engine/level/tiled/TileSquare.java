package engine.level.tiled;

import java.util.ArrayList;
import java.util.List;

import engine.client.graphics.sprite.Sprite;
import engine.level.tiled.pathfind.PathfindNode;


public class TileSquare extends Tile {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6957156549047238499L;

	public TileSquare(LevelTiled level, TileCoords tile, Sprite sprite) {
		super(level, tile, sprite);
		// TODO Auto-generated constructor stub
	}
	
	public TileSquare(LevelTiled level, TileCoords tile, Sprite sprite, int moveCost) {
		super(level, tile, sprite, moveCost);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public List<Tile> getAdjacentTiles() {
		List<Tile> list = new ArrayList<Tile>();
		int x = this.tileCoords.x;
		int y = this.tileCoords.y;
		if (x > 0) {
			list.add(this.level.tiles[x + 1][y]);
		}
		if (y > 0) {
			list.add(this.level.tiles[x][y + 1]);
		}
		if (x < this.level.getTileWidth() - 1) {
			list.add(this.level.tiles[x - 1][y]);
		}
		if (y < this.level.getTileHeight() - 1) {
			list.add(this.level.tiles[x][y - 1]);
		}
		return list;
	}
	
	@Override
	public List<? extends PathfindNode> getAdjacentNodes() {
		return getAdjacentTiles();
	}

	@Override
	public int AStarCostEstimate(PathfindNode other) {
		return this.getDistanceFrom((Tile) other);
	}
	
}
