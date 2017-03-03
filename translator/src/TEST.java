import java.util.*;
import java.nio.*;
import java.nio.file.*;

public class TEST {
	public static void main(String[] args) throws Exception {
		RecipeParser rp = new RecipeParser();
		List<Command> commands = rp.parseRecipe("test.r");

		for (Command c: commands) {
			System.out.println(c);
		}
	}
}
