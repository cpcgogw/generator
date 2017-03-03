import java.util.*;

public class Command {
	private final String command;
	private final List<String> params;

	public Command(String command, List<String> params) {
		this.command = command;
		this.params = params;
	}

	public String getCommand() {return command;}
	public List<String> getParams() {return params;}

	public void execute() {//Graph g) {
		switch (command) {
			case "DoOnce":
				doOnce();//, g);
				break;
			default: return;
		}
	}

	public void doOnce() {
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Command) {
			return command.equals(((Command) o).getCommand()) && params.equals(((Command) o).getParams());
		}
		return false;
	}

	@Override
	public String toString() {
		String s = "";
		s += "Command: "+command+" ";
		s += "Parameters: "+params+" ";
		return s;
	}
}
