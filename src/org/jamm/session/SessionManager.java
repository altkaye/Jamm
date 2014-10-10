package org.jamm.session;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private ConcurrentHashMap<String, Channel> sessions;
    
    public SessionManager() {
        this.sessions = new ConcurrentHashMap<String, Channel>();
    }
    
    public Session add(Channel ch) {
        Session s = new Session("");//TODO
        sessions.put(s.getId(), ch);
        return s;
    }
    
    public Channel get(Session s) {
        return sessions.get(s);
    }
}
