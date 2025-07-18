package view;

import model.Game;
import model.map.*;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import model.*;
import scala.Function1;
import scala.Tuple2;
import scala.collection.JavaConverters;
import scala.collection.immutable.List;
import scala.runtime.BoxedUnit;
import view.utils.*;

public class GameView extends JFrame implements View  {

    private final Map<Pair<Integer, Integer>, JButton> buttons = new HashMap<>();
    private Game game;
    private Maze maze;
    private Player player;
    private List<Guardian> guardians;
    private Map<Pair<Integer, Integer>, Guardian> guardiansPosition;
    private JLabel scoreLabel;
    private JLabel livesLabel;

    public GameView(Game game) {
        this.game = game;
        this.maze = game.maze();
        this.player = game.player();
        this.guardians = game.guardians();
        this.guardiansPosition = new HashMap<>();

        int size = maze.size();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(50 * size, 50 * size);

        // Panel for live and score
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.LIGHT_GRAY);
        
        scoreLabel = new JLabel("Score: " + player.score());
        livesLabel = new JLabel(" | Lives: " + player.lives());

        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        livesLabel.setFont(new Font("Arial", Font.BOLD, 16));

        topPanel.add(scoreLabel);
        topPanel.add(livesLabel);
        
        this.add(topPanel, BorderLayout.NORTH);

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
        scoreLabel.setText("Score: " + player.score());
        livesLabel.setText(" | Lives: " + player.lives());
        int size = maze.size();
        
        guardiansPosition.clear();
        
        // Convert Scala List to java
        JavaConverters.seqAsJavaList(guardians).forEach(guardian -> {
            Tuple2<Object, Object> pos = guardian.position();
            Pair<Integer, Integer> position = new Pair<>(
                    (Integer) pos._1(),
                    (Integer) pos._2()
            );
            guardiansPosition.put(position, guardian);
        });
        
        Tuple2<Object, Object> posP = player.position();
        int xP = (Integer) posP._1();
        int yP = (Integer) posP._2();

        buttons.values().forEach(btn -> btn.setText(" "));

        // Drawing of the guardians
        guardiansPosition.forEach((pos, guardian) -> {
            JButton btn = buttons.get(pos);
            btn.setText("G");
            
        });
        
        System.out.println("xP" + xP + " yP" + yP);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Pair<Integer, Integer> pos = new Pair<>(x, y);
                JButton btn = buttons.get(pos);
                if (btn != null) {
                    if(x==xP && y==yP){
                        btn.setText("웃");
                    }

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
