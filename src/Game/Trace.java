package Game;

public class Trace extends Cell implements IDayEndObserver {

    private int age;

    public Trace(Coordinate coordinate, Board board) {
        super(coordinate, board);
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
