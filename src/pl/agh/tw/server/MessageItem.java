package pl.agh.tw.server;

/**
 * Created by sirius on 11.03.18.
 */
public class MessageItem {
    private final ClientItem clientItem;
    private final String message;

    public MessageItem(ClientItem clientItem, String message) {
        this.clientItem = clientItem;
        this.message = message;
    }

    public ClientItem getClientItem() {
        return clientItem;
    }

    public String getMessage() {
        return message;
    }
}
