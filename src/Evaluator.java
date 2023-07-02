import Game.ConnectFourPlayable;
import Game.ConnectFourPlayer;
import Game.BotPlayer;
import MinMax.MinMaxPlayer;
import ui.ConnectFourViewer;
import ui.ViewerConfig;

import java.util.Arrays;

public class Evaluator {
    private int games;

    public Evaluator(int games) {
        this.games = games;
    }

    /*
    DEFAULT, (v1) versions.base.betterMinMax.BetterMinMaxPlayer vs (v1) versions.base.betterMinMax.BetterMinMaxPlayer
    Win: 469   Lose: 460  Draw: 71      w-l: 9

    (v2) addRedColumnReward vs (v1) base      {0.1f, 0.07f, 0.2f, 0.4f, 0.2f, 0.07f, 0.1f};
                (7 depth: 63% win )

    (v3)  vs (v2) addRedColumnReward


578 Wins for betterMinMax.BetterMinMaxPlayer
357 Wins for versions.column_reward.betterMinMax.BetterMinMaxPlayer
65 draws


========================================================================
 V  | new algo          || vs base              || vs prev ver.
====|===================|===================|============================
 V1 | base              | 50%                   |  -                    |
 V2 | Column reward     | 63%                   |  -                    |
 V3 | Row Patterns      |                       | 62%  w:578 l:357 d:65 |
    |                   |                       |                       |
    |                   |                       |                       |
    |                   |                       |                       |

V1: base, MinMax+alpha beta. Also, three in row + two in row reward.
V2: Give reward for having more pieces of you in the center
V3: Give reward for making three in a row in their row (red/yellow)

     */
    public void evaluate(BotPlayer player1, BotPlayer player2) {
        player1.setLog(false);
        player2.setLog(false);
        player1.setCanMakeRandomMove(false);
        player2.setCanMakeRandomMove(false);
        String p1Name = player1.getClass().getCanonicalName();
        String p2Name = player2.getClass().getCanonicalName();

        System.out.println(p1Name + " vs " + p2Name);
        System.out.println("=== FIGHT! ===");
        int p1Wins = 0;
        int p2Wins = 0;
        int draws = 0;

        int p1Moves = 0, p2Moves = 0;
        long p1MoveTime = 0, p2MoveTime = 0;

//        ConnectFourViewer viewer = new ConnectFourViewer(new ViewerConfig());
        int len = Math.min(games, randGames.length);
        for (int i = 0; i < len; i++) {
            System.out.println("Starting round " + (i + 1) + "/" + (len - 1));
            int[] gameBeginning = randGames[i];
            EvaluationConnectFour game = new EvaluationConnectFour(player1, player2);
            game.executeSet(gameBeginning);

//            viewer.viewGame(game);
            game.begin();

            if (game.gameState.gameDidDraw()) draws++;
            if (game.gameState.redDidWon()) p1Wins++;
            if (game.gameState.yellowDidWon()) p2Wins++;

            p1Moves += game.getPlayerMoves(player1);
            p1MoveTime += player1.getTotalMoveTimeTaken();
            p2Moves += game.getPlayerMoves(player2);
            p2MoveTime += player2.getTotalMoveTimeTaken();

//            System.out.println("game.moveHistory "+game.moveHistory);
//            System.out.println("p1: " + p1Wins+", p2: "+p2Wins+" draw: " + draws);

            game = new EvaluationConnectFour(player2, player1);
            game.executeSet(gameBeginning);

//            viewer.viewGame(game);
            game.begin();

            if (game.gameState.gameDidDraw()) draws++;
            if (game.gameState.redDidWon()) p2Wins++;
            if (game.gameState.yellowDidWon()) p1Wins++;

            p1Moves += game.getPlayerMoves(player1);
            p1MoveTime += player1.getTotalMoveTimeTaken();
            p2Moves += game.getPlayerMoves(player2);
            p2MoveTime += player2.getTotalMoveTimeTaken();

            System.out.println("game.moveHistory " + game.moveHistory);
            System.out.println("p1: " + p1Wins + ", p2: " + p2Wins + " draw: " + draws + " p1Win%: " + Math.round(p1Wins * 100 / (p1Wins + p2Wins)) + "%");

//            System.out.println("avg p1 ms per move: " + (p1MoveTime*1.0 / p1Moves));
//            System.out.println("avg p2 ms per move: " + (p2MoveTime*1.0 / p2Moves));
        }

        System.out.println("===== RESULTS =====");
        System.out.println(p1Wins + " Wins for " + p1Name);
        System.out.println(p2Wins + " Wins for " + p2Name);
        System.out.println(draws + " draws");
    }

