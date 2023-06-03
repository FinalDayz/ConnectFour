package MinMax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import Game.ConnectFour;
import Util.CustomHashMap;

public class Node implements Comparable<Node> {
	
	private int score;
	private int move;
	
	private int thisDepth;
	private List<Node> childNodes;
	private ConnectFour game;
	private boolean isRed, max;
	
	private boolean DEBUG = true;
	
	private boolean deadEnd = false;
	public String historyMove = "";
	
	public String debug = "--";
	
	private Long stopTime = -1l;
	private CustomHashMap<Long, MinMaxResult> table;
	
	public Node(ConnectFour game, int depth, int move, boolean max, boolean isRed, CustomHashMap<Long, MinMaxResult> table) {
		MinMax.NODE_COUNTER++;
		thisDepth = depth;
		this.game = game;
		this.move = move;
		this.isRed = isRed;
		this.max = max;
		this.table = table;
		historyMove = move + ",";
	}
	
	public int search(int toDepth) {
		return this.search(toDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	public int search(int toDepth, int alpha, int beta) {
		if(deadEnd)
			return score;
		game.executePlay(move);
		
		if(max) {
			score = Integer.MIN_VALUE;
		} else {
			score = Integer.MAX_VALUE;
		}

		if(thisDepth >= toDepth || game.gameState.gameDidEnd()) {
			score = evaluate();
//			if(thisDepth < 3)
//				println(debug+ " Move " + move +": " + score  + ", " + ( max ? "max" : "min"));
			
			game.undoMove(false);
			return score;
		}
		
		if(table.contains(game.getRedBitBoard(), game.getYellowBitBoard())) {
			MinMaxResult savesResult = table.getValue(game.getRedBitBoard(), game.getYellowBitBoard());
			if(savesResult.depth == this.thisDepth &&
					savesResult.maxDepth == toDepth) {
				this.score = savesResult.score;
				game.undoMove(false);
				return score;
			}
		}
		
		
		//only create new nodes if there aren't ones already
		if(childNodes == null || childNodes.size() == 0) {
			createNodes();
		} else {
			sortNodes();
		}
		
//		println(debug + " Move " + move + ", " + ( max ? "max" : "min"));
		for(Node node : childNodes) {
			int nodeScore;
			
			if(stopTime == -1 || System.currentTimeMillis() < stopTime) {
				nodeScore = node.search(toDepth, alpha, beta);
			} else {
				println("SKIPPP OUTTA TIME");
				nodeScore = node.score;
			}
			
			int bestResult = max ? 2 : -2;
			//this means that it has lost or won
			if(nodeScore == bestResult) {
				score = nodeScore;
				saveResult(toDepth);
				game.undoMove(false);
				
				deadEnd = true;
				return score;
			}
			
			if(max) {
				score = Math.max(score, nodeScore);
				alpha = Math.max(alpha, score);
				if(alpha > beta) {
					saveResult(toDepth);
					game.undoMove(false);
					return score;
				}
			} else {
				score = Math.min(score, nodeScore);
				beta = Math.min(beta, score);
				if(alpha > beta) {
					saveResult(toDepth);
					game.undoMove(false);
					return score;
				}
			}
		}
//		println(debug + " << score: " + score+" ("+Arrays.toString(scores)+")");
		saveResult(toDepth);
		game.undoMove(false);

		return score;
	}
	
	private void saveResult(int toDepth) {
		if(table.contains(game.getRedBitBoard(), game.getYellowBitBoard())) {
			MinMaxResult result = table.getValue(game.getRedBitBoard(), game.getYellowBitBoard());
			if(result.score != this.score && result.maxDepth == toDepth) {
				System.out.println("SCORE DOES NOT EQUAL OOPS "
						+ "score&depth: " + this.score + " & " + this.thisDepth
						+" their score&depth: " + result.score+" & " + result.depth);
			}
			return;
		}
		
		MinMaxResult result = new MinMaxResult();
		result.score = this.score;
		result.depth = this.thisDepth;
		result.maxDepth = toDepth;
		
		table.setValue(result, game.getRedBitBoard(), game.getYellowBitBoard());
	}
	
	private void sortNodes() {
		Collections.sort(this.childNodes, Collections.reverseOrder());
	}
	
	private void createNodes() {
		int[] topmoves = game.getAvailableMoves();
		childNodes = new ArrayList<Node>();
		for(int index = 0; index < topmoves.length; index++) {
			Node newNode = new Node(game, thisDepth + 1, topmoves[index], !max, isRed, table);
			newNode.debug += " " + debug;
			newNode.historyMove = historyMove + newNode.historyMove;
			childNodes.add(newNode);
		}
		
	}
	
	public int getScore() {
		return this.score;
	}
	
	public int getMove() {
		return this.move;
	}
	
	public String toString() {
		return "mv: " + move +" sc: " + score;
	}
	
	
	public int evaluate() {
		if(game.gameState.gameDidEnd()) {
			
			if(game.gameState.gameDidDraw()) {
				return -1;
			}
			
			if(game.gameState.redDidWon() == isRed) {
				//won
				return 2;
			} else {
				//lost
				return -2;
			}
		}
		
		return 0;
	}
	
	public void println(Object o) {System.out.println(o);}
	public void println() {System.out.println();}
	public void print(Object o) {System.out.print(o);}

	@Override
	public int compareTo(Node secondNode) {
		return Integer.compare(this.score, secondNode.score);
	}

	public void setStopTime(Long timeToEnd) {
		this.stopTime = timeToEnd;
	}

	public void clear() {
		if(childNodes == null)
			return;
		for(Node node : childNodes) {
			node.clear();
			node = null;
		}
		this.childNodes.clear();
		this.childNodes = null;
	}

	
}
