package com.taoing.ttsserver.server;

import com.taoing.ttsserver.utils.ApplicationContextUtil;
import com.taoing.ttsserver.utils.FileUtils;
import com.taoing.ttsserver.ws.TTSWebSocket;
import com.taoing.ttsserver.ws.WebSocketManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author : freaxjj.liu
 * @date 2023/2/9
 */
@Slf4j
@Service
public class WsBinder {
    private static WebSocketManager webSocketManager;

    private static Map<TTSWebSocket, ChannelHandlerContext> clientChannelMap = new HashMap<>();

    public static Boolean isBounded(TTSWebSocket client) {
        return clientChannelMap.containsKey(client);
    }

    public static void readShort(String text, ChannelHandlerContext ctx) {
        if(Objects.isNull(webSocketManager)) {
            webSocketManager = ApplicationContextUtil.getBean("webSocketManager");
        }
        //todo
        TTSWebSocket client = webSocketManager.getClient("xxttsa01");
        clientChannelMap.put(client, ctx);

        //todo  读取文件内容
        client.send(text);
        client.writeAudioStream(ctx);
    }

    public static void readFile(String fileName, ChannelHandlerContext ctx) {
        //todo
        if(Objects.isNull(webSocketManager)) {
            webSocketManager = ApplicationContextUtil.getBean("webSocketManager");
        }
        //todo
        TTSWebSocket client = webSocketManager.getClient("xxttsa02");
        clientChannelMap.put(client, ctx);

        FileUtils fileUtils = ApplicationContextUtil.getBean("fileUtils");

        //逐行读取合成语音
        String line;
        try {
            //todo 测试
            String filePath = "Q:\\E0010443\\Downloads\\" + fileName;
            File txtFile = new File(filePath);
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(txtFile), fileUtils.resolveCharset(filePath));
            BufferedReader br = new BufferedReader(reader);
            while ((line = br.readLine()) != null) {
                client.send(line);
                client.writeAudioStream(ctx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendResp(TTSWebSocket webSocket, byte[] res) {
        ChannelHandlerContext ctx = clientChannelMap.get(webSocket);
        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(res));
        if(Objects.nonNull(res)) {
            ctx.writeAndFlush(frame);
        }else {
            log.warn("BinaryWebSocketFrame is null!");
        }
    }

}
