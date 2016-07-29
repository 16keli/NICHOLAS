package engine.level.tiled;

import java.util.ArrayList;
import java.util.List;

import engine.Game;
import engine.client.graphics.Screen;
import engine.level.Level;
import engine.level.tiled.Tile.TileCoords;

public abstract class LevelTiled extends Level {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The list of {@code Tile}s in the fashion {@code tile[x][y]}
	 */
	public Tile[][] tiles;
	
	public LevelTiled(Game game, int w, int h, int tw, int th) {
		super(game, w, h);
		this.tiles = new Tile[tw][th];
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void tickLevel() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void render(Screen s) {
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				tiles[x][y].render(s);
			}
		}
	}
	
	public boolean tileExists(TileCoords t) {
		return tileExists(t.x, t.y);
	}
	
	public boolean tileExists(int x, int y) {
		if (x >= getTileWidth() || x < 0 || y >= getTileHeight() || y < 0) {
			return false;
		}
		return true;
	}
	
	public void setTile(Tile t) {
		tiles[t.tileCoords.x][t.tileCoords.y] = t;
	}
	
	public Tile getTile(TileCoords t) {
		return getTile(t.x, t.y);
	}
	
	public Tile getTile(int x, int y) {
		return tiles[x][y];
	}
	
	public int getTileWidthPixel() {
		return this.width / this.getTileWidth();
	}
	
	public int getTileHeightPixel() {
		return this.height / this.getTileHeight();
	}
	
	public int getTileWidth() {
		return tiles.length;
	}
	
	public int getTileHeight() {
		return tiles[0].length;
	}
	
	/**
	 * Gets all {@code Tile}s adjacent to the given {@code Tile}
	 * 
	 * @param t
	 *            The {@code Tile} to gets {@code Tile}s adjacent to
	 * @return All {@code Tile}s adjacent to that tile
	 */
	public abstract List<Tile> adjacentTiles(Tile t);
	
	/**
	 * Gets all {@code Tile}s adjacent to the given {@code Tile} except for {@code exclude}
	 * <p>
	 * Does not throw an Exception if {@code exclude} is not found
	 * 
	 * @param t
	 *            The {@code Tile} to get {@code Tile}s adjacent to
	 * @param exclude
	 *            The {@code Tile} to exclude from the list
	 * @return All {@code Tile}s adjacent to the given {@code Tile} except for {@code exclude}
	 */
	public List<Tile> adjacentTilesExcluding(Tile t, Tile exclude) {
		List<Tile> list = adjacentTiles(t);
		list.remove(exclude);
		return list;
	}
	
	/**
	 * Gets all {@code Tile}s adjacent to the given {@code Tile} except for the {@code Tile} at distance
	 * {@code range}
	 * <p>
	 * Does not throw an Exception if a suitable {@code Tile} is not found
	 * 
	 * @param t
	 *            The {@code Tile} to get {@code Tile}s adjacent to
	 * @param source
	 *            The {@code Tile} to check range relative to
	 * @param range
	 *            The range of the {@code Tile}s to check relative to
	 * @return All {@code Tile}s adjacent to the given {@code Tile} except for the {@code Tile} at the given
	 *         range
	 */
	public List<Tile> adjacentTilesExcluding(Tile t, Tile source, int range) {
		List<Tile> list = adjacentTiles(t);
		Tile remove = null;
		for (Tile tile : list) {
			if (tile.getDistanceFrom(source) == range) {
				remove = tile;
			}
		}
		if (remove != null) {
			list.remove(remove);
		}
		return list;
	}
	
	/**
	 * Gets all {@code Tile}s within a given range of the source {@code Tile}, excluding movement costs
	 * 
	 * @param t
	 *            The source {@code Tile}
	 * @param range
	 *            The range to search for {@code Tile}s
	 * @return All {@code Tile}s within a given range of the source {@code Tile}
	 */
	public List<Tile> adjacentTilesInAbsoluteRange(Tile t, int range) {
		List<Tile> tiles = new ArrayList<Tile>();
		tiles.addAll(adjacentTiles(t));
		for (int i = 1; i < range; i++) {
			for (Tile tile : tiles) {
				if (tile.getDistanceFrom(t) == i) {
					tiles.addAll(adjacentTilesExcluding(tile, t, i - 1));
				}
			}
		}
		return tiles;
	}
	
	/**
	 * Gets all {@code Tile}s within a given range of the source {@code Tile}, including movement costs, for a
	 * given {@code EntityTiled}
	 * 
	 * @param t
	 *            The source {@code Tile}
	 * @param ent
	 *            The {@code EntityTiled}
	 * @param range
	 *            The range to search for {@code Tile}s
	 * @return All {@code Tile}s within a given range of the source {@code Tile}
	 */
	public List<Tile> adjacentTilesInRange(Tile t, EntityTiled ent, int range) {
		List<Tile> tiles = new ArrayList<Tile>();
		tiles.addAll(adjacentTiles(t));
		for (int i = 1; i < range; i++) {
			for (Tile tile : tiles) {
				if (tile.getDistanceFrom(t) == i) {
					tiles.addAll(adjacentTilesExcluding(tile, t, i - tile.getMovementCost(ent.getClass())));
				}
			}
		}
		return tiles;
	}
	
}
