package org.jasic.jrpc.rpublish.intf;

import java.io.UnsupportedEncodingException;

/**
 * @Author 菜鹰.
 * @Date 2014/12/16
 */
public class ServiceInterfaceImpl implements ServiceInterface {

    @Override
    public String playLol() {
        String result = "default";
        try {
            result = new String("小学生游戏，你回家鲁管子吧~".getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String playDota() {
        String result = "default";
        try {
            result = new String("好啊一起开黑，网鱼网咖不见不散~".getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
