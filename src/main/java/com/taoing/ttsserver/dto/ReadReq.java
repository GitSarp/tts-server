package com.taoing.ttsserver.dto;

import com.taoing.ttsserver.vo.TextTTSReq;
import lombok.Data;

/**
 * @author : freaxjj.liu
 * @date 2023/1/31
 */
@Data
public class ReadReq extends TextTTSReq {
    /**
     * 文件名称或id
     */
    private String fileName;
}
