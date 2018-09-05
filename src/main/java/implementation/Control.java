package implementation;

import javafx.application.Platform;
import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;

//clasa ce se ocupa cu controlul unui joc
class Control {
    //tipul de joc : om vs om, om vs IA, IA vs IA
    private int gameType;
    //jucatorii
    private Player player1;
    private Player player2;
    //variabila ce retine al carui jucator este randul; pentru tipul om vs om
    private boolean player1Turn;
    private static View view;

    //om vs om
    Control(Player player1) {
        this.gameType = 0;
        this.player1 = player1;
        player1Turn = true;
    }

    //om vs IA
    Control(Player player1, int size, View viewObj) {
        this.gameType = 1;
        this.player1 = player1;
        this.player2 = new Computer(size, "white");
        view = viewObj;
    }

    //IA vs IA
    Control(int size, View viewObj) {
        this.gameType = 2;
        this.player1 = new Computer(size, "black");
        this.player2 = new Computer(size, "white");
        view = viewObj;
    }

    //elimin pietrele ce apartin aceluiasi lant de pe tabla
    //returnez numarul de piese pentru calcularea scorului
    static int removeStones(Stone[][] board, int line, int column, boolean context){
        int nr = 0;
        //apelata din Control
        if(context) {
            for (Stone stone : board[line][column].getChain().getStones()) {
                Platform.runLater(() -> View.removeStone(stone));
                Board.removeStone(stone.getLine(), stone.getColumn());
                nr++;
            }
        }
        //apelata din QScores
        else{
            for(Stone stone: board[line][column].getChain().getStones()){
                board[stone.getLine()][stone.getColumn()] = null;
                nr++;
            }
        }
        return nr;
    }

    //verific daca se pot elimina piesele oponentului (au ramas fara libertati in urma plasarii unei pietre)
    //returnez numarul de piese pentru calcularea scorului
    static int verifyNeighbourStones(Stone[][] board, String color, int line, int column, boolean context){
        int nr = 0;
        if(line > 0 && board[line - 1][column] != null){
            board[line - 1][column].decLibertiesNumber();
            if(!board[line - 1][column].getColor().equals(color) &&
                    board[line - 1][column].getChain().getLibertiesNumber() == 0   ){
                nr += removeStones(board,line - 1, column, context);
            }
        }
        if(line < Board.getTileNumber() && board[line + 1][column] != null){
            board[line + 1][column].decLibertiesNumber();
            if(!board[line + 1][column].getColor().equals(color) &&
                    board[line + 1][column].getChain().getLibertiesNumber() == 0   ){
                nr += removeStones(board,line + 1, column, context);
            }
        }
        if(column > 0 && board[line][column - 1] != null){
            board[line][column - 1].decLibertiesNumber();
            if(!board[line][column - 1].getColor().equals(color) &&
                    board[line][column - 1].getChain().getLibertiesNumber() == 0   ){
                nr += removeStones(board, line, column - 1, context);
            }
        }
        if(column < Board.getTileNumber() && board[line][column + 1] != null){
            board[line][column + 1].decLibertiesNumber();
            if(!board[line][column + 1].getColor().equals(color) &&
                    board[line][column + 1].getChain().getLibertiesNumber() == 0   ){
                nr += removeStones(board, line, column + 1, context);
            }
        }
        return nr;
    }

    //verific daca pot plasa o piesa (daca piesa sau lantul acesteia ar avea un numar nenul de libertati)
    static int[] verifyLiberties(Stone[][] board, Stone stone, int line, int column){
        int backup = 0;

        if(line > 0){
            if(board[line - 1][column] == null){
                stone.incLibertiesNumber();
            }
            else if(board[line - 1][column].getColor().equals(stone.getColor())){
                    backup = backup + board[line - 1][column].getChain().getLibertiesNumber() - 1;
                }
        }
        if(line < Board.getTileNumber()){
            if(board[line + 1][column] == null){
                stone.incLibertiesNumber();
            }
            else if(board[line + 1][column].getColor().equals(stone.getColor())){
                    backup = backup + board[line + 1][column].getChain().getLibertiesNumber() - 1;
                }
        }
        if(column > 0){
            if(board[line][column - 1] == null){
                stone.incLibertiesNumber();
            }
            else if(board[line][column - 1].getColor().equals(stone.getColor())){
                    backup = backup + board[line][column - 1].getChain().getLibertiesNumber() - 1;
                }
        }
        if(column < Board.getTileNumber()){
            if(board[line][column + 1] == null){
                stone.incLibertiesNumber();
            }
            else if(board[line][column + 1].getColor().equals(stone.getColor())){
                    backup = backup + board[line][column + 1].getChain().getLibertiesNumber() - 1;
                }
        }
        int[] lib = new int[2];
        lib[0] = stone.getLibertiesNumber();
        lib[1] = backup;
        return lib;
    }

