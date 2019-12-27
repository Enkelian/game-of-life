package Game;


import javax.swing.JToolBar;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class SidePanel extends JToolBar {

    private Coordinate size;
    private boolean isPaused;
    private JButton pauseButton;
    private JButton zoomIn, zoomOut;
    private List<IButtonPressedObserver> observers;

    public SidePanel (int height){
        this.size = new Coordinate(200, height);
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

        this.observers = new ArrayList<>();

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
