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
    private JSlider tracesLengthSlider;
    private CellColor defaultCellColor;
    private ArrayList<JCheckBox> conwayCheckBoxes;
    private ArrayList<JCheckBox> allCheckBoxes;

    public SidePanel (int height, Board board) throws IOException {
        this.setOrientation(VERTICAL);
        this.size = new Coordinate(200, height);
        this.board = board;
        this.setPreferredSize(new Dimension(this.size.x, this.size.y));
        this.setMaximumSize(new Dimension(this.size.x, this.size.y));
        this.isPaused = false;
        this.setFloatable(false);

        this.defaultCellColor = CellColor.BLUE;
        this.conwayCheckBoxes = new ArrayList<>();
        this.allCheckBoxes = new ArrayList<>();

        this.startIcon = new ImageIcon(ImageIO.read(new File("res\\start.png")));
        this.pauseIcon = new ImageIcon(ImageIO.read(new File("res\\pause.png")) );


        this.pauseButton = new JButton(this.pauseIcon);
        this.pauseButton.addActionListener(e -> this.pausePressed());
        this.add(this.pauseButton);

        this.saveAs = new JButton("Save as");
        this.saveAs.addActionListener(e -> this.saveToFile());
        this.add(this.saveAs);

        this.zoomIn = new JButton("Zoom in", new ImageIcon(ImageIO.read(new File("res\\plus.png")).getScaledInstance(50,50, Image.SCALE_SMOOTH)));
        this.zoomIn.addActionListener(this::zoomPressed);
        this.add(this.zoomIn);

        this.zoomOut = new JButton("Zoom out", new ImageIcon(ImageIO.read(new File("res\\minus.png")).getScaledInstance(50,50, Image.SCALE_SMOOTH)));
        this.zoomOut.addActionListener(this::zoomPressed);
        this.add(this.zoomOut);

        this.clearBoard = new JButton("Clear board");
        this.clearBoard.addActionListener(e -> this.clearBoard());
        this.add(this.clearBoard);

        this.addDrawEraseButton();
        this.addColorButtons();
        this.addShowTracesCheckBox();
        this.addTracesLengthSlider();
        this.addRulesCheckBoxes();

        JButton resetRules = new JButton(("Reset rules"));
        resetRules.addActionListener(e -> this.resetRules());
        this.add(resetRules);

        this.observers = new ArrayList<>();

    }

    private void addColorButtons() throws IOException {
        JLabel colorButtonsText = new JLabel("Choose color:");
        colorButtonsText.setBorder(BorderFactory.createEmptyBorder(4,3,4,3));
        this.add(colorButtonsText);

        JPanel colorButtonsPanel = new JPanel(new GridLayout(1,3));
        ButtonGroup colorButtons = new ButtonGroup();

        for(CellColor color : CellColor.values()){
            JRadioButton colorButton = new JRadioButton(new ImageIcon(ImageIO.read(new File("res\\"+color.toString()+".png"))));
            if(color.equals(this.defaultCellColor)) colorButton.doClick();
            colorButton.addActionListener( e -> colorButtonClicked(color));
            colorButton.setBorder(BorderFactory.createEmptyBorder(2,3,2,3));
            colorButtons.add(colorButton);
            colorButtonsPanel.add(colorButton);
        }

        this.add(colorButtonsPanel);
    }

    private void addDrawEraseButton(){
        this.drawErase = new JButton("Erase");
        this.drawErase.addActionListener(this::toggleDraw);
        this.add(this.drawErase);
    }

    private void addShowTracesCheckBox(){
        JCheckBox showTraces = new JCheckBox("Show traces", true);
        showTraces.setBorder(BorderFactory.createEmptyBorder(15,3,10,3));
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

    private void addRulesCheckBoxes(){
        JPanel rulesPanel = new JPanel(new GridLayout(1,2));
        rulesPanel.add(this.addBirthRulesCheckBoxes());
        rulesPanel.add(this.addSurvivalRulesCheckBoxes());
        this.add(rulesPanel);
    }

    private JPanel addBirthRulesCheckBoxes(){
        JPanel birthRulesPanel = new JPanel(new GridLayout (9, 1));
        birthRulesPanel.add(new JLabel("Birth:"));
        for(int rule = 1; rule <= 8; rule++){
            JCheckBox birthRule = new JCheckBox(""+rule, this.board.containsBirthRule(rule));
            if(rule == 3) this.conwayCheckBoxes.add(birthRule);
            this.allCheckBoxes.add(birthRule);
            birthRule.addActionListener(this::birthRuleAction);
            birthRulesPanel.add(birthRule);
        }
        return birthRulesPanel;
    }

    private void birthRuleAction(ActionEvent e){
        JCheckBox birthRule = (JCheckBox) e.getSource();
        int rule = Integer.parseInt(birthRule.getText());
        if(birthRule.isSelected()) this.board.addBirthRule(rule);
        else this.board.removeBirthRule(rule);
    }

    private JPanel addSurvivalRulesCheckBoxes(){
        JPanel survivalRulesPanel = new JPanel(new GridLayout (9, 1));
        survivalRulesPanel.add(new JLabel("Survival:"));
        for(int rule = 1; rule <= 8; rule++){
            JCheckBox survivalRule = new JCheckBox(""+rule, this.board.containsSurvivalRule(rule));
            if(rule == 2 || rule == 3) this.conwayCheckBoxes.add(survivalRule);
            this.allCheckBoxes.add(survivalRule);
            survivalRule.addActionListener(this::survivalRuleAction);
            survivalRulesPanel.add(survivalRule);
        }
        return survivalRulesPanel;
    }

    private void survivalRuleAction(ActionEvent e){
        JCheckBox survivalRule = (JCheckBox) e.getSource();
        int rule = Integer.parseInt(survivalRule.getText());
        if(survivalRule.isSelected()) this.board.addSurvivalRule(rule);
        else this.board.removeSurvivalRule(rule);
    }

    private void pausePressed(){
        if(this.pauseIcon.equals(this.pauseButton.getIcon())) this.pauseButton.setIcon(this.startIcon);
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

    private void addTracesLengthSlider(){
        this.add(new JLabel ("Length of traces:"));
        this.tracesLengthSlider = new JSlider(JSlider.HORIZONTAL, 20, 1);
        this.tracesLengthSlider.addChangeListener( e -> tracesLengthChanged(tracesLengthSlider.getValue()));
        this.tracesLengthSlider.setMajorTickSpacing(10);
        this.tracesLengthSlider.setMinorTickSpacing(1);
        this.tracesLengthSlider.setPaintTicks(true);
        this.add(this.tracesLengthSlider);
    }

    private void tracesLengthChanged(int value){
        for(IButtonPressedObserver observer : this.observers) observer.onSliderChanged(value);
    }

    public void onInit(){
        this.tracesLengthChanged(this.tracesLengthSlider.getValue());
        this.pausePressed();
        this.colorButtonClicked(this.defaultCellColor);

    }

    private void clearBoard(){
        if(this.isPaused) {
            for (IButtonPressedObserver observer : this.observers) observer.onClearBoard();
        }
    }

    private void resetRules(){
        for(JCheckBox ruleCheckBox : this.allCheckBoxes){
            if(ruleCheckBox.isSelected()){
                if(!this.conwayCheckBoxes.contains(ruleCheckBox)) ruleCheckBox.doClick();
            }
            else{
                if(this.conwayCheckBoxes.contains(ruleCheckBox)) ruleCheckBox.doClick();
            }

        }
    }

    public void addObserver(IButtonPressedObserver observer){
        this.observers.add(observer);
    }

    public void removeObserver(IButtonPressedObserver observer){
        this.observers.remove(observer);
    }
}