    //dupa asezarea unei piese, combin lanturile vecine intr-unul singur
    static boolean combineChains(Stone[][] board, Stone stone, String color, int line, int column){
        boolean contor = false;
        if(line > 0 && board[line - 1][column] != null && board[line - 1][column].getColor().equals(color) &&
                board[line - 1][column].getChain() != stone.getChain()){
            stone.getChain().join(board[line - 1][column].getChain());
            contor = true;
        }
        if(line < Board.getTileNumber() && board[line + 1][column] != null && board[line + 1][column].getColor().equals(color)
                && board[line + 1][column].getChain() != stone.getChain()){
            stone.getChain().join(board[line + 1][column].getChain());
            contor = true;
        }
        if(column > 0 && board[line][column - 1] != null && board[line][column - 1].getColor().equals(color) &&
                board[line][column - 1].getChain() != stone.getChain()){
            stone.getChain().join(board[line][column - 1].getChain());
            contor = true;
        }
        if(column < Board.getTileNumber() && board[line][column + 1] != null && board[line][column + 1].getColor().equals(color)
                && board[line][column + 1].getChain() != stone.getChain()){
            stone.getChain().join(board[line][column + 1].getChain());
            contor = true;
        }
        return contor;
    }

    //verific daca este KO
    private boolean verifyKO() {
        return Board.getStonesNr() > 0 && Arrays.deepEquals(Board.getBoard(), Board.getLastState());
    }

