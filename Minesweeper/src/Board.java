import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Aceasta clasa este pentru structura mapei jocului Minesweeper
 * Incepem cu partea de initializare a variabilelor
 */

public class Board extends JPanel {

    private static int NUMBER_TRIES = 0;

    private final int NUM_IMAGES = 13;
    private final int CELL_SIZE = 40;

    private final int IMAGE_WIDTH = 40;
    private final int IMAGE_HIGHT = 40;

    /**
     * Imaginile folosite in acest proiect sunt numerotate de la 0 la 12
     * Un patrat poate avea maxim 8 bombe in vecinatatea lui astfel numerele folosite sunt de la 1 la 8 si notate cu
     * acelasi numar pe care il reprezinta
     * Patratica libera este notata cu 0
     * Bomba este notata cu 9
     * Un patrat acoperit este notat cu 10
     * Un steag este notat cu 11
     * Un steag pus gresit este notat cu 12
     */

    private final int COVER_FOR_CELL = 10;
    private final int MARK_FOR_CELL = 11;
    private final int EMPTY_CELL = 0;
    private final int MINE_CELL = 9;
    private final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    private final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;

    private final int DRAW_MINE = 9;
    private final int DRAW_COVER = 10;
    private final int DRAW_MARK = 11;
    private final int DRAW_WRONG_MARK = 12;

    /**
     * Numarul de bombe, numarul de linii si de coloane si marimea mapei
     */

    private final int N_MINES = 40;
    private final int N_ROWS = 16;
    private final int N_COLS = 16;

    private final int BOARD_WIDTH = N_COLS * CELL_SIZE + 1;
    private final int BOARD_HEIGHT = N_ROWS * CELL_SIZE + 1;

    private int[] field;
    private boolean inGame;
    private int minesLeft;
    private Image[] img;

    private int allCells;
    private final JLabel status;

    public CountTime countTime;

    public Board(JLabel status,CountTime countTime) {

        this.status = status;
        initBoard();
        this.countTime = countTime;

    }

    /**
     * Aceasta metoda initializeaza vectorul cu pozele pentru fiecare element in parte al jocului ex: bomba, steag, 1, 2 etc.
     * De asemenea avem metoda MinesAdapter care extinde MouseAdapter si sesizeaza miscarile mouse-ului
     */
    private void initBoard() {

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        img = new Image[NUM_IMAGES];

        for (int i = 0; i < NUM_IMAGES; i++) {
            var path = "src/resources/" + i + ".jpg";
            img[i] = (new ImageIcon(path)).getImage().getScaledInstance(IMAGE_WIDTH, IMAGE_HIGHT,   Image.SCALE_REPLICATE );
        }

        addMouseListener(new MinesAdapter());
        newGame();
    }


