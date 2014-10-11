package org.jamm.message;

import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.jamm.session.Session;

public abstract class AbstractMessage {
    private Session session;//TODO dirty
    public AbstractMessage() {};

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public abstract MessageToMessageDecoder getDecoder();
    public abstract MessageToMessageEncoder getEncoder();
}
