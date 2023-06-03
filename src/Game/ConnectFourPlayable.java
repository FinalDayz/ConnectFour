package Game;

import java.util.Arrays;

public class ConnectFourPlayable extends ConnectFour {
	ConnectFourPlayer redPlayer, yellowPlayer;
	
	public ConnectFourPlayable(ConnectFourPlayer redPlayer, ConnectFourPlayer yellowPlayer) {
		super();
		this.redPlayer = redPlayer;
		this.yellowPlayer = yellowPlayer;
		
		this.redPlayer.init(this, true);
		this.yellowPlayer.init(this, false);
	}
	
	public void setBeginPlayer(ConnectFourPlayer player) {
		if(player == redPlayer) {
			this.redTurn = true;
		} else {
			this.redTurn = false;
		}
	}
	
	@Override
	public void makePlay(int columnIndex) {
		this.executePlay(columnIndex);
		println("Plays: ");
		println(Arrays.toString(this.moveHistory.toArray()));
		giveTurn();
	}
	
	
	public void begin() {
		giveTurn();
	}
	
	private void giveTurn() {
		if(this.gameState.gameDidEnd()) {
			printBoard();
			System.out.println("Game ended!");
			if(gameState.yellowDidWon())
				System.out.println("Yellow won!");
			if(gameState.redDidWon())
				System.out.println("Red won!");
			if(gameState.gameDidDraw())
				System.out.println("Draw!");
			
			return;
		}
		try {
			if(this.redTurn) {
				this.redPlayer.makePlay();
			} else {
				this.yellowPlayer.makePlay();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void executeSet(int...columns) {
		for(int column : columns) {
			this.executePlay(column);
		}
	}

}
