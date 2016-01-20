import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.sasl.AuthenticationException;
import rocks.xmpp.core.session.TcpConnectionConfiguration;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.extensions.httpbind.BoshConnectionConfiguration;
import rocks.xmpp.im.roster.RosterManager;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * Created by harshit on 20/1/16.
 */
public class Bot {

    private static Bot bot;

    private Bot() {
        initializeConnection();
    }

    public static synchronized Bot getBot() {
        if (bot == null) {
            bot = new Bot();
        }
        return bot;
    }

    public void initializeConnection() {
        TcpConnectionConfiguration tcpConfiguration = TcpConnectionConfiguration.builder()
                .hostname("domain")
                .port(5222)
                .proxy(Proxy.NO_PROXY)        // Proxy for the TCP connection
                .keepAliveInterval(20)        // Whitespace keep-alive interval
                .secure(false)
                .build();
        BoshConnectionConfiguration boshConfiguration = BoshConnectionConfiguration.builder()
                .hostname("domain")
                .port(5280)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("hostname", 3128)))
                .file("/http-bind/")
                .wait(60)  // BOSH connection manager should wait maximal 60 seconds before responding to a request.
                .build();


        XmppClient xmppClient = new XmppClient("domain", tcpConfiguration, boshConfiguration);

        // Listen for messages
        xmppClient.addInboundMessageListener(e -> {
            Message message = e.getMessage();
            // Handle inbound message.

            xmppClient.send(new Message(message.getFrom(), Message.Type.CHAT, message.getBody()));
        });

        try {
            xmppClient.connect();
        } catch (XmppException e) {
            e.printStackTrace();
        }

        /*
          Third parameter is resource. Used to support chat across different entities like Laptop/Mobile.
          Hard code resource to the client/libray - help us identify the client.
         */

        try {
            xmppClient.login("username", "password", "babbler");
        } catch (AuthenticationException e) {
            // Login failed, because the server returned a SASL failure, most likely due to wrong credentials.
        } catch (XmppException e) {
            // Other causes, e.g. no response, failure during resource binding, etc.
        }
    }
}
