import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import Game.ConnectFour;
import Game.ConnectFourPlayable;
import Game.ConnectFourPlayer;
import Game.GameWatcher;
import Game.State;
import betterMinMax.BetterMinMaxPlayer;
import betterMinMax.SmartEvaluationFunction;
import ui.ConnectFourViewer;
import ui.ViewerConfig;

public class GraphicsPlayer implements ConnectFourPlayer {
	
	ConnectFourViewer ui;
	private ConnectFourPlayable game;


	@Override
	public void makePlay() throws InterruptedException {
		SmartEvaluationFunction.testPatterns(game);
		ui.canMakePlay();
	}

	@Override
	public void init(ConnectFourPlayable game, boolean redPlayer) {
		this.game = game;
		ViewerConfig config = new ViewerConfig();
		config.setPlayer(redPlayer);
		ui = new ConnectFourViewer(config);
		ui.viewGame(game);
	}

	@Override
	public void update() {}

	@Override
	public void setLog(boolean log) {

	}

}
