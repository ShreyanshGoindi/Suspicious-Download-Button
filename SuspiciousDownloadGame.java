package suspiciousdownloadgame;
/*
 * SuspiciousDownloadGame.java
 * A playful demo inspired by "There's No Game", but themed as a suspicious download button.
 * The app insists there is no download, teases the user, and eventually reveals a puzzle-like sequence.
 * Safe, harmless, and only for fun.
 *
 * Run:
 *   javac SuspiciousDownloadGame.java && java SuspiciousDownloadGame
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SuspiciousDownloadGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SuspiciousDownloadGame::createAndShowGui);
    }

    private static void createAndShowGui() {
        JFrame frame = new JFrame("Totally Safe Downloader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);

        DownloadPanel panel = new DownloadPanel(frame);
        frame.setContentPane(panel);
        frame.setVisible(true);
        panel.startIntro();
    }
}

class DownloadPanel extends JPanel {
    private final JFrame parent;
    private final JLabel title;
    private final JButton downloadBtn;
    private final JTextArea narrator;
    private boolean admitted = false;
    private final Random rnd = new Random();
    private int clickCount = 0;

    DownloadPanel(JFrame parent) {
        this.parent = parent;
        setLayout(null);
        setBackground(new Color(0x111111));

        title = new JLabel("There is absolutely NO download here.");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Serif", Font.BOLD, 26));
        title.setBounds(140, 40, 600, 40);
        add(title);

        downloadBtn = new JButton("⬇️  Download Totally Safe File (not suspicious)");
        downloadBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        downloadBtn.setBackground(new Color(0xFF5E5E));
        downloadBtn.setForeground(Color.WHITE);
        downloadBtn.setFocusPainted(false);
        downloadBtn.setBounds(250, 200, 300, 80);
        add(downloadBtn);

        narrator = new JTextArea();
        narrator.setEditable(false);
        narrator.setOpaque(false);
        narrator.setForeground(new Color(0xBBBBBB));
        narrator.setFont(new Font("Monospaced", Font.PLAIN, 14));
        narrator.setBounds(20, 400, 760, 80);
        narrator.setText("(The system voice: You really shouldn’t click that.)");
        add(narrator);

        setFocusable(true);

        downloadBtn.addActionListener(e -> onDownloadClicked());
        downloadBtn.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!admitted && rnd.nextDouble() < 0.15) dodgeButton();
            }
        });
    }

    void startIntro() {
        typeNarration("Welcome. There’s totally no download here. Definitely no virus. No fun either.", 30);
    }

    private void typeNarration(String text, int delayMs) {
        narrator.setText("");
        new Thread(() -> {
            for (int i = 0; i < text.length(); i++) {
                String partial = text.substring(0, i + 1);
                SwingUtilities.invokeLater(() -> narrator.setText(partial));
                try { Thread.sleep(delayMs); } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    private void onDownloadClicked() {
        clickCount++;
        Toolkit.getDefaultToolkit().beep();

        if (!admitted) {
            String[] responses = {
                "No download available.",
                "Why are you clicking that?", 
                "Error: File vanished mysteriously.",
                "Don’t you trust me?", 
                "You’re persistent… aren’t you?"
            };

            JOptionPane.showMessageDialog(parent, responses[rnd.nextInt(responses.length)], "Warning", JOptionPane.WARNING_MESSAGE);

            if (clickCount > 5 && rnd.nextDouble() < 0.4) {
                admitted = true;
                JOptionPane.showMessageDialog(parent, "Fine. Maybe there IS a download. But it’s hidden.", "Suspicious", JOptionPane.INFORMATION_MESSAGE);
                revealPuzzle();
            }
        } else {
            revealPuzzle();
        }
    }

    private void dodgeButton() {
        int newX = rnd.nextInt(getWidth() - downloadBtn.getWidth() - 20);
        int newY = rnd.nextInt(getHeight() - downloadBtn.getHeight() - 100) + 60;
        downloadBtn.setLocation(newX, newY);
    }

    private void revealPuzzle() {
        JDialog dlg = new JDialog(parent, "Suspicious Download Puzzle", true);
        dlg.setSize(520, 360);
        dlg.setLocationRelativeTo(parent);

        JPanel board = new JPanel(null);
        board.setBackground(new Color(0x202020));

        String[] msgs = {"not a virus", "definitely fun", "press here"};
        Tile[] tiles = new Tile[3];
        for (int i = 0; i < 3; i++) {
            tiles[i] = new Tile(msgs[i]);
            tiles[i].setBounds(40 + i * 150, 80, 130, 100);
            board.add(tiles[i]);
        }

        JButton done = new JButton("I’m done");
        done.setBounds(200, 240, 120, 36);
        board.add(done);

        done.addActionListener(e -> {
            java.util.List<Tile> tileList = Arrays.asList(tiles);
            tileList.sort(Comparator.comparingInt(Tile::getX));

            String[] arranged = tileList.stream().map(Tile::getLabelText).toArray(String[]::new);
            String[] target = {"not a virus", "definitely fun", "press here"};

            boolean ok = Arrays.equals(arranged, target);

            if (ok) {
                JOptionPane.showMessageDialog(dlg, "Perfect order! Download unlocked.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                showFakeDownload();
            } else {
                JOptionPane.showMessageDialog(dlg, "That order doesn’t look right. Try again.", "Hmm…", JOptionPane.WARNING_MESSAGE);
            }
        });

        dlg.setContentPane(board);
        dlg.setVisible(true);
    }

    private void showFakeDownload() {
        JDialog progress = new JDialog(parent, "Downloading suspiciously…", true);
        progress.setSize(400, 140);
        progress.setLocationRelativeTo(parent);

        JLabel label = new JLabel("⬇️ Downloading suspicious_file.notexe", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        progress.add(label, BorderLayout.NORTH);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);
        progress.add(bar, BorderLayout.CENTER);

        new Thread(() -> {
            for (int i = 0; i <= 100; i++) {
                try { Thread.sleep(40 + rnd.nextInt(60)); } catch (InterruptedException ignored) {}
                int val = i;
                SwingUtilities.invokeLater(() -> bar.setValue(val));
            }
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(progress, "Download complete. (Or is it?)", "Done", JOptionPane.INFORMATION_MESSAGE);
                progress.dispose();
            });
        }).start();

        progress.getContentPane().setBackground(new Color(0x222222));
        progress.setVisible(true);
    }

    static class Tile extends JComponent {
        private final JLabel label;
        private Point offset;

        Tile(String text) {
            setLayout(new BorderLayout());
            setOpaque(true);
            setBackground(new Color(0x333333));
            setBorder(BorderFactory.createLineBorder(Color.GRAY));

            label = new JLabel(text, SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            add(label, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) { offset = e.getPoint(); }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    Point loc = getLocation();
                    setLocation(loc.x + e.getX() - offset.x, loc.y + e.getY() - offset.y);
                }
            });
        }

        String getLabelText() { return label.getText(); }
    }
}
