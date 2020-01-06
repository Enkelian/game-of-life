package Game;

public enum CellColor {
    WHITE,
    RED,
    GREEN,
    ORANGE;

    @Override
    public String toString() {
        switch (this){
            case WHITE:
                return "white";
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
