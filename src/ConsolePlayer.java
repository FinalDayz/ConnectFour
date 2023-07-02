import java.util.Scanner;

import Game.ConnectFour;
import Game.ConnectFourPlayable;
import Game.ConnectFourPlayer;
import Game.State;

public class ConsolePlayer implements ConnectFourPlayer {
	ConnectFour game;
	Scanner in;
	
	@Override
	public void init(ConnectFourPlayable game, boolean redPlayer) {
		this.game = game;
		in = new Scanner(System.in); 
	}

	@Override
	public void makePlay() throws InterruptedException {
		
		// Thread.sleep(1000);
		println("Make a play:");
		
		for(int i = 0; i < game.getWidth(); i++) {
			System.out.print(i + " ");
		}
		System.out.println();
		game.printBoard('O', 'X', '-');
		

		while(!in.hasNextLine()) {
			Thread.sleep(10);
		}
		
        int column = (int)Integer.parseInt(in.nextLine());
        System.out.println("Waiting for oponent...");
		game.makePlay(column);
		
	}
	
	public void println(Object o) {System.out.println(o);}
	public void println() {System.out.println();}
	public void print(Object o) {System.out.print(o);}

	@Override
	public void update() {
	}

	@Override
	public void setLog(boolean log) {

	}
}
