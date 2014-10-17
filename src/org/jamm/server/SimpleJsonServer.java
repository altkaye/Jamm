package org.jamm.server;

import org.jamm.message.JsonMessage;
import org.jamm.socket.ServerSocketInterface;
import org.jamm.socket.TcpServerSocket;

import java.util.Queue;

public class SimpleJsonServer extends AbstractServer<JsonMessage> {
    public SimpleJsonServer() {
        super(60, new TcpServerSocket<JsonMessage>(8888));
    }

    @Override
    public void onUpdate(Queue<JsonMessage> jsonMessages) {
        // write logic here
        while (jsonMessages.size() > 0) {
            JsonMessage mes = jsonMessages.poll();
            System.out.print(mes.getBody().toString());
        }
    }
}
