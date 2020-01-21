package Game;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GameFrame extends JFrame {

    private Board board;
    private GamePanel gamePanel;
    private SidePanel sidePanel;
    private JPanel contents;

    public GameFrame(Board board) throws IOException {

        this.board = board;

        this.setTitle("Game of life");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.gamePanel = new GamePanel(this.board);
        this.sidePanel = new SidePanel(this.gamePanel.getHeight(), this.board);
        this.sidePanel.addObserver(this.gamePanel);
        this.sidePanel.onInit();
        this.contents = new JPanel(new BorderLayout());
        this.contents.add(this.gamePanel, BorderLayout.LINE_END);
        this.contents.add(this.sidePanel, BorderLayout.LINE_START);
        this.setContentPane(this.contents);
        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.gamePanel.init();
    }



}
