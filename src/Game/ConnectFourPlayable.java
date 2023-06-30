package Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import betterMinMax.SmartEvaluationFunction;

public class ConnectFourPlayable extends ConnectFour {
	
	ConnectFourPlayer redPlayer, yellowPlayer;
	private List<GameWatcher> watchers = new LinkedList<>();
	private int minTimePerMove = 0;
	private long lastTurn = 0;
	ConnectFourPlayer beginPlayer;
	
	public ConnectFourPlayable(ConnectFourPlayer redPlayer, ConnectFourPlayer yellowPlayer) {
		super();
		this.redPlayer = redPlayer;
		this.yellowPlayer = yellowPlayer;
		this.beginPlayer = redPlayer;
		
		this.redPlayer.init(this, true);
		this.yellowPlayer.init(this, false);
	}
	
	public void setBeginPlayer(ConnectFourPlayer player) {
		this.beginPlayer = player;
		if(player == redPlayer) {
			this.redTurn = true;
		} else {
			this.redTurn = false;
		}
	}
	
	@Override
	public void makePlay(int columnIndex) {
		this.executePlay(columnIndex);

		long timeTookForMove = System.currentTimeMillis() - lastTurn;
		if(timeTookForMove < minTimePerMove) {
			try {
				Thread.sleep(minTimePerMove-timeTookForMove);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		new Thread(() -> {
			giveTurn();
		}).start();
	}
	
	public void reset() {
		
		moveHistory = new ArrayList<Integer>();

		this.bitBoardRed = 0l;
		this.bitBoardYellow = 0l;
		this.availableMoves = calcAvailableMoves();
		this.gameState = new State();
		setBeginPlayer(beginPlayer);

		begin();
	}
	
	public void begin() {
		giveTurn();
	}
	
	private void giveTurn() {
		lastTurn = System.currentTimeMillis();
		System.out.println("MOVES PLAYED: "+moveHistory);
		SmartEvaluationFunction.testPatterns(this);
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

		this.redPlayer.update();
		this.yellowPlayer.update();
		for(GameWatcher watcher : watchers) {
			watcher.update(this);
		}
	}

	public void executeSet(int...columns) {
		for(int column : columns) {
			this.executePlay(column);
		}
		this.redPlayer.update();
		this.yellowPlayer.update();
	}
	public void attachWatcher(GameWatcher watcher) {
		this.watchers.add(watcher);
	}

    public void setMinTimePerMove(int timeInMs) {
		this.minTimePerMove = timeInMs;
    }
}