    public static void makeRandGames(int amount, int moves) {
        Integer[][] randGames = new Integer[amount][];
        for (int i = 0; i < amount; i++) {
            randGames[i] = generateRandGame(moves);
        }

        for (Integer[] game : randGames) {
            System.out.println(
                    Arrays.toString(game)
                            .replace('[', '{')
                            .replace(']', '}') +
                            ",");
        }
    }

    private static Integer[] generateRandGame(int moves) {
        MinMaxPlayer randPlayer1 = new MinMaxPlayer(4, 200);
        MinMaxPlayer randPlayer2 = new MinMaxPlayer(4, 200);
        randPlayer1.setLog(false);
        randPlayer2.setLog(false);
        EvaluationConnectFour game = new EvaluationConnectFour(randPlayer1, randPlayer2, moves);

        game.begin();

        System.out.println(game.moveHistory);

        return game.moveHistory.toArray(new Integer[]{});
    }

    static class EvaluationConnectFour extends ConnectFourPlayable {
        private final int maxMoves;

        public EvaluationConnectFour(ConnectFourPlayer redPlayer, ConnectFourPlayer yellowPlayer) {
            this(redPlayer, yellowPlayer, 45);
        }

        public EvaluationConnectFour(ConnectFourPlayer redPlayer, ConnectFourPlayer yellowPlayer, int moves) {
            super(redPlayer, yellowPlayer);
            this.maxMoves = moves;

            log = false;
        }

        @Override
        public void makePlay(int columnIndex) {
            this.executePlay(columnIndex);
            updateWatchers();

            if (this.moveHistory.size() >= maxMoves) {
                return;
            }

            giveTurn();
        }
    }

