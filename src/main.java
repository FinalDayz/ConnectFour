import java.util.Arrays;

import Game.ConnectFourPlayable;
import betterMinMax.BetterMinMaxPlayer;
import test.ConnectFourTest;
import test.DoubleHashMapTest;

public class main {

	public static void testFloatMultplSpeed() {
		float playerInFloat = -1;
		short playerInShort = -1;
		int playerInInt = -1;
		int randNums = 200_000_000;
		float[] numsToMultiplyF = new float[randNums];
		float[] numsToMultiplyS = new float[randNums];
		float[] numsToMultiplyI = new float[randNums];

		for(int i = 0; i < randNums; i++) {
			numsToMultiplyF[i] = (float) Math.random() * 100 - 50;
			numsToMultiplyS[i] = numsToMultiplyF[i];
			numsToMultiplyI[i] = numsToMultiplyF[i];
		}

		long startIntTime = System.currentTimeMillis();
		for(int i = 0; i < numsToMultiplyI.length; i++) {
			numsToMultiplyI[i] = numsToMultiplyI[i] * -1;
		}
		long tookInt = System.currentTimeMillis() - startIntTime;

		long startShortTime = System.currentTimeMillis();
		for(int i = 0; i < numsToMultiplyS.length; i++) {
			numsToMultiplyS[i] = numsToMultiplyS[i] * playerInShort;
		}
		long tookShort = System.currentTimeMillis() - startShortTime;

		long startFloatTime = System.currentTimeMillis();
		for(int i = 0; i < numsToMultiplyF.length; i++) {
			numsToMultiplyF[i] = numsToMultiplyF[i] * playerInFloat;
		}
		long tookFloat = System.currentTimeMillis() - startFloatTime;


		System.out.println("tookFloat: " + tookFloat);
		System.out.println("tookShort: " + tookShort);
		System.out.println("tookDynamic: " + tookInt);
	}

	public static void main(String[] args) throws InterruptedException {
		ConnectFourTest test = new ConnectFourTest();
		test.executeTests();
		(new DoubleHashMapTest()).executeTests();

//		testFloatMultplSpeed();

		System.out.println("Row patterns vs Column reward, 100ms");
		Evaluator evaluator = new Evaluator(500);
		BetterMinMaxPlayer newestBetterMinMaxPlayer = new BetterMinMaxPlayer();
		newestBetterMinMaxPlayer.useCache = false;

		evaluator.evaluate(
				new int[]{27},
				100,
				newestBetterMinMaxPlayer,
				new versions.v4.row_modifiers.betterMinMax.BetterMinMaxPlayer()
//				new versions.v3.row_pattern_reward.betterMinMax.BetterMinMaxPlayer()
//				new versions.v2.column_reward.betterMinMax.BetterMinMaxPlayer()
//				new versions.v1.base.betterMinMax.BetterMinMaxPlayer(7, 1000)
		);

		System.exit(0);

//		versions.v4.row_modifiers.betterMinMax.BetterMinMaxPlayer minMaxPlayer = new versions.v4.row_modifiers.betterMinMax.BetterMinMaxPlayer(10, 32000);
		BetterMinMaxPlayer minMaxPlayer = new BetterMinMaxPlayer(10, 25000);
		minMaxPlayer.setCanMakeRandomMove(false);
		minMaxPlayer.useCache = false;
		//minMaxPlayer.setColumnOnEqualChance(0);
		//minMaxPlayer.setTimeLimit(5000);
		// ConsolePlayer human = new ConsolePlayer();
		GraphicsPlayer human = new GraphicsPlayer();
		
		//human is yellow, starts 2nd, bot starts
		ConnectFourPlayable game = new ConnectFourPlayable(minMaxPlayer, human);//game.executeSet(3);
		//human is Red, starts first, bot is 2nd
//		 ConnectFourPlayable game = new ConnectFourPlayable(human, minMaxPlayer);

//		game.executeSet(6, 2, 1, 3, 5, 3, 3, 2, 1, 1, 3, 2, 2, 1, 4, 4, 5, 4, 4, 1, 1, 3, 2, 3, 2, 4, 4, 0, 0, 6, 6, 6, 0);
//		game.setBeginPlayer(minMaxPlayer);

//		ConnectFourPlayable game = new ConnectFourPlayable(human, human);

		// Bot vs Bot
		// ConnectFourPlayable game = new ConnectFourPlayable(minMaxPlayer2, minMaxPlayer);

//		 ConnectFourViewer viewer = new ConnectFourViewer(new ViewerConfig());
//		 game.setMinTimePerMove(2000);
//		 viewer.viewGame(game);

		//5276313436535

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
