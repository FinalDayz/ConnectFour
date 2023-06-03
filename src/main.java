import java.util.Arrays;

import Game.ConnectFour;
import Game.ConnectFourPlayable;
import MinMax.MinMax;
import MinMax.MinMaxPlayer;
import Util.CustomHashMap;
import test.ConnectFourTest;
import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

public class main {
	public static void main(String[] args) {
		ConnectFourTest test = new ConnectFourTest();
		test.executeTests();
		
		
		MinMaxPlayer minMaxPlayer = new MinMaxPlayer(12, 10000);
		//minMaxPlayer.setColumnOnEqualChance(0);
		//minMaxPlayer.setTimeLimit(5000);
		ConsolePlayer human = new ConsolePlayer();
	//	GraphicsPlayer humanPlayer = new GraphicsPlayer();
	
		
		ConnectFourPlayable game = new ConnectFourPlayable(minMaxPlayer, human);
		//easy 1
		//game.executeSet(3, 6, 5, 6, 5, 5, 3, 1);
		
		
		//complex
		//game.executeSet(3, 6, 2, 6, 3, 3);
		
		//easy 2
		//game.executeSet(1, 6, 2, 6, 3, 3);
		

		game.setBeginPlayer(minMaxPlayer);
		
		game.printBoard();
		
		game.begin();
	}
	

	public static void println(int[] o) {System.out.println(Arrays.toString(o));}
	public static void println(Object o) {System.out.println(o);}
	public static void println() {System.out.println();}
	public static void print(Object o) {System.out.print(o);}
	
	
}
