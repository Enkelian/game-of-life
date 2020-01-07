package Game;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import javax.swing.JCheckBox;
import java.awt.event.ActionEvent;
import java.io.*;


import java.util.ArrayList;
import java.util.List;

public class SidePanel extends JToolBar {

    private Coordinate size;
    private boolean isPaused;
    private JButton pauseButton, drawErase, saveAs, clearBoard;
    private JButton zoomIn, zoomOut;
    private Board board;
    private List<IButtonPressedObserver> observers;
    private ImageIcon startIcon, pauseIcon;

    public SidePanel (int height, Board board) throws IOException {
        this.setOrientation(VERTICAL);
        this.size = new Coordinate(200, height);
        this.board = board;
        this.setPreferredSize(new Dimension(this.size.x, this.size.y));
        this.setMaximumSize(new Dimension(this.size.x, this.size.y));
        this.isPaused = true;
        this.setFloatable(false);

        this.startIcon = new ImageIcon(ImageIO.read(new File("res\\start.png")));
        this.pauseIcon = new ImageIcon(ImageIO.read(new File("res\\pause.png")) );


        this.pauseButton = new JButton(this.startIcon);
        this.pauseButton.addActionListener(e -> this.pausePressed(this.pauseButton));
        this.add(this.pauseButton);

        this.saveAs = new JButton("Save as");
        this.saveAs.addActionListener(e -> this.saveToFile());
        this.add(this.saveAs);

        this.clearBoard = new JButton("Clear");
        this.clearBoard.addActionListener(e -> this.clearBoard());
        this.add(this.clearBoard);

        this.zoomIn = new JButton("Zoom in", new ImageIcon(ImageIO.read(new File("res\\plus.png"))));
        this.zoomIn.addActionListener(this::zoomPressed);
        this.add(this.zoomIn);

        this.zoomOut = new JButton("Zoom out", new ImageIcon(ImageIO.read(new File("res\\minus.png"))));
        this.zoomOut.addActionListener(this::zoomPressed);
        this.add(this.zoomOut);

        this.addDrawEraseButton();
        this.addColorButtons();
        this.addShowTracesCheckBox();
        this.addBirthRulesCheckBoxes();
        this.addSurvivalRulesCheckBoxes();

        this.observers = new ArrayList<>();

    }

    private void addColorButtons(){
        for(CellColor color : CellColor.values()){
            JButton colorButton = new JButton(color.toString());
            colorButton.addActionListener( e -> colorButtonClicked(color));
            this.add(colorButton);
        }
    }

    private void addDrawEraseButton(){
        this.drawErase = new JButton("Draw");
        this.drawErase.addActionListener(this::toggleDraw);
        this.add(this.drawErase);
    }

    private void addShowTracesCheckBox(){
        JCheckBox showTraces = new JCheckBox("Show traces", true);
        showTraces.addActionListener( e -> this.toggleTraces());
        this.add(showTraces);
    }

    private void toggleTraces(){
        for(IButtonPressedObserver observer : this.observers) observer.onToggleTraces();
    }

    private void colorButtonClicked(CellColor color){
        for(IButtonPressedObserver observer : this.observers) observer.onColorButtonClicked(color);

    }

    private void toggleDraw(ActionEvent e){
        if(this.isPaused) {
            for (IButtonPressedObserver observer : this.observers) observer.onToggleDraw();
            if ("Draw".equals(e.getActionCommand())) this.drawErase.setText("Erase");
            else this.drawErase.setText("Draw");
        }

    }

    private void addBirthRulesCheckBoxes(){
        this.add(new JLabel("Birth rules:"));
        for(int rule = 1; rule <= 8; rule++){
            JCheckBox birthRule = new JCheckBox(""+rule, this.board.containsBirthRule(rule));
            birthRule.addActionListener(this::birthRuleAction);
            this.add(birthRule);
        }
    }

    private void birthRuleAction(ActionEvent e){
        JCheckBox birthRule = (JCheckBox) e.getSource();
        int rule = Integer.parseInt(birthRule.getText());
        if(birthRule.isSelected()) this.board.addBirthRule(rule);
        else this.board.removeBirthRule(rule);
    }

    private void addSurvivalRulesCheckBoxes(){
        this.add(new JLabel("Survival rules:"));
        for(int rule = 1; rule <= 8; rule++){
            JCheckBox survivalRule = new JCheckBox(""+rule, this.board.containsSurvivalRule(rule));
            survivalRule.addActionListener(this::survivalRuleAction);
            this.add(survivalRule);
        }
    }

    private void survivalRuleAction(ActionEvent e){
        JCheckBox survivalRule = (JCheckBox) e.getSource();
        int rule = Integer.parseInt(survivalRule.getText());
        if(survivalRule.isSelected()) this.board.addSurvivalRule(rule);
        else this.board.removeSurvivalRule(rule);
    }

    private void pausePressed(JButton button){
        if(this.pauseIcon.equals(button.getIcon())) this.pauseButton.setIcon(this.startIcon);
        else this.pauseButton.setIcon(this.pauseIcon);
        this.isPaused = !this.isPaused;
        for(IButtonPressedObserver observer : this.observers) observer.onPausePressed();
    }

    private void zoomPressed(ActionEvent e){
        if("Zoom in".equals(e.getActionCommand())){
            for(IButtonPressedObserver observer : this.observers) observer.onZoomInPressed();
        }
        else if("Zoom out".equals(e.getActionCommand())){
            for(IButtonPressedObserver observer : this.observers) observer.onZoomOutPressed();
        }
    }

    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        int retval = fileChooser.showSaveDialog(this.saveAs);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file == null) {
                return;
            }
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getParentFile(), file.getName() + ".txt");
            }
            try {
                JTextArea textArea = new JTextArea();
                for(Cell cell : this.board.getCurrentState())  textArea.append(cell.toString());
                textArea.write(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
                Desktop.getDesktop().open(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void clearBoard(){
        for(IButtonPressedObserver observer : this.observers) observer.onClearBoard();
    }

    public void addObserver(IButtonPressedObserver observer){
        this.observers.add(observer);
    }

    public void removeObserver(IButtonPressedObserver observer){
        this.observers.remove(observer);
    }
}
