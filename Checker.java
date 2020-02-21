import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.Instant;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Checker
{
    //this program works best for fixed output programs;
    private static String log = null;
    protected static int verdict = -1;
    private static final String pass = "jarvis";
    private static ArrayList<String> inp = null;
    private static ArrayList<String> out = null;
    private static ArrayList<String> ans = null;
    private static HashMap<Integer, String> verdictMap = null;

    //new method to comapre if expected output is equal to received output;
    //in this method, appropriate log is set for the testcase;
    private static void compareIfEqual()
    {
        int os = Checker.out.size();
        int as = Checker.ans.size();
        //full answer is not found;
        //else a full answer is found, but there is a mismatch;
        //report first mismatch instance;
        if(os == 0)
            Checker.log = "<no output>";
        else if(os < as)
            Checker.log = "Unexpected EOF. Expected " + as + " lines, found " + os + " lines";
        else if(os > as)
            Checker.log = "Extraneous output printed";
        else
            for(int i = 0; i < os; i++)
                if(!ans.get(i).equals(out.get(i)))
                {
                    Checker.log = ("Wrong answer on line number " + (i+1));
                    break;
                }
    }

    private static void exitWithMessage(String s)
    {
        System.out.println(s);
        System.exit(0);
    }

    //main function:
    public static void main (String[] args) throws IOException
    {
        int score = 0, no_of_testcases = 3;
        System.out.print("\nEnter Password: ");
        //invoke a console object's readPassword method to read the password for the judge;
        //the password in this case will not be visible;
        String inp_pass = new String(System.console().readPassword());

        //display error message if the number of arguments are not adequate;
        if(args.length < 1 || args.length > 2)
            Checker.exitWithMessage("Usage: java -jar RCS.jar [judge xxx | reveal X | clean]");

        //verify the password;
        if(inp_pass.equals(Checker.pass))
            switch (args[0])
            {
                case "judge":
                    if(args.length < 2)
                        Checker.exitWithMessage("Missing source code filename. Format: judge xxx");

                    ArrayList<TestCase> pack = new ArrayList<TestCase>();
                    //keep a verdict map to have a track of the color-coded output along with a verdict digit [0-2] for simplicity;
                    Checker.verdictMap = new HashMap<>();
                    Checker.verdictMap.put(0, ("\033[1;" + 31 + "m" + "Wrong Answer       " + "\033[0m"));
                    Checker.verdictMap.put(3, ("\033[1;" + 31 + "m" + "Runtime Error      " + "\033[0m"));
                    Checker.verdictMap.put(1, ("\033[1;" + 32 + "m" + "Accepted           " + "\033[0m"));
                    Checker.verdictMap.put(2, ("\033[1;" + 33 + "m" + "Time Limit Exceeded" + "\033[0m"));

                    for (int i = 1; i <= no_of_testcases; i++)
                    {
                        String st;
                        //initialize variables for TestCase class;
                        //verdict -1 indicates that current testcase is still under judgement;
                        Checker.log = "Ok";
                        Checker.verdict = -1;
                        Checker.inp = new ArrayList<String>();
                        Checker.out = new ArrayList<String>();
                        Checker.ans = new ArrayList<String>();

                        BufferedReader bfri = null;
                        BufferedReader bfra = null;
                        try
                        {
                        		bfri = new BufferedReader(new FileReader("test_files/t" + i + ".txt"));
                        		bfra = new BufferedReader(new FileReader("test_files/t" + i + "_o.txt"));
                        }
                        catch(IOException ie)
                        {
                        		Checker.exitWithMessage("Unable to read necessary files");
                        }
                        //fetch input for the given problem;
                        //don't include any stray newlines or spaces;
                        while ((st = bfri.readLine()) != null)
                            if (st.trim().length() > 0)
                                Checker.inp.add(st.trim());
                        //fetch required answer for the problem;
                        //don't include any stray newlines or spaces;
                        while ((st = bfra.readLine()) != null)
                            if (st.trim().length() > 0)
                                Checker.ans.add(st.trim());

                        //begin the sub-process;
                        //first compile it;
                        String compileCmd = (((args[1].charAt(args[1].length() - 1) == 'c') ? "gcc -lm " : "g++ -std=c++17 ") + args[1]);
                        String params[] = {"bash", "-c", compileCmd};
                        Process compiler = Runtime.getRuntime().exec(params);
                        try
                        {
                        		compiler.waitFor();
                        }
                        catch(InterruptedException ie)
                        {
                    		    //don't print anything;
                        }

                        if(compiler.exitValue() != 0)
                            Checker.exitWithMessage("\033[1;" + 31 + "m" + "Compilation Error!\n" + "\033[0m");

                        //if compilation is successful, run it;
                        Instant start = null, end = null;
                        Process p = null;
                        try
                        {
                            p = new ProcessBuilder().command("./a.out").start();
                            start = Instant.now();
                        }
                        catch (IOException ie)
                        {
                            Checker.exitWithMessage("No executable found. Compile the source code first.");
                        }
                        //get the I/O streams of the subprocess;
                        //and construct wrapper classes around them of ease I/O;
                        PrintWriter pw = new PrintWriter(p.getOutputStream());
                        BufferedReader bfro = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        //begin killer thread to check for timelimit;
                        //this constructor internally calls start;
                        TimerThread timer = new TimerThread(p, 2500);

                        try
                        {
                            //provide input to the subprocess;
                            for (String s : Checker.inp)
                            {
                                pw.println(s);
                                pw.flush();
                            }
                            //read the output from the subprocess;
                            //don't include stray spaces or newlines;
                            while ((st = bfro.readLine()) != null)
                                if (st.trim().length() > 0)
                                    Checker.out.add(st.trim());

                            //if process has already ended but killer is still running;
                            //interrupt the killer thread;
                            //but first allow the process to wrap up;
                            if(p.isAlive())
                            {
                                timer.interrupt();
                                p.waitFor();
                            }
                            end = Instant.now();
                        }
                        catch (IOException ie)
                        {
                            //if the process has been killer externally meanwhile, an exception is generated;
                            //generated exception indicates an TLE;
                            Checker.verdict = 2;
                        }
                        catch (InterruptedException inte)
                        {
                            //don't do anything;
                        }
                        finally
                        {
                            //join running threads;
                            try
                            {
                                if(!timer.isInterrupted())
                                {
                                    timer.join();
                                    end = Instant.now();
                                }
                            }
                            catch(InterruptedException ie)
                            {
                                //don't report anything;
                            }

                            //close all streams;
                            bfri.close();
                            bfra.close();
                            bfro.close();
                        }

                        //verify output;
                        //if already TLE has occured, set log as "TLE";
                        //else check the output;
                        if(Checker.verdict == 2)
                            Checker.log = "TLE";
                        else
                        {
                            if(p.exitValue() != 0)
                            {
                                Checker.verdict = 3;
                                Checker.log = "Runtime error";
                            }
                            else
                            {
                                Checker.compareIfEqual();
                                Checker.verdict = (Checker.log.equals("Ok") ? 1 : 0);
                            }
                        }

                        if (Checker.verdict == 1)
                            score++;
                        //save the testcase;
                        long timeElapsed = Duration.between(start, end).toNanos();
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        String currTime = dtf.format(LocalDateTime.now());
                        TestCase test = new TestCase(i, timeElapsed,
                        							 Checker.verdictMap.get(Checker.verdict),
                                                     Checker.log,
                                                     Checker.inp,
                                                     Checker.out,
                                                     Checker.ans,
                                                     currTime);
                        test.showResult();
                        pack.add(test);
                        if(compiler.isAlive())
                        	compiler.destroyForcibly();
                        System.gc();
                    }
                    //dump all testcases into a file for future reference;
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Results.dat"));
                    oos.writeObject(pack);
                    oos.close();
                    //output final score along with WELL DONE message (optional);
                    System.out.println("\n\t\t        +----------------+");
                    System.out.print("\t\t\t| Final Score: " + score + " |");
                    System.out.println("\n\t\t        +----------------+");
                    if (score == no_of_testcases)
                    {
                        System.out.print("\t\t\t|\033[1;" + 32 + "m" + "   WELL DONE!" + "\033[0m   |");
                        System.out.println("\n\t\t        +----------------+\n");
                    }
                    else
                        System.out.print("\n");
                    break;

                case "reveal":
                    //to reveal a particular testcase;
                    if(args.length < 2)
                        Checker.exitWithMessage("Missing testcase no. Format: reveal X");
                    int idx = Integer.parseInt(args[1]);
                    if(idx > no_of_testcases)
                    	Checker.exitWithMessage("Testcase not found.");
                    try
                    {
                        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Results.dat"));
                        pack = (ArrayList<TestCase>) ois.readObject();
                        System.out.println(pack.get(idx - 1));
                        ois.close();
                    }
                    catch (FileNotFoundException fnfe)
                    {
                        System.out.println("Judge atleast once before revealing");
                    }
                    catch (IOException | ClassNotFoundException | ClassCastException ge)
                    {
                        ge.printStackTrace();
                    }
                    break;

                case "clean":
                    //clean all files related to judge and the coder's executable file;
                    String[] cmd = {"rm Results.dat", "rm -rf test_files", "rm RCS.jar", "rm a.out"};
                    for (String s : cmd)
                        Runtime.getRuntime().exec(s);
                    System.out.println("Clean successful...");
                    break;

                default:
                    //if random cmd argument is found;
                    System.out.println("Usage: java -jar RCS.jar [judge xxx | reveal X | clean]");
                    break;
            }
        else
            System.out.println("Incorrect Password, try again!");
    }
}
