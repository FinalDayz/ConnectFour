package MinMax;


import java.util.Arrays;

import Game.ConnectFour;
import Util.CustomHashMap;

public class MinMax {
	
	private ConnectFour game;
	public Node[] topNodes;
	
	static int NODE_COUNTER = 0;
	
	private CustomHashMap<Long, MinMaxResult> table;
	
	public MinMax() {
		
	}
	
	protected void clearNodes() {
		System.out.println("Clear: "+game.moveHistory);
		for(Node node : topNodes) {
			node.clear();
		}
		table = new CustomHashMap<Long, MinMaxResult>(2);
		topNodes = null;
	}
	
	public void executeMove(ConnectFour game, int depth, long maxTimeToTake) {
		
		this.game = game;
		table = new CustomHashMap<Long, MinMaxResult>(2);
		
		Long timeToEnd = System.currentTimeMillis() + maxTimeToTake;
		println("SKIPPP??? " + maxTimeToTake);
		//if(topNodes == null)
			createTopNodes(timeToEnd);
		//else
		//	getTopNodes();
			
		//for each depth, search every node
		boolean foundSolution = false;
		for(int searchDepth = 1; searchDepth <= depth; searchDepth++) {
			NODE_COUNTER = 0;
			println("search " + searchDepth);
			for(Node node : this.topNodes) {
				//FALSE WANT MAX GELD VOOR VOLGENDE LAAG
				if(node.getScore() == 2 || node.getScore() == -2) {
					continue;
				}
				
				int value = node.search(searchDepth);
				if(value == 2) {
					foundSolution = true;
				}
			}
			
			if(foundSolution || System.currentTimeMillis() > timeToEnd) {
				println("SKIPPP OUTTA TIME2 " + System.currentTimeMillis() +" > "+ timeToEnd);
				break;
			}

		}
		
		println("NODES: " + NODE_COUNTER);
	
		
	}
	
	private void getTopNodes() {
		
	}
	
	private void createTopNodes(Long timeToEnd) {
		int[] topmoves = game.getAvailableMoves();
		topNodes = new Node[topmoves.length];
		for(int index = 0; index < topmoves.length; index++) {
			topNodes[index] = new Node(game, 1, topmoves[index], false, game.redTurn, table);
			topNodes[index].setStopTime(timeToEnd);
		}
	}
	
	
	public void println(Object o) {System.out.println(o);}
	public void println() {System.out.println();}
	public void print(Object o) {System.out.print(o);}
}
