package Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Coordinate {

    public final int x, y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString(){
        return "("+this.x+","+this.y+")";
    }

    public boolean precedes (Coordinate other){
        return (this.x<=other.x && this.y<=other.y);
    }

    public boolean follows (Coordinate other){
        return (this.x>=other.x && this.y>=other.y);
    }

    public Coordinate upperRight(Coordinate other){
        return new Coordinate(Math.max(this.x,other.x),Math.max(this.y,other.y));
    }

    public Coordinate lowerLeft(Coordinate other){
        return new Coordinate(Math.min(this.x,other.x),Math.min(this.y,other.y));
    }

    public Coordinate add(Coordinate other){
        return new Coordinate(this.x+other.x,this.y+other.y);
    }

    public Coordinate opposite(){
        return new Coordinate(-this.x,-this.y);
    }

    public Coordinate subtract(Coordinate other){
        return this.add(other.opposite());
    }

    @Override
    public int hashCode(){
        int hash=0;
        hash+=this.x*2137;
        hash+=this.y*13;
        return hash;
    }
    @Override
    public boolean equals(Object other){
        if(this==other) return true;
        if(!(other instanceof Coordinate)) return false;
        Coordinate that=(Coordinate) other;
        return (this.x==that.x && this.y==that.y);
    }

    public List<Coordinate> neighbours(){
        List<Coordinate> neighbours = new ArrayList<>();

        neighbours.add(new Coordinate(this.x + 1, this.y + 1));
        neighbours.add(new Coordinate(this.x + 1, this.y));
        neighbours.add(new Coordinate(this.x + 1, this.y - 1));
        neighbours.add(new Coordinate(this.x, this.y + 1));
        neighbours.add(new Coordinate(this.x, this.y - 1));
        neighbours.add(new Coordinate(this.x - 1, this.y + 1));
        neighbours.add(new Coordinate(this.x - 1, this.y));
        neighbours.add(new Coordinate(this.x - 1, this.y - 1));

        return neighbours;
    }
}