    //functie ce se ocupa de demersul jocului
    int control(){
        int threadSleepTime = 500;
        //om vs om
        if (gameType == 0){
            //setez culoarea cu care se joaca in functie de al cui este randul
            String color;
            if (player1Turn)
                color = "black";
            else
                color = "white";

            //aflu mutarea jucatorului
            int[] move = player1.getMove();
            int line = move[0];
            int column = move[1];

            //creez o piatra noua in pozitia data de jucator
            Chain chain = new Chain();
            Stone stone = new Stone(color, chain);
            stone.setPosition(line, column);

            //verific daca mutarea este valida
            if(line >= 0 && column >=0 && line <= Board.getTileNumber() && column <= Board.getTileNumber() && !Board.hasStone(line, column)){
                int[] lib = verifyLiberties(Board.getBoard(), stone, line, column);
                if(lib[0] + lib[1] == 0 || verifyKO()){
                    return -2;
                }

                //elimin efectul ultimei piese asezata pe tabla
                //asez piatra pe tabla
                Platform.runLater(() -> {
                    View.pieceGroupRemoveEffect();
                    stone.setShape(View.drawStone(color, line, column, false, player1Turn));
                });
                Board.saveLastState();
                Board.setStone(line, column, stone);

                //verific daca am luat pietre detinute de oponent
                verifyNeighbourStones(Board.getBoard(), stone.getColor(), line, column, true);
                //efectuez eventualele modificari ale lanturilor proprii ce pot aparea in urma plasarii pietrei
                combineChains(Board.getBoard(), stone, stone.getColor(), line, column);
            }
            else return -1;
            stone.getChain().getLibertiesNumber();
            //dau randul celuilalt jucator
            player1Turn = !player1Turn;
        }
        //om vs IA
        if (gameType == 1){
            //aflu mutarea jucatorului uman
            int[] move = player1.getMove();
            final int line1 = move[0];
            final int column1 = move[1];

            //creez o piatra noua in pozitia data de jucator
            Chain chain = new Chain();
            final Stone stone1 = new Stone("black", chain);
            stone1.setPosition(line1, column1);

            //verific daca mutarea este valida
            if(line1 >= 0 && column1 >=0 && line1 <= Board.getTileNumber() &&
                    column1 <= Board.getTileNumber() &&
                    !Board.hasStone(line1,column1)){
                int[] lib = verifyLiberties(Board.getBoard(), stone1, line1, column1);
                if(lib[0] + lib[1] == 0 || verifyKO())
                    return -2;

                //elimin efectul ultimei piese asezata pe tabla
                //asez piatra pe tabla
                Platform.runLater(() -> {
                    View.pieceGroupRemoveEffect();
                    stone1.setShape(View.drawStone("black", line1, column1,
                            false, true));
                });
                Board.saveLastState();
                Board.setStone(line1, column1, stone1);

                //verific daca am luat pietre detinute de oponent
                verifyNeighbourStones(Board.getBoard(), stone1.getColor(),
                        line1, column1, true);
                //efectuez eventualele modificari ale lanturilor proprii ce pot aparea in urma plasarii pietrei
                combineChains(Board.getBoard(), stone1, stone1.getColor(),
                        line1, column1);
            }
            else return -1;

            //aflu mutarea inteligentei artificiale
            move = player2.getMove();
            final int line2 = move[0];
            final int column2 = move[1];

            //creez o piatra noua in pozitia data de jucator
            chain = new Chain();
            final Stone stone2 = new Stone("white", chain);
            stone2.setPosition(line2, column2);

            TableRow.position = "" + line2 + ", " + column2;

            //setez libertatile
            int[] lib = verifyLiberties(Board.getBoard(), stone2, line2, column2);
            TableRow.libertiesNumber = lib[0];

            //elimin efectul ultimei piese asezata pe tabla
            //asez piatra pe tabla
            Platform.runLater(() -> {
                View.pieceGroupRemoveEffect();
                stone2.setShape(View.drawStone("white", line2, column2, false, false));
            });
            Board.saveLastState();
            Board.setStone(line2, column2, stone2);

            //verific daca am luat pietre detinute de oponent
            verifyNeighbourStones(Board.getBoard(), stone2.getColor(), line2, column2, true);
            //efectuez eventualele modificari ale lanturilor proprii ce pot aparea in urma plasarii pietrei
            TableRow.groupAttached = combineChains(Board.getBoard(), stone2, stone2.getColor(), line2, column2);
            TableRow.groupLibertiesNumber = stone2.getChain().getLibertiesNumber();

            Platform.runLater(() -> {
                view.addTableRow(TableRow.position, TableRow.groupAttached, TableRow.libertiesNumber,
                        TableRow.groupLibertiesNumber, TableRow.netScore, TableRow.prediction);
            });
        }
        //IA vs IA
        if (gameType == 2){
            //cat timp nu am ajuns la o stare finala
            boolean gameOn = true;
            while(gameOn) {
                //aflu mutarea jucatorului artificial
                int[] move = player1.getMove();
                final int line1 = move[0];
                final int column1 = move[1];

                //daca nu mai are nici o mutare valida, inchei jocul
                if (line1 == -1 && column1 == -1){
                    System.out.println("Game over.");
                    return 0;
                }

                //creez o piatra noua in pozitia data de jucator
                Chain chain = new Chain();
                final Stone stone1 = new Stone("black", chain);
                stone1.setPosition(line1, column1);

                TableRow.position = "" + line1 + ", " + column1;

                //setez libertatile
                int[] lib = verifyLiberties(Board.getBoard(), stone1, line1, column1);
                TableRow.libertiesNumber = lib[0];

                //elimin efectul ultimei piese asezata pe tabla
                //asez piatra pe tabla
                Platform.runLater(() -> {
                    View.pieceGroupRemoveEffect();
                    stone1.setShape(View.drawStone("black", line1, column1, false, true));
                });
                try {
                    Thread.sleep(threadSleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Board.saveLastState();
                Board.setStone(line1, column1, stone1);

                //verific daca am luat pietre detinute de oponent
                verifyNeighbourStones(Board.getBoard(), stone1.getColor(), line1, column1, true);
                //efectuez eventualele modificari ale lanturilor proprii ce pot aparea in urma plasarii pietrei
                TableRow.groupAttached = combineChains(Board.getBoard(), stone1, stone1.getColor(), line1, column1);
                TableRow.groupLibertiesNumber = stone1.getChain().getLibertiesNumber();

                Platform.runLater(() -> {
                    view.addTableRow(TableRow.position, TableRow.groupAttached, TableRow.libertiesNumber,
                            TableRow.groupLibertiesNumber, TableRow.netScore, TableRow.prediction);
                });

                //aflu mutarea celui de al doilea jucator artificial
                move = player2.getMove();
                final int line2 = move[0];
                final int column2 = move[1];

                //daca nu mai are nici o mutare valida, inchei jocul
                if (line2 == -1 && column2 == -1){
                    System.out.println("Game over.");
                    return 0;
                }

                //creez o piatra noua in pozitia data de jucator
                chain = new Chain();
                final Stone stone2 = new Stone("white", chain);
                stone2.setPosition(line2, column2);

                TableRow.position = "" + line2 + ", " + column2;

                //setez libertatile
                lib = verifyLiberties(Board.getBoard(), stone2, line2, column2);
                TableRow.libertiesNumber = lib[0];

                //elimin efectul ultimei piese asezata pe tabla
                //asez piatra pe tabla
                Platform.runLater(() -> {
                    View.pieceGroupRemoveEffect();
                    stone2.setShape(View.drawStone("white", line2, column2, false, false));
                });
                try {
                    Thread.sleep(threadSleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Board.saveLastState();
                Board.setStone(line2, column2, stone2);

                //verific daca am luat pietre detinute de oponent
                verifyNeighbourStones(Board.getBoard(), stone2.getColor(), line2, column2, true);
                //efectuez eventualele modificari ale lanturilor proprii ce pot aparea in urma plasarii pietrei
                TableRow.groupAttached = combineChains(Board.getBoard(), stone2, stone2.getColor(), line2, column2);
                TableRow.groupLibertiesNumber = stone2.getChain().getLibertiesNumber();

                Platform.runLater(() -> {
                    view.addTableRow(TableRow.position, TableRow.groupAttached, TableRow.libertiesNumber,
                            TableRow.groupLibertiesNumber, TableRow.netScore, TableRow.prediction);
                });
            }
        }
        return 0;
    }
}
