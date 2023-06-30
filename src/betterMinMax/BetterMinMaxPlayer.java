package betterMinMax;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import Game.ConnectFour;
import Game.ConnectFourPlayable;
import Game.ConnectFourPlayer;
import MinMax.MinMax;

public class BetterMinMaxPlayer implements ConnectFourPlayer {

	ConnectFour game;
	public int searchDepth;
	
	private long maxTimeToTake;

	private EvaluationFunction evalFunction;

    public static Node[] TOP_NODES = null;
	private Node[] topNodes = null;

	public boolean useCache = true;
	
	public BetterMinMaxPlayer(int searchDepth, long maxTimeToTake) {
		this.searchDepth = searchDepth;
		this.maxTimeToTake = maxTimeToTake;
	}

	void evaluatePosition() throws InterruptedException {
		DoubleHashMap<Long, Float> cache = new DoubleHashMap<>();
		if(!useCache) {
			cache = new DoubleFakeHashMap<>();
		}
		long timestampReturn = System.currentTimeMillis() + maxTimeToTake;

		int[] moves = game.getAvailableMoves();
		System.out.println(game.getAvailableMoves().length);
		topNodes = new Node[moves.length];
		int index = 0;
		for(int nodeMove : moves) {
			topNodes[index] = new MinNode(game, evalFunction, 1, nodeMove, cache);
			topNodes[index].setMaxTimeStamp(timestampReturn);
			index++;
		}

		TOP_NODES = topNodes;

		for(int currentMaxDepth = 1; currentMaxDepth <= searchDepth; currentMaxDepth+=1) {
			Node.nodedPurged = 0;
			cache.clear();
			// int nodeI = 3;
			// int nodeI2 = 2;
			// if(topNodes[nodeI].childNodes != null && topNodes[nodeI].childNodes[nodeI2].childNodes != null) {
			// 	System.out.println("Top nodes before depth " + currentMaxDepth);
			// 	for(Node node : topNodes[nodeI].childNodes[nodeI2].childNodes) {
			// 		System.out.print(node.getMoveIndex()+"\t");
			// 	}
			// 	System.out.println();
			// 	for(Node node : topNodes[nodeI].childNodes[nodeI2].childNodes) {
			// 		int score = node.nodeScore == Integer.MAX_VALUE ? 9999 : node.nodeScore; 
			// 		score = score == Integer.MIN_VALUE ? -9999 : score; 
			// 		System.out.print(score+"\t");
			// 	}
			// 	System.out.println();
			// }

			System.out.println("Searching depth " + currentMaxDepth);

			Node.sortNodes(topNodes);

			searchNodes(topNodes, currentMaxDepth);

			
			Node bestNode = determaneBestNodes(topNodes).get(0);
			if(evalFunction.scoreIsWinOrLoss(bestNode.nodeScore, true)) {
				return;
			}
		

			if(timestampReturn < System.currentTimeMillis()) {
				System.out.println("Quitting because out of time");
				return;
			}
		}
		System.out.println("Node.nodedPurged "+Node.nodedPurged);
    }

