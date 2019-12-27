package Game;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.*;

public class GamePanel extends JPanel implements Runnable, IButtonPressedObserver, MouseListener, KeyListener {

    private Board board;
    private Coordinate bound;
    private List<Integer> allowedCellSizes;
    private final int minCellSize = 1, maxCellSize = 40;
    private int cellSizeIdx;
    private int cellSize;
    private boolean running;
    private int delay = 100;

    private Coordinate focusLowerBound, focusUpperBound;

    public GamePanel(Board board){
        this.board = board;
        this.bound = new Coordinate(1200, 600);
        this.setPreferredSize(new Dimension(this.bound.x, this.bound.y));
        this.setAllowedCellSizes();
        this.cellSizeIdx = this.allowedCellSizes.size()/2;
        this.cellSize = this.allowedCellSizes.get(this.cellSizeIdx);
        this.focusLowerBound = new Coordinate(0,0);
        this.focusUpperBound = new Coordinate(this.bound.x/this.cellSize, this.bound.y/this.cellSize);
        this.setFocusable(true);
        this.addMouseListener(this);
        this.addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for(int x = this.focusLowerBound.x; x <= this.focusUpperBound.x; x++){
            for(int y = this.focusLowerBound.y; y <= this.focusUpperBound.y; y++){

                int rectPosX = (x - (this.focusLowerBound.x)) * this.cellSize;
                int rectPosY = (y - (this.focusLowerBound.y)) * this.cellSize;

                if(this.board.isAliveAt(new Coordinate(x, y))) {
                    g.setColor(Color.WHITE);
                    g.fillRect(rectPosX, rectPosY, this.cellSize, this.cellSize);
                }
                else {
                    g.setColor(Color.BLACK);
                    g.fillRect(rectPosX, rectPosY, this.cellSize, this.cellSize);
                }
            }
        }

    }

    public int getHeight(){ return (this.focusUpperBound.y - this.focusLowerBound.y)*this.cellSize; }

    public void init(){
        this.running = true;
        this.run();
    }

    public synchronized boolean pause(){
        return this.running = !this.running;
    }

    private synchronized boolean keepRunning(){
        return this.running;
    }

    @Override
    public void run() {
        while(true) {
            while (this.keepRunning()) {
                try {
                    Thread.sleep(this.delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.board.day();
                this.repaint();
            }
        }
    }

    @Override
    public void onPausePressed() {
        this.pause();
        this.board.updateNeighbours();
    }

    private List<Integer> setAllowedCellSizes(){
        this.allowedCellSizes = new ArrayList<>();

        for(int size = this.minCellSize; size < this.maxCellSize; size++){
            if(this.bound.x % size == 0 && this.bound.y % size == 0) allowedCellSizes.add(size);
        }

        return allowedCellSizes;
    }

    private void scaleUpperBound(int oldCellSize){
        int newWidth = ((this.focusUpperBound.x - this.focusLowerBound.x) * oldCellSize) / this.cellSize;
        int newHeight = ((this.focusUpperBound.y - this.focusLowerBound.y) * oldCellSize) / this.cellSize;
        this.focusUpperBound = new Coordinate( this.focusLowerBound.x + newWidth, this.focusLowerBound.y + newHeight);
    }

    @Override
    public void onZoomInPressed() {
        if(this.cellSize >= this.maxCellSize) return;
        int oldCellSize = this.cellSize;
        this.cellSizeIdx++;
        this.cellSize = this.allowedCellSizes.get(this.cellSizeIdx);
        this.scaleUpperBound(oldCellSize);
        this.repaint();
    }

    @Override
    public void onZoomOutPressed() {
        if(this.cellSize <= this.minCellSize) return;
        int oldCellSize = this.cellSize;
        this.cellSizeIdx--;
        this.cellSize = this.allowedCellSizes.get(this.cellSizeIdx);
        this.scaleUpperBound(oldCellSize);
        this.repaint();

    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        this.requestFocus();
        if(this.running) return;
        Coordinate coordinate = new Coordinate((mouseEvent.getX() / this.cellSize) + this.focusLowerBound.x, (mouseEvent.getY() / this.cellSize) + this.focusLowerBound.y);
        if (board.isAliveAt(coordinate)) board.removeCell(coordinate);
        else board.addCell(coordinate);
        this.repaint();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) { }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) { }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) { }

    @Override
    public void mouseExited(MouseEvent mouseEvent) { }

    @Override
    public void keyTyped(KeyEvent keyEvent) { }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        Coordinate unitUp = new Coordinate(0,1);
        Coordinate unitRight = new Coordinate(1,0);

        switch (keyCode){
            case KeyEvent.VK_DOWN:
                this.focusUpperBound = this.focusUpperBound.add(unitUp);
                this.focusLowerBound = this.focusLowerBound.add(unitUp);
                break;
            case KeyEvent.VK_UP:
                this.focusUpperBound = this.focusUpperBound.subtract(unitUp);
                this.focusLowerBound = this.focusLowerBound.subtract(unitUp);
                break;
            case KeyEvent.VK_RIGHT:
                this.focusUpperBound = this.focusUpperBound.add(unitRight);
                this.focusLowerBound = this.focusLowerBound.add(unitRight);
                break;
            case KeyEvent.VK_LEFT:
                this.focusUpperBound = this.focusUpperBound.subtract(unitRight);
                this.focusLowerBound = this.focusLowerBound.subtract(unitRight);
                break;
        }

        if(!this.running) this.repaint();
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) { }
}
