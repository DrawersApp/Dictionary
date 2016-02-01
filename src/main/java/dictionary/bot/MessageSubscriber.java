package dictionary.bot;

import org.jivesoftware.smack.packet.Message;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by harshit on 30/1/16.
 */
public interface MessageSubscriber {
    /**
     * @return String
     * This text is shown when {@link #generateReply(Message)} message throws exception.
     */
    String getErrorDefaultText();

    /**
     *
     * @param message
     * @return String
     * This function takes the input message,
     * makes the computation and return the relevant reply.
     */
    default String generateReply(Message message) throws UnsupportedEncodingException {
        String body = message.getBody();
        if (body == null) {
            return "Empty Message";
        }
        body = URLDecoder.decode(body, "UTF-8");
        DrawersBotString drawersBotString = OperationsManager.getOperationsManager().getDrawersBotString(body);
        if (drawersBotString == null) {
            return "Incorrect message type";
        }
        OutputBody outputBody = OperationsManager.getOperationsManager().performOperations(drawersBotString);
        if (outputBody == null) {
            return getErrorDefaultText();
        }
        String replyString = outputBody.toUserString();
        if (replyString == null || replyString.length() == 0) {
            return getErrorDefaultText();
        }
        return replyString;
    }
}
