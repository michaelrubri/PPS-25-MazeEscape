package view;

import model.Game;
import model.map.*;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import model.Player;
import scala.Function1;
import scala.Tuple2;
import scala.runtime.BoxedUnit;
import view.utils.*;

public class GameView extends JFrame implements View  {

    private final Map<Pair<Integer, Integer>, JButton> buttons = new HashMap<>();
    private Game game;
    private Maze maze;
    private Player player;

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
                button.setOpaque(true);
                button.setEnabled(false);
                // Add button only if we can move there
                if (maze.isWalkable(new scala.Tuple2<>(x, y))) {
                    final int fx = x;
                    final int fy = y;
                    button.addActionListener(e -> {
                        EventBus.publish(new CellClickEvent(fx, fy));  
                    });
                    buttons.put(new Pair<>(x, y), button);
                }

                if(cell instanceof FloorCell)  button.setBackground(Color.WHITE);
                else if (cell instanceof WallCell) button.setBackground(Color.BLACK);
                else button.setBackground(Color.YELLOW);

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
        System.out.println("xP: " + xP + " yP: " + yP);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Pair<Integer, Integer> pos = new Pair<>(x, y);
                // Button corresponding to the position
                JButton btn = buttons.get(pos);
                if (btn != null) {
                    if(x==xP && y==yP){
                        btn.setText("웃");
                    }
                    else btn.setText(" ");

                    // Enable buttons only if adjacent
                    if (game.isAdjacent(new scala.Tuple2<>(x, y), new scala.Tuple2<>(xP, yP)) &&
                            maze.isWalkable(new scala.Tuple2<>(x, y))) {
                        btn.setEnabled(true);
                    }
                    else 
                        btn.setEnabled(false);
                    
                }
            }
        }
        
    }
    
    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

 

    @Override
    public void showFightChoice(Function1<String, BoxedUnit> choice) {

    }
    

    @Override
    public void showPuzzle(String question, Function1<String, BoxedUnit> answer) {
        
        String usersAnswer = JOptionPane.showInputDialog(
                this, question, 
                "Quiz",
                JOptionPane.QUESTION_MESSAGE
        );

        answer.apply(usersAnswer);
    }

}
