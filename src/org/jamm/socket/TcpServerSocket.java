package org.jamm.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.jamm.handler.ChannelHandler;
import org.jamm.message.MessageListenerInterface;
import org.jamm.pipeline.JsonMessageDecoder;
import org.jamm.pipeline.JsonMessageEncoder;
import org.jamm.session.Session;
import org.jamm.util.DebugLog;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class TcpServerSocket<T> implements ServerSocketInterface<T> {
    private final int port;
    private final boolean usesSelfSsl;
    private ChannelFuture channelFuture;
    
    private ChannelHandler handler;

    private List<MessageListenerInterface<T>> messageReceivedListeners;

    private Thread serverThread;

   // ExecutorService exec;

    //TODO
    private TcpServerSocket(int socketPort, int threadPoolSize, boolean usesSelfSsl) {
        this.port = socketPort;
        this.usesSelfSsl = usesSelfSsl;
       // exec = Executors.newFixedThreadPool(threadPoolSize);
        messageReceivedListeners = new ArrayList<MessageListenerInterface<T>>();
    }
    
    public TcpServerSocket(int socketPort) {
        this(socketPort, 3, true);
    }
    
    public void close() {
        channelFuture.channel().close();
    }

    @Override
    public void addMessageListener(MessageListenerInterface<T> listenerInterface) {
        messageReceivedListeners.add(listenerInterface);
    }

    private void callOnReceive(Session s, Object msg) {
       // exec.submit(()-> {
            for (MessageListenerInterface<T> listener : messageReceivedListeners) {
                listener.onReceive(s, (T)msg);
            }
        //});
    }

    @Override
    public void send(Session s, T msg) {
        handler.send(s, msg);
    }

    private void openSocket() {
        handler = new ChannelHandler();
        handler.setReadCallback((s, obj) -> {
            callOnReceive(s, obj);
        });
        final SelfSignedCertificate ssc;
        final SslContext sslCtx;
        if (usesSelfSsl) {
            try {
                ssc = new SelfSignedCertificate();
                sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
            } catch (CertificateException | SSLException e1) {
                e1.printStackTrace();
                return;
            }
        } else {
            ssc = null;
            sslCtx = null;
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap severBootstrap = new ServerBootstrap();
            severBootstrap.group(bossGroup, workerGroup);
            severBootstrap.channel(NioServerSocketChannel.class);
            severBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    if (usesSelfSsl) {
                        pipeline.addLast(sslCtx.newHandler(ch.alloc()));
                    }
                    pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                    pipeline.addLast(new JsonMessageDecoder());
                    pipeline.addLast(new JsonMessageEncoder());
                    pipeline.addLast(handler);
                }
            });
            severBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            severBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            channelFuture = severBootstrap.bind(port);
            DebugLog.d("starting server socket");
            channelFuture.sync();
            channelFuture.channel().closeFuture().sync();
            DebugLog.d("closing server socket");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            DebugLog.d("closed server socket.");
        }


    }

    public void open() {
        serverThread = new Thread(()-> {
            openSocket();
        });
        serverThread.start();
    }
}
