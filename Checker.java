/* this program works best for fixed output problems */
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;

import java.util.Timer;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.TimerTask;

import java.time.Instant;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*************************************************************************************************************************************************************************/
/*************************************************************************************************************************************************************************/
/*************************************************************************************************************************************************************************/

class TestCase implements Serializable 
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

/*************************************************************************************************************************************************************************/
/*************************************************************************************************************************************************************************/
/*************************************************************************************************************************************************************************/

public class Checker
{
	private static int verdict;
	private static String log;
	private static Process process;
	private static ArrayList<String> inp;
	private static ArrayList<String> out;
	private static ArrayList<String> ans;
	private static final String pass = "jarvis";
	private static HashMap<Integer, String> verdictMap;
	private static HashMap<String, String> compileCmds;


	private static String getLog()
	{
		String tempLog = "OK";
		int os = Checker.out.size();
		int as = Checker.ans.size();
		if(os == 0)
			tempLog = "<no output>";
		else if(os < as)
			tempLog = "Unexpected EOF. Expected " + as + " lines, found " + os + " lines";
		else if(os > as)
			tempLog = "Extraneous output printed";
		else
			for(int i = 0; i < os; i++)
				if(!ans.get(i).equals(out.get(i)))
				{
					tempLog = ("Wrong answer on line number " + (i+1));
					break;
				}

		return tempLog;
	}


	private static void exitWithMessage(String s)
	{
		System.out.println(s);
		System.exit(0);
	}


	private static ArrayList read(String arg, int idx) throws IOException
	{
		String st, fname = ("test_files/t" + idx);
		ArrayList<String> data = new ArrayList<String>();
        fname += ((arg.equals("input") ? "" : "_o") + ".txt");
		BufferedReader reader = new BufferedReader(new FileReader(fname));

		while ((st = reader.readLine()) != null)
		{
			st = st.trim();
			if (st.length() > 0)
				data.add(st);
		}
		reader.close();
		return data;
	}


	private static ArrayList read(InputStream is) throws IOException
	{
		String st;
		ArrayList<String> data = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		while ((st = reader.readLine()) != null)
		{
			st = st.trim();
			if (st.length() > 0)
				data.add(st);
		}
		reader.close();
		return data;
	}


	private static void write(OutputStream os, ArrayList<String> arrlist)
	{
		PrintWriter pw = new PrintWriter(os);
		for(String st : arrlist)
		{
			pw.println(st);
			pw.flush();
		}
		return;
	}


