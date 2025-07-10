package view;

import model.map.*; // importa tus celdas y Maze aquí

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GameView extends JFrame {

    private final Map<Pair<Integer, Integer>, JButton> buttons = new HashMap<>();
    private final Maze maze;
    private Pair<Integer, Integer> playerPos;

    public GameView(Maze maze) {
        this.maze = maze;
        this.playerPos = new Pair<>(1, 1); // posición inicial del jugador

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
                    buttons.put(new Pair<>(x, y), button);
                }

                panel.add(button);
            }
        }

        updateView();
        this.setVisible(true);
    }

    private void updateView() {
        int size = maze.size();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Pair<Integer, Integer> pos = new Pair<>(x, y);
                JButton btn = buttons.get(pos);
                if (btn != null) {
                    btn.setText(" ");

                    // Habilitamos solo las celdas adyacentes
                    if (isAdjacent(x, y, model.Player$.MODULE$.position() , playerPos.getValue())) {
                        btn.setEnabled(true);
                    } else {
                        btn.setEnabled(false);
                    }
                }
            }
        }

        // Mostrar jugador
        JButton playerBtn = buttons.get(playerPos);
        if (playerBtn != null) {
            playerBtn.setText("P");
        }
    }

    private void tryMoveTo(int x, int y) {
        if (maze.isWalkable(x, y)) {
            playerPos = new Pair<>(x, y);
            updateView();

            if (maze.isExit(x, y)) {
                JOptionPane.showMessageDialog(this, "¡Has escapado!");
                System.exit(0);
            }
        }
    }

    private boolean isAdjacent(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2) == 1;
    }

}
