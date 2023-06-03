package test;

import Game.ConnectFour;

public class ConnectFourTest {
	public ConnectFourTest() {
		
	}
	
	public void executeTests() {
		testHorizontal();
		testVertical();
		testDiagonal1();
		testDiagonal2();
	}
	
	public void testHorizontal() {
		ConnectFour game = new ConnectFour(7, 6, false);
		game.disableLog();
		game.makePlay(0);
		game.makePlay(0);
		
		game.makePlay(1);
		game.makePlay(1);

		game.makePlay(2);
		game.makePlay(2);

		this.equals(false, game.gameState.yellowDidWon());
		this.equals(false, game.gameState.redDidWon());
		
		game.makePlay(3);
		
		this.equals(false, game.gameState.yellowDidWon());
		this.equals(true, game.gameState.redDidWon());
	}
	
	public void testDiagonal2() {
		ConnectFour game = new ConnectFour(7, 6, false);
		game.disableLog();
		game.makePlay(5);
		game.makePlay(4);
		
		game.makePlay(4);
		game.makePlay(2);
		
		game.makePlay(3);
		game.makePlay(3);
		
		game.makePlay(3);
		game.makePlay(2);
		
		game.makePlay(1);
		game.makePlay(2);
		
		this.equals(false, game.gameState.yellowDidWon());
		this.equals(false, game.gameState.redDidWon());
		
		game.makePlay(2);
		
		this.equals(false, game.gameState.yellowDidWon());
		this.equals(true, game.gameState.redDidWon());
	}
	
	
	
	public void testDiagonal1() {
		ConnectFour game = new ConnectFour(7, 6, false);
		game.disableLog();
		game.makePlay(1);
		game.makePlay(2);
		
		game.makePlay(2);
		game.makePlay(4);
		
		game.makePlay(3);
		game.makePlay(3);
		
		game.makePlay(3);
		game.makePlay(4);
		
		game.makePlay(5);
		game.makePlay(4);
		
		this.equals(false, game.gameState.yellowDidWon());
		this.equals(false, game.gameState.redDidWon());
		
		game.makePlay(4);
		
		this.equals(false, game.gameState.yellowDidWon());
		this.equals(true, game.gameState.redDidWon());
	}
	
	public void testVertical() {
		ConnectFour game = new ConnectFour(7, 6, false);
		game.disableLog();
		
		game.makePlay(2);
		game.makePlay(1);
		
		game.makePlay(2);
		game.makePlay(1);
		
		
		game.makePlay(1);
		game.makePlay(2);
		
		game.makePlay(1);
		game.makePlay(2);
		
		game.makePlay(1);
		game.makePlay(2);
		
		this.equals(false, game.gameState.yellowDidWon());
		this.equals(false, game.gameState.redDidWon());
		
		game.makePlay(1);
		
		this.equals(false, game.gameState.yellowDidWon());
		this.equals(true, game.gameState.redDidWon());
		
		game = new ConnectFour(7, 6, false);
		game.disableLog();
		
		game.makePlay(1);
		game.makePlay(2);
		
		game.makePlay(1);
		game.makePlay(2);
		
		game.makePlay(1);
		game.makePlay(2);
		
		game.makePlay(3);
		
		this.equals(false, game.gameState.yellowDidWon());
		this.equals(false, game.gameState.redDidWon());
		
		game.makePlay(2);
		

		this.equals(true, game.gameState.yellowDidWon());
		this.equals(false, game.gameState.redDidWon());

	}
	
	public void equals(boolean x, boolean y) {
		if(x != y) {
			throw new IllegalArgumentException("x not equals y");
		}
	}
	
	public void equals(double x, double y) {
		if(x != y) {
			throw new IllegalArgumentException("x not equals y");
		}
	}
	
	public void equals(int x, int y) {
		if(x != y) {
			throw new IllegalArgumentException("x not equals y");
		}
	}
}
