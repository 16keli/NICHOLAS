package engine.util.math;

import java.io.Serializable;

/**
 * A good-enough implementation of a Matrix with values stored using double-precision floating point values
 * <p>
 * Like all my Mathematical classes, {@code Matrix} is immutable. Any operations done will simply return a new
 * instance of {@code Matrix}.
 * <p>
 * This class is still in TODO mode for now. It will become more flushed out as I need features.
 * 
 * @author Kevin
 */
public class Matrix implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1505314511390282418L;
	
	/**
	 * The internal storage of values
	 */
	private double[][] values;
	
	/**
	 * The size of this {@code Matrix} in rows
	 */
	private int rows;
	
	/**
	 * The size of this {@code Matrix} in columns
	 */
	private int columns;
	
	// Constructors
	
	/**
	 * Constructs a new {@code Matrix} with the specified size and 0 as the value for every element
	 * 
	 * @param rows
	 *            The size of this {@code Matrix} in rows
	 * @param cols
	 *            The size of this {@code Matrix} in columns
	 */
	public Matrix(int rows, int cols) {
		this.rows = rows;
		this.columns = cols;
		this.values = new double[rows][cols];
	}
	
	/**
	 * Constructs a new {@code Matrix} with the specified size and fills it with the given scalar value
	 * 
	 * @param rows
	 *            The size of this {@code Matrix} in rows
	 * @param cols
	 *            The size of this {@code Matrix} in columns
	 * @param val
	 *            The value to fill the {@code Matrix} with
	 */
	public Matrix(int rows, int cols, double val) {
		this(rows, cols);
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {
				this.values[x][y] = val;
			}
		}
	}
	
	/**
	 * Constructs a new {@code Matrix} from the given two-dimensional array
	 * 
	 * @param values
	 *            The 2d array of values to initialize this {@code Matrix} with
	 * @throws IllegalArgumentException
	 *             If not all rows are the same length
	 */
	public Matrix(double[][] values) {
		this.rows = values.length;
		this.columns = values[0].length;
		for (int x = 1; x < this.rows; x++) {
			if (values[x].length != this.columns) {
				throw new IllegalArgumentException("Not all rows are the same size!");
			}
		}
		this.values = values;
	}
	
	// "Transformation" Operations
	
	/**
	 * Returns this {@code Matrix} transposed
	 * 
	 * @return
	 */
	public Matrix transpose() {
		Matrix trans = new Matrix(this.rows, this.columns);
		for (int x = 0; x < this.rows; x++) {
			for (int y = 0; y < this.columns; y++) {
				trans.values[y][x] = this.values[x][y];
			}
		}
		return trans;
	}
	
	// Getter Operations
	
	/**
	 * Retrieves the value stored in the {@code Matrix} at the given location
	 * 
	 * @param row
	 *            The row location
	 * @param col
	 *            The column location
	 * @return
	 */
	public double get(int row, int col) {
		return this.values[row][col];
	}
	
	/**
	 * Retrieves the submatrix that goes from {@code rowStart} to {@code rowEnd} and {@code colStart} to
	 * {@code colEnd}
	 * 
	 * @param rowStart
	 *            The row index to start the submatrix at, inclusive
	 * @param colStart
	 *            The column index to start the submatrix at, inclusive
	 * @param rowEnd
	 *            The row index to end the submatrix at, exclusive
	 * @param colEnd
	 *            The column index to end the submatrix at, exclusive
	 * @return The submatrix that goes from {@code rowStart} to {@code rowEnd} and {@code colStart} to
	 *         {@code colEnd}
	 * @throws ArrayIndexOutOfBoundsException
	 *             If any of the parameters are larger than the size of this {@code Matrix}
	 */
	public Matrix subMatrix(int rowStart, int colStart, int rowEnd, int colEnd) {
		if (rowEnd > this.rows || colEnd > this.columns || rowStart < 0 || colStart < 0) {
			throw new ArrayIndexOutOfBoundsException("Attempted to create submatrix from matrix with size ("
					+ this.rows + ", " + this.columns + "), with parameters out of bounds!");
		}
		Matrix sub = new Matrix(rowEnd - rowStart, colEnd - colStart);
		for (int x = rowStart; x < rowEnd; x++) {
			for (int y = colStart; y < colEnd; y++) {
				sub.values[x - rowStart][y - colStart] = this.values[x][y];
			}
		}
		return sub;
	}
	
	/**
	 * Retrieves the submatrix with values from {@code this}'s rows and columns specified in {@code rows} and
	 * {@code cols}
	 * 
	 * @param rows
	 *            The array of row indices
	 * @param cols
	 *            The array of column indices
	 * @return The submatrix with values from {@code this}'s rows and columns specified in {@code rows} and
	 *         {@code cols}
	 * @throws ArrayIndexOutOfBoundsException
	 *             If any of the parameters are larger than the size of this {@code Matrix}
	 */
	public Matrix subMatrix(int[] rows, int[] cols) {
		Matrix sub = new Matrix(rows.length, cols.length);
		if (rows.length > this.rows || cols.length > this.columns) {
			throw new ArrayIndexOutOfBoundsException("Attempted to create submatrix from matrix with size ("
					+ this.rows + ", " + this.columns + "), with parameters out of bounds!");
		}
		for (int x = 0; x < rows.length; x++) {
			for (int y = 0; y < cols.length; y++) {
				sub.values[x][y] = this.values[rows[x]][cols[y]];
			}
		}
		return sub;
	}
	
	/**
	 * Retrieves the submatrix that includes all the rows and columns except for those given by the arguments
	 * 
	 * @param rowNum
	 *            The row number to exclude
	 * @param colNum
	 *            The column number to exclude
	 * @return
	 */
	public Matrix subMatrixExcluding(int rowNum, int colNum) {
		int[] rowNums = new int[rows - 1];
		int[] colNums = new int[columns - 1];
		for (int i = 0; i < rows - 1; i++) {
			if (i < rowNum) {
				rowNums[i] = i;
			} else if (i > rowNum) {
				rowNums[i - 1] = i;
			}
		}
		for (int i = 0; i < columns - 1; i++) {
			if (i < colNum) {
				colNums[i] = i;
			} else if (i > colNum) {
				colNums[i - 1] = i;
			}
		}
		
		return subMatrix(rowNums, colNums);
	}
	
	/**
	 * Retrieves the values stored in this {@code Matrix}
	 * 
	 * @return
	 */
	public double[][] getValues() {
		return this.values;
	}
	
	/**
	 * Retrieves the number of rows in this {@code Matrix}
	 * 
	 * @return
	 */
	public int getRows() {
		return this.rows;
	}
	
	/**
	 * Retrieves the number of columns in this {@code Matrix}
	 * 
	 * @return
	 */
	public int getColumns() {
		return this.columns;
	}
	
	/**
	 * Checks whether this {@code Matrix} is a square matrix
	 * 
	 * @return
	 */
	public boolean isSquare() {
		return this.rows == this.columns;
	}
	
}
