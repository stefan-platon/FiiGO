package implementation;

//clasa ce reprezinta un jucator (uman sau artificial)
abstract class Player {
    //mutarea efectuata
    int[] move;
    //culoarea cu care joaca
    boolean color;

    abstract int[] getMove();
}
