package Game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Util.Log;
import betterMinMax.SmartEvaluationFunction;

public class ConnectFour {

	public static final int WIDTH = 7;
	public static final int HEIGHT = 6;

	public boolean log = true;
	private int width, height;
	
	protected long bitBoardRed;
	protected long bitBoardYellow;
	public boolean redTurn = true;
	
	private static final long COLUMN_BITS = 0b0000000100000010000001000000100000010000001l;
	private static final long ROW_BITS =   0b1111111l;
	public static final long FULL_BOARD = 0b111111111111111111111111111111111111111111l;
	
	public State gameState;
	
	public ArrayList<Integer> moveHistory = new ArrayList<Integer>();
	protected int[] availableMoves;
	
	public ConnectFour() {
		this(WIDTH, HEIGHT, true);
	}

	public ConnectFour copy() {
		ConnectFour newInstance = new ConnectFour(width, height, log);
		newInstance.bitBoardRed = bitBoardRed;
		newInstance.bitBoardYellow = bitBoardYellow;
		newInstance.redTurn = redTurn;
		newInstance.gameState = gameState.copy();
		return newInstance;
	}
	
	public ConnectFour(int width, int height, boolean log) {
		this.log = log;
		if(width * height > 64) {
			throw new IllegalArgumentException("width * height cant be more than 64");
		}
		
		this.width = width;
		this.height = height;
	
		this.availableMoves = this.calcAvailableMoves();
		this.gameState = new State();
	}
	
	public int[] getAvailableMoves() {
		return availableMoves;
	}

	public int[] getAvailableMoves(boolean calcAvailableMoves) {
		if(calcAvailableMoves) {
			availableMoves = calcAvailableMoves();
		}
		return availableMoves;
	}
	
