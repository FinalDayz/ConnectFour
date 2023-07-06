package betterMinMax;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import Game.ConnectFour;
import Game.ConnectFourPlayable;
import Game.BotPlayer;
import versions.v4.row_modifiers.betterMinMax.CacheEntry;

public class BetterMinMaxPlayer implements BotPlayer {

    ConnectFour game;
    public int searchDepth;

    private long maxTimeToTake;

    private EvaluationFunction evalFunction;

    public static NegamaxNode[] TOP_NODES = null;
    private NegamaxNode[] topNodes = null;

    public boolean useCache = true;
    private boolean log = true;
    private boolean randMove = true;
    private long lastMoveMs = 0;
    private long totalTimeTaken = 0;
    private boolean isRed;

    public BetterMinMaxPlayer(int searchDepth, long maxTimeToTake) {
        this.searchDepth = searchDepth;
        this.maxTimeToTake = maxTimeToTake;
    }

    @Override
    public void init(ConnectFourPlayable game, boolean redPlayer) {
        this.game = game;
        this.evalFunction = new SmartEvaluationFunction();
//        this.evalFunction = new SimpleDepthEvaluationFunction(true);
        this.totalTimeTaken = 0;
        this.isRed = redPlayer;
    }

    public BetterMinMaxPlayer() {}
    public void setMaxTimeToTake(long time) {
        this.maxTimeToTake = time;
    }
    public void setMaxDepth(int depth) {
        this.searchDepth = depth;
    }

    public static int lastCompletedDepth = 0;
    void evaluatePosition() throws InterruptedException {
        DoubleHashMap<Long, CacheEntry> cache = new DoubleHashMap<>();
        if (!useCache) {
            cache = new DoubleFakeHashMap<>();
        }
        long timestampReturn = System.currentTimeMillis() + maxTimeToTake;

        int[] moves = game.getAvailableMoves();
        println(game.getAvailableMoves().length);
        topNodes = new NegamaxNode[moves.length];
        int index = 0;
        for (int nodeMove : moves) {
            topNodes[index] = new NegamaxNode(game, evalFunction, 1, nodeMove, cache, isRed ? -1 : 1);
            topNodes[index].setMaxTimeStamp(timestampReturn);
            index++;
        }

        TOP_NODES = topNodes;

        for (int currentMaxDepth = 1; currentMaxDepth <= searchDepth; currentMaxDepth += 1) {
            cache.clear();

            println("Searching depth " + currentMaxDepth);
            NegamaxNode.sortNodes(topNodes);

            searchNodes(topNodes, currentMaxDepth);

            if (timestampReturn >= System.currentTimeMillis()) {
                lastCompletedDepth = currentMaxDepth;
            }

            NegamaxNode bestNode = determaneBestNodes(topNodes).get(0);
            if (evalFunction.scoreIsWinOrLoss(bestNode.getReverseScore(), true)) {
                return;
            }

            if (timestampReturn < System.currentTimeMillis()) {
                println("Quitting because out of time");
                return;
            }
        }

    }

    void searchNodes(NegamaxNode[] nodes, int depth) throws InterruptedException {

        for (NegamaxNode node : nodes) {
            node.search(depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (evalFunction.scoreIsWinOrLoss(node.getReverseScore(), true)) {
                return;
            }
        }
    }

    @Override
    public void makePlay() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        evaluatePosition();

        List<NegamaxNode> bestNodes = determaneBestNodes(topNodes);

//        debugNodes(topNodes);

        lastMoveMs = System.currentTimeMillis() - startTime;
        totalTimeTaken += lastMoveMs;

        if (log) {
            Arrays.sort(topNodes, (a, b) -> a.moveIndex - b.moveIndex);

            println("[betterMinMax] values of nodes:");
            print("\t [");
            for (NegamaxNode node : topNodes) {
                print(node.getReverseScore() + ", ");
            }
            print("]");
            println();
            println("BetterMinMax]: That took " + (System.currentTimeMillis() - startTime) + " ms");
        }

        if (!randMove) {
            // Make sure nothing is up to randomes
            if(bestNodes.size() > 1) {
                bestNodes.sort((a, b) -> a.moveIndex - b.moveIndex);
            }
            game.makePlay(bestNodes.get(0).moveIndex);
            return;
        }

        NegamaxNode randomBestMove = chooseRandomNode(bestNodes);

        game.makePlay(randomBestMove.getMoveIndex());
    }

