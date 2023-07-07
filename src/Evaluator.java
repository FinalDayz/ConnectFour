import Game.ConnectFourPlayable;
import Game.ConnectFourPlayer;
import Game.BotPlayer;
import MinMax.MinMaxPlayer;
import betterMinMax.BetterMinMaxPlayer;
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


==========================================================================
 V  | new algo           || vs base              || vs prev ver.
====|====================|========================|=============================
 V1 | base               | 49%  w:452 l:470 d:78  |  -                     |
 V2 | Column reward      | 54%  w:496 l:426 d:78  |  -                     |
 V3 | Row Patterns       | 63%  w:593 l:339 d:68  | 53.5% w:496 l:427 d:77 |
 V4 | Row modifiers      | 61%  w:578 l:358 d:64  | 60%  w:566 l:365 d:69  |
    |                    |                        |                        |
    |                    |                        |                        |

V1: base, MinMax+alpha beta. Also, three in row + two in row reward.
V2: Give reward for having more pieces of you in the center
V3: Give reward for making three in a row in their row (red/yellow)

     */
    public void evaluate(int[] depths, long timeToTake, BotPlayer player1, BotPlayer player2) {
        player1.setLog(false);
        player2.setLog(false);
        player1.setCanMakeRandomMove(false);
        player2.setCanMakeRandomMove(false);
        String p1Name = player1.getClass().getCanonicalName();
        String p2Name = player2.getClass().getCanonicalName();

        System.out.println(p1Name + " vs " + p2Name);
        System.out.println("=== FIGHT! ===");

        EvaluateResult totalResult = new EvaluateResult();

        player1.setMaxTimeToTake(timeToTake);
        player2.setMaxTimeToTake(timeToTake);

        for(int depth : depths) {
            int logEvery = 2000;
            player1.setMaxDepth(depth);
            player2.setMaxDepth(depth);

            System.out.println("Starting for depth: " + depth);
            EvaluateResult result = evaluatePlayers(player1, player2, logEvery);
            totalResult.addFrom(result);
            System.out.println("=========================");
            System.out.println("Completed depth " + depth);
            System.out.println("Total for now:");


            System.out.println(
                    "p1: " + totalResult.p1Wins + ", p2: " + totalResult.p2Wins + " draw: " + totalResult.draws +
                            " p1Win%: " + totalResult.calcWinPercentage() + "%"

            );
            System.out.println("\n");
        }

        System.out.println("\n\n==================== RESULTS =======================");
        System.out.println(totalResult.p1Wins + " Wins for " + p1Name);
        System.out.println(totalResult.p2Wins + " Wins for " + p2Name);
        System.out.println(totalResult.draws + " draws");
        System.out.println("PERCENTAGR FOR Player 1: " + totalResult.calcWinPercentage() + "%");
        System.out.println("avg Player 1 move time: " + totalResult.p1AvgMoveTime());
        System.out.println("avg Player 2 move time: " + totalResult.p2AvgMoveTime());
    }

    public static boolean EVALUATE_CHOICE = true;
    public void evaluateSpeed(BetterMinMaxPlayer newestBetterMinMaxPlayer, int depth) {
        newestBetterMinMaxPlayer.setMaxDepth(depth);
        newestBetterMinMaxPlayer.setMaxTimeToTake(5000);

        for(int i = 0; i < 3/*randGames.length*/; i++) {
            int[] gameBeginning = randGames[i];
            for (boolean abChoice : new boolean[]{true, false}) {
                EVALUATE_CHOICE = abChoice;
                EvaluationConnectFour game = new EvaluationConnectFour(
                        newestBetterMinMaxPlayer,
                        newestBetterMinMaxPlayer,
                        1
                );
                game.executeSet(gameBeginning);
                game.begin();
                System.out.println("finished with "+i);
            }
        }
    }

    private EvaluateResult evaluatePlayers(BotPlayer player1, BotPlayer player2, int logEveryMs) {
        EvaluateResult result = new EvaluateResult();

        int len = Math.min(games, randGames.length);
        long lastLog = 0;
        for (int i = 0; i < len; i++) {
            boolean log = lastLog+logEveryMs <= System.currentTimeMillis();
//            log = true;
            if (log) {
                lastLog = System.currentTimeMillis();
                System.out.print("round " + (i + 1) + "/" + (len - 1) + "] ");
            }

            int[] gameBeginning = randGames[i];

            for (boolean p1IsRed : new boolean[]{true, false}) {
                EvaluationConnectFour game = new EvaluationConnectFour(
                        p1IsRed ? player1 : player2,
                        p1IsRed ? player2 : player1
                );
                game.executeSet(gameBeginning);
                game.begin();

                result.draws += game.gameState.gameDidDraw() ? 1 : 0;
                result.p1Wins += game.playerDidWon(player1) ? 1 : 0;
                result.p2Wins += game.playerDidWon(player2) ? 1 : 0;

                result.p1Moves += game.getPlayerMoves(player1);
                result.p1MoveTime += player1.getTotalMoveTimeTaken();
                result.p2Moves += game.getPlayerMoves(player2);
                result.p2MoveTime += player2.getTotalMoveTimeTaken();

//                System.out.println(result.calcWinPercentage());
//                System.out.println(game.moveHistory);
            }

            if (log)
                System.out.println(
                        "p1: " + result.p1Wins + ", p2: " + result.p2Wins + " draw: " + result.draws +
                                " p1Win%: " + result.calcWinPercentage() + "%"

                );

        }

        return result;
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

    class EvaluateResult {
        public int p1Wins = 0;
        public int p2Wins = 0;
        public int draws = 0;

        public int p1Moves = 0, p2Moves = 0;
        public long p1MoveTime = 0, p2MoveTime = 0;

        public float calcWinPercentage() {
            return calcWinPercentage(p1Wins, p2Wins, draws);
        }

        public void addFrom(EvaluateResult other) {
            this.p1Wins += other.p1Wins;
            this.p2Wins += other.p2Wins;
            this.draws += other.draws;
            this.p1Moves += other.p1Moves;
            this.p2Moves += other.p2Moves;
            this.p1MoveTime += other.p1MoveTime;
            this.p2MoveTime += other.p2MoveTime;
        }

        public static float calcWinPercentage(float p1Win, float p2Wins, float draws) {
            float totalGames = p1Win + p2Wins + draws;
            return
                    Math.round(
                            (p1Win + draws * 0.5) / totalGames * 1000
                    ) / 10.0f;
        }

        private static float avgMoveTime(long moveTime, int moves) {
            return Math.round(moveTime/(moves*1.0f)*100)/100.0f;
        }
        public float p1AvgMoveTime() {
            return avgMoveTime(p1MoveTime, p1Moves);
        }
        public float p2AvgMoveTime() {
            return avgMoveTime(p2MoveTime, p2Moves);
        }
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
