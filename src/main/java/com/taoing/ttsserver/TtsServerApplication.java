package com.taoing.ttsserver;

import com.taoing.ttsserver.server.WsChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TtsServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TtsServerApplication.class, args);

        //启动websocket服务器
        startWsServer();
    }

    public static void startWsServer() {
        // 一个主线程组(用于监听新连接并初始化通道)，一个分发线程组(用于IO事件的处理)
        EventLoopGroup mainGroup = new NioEventLoopGroup(1);
        EventLoopGroup subGroup = new NioEventLoopGroup();
        ServerBootstrap sb = new ServerBootstrap();
        try {
            sb.group(mainGroup, subGroup)
                    .channel(NioServerSocketChannel.class)
                    // 这里是一个自定义的通道初始化器，用来添加编解码器和处理器
                    .childHandler(new WsChannelInitializer());
            // 绑定88端口，Websocket服务器的端口就是这个
            ChannelFuture future = sb.bind(8888).sync();
            // 一直阻塞直到服务器关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            mainGroup.shutdownGracefully();
            subGroup.shutdownGracefully();
        }
    }

}
