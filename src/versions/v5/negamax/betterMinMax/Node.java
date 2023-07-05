package versions.v5.negamax.betterMinMax;

import Game.ConnectFour;
import versions.v4.row_modifiers.betterMinMax.CacheEntry;

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
    private DoubleHashMap<Long, CacheEntry> cache;

    public Node(ConnectFour game, EvaluationFunction evalFunction, int currentDepth, int move, DoubleHashMap<Long, CacheEntry> cache) {
        this.game = game;
        this.currentDepth = currentDepth;
        this.evalFunction = evalFunction;
        this.moveIndex = move;
        this.cache = cache;
        nodeScore = initScore();
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

    private boolean checkCanUseCache(CacheEntry savedEntry, float alpha, float beta) {
        if(savedEntry.type == CacheEntry.TYPE_EXACT) {
            return true;
        }

        if(savedEntry.type == CacheEntry.TYPE_UPPER_BOUND && savedEntry.value <= alpha) {
            return true;
        }

        if(savedEntry.type == CacheEntry.TYPE_LOWER_BOUND && savedEntry.value >= beta) {
            return true;
        }

        return false;
    }

    public static int player = 0;
    public void negamaxSearch(int maxDepth, float alpha, float beta) {

    }

    public void search(int maxDepth, float alpha, float beta) throws InterruptedException {
        game.executePlay(moveIndex);

        // debugInfo+="\n\nBegin " + this+"\n";

        CacheEntry savedEntry = cache.get(game.getRedBitBoard(), game.getYellowBitBoard());
        if(savedEntry != null) {
            if(checkCanUseCache(savedEntry, alpha, beta)) {
                nodeScore = savedEntry.value;
                game.undoMove(false);
                // debugInfo += "Got a cache hit! Score of cache hit: "+score+
                //     " (cache key: "+game.getRedBitBoard()+","+game.getYellowBitBoard()+")\n";
                return;
            }
        }
        
        boolean doReturn = maxDepth == currentDepth || game.gameState.gameDidEnd();

        if(currentDepth <= 3 && maxTimeStamp < System.currentTimeMillis()) {
            game.undoMove(false);
            return;
        }

        if(doReturn) {
            nodeScore = evalFunction.evaluate(game, currentDepth);
            game.undoMove(false);
            return;
        }
        byte nodeCacheType = CacheEntry.TYPE_UPPER_BOUND;

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
//                if(this.isMaximizing())
                // TODO: set correct one

//                nodeCacheType = CacheEntry.TYPE_LOWER_BOUND;
//                CacheEntry entry = new CacheEntry();
//                entry.value = nodeScore;
//                entry.type = nodeCacheType;
//                cache.put(game.getRedBitBoard(), game.getYellowBitBoard(), entry);

                // debugInfo+= "God alpha beta hit, returning with score " + nodeScore+"\n";
                // nodedPurged += (maxDepth - currentDepth) + 1;

                // cache.put(game.getRedBitBoard(), game.getYellowBitBoard(), this.nodeScore);
                game.undoMove(false);
                return;
            }
            if(foundNewBestMove(nodeScore, alpha, beta)) {
                nodeCacheType = 0;
            }
            if (nodeScore > alpha && !isMaximizing()) {
                nodeCacheType = 0;
            }

            alpha = setAlpha(alpha, nodeScore);
            beta = setBeta(beta, nodeScore);
        }

        if(nodeCacheType == CacheEntry.TYPE_UPPER_BOUND || nodeCacheType == CacheEntry.TYPE_EXACT) {
            CacheEntry entry = new CacheEntry();
            entry.value = nodeScore;
            entry.type = nodeCacheType;
            cache.put(game.getRedBitBoard(), game.getYellowBitBoard(), entry);
        }

        game.undoMove(false);
    }

    private Node[] createNodes() {
        int[] moves = game.getAvailableMoves();
        Node[] nodes = new Node[moves.length];
        int index = 0;
        for(int moveIndex : moves) {
            
            nodes[index] = createOppositeNode(game, evalFunction, currentDepth + 1, moveIndex, cache);
            // debugInfo += "Create node " + nodes[index]+" for move " + moveIndex + "\n";
            if(currentDepth <= 2) {
                nodes[index].setMaxTimeStamp(maxTimeStamp);
            }
            index++;
        }
        return nodes;
    }

    public int getMoveIndex(){
        return moveIndex;
    }

    abstract Node createOppositeNode(ConnectFour game, EvaluationFunction evalFunction, int currentDepth, int hoverMoveIndex, DoubleHashMap<Long, CacheEntry> cache);

    abstract float compareScore(float score1, float score2);

    abstract boolean compareAlphaBeta(float score, float alpha, float beta);
    abstract boolean foundNewBestMove(float score, float alpha, float beta);
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
