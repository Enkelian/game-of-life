package Game;

import javafx.geometry.Side;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    private int width;
    private int height;
    private Board board;
    private GamePanel gamePanel;
    private SidePanel sidePanel;
    private JPanel contents;

    public GameFrame(Board board){

        this.board = board;

        this.setTitle("Game of life");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.gamePanel = new GamePanel(this.board);
        this.sidePanel = new SidePanel(this.gamePanel.getHeight());
        this.sidePanel.addObserver(this.gamePanel);
        this.contents = new JPanel(new BorderLayout());
        this.contents.add(this.gamePanel, BorderLayout.LINE_START);
        this.contents.add(this.sidePanel, BorderLayout.LINE_END);
        this.setContentPane(this.contents);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.gamePanel.init();
    }

}