    private void debugNodes(NegamaxNode[] nodes) throws InterruptedException {
        ConnectFour gameToDebug = game.copy();
        Scanner in = new Scanner(System.in);
        System.out.println("[DEBUG MODE ACTIVATES, ENTER 'q' TO EXIT, NODE INDEX NUMBER TO GO DEEPER AND -1 TO GO UP, 99 for node info]");
        Thread.sleep(1000);
        int currentDepth = 0;

        Stack<NegamaxNode[]> parentNodes = new Stack<>();
        Stack<NegamaxNode> lastChosenNodes = new Stack<>();
        boolean toMaximize = true;
        NegamaxNode lastChosenNode = null;

        while (true) {
            System.out.println("===============");
            for (int i = 0; i < game.getWidth(); i++) {
                System.out.print(i + " ");
            }
            System.out.println();
            game.printBoard();
            System.out.println(
                    (game.isRedTurn() ? "\t[Its reds move]" : "\t[Its yellows move]") +
                            (toMaximize ? " tries to maximize" : " tries to minumize") +
                            (lastChosenNode != null ? (", So score of this row is " + strScore(lastChosenNode.getReverseScore())) : "") +
                            (lastChosenNode != null ? (lastChosenNode.getPlayer() == 1 ? "(max)" : "(min)") : "")
            );
            System.out.println("\tPlayers value: (1/-1): " +
                    (lastChosenNode == null ? (isRed ? 1 : -1) : lastChosenNode.player)
            );
            System.out.println("NegamaxNode in depth " + currentDepth);
            for (NegamaxNode node : nodes) {
                System.out.print(node.getMoveIndex() + "\t");
            }
            System.out.println();
            for (NegamaxNode node : nodes) {
                float score = node.getReverseScore() == Integer.MAX_VALUE ? 9999 : node.getReverseScore();
                score = score == Integer.MIN_VALUE ? -9999 : score;
                System.out.print(strScore(score) + "\t");
            }

            System.out.println();
            System.out.println("Enter action:");

            while (!in.hasNextLine()) {
                Thread.sleep(10);
            }

            int moveToInvestigate = (int) Integer.parseInt(in.nextLine());
            if (moveToInvestigate == -1) {
                toMaximize = !toMaximize;
                System.out.println("Move up...");
                nodes = parentNodes.pop();
                game.undoMove(false);

                if (lastChosenNodes.empty()) {
                    lastChosenNode = null;
                } else {
                    lastChosenNode = lastChosenNodes.pop();
                }
            }

            if (moveToInvestigate == 99) {
                System.out.println("[INFO OF NODE]: " + lastChosenNode.debugInfo);
            }

            if (moveToInvestigate == 100) {
                System.out.println("[exiting...]: ");
                break;
            }

            for (NegamaxNode node : nodes) {
                if (node.moveIndex == moveToInvestigate) {
                    if (node.childNodes == null) {
                        System.out.println("Node has no children");
                        lastChosenNode = node;
                        toMaximize = !toMaximize;
                        parentNodes.add(nodes);
                        nodes = new NegamaxNode[]{};
                        currentDepth++;
                        System.out.println("Selected node " + node.moveIndex);
                        game.executePlay(node.moveIndex);
                        break;
                    }
                    lastChosenNodes.add(lastChosenNode);

                    lastChosenNode = node;
                    toMaximize = !toMaximize;
                    parentNodes.add(nodes);
                    nodes = node.childNodes;
                    currentDepth++;
                    System.out.println("Selected node " + node.moveIndex);
                    game.executePlay(node.moveIndex);
                }
            }
        }

    }

    public void println(Object o) {
        if (log) System.out.println(o);
    }

    public void println() {
        if (log) System.out.println();
    }

    public void print(Object o) {
        if (log) System.out.print(o);
    }

    private String strScore(float score) {
        return "" + Math.round(score * 1000) / 1000.0;
    }

    private NegamaxNode chooseRandomNode(List<NegamaxNode> bestNodes) {
        int randIndex = (int) Math.floor(Math.random() * bestNodes.size());
        return bestNodes.get(randIndex);
    }

    private List<NegamaxNode> determaneBestNodes(NegamaxNode[] parentNoded) {

        LinkedList<NegamaxNode> bestMoves = new LinkedList<>();

        float maxScore = Integer.MIN_VALUE;
        for (NegamaxNode node : topNodes) {
            maxScore = Math.max(node.getReverseScore(), maxScore);
        }

        for (NegamaxNode node : topNodes) {
            if (node.getReverseScore() == maxScore) {
                bestMoves.add(node);
            }
        }

        return bestMoves;
    }

    @Override
    public void update() {
    }

    @Override
    public void setLog(boolean log) {
        this.log = log;
    }

    @Override
    public void setCanMakeRandomMove(boolean randMove) {
        this.randMove = randMove;
    }

    @Override
    public long getTotalMoveTimeTaken() {
        return totalTimeTaken;
    }
}
