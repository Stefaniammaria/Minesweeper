import javax.swing.*;

public class Timer extends JLabel {

    /**
     * Aceasta clasa este folosita pentru a cronometra si afisa timpul unui joc
     */

    public static int SECONDS = 0;
    public static int MINUTES = 0;

    public Timer() {

        updateTime();

    }

    public static void resetTime()
    {
        SECONDS = 0;
        MINUTES = 0;
    }

    public void updateTime()
    {
        setText(((MINUTES < 10) ?  "0" : "") + MINUTES + ":" +  ((SECONDS < 10) ?  "0" : "" )+ SECONDS);
    }

}
