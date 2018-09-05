package implementation;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static java.lang.Math.abs;

//clasa ce reprezinta un jucator artificial
public class Computer extends Player {

    //numarul de patrate dintr-o linie a tablei de joc
    private static int size;
    //reteaua neuronala
    private static MultiLayerNetwork net;
    //ultima retea neuronala folosita
    private static int lastNetUsed = 0;
    //daca tabla se afla in starea initiala
    private static boolean firstState = true;

    //revin la starea initiala
    static void resetFirstState(){
        firstState = true;
    }

    //aduc in memorie reteaua (daca este cazul), setez dimensiunea tablei si culoarea jucatorului
    Computer(int size, String color) {
        //creez o mutare pentru jucatorul curent
        move = new int[2];
        //setez culoarea acestuia
        this.color = color.equals("black");
        //daca nu am o retea in memorie sau daca aceasta este pentru alta dimensiune a tablei, o aduc in memorie pe cea potrivita
        if (net == null || lastNetUsed != size) {
            //setez dimensiunea unei linii / coloane
            Computer.size = size;
            //caut reteaua potrivita
            String pathName = "";
            switch(size) {
                case 10 :
                    pathName = "src/main/resources/nets/09.zip";
                    break;
                case 14 :
                    pathName = "src/main/resources/nets/13.zip";
                    break;
                case 20 :
                    pathName = "src/main/resources/nets/19.zip";
                    break;
                default :
                    System.exit(100);
            }
            //aduc in memorie reteaua potrivita
            //daca exista, o incarc, altfel o creez prima data
            File f = new File(pathName);
            if(f.exists() && f.isFile()) {
                net = loadNetwork(pathName);
            }
            else{
                createNetwork(pathName);
                net = loadNetwork(pathName);
            }
            //retin ultima retea folosita
            lastNetUsed = size;
        }
        //pornesc urmarirea progresului antrenamentului retelei
        showTrainingUI();
    }