    void searchNodes(Node[] nodes, int depth) throws InterruptedException {

        for(Node node : nodes) {
            node.search(depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
			if(evalFunction.scoreIsWinOrLoss(node.nodeScore, node.isMaximizing())) {
                return;
            }
        }
    }

    @Override
    public void init(ConnectFourPlayable game, boolean redPlayer) {
		this.game = game;
		this.evalFunction = new SmartEvaluationFunction(redPlayer);
		// this.evalFunction = new SimpleDepthEvaluationFunction(redPlayer);
    }

    @Override
    public void makePlay() throws InterruptedException {
		long startTime = System.currentTimeMillis();
		evaluatePosition();

		List<Node> bestNodes = determaneBestNodes(topNodes);

		Node randomBestMove = chooseRandomNode(bestNodes);

		Arrays.sort(topNodes, (a, b) -> a.moveIndex-b.moveIndex);

		System.out.println("[betterMinMax] values of nodes:");
		System.out.print("\t [");
		for(Node node : topNodes) {
			System.out.print(node.nodeScore +", ");
		}
		System.out.print("]");
		System.out.println();

		System.out.println("BetterMinMax]: That took " + (System.currentTimeMillis() - startTime)+"ms");

		// debugNodes(topNodes);

		game.makePlay(randomBestMove.getMoveIndex());
    }

	private void debugNodes(Node[] nodes) throws InterruptedException {
		ConnectFour gameToDebug = game.copy();
		Scanner in = new Scanner(System.in); 
		System.out.println("[DEBUG MODE ACTIVATES, ENTER 'q' TO EXIT, NODE INDEX NUMBER TO GO DEEPER AND -1 TO GO UP, 99 for node info]");
		Thread.sleep(1000);
		int currentDepth = 0;

		Stack<Node[]> parentNodes = new Stack<>();
		Stack<Node> lastChosenNodes = new Stack<>();
		boolean toMaximize = true;
		Node lastChosenNode = null;
		
		while(true){
			System.out.println("===============");
			for(int i = 0; i < game.getWidth(); i++) {
				System.out.print(i + " ");
			}
			System.out.println();
			game.printBoard();
			System.out.println(
				(game.isRedTurn() ? "\t[Its reds move]" : "\t[Its yellows move]") +
				(toMaximize ? " tries to maximize" : " tries to minumize") + 
				(lastChosenNode != null ? (", So score of this row is " + strScore(lastChosenNode.nodeScore)) : "") +
				(lastChosenNode != null ? (lastChosenNode.isMaximizing() ? "(max)" : "(min)") : "")
			);
			System.out.println("Nodes in depth " + currentDepth);
			for(Node node : nodes) {
				System.out.print(node.getMoveIndex()+"\t");
			}
			System.out.println();
			for(Node node : nodes) {
				float score = node.nodeScore == Integer.MAX_VALUE ? 9999 : node.nodeScore; 
				score = score == Integer.MIN_VALUE ? -9999 : score; 
				System.out.print(strScore(score)+"\t");
			}

			System.out.println();
			System.out.println("Enter action:");

			while(!in.hasNextLine()) {
				Thread.sleep(10);
			}

			
			int moveToInvestigate = (int) Integer.parseInt(in.nextLine());
			if(moveToInvestigate == -1) {
				toMaximize = !toMaximize;
				System.out.println("Move up...");
				nodes = parentNodes.pop();
				game.undoMove(false);

				if(lastChosenNodes.empty()) {
					lastChosenNode = null;
				} else {
					lastChosenNode = lastChosenNodes.pop();
				}
			}

			if(moveToInvestigate == 99) {
				System.out.println("[INFO OF NODE]: " + lastChosenNode.debugInfo);
			}

			for(Node node : nodes) {
				if(node.moveIndex == moveToInvestigate) {
					if(node.childNodes == null) {
						System.out.println("Node has no children");
						lastChosenNode = node;
						toMaximize = !toMaximize;
						parentNodes.add(nodes);
						nodes = new Node[]{};
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

	private String strScore(float score) {
		return ""+Math.round(score*1000)/1000.0;
	}

	private Node chooseRandomNode(List<Node> bestNodes) {
		int randIndex = (int) Math.floor(Math.random() * bestNodes.size());
        return bestNodes.get(randIndex);
    }

    private List<Node> determaneBestNodes(Node[] parentNoded) {
		
		LinkedList<Node> bestMoves = new LinkedList<>();

		float maxScore = Integer.MIN_VALUE;
		for(Node node : topNodes) {
			maxScore = Math.max(node.nodeScore, maxScore);
		}

		for(Node node : topNodes) {
			if(node.nodeScore == maxScore) {
				bestMoves.add(node);
			}
		}

		return bestMoves;
	}

	@Override
	public void update() {}

    
    
}
