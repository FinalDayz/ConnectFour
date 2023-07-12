import java.util.Arrays;

import Game.ConnectFourPlayable;
import betterMinMax.BetterMinMaxPlayer;
import betterMinMax.NegamaxNode;
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
		Evaluator evaluator = new Evaluator(400);
		BetterMinMaxPlayer newestBetterMinMaxPlayer = new BetterMinMaxPlayer();

		// -Djava.compiler=NONE
//		evaluator.evaluateSpeed(newestBetterMinMaxPlayer, 5);
//		System.exit(0);

// 		evaluator.evaluate(
// 				new int[]{4,5,6,7},
// 				1000,
// 				newestBetterMinMaxPlayer,
// 				new versions.v5.negamax.betterMinMax.BetterMinMaxPlayer()
// 				// new versions.v4.row_modifiers.betterMinMax.BetterMinMaxPlayer()
// 				// new versions.v3.row_pattern_reward.betterMinMax.BetterMinMaxPlayer()
// //				new versions.v2.column_reward.betterMinMax.BetterMinMaxPlayer()
// //				new versions.v1.base.betterMinMax.BetterMinMaxPlayer(7, 1000)
// 		);

// 		System.exit(0);

//		versions.v4.row_modifiers.betterMinMax.BetterMinMaxPlayer minMaxPlayer = new versions.v4.row_modifiers.betterMinMax.BetterMinMaxPlayer(10, 32000);
		BetterMinMaxPlayer minMaxPlayer = new BetterMinMaxPlayer(27, 4500);
		// minMaxPlayer.loadCache("v5_depth_13.lookup", 13);
		// minMaxPlayer.writeCacheOnce("v5_depth_13.lookup");

//		minMaxPlayer.setCanMakeRandomMove(false);

		//minMaxPlayer.setColumnOnEqualChance(0);
		//minMaxPlayer.setTimeLimit(5000);
		// ConsolePlayer human = new ConsolePlayer();
		GraphicsPlayer human = new GraphicsPlayer();
		
		//human is yellow, starts 2nd, bot starts
		ConnectFourPlayable game = new ConnectFourPlayable(minMaxPlayer, human);//game.executeSet(3);
		//human is Red, starts first, bot is 2nd
		//  ConnectFourPlayable game = new ConnectFourPlayable(human, minMaxPlayer);

		 game.executeSet(3, 3, 3, 3, 3, 4, 1, 1, 4, 1, 1, 0, 0, 0, 1, 0);
		// game.executeSet(3, 3, 3, 3);//, 4, 2, 2, 2, 4, 2, 6, 5 );

		// game.executeSet(3, 3, 3, 3, 4, 2, 2, 2, 2, 5, 5, 0, 3, 3, 2, 5, 0, 6, 5, 5, 4, 5, 0, 2, 0, 0, 0, 4, 1);
								//  3, 3, 3, 3, 4, 2, 2, 2, 2, 5, 5, 0, 3, 3, 2, 5, 0, 6, 5, 5, 4, 5, 0, 2, 0, 0, 0, 4, 1) yellow to move
		// game.setBeginPlayer(minMaxPlayer);


		game.begin();
	}
	

	public static void println(int[] o) {System.out.println(Arrays.toString(o));}
	public static void println(Object o) {System.out.println(o);}
	public static void println() {System.out.println();}
	public static void print(Object o) {System.out.print(o);}
	
}
