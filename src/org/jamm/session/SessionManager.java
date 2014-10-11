package org.jamm.session;

import io.netty.channel.Channel;
import org.jamm.util.Utils;

import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final int SESSION_STR_LEN = 8;
    private ConcurrentHashMap<String, Channel> sessions;
    private ConcurrentHashMap<String, Session> sessionsChName;
    
    public SessionManager() {
        this.sessions = new ConcurrentHashMap<String, Channel>();
        this.sessionsChName = new ConcurrentHashMap<String, Session>();
    }
    
    public Session add(Channel ch) {
        String sessionId;

        while (!sessions.contains((sessionId = Utils.GetRandomStr(SESSION_STR_LEN)))) {};

        Session s = new Session(sessionId);
        sessions.put(s.getId(), ch);
        sessionsChName.put(ch.toString(), s);
        return s;
    }

    public Channel remove(Session s) {
        Channel ch = sessions.remove(s.getId());
        sessionsChName.remove(ch.toString());
        return ch;
    }

    public Session remove(Channel ch) {
        Session s = sessionsChName.remove(ch.toString());
        sessions.remove(s);
        return s;
    }
    
    public Channel get(Session s) {
        return sessions.get(s);
    }

    public Session get(Channel ch) {
        return sessionsChName.get(ch.toString());
    }
}
