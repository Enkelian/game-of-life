package Game;

public enum CellColor {
    BLUE,
    RED,
    GREEN,
    ORANGE;

    @Override
    public String toString() {
        switch (this){
            case BLUE:
                return "blue";
            case RED:
                return "red";
            case GREEN:
                return "green";
            case ORANGE:
                return "orange";
            default:
                return "";
        }
    }
}
