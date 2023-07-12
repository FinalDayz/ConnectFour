package versions.v5.negamax.betterMinMax;

import Game.ConnectFour;
import ui.BitBoardViewer;

public class SmartEvaluationFunction implements EvaluationFunction {

    private static final float POSITIVE_PATTERN_MODIFIER = 1.6f;
    private static final float NEGATIVE_PATTERN_MODIFIER = 0.625f;
    private static final float[] ROW_MODIFIERS = new float[]{
            1.6f,         // ROW 1 (from bottom)
            1.8f,      // ROW 2 (from bottom)
            1.6f,       // ROW 3 (from bottom)
            1.4f,      // ROW 4 (from bottom)
            1.2f,       // ROW 5 (from bottom)
            1.0f,      // ROW 6 (from bottom)
    };

//    private static final float[] ROW_MODIFIERS = new float[]{
//            1f,     // ROW 1 (from bottom)
//            0.95f,     // ROW 2 (from bottom)
//            0.978f,     // ROW 3 (from bottom)
//            0.97f,     // ROW 4 (from bottom)
//            0.965f,     // ROW 5 (from bottom)
//            0.96f,     // ROW 6 (from bottom)
//
//
////            0.985f,     // ROW 2 (from bottom)
////            0.978f,     // ROW 3 (from bottom)
////            0.97f,     // ROW 4 (from bottom)
////            0.965f,     // ROW 5 (from bottom)
////            0.96f,     // ROW 6 (from bottom)
////            1f,     // ROW 1 (from bottom)
//    };

    private static final float[] ROW_MODIFIERS_RED = new float[]{
            ROW_MODIFIERS[0] * POSITIVE_PATTERN_MODIFIER,
            ROW_MODIFIERS[1] * NEGATIVE_PATTERN_MODIFIER,
            ROW_MODIFIERS[2] * POSITIVE_PATTERN_MODIFIER,
            ROW_MODIFIERS[3] * NEGATIVE_PATTERN_MODIFIER,
            ROW_MODIFIERS[4] * POSITIVE_PATTERN_MODIFIER,
            ROW_MODIFIERS[5] * NEGATIVE_PATTERN_MODIFIER,
    };

    private static final float[] ROW_MODIFIERS_YELLOW = new float[]{
            ROW_MODIFIERS[0] * NEGATIVE_PATTERN_MODIFIER,
            ROW_MODIFIERS[1] * POSITIVE_PATTERN_MODIFIER,
            ROW_MODIFIERS[2] * NEGATIVE_PATTERN_MODIFIER,
            ROW_MODIFIERS[3] * POSITIVE_PATTERN_MODIFIER,
            ROW_MODIFIERS[4] * NEGATIVE_PATTERN_MODIFIER,
            ROW_MODIFIERS[5] * POSITIVE_PATTERN_MODIFIER,
    };
    private static long pattern = 0b10000100001000010000100001000010000100001l;
    private long[] allPatterns;

    private static final float[] COLUMN_REWARD =
            // NONE:
//            new float[]{ 0, 0.01f, 0.05f, 0.1f, 0.05f, 0.01f, 0};
//            new float[]{ 0, 0.02f, 0.1f, 0.2f, 0.1f, 0.02f, 0};
//            new float[]{ 0, 0.05f, 0.15f, 0.3f, 0.15f, 0.05f, 0};
            new float[]{0.1f, 0.07f, 0.2f, 0.4f, 0.2f, 0.07f, 0.1f};

    public SmartEvaluationFunction() {

        allPatterns = new long[5];
        allPatterns[0] = pattern;

        for (int i = 1; i < allPatterns.length; i++) {
            allPatterns[i] = (allPatterns[i - 1] << 1) & ConnectFour.FULL_BOARD;
        }
    }

    private float calculateHits(long pattern, long board, long boardOppositeSide, boolean isRed) {
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


        return countWinHorizontal(bitBoard, pattern, isRed)
                + countWinVertical(bitBoard, pattern, isRed)
                + countWinRightDescendingDiagonal(bitBoard, pattern, isRed)
                + countWinLeftDescendingDiagonal(bitBoard, pattern, isRed);
    }

    private float countWinHorizontal(long bitBoard, long pattern, boolean isRed) {
        long boardWinOutcome = ConnectFour.checkHorizontalWin(bitBoard);
        long falsePositives = boardWinOutcome & (boardWinOutcome >> 1);
        boardWinOutcome = boardWinOutcome & (~falsePositives);

        return applyHitsReward(
                boardWinOutcome,
                Patterns::applyHorizontal,
                pattern,
                isRed ? ROW_MODIFIERS_RED : ROW_MODIFIERS_YELLOW
        );
    }

    private float countWinVertical(long bitBoard, long pattern, boolean isRed) {
        long boardWinOutcome = ConnectFour.checkVerticallWin(bitBoard);
        long falsePositives = boardWinOutcome & (boardWinOutcome >> ConnectFour.WIDTH);
        boardWinOutcome = boardWinOutcome & (~falsePositives);

        return applyHitsReward(
                boardWinOutcome,
                Patterns::applyVertical,
                pattern,
                isRed ? ROW_MODIFIERS_RED : ROW_MODIFIERS_YELLOW
        );
    }

    private float countWinRightDescendingDiagonal(long bitBoard, long pattern, boolean isRed) {
        long boardWinOutcome = ConnectFour.checkDiagonal2Win(bitBoard);
        long falsePositives = boardWinOutcome & (boardWinOutcome << (ConnectFour.WIDTH + 1));
        boardWinOutcome = boardWinOutcome & (~falsePositives);

        return applyHitsReward(
                boardWinOutcome,
                Patterns::applyRightDescendingDiagonal,
                pattern,
                isRed ? ROW_MODIFIERS_RED : ROW_MODIFIERS_YELLOW
        );
    }

