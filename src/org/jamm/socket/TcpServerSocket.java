package org.jamm.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import org.jamm.handler.MessageHandler;

public class TcpServerSocket {
    private final int port;
    private final boolean usesSelfSsl;
    private ChannelFuture channelFuture;
    
    private MessageHandler handler;

    public TcpServerSocket(int socketPort, boolean usesSelfSsl) {
        this.port = socketPort;
        this.usesSelfSsl = usesSelfSsl;
    }
    
    public TcpServerSocket(int socketPort) {
        this(socketPort, false);
    }
    
    public void stop() {
        //TODO
    }

    public void start() {
        handler = new MessageHandler();
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
                    pipeline.addLast(new StringDecoder());
                    pipeline.addLast(new StringEncoder());
                    pipeline.addLast(handler);
                }
            });
            severBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            severBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            channelFuture = severBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
