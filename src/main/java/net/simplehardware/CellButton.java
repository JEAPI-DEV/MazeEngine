package net.simplehardware;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class CellButton extends JPanel {

    final int x, y;
    private Mode mode = Mode.FLOOR;
    private int playerId = 0;
    private final MazeEditor editor;

    public CellButton(int x, int y, MazeEditor editor) {
        this.x = x;
        this.y = y;
        this.editor = editor;
        setPreferredSize(new Dimension(60, 60));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createLineBorder(new Color(189, 189, 189), 1));

        // Click and drag
        addMouseListener(
            new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        applyCurrentMode();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (
                        (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0
                    ) {
                        Mode m = editor.getCurrentMode();
                        if (m == Mode.WALL || m == Mode.FLOOR) {
                            applyCurrentMode();
                        }
                    }
                }
            }
        );
    }

    private void applyCurrentMode() {
        Mode current = editor.getCurrentMode();
        int pid = (current == Mode.START ||
                current == Mode.FINISH ||
                current == Mode.SHEET ||
                isFormMode(current))
            ? editor.getCurrentPlayerId()
            : 0;
        setMode(current, pid);
    }

    public void setMode(Mode m, int pid) {
        this.mode = m;
        this.playerId = (m == Mode.START ||
                m == Mode.FINISH ||
                m == Mode.SHEET ||
                isFormMode(m))
            ? pid
            : 0;
        updateColor();
        repaint();
    }

    private void updateColor() {
        switch (mode) {
            case FLOOR -> setBackground(new Color(245, 245, 245));
            case WALL -> setBackground(new Color(120, 20, 20));
            case START -> setBackground(new Color(0, 255, 255));
            case FINISH -> setBackground(new Color(50, 255, 0));
            case SHEET -> setBackground(new Color(255, 152, 0));
            default -> {
                if (isFormMode(mode)) {
                    setBackground(getFormColor(mode));
                }
            }
        }
    }

    public Mode getMode() {
        return mode;
    }

    public int getPlayerId() {
        return playerId;
    }

    private boolean isFormMode(Mode m) {
        return m.name().startsWith("FORM_");
    }

    private Color getFormColor(Mode formMode) {
        String formLetter = formMode.name().substring(5);
        char letter = formLetter.charAt(0);

        return switch (letter) {
            case 'A' -> new Color(224, 60, 60);
            case 'B' -> new Color(255, 255, 0);
            case 'C' -> new Color(196, 106, 255);
            case 'D' -> new Color(101, 187, 255);
            case 'E' -> new Color(255, 0, 255);
            case 'F' -> new Color(0, 255, 255);
            case 'G' -> new Color(255, 165, 0);
            case 'H' -> new Color(128, 0, 128);
            case 'I' -> new Color(255, 192, 203);
            case 'J' -> new Color(165, 42, 42);
            case 'K' -> new Color(255, 215, 0);
            case 'L' -> new Color(0, 128, 0);
            case 'M' -> new Color(128, 0, 0);
            case 'N' -> new Color(0, 0, 128);
            case 'O' -> new Color(255, 140, 0);
            case 'P' -> new Color(75, 0, 130);
            case 'Q' -> new Color(240, 128, 128);
            case 'R' -> new Color(50, 205, 50);
            case 'S' -> new Color(255, 69, 0);
            case 'T' -> new Color(255, 20, 147);
            case 'U' -> new Color(64, 224, 208);
            case 'V' -> new Color(220, 20, 60);
            case 'W' -> new Color(255, 255, 224);
            case 'X' -> new Color(152, 251, 152);
            case 'Y' -> new Color(238, 130, 238);
            case 'Z' -> new Color(70, 130, 180);
            default -> new Color(128, 128, 128);
        };
    }

    private Color getPlayerColor(int pid) {
        float hue = (pid - 1) * 0.125f;
        return Color.getHSBColor(hue, 0.7f, 0.9f);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
        FontMetrics fm = g.getFontMetrics();

        if (mode == Mode.START || mode == Mode.FINISH) {
            String text = (mode == Mode.START ? "@" : "!") + playerId;
            int tx = (getWidth() - fm.stringWidth(text)) / 2;
            int ty = (getHeight() + fm.getAscent()) / 2 - 4;
            g.drawString(text, tx, ty);
        } else if (isFormMode(mode)) {
            String formLetter = mode.name().substring(5);
            String text = formLetter + playerId;
            int tx = (getWidth() - fm.stringWidth(text)) / 2;
            int ty = (getHeight() + fm.getAscent()) / 2 - 4;
            g.drawString(text, tx, ty);
        } else if (mode == Mode.SHEET) {
            String text = "S" + playerId;
            int tx = (getWidth() - fm.stringWidth(text)) / 2;
            int ty = (getHeight() + fm.getAscent()) / 2 - 4;
            g.drawString(text, tx, ty);
        }
    }
}
