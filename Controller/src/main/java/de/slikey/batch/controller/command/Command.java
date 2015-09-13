package de.slikey.batch.controller.command;

import de.slikey.batch.controller.MainController;

/**
 * @author Kevin
 * @since 09.09.2015
 */
public abstract class Command {

    private final MainController mainController;
    private final String label;
    private final String[] aliases;

    public Command(MainController mainController, String label, String[] aliases) {
        this.mainController = mainController;
        this.label = label;
        this.aliases = aliases;
    }

    public final MainController getMainController() {
        return mainController;
    }

    public final String getLabel() {
        return label;
    }

    public final String[] getAliases() {
        return aliases;
    }

    public final boolean shouldHandle(String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase(label)) {
                return true;
            }
            if (aliases != null) {
                for (String alias : aliases) {
                    if (args[0].equalsIgnoreCase(alias)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public abstract boolean execute(String[] args);

    public abstract String getUsage();

}
