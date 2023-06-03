package Game;

import java.util.ArrayList;

public class ConnectFour {
	private boolean log = true;
	private int width, height;
	
	private long bitBoardRed;
	private long bitBoardYellow;
	public boolean redTurn = true;
	
	private long COLUMN_BITS = 0;
	private long ROW_BITS = 0;
	
	public State gameState;
	
	public ArrayList<Integer> moveHistory = new ArrayList<Integer>();
	private int[] availableMoves;

	
	public ConnectFour() {
		this(7, 6, true);
	}
	
	public ConnectFour(int width, int height, boolean log) {
		this.log = log;
		if(width * height > 64) {
			throw new IllegalArgumentException("width * height cant be more than 64");
		}
		
		this.width = width;
		this.height = height;

		for(int i = 0; i < width; i++) {
			ROW_BITS += 1l << i;
		}
		
		for(int i = 0; i < height; i++) {
			COLUMN_BITS += 1l << i * width;
		}
		
		
		this.availableMoves = this.calcAvailableMoves();
		println(availableMoves);
		this.gameState = new State();
	}
	
	public int[] getAvailableMoves() {
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
		
		//this.printBoard();
		
		checkState();
		
		
		this.availableMoves = this.calcAvailableMoves();
		
		//only change turn if game did NOT end
		if(!this.gameState.gameDidEnd()) {
			this.redTurn = !redTurn;
		}

	}
	
	private int[] calcAvailableMoves() {
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
		boolean redWon = this.checkWin(this.bitBoardRed);
		boolean yellowWon = this.checkWin(this.bitBoardYellow);
		
		if(redWon) {
			this.gameState.setRedWon();
		} else if(yellowWon) {
			this.gameState.setYellowWon();
		} else if(this.availableMoves.length == 0) {
			this.gameState.setDraw();
		}
		
	}
	
	public void undoMove(boolean checkWin) {
		if(this.moveHistory.size() == 0)
			return;
		
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
	}
	
	public long getRedBitBoard() {
		return this.bitBoardRed;
	}
	
	
	public long getYellowBitBoard() {
		return this.bitBoardYellow;
	}
	
	public long getTotalBoard() {
		return this.bitBoardYellow | this.bitBoardRed;
	}
	
	private boolean checkWin(long bitBoard) {
		return checkHorizontalWin(bitBoard) ||
				checkVerticallWin(bitBoard) ||
				checkDiagonal1Win(bitBoard) ||
				checkDiagonal2Win(bitBoard);
	}
	
	private boolean checkDiagonal1Win(long bitBoard) {
		long compared = bitBoard;
		long notTopRow = ~(this.ROW_BITS << width - 1);
		long notRightColumn = ~(this.COLUMN_BITS << width - 1);
		for(int i = 0; i < 3; i++) {
			//delete top row and right column, then move it to the top right
			compared = (((notRightColumn & compared & notTopRow ) >> width) << 1) & compared;
		}
		return (compared >= 1);
	}
	
	private boolean checkDiagonal2Win(long bitBoard) {
		long compared = bitBoard;
		long notBottomRow = ~(this.ROW_BITS);
		long notLeftColumn = ~(this.COLUMN_BITS);
		
		for(int i = 0; i < 3; i++) {
			//delete top row and right column, then move it to the top right
			compared = (((notLeftColumn & compared & notBottomRow ) >> width) >> 1) & compared;
		}
		return (compared >= 1);
	}
	
	private boolean checkVerticallWin(long bitBoard) {
		long compared = bitBoard;
		
		long notTopColumn = ~this.ROW_BITS;

		for(int i = 0; i < 3; i++) {
			//delete top row, and move to top
			compared = ((compared & notTopColumn) >> width) & compared;
		}
		return (compared >= 1);
	}
	
	private boolean checkHorizontalWin(long bitBoard) {
		long compared = bitBoard;
		long notRightColumn = ~(this.COLUMN_BITS << width - 1);
		for(int i = 0; i < 3; i++) {
			//delete right column and 
			compared = ((compared & notRightColumn) << 1) & compared;
		}
		return (compared >= 1);
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
		
		String bitsRed = String.format("%64s", Long.toBinaryString(bitBoardRed)).replace(' ', '0');
		String bitsYellow = String.format("%64s", Long.toBinaryString(bitBoardYellow)).replace(' ', '0');
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				int index = bitsRed.length() - x - y * width - 1;
				char charRed = bitsRed.charAt(index);
				char charYellow = bitsYellow.charAt(index);
				if(charRed == '1' && charYellow == '0') {
					print("R ");
				} else if(charRed == '0' && charYellow == '1') {
					print("Y ");
				} else if(charRed == '0' && charYellow == '0') {
					print("- ");
				} else {
					print("X ");
				}
			}
			println();
		}
	}
	
	public String toString() {
		String board = "";
		for(int y = 0; y < width; y++) {
			for(int x = 0; x < height; x++) {
				
			}
		}
		
		return board;
	}
	
	public void disableLog() {
		this.log = false;
		println("DIS ");
	}
	
	public void println(Object o) {if(log)System.out.println(o);}
	public void println() {if(log)System.out.println();}
	public void print(Object o) {if(log)System.out.print(o);}
}
