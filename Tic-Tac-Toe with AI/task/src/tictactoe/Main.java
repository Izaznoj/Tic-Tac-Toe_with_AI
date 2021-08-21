package tictactoe;

import java.util.*;

enum Message {
    START("Input command: "),
    AI_MOVE_EASY("Making move level \"easy\""),
    AI_MOVE_MEDIUM("Making move level \"medium\""),
    AI_MOVE_HARD("Making move level \"hard\""),
    USER_MOVE("Enter the coordinates: "),
    BAD_PARAMETERS("Bad parameters!"),
    NOT_EMPTY("This cell is occupied! Choose another one!"),
    NOT_NUMBER("You should enter numbers!"),
    OUT_OF_RANGE("Coordinates should be from 1 to 3!");

    private final String message;

    Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

enum State {
    NOT_FINISHED(""),
    DRAW("Draw"),
    X_WINS("X wins"),
    O_WINS("O wins"),
    EXIT("exit");

    private final String message;

    State(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

enum PlayerSymbol {
    X("X"), O("O"), NONE(" ");

    String symbol;

    PlayerSymbol(String symbol) {
        this.symbol = symbol;
    }
}

enum StartParameter {
    USER("user"), EASY_AI("easy"), MEDIUM_AI("medium"), HARD_AI("hard");

    String name;
    StartParameter(String name) {
        this.name = name;
    }

    public static boolean containsValue(String value) {
        for (StartParameter p : StartParameter.values()) {
            if (value.equals(p.name)) {
                return true;
            }
        }
        return false;
    }
}

abstract class Player {

    PlayerSymbol playerSymbol;
    Player otherPlayer;

    Player(PlayerSymbol playerSymbol) {
        this.playerSymbol = playerSymbol;
    }
    abstract void makeMove(Table table);

    public boolean isWinning(Table table) {
        return (table.cells[0].player.equals(playerSymbol) && table.cells[1].player.equals(playerSymbol) && table.cells[2].player.equals(playerSymbol) ||
                table.cells[3].player.equals(playerSymbol) && table.cells[4].player.equals(playerSymbol) && table.cells[5].player.equals(playerSymbol) ||
                table.cells[6].player.equals(playerSymbol) && table.cells[7].player.equals(playerSymbol) && table.cells[8].player.equals(playerSymbol) ||
                table.cells[0].player.equals(playerSymbol) && table.cells[3].player.equals(playerSymbol) && table.cells[6].player.equals(playerSymbol) ||
                table.cells[1].player.equals(playerSymbol) && table.cells[4].player.equals(playerSymbol) && table.cells[7].player.equals(playerSymbol) ||
                table.cells[2].player.equals(playerSymbol) && table.cells[5].player.equals(playerSymbol) && table.cells[8].player.equals(playerSymbol) ||
                table.cells[0].player.equals(playerSymbol) && table.cells[4].player.equals(playerSymbol) && table.cells[8].player.equals(playerSymbol) ||
                table.cells[2].player.equals(playerSymbol) && table.cells[4].player.equals(playerSymbol) && table.cells[6].player.equals(playerSymbol));
    }

}

abstract class AIPlayer extends Player {

    AIPlayer(PlayerSymbol playerSymbol) {
        super(playerSymbol);
    }

    public void makeRandomMove(Table table){
        Random random = new Random();
        int n = random.nextInt(9);
        while (!table.cells[n].isEmpty()) {
            n = random.nextInt(9);
        }
        table.setInput(n, playerSymbol);
    }
}

class UserPlayer extends Player {

    UserPlayer(PlayerSymbol playerSymbol) {
        super(playerSymbol);
    }

    @Override
    void makeMove(Table table){
        Scanner scanner = new Scanner(System.in);
        boolean rightInput = false;
        int x, y;
        while (!rightInput) {
            System.out.println(Message.USER_MOVE.getMessage());
            try {
                x = scanner.nextInt();
                y = scanner.nextInt();
                if (x < 1 || x > 3 || y < 1 || y > 3) {
                    System.out.println(Message.OUT_OF_RANGE.getMessage());
                } else if (!table.cells[(x-1) * 3 + y - 1].isEmpty()){
                    System.out.println(Message.NOT_EMPTY.getMessage());
                } else {
                    rightInput = true;
                    table.setInput(x, y, playerSymbol);
                }
            } catch (InputMismatchException e) {
                System.out.println(Message.NOT_NUMBER.getMessage());
                scanner.nextLine();
            }
        }
    }
}

class EasyAIPlayer extends AIPlayer {

    EasyAIPlayer(PlayerSymbol playerSymbol) {
        super(playerSymbol);
    }

    @Override
    void makeMove(Table table){
        System.out.println(Message.AI_MOVE_EASY.getMessage());
        makeRandomMove(table);
    }
}

class MediumAIPlayer extends AIPlayer {

    MediumAIPlayer(PlayerSymbol playerSymbol) {
        super(playerSymbol);
    }

    @Override
    void makeMove(Table table) {
        System.out.println(Message.AI_MOVE_MEDIUM.getMessage());
        if (!(tryWinOrBlock(table, this) || tryWinOrBlock(table, otherPlayer))) {
            makeRandomMove(table);
        }
    }

    boolean tryWinOrBlock(Table table, Player player){
        for (int i : table.emptyCells()) {
            table.setInput(i, player.playerSymbol);
            if (player.isWinning(table)) {
                table.setInput(i, this.playerSymbol);
                return true;
            }
            table.setInput(i, PlayerSymbol.NONE);
        }
        return false;
    }
}

class HardAIPlayer extends AIPlayer {

    HardAIPlayer(PlayerSymbol playerSymbol) {
        super(playerSymbol);
    }

    @Override
    void makeMove(Table table) {
        System.out.println(Message.AI_MOVE_HARD.getMessage());
        table.setInput(minimax(table,this).index,this.playerSymbol);
    }

    private Move minimax(Table newTable, Player player) {

        if (this.otherPlayer.isWinning(newTable)) {
            return new Move(-10);
        } else if (this.isWinning(newTable)) {
            return new Move(10);
        } else if (newTable.emptyCells().isEmpty()) {
            return new Move(0);
        }



        List<Move> moves = new ArrayList<>();

        for (int i : newTable.emptyCells()) {
            Move move = new Move();
            move.index = i;

            newTable.cells[i].player = player.playerSymbol;
            move.score = minimax(newTable, player.otherPlayer).score;

            newTable.cells[i].player = PlayerSymbol.NONE;
            moves.add(move);
        }

        int bestMove = 0;

        if (player == this) {
            int bestScore = -10000;
            for (int i = 0; i < moves.size(); i++){
                if (moves.get(i).score > bestScore){
                    bestScore = moves.get(i).score;
                    bestMove = i;
                }
            }
        } else {
            int bestScore = 10000;
            for(int i = 0; i < moves.size(); i++){
                if(moves.get(i).score < bestScore){
                    bestScore = moves.get(i).score;
                    bestMove = i;
                }
            }
        }
        return moves.get(bestMove);
    }
}

class Move {
    int index;
    int score;

    Move() {}

    Move(int score) {
        this.score = score;
    }
}

class Table {
    Cell[] cells;

    Table() {
        this.cells = new Cell[9];
        this.setInitialState();
    }

    public void setInitialState() {
        for (int i = 0; i < 9; i++) {
            cells[i] = new Cell();
            cells[i].player = PlayerSymbol.NONE;
        }
    }

    public List<Integer> emptyCells() {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (cells[i].isEmpty()) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    public void setInput(int x, int y, PlayerSymbol player) {
        setInput((x - 1) * 3 + y - 1, player);
    }

    public void setInput(int n, PlayerSymbol player) {
        cells[n].player = player;
    }

    public void drawTable() {
        System.out.println("---------");
        System.out.printf("| %s %s %s |\n", cells[0].player.symbol, cells[1].player.symbol, cells[2].player.symbol);
        System.out.printf("| %s %s %s |\n", cells[3].player.symbol, cells[4].player.symbol, cells[5].player.symbol);
        System.out.printf("| %s %s %s |\n", cells[6].player.symbol, cells[7].player.symbol, cells[8].player.symbol);
        System.out.println("---------");
    }

}

class Cell {
    PlayerSymbol player;

    Cell() {
    }

    public boolean isEmpty() {
        return player.equals(PlayerSymbol.NONE);
    }
}

class Game {
    Table table;
    State state;
    Player player1;
    Player player2;

    Game() {
        menu();
    }

    public void menu() {
        state = State.NOT_FINISHED;
        while (!state.equals(State.EXIT)) {
            System.out.println(Message.START.getMessage());
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            String[] parameters = input.split(" ");
            while ((parameters.length == 3 && !parameters[0].equals("start")
                    && !(StartParameter.containsValue(parameters[1]))
                    && !(StartParameter.containsValue(parameters[2])))
                    || (parameters.length == 1 && !parameters[0].equals("exit"))
            || (parameters.length != 1 && parameters.length != 3)) {
                System.out.println(Message.BAD_PARAMETERS.getMessage());
                System.out.println(Message.START.getMessage());
                input = scanner.nextLine();
                parameters = input.split(" ");
            }
            if (parameters[0].equals("start")) {
                startGame(parameters[1], parameters[2]);
            } else {
                state = State.EXIT;
            }
        }
    }

    public void startGame(String player1, String player2) {
        table = new Table();
        if (player1.equals(StartParameter.USER.name)) {
            this.player1 = new UserPlayer(PlayerSymbol.X);
        } else if (player1.equals(StartParameter.EASY_AI.name)){
            this.player1 = new EasyAIPlayer(PlayerSymbol.X);
        } else if (player1.equals(StartParameter.MEDIUM_AI.name)){
            this.player1 = new MediumAIPlayer(PlayerSymbol.X);
        } else {
            this.player1 = new HardAIPlayer(PlayerSymbol.X);
        }

        if (player2.equals(StartParameter.USER.name)) {
            this.player2 = new UserPlayer(PlayerSymbol.O);
        } else if (player2.equals(StartParameter.EASY_AI.name)) {
            this.player2 = new EasyAIPlayer(PlayerSymbol.O);
        } else if (player2.equals(StartParameter.MEDIUM_AI.name)){
            this.player2 = new MediumAIPlayer(PlayerSymbol.O);
        } else {
            this.player2 = new HardAIPlayer(PlayerSymbol.O);
        }

        this.player1.otherPlayer = this.player2;
        this.player2.otherPlayer = this.player1;
        playGame();
    }

    public void playGame() {
        table.drawTable();
        while(state == State.NOT_FINISHED) {
            if (Arrays.stream(table.cells).filter(cell -> cell.player.equals(PlayerSymbol.X)).count()
                    == Arrays.stream(table.cells).filter(cell -> cell.player.equals(PlayerSymbol.O)).count()) {
                player1.makeMove(this.table);
            } else {
                player2.makeMove(this.table);
            }
            table.drawTable();
            checkState();
        }
    }

    public void checkState(){
        if (player1.isWinning(table)) {
            state = player1.playerSymbol.equals(PlayerSymbol.X) ? State.X_WINS : State.O_WINS;
            System.out.println(state.getMessage());
            menu();
        } else if (player2.isWinning(table)) {
            state = player2.playerSymbol.equals(PlayerSymbol.X) ? State.X_WINS : State.O_WINS;
            System.out.println(state.getMessage());
            menu();
        } else if (checkDraw()) {
            state = State.DRAW;
            System.out.println(state.getMessage());
            menu();
        }
    }

    public boolean checkDraw() {
        return state.equals(State.NOT_FINISHED) && Arrays.stream(table.cells).noneMatch(Cell::isEmpty);
    }
}

public class Main {
    public static void main(String[] args) {
        new Game();
    }
}
