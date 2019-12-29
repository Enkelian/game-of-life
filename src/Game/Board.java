package Game;

import java.util.*;
import java.util.List;

public class Board {

    private List<Integer> survivalRules;        //for Conway's only 2 and 3
    private List<Integer> birthRules;           //for Conway's only 3
    private Coordinate lowerBond, upperBound;       //not initial values anymore, soon to be computed every day
    private HashMap<Coordinate, Cell> aliveCellsByPosition;
    private HashMap<Coordinate, Trace> tracesByPosition;
    private List<IDayEndObserver> dayEndObservers;
    private int maxTraceAge;

    public Board(){
        this.survivalRules = new ArrayList<>();
        this.birthRules = new ArrayList<>();
        this.aliveCellsByPosition = new HashMap<>();
        this.tracesByPosition = new HashMap<>();
        this.dayEndObservers = new ArrayList<>();
        this.maxTraceAge = 20;
        this.survivalRules.add(2);
        this.survivalRules.add(3);
        this.birthRules.add(3);

        this.setGlider(100, 50);
        this.setGlider(30,30);

        this.updateNeighbours();
    }

    public int getWidth(){ return this.upperBound.x + 1; }

    public int getHeight(){ return this.upperBound.y + 1; }

//    public Coordinate getUpperBound(){ return this.upperBound; }
//
//    public Coordinate getLowerBond(){ return this.lowerBond; }

    public boolean containsBirthRule(int rule){ return this.birthRules.contains(rule); }

    public boolean containsSurvivalRule(Integer rule){ return this.survivalRules.contains(rule); }

    public void addBirthRule(Integer rule){
        if(!this.containsBirthRule(rule)) this.birthRules.add(rule);
    }

    public void addSurvivalRule(Integer rule){
        if(!this.containsSurvivalRule(rule)) this.survivalRules.add(rule);
    }

    public void removeBirthRule(Integer rule){ this.birthRules.remove(rule); }

    public void removeSurvivalRule(Integer rule){ this.survivalRules.remove(rule); }

    public boolean isAliveAt(Coordinate coordinate){
        return this.aliveCellsByPosition.get(coordinate) != null;
    }

    public int traceAgeAt(Coordinate coordinate){
        Trace traceHere = this.tracesByPosition.get(coordinate);
        if(traceHere != null) return traceHere.getAge();
        else return -1;
    }

    public void day(){

        ArrayList <Cell> dead = new ArrayList<>();
        Collection<Cell> aliveCells = this.aliveCellsByPosition.values();

        for(Cell cell : aliveCells){
            if(!this.survivalRules.contains(cell.getNeighboursCount())) dead.add(cell);
        }

        Set<Coordinate> candidatesCoordinates = new LinkedHashSet<>();

        for(Cell cell : aliveCells) {
            candidatesCoordinates.addAll(cell.getCoordinate().neighbours());
        }

        ArrayList<Coordinate> invalidCandidates = new ArrayList<>();

        for(Coordinate coordinate : candidatesCoordinates){
            if(this.isAliveAt(coordinate)) invalidCandidates.add(coordinate);
            else{
                int aliveNeighboursCount = 0;
                for(Coordinate neighbour : coordinate.neighbours()){
                    if(this.isAliveAt(neighbour)) aliveNeighboursCount++;
                }
                if(!this.birthRules.contains(aliveNeighboursCount)) invalidCandidates.add(coordinate);
            }

        }

        candidatesCoordinates.removeAll(invalidCandidates);

        for(Coordinate coordinate : candidatesCoordinates){
            this.addCell(coordinate);
        }

        for(Cell toBeKilled : dead){
            this.removeCell(toBeKilled.getCoordinate());
        }
        dead.clear();

        List<Trace> tracesToBeRemoved = new ArrayList<>();

        for(Trace trace : this.tracesByPosition.values()){
            if(trace.getAge() >= this.maxTraceAge) tracesToBeRemoved.add(trace);
        }

        for(Trace trace : tracesToBeRemoved) this.tracesByPosition.remove(trace.getCoordinate());

        this.updateNeighbours();

        for(IDayEndObserver observer : this.dayEndObservers) observer.onDayEnd();
    }

//    private void updateBounds()

    public void updateNeighbours(){
        for (Cell aliveCell : this.aliveCellsByPosition.values()) {
            aliveCell.updateNeighboursCount();
        }
    }

    public void addCell(Coordinate coordinate){
        Cell newCell = new Cell(coordinate, this);
        this.aliveCellsByPosition.put(newCell.getCoordinate(), newCell);
    }

    public void removeCell(Coordinate coordinate){
        this.aliveCellsByPosition.remove(coordinate);
        this.tracesByPosition.put(coordinate, new Trace(coordinate, this));
    }

    public void setGlider(int gliderX, int gliderY){
        this.addCell(new Coordinate(gliderX, gliderY));
        this.addCell(new Coordinate(gliderX + 1, gliderY));
        this.addCell(new Coordinate(gliderX + 2,gliderY));
        this.addCell(new Coordinate(gliderX ,gliderY + 1));
        this.addCell(new Coordinate(gliderX + 1,gliderY + 2));
    }

    public void addDayEndObserver(IDayEndObserver observer){
        this.dayEndObservers.add(observer);
    }

    public void removeDayEndObserver(IDayEndObserver observer){
        this.dayEndObservers.remove(observer);
    }
}
