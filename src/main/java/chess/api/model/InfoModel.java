package chess.api.model;

public class InfoModel {

    public boolean success;
    public String info;

    public InfoModel(boolean success, String info) {
        this.success = success;
        this.info = info;
    }
}
