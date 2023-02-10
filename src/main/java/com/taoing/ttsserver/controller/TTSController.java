package com.taoing.ttsserver.controller;

import com.taoing.ttsserver.dto.ReadReq;
import com.taoing.ttsserver.service.TTSService;
import com.taoing.ttsserver.vo.TextTTSReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequestMapping(value = "/tests")
@RestController
public class TTSController {
    @Autowired
    private TTSService ttsService;

    /**
     * 合成文本语音
     * 接口不支持多线程, 因此, 一个key只支持一个用户调用
     * 可用key: "xxttsa01", "xxttsa02", "xxttsa03"
     * @param response
     * @param param
     */
    @PostMapping(value = "/text2Speech")
    public void text2Speech(HttpServletResponse response, @RequestBody TextTTSReq param) {
        if (log.isDebugEnabled()) {
            log.debug("req: {}", param.toString());
        }
        if (!StringUtils.hasLength(param.getKey())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (!StringUtils.hasLength(param.getText())) {
            return;
        }
        this.ttsService.synthesisAudio(response, param);
    }

    @PostMapping(value = "/readShort")
    public void readShort(HttpServletResponse response, @RequestBody ReadReq readReq) {
        if (log.isDebugEnabled()) {
            log.debug("req: {}", readReq.toString());
        }
        if (!StringUtils.hasLength(readReq.getKey())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (!StringUtils.hasLength(readReq.getFileName())) {
            return;
        }
        //朗读文件
        ttsService.speechTxt(response, readReq);
    }
}
