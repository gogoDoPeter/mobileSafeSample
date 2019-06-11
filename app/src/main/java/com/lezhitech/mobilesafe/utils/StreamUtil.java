package com.lezhitech.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {
    /**
     * @param is 流对象
     * @return 流转换成字符串     返回null代表异常
     */
    public static String stream2String(InputStream is) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int temp = -1;
        try {
            while ((temp = is.read(buffer)) != -1) {
                bos.write(buffer, 0, temp);
            }
            return bos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String streamToString(InputStream  in){
        String result ="";

        try{
            //创建一个字节数组写入流
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length = 0;
            while (  (length =  in.read(buffer)) !=-1) {
                out.write(buffer, 0, length);
                out.flush();
            }

            result = new String(out.toByteArray(),"utf-8");

//			result = out.toString();//将字节流转换成string

            out.close();
        }catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }
}
