package org.jamm.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.jamm.message.JsonMessage;
import org.jamm.session.Session;
import org.jamm.session.SessionManager;

public class ChannelHandler extends ChannelInboundHandlerAdapter implements GenericFutureListener<Future<Channel>> {
    public interface OnReadListenerInterface {
        public void onRead(Session s, Object msg);
    }
    private SessionManager manager;
    private OnReadListenerInterface readCallback;

    public ChannelHandler() {
        manager = new SessionManager();
    }

    public void setReadCallback(OnReadListenerInterface readCallback) {
        this.readCallback = readCallback;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(this);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
      //  JsonMessage message = (JsonMessage)msg;
       // System.out.println(message.getBody());// debug code
        readCallback.onRead(manager.get(ctx.channel()), msg);
    }

    public void send(Session s, Object msg) {
        Channel ch = manager.get(s);
        if (ch != null) {
            ch.writeAndFlush(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        manager.remove(ctx.channel());
        ctx.close();
    }

    @Override
    public void operationComplete(Future<Channel> ch) throws Exception {
        manager.add(ch.get());
    }
}
