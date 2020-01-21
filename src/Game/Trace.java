package Game;

public class Trace extends Cell implements IDayEndObserver {

    private int age;
    private CellColor color;

    public Trace(Coordinate coordinate, CellColor color, Board board){
        super(coordinate,board,color);
        this.age = 0;
        this.board.addDayEndObserver(this);
    }

    public int getAge(){
        return this.age;
    }

    @Override
    public void onDayEnd() {
        this.age++;
    }
}
