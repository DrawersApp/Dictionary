import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.sasl.AuthenticationException;
import rocks.xmpp.core.session.TcpConnectionConfiguration;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stream.model.StreamElement;
import rocks.xmpp.extensions.httpbind.BoshConnectionConfiguration;
import rocks.xmpp.extensions.json.model.Json;
import rx.Observable;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .hostname("ejabberd.sandwitch.in")
                .port(5222)
                .proxy(Proxy.NO_PROXY)        // Proxy for the TCP connection
                .keepAliveInterval(20)        // Whitespace keep-alive interval
                .secure(false)
                .build();
        BoshConnectionConfiguration boshConfiguration = BoshConnectionConfiguration.builder()
                .hostname("ejabberd.sandwitch.in")
                .port(5280)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("hostname", 3128)))
                .file("/http-bind/")
                .wait(60)  // BOSH connection manager should wait maximal 60 seconds before responding to a request.
                .build();


        XmppClient xmppClient = new XmppClient("ejabberd.sandwitch.in", tcpConfiguration, boshConfiguration);

        // Listen for messages
        xmppClient.addInboundMessageListener(e -> {
            Message message = e.getMessage();
            // Handle inbound message.
            Observable.just(message)
                    .map(m -> m.getBody())
                    .filter(b -> b!= null && b.length()>0)
                    .map(w -> RetrofitAdapter.getRetrofitAdapter().getDictionaryInterface().getMeaning(w))
                    .map(x -> generateText(x))
                    .filter( y -> y.length() > 0)
                    .subscribe(s -> xmppClient.send(generateMessage(message.getFrom(), Message.Type.CHAT, s)));
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
            xmppClient.login("user", "pwd", "babbler");
        } catch (AuthenticationException e) {
            // Login failed, because the server returned a SASL failure, most likely due to wrong credentials.
        } catch (XmppException e) {
            // Other causes, e.g. no response, failure during resource binding, etc.
        }
    }

    private StreamElement generateMessage(Jid from, Message.Type chat, String s) {
        Message message = new Message(from, chat, s);
        message.setId(UUID.randomUUID().toString());
        message.addExtension(generateJsonContainer(ChatType.TEXT));
        return message;
    }

    private String generateText(Meaning meaning) {
        if (meaning != null && meaning.getDefinitions() != null) {
           return meaning.getDefinitions().stream().map(w  -> w.getText()).collect(Collectors.joining("\n"));
        } else {
            return "";
        }
    }

    public Json generateJsonContainer(ChatType chatType) {
        ChatMetaData  chatMetaData = new ChatMetaData(chatType.toString(), System.currentTimeMillis());
        Json jsonPacketExtension = new Json(chatMetaData.toJsonString());
        return jsonPacketExtension;
    }

}
