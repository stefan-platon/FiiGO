package implementation;

//clasa ce reprezinta o tabla de joc
class Board {
    //numarul de patrate pe linie
    private static int tileNumber;
    //dimensiunea unui patrat
    private static int tileSize;
    //numarul de piese pe tabla la un moment dat
    private static  int stonesNr = 0;

    //structura unde se stocheaza o stare a tablei
    private static Stone[][] board;

    //structura unde se stocheaza ultima stare a tablei
    private static Stone[][] lastState;

    //initializez tileNumber, tileSize si aloc spatiu pentru structura in functie de dimensiunea tablei
    static void createBoard(int size, double screenWidth){
        tileNumber = size;
        switch(size){
            case 9 : tileSize = (int)(screenWidth / (2.4 * size)); break;
            case 13: tileSize = (int)(screenWidth / (2.2 * size)); break;
            case 19: tileSize = (int)(screenWidth / (2.1 * size)); break;
        }
        board = new Stone[tileNumber + 1][tileNumber + 1];
        lastState = new Stone[tileNumber + 1][tileNumber + 1];
    }

    //revin la starea initiala a unei table
    static void resetBoard(){
        board = new Stone[tileNumber + 1][tileNumber + 1];
    }

    static int getTileNumber() {
        return tileNumber;
    }

    static int getTileSize() {
        return tileSize;
    }

    static int getStonesNr(){
        return stonesNr;
    }

    //preiau o piatra de pe tabla
    static Stone getStone(int boardX, int boardY){
        return board[boardX][boardY];
    }

    //pun o piatra pe tabla
    static void setStone(int boardX, int boardY, Stone stone){
        board[boardX][boardY] = stone;
        stonesNr++;
    }

    //elimin o piatra de pe tabla
    static void removeStone(int boardX, int boardY){
        board[boardX][boardY] = null;
        stonesNr--;
    }

    //verific daca tabla are o piatra pe pozitia data
    static boolean hasStone(int boardX, int boardY){
        return board[boardX][boardY] != null;
    }

    static Stone[][] getBoard(){
        return board;
    }

    static void saveLastState(){
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(board[i], 0, lastState[i], 0, board[i].length);
        }
    }

    static Stone[][] getLastState(){
        return lastState;
    }
}
