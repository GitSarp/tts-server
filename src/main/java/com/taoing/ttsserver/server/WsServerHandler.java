package com.taoing.ttsserver.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : freaxjj.liu
 * @date 2023/2/9
 */
@Slf4j
public class WsServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    /**
     * 用于记录和管理所有客户端的channel
     */
    private ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 获取客户端传输来的文本消息
        String text = msg.text();
        // 这个是自定义的日志工具类，可见其它文章
        log.info("收到的文本消息：[{}]", text);
        // 在这里可以判断消息类型(比如初始化连接、消息在客户端间传输等)
        // 然后可以将客户端Channel与对应的唯一标识用Map关联起来，就可以做定向推送，而不是广播

        if(text.startsWith("read:")) { //一次性朗读
            String content = text.substring(5);
            WsBinder.readShort(content, ctx);
        } else if (text.startsWith("readFile:")) { //循环朗读文件
            String fileName = text.substring(9);
            WsBinder.readFile(fileName, ctx);
        }

        // 写回客户端，这里是广播
        //clients.writeAndFlush(new TextWebSocketFrame("服务器收到消息: " + text));
    }

    /**
     * 当客户端连接服务端(打开连接)后
     * 获取客户端的channel，并放到ChannelGroup中进行管理
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        // 不能在这里做关联，因为这里不能接受客户端的消息，是没法绑定的
        clients.add(ctx.channel());
    }

    /**
     * 当触发当前方法时，ChannelGroup会自动移除对应客户端的channel
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        log.info("客户端断开连接，channel的长ID：[{}]", ctx.channel().id().asLongText());
        log.info("客户端断开连接，channel的短ID：[{}]", ctx.channel().id().asShortText());
    }
}
