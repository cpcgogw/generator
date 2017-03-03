import java.lang.reflect.*;
import java.util.*;
import java.nio.file.*;
import java.nio.*;
import java.io.*;

public class RecipeParserTest {
	public static RecipeParser rp;
	public static List<Command> commands;

	public static void setup() throws IOException {
		rp = new RecipeParser();
	}

	public static void main(String[] args) throws IOException {
		if (runTests()) {
			System.out.println("All tests passed.");
		} else {
			System.out.println("ERROR: Not all tests passed.");
		}
	}

	public static boolean runTests() {
		return parseParameterTest() 
			&& parseParametersTest() 
			&& parseCommand() 
			&& parseStatement() 
			&& parseRecipe();
	}

	public static boolean parseStatement() {
		String s1 = "execute(first,second,third)";
		String s2 = "execute(first,second,)";
		String s3 = "(first,second)";
		String s4 = "asdfasdfa";

		try {
			setup();
			Command c;
			boolean pass = true;
			List<String> list = new ArrayList<String>();

			Method method = rp.getClass().getDeclaredMethod("parseStatement", String.class);
			method.setAccessible(true);

			list.add("first");
			list.add("second");
			list.add("third");

			pass = pass && ((Command) method.invoke(rp, s1)).equals(new Command("execute", list));
			list.remove("third");
			pass = pass && ((Command) method.invoke(rp, s2)).equals(new Command("execute", list));
			pass = pass && ((Command) method.invoke(rp, s3)) == null;
			pass = pass && ((Command) method.invoke(rp, s4)) == null;

			return pass;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean parseCommand() {
		String s1 = "execute(elasda2)";
		String s2 = "grow(elasda2,1231j,12391,110101bbb)";
		String s3 = "(elasda2)";
		String s4 = "";

		try {
			boolean pass = true;
			setup();
			Method method = rp.getClass().getDeclaredMethod("parseCommand", String.class);

			method.setAccessible(true);
			pass = pass && ((String) method.invoke(rp, s1)).equals("execute");
			pass = pass && ((String) method.invoke(rp, s2)).equals("grow");
			pass = pass && method.invoke(rp, s3) == null;
			pass = pass && method.invoke(rp, s4) == null;

			return pass;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean parseRecipe() {
		try {
			setup();
			boolean pass = true;
			Command c;
			List<String> list = new ArrayList<String>();

			Method method = rp.getClass().getDeclaredMethod("parseRecipe", FileReader.class);
			method.setAccessible(true);
			commands = (List<Command>) method.invoke(rp, new FileReader("test.r"));

			list.add("start");
			pass = pass && (commands.get(0)).equals(new Command("SetAxiom", list));
			list.clear();
			list.add("startRule");
			pass = pass && (commands.get(1)).equals(new Command("DoOnce", list));
			list.clear();
			list.add("growRule");
			list.add("3");
			pass = pass && (commands.get(2)).equals(new Command("DoNTimes", list));
			list.clear();
			list.add("exitRule");
			pass = pass && (commands.get(3)).equals(new Command("DoOnce", list));

			return pass;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean parseParametersTest() {
		String s1 = "grow(123,param1,param2,elos)";
		String s2 = "(123,param1,param2,elos)";
		String s3 = "()";

		try {
			setup();
			boolean pass = true;
			List<String> params = new ArrayList<String>();

			Method method = rp.getClass().getDeclaredMethod("parseParameters", String.class);
			method.setAccessible(true);

			params.add("123");
			params.add("param1");
			params.add("param2");
			params.add("elos");
			pass = pass && ((List<String>) method.invoke(rp, s1)).equals(params);
			pass = pass && ((List<String>) method.invoke(rp, s2)).equals(params);
			params.clear();
			pass = pass && ((List<String>) method.invoke(rp, s3)).isEmpty();

			return pass;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean parseParameterTest() {
		String s1 = "parameter1,";
		String s2 = "1233110,";
		String s3 = ")";

		try {
			setup();
			boolean pass = true;

			Method method = rp.getClass().getDeclaredMethod("parseParameter", String.class);
			method.setAccessible(true);

			pass = pass && method.invoke(rp, s1).equals("parameter1");
			pass = pass && method.invoke(rp, s2).equals("1233110");
			pass = pass && method.invoke(rp, s3) == null;

			return pass;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
