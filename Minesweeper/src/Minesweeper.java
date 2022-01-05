import java.awt.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;

/**
 * Aceasta clasa este clasa principala unde se contruieste interfata grafica si se apeleaza toate functiile necesare
 * Seteaza culorile grid-urilor
 * Afiseaza si centreaza timer-ul, numarul bombelor ramase si numarul de incercari
 */

public class Minesweeper extends JFrame {
    private JLabel status;
    public Board gameBoard;
    public static Timer timer;
    public CountTime countTime;
    public static JLabel tries;

    public static final Color CYAN_COLOR = new Color(31, 225, 246);

    public Minesweeper() {
        Float ration = 1.0f;
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setOpaque(false);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        timer = new Timer();
        JPanel panel2 = new JPanel();
        status = new JLabel("");
        left.add(status);
        tries = new JLabel("Number of tries: 1");
        tries.setFont(new Font(Font.DIALOG,  Font.BOLD, 25));
        right.add(tries);
        panel2.add(left,ration);
        panel2.add(new JLabel(""));
        panel2.add(right,ration);
        status.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
        status.setFont(new Font(Font.DIALOG,  Font.BOLD, 25));
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        add(panel2, BorderLayout.NORTH);
        countTime = new CountTime(timer);
        gameBoard = new Board(status,countTime);
        panel.add(gameBoard);
        gameBoard.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
        panel.setBorder(new CompoundBorder(
                        BorderFactory.createLineBorder(Color.BLACK, 4),
                        BorderFactory.createLineBorder(CYAN_COLOR, 25)
                )
        );
        add(panel);
        setResizable(false);
        timer.setFont(new Font(Font.DIALOG,  Font.BOLD, 25));
        add(timer,BorderLayout.SOUTH);

        /**
         * centrare pe ecran
         * inchide programul cand inchizi jocul
         */
        
        pack();
        setTitle("Minesweeper");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Incepe un joc nou
     */

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                Minesweeper ms = new Minesweeper();
                ms.setVisible(true);
                ms.repaint();
                Thread thread = new Thread(ms.countTime);
                thread.start();
            }
        });
    }
}