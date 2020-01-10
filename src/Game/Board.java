package Game;


import java.util.*;
import java.util.List;

public class Board {

    private List<Integer> survivalRules;        //for Conway's only 2 and 3
    private List<Integer> birthRules;           //for Conway's only 3
    private Coordinate lowerBound, upperBound;
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
        this.maxTraceAge = 255/5;

        this.lowerBound = new Coordinate(Integer.MIN_VALUE, Integer.MIN_VALUE);
        this.upperBound = new Coordinate(Integer.MAX_VALUE, Integer.MAX_VALUE);

        this.survivalRules.add(2);
        this.survivalRules.add(3);
        this.birthRules.add(3);

        this.setGlider(100, 50, CellColor.WHITE);
        this.setGlider(30,30, CellColor.RED);
        this.setGlider(50,40, CellColor.GREEN);
        this.setGlider(70, 10, CellColor.ORANGE);

        this.updateNeighbours();
    }

    public int getWidth(){ return this.upperBound.x + 1; }

    public int getHeight(){ return this.upperBound.y + 1; }

    public Coordinate getUpperBound(){ return this.upperBound; }

    public Coordinate getLowerBond(){ return this.lowerBound; }

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
        return this.getCellAt(coordinate) != null;
    }

    public Cell getCellAt(Coordinate coordinate){
        return (coordinate.follows(this.lowerBound) && coordinate.precedes(this.upperBound)) ? this.aliveCellsByPosition.get(coordinate) : null;
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
        HashMap<Coordinate, HashMap<CellColor, Integer>> neighboursCountByColor = new HashMap<>();

        for(Coordinate coordinate : candidatesCoordinates){
            if(this.isAliveAt(coordinate)) invalidCandidates.add(coordinate);
            else{
                int aliveNeighboursCount = 0;
                HashMap<CellColor, Integer> neighboursCount = new HashMap<>();
                for(CellColor color: CellColor.values()) neighboursCount.put(color, 0);
                boolean anyValid = false;
                for(Coordinate neighbour : coordinate.neighbours()){
                    Cell neighbourCell = this.getCellAt(neighbour);
                    if(neighbourCell != null){
                        anyValid = true;
                        aliveNeighboursCount++;
                        neighboursCount.put(neighbourCell.getColor(), neighboursCount.get(neighbourCell.getColor()) + 1);
                    }
                    if(anyValid) neighboursCountByColor.put(coordinate, neighboursCount);
                }
                if(!this.birthRules.contains(aliveNeighboursCount)) invalidCandidates.add(coordinate);
            }

        }

        candidatesCoordinates.removeAll(invalidCandidates);

        for(Coordinate coordinate : candidatesCoordinates){

            HashMap<CellColor, Integer> neighboursColors = neighboursCountByColor.get(coordinate);

            CellColor finalColor = CellColor.WHITE;
            int maxColorCount = 0;
            int numberOfDraws = 0;
            CellColor missingColor = null;

            for(CellColor color : CellColor.values()){
                if(neighboursColors.get(color) > maxColorCount){
                    finalColor = color;
                    maxColorCount = neighboursColors.get(color);
                }
                if(neighboursColors.get(color).equals(1)){
                    numberOfDraws++;
                }
                if(neighboursColors.get(color).equals(0)) missingColor = color;
            }

            if(numberOfDraws == 3 && missingColor != null) finalColor = missingColor;

            this.addCell(coordinate, finalColor);

        }

        for(Cell toBeKilled : dead){
            this.removeCell(toBeKilled.getCoordinate());
        }
        dead.clear();

        this.removeInvalidTraces();
        this.updateNeighbours();

        for(IDayEndObserver observer : this.dayEndObservers) observer.onDayEnd();
    }


    public void updateNeighbours(){
        for (Cell aliveCell : this.aliveCellsByPosition.values()) {
            aliveCell.updateNeighboursCount();
        }
    }

    public void addCell(Coordinate coordinate, CellColor color){
        Cell newCell = new Cell(coordinate, this, color);
        this.aliveCellsByPosition.put(newCell.getCoordinate(), newCell);
    }

    public void removeCell(Coordinate coordinate){
        if(this.aliveCellsByPosition.remove(coordinate) != null)
            this.tracesByPosition.put(coordinate, new Trace(coordinate, this));
    }

    private void setGlider(int gliderX, int gliderY, CellColor color){
        this.addCell(new Coordinate(gliderX, gliderY), color);
        this.addCell(new Coordinate(gliderX + 1, gliderY), color);
        this.addCell(new Coordinate(gliderX + 2,gliderY), color);
        this.addCell(new Coordinate(gliderX ,gliderY + 1), color);
        this.addCell(new Coordinate(gliderX + 1,gliderY + 2), color);
    }

    public void setMaxTraceAge(int maxTraceAge){
        this.maxTraceAge = maxTraceAge;
        this.removeInvalidTraces();
    }

    private void removeInvalidTraces(){
        List<Trace> tracesToBeRemoved = new ArrayList<>();

        for(Trace trace : this.tracesByPosition.values()){
            if(trace.getAge() >= this.maxTraceAge) tracesToBeRemoved.add(trace);
        }

        for(Trace trace : tracesToBeRemoved) this.tracesByPosition.remove(trace.getCoordinate());
    }

    public Collection<Cell> getCurrentState(){
        return this.aliveCellsByPosition.values();
    }

    public void clearBoard(){
        this.aliveCellsByPosition.clear();
        this.tracesByPosition.clear();
    }

    public void removeCellWithoutTrace(Coordinate coordinate){
        this.aliveCellsByPosition.remove(coordinate);
    }

    public void setRulesToConway(){
        this.birthRules.clear();
        this.birthRules.add(3);

        this.survivalRules.clear();
        this.survivalRules.add(2);
        this.survivalRules.add(3);
    }

    public void addDayEndObserver(IDayEndObserver observer){
        this.dayEndObservers.add(observer);
    }

    public void removeDayEndObserver(IDayEndObserver observer){
        this.dayEndObservers.remove(observer);
    }
}
