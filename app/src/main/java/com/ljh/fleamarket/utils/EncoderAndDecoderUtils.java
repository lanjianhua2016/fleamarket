package com.ljh.fleamarket.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class EncoderAndDecoderUtils {
    public static String enCoder(String encoderStr){
        try
        {
            encoderStr = URLEncoder.encode(encoderStr, "utf-8");
            return encoderStr;
        } catch (UnsupportedEncodingException e) {
            return encoderStr;
        }
    }

    public static String deCoder(String decoderStr){
        try
        {
            decoderStr = URLDecoder.decode(decoderStr, "utf-8");
            return decoderStr;
        } catch (UnsupportedEncodingException e) {
            return decoderStr;
        }
    }
}
