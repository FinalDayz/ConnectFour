package betterMinMax;

import Game.ConnectFour;
import ui.BitBoardViewer;


public class Patterns {

    static long FULL_BOARD = ConnectFour.FULL_BOARD;
    static long RED_ADVANTAGE_ROWS = 0b111111100000001111111000000011111110000000L;
    static long YELLOW_ADVANTAGE_ROWS = (~RED_ADVANTAGE_ROWS) & FULL_BOARD;

    static long applyLeftDescendingDiagonal(long bitBoard) {
        bitBoard |= bitBoard << (ConnectFour.WIDTH - 1) |
                bitBoard << (ConnectFour.WIDTH - 1) * 2 |
                bitBoard << (ConnectFour.WIDTH - 1) * 3;

        return bitBoard;
    }

    static long applyRightDescendingDiagonal(long bitBoard) {
        bitBoard |= bitBoard << (ConnectFour.WIDTH + 1) |
                bitBoard << (ConnectFour.WIDTH + 1) * 2 |
                bitBoard << (ConnectFour.WIDTH + 1) * 3;

        return bitBoard;
    }

    static long applyHorizontal(long bitBoard) {
        bitBoard |= bitBoard >> 1 |
                bitBoard >> 2 |
                bitBoard >> 3;

        return bitBoard;
    }

    public static long applyVertical(long bitBoard) {
        bitBoard |= bitBoard << ConnectFour.WIDTH |
                bitBoard << ConnectFour.WIDTH * 2 |
                bitBoard << ConnectFour.WIDTH * 3;

        return bitBoard;
    }

    static interface ApplyPattern {
        long applyPattern(long bitBoard);
    }
}

