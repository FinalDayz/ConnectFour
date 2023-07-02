package MinMax;

import java.util.ArrayList;
import java.util.Arrays;

import Game.ConnectFour;
import Game.ConnectFourPlayable;
import Game.ConnectFourPlayer;

public class MinMaxPlayer extends MinMax implements ConnectFourPlayer {

	ConnectFour game;
	public int searchDepth;
	public long lastTimeTook = -1;
	public int lastMove;
	
	boolean randomColumnOnEqualChance = true;
	int columnOnEqualChance = -1;
	
	private long maxTimeToTake;
	
	public MinMaxPlayer(int searchDepth, long maxTimeToTake) {
		this.searchDepth = searchDepth;
		this.maxTimeToTake = maxTimeToTake;
	}
	
	public void setColumnOnEqualChance(int column) {
		this.randomColumnOnEqualChance = false;
		this.columnOnEqualChance = column;
	}
	
	public void setRandomColumnOnEqualChance() {
		this.randomColumnOnEqualChance = true;
	}
	
	@Override
	public void init(ConnectFourPlayable game, boolean redPlayer) {
		this.game = game;
	}

	@Override
	public void makePlay() {
		long startTime = System.currentTimeMillis();
		this.executeMove(game, searchDepth, maxTimeToTake);
		int maxScore = Integer.MIN_VALUE;
		
		ArrayList<Integer> scores = new ArrayList<Integer>();
		for(Node node : topNodes) {
			maxScore = Math.max(node.getScore(), maxScore);
			scores.add(node.getScore());
		}
		
		
		ArrayList<Node> maxNodes = new ArrayList<Node>();
		
		for(Node node : topNodes) {
			if(node.getScore() == maxScore) {
				maxNodes.add(node);
			}
		}
		
		System.out.println("Ai moves:");
		System.out.println("\t"+Arrays.toString(scores.toArray()));
		Node chosenNode;
		
		if(randomColumnOnEqualChance)
			chosenNode = maxNodes.get((int) Math.floor(Math.random() * maxNodes.size()));
		else 
			chosenNode = maxNodes.get(columnOnEqualChance);
		
		lastTimeTook = System.currentTimeMillis() - startTime;
		
		println("Search of depth " + searchDepth + " took: " + lastTimeTook + " ms");
		
		lastMove = chosenNode.getMove();
		
		maxNodes.clear();
		chosenNode.clear();
		maxNodes.clear();
		
		this.clearNodes();
		
		
		game.makePlay(lastMove);
	}

	@Override
	public void update() {
	}

	@Override
	public void setLog(boolean log) {

	}
}
