package tpjava.vue;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import tpjava.model.MonModel;

public class MonVue extends JFrame {
    private JPanel gridPanel;

    public MonVue(MonModel model, int size) {
        super("MVC JFrame avec Grille");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 800);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        gridPanel = new JPanel(new GridLayout(size, size, 1, 1));
        for (int i = 1; i <= size * size; i++) {
            JPanel cell = new JPanel();
            cell.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    cell.setBackground(java.awt.Color.GRAY);
                }
            });
            gridPanel.add(cell);
        }
        this.add(gridPanel, BorderLayout.CENTER);
    }
}
