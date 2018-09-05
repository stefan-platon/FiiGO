package implementation;

//clasa ce reprezinta jucatorul uman
class Human extends Player{

    //mutarea jucatorului
    Human() {
        move = new int[2];
    }

    public int[] getMove() {
        return move;
    }

    //setez mutarea jucatorului (informatie primita de la interfata grafica)
    void setMove(int line, int col) {
        move[0] = line;
        move[1] = col;
    }
}
