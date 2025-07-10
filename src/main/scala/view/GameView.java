package view;

import model.Game;
import model.map.*; // importa tus celdas y Maze aquí

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import model.Player;
import scala.Function1;
import scala.Tuple2;
import model.puzzle.*;
import scala.runtime.BoxedUnit;

public class GameView extends JFrame implements View  {

    private final Map<Pair<Integer, Integer>, JButton> buttons = new HashMap<>();
    private final Game game;
    private final Maze maze;
    private final Player player;

    public GameView(Game game) {
        this.game = game;
        this.maze = game.maze();
        this.player = game.player();

        int size = maze.size();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(50 * size, 50 * size);

        JPanel panel = new JPanel(new GridLayout(size, size));
        this.getContentPane().add(panel);

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Cell cell = maze.getCell(x, y);
                JButton button = new JButton();

                // Add button only if we can move there
                if (cell instanceof FloorCell || cell instanceof DoorCell) {
                    final int fx = x;
                    final int fy = y;
                    button.addActionListener(e -> tryMoveTo(fx, fy));
                    button.addActionListener(e -> {
                        EventBus.post(new CellClickEvent(x, y));  // Dispara el evento
                    });
                    buttons.put(new Pair<>(x, y), button);
                }

                panel.add(button);
            }
        }

        updateView();
        this.setVisible(true);
    }

    public void updateView() {
        int size = maze.size();

        Tuple2<Object, Object> posP = player.position();

        // Cast to integer
        int xP = (Integer) posP._1();
        int yP = (Integer) posP._2();
        
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Pair<Integer, Integer> pos = new Pair<>(x, y);
                Cell cell = maze.getCell(x, y);
                // Button corresponding to the position
                JButton btn = buttons.get(pos);
                if (btn != null) {
                    btn.setText(" "); // not sure we need this
                    
                    if(cell instanceof FloorCell)  btn.setBackground(Color.WHITE);
                    else if (cell instanceof WallCell)  btn.setBackground(Color.BLACK);
                    else btn.setBackground(Color.YELLOW);

                    // Enable buttons only if adjacent
                    
                    if (game.isAdjacent((x, y), (xP, yP)) && maze.isWalkable((x, y))) {}  
                        btn.setEnabled(true);
                    else 
                        btn.setEnabled(false);
                    
                }
            }
        }

        // Display player
        JButton playerBtn = buttons.get(player.position());
        if (playerBtn != null) {
            playerBtn.setText("P");
        }
    }
    
    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

 

    @Override
    public void showFightChoice(Function1<String, BoxedUnit> choice) {

    }

    private void tryMoveTo(int x, int y) {
        game.movePlayerTo((x, y))
        if (maze.isExit((x, y))) {
            showMessage("You have exited!");
            System.exit(0);
        }

    }

    @Override
    public void showPuzzle(String question, Function1<String, BoxedUnit> answer) {
        
        String usersAnswer = JOptionPane.showInputDialog(
                this, question, 
                "Quiz",
                JOptionPane.QUESTION_MESSAGE
        );

        // Verify
        if (usersAnswer != null) {  // Si el usuario no cancela
            
            boolean isCorrect = answer.apply(usersAnswer);//puzzle.checkAnswer(usersAnswer.trim().toLowerCase());

            if (isCorrect) {
                showMessage("Correct!"); 
                //unlock
            } else {
                showMessage("Wrong aswer...");
                //lock
                );
            }
        }
    }

}
