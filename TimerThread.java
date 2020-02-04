public class TimerThread extends Thread 
{
    private final Process process;
    private final int time_limit;

    TimerThread(Process p, int time_limit)
    {
        this.process = p;
        this.time_limit = time_limit;
        this.start();
    }

    public void run()
    {
        try
        {
            Thread.sleep(time_limit);
            if(process.isAlive() && !Thread.currentThread().isInterrupted())
            {
                process.destroyForcibly();
                Checker.verdict = 2;
            }
        }
        catch(InterruptedException inte)
        {
            //don't do anything;
            return;
        }
    }
}
