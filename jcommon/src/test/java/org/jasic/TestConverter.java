package org.jasic;

import org.jasic.util.EncodeConvertor;

/**
 * @Author 菜鹰.
 * @Date 2014/12/16
 */
public class TestConverter {

    public static void main(String[] args) {
        EncodeConvertor convertor = new EncodeConvertor();
        convertor.encodeConverted("D:\\klive\\midea\\wspace\\schedulecenter-resp\\schedulecenter-dev",
                "GBK","D:\\klive\\midea\\wspace\\schedulecenter-resp\\schedulecenter-trunk","UTF-8","properties");
    }
}
