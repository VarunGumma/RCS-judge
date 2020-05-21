import java.io.Serializable;
import java.util.ArrayList;

public class TestCase implements Serializable 
{
    private final String log;
    private final int testNo;
    private final String time;
    private final long execTime;
    public final String verdict;
    private final ArrayList<String> inp;
    private final ArrayList<String> out;
    private final ArrayList<String> ans;
    private static final long serialVersionUID = 1L;

    TestCase(int testNo, long execTime, String verdict, String log, ArrayList<String> inp, ArrayList<String> out, ArrayList<String> ans, String time)
    {
        this.inp = inp;
        this.out = out;
        this.ans = ans;
        this.log = log;
        this.time = time;
        this.testNo = testNo;
        this.verdict = verdict;
        this.execTime = execTime;
    }

    void showResult()
    {
        if(this.testNo == 1)
            System.out.println("\n\t+--------------------------------------------+");
        System.out.println("\t| testcase #" + this.testNo + " | verdict: " + this.verdict + " |");
        System.out.println("\t+--------------------------------------------+");
    }

    @Override
    public String toString()
    {
        double etime = ((double)execTime)/1000000;
        StringBuilder finalAns = new StringBuilder("\n");
        finalAns.append("---------------------------------------------------------------\n");
        finalAns.append("TESTCASE: ").append(this.testNo).append("\n");
        finalAns.append("JUDGED AT: ").append(this.time).append("\n");
        finalAns.append("RUNTIME: ").append(etime).append(" ms\n");
        finalAns.append("VERDICT: ").append(this.verdict).append("\n");

        finalAns.append("\nINPUT:\n");
        for(int i = 0; i < Math.min(31, this.inp.size()); i++)
            finalAns.append(this.inp.get(i), 0, Math.min(255, this.inp.get(i).length())).append(" ").append((this.inp.get(i).length() > 75) ? "...\n" : "\n");
        finalAns.append((this.inp.size() < 31) ? "" : "...\n");

        finalAns.append("\nYOUR ANSWER:\n");
        for(int i = 0; i < Math.min(31, this.out.size()); i++)
            finalAns.append(this.out.get(i), 0, Math.min(255, this.out.get(i).length())).append(" ").append((this.out.get(i).length() > 75) ? "...\n" : "\n");
        finalAns.append((this.out.size() < 31) ? "" : "...\n");

        finalAns.append("\nJURY'S ANSWER:\n");
        for(int i = 0; i < Math.min(31, this.ans.size()); i++)
            finalAns.append(this.ans.get(i), 0, Math.min(255, this.ans.get(i).length())).append(" ").append((this.ans.get(i).length() > 75) ? "...\n" : "\n");
        finalAns.append((this.ans.size() < 31) ? "" : "...\n");

        finalAns.append("\nLOG:\n");
        finalAns.append(this.log);
        finalAns.append("\n---------------------------------------------------------------\n");
        return finalAns.toString();
    }
}
