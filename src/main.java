import java.util.Arrays;

import Game.ConnectFour;
import Game.ConnectFourPlayable;
import MinMax.MinMax;
import MinMax.MinMaxPlayer;
import Util.CustomHashMap;
import Util.Log;
import betterMinMax.BetterMinMaxPlayer;
import test.ConnectFourTest;
import test.DoubleHashMapTest;
import ui.ConnectFourViewer;
import ui.ViewerConfig;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

public class main {

	public static void main(String[] args) throws InterruptedException {
		ConnectFourTest test = new ConnectFourTest();
		test.executeTests();
		(new DoubleHashMapTest()).executeTests();
		
		
		// MinMaxPlayer minMaxPlayer2 = new MinMaxPlayer(25,  8000);
		BetterMinMaxPlayer minMaxPlayer = new BetterMinMaxPlayer(99, 3000);
		minMaxPlayer.useCache = false;
		//minMaxPlayer.setColumnOnEqualChance(0);
		//minMaxPlayer.setTimeLimit(5000);
		// ConsolePlayer human = new ConsolePlayer();
		GraphicsPlayer human = new GraphicsPlayer();
		
		//human is yellow, starts 2nd, bot starts
		ConnectFourPlayable game = new ConnectFourPlayable(minMaxPlayer, human);//game.executeSet(3);
		//human is Red, starts first, bot is 2nd
		// ConnectFourPlayable game = new ConnectFourPlayable(human, minMaxPlayer);

		// Bot vs Bot
		// ConnectFourPlayable game = new ConnectFourPlayable(minMaxPlayer2, minMaxPlayer);

		// ConnectFourViewer viewer = new ConnectFourViewer(game, new ViewerConfig());
		// game.setMinTimePerMove(2000);
		// viewer.viewGame(game);

		//5276313436535
		// game.executeSet(0,2,0,0,5,1,3,5,2,5,0,0,2,5);
		//easy 1
		// game.executeSet(3, 6, 5, 6, 5, 5, 3, 1);
		
		
		//complex
		// game.executeSet(3, 6, 2, 6, 3, 3);
		
		//easy 2
		// game.executeSet(1, 6, 2, 6, 3, 3);

		// Red will lose, but it can stall a bit
		// game.executeSet(2, 3, 2, 3, 0, 3, 3, 2, 0, 4, 5, 2, 0, 0, 3, 4, 4, 2);


		// Yellow loses in 5 moves
		// game.executeSet(3, 0, 3, 6, 4, 1, 4);
		// game.setBeginPlayer(minMaxPlayer);


		// Log.LOG = true;
		// ConnectFour.checkDiagonal2Win(game.getRedBitBoard());

		// game.setBeginPlayer(minMaxPlayer);
		
		game.printBoard();
		
		game.begin();


		// int[] moves = new int[]{2, 1, 3, 6, 1, 3, 0, 2, 4, 5, 4, 3, 3, 2, 1, 1, 3, 2, 2, 1, 6, 1, 3, 0, 6, 6, 2, 6, 6, 0, 5, 4, 4, 5};
		// for(int move : moves) {
		// 	System.out.println(move);
		// 	game.executePlay(move);
		// 	Thread.sleep(1000);
		// }

	}
	

	public static void println(int[] o) {System.out.println(Arrays.toString(o));}
	public static void println(Object o) {System.out.println(o);}
	public static void println() {System.out.println();}
	public static void print(Object o) {System.out.print(o);}
	
}
