package Game;

import java.util.List;

public class Cell {

    private Coordinate coordinate;
    private Integer neighboursCount;
    protected Board board;

    public Cell(Coordinate coordinate, Board board){
        this.coordinate = coordinate;
        this.board = board;
        this.neighboursCount = 0;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Cell)) return false;
        Cell another = (Cell) obj;
        return this.coordinate.equals(another.coordinate) && this.board == another.board;
    }

    @Override
    public String toString() {
//        if(this.isAlive()) return "1";
//        else return "0";
        return this.neighboursCount.toString();
    }

    public int getNeighboursCount(){ return this.neighboursCount; }

    public Coordinate getCoordinate(){ return this.coordinate; }

    public void updateNeighboursCount(){
        this.neighboursCount = 0;

        for(Coordinate coordinate : this.coordinate.neighbours()){
            if(this.board.isAliveAt(coordinate)) this.neighboursCount++;
        }
    }


}
