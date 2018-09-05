package implementation;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.abs;

class QScores {
    private int size;
    private boolean color;

    QScores(int size, boolean color){
        this.size = size;
        this.color = color;
    }

    //gasesc scorurile pentru starea curenta
    INDArray getBoardScores() {
        //creez un vector ce va contine aceste scoruri si va putea fi folosit la antrenare
        INDArray boardScores = Nd4j.zeros(1, size * size);

        //creez o copie a tablei pentru a o putea modifica
        Stone[][] boardCopy = Board.getBoard(), board = new Stone[size][size];

        //gasesc recursiv scorurile tablei curente
        float[][] boardScoresFloat = new float[size][size];
        //simulez o mutare a jucatorului
        int stonesNr = Board.getStonesNr();
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++){
                Chain chain = new Chain();
                Stone stone = new Stone(color ? "black" : "white", chain);
                stone.setPosition(i, j);
                for (int k = 0; k < boardCopy.length; k++) {
                    board[k] = Arrays.copyOf(boardCopy[k], boardCopy[k].length);
                }
                int[] lib = Control.verifyLiberties(board, stone, i, j);
                if(board[i][j] == null && lib[0] + lib[1] != 0){
                    int depth = 0;
                    int dif = size / 2;
                    boardScoresFloat[i][j] = getQScore(board, i, j, depth, depth)
                            + new Random().nextInt(1000 / stonesNr) - (500 / stonesNr)
                            - abs(i - dif) - abs(j - dif);
                }
                else{
                    boardScoresFloat[i][j] = -5000;
                }
            }

        //normalizez vectorul
        //aflu norma vectorului de valori
        double sum = 0;
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++)
                sum += boardScoresFloat[i][j] * boardScoresFloat[i][j];
        sum = Math.sqrt(sum);
        //aplic norma
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++)
                boardScoresFloat[i][j] /= sum;

        //inserez rezultatele gasite intr-un INDArray
        int poz = 0;
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++){
                boardScores.putScalar(new int[]{0, poz}, boardScoresFloat[i][j]);
                poz++;
            }
        //System.out.println(boardScores);
        return boardScores;
    }

    private float getQScore(Stone[][] board, int line, int col,
                            int depth, int initialDepth){
        //calculez scorul pozitiei data ca parametru
        //simulez asezarea
        Chain chain = new Chain();
        Stone stone;
        if(depth % 2 == 1) stone = new Stone(color ? "black" : "white", chain);
        else stone = new Stone(color ? "white" : "black", chain);
        stone.setPosition(line, col);
        int nrStonesRemoved, chainSize, chainLiberties, score;
        board[line][col] = stone;
        //verific daca am luat pietre detinute de oponent
        nrStonesRemoved = Control.verifyNeighbourStones(board, stone.getColor(),
                line, col, false);
        //efectuez eventualele modificari ale lanturilor proprii ce pot aparea in urma plasarii pietrei
        Control.combineChains(board, stone, stone.getColor(), line, col);
        chainSize = stone.getChain().getStones().size();
        chainLiberties = stone.getChain().getLibertiesNumber();
        //aflu scorul starii
        score = nrStonesRemoved * 10 + chainSize * 30 + chainLiberties * 50;

        if(depth == 0){
            return (initialDepth % 2 == 1) ? -score : score;
        }

        float currentScore, max = 0;
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++){
                chain = new Chain();
                if(depth % 2 == 1) stone = new Stone(color ? "white" : "black", chain);
                else stone = new Stone(color ? "black" : "white", chain);
                stone.setPosition(i, j);
                int[] lib = Control.verifyLiberties(board, stone, i, j);
                if(board[i][j] == null && lib[0] + lib[1] != 0){
                    currentScore = getQScore(board, i, j, depth - 1, initialDepth);
                    if(currentScore > max){
                        max = currentScore;
                    }
                }
            }

        score += max * (depth / initialDepth);
        return (depth % 2 == 1) ? score : -score;
    }
}
