package chess.api.model;

public class InfoListModel<T> {

    public boolean success;
    public T[] info;

    public InfoListModel(boolean success, T[] info) {
        this.success = success;
        this.info = info;
    }
}
