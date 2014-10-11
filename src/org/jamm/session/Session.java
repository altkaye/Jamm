package org.jamm.session;

public class Session {
    private final String id;
    
    public Session(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        return this.id.equals(((Session)obj).id);
    }
    
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