    //transform starea curenta a tablei intr-un vector input pentru retea
    private INDArray computeInput() {
        int position = 0;
        int inputSize = size * size;
        INDArray input = Nd4j.zeros(1, inputSize);
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                //adaug in vector 1 pentru jucator, -1 pentru oponent si 0 pentru neocupat
                if (Board.hasStone(i, j))
                    if (!Board.getStone(i, j).getColor().equals(color))
                        input.putScalar(new int[]{0, position}, 1);
                    else
                        input.putScalar(new int[]{0, position}, -1);
                else
                    input.putScalar(new int[]{0, position}, 0);
                position++;
            }
        return input;
    }

    //verific daca mutarea data de retea este valida
    //calculez numarul de libertati pe care le-ar avea atat piatra cat si lantul acesteia
    private int verifyLiberties(int line, int column) {
        int liberties = 0;

        if (Board.hasStone(line, column)) {
            return liberties;
        }
        if (line > 0){
            if(!Board.hasStone(line - 1, column)){
                liberties++;
            }
            else if (Board.getStone(line - 1, column).getColor().equals(color)){
                liberties += Board.getStone(line - 1, column).getChain().getLibertiesNumber() - 1;
            }
        }
        if (line < Board.getTileNumber()){
            if (!Board.hasStone(line + 1, column)){
                liberties++;
            }
            else if (Board.getStone(line + 1, column).getColor().equals(color)){
                liberties += Board.getStone(line + 1, column).getChain().getLibertiesNumber() - 1;
            }
        }
        if (column > 0){
            if (!Board.hasStone(line, column - 1)){
                liberties++;
            }
            else if (Board.getStone(line, column - 1).getColor().equals(color)){
                liberties += Board.getStone(line, column - 1).getChain().getLibertiesNumber() - 1;
            }
        }
        if (column < Board.getTileNumber()){
            if (!Board.hasStone(line, column + 1)){
                liberties++;
            }
            else if (Board.getStone(line, column + 1).getColor().equals(color)){
                liberties += Board.getStone(line, column + 1).getChain().getLibertiesNumber() - 1;
            }
        }
        return liberties;
    }

    //verific daca este KO
    private boolean verifyKO() {
        return Board.getStonesNr() > 0 && Arrays.deepEquals(Board.getBoard(), Board.getLastState());
    }

    //trimit mutarea data de reteaua neuronala
    public int[] getMove() {
        //daca este stare initiala, spun explicit mutarea
        if(firstState){
            //trec la o stare intermediara
            firstState = false;
            //specific mutarea
            move[0] = size / 4;
            move[1] = size / 4;
            TableRow.netScore = 0;
            return move;
        }

        //aflu output-ul dat de retea
        INDArray input = computeInput();
        INDArray output = net.output(input);

        //gasesc scorul maxim pentru a verifica daca se adevereste predictia
        float maxim1 = -1, pos1 = 0;
        for ( int i = 0; i < output.length(); i++){
            if(output.getFloat(i) > maxim1){
                pos1 = i;
                maxim1 = output.getFloat(i);
            }
        }

        //antrenez reteaua si reiau output-ul dat de aceasta
        trainNetwork(output);
        output = net.output(input);
        System.out.println(output);

        //transform vectorul dat de retea in vector de numere reale
        Float[] floatOutput = new Float[output.length()];
        for ( int i = 0; i < output.length(); i++)
            floatOutput[i] = output.getFloat(i);

        do {
            int dif = size / 2;
            //gasesc mutarea cu scor maxim
            int maxPos = 0, maxPosLine = 0, maxPosColumn = 0;
            int line, column;
            for (int i = 1; i < floatOutput.length; i++){
                line = i / size;
                column = i % size;
                if (floatOutput[i] / ((abs(line - dif) + abs(column - dif)))
                        > floatOutput[maxPos] / (abs(maxPosLine - dif) + abs(maxPosColumn - dif))){
                    maxPos = i;
                    maxPosLine = line;
                    maxPosColumn = column;
                }
            }

            TableRow.netScore = floatOutput[maxPos];
            TableRow.prediction = !(maxPos != pos1);

            //System.out.println(maxPos);
            //daca scorul maxim gasit este -1, atunci nu se mai exista loc liber pe tabla
            if (floatOutput[maxPos] == -1) {
                move[0] = -1;
                move[1] = -1;
                return move;
            }

            //aflu linia si coloana locului specificat de retea
            move[0] = maxPosLine;
            move[1] = maxPosColumn;

            //retin ca mutarea a fost incercata, asignez valoarea minima locului
            //functia de activare a stratului de output imi ofera numere peste 0
            floatOutput[maxPos] = (float) -1;
            //verific daca mutarea este valida; daca nu, incerc mutarea cu scorul imediat mai mic
        } while(verifyLiberties(move[0], move[1]) == 0 || verifyKO());

        return move;
    }

    //functie de antrenare a retelei; primesc output-ul oferit de retea
    private void trainNetwork(INDArray output) {
        int inputSize = size * size;
        //gasesc mutarea cea mai buna
        INDArray bestMove = new QScores(size, this.color).getBoardScores();
        //creez un vector (pentru output-ul dat de retea) ce poate fi folosit la antrenare
        INDArray processedOutput = Nd4j.zeros(1, inputSize);
        for(int i = 0; i < inputSize; i++)
            processedOutput.putScalar(new int[]{0, i}, output.getDouble(i));
        //creez setul de date de antrenare
        DataSet ds = new DataSet(processedOutput, bestMove);
        //antrenez reteaua pentru un numar de epochs
        for (int epoch = 0; epoch < 3; epoch++) net.fit(ds);
    }

    //creez o retea neuronala
    private void createNetwork(String path) {
        int netSize = size * size;
        //definesc o retea neuronala cu mai multe straturi
        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                //numarul de iteratii la antrenare
                .iterations(1)
                //modul cum initializez ponderea de pe arce
                .weightInit(WeightInit.RELU_UNIFORM)
                //algoritm de optimizare a antrenarii
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                //functia de actualizare a ponderilor de pe arce
                .updater(Updater.NESTEROVS)
                //rata de invatare
                .learningRate(0.0001)
                //specificatii pentru a limita overfitting-ul la antrenare
                .gradientNormalization(GradientNormalization.RenormalizeL2PerParamType).
                        gradientNormalizationThreshold(0.5)
                .regularization(true).dropOut(0.5).l1(0.00001).l2(0.00001)
                //definesc straturile (tip de strat, numar de noduri, functie de activare)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(netSize).nOut(netSize / 2).
                        activation(Activation.IDENTITY).build())
                .layer(1, new DenseLayer.Builder().nIn(netSize / 2).nOut(netSize / 3).
                        activation(Activation.ELU).build())
                .layer(2, new DenseLayer.Builder().nIn(netSize / 3).nOut(netSize / 3).
                        activation(Activation.ELU).build())
                .layer(3, new DenseLayer.Builder().nIn(netSize / 3).nOut(netSize / 2).
                        activation(Activation.ELU).build())
                .layer(4, new OutputLayer.Builder(LossFunctions.LossFunction.
                        MEAN_SQUARED_LOGARITHMIC_ERROR).activation(Activation.RELU)
                        .nIn(netSize / 2).nOut(netSize).build())
                //creez configuratia
                .backprop(true).pretrain(false).build();

        //creez o retea pe baza configuratiei
        net = new MultiLayerNetwork(configuration);
        net.init();

        //salvez reteaua pe disk
        File locationToSave = new File(path);
        try {
            ModelSerializer.writeModel(net, locationToSave, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Net saved.");
    }

    //incarc reteaua neuronala in memorie
    private MultiLayerNetwork loadNetwork(String path) {
        File locationToLoad = new File(path);
        MultiLayerNetwork restored = null;
        try {
            restored = ModelSerializer.restoreMultiLayerNetwork(locationToLoad);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Net loaded.");
        return restored;
    }

    //activez procesul de urmarire al antrenarii retelei
    private void showTrainingUI() {
        //initializez interfata back-end
        UIServer uiServer = UIServer.getInstance();

        //setez unde sa se depoziteze informatiile despre retea
        StatsStorage statsStorage = new InMemoryStatsStorage();
        //la ce interval sa se actualizeze informatiile afisate
        int listenerFrequency = 1;
        //colectez informatiile despre retea, in timp ce se antreneaza
        net.setListeners(new StatsListener(statsStorage, listenerFrequency));

        //activez interfata grafica
        //http://localhost:9000/train
        uiServer.attach(statsStorage);
    }
}
