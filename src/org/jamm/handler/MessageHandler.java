package org.jamm.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.jamm.session.SessionManager;

public class MessageHandler extends ChannelInboundHandlerAdapter implements GenericFutureListener<Future<Channel>> {
    private SessionManager manager;

    public MessageHandler() {
        manager = new SessionManager();
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(this);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String strMsg = (String) msg;
        System.out.println(strMsg);// TODO debug code
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void operationComplete(Future<Channel> ch) throws Exception {
        manager.add(ch.get());
    }
}
