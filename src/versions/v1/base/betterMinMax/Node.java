package versions.v1.base.betterMinMax;

import Game.ConnectFour;

import java.util.Arrays;

public abstract class Node implements Comparable<Node> {

    public float nodeScore;
    Node[] childNodes = null;
    private final ConnectFour game;
    private final int currentDepth;
    private final EvaluationFunction evalFunction;
    public int moveIndex;

    static int nodedPurged = 0;
    public String debugInfo = "";
    private long maxTimeStamp;
    private DoubleHashMap<Long, Float> cache;

    public Node(ConnectFour game, EvaluationFunction evalFunction, int currentDepth, int move, DoubleHashMap<Long, Float> cache) {
        this.game = game;
        this.currentDepth = currentDepth;
        this.evalFunction = evalFunction;
        this.moveIndex = move;
        this.cache = cache;
    }

    static void sortNodes(Node[] nodesToSort) {
        Arrays.sort(
                nodesToSort,
                (a, b) -> a.compareTo(b)
        );
    }

    protected int compareTo(Node other, boolean reverse) {

        if(reverse) {
            return Float.compare(other.nodeScore, nodeScore);
        } else {
            return Float.compare(nodeScore, other.nodeScore);
        }
    }

    abstract int initScore();

    public void search(int maxDepth, float alpha, float beta) throws InterruptedException {
        game.executePlay(moveIndex);

        // debugInfo+="\n\nBegin " + this+"\n";

        Float score = cache.get(game.getRedBitBoard(), game.getYellowBitBoard());
        if(score != null) {
            // debugInfo += "Got a cache hit! Score of cache hit: "+score+
            //     " (cache key: "+game.getRedBitBoard()+","+game.getYellowBitBoard()+")\n";
            this.nodeScore = score;
            game.undoMove(false);
            return;
        }

        boolean doReturn = maxDepth == currentDepth || game.gameState.gameDidEnd();

        if(currentDepth <= 5 && maxTimeStamp != 0 && maxTimeStamp < System.currentTimeMillis()) {
            game.undoMove(false);
            return;
        }

        if(doReturn) {
            nodeScore = evalFunction.evaluate(game, currentDepth);
            game.undoMove(false);
            return;
        }

        nodeScore = initScore();

        if(childNodes == null) {
            childNodes = createNodes();
        } else {
            sortNodes(childNodes);
        }

        for(Node node : childNodes) {

            node.search(maxDepth, alpha, beta);
            // debugInfo+= "Searched child node (mv:"+node.moveIndex+"), got a value of "+node.nodeScore+" from child. own value is: " + nodeScore+"\n";
            this.nodeScore = compareScore(node.nodeScore, nodeScore);
            // if(evalFunction.scoreIsWinOrLoss(nodeScore, isMaximizing())) {
            //     game.undoMove(false);
            //     return;
            // }

            if(compareAlphaBeta(this.nodeScore, alpha, beta)) {
                // debugInfo+= "God alpha beta hit, returning with score " + nodeScore+"\n";
                // nodedPurged += (maxDepth - currentDepth) + 1;

                // cache.put(game.getRedBitBoard(), game.getYellowBitBoard(), this.nodeScore);
                game.undoMove(false);
                return;
            }
            alpha = setAlpha(alpha, nodeScore);
            beta = setBeta(beta, nodeScore);
        }

        cache.put(game.getRedBitBoard(), game.getYellowBitBoard(), this.nodeScore);

        game.undoMove(false);
    }

    private Node[] createNodes() {
        int[] moves = game.getAvailableMoves();
        Node[] nodes = new Node[moves.length];
        int index = 0;
        for(int moveIndex : moves) {

            nodes[index] = createOppositeNode(game, evalFunction, currentDepth + 1, moveIndex, cache);
            // debugInfo += "Create node " + nodes[index]+" for move " + moveIndex + "\n";

            if(currentDepth <= 4  && maxTimeStamp != 0) {
                nodes[index].setMaxTimeStamp(maxTimeStamp);
            }
            index++;
        }
        return nodes;
    }

    public int getMoveIndex(){
        return moveIndex;
    }

    abstract Node createOppositeNode(ConnectFour game, EvaluationFunction evalFunction, int currentDepth, int hoverMoveIndex, DoubleHashMap<Long, Float> cache);

    abstract float compareScore(float score1, float score2);

    abstract boolean compareAlphaBeta(float score, float alpha, float beta);
    abstract float setAlpha(float alpha, float score);
    abstract float setBeta(float beta, float score);
    abstract boolean isMaximizing();

    public Node[] getChildNodes() {
        return childNodes;
    }

    abstract public int compareTo(Node other);

    public void setMaxTimeStamp(long maxTimeStamp) {
        this.maxTimeStamp = maxTimeStamp;
    }
}