    /**
     *Prima data fiecare patratica va fii acoperita/initializata
     *Jocul afiseaza in timp real cate mine mai sunt de gasit
     *Alege random o pozitie, verifica daca nu a mai fost folosita, si o seteaza ca mina
     *Dupa ce a setat o mina se duce pe fiecare vecin al minei si incrementeaza numarul daca nu este bomba
     */
    private void newGame() {

        int cell;

        var random = new Random();
        inGame = true;
        minesLeft = N_MINES;

        allCells = N_ROWS * N_COLS;
        field = new int[allCells];

        for (int i = 0; i < allCells; i++) {

            field[i] = COVER_FOR_CELL;
        }

        status.setText("Number of flags left: " + Integer.toString(minesLeft));  //arata cate mine mai sunt de gasit

        int i = 0;

        while (i < N_MINES) { //seteaza minele random pe mapa

            int position = (int) (allCells * random.nextDouble());

            if ((position < allCells)
                    && (field[position] != COVERED_MINE_CELL)) {

                int current_col = position % N_COLS;
                field[position] = COVERED_MINE_CELL;
                i++;

                if (current_col > 0) {
                    cell = position - 1 - N_COLS;
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position - 1;
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }

                    cell = position + N_COLS - 1;
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                }

                cell = position - N_COLS;
                if (cell >= 0) {
                    if (field[cell] != COVERED_MINE_CELL) {
                        field[cell] += 1;
                    }
                }

                cell = position + N_COLS;
                if (cell < allCells) {
                    if (field[cell] != COVERED_MINE_CELL) {
                        field[cell] += 1;
                    }
                }

                if (current_col < (N_COLS - 1)) {
                    cell = position - N_COLS + 1;
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position + N_COLS + 1;
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position + 1;
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                }
            }
        }
    }

    /**
     * Aceasta metoda este una recursiva si are rolul ca in momentul gasirii unei casute libere sa caute si sa descopere
     * toate celalalte casute libere din apropiere si de asemenea si primele numere de langa casutele libere
     * Aceasta metoda merge din vecin in vecin ca si metoda anterioara
     * @param j este prima casuta libera gasita in timpul jocului
     */

    private void find_empty_cells(int j) {

        int current_col = j % N_COLS;
        int cell;

        if (current_col > 0) {
            cell = j - N_COLS - 1;
            if (cell >= 0) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j - 1;
            if (cell >= 0) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + N_COLS - 1;
            if (cell < allCells) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }
        }

        cell = j - N_COLS;
        if (cell >= 0) {
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL) {
                    find_empty_cells(cell);
                }
            }
        }

        cell = j + N_COLS;
        if (cell < allCells) {
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL) {
                    find_empty_cells(cell);
                }
            }
        }

        if (current_col < (N_COLS - 1)) {
            cell = j - N_COLS + 1;
            if (cell >= 0) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + N_COLS + 1;
            if (cell < allCells) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + 1;
            if (cell < allCells) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }
        }

    }

    /**
     * Aceasta metoda deseneaza fiecare patratica in parte in functie de valorile pe care le-am stocat in vector asa cum
     * le-am mentionat si mai sus
     * De asemenea metoda verifica daca jocul a fost castigat sau pierdur si incrementeaza numarul de incercari/jocuri jucate
     * @param g Metoda foloseste obiecte de tip Graphics pentru a lucra cu imaginile pe care dorim sa le folosim
     */
    @Override
    public void paintComponent(Graphics g) {

        int uncover = 0;
        for (int i = 0; i < N_ROWS; i++) {
            for (int j = 0; j < N_COLS; j++) {
                int cell = field[(i * N_COLS) + j];
                if (inGame && cell == MINE_CELL) {
                    inGame = false;
                }

                if (!inGame) {
                    if (cell == COVERED_MINE_CELL) {
                        cell = DRAW_MINE;
                    } else if (cell == MARKED_MINE_CELL) {
                        cell = DRAW_MARK;
                    } else if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_WRONG_MARK;
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                    }

                } else {
                    if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_MARK;
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                        uncover++;
                    }
                }

                g.drawImage(img[cell], (j * CELL_SIZE),
                        (i * CELL_SIZE), this);
            }
        }

        if (uncover == 0 && inGame) {

            inGame = false;
            status.setText("You won after " + NUMBER_TRIES + " tries !");
            NUMBER_TRIES = 0;
            Timer.resetTime();
            countTime.stop();

        } else if (!inGame) {
            NUMBER_TRIES++;
            Minesweeper.tries.setText("Number of tries: " + NUMBER_TRIES);
            status.setText("Game Over!");
            Timer.resetTime();
            countTime.stop();
        }
    }

    /**
     * Metoda MinesAdapter extinde MouseAdapter si sesizeaza miscarile mouse-ului
     * In functie de ce apasam si cum apasam jocul va sesiza ce vrem sa facem
     * Metoda reseteazza jocul
     * Aceasta sesizeaza daca vrem sa punem steag pe un atrat acoperit
     * Daca apasam o mina vom pierde jocul
     * Daca apasam pe un patrat liber se va apela metoda find_empty_cells
     */

    private class MinesAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {

            int x = e.getX();
            int y = e.getY();

            int cCol = x / CELL_SIZE;
            int cRow = y / CELL_SIZE;

            boolean doRepaint = false;

            if (!inGame) {
                newGame();
                Thread thread = new Thread(countTime);
                thread.start();
                repaint();
            }

            if ((x < N_COLS * CELL_SIZE) && (y < N_ROWS * CELL_SIZE)) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (field[(cRow * N_COLS) + cCol] > MINE_CELL) {
                        doRepaint = true;
                        if (field[(cRow * N_COLS) + cCol] <= COVERED_MINE_CELL) {
                            if (minesLeft > 0) {
                                field[(cRow * N_COLS) + cCol] += MARK_FOR_CELL;
                                minesLeft--;
                                String msg = Integer.toString(minesLeft);
                                status.setText("Number of flags left: " + msg);
                            } else {
                                status.setText("No flags left");
                            }
                        } else {
                            field[(cRow * N_COLS) + cCol] -= MARK_FOR_CELL;
                            minesLeft++;
                            String msg = Integer.toString(minesLeft);
                            status.setText("Number of flags left: " + msg);
                        }
                    }

                } else {
                    if (field[(cRow * N_COLS) + cCol] > COVERED_MINE_CELL) {
                        return;
                    }
                    if ((field[(cRow * N_COLS) + cCol] > MINE_CELL)
                            && (field[(cRow * N_COLS) + cCol] < MARKED_MINE_CELL)) {
                        field[(cRow * N_COLS) + cCol] -= COVER_FOR_CELL;
                        doRepaint = true;
                        if (field[(cRow * N_COLS) + cCol] == MINE_CELL) {
                            inGame = false;
                        }

                        if (field[(cRow * N_COLS) + cCol] == EMPTY_CELL) {
                            find_empty_cells((cRow * N_COLS) + cCol);
                        }
                    }
                }

                if (doRepaint) {
                    repaint();
                }
            }
        }
    }
}