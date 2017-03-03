/**
 * RecipeParser is used to parse a single recipe into a comand 
 * structure so that we can more easily execute a set of rules.
 *
 * TODO: Change so StringBuffer is used for better performance.
 * TODO: Fix custom Errors for better handling of incorrect 
 * input instead of returning nulls.
 */

import java.io.File;
import java.nio.file.*;
import java.nio.*;
import java.io.*;
import java.util.*;

public class RecipeParser {
	// Takes file and tries to parse it into a list of commands.
	public List<Command> parseRecipe(String file) throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		List<Command> commands = new ArrayList<Command>();
		String line;

		while ((line = br.readLine()) != null) {
			Command c = parseStatement(line);
			commands.add(c);
		}

		return commands;
	}

	// Tries to parse a single string into a command
	private Command parseStatement(String line) {
		String command = parseCommand(line);
		if (command == null)
			return null;
		List<String> params = parseParameters(line);
		if (params == null)
			return null;
		return new Command(command, params);
	}

	// Tries to parse the command portion of the given String
	private String parseCommand(String line) {
		String s = "";
		char c;

		//TODO: change to better performance loop with charAt instead
		for (int i=0; i<line.length(); i++) {
			if ((c = line.charAt(i)) == '(') {
				if (s.isEmpty() || i == 0)
					return null;
				return s;
			}
			s += c;
		}

		return null;
	}

	// Tries to parse the parameters portion of the given String
	private List<String> parseParameters(String line) {
		List<String> params = new ArrayList<String>();
		int i=0;

		while (line.charAt(i) != '(')
			i++;

		String param;
		while ((param = parseParameter(line.substring(i+1))) != null) {
			i += param.length()+1;
			params.add(param);
		}

		return params;
	}

	// Tries to parse a single parameter of the given String
	private String parseParameter(String line) {
		String s = "";
		char c;

		if (line.equals("") || line.equals(")"))
			return null;

		for (int i=0; i<line.length(); i++) {
			if ((c = line.charAt(i)) == ',' || c == ')') {
				return s;
			} else {
				s += c;
			}
		}

		// Should throw error, Argument Exception
		return "test";
	}
}
