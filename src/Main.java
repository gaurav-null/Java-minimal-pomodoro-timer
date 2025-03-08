import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;

public class Main extends JFrame {
    private JLabel timerLabel;
    private JLabel infoLabel;
    private RoundedButton startButton;
    private RoundedButton breakButton;
    private Timer timer;
    private int timeRemaining;
    private boolean isStudyTime = true;
    private int sessionCount = 0;

    public Main() {
        setTitle("Pomodoro Timer");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color backgroundColor1 = new Color(32, 32, 32);
        Color backgroundColor2 = new Color(18, 18, 18);
        Color cardColor = new Color(48, 48, 48, 200);
        Color textColor = new Color(255, 255, 255);
        Color buttonColor = new Color(0, 120, 212);
        Color buttonHoverColor = new Color(0, 90, 180);

        setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, backgroundColor1, getWidth(), getHeight(), backgroundColor2);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        });

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        infoLabel = new JLabel("<html>Study for 25 minutes, then take a 5-minute break.<br>After 4 sessions, take a 15-minute break.</html>", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setForeground(textColor);
        infoLabel.setBackground(new Color(0, 0, 0, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0.2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(infoLabel, gbc);

        timerLabel = new JLabel("25:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        timerLabel.setForeground(textColor);
        timerLabel.setBackground(new Color(0, 0, 0, 0));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.6;
        add(timerLabel, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        startButton = new RoundedButton("Start Study", buttonColor, buttonHoverColor, textColor);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startStudy();
            }
        });

        breakButton = new RoundedButton("Start Break", buttonColor, buttonHoverColor, textColor);
        breakButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startBreak();
            }
        });

        buttonPanel.add(startButton);
        buttonPanel.add(breakButton);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.2;
        add(buttonPanel, gbc);

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                if (timeRemaining >= 0) {
                    updateTimerLabel();
                } else {
                    timer.stop();
                    playSound("alarm.wav");
                    if (isStudyTime) {
                        sessionCount++;
                        if (sessionCount % 4 == 0) {
                            showFluentMessage("Time for a 15-minute break!", "Break Time");
                        } else {
                            showFluentMessage("Time for a 5-minute break!", "Break Time");
                        }
                    } else {
                        showFluentMessage("Break's over! Time to study.", "Study Time");
                    }
                }
            }
        });
    }

    private void playSound(String soundFile) {
        try {
            File file = new File(soundFile);
            if (file.exists()) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            } else {
                System.err.println("Sound file not found: " + soundFile);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void showFluentMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE, UIManager.getIcon("OptionPane.informationIcon"));
    }

    private void startStudy() {
        isStudyTime = true;
        timeRemaining = 25 * 60;
        updateTimerLabel();
        timer.start();
    }

    private void startBreak() {
        isStudyTime = false;
        if (sessionCount % 4 == 0) {
            timeRemaining = 15 * 60;
        } else {
            timeRemaining = 5 * 60;
        }
        updateTimerLabel();
        timer.start();
    }

    private void updateTimerLabel() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main frame = new Main();
                frame.setVisible(true);
            }
        });
    }

    static class RoundedButton extends JButton {
        private Color backgroundColor;
        private Color hoverColor;
        private Color textColor;
        private int cornerRadius = 20;

        public RoundedButton(String text, Color backgroundColor, Color hoverColor, Color textColor) {
            super(text);
            this.backgroundColor = backgroundColor;
            this.hoverColor = hoverColor;
            this.textColor = textColor;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(textColor);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(hoverColor);
                    repaint();
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(backgroundColor);
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isArmed()) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(backgroundColor);
            }
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

            g2.setColor(getForeground());
            FontMetrics metrics = g2.getFontMetrics(getFont());
            int x = (getWidth() - metrics.stringWidth(getText())) / 2;
            int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
            g2.drawString(getText(), x, y);

            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
        }
    }
}