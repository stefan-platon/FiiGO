package implementation;

import javafx.scene.shape.Ellipse;

//clasa ce reprezinta o piatra
class Stone {
    //culoarea pietrei
    private boolean color;
    //numarul de libertati
    private int libertiesNumber = 0;
    //lantul din care face parte
    private Chain chain;
    //posizitia pe tabla
    private int line, column;
    //reprezentarea grafica
    private Ellipse shape;

    String getColor() {
        if(color) return "black";
        else return "white";
    }

    Chain getChain() {
        return chain;
    }

    void setChain(Chain chain) {
        this.chain = chain;
    }

    int getLibertiesNumber() {
        return libertiesNumber;
    }

    void decLibertiesNumber() {
        this.libertiesNumber--;
    }

    void incLibertiesNumber() {
        this.libertiesNumber++;
    }

    int getLine() {
        return line;
    }

    int getColumn() {
        return column;
    }

    void setPosition(int line, int column ) {
        this.line = line;
        this.column = column;
    }

    Ellipse getShape() {
        return shape;
    }

    void setShape(Ellipse shape) {
        this.shape = shape;
    }

    Stone(String color, Chain chain){
        this.color = color.equals("black");
        this.chain = chain;
        this.chain.addStone(this);
    }
}