	public void makePlay(int columnIndex) {
		this.executePlay(columnIndex);
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void executePlay(int columnIndex) {
		if(columnIndex < 0 || columnIndex >= width) {
			throw new IllegalArgumentException("column is out of range, " + columnIndex+" < 0 or >= " + width);
		}
		long column = 1 << columnIndex;
				
		long affectedBitBoard = redTurn ? this.bitBoardRed : this.bitBoardYellow;
		long totalBitBoard = this.bitBoardRed | this.bitBoardYellow;
		
		
		long columnBits = this.COLUMN_BITS << columnIndex;
		long occupiedPlaces = (totalBitBoard & columnBits);
		long totalBitBoardColumn = totalBitBoard & occupiedPlaces;
		if((totalBitBoard & columnBits) == columnBits) {
			throw new IllegalArgumentException("Column is full");
		}
		
		moveHistory.add(columnIndex);
		
		
		//this means the row is empty
		if(occupiedPlaces == 0) {
			affectedBitBoard += column << width * (height - 1);
		} else {
			long cell = (totalBitBoardColumn ^ columnBits) & (totalBitBoardColumn >> width);
			affectedBitBoard += cell;
		}
		
		if(redTurn) {
			this.bitBoardRed = affectedBitBoard;
		} else {
			this.bitBoardYellow = affectedBitBoard;
		}
		
		this.availableMoves = this.calcAvailableMoves();
		checkState();
		
		//only change turn if game did NOT end
		if(!this.gameState.gameDidEnd()) {
			this.redTurn = !redTurn;
		}

	}
	
	protected int[] calcAvailableMoves() {
		long freeCols = (~(this.bitBoardRed | this.bitBoardYellow)) & this.ROW_BITS;
		int[] moves = new int[Long.bitCount(freeCols)];
	
		int index = 0;
		for(int i = 0; i < width; i++) {
			if((freeCols & (1l << i)) >= 1) {
				moves[index] = i;
				index++;
			}
		}
		
		return moves;
	}
	
	private void checkState() {
		boolean redWon = checkWin(this.bitBoardRed);
		boolean yellowWon = checkWin(this.bitBoardYellow);
		
		if(redWon) {
			this.gameState.setRedWon();
		} else if(yellowWon) {
			this.gameState.setYellowWon();
		} else if(this.availableMoves.length == 0) {
			this.gameState.setDraw();
		}
		
	}
	
	public int undoMove(boolean checkWin) {
		if(this.moveHistory.size() == 0)
			return -1;
		
		//only change turn if game did NOT end
		if(!this.gameState.gameDidEnd()) {
			this.redTurn = !redTurn;
		}
		
		this.gameState.reset();
		int columnToUndo = moveHistory.remove(moveHistory.size() - 1);
		long affectedColumn = this.COLUMN_BITS << columnToUndo;
		
	
		long affectedBitBoard = redTurn ? this.bitBoardRed : this.bitBoardYellow;
		long totalBitBoard = this.bitBoardRed | this.bitBoardYellow;
		
		long occupiedPlaces = (totalBitBoard & affectedColumn);
		long totalBitBoardColumn = totalBitBoard & occupiedPlaces;
		
		long cellToUndo;
		if((totalBitBoard & affectedColumn) == affectedColumn) {
			cellToUndo = 1 << columnToUndo;
		} else {
			
			//get cell that is at the top
			cellToUndo = (totalBitBoardColumn ^ affectedColumn) & (totalBitBoardColumn >> width);
			//move 1 row down
			cellToUndo = cellToUndo << width;
			
		}
		
		affectedBitBoard -= cellToUndo;
		
		if(redTurn) {
			this.bitBoardRed = affectedBitBoard;
		} else {
			this.bitBoardYellow = affectedBitBoard;
		}
		
		if(checkWin)
			this.checkState();

		return columnToUndo;
	}

	public boolean isRedTurn() {
		return redTurn;
	}
	
	public long getRedBitBoard() {
		return this.bitBoardRed;
	}

	public boolean isRedOrYellowAtPosition(int x, int y) {
		return isRedAtPosition(x, y) || isYellowAtPosition(x, y);
	}

	public boolean isRedAtPosition(int x, int y) {
		long index = y * width + x;
		// index = width * height - index;
		return ((bitBoardRed >> index) & 1) == 1;
	}

	public boolean isYellowAtPosition(int x, int y) {
		long index = y * width + x;
		// index = width * height - index;
		return ((bitBoardYellow >> index) & 1) == 1;
	}
	
	public long getYellowBitBoard() {
		return this.bitBoardYellow;
	}
	
	public long getTotalBoard() {
		return this.bitBoardYellow | this.bitBoardRed;
	}
	
	public static boolean checkWin(long bitBoard) {
		return checkHorizontalWin(bitBoard) >= 1 ||
				checkVerticallWin(bitBoard) >= 1 ||
				checkDiagonal1Win(bitBoard) >= 1 ||
				checkDiagonal2Win(bitBoard) >= 1;
	}
	
	public static long checkDiagonal1Win(long bitBoard) {
		return checkDiagonal1Win(bitBoard, 4);
	}
	// Left descending
	public static long checkDiagonal1Win(long bitBoard, int connectLength) {
		long compared = bitBoard;
		long notTopRow = ~(ROW_BITS);
		long notRightColumn = ~(COLUMN_BITS << WIDTH - 1);

		for(int i = 0; i < connectLength-1; i++) {
			//delete top row and right column, then move it to the top right
			compared = (((notRightColumn & compared & notTopRow ) >> WIDTH) << 1) & compared;
		}
		return compared;
	}
	
	public static long checkDiagonal2Win(long bitBoard) {
		return checkDiagonal2Win(bitBoard, 4);
	}
	// Right descending
	public static long checkDiagonal2Win(long bitBoard, int connectLength) {
		long compared = bitBoard;
		long notTopRow = ~(ROW_BITS);
		long notLeftColumn = ~(COLUMN_BITS);
		
		for(int i = 0; i < connectLength-1; i++) {
			compared = (((notLeftColumn & compared & notTopRow ) >> WIDTH) >> 1) & compared;
		}
		return compared;
	}
	
	public static long checkVerticallWin(long bitBoard) {
		return checkVerticallWin(bitBoard, 4);
	}
	public static long checkVerticallWin(long bitBoard, int connectLength) {
		long compared = bitBoard;
		
		long notTopColumn = ~ROW_BITS;

		for(int i = 0; i < connectLength-1; i++) {
			//delete top row, and move to top
			compared = ((compared & notTopColumn) >> WIDTH) & compared;
		}
		return compared;
	}
	
	public static long checkHorizontalWin(long bitBoard) {
		return checkHorizontalWin(bitBoard, 4);
	}
	public static long checkHorizontalWin(long bitBoard, int connectLength) {
		long compared = bitBoard;
		long notRightColumn = ~(COLUMN_BITS << WIDTH - 1);
		for(int i = 0; i < connectLength-1; i++) {
			//delete right column and 
			compared = ((compared & notRightColumn) << 1) & compared;
		}
		return compared;
	}
	
	
	public void printLong(long inp) {
		println(getLongBits(inp));
	}
	
	public String getLongBits(long inp) {
		String bits = String.format("%64s", Long.toBinaryString(inp)).replace(' ', '0');
		String out = "";
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				int index = bits.length() - x - y * width - 1;
				out += bits.charAt(index);
			}
			out += "\n";
		}
		return out;
	}
	
	private String toHex(long inp) {
		return Long.toHexString(inp);
	}

	public void printBoard() {
		printBoard('R','Y', '-');
	}

	public void printBoard(char red, char yellow, char nothing) {
		
		String bitsRed = String.format("%64s", Long.toBinaryString(bitBoardRed)).replace(' ', '0');
		String bitsYellow = String.format("%64s", Long.toBinaryString(bitBoardYellow)).replace(' ', '0');
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				int index = bitsRed.length() - x - y * width - 1;
				char charRed = bitsRed.charAt(index);
				char charYellow = bitsYellow.charAt(index);
				if(charRed == '1' && charYellow == '0') {
					print(red+ " ");
				} else if(charRed == '0' && charYellow == '1') {
					print(yellow+ " ");
				} else if(charRed == '0' && charYellow == '0') {
					print(nothing+ " ");
				} else {
					print("X ");
				}
			}
			println();
		}
	}
	
	public void disableLog() {
		this.log = false;
		println("DIS ");
	}
	
	public void println(Object o) {if(log)System.out.println(o);}
	public void println() {if(log)System.out.println();}
	public void print(Object o) {if(log)System.out.print(o);}


}
