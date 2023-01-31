package com.taoing.ttsserver.utils;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author : freaxjj.liu
 * @date 2023/1/31
 */
@Component
public class FileUtils {
    /**
     * 读取txt成字符串
     * @param filePath
     * @return
     */
    public static String readTxt(String filePath) {
        StringBuilder content = new StringBuilder();
        String line;
        try {
            File filename = new File(filePath);
            String charset = resolveCharset(filePath);
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename), charset);
            BufferedReader br = new BufferedReader(reader);
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public static void main(String[] args) {
        System.out.println(readTxt("Q:\\E0010443\\Downloads\\《重生2011》.txt"));
    }

    public static String resolveCharset(String path) throws Exception {
        InputStream inputStream = new FileInputStream(path);
        byte[] head = new byte[3];
        inputStream.read(head);
        String charset = "gb2312";  //或GBK
        if (head[0] == -1 && head[1] == -2 )
            charset = "UTF-16";
        else if (head[0] == -2 && head[1] == -1 )
            charset = "Unicode";
        else if(head[0]==-17 && head[1]==-69 && head[2] ==-65)
            charset = "UTF-8";
        inputStream.close();
        return charset;
    }
}
