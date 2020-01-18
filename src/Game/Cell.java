package Game;

public class Cell {

    private Coordinate coordinate;
    private Integer neighboursCount;
    private CellColor color;
    Board board;

    public Cell(Coordinate coordinate, Board board){
        this.coordinate = coordinate;
        this.board = board;
        this.neighboursCount = 0;
        this.color = CellColor.BLUE;
    }

    public Cell(Coordinate coordinate, Board board, CellColor color){
        this(coordinate, board);
        this.color = color;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Cell)) return false;
        Cell another = (Cell) obj;
        return this.coordinate.equals(another.coordinate) && this.board == another.board;
    }

    @Override
    public String toString() {
        return "coordinate='" + this.coordinate + '\'' + ", color='" + this.color + '\'';
    }

    public void changeColor(CellColor color){
        this.color = color;
    }

    public CellColor getColor(){ return this.color; }

    public int getNeighboursCount(){ return this.neighboursCount; }

    public Coordinate getCoordinate(){ return this.coordinate; }

    public void updateNeighboursCount(){
        this.neighboursCount = 0;

        for(Coordinate coordinate : this.coordinate.neighbours()){
            if(this.board.isAliveAt(coordinate)) this.neighboursCount++;
        }
    }

}
