package chess.api.model;

public class CommandModel {

    public String serverName;

    public String command;

    @Override
    public String toString() {
        return "command='" + command + '\'' +
                '}';
    }
}