	public static void main (String[] args) throws IOException
	{
		int score = 0, no_of_testcases = 3, tries = 3;
		if(args.length < 1 || args.length > 2)
			Checker.exitWithMessage("Usage: java -jar RCS.jar [judge xxx | reveal X | clean]");

		switch (args[0])
		{
			case "judge":
				String inp_pass;
				if(args.length < 2)
					Checker.exitWithMessage("Missing source code filename. Format: judge xxx");
				while(tries > 0)
				{   
					System.out.print("\nEnter Password (" + tries + ((tries == 1) ? " try " : " tries ") + "left): ");
					inp_pass = new String(System.console().readPassword());
					if(inp_pass.equals(Checker.pass))
						break;
					else
					{
						tries--;
						System.out.println("Incorrect Password, try again. ");
						if(tries == 0)
							Checker.exitWithMessage("");
					}
				}

				ArrayList<TestCase> pack = new ArrayList<TestCase>();
				Checker.verdictMap = new HashMap<>();
				Checker.verdictMap.put(0, ("\033[1;" + 31 + "m" + "Wrong Answer       " + "\033[0m"));
				Checker.verdictMap.put(3, ("\033[1;" + 31 + "m" + "Runtime Error      " + "\033[0m"));
				Checker.verdictMap.put(1, ("\033[1;" + 32 + "m" + "Accepted           " + "\033[0m"));
				Checker.verdictMap.put(2, ("\033[1;" + 33 + "m" + "Time Limit Exceeded" + "\033[0m"));

				Checker.compileCmds = new HashMap<>();
				Checker.compileCmds.put(".c", "gcc --std=c99 -lm ");
				Checker.compileCmds.put(".cpp", "g++ --std=c++17 ");

				String ext = args[1].substring(args[1].lastIndexOf("."), args[1].length());
				String ccmd = Checker.compileCmds.get(ext) + args[1];
				String params[] = {"bash", "-c", ccmd};
				Process compiler = Runtime.getRuntime().exec(params);
				try
				{
					compiler.waitFor();
				}
				catch(InterruptedException ie)
				{
					/*don't print anything*/
				}

				if(compiler.exitValue() != 0)
					Checker.exitWithMessage("\033[1;" + 31 + "m" + "Compilation Error!\n" + "\033[0m");

				for (int testcaseno = 1; testcaseno <= no_of_testcases; testcaseno++)
				{
					Checker.verdict = -1;
					Checker.process = null;
					try
					{
						Checker.inp = Checker.read("input", testcaseno);
						Checker.ans = Checker.read("answer", testcaseno);
					}
					catch(IOException ie)
					{
						Checker.exitWithMessage("Unable to read required files.");
					}

					Instant start = null, end = null;
					try
					{
						Checker.process = Runtime.getRuntime().exec("./a.out");
					}
					catch (IOException ie)
					{
						Checker.exitWithMessage("No executable found. Compile the source code first.");
					}

					PrintWriter pw = new PrintWriter(Checker.process.getOutputStream());
					Timer timer = new Timer("terminater");
					timer.schedule(new TimerTask(){
						@Override
						public void run(){
							if(Checker.process.isAlive())
							{
								Checker.process.destroyForcibly();
								Checker.verdict = 2;
							}
						}
					}, 2050);

					try
					{
						start = Instant.now();
						Checker.write(Checker.process.getOutputStream(), Checker.inp);
						Checker.out = Checker.read(Checker.process.getInputStream());
						Checker.process.waitFor();
					}
					catch (IOException ie)
					{
						Checker.verdict = 2;
					}
					catch (InterruptedException inte)
					{
						/*don't do anything;*/
					}
					finally
					{
						end = Instant.now();
						timer.cancel();
						timer.purge();
					}

					if(Checker.verdict == 2)
						Checker.log = "TLE";
					else
					{
						if(Checker.process.exitValue() != 0)
						{
							Checker.verdict = 3;
							Checker.log = "Runtime error";
						}
						else
						{
							Checker.log = Checker.getLog();
							Checker.verdict = (Checker.log.equals("OK") ? 1 : 0);
						}
					}

					if (Checker.verdict == 1)
						score++;
					long timeElapsed = Duration.between(start, end).toNanos();
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
					String currTime = dtf.format(LocalDateTime.now());
					TestCase test = new TestCase(testcaseno, timeElapsed, Checker.verdictMap.get(Checker.verdict), Checker.log, Checker.inp, Checker.out, Checker.ans, currTime);

					test.showResult();
					pack.add(test);
					System.gc();
				}

				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Results.dat"));
				oos.writeObject(pack);
				oos.close();

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
					Checker.exitWithMessage("Judge atleast once before revealing");
				}
				catch (IOException | ClassNotFoundException | ClassCastException ge)
				{
					Checker.exitWithMessage("Unknown Exception");
				}
				break;

			case "clean":
				String[] cmd = {"rm Results.dat", "rm -rf test_files", "rm RCS.jar", "rm a.out"};
				for (String s : cmd)
					Runtime.getRuntime().exec(s);
				System.out.println("Clean successful...");
				break;

			default:
				System.out.println("Usage: java -jar RCS.jar [judge xxx | reveal X | clean]");
				break;
		}
	}
}

/*************************************************************************************************************************************************************************/
/*************************************************************************************************************************************************************************/
/*************************************************************************************************************************************************************************/