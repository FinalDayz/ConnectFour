package ui;

import java.util.Arrays;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import Game.ConnectFour;
import Game.ConnectFourPlayable;
import Game.GameWatcher;
import Game.State;
import MinMax.MinMaxPlayer;
import betterMinMax.BetterMinMaxPlayer;
import betterMinMax.NegamaxNode;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Point;
import java.awt.BasicStroke;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ConnectFourViewer implements GameWatcher {
    
    ConnectFourPlayable game;
	JPanel drawPanel;
	JFrame windowFrame;
	
	protected int TILE_SIZE = 75;//75;

	private Point mousePosition = new Point(0,0);
	protected Point startBoardDrawPosition = new Point(50, 50);
	private boolean canMakePlay = false;
	protected ConnectFour gameToSimulate;
    private boolean isRedPlayer;
    private ViewerConfig config;
    private Stack<Integer> undoneMoves = new Stack();

	private Button discoverBranch;

	public ConnectFourViewer(ViewerConfig config) {
		this.config = config;

		initialize();
		discoverBranch = new Button(600, 200, "Discover Best Branch", () -> {

			if(currentDiscoveringNodes == null) {
				currentDiscoveringNodes = BetterMinMaxPlayer.TOP_NODES;
				System.out.println("Set new");
				discoverBranch(currentDiscoveringNodes, false);
				return;
			}

			discoverBranch(currentDiscoveringNodes, true);
		});
	}

	public void initialize() {
		windowFrame = new JFrame("ConnectFour");
		
		drawPanel = new JPanel() {
			protected void paintComponent(Graphics g) {
				drawFrame((Graphics2D) g);
			}
		};

		windowFrame.add(drawPanel);
		windowFrame.setVisible(true);
		windowFrame.setDefaultCloseOperation(
			WindowConstants.EXIT_ON_CLOSE
		);
		windowFrame.setBounds(300, 100, 640 + 800, 920);

        windowFrame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                pressedKey(e);
            }
        });

		drawPanel.addMouseMotionListener(new MouseMotionListener() {

			public void mouseMoved(MouseEvent e) {
				mouseMove(e);
			}

			public void mouseDragged(MouseEvent e) {}

		});
        
		drawPanel.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				clickMouse(e);
				discoverBranch.mousePressed(e);
			}
		});

		new Thread(() -> {
			paintLoop();
		}).start();
		
	}

    protected void pressedKey(KeyEvent e) {
        if(!config.canControlGame()) {
            return;
        }

        System.out.println(e.getKeyCode());
        if(e.getKeyCode() == 32) { // SPACE
            
        }

        if(e.getKeyCode() == 37) { // LEFT ARROW
            undoneMoves.add(
                game.undoMove(false)
            );
            update(game);
        }

        if(e.getKeyCode() == 39) { // RIGHT ARROW
            if(undoneMoves.empty()) {
                return;
            }
            game.executePlay(
                undoneMoves.pop()
            );
            update(game);
        }

		if(e.getKeyCode() == 82) { // 'R' restart
			game.reset();
		}
    }

	void paintLoop() {
		while(true) {
			// gameToSimulate = game.copy();
			drawPanel.repaint();
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected void clickMouse(MouseEvent e) {
        if(!config.isInteractive()) {
            return;
        }
		Point mousePositionOnBoard = getMousePositionOnBoard();

		if(!canMakePlay || mousePositionOnBoard == null) {
			return;
		}
		int move = mousePositionOnBoard.x;

		boolean canMakeMove = Arrays.stream(
				game.getAvailableMoves(true)
			).anyMatch(column -> column == move);

		if(!canMakeMove) {
			return;
		}

		canMakePlay = false;

		new Thread(() -> {
			game.makePlay(move);
			gameToSimulate = game.copy();
		}).start();
		
	}

	protected void mouseMove(MouseEvent e) {
        if(!config.isInteractive()) {
            return;
        }
		this.mousePosition = e.getPoint();
	}

	protected Point getMousePositionOnBoard() {
		int rightSide = game.getWidth() * TILE_SIZE;
		int bottomSide = game.getHeight() * TILE_SIZE;

		if(mousePosition.getX() < startBoardDrawPosition.x || 
			mousePosition.getY() < startBoardDrawPosition.y ||
			mousePosition.getX() >= startBoardDrawPosition.x + rightSide ||
			mousePosition.getY() >= startBoardDrawPosition.y + bottomSide) {
			return null;
		}

		return new Point(
			(int) Math.floor((mousePosition.getX() - startBoardDrawPosition.x) / TILE_SIZE),
			(int) Math.floor((mousePosition.getY() - startBoardDrawPosition.y) / TILE_SIZE)
		);
	}

	protected void drawFrame(Graphics2D g) {
		Point mousePositionOnBoard = getMousePositionOnBoard();
		ConnectFour game = gameToSimulate;

		int widthTiles = game.getWidth();
		int heightTiles = game.getHeight();

		// Set background black
		g.setColor(Color.BLACK);
		g.fillRect(0, 0,windowFrame.getWidth(), windowFrame.getHeight());
		
		// Set left and top margin
		g.translate(startBoardDrawPosition.x, startBoardDrawPosition.y);

		int rightSide = (widthTiles) * TILE_SIZE;
		int bottomSide = (heightTiles) * TILE_SIZE;


		if(gameToSimulate != null && canMakePlay && mousePositionOnBoard != null) {
			int hoverMoveIndex = mousePositionOnBoard.x;

			boolean canMakeMove = Arrays.stream(gameToSimulate.getAvailableMoves(true))
				.anyMatch(column -> column == hoverMoveIndex);


			if(canMakeMove) {
				int y = 0;

				while(!game.isRedOrYellowAtPosition(hoverMoveIndex, y+1) && y+1 < heightTiles) {
					y++;
				}
				
				g.setColor(getPlayerColor(game.isRedTurn()).darker().darker());
				g.fillOval(hoverMoveIndex * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
			}
		}

		drawGame(g, game, 0, 0);

		if(game.gameState.gameDidEnd()) {
			g.setColor(Color.MAGENTA);
			String endStr = getGameStateStr(game.gameState, isRedPlayer);
			
			g.setFont(new Font("Arial", Font.BOLD, 70));
			g.drawString(endStr, rightSide/2-200, bottomSide/2-10);
		}

		discoverBranch.draw(g);

		drawDebugNodes(g);
		drawEval(g);
	}

	private NegamaxNode[] currentDiscoveringNodes;
	private void discoverBranch(NegamaxNode[] nodes, boolean play) {
		if(nodes == null || nodes.length == 0) {
			return;
		}

		System.out.println("Discovering branch...");
		NegamaxNode maxNode = nodes[0];
		float maxScore = Integer.MIN_VALUE;
		for(NegamaxNode node : nodes) {
			System.out.println("Move index: " + node.getMoveIndex()+" score: " + node.getReverseScore());
			if(node.getReverseScore() > maxScore) {
				maxNode = node;
				maxScore = node.getReverseScore();
			}
		}

		System.out.println("Node move: " + maxNode.moveIndex+" Max score: '" + maxScore+"'");

		if(play)
		gameToSimulate.executePlay(maxNode.moveIndex);
		currentDiscoveringNodes = maxNode.getChildNodes();
	}

	protected void drawEval(Graphics2D g) {
		g.setFont(new Font("Arial", Font.CENTER_BASELINE, 18));
		g.drawString("Evaluation: ", 540, 10);

		g.setFont(new Font("Arial", Font.CENTER_BASELINE, 14));

		NegamaxNode[] parentNodes = BetterMinMaxPlayer.TOP_NODES;
		if(parentNodes == null || parentNodes.length == 0) {
			g.drawString("Waiting to play...", 540, 40);
			return;
		}

		g.drawString("Depth: " + BetterMinMaxPlayer.lastCompletedDepth, 540, 70);

		float maxScore = Integer.MIN_VALUE;
		for(NegamaxNode node : parentNodes) {
			if(node.getReverseScore() == Integer.MIN_VALUE || node.getReverseScore() == Integer.MAX_VALUE) continue;
			maxScore = Math.max(node.getReverseScore(), maxScore);
		}

		if(Math.abs(maxScore) < 35) {
			String scoreStr = ""+Math.round(maxScore * 100) / 100.0;
			g.drawString(scoreStr, 540, 40);
			return;
		}

		int mateIn = (int) Math.floor(1000 / maxScore);
		g.drawString("M " + mateIn, 540, 40);

//		if(parentNodes != null) {
//			int index = 0;
//			for(NegamaxNode node : parentNodes) {
//				drawNode(g, node, 1, index * 180, 500, 180);
//				index++;
//			}
//		}
	}

	protected void drawGame(Graphics2D g, ConnectFour game, int startX, int startY) {
		g.translate(startX, startY);

		int widthTiles = game.getWidth();
		int heightTiles = game.getHeight();

		int rightSide = (widthTiles) * TILE_SIZE;
		int bottomSide = (heightTiles) * TILE_SIZE;

		// Draw the board lines
		g.setColor(Color.WHITE);
		for(int x = 0; x < widthTiles+1; x++) {
			g.drawLine(x * TILE_SIZE, 0, x * TILE_SIZE, bottomSide);
		}
		for(int y = 0; y < heightTiles+1; y++) {
			g.drawLine(0, y * TILE_SIZE, rightSide, y * TILE_SIZE);
		}

		g.setColor(Color.WHITE);
		for(int x = 0; x < widthTiles; x++) {
			for(int y = 0; y < heightTiles; y++) {
				int positionX = x * TILE_SIZE;
				int positionY = y * TILE_SIZE;

				if(game.isRedAtPosition(x, y)) {
					g.setColor(getPlayerColor(true));
					g.fillOval(positionX, positionY, TILE_SIZE, TILE_SIZE);
				}

				if(game.isYellowAtPosition(x, y)) {
					g.setColor(getPlayerColor(false));
					g.fillOval(positionX, positionY, TILE_SIZE, TILE_SIZE);
				}
			}
		}
		g.translate(-startX, -startY);
	}

	protected void drawDebugNodes(Graphics2D g) {
		g.setFont(new Font("Arial", Font.CENTER_BASELINE, 10));
		((Graphics2D) g).setStroke(new BasicStroke(0.1f));

		NegamaxNode[] parentNodes = BetterMinMaxPlayer.TOP_NODES;
		if(parentNodes != null) {
			int index = 0;
			for(NegamaxNode node : parentNodes) {
				drawNode(g, node, 1, index * 180, 500, 180);
				index++;
			}
		}
	}

	private void drawNode(Graphics2D g, NegamaxNode node, int depth, int x, int y, float range) {

		g.setColor(Color.WHITE);
		g.drawString(node.getReverseScore()+"", x+5, y+13);
		if(node.getChildNodes() == null || depth > 2) {
			return;
		}
		int index = 0;
		for(NegamaxNode childNode : node.getChildNodes()) {
			float childRange = range / node.getChildNodes().length;
			int childDepth = depth + 1;
			int childX = (int) Math.round(x + index*1.0/node.getChildNodes().length * range);
			int childY = 500 + depth * 100;
			
			g.drawLine(x+10, y+20, childX+10, childY);
			drawNode(g, childNode, childDepth, childX, childY, childRange);
			index++;
		}
	}

	private Color getPlayerColor(boolean forRed) {
		return forRed ? new Color(200, 0, 0) : new Color(220, 200, 0);
	}

	private String getGameStateStr(State gameState, boolean isRed) {
		if(gameState.gameDidDraw()) {
			return "Game is drawn!";
		}

        if(!config.isInteractive()) {
            return gameState.redDidWon() ? "Red won" : "Yellow won";
        }

		if(isRed && gameState.redDidWon()) {
			return "You won!";
		}

		return "You lost :(";
	}

	public void viewGame(ConnectFourPlayable game) {
		this.game = game;

		this.gameToSimulate = game.copy();
		game.attachWatcher(this);
	}

	@Override
	public void update(ConnectFour game) {
		gameToSimulate = game.copy();
	}

    public void canMakePlay() {
        if(!config.isInteractive()) {
            System.err.println("Warning, cannot enable 'namePlay' if is interactive is false");
            return;
        }
        canMakePlay = true;
    }

	public void close() {
		windowFrame.dispose();
	}
}
