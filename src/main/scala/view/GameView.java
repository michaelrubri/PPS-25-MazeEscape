/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package view;

import model.Game;
import model.entities.Guardian;
import model.entities.Player;
import model.map.*;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import scala.Function1;
import scala.Tuple2;
import scala.collection.JavaConverters;
import scala.runtime.BoxedUnit;
import view.utils.*;

public class GameView extends JFrame implements View  {

    private final Map<Pair<Integer, Integer>, JButton> buttons = new HashMap<>();
    private final Game game;
    private final Maze maze;
    private final JLabel scoreLabel;
    private final JLabel livesLabel;

    public GameView(Game game) {
        this.game = game;
        this.maze = game.maze();
        int size = maze.size();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // Top panel for lives and score
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.LIGHT_GRAY);
        scoreLabel = new JLabel();
        livesLabel = new JLabel();
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        livesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(scoreLabel);
        topPanel.add(livesLabel);
        this.add(topPanel, BorderLayout.NORTH);

        // Creations of buttons for every cell
        GridLayout gridLayout = new GridLayout(size, size);
        gridLayout.setHgap(1);
        gridLayout.setVgap(1);
        JPanel grid = new JPanel(gridLayout);
        grid.setBackground(Color.DARK_GRAY);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                JButton button = new JButton();
                button.setOpaque(true);
                button.setBorderPainted(true);
                button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                button.setMargin(new Insets(0, 0, 0, 0));
                // Adding a listener
                final int fx = x;
                final int fy = y;
                button.addActionListener(e -> EventBus.publish(new CellClickEvent(fx, fy)));
                buttons.put(new Pair<>(x, y), button);
                grid.add(button);
            }
        }
        this.add(grid, BorderLayout.CENTER);
        initEventBus();
        SwingUtilities.invokeLater(this::updateView);
        this.setSize(50 * size, 50 * size + 50);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    private void initEventBus() {
        EventBus.subscribe(event -> {});
    }

    public void updateView() {
        SwingUtilities.invokeLater(() -> {
            // Read the updated state
            Player player = game.player();
            Tuple2<Object, Object> playerPos = player.position();
            int px = (Integer) playerPos._1();
            int py = (Integer) playerPos._2();
            // Update player's stats
            scoreLabel.setText("Score: " + player.score());
            livesLabel.setText(" | Lives: " + player.lives());
            // Update guardians
            Map<Pair<Integer, Integer>, Guardian> guardiansPos = new HashMap<>();
            List<Guardian> guardians = JavaConverters.seqAsJavaList(game.guardians());
            guardians.forEach(guardian -> {
                Tuple2<Object, Object> guardianPos = guardian.position();
                guardiansPos.put(new Pair<>((Integer) guardianPos._1(), (Integer) guardianPos._2()), guardian);
            });
            // Checks every cell
            for (int x = 0; x < maze.size(); x++) {
                for (int y = 0; y < maze.size(); y++) {
                    Pair<Integer, Integer> pos = new Pair<>(x, y);
                    JButton button = buttons.get(pos);
                    Cell cell = maze.getCell(x, y);
                    if (cell instanceof FloorCell) button.setBackground(Color.WHITE);
                    else if (cell instanceof WallCell) button.setBackground(Color.BLACK);
                    else button.setBackground(Color.YELLOW);
                    button.setText(" ");
                    if (guardiansPos.containsKey(pos)) {
                        button.setText("G");
                    }
                    if (x == px && y == py) {
                        button.setText("웃");
                    }
                    // Define if the cell is clickable
                    boolean isAdjacent = game.isAdjacent(new Tuple2<>(px, py), new Tuple2<>(x, y));
                    boolean isWalkable = maze.isWalkable(new Tuple2<>(x, y));
                    boolean hasGuardian = guardiansPos.containsKey(pos);
                    boolean isDoor = cell instanceof DoorCell;
                    // Define valid actions
                    boolean enable =
                            (!hasGuardian && isWalkable && isAdjacent)
                            || (isDoor && isAdjacent)
                            || (hasGuardian && isAdjacent);
                    button.setEnabled(enable);
                }
            }
        });
    }
    
    @Override
    public void showMessage(String msg) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, msg));
    }

    @Override
    public void showPuzzle(String question, Function1<String, BoxedUnit> answer) {
        SwingUtilities.invokeLater(() -> {
            String userAns = JOptionPane.showInputDialog(
                    this, question, "Puzzle", JOptionPane.QUESTION_MESSAGE
            );
            answer.apply(userAns);
        });
    }
    
    @Override
    public void showFightChoice(Function1<String, BoxedUnit> choice) {
        SwingUtilities.invokeLater(() -> {
            String[] options = { "Logic", "Luck" };
            int selection = JOptionPane.showOptionDialog(
                    this,
                    "Choose fight type:",
                    "Fight choice",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );
            if (selection >= 0 && selection < options.length) {
                choice.apply(options[selection].toLowerCase());
            }
        });
    }

    @Override
    public void showEndGameMenu(boolean victory, Function1<String, BoxedUnit> choice) {
        SwingUtilities.invokeLater(() -> {
            String title = victory ? "You Win!" : "Game Over!";
            String[] options = { "Restart", "Exit" };
            int selection = JOptionPane.showOptionDialog(
                    this,
                    victory ? "Congratulations, you escaped" : "You failed to escape",
                    title,
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );
            if (selection >= 0 && selection < options.length) {
                choice.apply(options[selection].toLowerCase());
            }
        });
    }
    
}