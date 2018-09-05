package implementation;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

//clasa responsabila de interfata grafica
public class View extends Application {
    //fereastra aplicatiei
    private static Stage window = new Stage();
    //pentru a obtine dimensiunea ecranului
    private static Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();

    //patratele de pe tabla
    private static Group tileGroup = new Group();
    //pietrele de pe tabla
    private static Group pieceGroup = new Group();
    //piatra semi-transparenta pentru a evidentia pozitia cursorului
    private static Group ghostPiece = new Group();

    //tipul implicit al jocului
    private int gameType = 2;
    //pentru a sti ce culoare sa aiba ghostPiece
    private boolean player1Turn = true;

    //tabel ve contine informatii suplimentare
    TableView<TableRow> table;

    //pornirea aplicariei
    public static void main(String[] args) {
        launch(args);
    }

    //initializez ferastra aplicatiei
    @Override
    public void start(Stage primaryStage) throws Exception {
        window.setTitle("FIIGO");
        //setez ca scena initiala fereastra de alegere a dimensiunii tablei si a tipului de joc
        window.setScene(new Scene(createIntroductoryScreen()));
        window.setResizable(false);
        window.setX(100);
        window.setY(0);
        window.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        window.show();
    }

    //panou unde se alege dimensiunea tablei si tipul jocului
    private Parent createIntroductoryScreen(){
        window.setWidth(screenSize.getWidth() / 1.7);
        window.setHeight(screenSize.getHeight() / 1.5);

        Pane root = new Pane();

        //obiect ce ajuta la pozitionarea elementelor
        GridPane gridpane = new GridPane();
        gridpane.setAlignment(Pos.CENTER);
        gridpane.setPadding(new Insets(35, 0, 0, 45));
        gridpane.setPrefWidth(360);
        gridpane.setPrefHeight(350);
        gridpane.setVgap(20);

        Label title = new Label("Welcome to FIIGO");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        title.setMinWidth(gridpane.getPrefWidth());
        title.setPadding(new Insets(0, 0, 0, 190));
        GridPane.setConstraints(title, 0, 0, 2, 1);

        Label statement = new Label("Please choose the board dimension:");
        statement.setFont(Font.font("Verdana", FontWeight.BOLD, 17));
        statement.setMinWidth(gridpane.getPrefWidth());
        statement.setPadding(new Insets(0, 0, 0, 5));
        GridPane.setConstraints(statement, 0, 1);

        //imagine de fundal pentru butoane
        Image image = new Image("file:src/main/resources/images/wood2.jpg", true);
        BackgroundImage bgImage = new BackgroundImage( image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                new BackgroundSize(gridpane.getWidth(), gridpane.getHeight(), false, false, true, true));
        Background background = new Background(bgImage);

        //efecte pentru butoane
        DropShadow shadowNormal = new DropShadow(BlurType.GAUSSIAN , Color.color(0.8431, 0.8667, 0.698),
                7,3,0,0);
        DropShadow shadowSelected = new DropShadow(BlurType.GAUSSIAN , Color.DARKCYAN , 7,1,0,0);

        Button dimension9 = new Button("9 x 9");
        dimension9.setBackground(background);
        dimension9.setMinWidth(gridpane.getPrefWidth());
        dimension9.setMinHeight(gridpane.getPrefHeight() / 4);
        dimension9.setFont(Font.font("Verdana",FontWeight.BOLD, FontPosture.ITALIC, 40));
        dimension9.setEffect(shadowNormal);
        //creez jocul folosind specificatiile date
        dimension9.setOnAction(e -> {
            Board.createBoard(9, screenSize.getWidth());
            window.setHeight((Board.getTileNumber() + 2.5) * Board.getTileSize());
            //afisez sau nu tabelul
            if(gameType == 0){
                window.setWidth((Board.getTileNumber() + 5) * Board.getTileSize());
            }
            else{
                window.setWidth((Board.getTileNumber() + 10) * Board.getTileSize());
            }
            //scena in care se desfasoara jocul propriu-zis
            window.setScene(new Scene(createGameScreen()));
        });
        dimension9.setOnMouseEntered(e -> dimension9.setEffect(shadowSelected));
        dimension9.setOnMouseExited(e -> dimension9.setEffect(shadowNormal));
        GridPane.setConstraints(dimension9, 0, 2, 1, 2);

        Button dimension13 = new Button("13 x 13");
        dimension13.setBackground(background);
        dimension13.setMinWidth(gridpane.getPrefWidth());
        dimension13.setMinHeight(gridpane.getPrefHeight() / 4);
        dimension13.setFont(Font.font("Verdana",FontWeight.BOLD, FontPosture.ITALIC, 40));
        dimension13.setEffect(shadowNormal);
        //creez jocul folosind specificatiile date
        dimension13.setOnAction(e -> {
            Board.createBoard(13, screenSize.getWidth());
            window.setHeight((Board.getTileNumber() + 2.5) * Board.getTileSize());
            //afisez sau nu tabelul
            if(gameType == 0){
                window.setWidth((Board.getTileNumber() + 7) * Board.getTileSize());
            }
            else{
                window.setWidth((Board.getTileNumber() + 12.5) * Board.getTileSize());
            }
            //scena in care se desfasoara jocul propriu-zis
            window.setScene(new Scene(createGameScreen()));
        });
        dimension13.setOnMouseEntered(e -> dimension13.setEffect(shadowSelected));
        dimension13.setOnMouseExited(e -> dimension13.setEffect(shadowNormal));
        GridPane.setConstraints(dimension13, 0, 4, 1, 2);

        Button dimension19 = new Button("19 x 19");
        dimension19.setBackground(background);
        dimension19.setMinWidth(gridpane.getPrefWidth());
        dimension19.setMinHeight(gridpane.getPrefHeight() / 4);
        dimension19.setFont(Font.font("Verdana",FontWeight.BOLD, FontPosture.ITALIC, 40));
        dimension19.setEffect(shadowNormal);
        //creez jocul folosind specificatiile date
        dimension19.setOnAction(e -> {
            Board.createBoard(19, screenSize.getWidth());
            window.setHeight((Board.getTileNumber() + 2.5) * Board.getTileSize());
            //afisez sau nu tabelul
            if(gameType == 0){
                window.setWidth((Board.getTileNumber() + 8) * Board.getTileSize());
            }
            else{
                window.setWidth((Board.getTileNumber() + 17) * Board.getTileSize());
            }
            //scena in care se desfasoara jocul propriu-zis
            window.setScene(new Scene(createGameScreen()));
        });
        dimension19.setOnMouseEntered(e -> dimension19.setEffect(shadowSelected));
        dimension19.setOnMouseExited(e -> dimension19.setEffect(shadowNormal));
        GridPane.setConstraints(dimension19, 0, 6, 1, 2);

        Label statement2 = new Label("Please choose the game type:");
        statement2.setFont(Font.font("Verdana", FontWeight.BOLD, 17));
        statement2.setMinWidth(gridpane.getPrefWidth());
        statement2.setPadding(new Insets(0, 0, 0, 40));
        GridPane.setConstraints(statement2, 1, 1);

        //grup de butoane pentru alegerea unui tip de joc
        ToggleGroup toogleGroup = new ToggleGroup();

        RadioButton rb1 = new RadioButton("Human vs Human");
        rb1.setMinWidth(gridpane.getPrefWidth());
        rb1.setMinHeight(gridpane.getPrefHeight() / 4);
        rb1.setPadding(new Insets(0, 0, 0, 20));
        rb1.setFont(Font.font("Verdana",FontWeight.BOLD, FontPosture.ITALIC, 23));
        GridPane.setConstraints(rb1, 1, 2, 1, 2);
        rb1.setToggleGroup(toogleGroup);
        rb1.setUserData("0");

        RadioButton rb2 = new RadioButton("Human vs Computer");
        rb2.setMinWidth(gridpane.getPrefWidth());
        rb2.setMinHeight(gridpane.getPrefHeight() / 4);
        rb2.setPadding(new Insets(0, 0, 0, 20));
        rb2.setFont(Font.font("Verdana",FontWeight.BOLD, FontPosture.ITALIC, 23));
        GridPane.setConstraints(rb2, 1, 4, 1, 2);
        rb2.setToggleGroup(toogleGroup);
        rb2.setUserData("1");

        RadioButton rb3 = new RadioButton("Computer vs Computer");
        rb3.setMinWidth(gridpane.getPrefWidth());
        rb3.setMinHeight(gridpane.getPrefHeight() / 4);
        rb3.setPadding(new Insets(0, 0, 0, 20));
        rb3.setFont(Font.font("Verdana",FontWeight.BOLD, FontPosture.ITALIC, 23));
        GridPane.setConstraints(rb3, 1, 6, 1, 2);
        rb3.setToggleGroup(toogleGroup);
        rb3.setUserData("2");
        rb3.setSelected(true);

        //daca un buton este apasat, actualizez datele
        toogleGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            if (toogleGroup.getSelectedToggle() != null)
                gameType = Integer.parseInt(toogleGroup.getSelectedToggle().getUserData().toString());
        });

        //stochez toate elementele in obiectul responsabil de pozitionare
        gridpane.getChildren().addAll(title, statement, dimension9, dimension13, dimension19, statement2, rb1, rb2, rb3);
        root.getChildren().add(gridpane);

        //imagine de fundal pentru fereastra
        image = new Image("file:src/main/resources/images/wood1.jpg", true);
        bgImage = new BackgroundImage( image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                new BackgroundSize(window.getWidth(), window.getHeight(), false, false, true, true));
        background = new Background(bgImage);
        root.setBackground(background);

        return root;
    }

    //elimin efectul ultimei pietre asezate
    static void pieceGroupRemoveEffect(){
        //verific daca exista o piesa asezata anterior
        if (pieceGroup.getChildren().size() > 1)
            pieceGroup.getChildren().get(pieceGroup.getChildren().size() - 2).setEffect(
                    new DropShadow(BlurType.GAUSSIAN , Color.DARKCYAN , 0,0,0,0));
    }

    //functie de desenare a unei pietre
    static Ellipse drawStone(String color, int boardY, int boardX, boolean ghost, boolean player){
        //creez o forma noua
        Ellipse piece = new Ellipse((boardX + 1) * Board.getTileSize(), (boardY + 1) * Board.getTileSize(),
                Board.getTileSize() / 2.1, Board.getTileSize() / 2.1);

        //setez imaginea corespunzatoare culorii date
        ImagePattern pattern;
        if(color.equals("black")){
            pattern = new ImagePattern(new Image("file:src/main/resources/images/black.png"));
        }
        else{
            pattern = new ImagePattern(new Image("file:src/main/resources/images/white.png"));
        }
        piece.setFill(pattern);

        //setez transparenta si efectul de ultima piesa plasata in functie de parametrii dati
        if(player){
            if(ghost){
                piece.setOpacity(0.5);
                ghostPiece.getChildren().add(piece);
            }
            else{
                piece.setEffect(new DropShadow(BlurType.GAUSSIAN , Color.DARKCYAN , 15,0.7,0,0));
                pieceGroup.getChildren().add(piece);
            }
        }
        else{
            if(pieceGroup.getChildren().size() > 1){
                pieceGroup.getChildren().get(pieceGroup.getChildren().size() - 2).setEffect(
                        new DropShadow(BlurType.GAUSSIAN , Color.CRIMSON , 0,0,0,0));
            }
            piece.setEffect(new DropShadow(BlurType.GAUSSIAN , Color.CRIMSON , 15,0.7,0,0));
            pieceGroup.getChildren().add(piece);
        }
        return piece;
    }

    //elimin o piatra de pe tabla
    static void removeStone(Stone stone){
        pieceGroup.getChildren().remove(stone.getShape());
    }

    //scena in care se desfasoara jocul propriu-zis
    private Parent createGameScreen(){
        Pane root = new Pane();

        //imaginea de fundal a butoanelor
        Image image = new Image("file:src/main/resources/images/wood2.jpg", true);
        BackgroundImage bgImage = new BackgroundImage( image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                new BackgroundSize(200, 100, false, false, true, true));
        Background background = new Background(bgImage);

        //efecte pentru butoane
        DropShadow shadowNormal = new DropShadow(BlurType.GAUSSIAN , Color.color(0.8431, 0.8667, 0.698),
                7,3,0,0);
        DropShadow shadowSelected = new DropShadow(BlurType.GAUSSIAN , Color.DARKCYAN , 7,1,0,0);

        Button restartButton = new Button("Restart");
        restartButton.setBackground(background);
        restartButton.setMinWidth(170);
        restartButton.setMinHeight(75);
        restartButton.setFont(Font.font("Verdana",FontWeight.BOLD, FontPosture.ITALIC, 30));
        restartButton.setEffect(shadowNormal);
        //resetez tabla de joc
        restartButton.setOnAction(e -> {
            tileGroup = new Group();
            pieceGroup = new Group();
            ghostPiece = new Group();
            Board.resetBoard();
            window.setScene(new Scene(createGameScreen()));
        });
        restartButton.setOnMouseEntered(e -> restartButton.setEffect(shadowSelected));
        restartButton.setOnMouseExited(e -> restartButton.setEffect(shadowNormal));

        Button backButton = new Button("Back");
        backButton.setBackground(background);
        backButton.setMinWidth(170);
        backButton.setMinHeight(75);
        backButton.setFont(Font.font("Verdana",FontWeight.BOLD, FontPosture.ITALIC, 30));
        backButton.setEffect(shadowNormal);
        //ma intorc la fereastra initiala
        backButton.setOnAction(e -> {
            tileGroup = new Group();
            pieceGroup = new Group();
            ghostPiece = new Group();
            gameType = 2;
            Board.resetBoard();
            Computer.resetFirstState();
            window.setScene(new Scene(createIntroductoryScreen()));
        });
        backButton.setOnMouseEntered(e -> backButton.setEffect(shadowSelected));
        backButton.setOnMouseExited(e -> backButton.setEffect(shadowNormal));

        //coloana position
        TableColumn<TableRow, String> positionColumn = new TableColumn<>("Position");
        positionColumn.setPrefWidth(60);
        positionColumn.setMinWidth(60);
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("positionObj"));
        //coloana groupAttached
        TableColumn<TableRow, Boolean> groupAttachedColumn = new TableColumn<>("Attached");
        groupAttachedColumn.setPrefWidth(60);
        groupAttachedColumn.setMinWidth(60);
        groupAttachedColumn.setCellValueFactory(new PropertyValueFactory<>("groupAttachedObj"));
        //coloana libertiesNumber
        TableColumn<TableRow, Integer> libertiesNumberColumn = new TableColumn<>("Liberties");
        libertiesNumberColumn.setPrefWidth(60);
        libertiesNumberColumn.setMinWidth(60);
        libertiesNumberColumn.setCellValueFactory(new PropertyValueFactory<>("libertiesNumberObj"));
        //coloana groupLibertiesNumber
        TableColumn<TableRow, Integer> groupLibertiesNumberColumn = new TableColumn<>("Group liberties");
        groupLibertiesNumberColumn.setPrefWidth(95);
        groupLibertiesNumberColumn.setMinWidth(95);
        groupLibertiesNumberColumn.setCellValueFactory(new PropertyValueFactory<>("groupLibertiesNumberObj"));
        //coloana netScore
        TableColumn<TableRow, Float> netScoreColumn = new TableColumn<>("Network score");
        netScoreColumn.setPrefWidth(95);
        netScoreColumn.setMinWidth(95);
        netScoreColumn.setCellValueFactory(new PropertyValueFactory<>("netScoreObj"));
        //coloana prediction
        TableColumn<TableRow, Boolean> predictionColumn = new TableColumn<>("Prediction success");
        predictionColumn.setPrefWidth(115);
        predictionColumn.setMinWidth(115);
        predictionColumn.setCellValueFactory(new PropertyValueFactory<>("predictionObj"));

        table = new TableView<>();
        table.getColumns().addAll(positionColumn, groupAttachedColumn, libertiesNumberColumn, groupLibertiesNumberColumn,
                netScoreColumn, predictionColumn);

        if(gameType == 0){
            root.getChildren().addAll(tileGroup, pieceGroup, ghostPiece, restartButton, backButton);
        }
        else{
            root.getChildren().addAll(tileGroup, pieceGroup, ghostPiece, restartButton, backButton, table);
        }


        //setez grosimea liniilor si pozitia butoanelor in functie de dimensiunea tablei
        double strokeWidth = 3;
        switch(Board.getTileNumber()) {
            case 9:
                strokeWidth = 3;
                //afisez sau nu tabelul
                if(gameType == 0){
                    restartButton.setLayoutX(670);
                    backButton.setLayoutX(670);
                    restartButton.setLayoutY(250);
                    backButton.setLayoutY(250 + 1.5 * backButton.getMinHeight());
                }
                else{
                    restartButton.setLayoutX(805);
                    backButton.setLayoutX(805);
                    restartButton.setLayoutY(Board.getTileSize());
                    backButton.setLayoutY(Board.getTileSize() + 1.5 * backButton.getMinHeight());
                    table.setLayoutX(665);
                    table.setLayoutY(Board.getTileSize() + 3 * backButton.getMinHeight());
                    table.setPrefHeight(340);
                    table.setMaxHeight(340);
                }
                break;
            case 13:
                strokeWidth = 2;
                //afisez sau nu tabelul
                if(gameType == 0){
                    restartButton.setLayoutX(710);
                    restartButton.setLayoutY(250);
                    backButton.setLayoutX(710);
                    backButton.setLayoutY(250 + 1.5 * backButton.getMinHeight());
                }
                else{
                    restartButton.setLayoutX(830);
                    restartButton.setLayoutY(Board.getTileSize());
                    backButton.setLayoutX(830);
                    backButton.setLayoutY(Board.getTileSize() + 1.5 * backButton.getMinHeight());
                    table.setLayoutX(680);
                    table.setLayoutY(Board.getTileSize() + 3 * backButton.getMinHeight());
                    table.setPrefHeight(385);
                    table.setMaxHeight(385);
                }
                break;
            case 19:
                strokeWidth = 1;
                //afisez sau nu tabelul
                if(gameType == 0){
                    restartButton.setLayoutX(713);
                    restartButton.setLayoutY(250);
                    backButton.setLayoutX(713);
                    backButton.setLayoutY(250 + 1.5 * backButton.getMinHeight());
                }
                else{
                    restartButton.setLayoutX(865);
                    restartButton.setLayoutY(Board.getTileSize());
                    backButton.setLayoutX(865);
                    backButton.setLayoutY(Board.getTileSize() + 1.5 * backButton.getMinHeight());
                    table.setLayoutX(705);
                    table.setLayoutY(Board.getTileSize() + 3 * backButton.getMinHeight());
                    table.setPrefHeight(420);
                    table.setMaxHeight(420);
                }
                break;
        }

        //afisez liniile tablei
        for (int i = 0; i < Board.getTileNumber(); i++) {
            for (int j = 0; j < Board.getTileNumber(); j++) {
                Rectangle tile = new Rectangle((i + 1) * Board.getTileSize(), (j + 1) * Board.getTileSize(),
                        Board.getTileSize(), Board.getTileSize());
                tile.setStroke(Color.BLACK);
                tile.setStrokeWidth(strokeWidth);
                tile.setFill(Color.TRANSPARENT);
                tileGroup.getChildren().add(tile);
            }
        }

        //obiect ce se ocupa de desfasurarea jocului
        Control control;
        //creez obiecte pentru jucatori on functie de tipul jocului ales
        Human human = new Human();
        if (gameType == 0)
            control = new Control(human);
        else if (gameType == 1)
            control = new Control(human, Board.getTileNumber() + 1, this);
        else {
            control = new Control(Board.getTileNumber() + 1, this);
            Thread thread = new Thread(control::control);
            thread.start();
        }

        //la apasarea pe tabla preiau pozitia si trimit clasei de control
        root.setOnMouseClicked(e -> {
            //daca se joaca calculator vs calculator, nu înregistrez acțiunea
            if (gameType != 2) {
                double posX = e.getX();
                double posY = e.getY();

                int boardCol = (int)Math.round((posX / Board.getTileSize()) - 1);
                int boardLine = (int)Math.round((posY / Board.getTileSize()) - 1);

                human.setMove(boardLine, boardCol);

                control.control();

                //la om vs om, ofer randul celuilalt jucator (schimb culoarea)
                if (gameType == 0)
                    player1Turn = !player1Turn;
            }
        });

        //afisez o piatra semi-transparenta in pozitia data de mouse (daca e pe tabla de joc)
        root.setOnMouseMoved(e -> {
            double posX = e.getX();
            double posY = e.getY();

            int boardX = (int)Math.round((posX / Board.getTileSize()) - 1);
            int boardY = (int)Math.round((posY / Board.getTileSize()) - 1);

            if(boardX >= 0 && boardY >=0 && boardX <= Board.getTileNumber() && boardY <= Board.getTileNumber()){
                //elimin ultima piatra afisata
                if(!ghostPiece.getChildren().isEmpty()){
                    ghostPiece.getChildren().remove(0);
                }
                //setez culoarea in functie de al cui jucator este randul
                drawStone(player1Turn ? "black" : "white", boardY, boardX, true, true);
            }
        });

        //imagine de fundal pentru fereastra
        image = new Image("file:src/main/resources/images/wood1.jpg", true);
        bgImage = new BackgroundImage( image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                new BackgroundSize(window.getWidth(), window.getHeight(), false, false, true, true));
        background = new Background(bgImage);
        root.setBackground(background);

        return  root;
    }

    //inserez o linie in tabelul de informatii
    void addTableRow(String position, boolean groupAttached, int libertiesNumber, int groupLibertiesNumber,
                     float netScore, boolean prediction){
        TableRow row = new TableRow(position, groupAttached, libertiesNumber, groupLibertiesNumber, netScore, prediction);
        table.getItems().add(row);
    }
}
