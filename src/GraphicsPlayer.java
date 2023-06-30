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
import betterMinMax.Node;
import ui.ConnectFourViewer;
import ui.ViewerConfig;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Point;

import java.awt.BasicStroke;

public class GraphicsPlayer implements ConnectFourPlayer {
	
	ConnectFourViewer ui;


	@Override
	public void makePlay() throws InterruptedException {
		ui.canMakePlay();
	}

	@Override
	public void init(ConnectFourPlayable game, boolean redPlayer) {
		ViewerConfig config = new ViewerConfig();
		config.setPlayer(redPlayer);
		ui = new ConnectFourViewer(game, config);
		ui.viewGame(game);
	}

	@Override
	public void update() {}

}
