package org.jamm.message;

import org.jamm.session.Session;

public interface MessageListenerInterface<T> {
    public void onReceive(Session session, T msg);
}
