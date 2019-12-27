package Game;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Board {


    private List<Integer> survivalRules;        //for Conway's only 2 and 3
    private List<Integer> birthRules;           //for Conway's only 3
    private Coordinate lowerBond, upperBound;       //not initial values anymore, soon to be computed every day
    private ArrayList<Cell> aliveCells;


    public Board(){
        this.survivalRules = new ArrayList<>();
        this.birthRules = new ArrayList<>();
        this.aliveCells = new ArrayList<>();
        this.survivalRules.add(2);
        this.survivalRules.add(3);
        this.birthRules.add(3);


        this.setGlider(100, 50);
        this.setGlider(30,30);

        this.updateNeighbours();
        this.updateBounds();
    }

    public int getWidth(){ return this.upperBound.x + 1; }

    public int getHeight(){ return this.upperBound.y + 1; }

    public Coordinate getUpperBound(){ return this.upperBound; }

    public Coordinate getLowerBond(){ return this.lowerBond; }

    public boolean isAliveAt(Coordinate coordinate){
        for(Cell cell : this.aliveCells){
            if(cell.getCoordinate().equals(coordinate)) return true;
        }

        return false;
    }

    public void day(){

        ArrayList <Cell> dead = new ArrayList<>();

        for(Cell cell : this.aliveCells){
            if(!this.survivalRules.contains(cell.getNeighboursCount())) dead.add(cell);
        }

        ArrayList<Cell> birthCandidates = new ArrayList<>();
        Set<Coordinate> candidatesCoordinates = new LinkedHashSet<>();

        for(Cell cell : this.aliveCells) {
            candidatesCoordinates.addAll(cell.getCoordinate().neighbours());
        }

        for(Coordinate coordinate : candidatesCoordinates){
            if(!this.isAliveAt(coordinate)) birthCandidates.add(new Cell(coordinate, this));
        }

        for(Cell cell : birthCandidates) cell.updateNeighboursCount();

        for(Cell cell : birthCandidates){
            if(this.birthRules.contains(cell.getNeighboursCount())) this.aliveCells.add(cell);
        }

        this.aliveCells.removeAll(dead);
        birthCandidates.clear();
        dead.clear();

        this.updateNeighbours();

        this.updateBounds();
    }

    private void updateBounds(){
        if(this.aliveCells.isEmpty()){
            this.lowerBond = this.upperBound = (new Coordinate(0,0));
            return;
        }

        this.lowerBond = this.upperBound = this.aliveCells.get(0).getCoordinate();

        for(Cell cell : this.aliveCells){
            this.lowerBond = this.lowerBond.lowerLeft(cell.getCoordinate());
            this.upperBound = this.upperBound.upperRight(cell.getCoordinate());
        }

    }

    public void updateNeighbours(){
        for(int i = 0; i < this.aliveCells.size(); i++){
            this.aliveCells.get(i).updateNeighboursCount();
        }
    }

    public void addCell(Coordinate coordinate){
        this.aliveCells.add(new Cell(coordinate, this));
    }

    public void removeCell(Coordinate coordinate){
        Cell cellToBeRemoved = null;
        for(Cell cell : this.aliveCells){
            if(cell.getCoordinate().equals(coordinate)){
                cellToBeRemoved = cell;
                break;
            }
        }
         this.aliveCells.remove(cellToBeRemoved);
    }

    public void setGlider(int gliderX, int gliderY){
        this.addCell(new Coordinate(gliderX, gliderY));
        this.addCell(new Coordinate(gliderX + 1, gliderY));
        this.addCell(new Coordinate(gliderX + 2,gliderY));
        this.addCell(new Coordinate(gliderX ,gliderY + 1));
        this.addCell(new Coordinate(gliderX + 1,gliderY + 2));
    }

}
