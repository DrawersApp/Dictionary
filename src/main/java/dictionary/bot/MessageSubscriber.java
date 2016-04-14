package dictionary.bot;

import org.drawers.bot.dto.DrawersMessage;

/**
 * Created by harshit on 30/1/16.
 */
public interface MessageSubscriber {
    /**
     * @return String
     * This text is shown when {@link #generateReply(DrawersMessage)} message throws exception.
     */
    DrawersMessage getErrorDefaultMessage(DrawersMessage message);

    /**
     *
     * @return String
     * Publishes text to algolia.
     */
    default void publishText() {
         DrawersBotStringHelp.getDrawersBotStringHelp().toJsonString();
    }

    /**
     *
     * @return String
     * This function takes the input message,
     * makes the computation and return the relevant reply.
     */
    default DrawersMessage generateReply(DrawersMessage message) {

        try {
            String body = message.getMessage();
            DrawersBotString drawersBotString = OperationsManager.getOperationsManager().getDrawersBotString(body);
            if (drawersBotString == null) {
                return new DrawersMessage(message.getSender(), "Incorrect message type");
            }
            OutputBody outputBody = OperationsManager.getOperationsManager().performOperations(drawersBotString);
            if (outputBody == null) {
                return getErrorDefaultMessage(message);
            }
            String replyString = outputBody.toUserString();
            System.out.println(replyString);
            if (replyString == null || replyString.length() == 0) {
                return getErrorDefaultMessage(message);
            }
            return new DrawersMessage(message.getSender(), replyString);
        } catch (Exception ex) {
         //   ex.printStackTrace();
            return new DrawersMessage(message.getSender(), "Something went wrong");
        }
    }
}
