package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

public class MenuView {
    private JFrame frame;
    private JPanel panel;

    public MenuView(Consumer<String> onDifficultySelected) {
        frame = new JFrame("Select Difficulty");
        panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));

        JLabel label = new JLabel("Choose difficulty:", SwingConstants.CENTER);
        panel.add(label);

        addButton("Easy", onDifficultySelected);
        addButton("Normal", onDifficultySelected);
        addButton("Hard", onDifficultySelected);

        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null); // center
        frame.setVisible(true);
    }

    private void addButton(String text, Consumer<String> callback) {
        JButton button = new JButton(text);
        button.addActionListener(e -> {
            frame.dispose(); // close menu
            callback.accept(text.toLowerCase()); // send "easy", "medium", "hard"
        });
        panel.add(button);
    }
}
