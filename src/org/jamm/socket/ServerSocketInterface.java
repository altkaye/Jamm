package org.jamm.socket;

import org.jamm.message.MessageListenerInterface;
import org.jamm.session.Session;

public interface ServerSocketInterface<T> {
    public void addMessageListener(MessageListenerInterface<T> listenerInterface);
    public void send(Session s, T msg);
    public void open();
    public void close();
}
