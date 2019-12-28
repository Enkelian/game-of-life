package Game;


import javax.swing.*;
import java.awt.Dimension;
import javax.swing.JCheckBox;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

public class SidePanel extends JToolBar {

    private Coordinate size;
    private boolean isPaused;
    private JButton pauseButton;
    private JButton zoomIn, zoomOut;
    private Board board;
    private List<IButtonPressedObserver> observers;

    public SidePanel (int height, Board board){
        this.setOrientation(VERTICAL);
        this.size = new Coordinate(200, height);
        this.board = board;
        this.setPreferredSize(new Dimension(this.size.x, this.size.y));
        this.isPaused = false;

        this.pauseButton = new JButton("Pause");
        this.pauseButton.addActionListener(this::pausePressed);
        this.add(this.pauseButton);

        this.zoomIn = new JButton("Zoom in");
        this.zoomIn.addActionListener(this::zoomPressed);
        this.add(this.zoomIn);

        this.zoomOut = new JButton("Zoom out");
        this.zoomOut.addActionListener(this::zoomPressed);
        this.add(this.zoomOut);

        this.addBirthRulesCheckBoxes();
        this.addSurvivalRulesCheckBoxes();

        this.observers = new ArrayList<>();

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

    private void pausePressed(ActionEvent e){
        if("Pause".equals(e.getActionCommand())) this.pauseButton.setText("Start");
        else this.pauseButton.setText("Pause");
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

    public void addObserver(IButtonPressedObserver observer){
        this.observers.add(observer);
    }

    public void removeObserver(IButtonPressedObserver observer){
        this.observers.remove(observer);
    }
}
