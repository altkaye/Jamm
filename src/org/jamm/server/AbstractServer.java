package org.jamm.server;

import org.jamm.message.AbstractMessage;
import org.jamm.message.MessageListenerInterface;
import org.jamm.session.Session;
import org.jamm.socket.ServerSocketInterface;

import java.util.LinkedList;
import java.util.Queue;

abstract public class AbstractServer<TMessage extends AbstractMessage> implements MessageListenerInterface<TMessage> {
    private Queue<TMessage> messageQueue;
    private volatile boolean opened;
    private final int targetFps;

    private Thread thread;

    private ServerSocketInterface<TMessage> socketInterface;

    public AbstractServer(int targetFps, ServerSocketInterface<TMessage> socketInterface) {
        messageQueue = new LinkedList<TMessage>();
        this.targetFps = targetFps;
        this.socketInterface = socketInterface;
        this.socketInterface.addMessageListener(this);
    }

    protected ServerSocketInterface<TMessage> getSocketInterface() {
        return socketInterface;
    }

    public void open() {
        opened = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                socketInterface.open();
                updateCall();
            }
        });
        thread.start();
    }

    public void close() {
        socketInterface.close();
        opened = false;
    }

    public void updateCall() {
        Queue<TMessage> tempQueue = new LinkedList<TMessage>();
        long error = 0;
        long idealSleep = (1000 << 16) / targetFps;
        long oldTime;
        long newTime = System.currentTimeMillis() << 16;
        while (opened) {
            oldTime = newTime;

            synchronized (messageQueue) {
                while (messageQueue.size() > 0) {
                    tempQueue.add(messageQueue.poll());
                }
            }

            onUpdate(tempQueue);

            newTime = System.currentTimeMillis() << 16;
            long sleepTime = idealSleep - (newTime - oldTime) - error;
            if (sleepTime < 0x20000) sleepTime = 0x20000;
            oldTime = newTime;
            try {
                Thread.sleep(sleepTime >> 16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            newTime = System.currentTimeMillis() << 16;
            error = newTime - oldTime - sleepTime;
        }
    }

    abstract public void onUpdate(Queue<TMessage> messageQueue);

    @Override
    public void onReceive(Session s, TMessage msg) {
        msg.setSession(s);
        messageQueue.add(msg);
    }
}
