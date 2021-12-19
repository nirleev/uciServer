package chess.swagger.model;

public class CommandModel {

    public String serverName;

    public String command;

    public String source;

    @Override
    public String toString() {
        return "command='" + command + '\'' +
                '}';
    }
}
