import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Game.ConnectFour;
import Game.ConnectFourPlayer;

public class GraphicsPlayer implements ConnectFourPlayer {
	
	ConnectFour game;
	JPanel drawPanel;
	JFrame windowFrame;
	
	@Override
	public void init(ConnectFour game, boolean redPlayer) {
		this.game = game;
		windowFrame = new JFrame("ConnectFour");
		drawPanel = new JPanel();
		windowFrame.add(drawPanel);
	}

	@Override
	public void makePlay() throws InterruptedException {
		
	}

}