    private final static int[][] randGames = new int[][]{
            {},
            {0},
            {1},
            {2},
            {3},
            {4},
            {5},
            {6},
            {4, 0, 1},
            {3, 4, 3},
            {0, 3, 6},
            {6, 0, 2},
            {5, 0, 3},
            {5, 3, 1},
            {6, 3, 0},
            {3, 5, 2},
            {5, 0, 0},
            {4, 6, 5},
            {6, 2, 1},
            {6, 1, 4},
            {5, 0, 4},
            {3, 5, 4},
            {6, 0, 5},
            {3, 3, 0},
            {6, 3, 1},
            {5, 5, 6},
            {3, 2, 4},
            {2, 2, 1},
            {4, 0, 2},
            {5, 6, 6},
            {0, 5, 1},
            {2, 3, 2},
            {0, 4, 2},
            {6, 3, 0},
            {0, 2, 4},
            {3, 5, 1},
            {4, 0, 6},
            {0, 6, 2},
            {5, 5, 6},
            {1, 6, 6},
            {1, 2, 1},
            {1, 1, 5},
            {1, 5, 5},
            {1, 2, 2},
            {6, 3, 5},
            {5, 3, 6},
            {4, 4, 5},
            {6, 0, 5},
            {6, 3, 3},
            {4, 6, 6},
            {0, 4, 0},
            {0, 4, 2},
            {6, 5, 4},
            {4, 6, 2},
            {1, 4, 4},
            {1, 1, 6},
            {2, 3, 0},
            {5, 5, 6},
            {1, 6, 1, 1},
            {4, 2, 5, 4},
            {0, 3, 3, 0},
            {0, 2, 4, 4},
            {2, 2, 5, 1},
            {6, 5, 0, 6},
            {1, 3, 1, 3},
            {6, 2, 6, 2},
            {4, 6, 6, 6},
            {2, 5, 5, 6},
            {6, 1, 6, 1},
            {6, 1, 4, 2},
            {1, 1, 2, 3},
            {3, 3, 6, 0},
            {1, 3, 0, 6},
            {2, 6, 5, 1},
            {4, 5, 5, 4},
            {3, 5, 4, 3},
            {2, 5, 0, 4},
            {3, 5, 6, 6},
            {3, 0, 5, 6},
            {2, 6, 3, 4},
            {4, 0, 6, 4},
            {0, 6, 4, 2},
            {5, 3, 5, 6},
            {1, 0, 3, 4},
            {4, 6, 6, 3},
            {2, 4, 6, 2},
            {5, 3, 5, 4},
            {6, 6, 5, 1},
            {3, 3, 0, 6},
            {6, 1, 3, 0},
            {3, 6, 5, 0},
            {2, 3, 0, 1},
            {2, 3, 2, 4},
            {5, 4, 1, 3},
            {2, 4, 0, 1},
            {0, 0, 1, 3},
            {4, 0, 3, 2},
            {3, 2, 2, 1},
            {3, 0, 4, 2},
            {1, 5, 1, 0},
            {6, 2, 6, 4},
            {1, 0, 6, 4},
            {2, 4, 2, 0},
            {0, 4, 3, 1},
            {3, 2, 6, 6},
            {3, 4, 2, 4},
            {1, 6, 6, 4},
            {5, 0, 1, 5},
            {4, 1, 4, 0},
            {6, 3, 6, 3},
            {2, 1, 2, 3},
            {5, 2, 4, 2},
            {6, 1, 1, 1},
            {2, 2, 1, 3},
            {6, 3, 6, 5},
            {0, 0, 6, 3},
            {2, 2, 3, 4},
            {1, 1, 6, 6},
            {2, 5, 0, 4},
            {2, 0, 4, 5},
            {4, 4, 6, 0},
            {4, 2, 6, 2},
            {5, 5, 0, 5},
            {1, 0, 0, 0},
            {5, 1, 5, 0},
            {4, 6, 1, 0},
            {6, 6, 4, 2},
            {1, 3, 3, 1},
            {5, 1, 1, 4},
            {4, 3, 0, 6},
            {6, 6, 2, 3},
            {3, 3, 6, 1},
            {5, 6, 1, 1},
            {0, 1, 3, 2},
            {0, 5, 5, 1},
            {1, 1, 3, 2},
            {2, 5, 4, 2},
            {2, 1, 3, 1},
            {6, 2, 4, 3},
            {1, 6, 4, 5},
            {5, 1, 2, 1},
            {5, 2, 3, 1},
            {2, 3, 0, 2},
            {1, 1, 0, 2},
            {2, 4, 5, 1},
            {4, 1, 5, 2},
            {2, 4, 0, 1},
            {2, 0, 2, 5},
            {1, 6, 3, 4},
            {6, 6, 6, 6},
            {5, 2, 3, 6},
            {6, 1, 2, 3},
            {2, 2, 3, 1},
            {6, 4, 4, 4},
            {1, 6, 5, 1},
            {2, 1, 1, 2},
            {3, 5, 3, 3},
            {1, 1, 3, 2},
            {1, 6, 1, 1},
            {5, 2, 1, 6, 3},
            {6, 1, 2, 6, 2},
            {5, 3, 6, 1, 2},
            {3, 3, 3, 0, 3},
            {1, 3, 2, 4, 2},
            {2, 4, 3, 6, 3},
            {0, 0, 3, 6, 0},
            {2, 0, 1, 6, 1},
            {3, 6, 6, 0, 3},
            {6, 5, 0, 5, 1},
            {0, 3, 3, 0, 0},
            {2, 3, 0, 2, 1},
            {6, 6, 6, 2, 0},
            {0, 3, 0, 4, 5},
            {2, 5, 2, 0, 6},
            {2, 1, 5, 0, 6},
            {6, 2, 4, 0, 1},
            {1, 4, 5, 3, 6},
            {0, 1, 3, 6, 4},
            {1, 5, 4, 2, 2},
            {3, 6, 1, 4, 5},
            {4, 4, 6, 5, 0},
            {5, 5, 1, 6, 2},
            {5, 1, 6, 0, 4},
            {1, 5, 1, 3, 2},
            {3, 5, 1, 0, 2},
            {0, 2, 5, 1, 6},
            {2, 6, 3, 1, 2},
            {6, 1, 5, 3, 2},
            {4, 5, 6, 0, 0},
            {5, 2, 6, 5, 1},
            {0, 3, 3, 5, 2},
            {4, 3, 6, 0, 5},
            {5, 5, 1, 3, 5},
            {5, 5, 1, 2, 4},
            {1, 5, 0, 3, 2},
            {4, 1, 2, 1, 5},
            {3, 2, 6, 6, 4},
            {6, 4, 1, 3, 5},
            {2, 6, 4, 3, 2},
            {4, 0, 4, 6, 2},
            {0, 0, 6, 4, 1},
            {0, 0, 0, 6, 1},
            {2, 2, 4, 3, 4},
            {2, 1, 3, 4, 1},
            {6, 4, 1, 6, 4},
            {4, 5, 4, 1, 3},
            {4, 5, 1, 3, 6},
            {1, 1, 6, 0, 1},
            {5, 6, 1, 5, 1},
            {0, 4, 5, 4, 2},
            {1, 3, 4, 6, 0},
            {2, 2, 0, 0, 1},
            {5, 0, 6, 3, 3},
            {2, 2, 6, 5, 5},
            {5, 5, 3, 4, 5},
            {2, 3, 4, 5, 6},
            {5, 6, 4, 0, 6},
            {5, 5, 4, 3, 0},
            {3, 1, 4, 5, 0},
            {0, 6, 2, 0, 3},
            {1, 1, 6, 4, 4},
            {5, 4, 3, 5, 0},
            {4, 1, 3, 6, 5},
            {4, 0, 2, 3, 3},
            {3, 4, 6, 6, 5},
            {6, 2, 3, 3, 4},
            {6, 5, 4, 5, 0},
            {1, 3, 2, 3, 5},
            {1, 2, 4, 0, 0},
            {2, 4, 1, 2, 1},
            {6, 0, 2, 6, 4},
            {6, 0, 3, 3, 2},
            {6, 4, 6, 2, 5},
            {3, 3, 1, 4, 0},
            {0, 5, 2, 6, 3},
            {5, 6, 4, 6, 0},
            {3, 3, 6, 0, 0},
            {6, 6, 1, 6, 3},
            {1, 2, 5, 3, 6},
            {4, 4, 0, 6, 4},
            {2, 0, 5, 6, 0},
            {3, 3, 3, 3, 1},
            {5, 2, 5, 1, 4},
            {0, 5, 6, 0, 3},
            {0, 1, 6, 5, 5},
            {4, 5, 2, 0, 6},
            {3, 1, 1, 6, 5},
            {4, 0, 3, 5, 6},
            {3, 4, 2, 1, 2},
            {1, 2, 4, 6, 2},
            {0, 2, 3, 4, 1},
            {0, 6, 6, 4, 5},
            {3, 0, 5, 4, 6},
            {5, 2, 3, 3, 4},
            {4, 2, 6, 2, 5},
            {3, 2, 1, 3, 3},
            {4, 6, 4, 1, 5},
            {4, 1, 4, 0, 2},
            {3, 1, 0, 0, 2},
            {1, 0, 5, 5, 2},
            {1, 5, 4, 5, 0},
            {3, 0, 3, 3, 3},
            {0, 5, 4, 4, 5},
            {0, 2, 4, 0, 0},
            {3, 4, 4, 5, 5},
            {3, 6, 3, 5, 4},
            {3, 2, 2, 6, 0},
            {4, 3, 2, 6, 0},
            {2, 6, 3, 4, 6},
            {4, 2, 2, 1, 6},
            {6, 4, 3, 0, 2},
            {1, 3, 4, 3, 0},
            {5, 3, 5, 0, 0},
            {5, 6, 1, 5, 6},
            {5, 0, 3, 2, 5},
            {5, 1, 3, 6, 0},
            {3, 5, 4, 1, 5},
            {3, 5, 0, 4, 0},
            {2, 3, 6, 2, 2},
            {5, 1, 6, 0, 0},
            {1, 1, 5, 5, 5},
            {4, 0, 6, 1, 6},
            {5, 4, 4, 2, 6},
            {1, 6, 2, 4, 1},
            {5, 0, 1, 4, 5},
            {2, 5, 1, 4, 1},
            {3, 1, 1, 2, 4},
            {6, 2, 3, 5, 5},
            {2, 1, 4, 2, 2},
            {4, 4, 3, 2, 0},
            {4, 5, 1, 5, 1},
            {4, 0, 4, 3, 2},
            {4, 1, 0, 4, 3},
            {4, 0, 5, 2, 2},
            {0, 3, 5, 3, 1},
            {2, 4, 6, 1, 0},
            {3, 6, 6, 1, 5},
            {0, 0, 6, 4, 2},
            {4, 2, 6, 1, 2},
            {0, 6, 4, 5, 6},
            {6, 0, 3, 4, 4},
            {6, 4, 0, 1, 1},
            {6, 6, 5, 6, 4},
            {2, 3, 5, 4, 4},
            {3, 0, 0, 2, 6},
            {1, 5, 4, 2, 3},
            {6, 0, 4, 1, 2},
            {6, 4, 0, 1, 3},
            {5, 3, 3, 6, 2},
            {5, 2, 3, 3, 6, 4},
            {1, 6, 6, 5, 1, 2},
            {5, 0, 6, 4, 3, 2},
            {1, 5, 4, 4, 2, 3},
            {3, 4, 0, 2, 5, 0},
            {6, 5, 3, 6, 2, 0},
            {5, 5, 4, 3, 5, 6},
            {5, 6, 5, 3, 1, 2},
            {4, 6, 4, 5, 2, 5},
            {0, 3, 1, 2, 3, 6},
            {0, 2, 1, 4, 0, 6},
            {0, 2, 5, 6, 1, 5},
            {4, 1, 0, 5, 5, 4},
            {1, 5, 2, 0, 5, 3},
            {5, 4, 4, 1, 0, 2},
            {0, 1, 4, 0, 3, 6},
            {3, 2, 5, 5, 4, 6},
            {5, 5, 0, 0, 3, 4},
            {1, 6, 4, 6, 2, 3},
            {4, 3, 5, 1, 1, 0},
            {5, 5, 2, 6, 6, 3},
            {5, 3, 0, 6, 4, 4},
            {3, 6, 5, 4, 0, 5},
            {1, 4, 0, 0, 2, 3},
            {5, 0, 4, 6, 5, 6},
            {1, 0, 6, 5, 3, 2},
            {1, 5, 1, 5, 0, 4},
            {5, 4, 1, 1, 5, 3},
            {5, 5, 5, 2, 3, 6},
            {0, 5, 0, 5, 0, 0},
            {0, 0, 2, 4, 3, 1},
            {2, 4, 2, 0, 4, 2},
            {3, 3, 0, 0, 2, 1},
            {5, 4, 1, 0, 4, 1},
            {1, 0, 0, 1, 5, 5},
            {2, 0, 2, 1, 3, 3},
            {6, 2, 0, 2, 5, 6},
            {6, 5, 5, 4, 3, 0},
            {1, 1, 1, 6, 5, 5},
            {3, 4, 1, 0, 4, 3},
            {0, 5, 1, 0, 6, 0},
            {1, 1, 2, 3, 4, 3},
            {6, 5, 0, 4, 5, 1},
            {1, 4, 2, 4, 5, 2},
            {2, 2, 5, 1, 3, 4},
            {1, 2, 1, 5, 5, 0},
            {5, 1, 6, 5, 2, 3},
            {3, 0, 1, 0, 6, 2},
            {6, 1, 0, 6, 3, 3},
            {0, 6, 2, 3, 3, 3},
            {4, 2, 4, 1, 5, 1},
            {0, 6, 0, 6, 6, 2},
            {6, 2, 1, 2, 6, 2},
            {0, 6, 2, 3, 0, 1},
            {5, 3, 5, 1, 5, 5},
            {4, 5, 0, 2, 6, 5},
            {3, 2, 0, 4, 3, 4},
            {0, 6, 5, 3, 0, 4},
            {2, 3, 3, 2, 6, 6},
            {6, 4, 2, 3, 4, 6},
            {2, 4, 5, 5, 5, 1},
            {1, 0, 1, 3, 5, 6},
            {2, 4, 3, 4, 5, 5},
            {3, 6, 4, 1, 4, 6},
            {3, 3, 1, 2, 6, 0},
            {4, 6, 2, 1, 3, 5},
            {4, 2, 5, 6, 4, 3},
            {1, 5, 6, 1, 6, 5},
            {2, 5, 4, 5, 3, 1},
            {1, 5, 1, 2, 3, 4},
            {1, 0, 5, 4, 2, 4},
            {5, 5, 6, 5, 6, 6},
            {2, 2, 1, 4, 4, 2},
            {2, 2, 1, 3, 0, 0},
            {2, 2, 2, 3, 6, 4},
            {3, 0, 4, 2, 4, 4},
            {3, 2, 5, 6, 2, 5},
            {4, 2, 6, 0, 3, 5},
            {5, 0, 4, 6, 0, 3},
            {6, 6, 0, 0, 6, 4},
            {3, 2, 3, 0, 0, 2},
            {6, 2, 2, 5, 5, 2},
            {5, 6, 2, 2, 4, 3},
            {5, 0, 0, 0, 0, 6},
            {0, 6, 5, 2, 4, 1},
            {2, 6, 4, 1, 2, 1},
            {3, 4, 2, 2, 5, 1},
            {4, 1, 1, 6, 6, 2},
            {2, 0, 1, 4, 5, 1},
            {1, 1, 4, 6, 0, 5},
            {4, 2, 3, 4, 6, 5},
            {3, 3, 2, 1, 1, 0},
            {3, 4, 1, 1, 0, 2},
            {3, 5, 1, 2, 4, 4},
            {0, 1, 6, 6, 6, 3},
            {5, 1, 6, 6, 2, 1},
            {5, 5, 0, 3, 0, 5},
            {4, 5, 0, 3, 4, 3},
            {2, 0, 2, 4, 6, 0},
            {4, 1, 3, 2, 6, 5},
            {5, 2, 3, 3, 6, 4},
            {0, 4, 6, 2, 3, 3},
            {0, 6, 2, 2, 4, 5},
            {2, 3, 3, 4, 5, 2},
            {4, 3, 1, 4, 2, 0},
            {2, 0, 6, 4, 2, 1},
            {4, 1, 1, 0, 3, 5},
            {3, 5, 2, 1, 2, 5},
            {1, 3, 3, 0, 6, 1},
            {1, 2, 3, 3, 3, 6},
            {3, 1, 6, 5, 6, 5},
            {4, 2, 5, 0, 5, 0},
            {5, 6, 3, 2, 0, 6},
            {6, 5, 6, 4, 0, 5},
            {2, 6, 0, 1, 4, 0},
            {4, 5, 3, 1, 3, 1},
            {1, 3, 0, 3, 2, 0},
            {2, 0, 4, 1, 1, 0},
            {2, 1, 2, 6, 6, 6},
            {3, 1, 6, 4, 4, 3},
            {3, 1, 2, 1, 0, 0},
            {4, 5, 4, 5, 1, 5},
            {5, 0, 4, 6, 1, 6},
            {5, 6, 1, 5, 5, 2},
            {2, 4, 5, 0, 1, 3},
            {6, 6, 3, 1, 3, 2},
            {0, 4, 5, 1, 4, 1},
            {3, 0, 4, 5, 2, 1},
            {4, 6, 6, 0, 1, 6},
            {4, 0, 3, 5, 6, 4},
            {0, 1, 3, 5, 4, 6},
            {6, 1, 6, 2, 0, 0},
            {3, 5, 4, 5, 5, 6},
            {1, 6, 3, 4, 0, 2},
            {1, 0, 6, 6, 1, 0},
            {2, 0, 3, 5, 0, 6},
            {1, 4, 0, 3, 6, 6},
            {5, 5, 4, 2, 0, 6},
            {0, 0, 6, 5, 1, 5},
            {0, 4, 1, 0, 3, 2},
            {0, 6, 4, 3, 5, 5},
            {3, 4, 4, 4, 6, 3},
            {2, 2, 2, 3, 2, 4},
            {2, 1, 4, 5, 5, 3},
            {2, 3, 2, 4, 3, 1},
            {3, 4, 4, 0, 6, 5},
            {6, 4, 0, 3, 2, 6},
            {4, 2, 1, 5, 1, 1},
            {3, 5, 6, 1, 0, 3},
            {6, 4, 3, 4, 3, 4},
            {6, 5, 0, 3, 0, 3},
            {5, 3, 5, 5, 2, 0},
            {1, 2, 5, 6, 3, 5},
            {2, 2, 5, 5, 4, 3},
            {4, 5, 2, 4, 2, 6},
            {0, 5, 2, 5, 1, 3},
            {5, 6, 0, 4, 1, 5},
            {3, 6, 4, 1, 3, 2},
            {3, 0, 0, 0, 2, 1},
            {6, 3, 3, 0, 6, 4},
            {2, 3, 4, 2, 5, 5},
            {4, 6, 1, 5, 0, 0},
            {3, 1, 3, 5, 4, 4},
            {6, 2, 2, 4, 5, 0},
            {1, 6, 6, 6, 3, 2},
            {4, 3, 3, 0, 3, 2},
            {1, 1, 2, 3, 5, 2},
            {6, 1, 3, 3, 4, 5},
            {5, 4, 5, 6, 5, 5},
            {3, 5, 6, 2, 4, 2},
            {2, 1, 1, 2, 2, 4},
            {5, 3, 3, 5, 3, 1},
            {5, 6, 0, 5, 1, 3},
            {4, 1, 4, 0, 5, 3},
            {4, 6, 3, 1, 3, 3},
            {5, 0, 0, 0, 1, 4},
            {6, 4, 4, 3, 2, 5},
            {0, 6, 2, 3, 5, 1},
            {6, 0, 5, 3, 1, 0},
            {0, 1, 4, 2, 6, 1},
            {6, 5, 4, 6, 2, 4},
            {3, 3, 4, 5, 1, 2},
            {3, 1, 5, 2, 4, 6},
            {3, 1, 3, 5, 4, 3},
            {5, 5, 0, 2, 4, 5},
            {0, 0, 0, 0, 4, 0},
            {5, 3, 2, 3, 6, 4},
            {6, 5, 5, 3, 5, 0},
            {6, 0, 6, 0, 3, 2},
            {4, 0, 5, 2, 1, 6},
            {1, 3, 0, 6, 1, 1},
    };
}
