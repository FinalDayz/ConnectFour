package ui;

import Game.ConnectFour;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BitBoardViewer extends ConnectFourViewer {

    class BitBoardShow {
        public BitBoardShow(long bitBoard, String name) {
            this.bitBoard = bitBoard;
            this.name = name;
        }

        long bitBoard;
        String name;
    }

    private List<BitBoardShow> bitBoards = new ArrayList<>();
    private int currentSelectIndex = 0;
    private boolean blockThread = false;
    private String name;

    public static long[] staticGame = new long[]{ 0l, 0l };

    private static HashMap<String, BitBoardViewer> instances = new HashMap<>();

    public static BitBoardViewer add(String key, String displayName) {
        BitBoardViewer newbbv = new BitBoardViewer(displayName);
        instances.put(key, newbbv);
        return newbbv;
    }

    public static BitBoardViewer get(String key) {
        if(!instances.containsKey(key)) {
            throw new IllegalArgumentException("BitBoardViewer instance with key '"+key+"' doesn't exist");
        }

        return instances.get(key);
    }

    public BitBoardViewer(String name) {
        this(
                (new ViewerConfig())
                        .setCanControl(false)
        );
        this.name = name;
        this.windowFrame.setTitle(name);
    }

    public static void setStaticGame(ConnectFour game) {
        setStaticGame(game.getRedBitBoard(), game.getYellowBitBoard());
    }

    public static void setStaticGame(long redbb, long yellowbb) {
        staticGame[0] = redbb;
        staticGame[1] = yellowbb;
    }

    public void addBoard(long board, String name) {
        bitBoards.add(new BitBoardShow(board, name));
    }

    private BitBoardViewer(ViewerConfig config) {
        super(config);

        windowFrame.setBounds(100, 100, 1300, 640);
        startBoardDrawPosition = new Point(20, 80);
    }

    @Override
    protected void drawDebugNodes(Graphics2D g) {}

    @Override
    protected void drawFrame(Graphics2D g) {
        gameToSimulate = new ConnectFour(bitBoards.get(currentSelectIndex).bitBoard, 0l);

        super.drawFrame(g);

        if(blockThread) {
            g.setColor(Color.orange);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Blocking thread, press space to continue...", 0, TILE_SIZE * gameToSimulate.getHeight() + 30);
        }
        g.setColor(Color.WHITE);


        g.drawString("Showing static game", 650, -20);
        ConnectFour staticGameInstance = new ConnectFour(staticGame[0], staticGame[1]);
        drawGame(g, staticGameInstance, 650, 0);


        g.translate(-startBoardDrawPosition.x, -startBoardDrawPosition.y);

        g.setColor(Color.WHITE);

        g.drawString(
                (currentSelectIndex+1)+"/"+(bitBoards.size()) +"] "+ bitBoards.get(currentSelectIndex).name
                , 10, 60);

        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString(name, 10, 30);
    }

    @Override
    protected void pressedKey(KeyEvent e) {
        System.out.println(e.getKeyCode());
        int keyCode = e.getKeyCode();
        if(keyCode == 32) {// SPACE
            blockThread = false;
        }

        if(e.getKeyCode() == 37) { // LEFT ARROW
            currentSelectIndex--;
        }

        if(e.getKeyCode() == 39) { // RIGHT ARROW
            currentSelectIndex++;
        }

        currentSelectIndex = Math.max(currentSelectIndex, 0);
        currentSelectIndex = Math.min(currentSelectIndex, bitBoards.size() - 1);
    }

    public void blockUntilContinue() {
        blockThread = true;
        while(blockThread) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        windowFrame.dispose();
    }

    @Override
    protected Point getMousePositionOnBoard() {
        return null;
    }
}
