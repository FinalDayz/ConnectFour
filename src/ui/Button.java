package ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

public class Button {

    private int x;
    private int y;
    private int width, height;
    private String text;
    private int clickedUITimer = 0;
    private ButtonListener listener;

    private static int MARGIN = 10;


    public Button(int x, int y, String text, ButtonListener listener) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.listener = listener;
    }
    
    public void draw(Graphics2D g) {
        Font font = new Font("Arial", Font.BOLD, 16);
        Rectangle2D textRect = font.getStringBounds(text, g.getFontRenderContext());
        width = textRect.getBounds().width;
        height = textRect.getBounds().height;


        if(clickedUITimer > 0) {
            g.setColor(new Color(0, 205, 255));
        } else {
            g.setColor(new Color(0, 150, 200));
        }
        
        g.fillRect(x, y, width + MARGIN * 2, height + MARGIN);

    
        g.setFont(font);
        if(clickedUITimer > 0) {
            g.setColor(Color.gray);
        } else {
            g.setColor(Color.WHITE);
        }
        g.drawString(text, x + MARGIN, y + 20);

        clickedUITimer = Math.max(0, clickedUITimer-1);
    }

    public static interface ButtonListener {
        void click();
    }

    public void mousePressed(MouseEvent e) {
        if(e.getX() > x && e.getX() < e.getX() + width &&
            e.getY() > y && e.getY() < e.getY() + height
        ) {
            clickedUITimer = 10;
            listener.click();
        }
    }
}