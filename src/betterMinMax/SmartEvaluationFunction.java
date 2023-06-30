package betterMinMax;

import Game.ConnectFour;

public class SmartEvaluationFunction implements EvaluationFunction {

    private boolean isRed;
    private static long pattern = 0b10000100001000010000100001000010000100001l;
    private long[] allPatterns;

    public SmartEvaluationFunction(boolean isRed) {
        this.isRed = isRed;

        allPatterns = new long[5];
        allPatterns[0] = pattern;

        for(int i = 1; i < allPatterns.length; i++) {
            allPatterns[i] = (allPatterns[i-1] << 1) & ConnectFour.FULL_BOARD;
        }
    }

    private int calculateHits(long pattern, long board, long boardOppositeSide) {
        long bitBoard = board | (pattern & ~boardOppositeSide);

        // if(countWinHorizontal(bitBoard)
        // + countWinVertical(bitBoard)
        // + countWinRightDescendingDiagonal(bitBoard)
        // + countWinLeftDescendingDiagonal(bitBoard) >= 1) {
        //     System.out.println("THE calculateHits BOARD:::");
        //     printBoard(bitBoard);

        //     if(countWinHorizontal(bitBoard) >= 1) {
        //         System.out.println("For checkHorizontalWin " +countWinHorizontal(bitBoard));
        //     }
        //     if(countWinVertical(bitBoard) >= 1) {
        //         System.out.println("For checkVerticallWin " + countWinVertical(bitBoard));
        //     }
        //     if(countWinRightDescendingDiagonal(bitBoard) >= 1) {
        //         System.out.println("For RightDescendingDiagonal " + countWinRightDescendingDiagonal(bitBoard));
        //     }
        //     if(countWinLeftDescendingDiagonal(bitBoard) >= 1) {
        //         System.out.println("For LeftDescendingDiagonal " + countWinLeftDescendingDiagonal(bitBoard));
        //     }
        // }

        return countWinHorizontal(bitBoard)
        + countWinVertical(bitBoard)
        + countWinRightDescendingDiagonal(bitBoard)
        + countWinLeftDescendingDiagonal(bitBoard);
    }

    private int countWinHorizontal(long bitBoard) {
        long boardWinOutcome = ConnectFour.checkHorizontalWin(bitBoard);
        long falsePositives = boardWinOutcome & (boardWinOutcome >> 1);
        boardWinOutcome = boardWinOutcome & (~falsePositives);

        return Long.bitCount(boardWinOutcome);
    }

    private int countWinVertical(long bitBoard) {
        long boardWinOutcome = ConnectFour.checkVerticallWin(bitBoard);
        long falsePositives = boardWinOutcome & (boardWinOutcome >> ConnectFour.WIDTH);
        boardWinOutcome = boardWinOutcome & (~falsePositives);

        return Long.bitCount(boardWinOutcome);
    }

    private int countWinRightDescendingDiagonal(long bitBoard) {
        long boardWinOutcome = ConnectFour.checkDiagonal2Win(bitBoard);
        long falsePositives = boardWinOutcome & (boardWinOutcome << (ConnectFour.WIDTH + 1));
        boardWinOutcome = boardWinOutcome & (~falsePositives);

        return Long.bitCount(boardWinOutcome);
    }

    private int countWinLeftDescendingDiagonal(long bitBoard) {
        long boardWinOutcome = ConnectFour.checkDiagonal1Win(bitBoard);
        long falsePositives = boardWinOutcome & (boardWinOutcome << (ConnectFour.WIDTH - 1));
        boardWinOutcome = boardWinOutcome & (~falsePositives);

        // if(patternNum == 3 && checkingHitsForColor.equals("Yellow")) {
        //     System.out.println("==========");
        //     System.out.println("CHECKING SPECIFIC CASE");
        //     System.out.println("Bitboard to check:" );
        //     printBoard(bitBoard);
        //     System.out.println("RESULTS IN WIN:::" + Long.bitCount(boardWinOutcome));
        //     System.out.println("Outcome of checkDiagonal2Win: " + Long.bitCount(ConnectFour.(bitBoard)));
        //     printBoard(boardWinOutcome);
        // }

        return Long.bitCount(boardWinOutcome);
    }


    private int applyPatterns(long[] patterns, long board, long boardOppositeSide) {
        int hits = 0;
        int i = 0;
        for(long pattern : patterns) {
            patternNum = i+1;
            int hitsForPattern = calculateHits(pattern, board, boardOppositeSide);
            hits += hitsForPattern;
            // if(hitsForPattern > 0) {
            //     System.out.println(checkingHitsForColor+" Hit for pattern ["+ (i+1)+"] NUMM:::: " + hitsForPattern);
            // }
            i++;
        }

        return hits;
    }
    static int patternNum = 0;
    static String checkingHitsForColor = "";

    public static void testPatterns(ConnectFour game) {
        SmartEvaluationFunction eval = new SmartEvaluationFunction(false);
        // checkingHitsForColor = "Red";
        int redHits = eval.applyPatterns(eval.allPatterns, game.getRedBitBoard(), game.getYellowBitBoard());
        // checkingHitsForColor = "Yellow";
        int yellowHits = eval.applyPatterns(eval.allPatterns, game.getYellowBitBoard(), game.getRedBitBoard());
        
        // System.out.println("[SmartEvaluationFunction] HITS FOR RED " + redHits);
        // System.out.println("[SmartEvaluationFunction] HITS FOR YELLOW " + yellowHits);
    }

    private float calculateDoubleHits(long bitBoard) {
        int doubleHits = 
            Long.bitCount(ConnectFour.checkDiagonal1Win(bitBoard, 2)) +
            Long.bitCount(ConnectFour.checkDiagonal2Win(bitBoard, 2)) +
            Long.bitCount(ConnectFour.checkHorizontalWin(bitBoard, 2)) +
            Long.bitCount(ConnectFour.checkVerticallWin(bitBoard, 2));

        // Return float from 0 to 1. The more double hits, the closer to 1;
        return 1-1f/(1 + doubleHits);
    }

    @Override
    public float evaluate(ConnectFour game, int depth) {
        if(game.gameState.gameDidDraw()) {
            return 0;
        }

        if(game.gameState.gameDidEnd()) {
            boolean won = game.gameState.redDidWon() == isRed;
            return 1000 / (won ? depth : -depth);
        }

        float redHits = applyPatterns(allPatterns, game.getRedBitBoard(), game.getYellowBitBoard());
        float yellowHits = applyPatterns(allPatterns, game.getYellowBitBoard(), game.getRedBitBoard());

        redHits += calculateDoubleHits(game.getRedBitBoard());
        yellowHits += calculateDoubleHits(game.getYellowBitBoard());

        return isRed ? (redHits - yellowHits) : (yellowHits - redHits);
    }

    public boolean scoreIsWinOrLoss(float score, boolean isMax) {
        return isMax ? score > 50 : score < -50;
    }

    public static void printBoard(long board) {
		String bitsBoard = String.format("%64s", Long.toBinaryString(board)).replace(' ', '0');
		
		for(int y = 0; y < 9; y++) {
			for(int x = 0; x < 7; x++) {
				int index = bitsBoard.length() - x - y * 7 - 1;
                // System.out.println(index);
				char charBoard = bitsBoard.charAt(index);
				if(charBoard == '1') {
					print("1 ");
				} else {
					print("- ");
				}
			}
			println();
		}
	}

    private static void println(Object o) {
        System.out.println(o);
    }

    private static void println() {
        System.out.println();
    }
    private static void print(Object o) {
        System.out.print(o);
    }
    
}
