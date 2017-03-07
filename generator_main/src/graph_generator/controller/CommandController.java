package graph_generator.controller;

import graph_generator.parser.Command;
import graph_generator.parser.DoNTimes;
import graph_generator.parser.DoOnce;
import utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all possible usable commands and allows execution of them.
 *
 * Created by time on 3/4/17.
 */
public class CommandController {
    public List<Command> commands = new ArrayList<>();
    public static CommandController controller = null;

    public static CommandController getInstance() {
        if (controller == null)
            controller = new CommandController();
        return controller;
    }

    /**
     * Adds all possible commands to list of possible commands.
     */
    private CommandController() {
        commands.add(new DoOnce());
        commands.add(new DoNTimes());
    }

    /**
     * Get specified command.
     *
     * @param match
     * Command being searched for.
     * @return
     * Command if found, null otherwise.
     */
    public Command getCommand(String match) {
        Log.print("Retrieving command "+match+" from list of commands.", Log.LEVEL.INFO);
        for (int i=0; i<commands.size(); i++) {
            if (commands.get(i).getName().equals(match))
                return commands.get(i);
        }

        return null;
    }
}