    private float countWinLeftDescendingDiagonal(long bitBoard, long pattern, boolean isRed) {
        long boardWinOutcome = ConnectFour.checkDiagonal1Win(bitBoard);
        long falsePositives = boardWinOutcome & (boardWinOutcome << (ConnectFour.WIDTH - 1));
        boardWinOutcome = boardWinOutcome & (~falsePositives);

        return applyHitsReward(
                boardWinOutcome,
                Patterns::applyLeftDescendingDiagonal,
                pattern,
                isRed ? ROW_MODIFIERS_RED : ROW_MODIFIERS_YELLOW
        );
    }

    private float applyHitsReward(
            long boardWinOutcome,
            Patterns.ApplyPattern patternFunction,
            long pattern,
            float[] rowModifiers
    ) {
        if (boardWinOutcome == 0) {
            return 0;
        }

        long diagonalFourInRow = patternFunction.applyPattern(boardWinOutcome);
        long patternHitsInFourInRow = diagonalFourInRow & pattern;

        float totalScore = 0f;
//        totalScore += Long.bitCount(patternHitsInFourInRow);
        totalScore += Long.bitCount(patternHitsInFourInRow & Patterns.ROW_PATTERNS[0]) * rowModifiers[0];
        totalScore += Long.bitCount(patternHitsInFourInRow & Patterns.ROW_PATTERNS[1]) * rowModifiers[1];
        totalScore += Long.bitCount(patternHitsInFourInRow & Patterns.ROW_PATTERNS[2]) * rowModifiers[2];
        totalScore += Long.bitCount(patternHitsInFourInRow & Patterns.ROW_PATTERNS[3]) * rowModifiers[3];
        totalScore += Long.bitCount(patternHitsInFourInRow & Patterns.ROW_PATTERNS[4]) * rowModifiers[4];
        totalScore += Long.bitCount(patternHitsInFourInRow & Patterns.ROW_PATTERNS[5]) * rowModifiers[5];

//        totalScore += Long.bitCount(patternHitsInFourInRow & positivePattern) * POSITIVE_PATTERN_MODIFIER;
//        totalScore += Long.bitCount(patternHitsInFourInRow & negativePattern) * NEGATIVE_PATTERN_MODIFIER;

        return totalScore;
    }

    private float applyPatterns(long[] patterns, long board, long boardOppositeSide, boolean isRed) {
        float hits = 0;
        int i = 0;
        for (long pattern : patterns) {
            patternNum = i + 1;
//            BitBoardViewer.add("pattern_" + patternNum, "Pattern " + patternNum + " for Left Descending Diagonal check")
//                    .addBoard(pattern, "Pattern mask");
            float hitsForPattern = calculateHits(pattern, board, boardOppositeSide, isRed);
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
        BitBoardViewer.setStaticGame(game);
        SmartEvaluationFunction eval = new SmartEvaluationFunction();
        // checkingHitsForColor = "Red";
        float redHits = eval.applyPatterns(eval.allPatterns, game.getRedBitBoard(), game.getYellowBitBoard(), true);
        // checkingHitsForColor = "Yellow";
        float yellowHits = eval.applyPatterns(eval.allPatterns, game.getYellowBitBoard(), game.getRedBitBoard(), false);

        System.out.println("RED SCORE: " + (redHits - yellowHits));
    }

    private float calculateDoubleHits(long bitBoard) {
        int doubleHits =
                Long.bitCount(ConnectFour.checkDiagonal1Win(bitBoard, 2)) +
                        Long.bitCount(ConnectFour.checkDiagonal2Win(bitBoard, 2)) +
                        Long.bitCount(ConnectFour.checkHorizontalWin(bitBoard, 2)) +
                        Long.bitCount(ConnectFour.checkVerticallWin(bitBoard, 2));

        // Return float from 0 to 1. The more double hits, the closer to 1;
//        return 1-1f/(1 + doubleHits);
        return 1 - 1f / (1 + doubleHits);
    }

    private float addRedColumnReward(long redBB, long yellowBB) {
        float score = 0;
        for (int column = 0; column < 7; column++) {
            float multiplier = COLUMN_REWARD[column];
            long columnMask = ConnectFour.COLUMN_BITS << column;
            score += multiplier * Long.bitCount(columnMask & redBB);
            score -= multiplier * Long.bitCount(columnMask & yellowBB);
        }

        return score;
    }

    @Override
    public float evaluate(ConnectFour game, int depth) {
        if (game.gameState.gameDidDraw()) {
            return 0;
        }

        if (game.gameState.gameDidEnd()) {
            boolean won = game.gameState.redDidWon();
            return (float) 1000 / (won ? depth : -depth);
        }

        long redBB = game.getRedBitBoard();
        long yellowBB = game.getYellowBitBoard();

        float redHits = applyPatterns(allPatterns, redBB, yellowBB, true);
        float yellowHits = applyPatterns(allPatterns, yellowBB, redBB, false);

        redHits += calculateDoubleHits(redBB);
        yellowHits += calculateDoubleHits(yellowBB);

        float endScore = redHits - yellowHits;

        endScore += addRedColumnReward(redBB, yellowBB);

        return endScore;
    }

    public boolean scoreIsWinOrLoss(float score, boolean isMax) {
        return isMax ? score > 50 : score < -50;
    }

    public static void printBoard(long board) {
        String bitsBoard = String.format("%64s", Long.toBinaryString(board)).replace(' ', '0');

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 7; x++) {
                int index = bitsBoard.length() - x - y * 7 - 1;
                // System.out.println(index);
                char charBoard = bitsBoard.charAt(index);
                if (charBoard == '1') {
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
