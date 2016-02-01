package dictionary.bot;

import org.jivesoftware.smack.SmackException;
import org.jxmpp.stringprep.XmppStringprepException;

/**
 * Created by harshit on 20/1/16.
 */
public class BotsCaller implements MessageSubscriber {

    public static void main(String[] args) throws XmppStringprepException, SmackException.NotConnectedException, InterruptedException, ClassNotFoundException {
        // Load all the classes which contains string.
        Class.forName(MeaningOperations.class.getName());
        System.out.print(DrawersBotStringHelp.getDrawersBotStringHelp().toJsonString());
        BotsCaller botsCaller = new BotsCaller();
        Bot.getBot(botsCaller);
        while (true) {
            Thread.sleep(1000000000000L);
        }
    }

    private String errorDefaultText = "Something went wrong";

    @Override
    public String getErrorDefaultText() {
        return errorDefaultText;
    }
}
