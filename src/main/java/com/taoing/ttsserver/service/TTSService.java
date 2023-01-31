package com.taoing.ttsserver.service;

import com.taoing.ttsserver.dto.ReadReq;
import com.taoing.ttsserver.utils.FileUtils;
import com.taoing.ttsserver.vo.TextTTSReq;
import com.taoing.ttsserver.ws.TTSWebSocket;
import com.taoing.ttsserver.ws.WebSocketManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

@Slf4j
@Service
public class TTSService {

    @Autowired
    private WebSocketManager webSocketManager;
    @Autowired
    private FileUtils fileUtils;

    /**
     * 合成文本语音
     * @param response
     * @param param
     */
    public void synthesisAudio(HttpServletResponse response, TextTTSReq param) {
        TTSWebSocket client = webSocketManager.getClient(param.getKey());
        if (client == null) {
            log.warn("请求key非法");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        client.send(param.getText());
        client.writeAudioStream(response);
    }

    /**
     * 逐行读取文件合成语音
     * @param response
     * @param readReq
     * @return
     */
    public void speechTxt(HttpServletResponse response, ReadReq readReq) {
        String key = readReq.getKey();
        TTSWebSocket client = webSocketManager.getClient(key);
        if (client == null) {
            log.warn("请求key非法");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        //第一次清空缓冲区
        client.init();

        //逐行读取合成语音
        String line;
        try {
            //todo 测试
            String filePath = "Q:\\E0010443\\Downloads\\" + readReq.getFileName();
            File txtFile = new File(filePath);
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(txtFile), fileUtils.resolveCharset(filePath));
            BufferedReader br = new BufferedReader(reader);
            while ((line = br.readLine()) != null) {
                client.send(line, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回结果
        client.writeAudioStream(response);
    }
}
