public class CountTime implements Runnable{

    private Timer timer;
    private static boolean ok = true;

    /**
     * Aceasta clasa este folosita pentru calcularea efectiva a timpului
     * @param timer se folosesc obiecte de tip timer
     */

    public CountTime(Timer timer) {
        this.timer = timer;
    }

    @Override
    public void run() {
        while(ok)
        {
            Timer.SECONDS++;
            Timer.MINUTES += Timer.SECONDS/60;
            Timer.SECONDS %= 60;
            timer.updateTime();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ok = true;
    }

    public void stop()
    {
        ok = false;
    }

}
