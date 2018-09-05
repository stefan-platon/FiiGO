package implementation;

import java.util.ArrayList;
import java.util.List;

//clasa ce reprezinta un lant
class Chain {
    //structura in care retin pietrele ce apartin aceluiasi lant
    private ArrayList<Stone> stones = new ArrayList<>();

    //calculez numarul de libertati ale lantului
    int getLibertiesNumber() {
        int libertiesNumber = 0;
        //libertatile ce apartin pietrelor din lant
        List<Integer> previousLiberties = new ArrayList<>();
        //pentru fiecare piatra din lant, verific cate libertati nealocate altei pietre din acelasi lant are
        for (Stone stone : stones) {
            int line = stone.getLine();
            int column = stone.getColumn();
            //verific (intr-o directie) sa nu ies de pe tabla si daca exista o libertate nealocata unei pietre din acelasi lant
            //daca da, aloc libertatea si cresc numarul de libertati
            //procedez similar pentru celelalte directii
            if(line > 0){
                if(!Board.hasStone(line - 1, column)) {
                    int position = (line - 1) * (Board.getTileNumber() + 1) + column;
                    if (!previousLiberties.contains(position)) {
                        previousLiberties.add(position);
                        libertiesNumber++;
                    }
                }
            }
            if(line < Board.getTileNumber()){
                if(!Board.hasStone(line + 1, column)){
                    int position = (line + 1) * (Board.getTileNumber() + 1) + column;
                    if (!previousLiberties.contains(position)) {
                        previousLiberties.add(position);
                        libertiesNumber++;
                    }
                }
            }
            if(column > 0){
                if(!Board.hasStone(line, column - 1)){
                    int position = line * (Board.getTileNumber() + 1) + column - 1;
                    if (!previousLiberties.contains(position)) {
                        previousLiberties.add(position);
                        libertiesNumber++;
                    }
                }
            }
            if(column < Board.getTileNumber()){
                if(!Board.hasStone(line, column + 1)){
                    int position = line * (Board.getTileNumber() + 1) + column + 1;
                    if (!previousLiberties.contains(position)) {
                        previousLiberties.add(position);
                        libertiesNumber++;
                    }
                }
            }
        }
        return libertiesNumber;
    }

    ArrayList<Stone> getStones() {
        return stones;
    }

    //adaug o piatra in lant si refac referintele
    void addStone(Stone stone) {
        stone.setChain(this);
        stones.add(stone);
    }

    //parcurg al doilea lant si adaug pietrele acestuia la lantul curent
    void join(Chain chain) {
        for(int i = 0; i < chain.getStones().size(); i++){
            this.addStone(chain.getStones().get(i));
        }
    }
}
