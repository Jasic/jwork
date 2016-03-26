package org.jasic;

import java.nio.ByteBuffer;

/**
 * @Author Jasic.
 * @Date 2011/18/19
 * 读|写
 *----------------------------------------------------------------------------------
 * 前置条件：
 * buffer大小：1Gb
 * element大小：1b
 * 循环：1
 *
 *
 * 测试结果：
 * byteBuffer：12314ms|11249ms
 * directByteBuffer:5091ms|5357ms
 *
 * direct:比jvm快7223ms|5892ms
 * ----------------------------------------------------------------------------------
 *  * 前置条件：
 * buffer大小：1Gb
 * element大小：1b
 * 循环：5
 *
 *
 * 测试结果：
 * byteBuffer：60894ms|62819ms
 * directByteBuffer:26969ms|26720ms
 *
 * direct:比jvm快33925ms|36099ms
 * ----------------------------------------------------------------------------------
 */
public class TestByteBuffer {

    private ByteBuffer byteBuffer;

    private ByteBuffer directByteBuffer;

    private int allCount = 5;

    private byte element[];

    private int bufferSize = 1024 * 1024 * 1024;
    long start;
    long end;

    public TestByteBuffer() {
        setup();
    }


    private void setup() {
        byteBuffer = ByteBuffer.allocate(bufferSize);
        directByteBuffer = ByteBuffer.allocateDirect(bufferSize);
        element = new byte[1];
    }

    public long testByteBufferRead() {
        start = System.currentTimeMillis();
        int count = 0;
        for (; count < allCount; count++)
            for (; ; ) {
                byteBuffer.get(element);
                if (byteBuffer.position() == byteBuffer.capacity()) {
//                    System.out.printf("从开始第[" + count + "]次花费时间[" + (System.currentTimeMillis() - start) + "] ms\n");
                    byteBuffer.rewind();
                    break;
                }
            }
        end = System.currentTimeMillis();
        System.out.printf("从开始第[" + count + "]次花费时间[" + (end - start) + "ms] \n");

        return end - start;

    }

    public long testDirectByteBufferRead() {
        start = System.currentTimeMillis();
        int count = 0;
        for (; count < allCount; count++)
            for (; ; ) {
                directByteBuffer.get(element);
                if (directByteBuffer.position() == directByteBuffer.capacity()) {
                    directByteBuffer.rewind();
//                    System.out.printf("从开始第[" + count + "]次花费时间[" + (System.currentTimeMillis() - start) + "] ms\n");
                    break;
                }
            }
        end = System.currentTimeMillis();
        System.out.printf("从开始第[" + count + "]次花费时间[" + (end - start) + "ms] \n");
        return end - start;
    }


    public long testByteBufferWrite() {
        start = System.currentTimeMillis();
        int count = 0;
        for (; count < allCount; count++)
            for (; ; ) {
                byteBuffer.put(element);
                if (byteBuffer.position() == byteBuffer.capacity()) {
                    byteBuffer.rewind();
//                    System.out.printf("从开始第[" + count + "]次花费时间[" + (System.currentTimeMillis() - start) + "] ms\n");
                    break;
                }
            }
        end = System.currentTimeMillis();
        System.out.printf("从开始第[" + count + "]次花费时间[" + (end - start) + "ms] \n");

        return end - start;

    }

    public long testDirectByteBufferWrite() {
        start = System.currentTimeMillis();
        int count = 0;
        for (; count < allCount; count++)
            for (; ; ) {
                directByteBuffer.put(element);
                if (directByteBuffer.position() == directByteBuffer.capacity()) {
                    directByteBuffer.rewind();
//                    System.out.printf("从开始第[" + count + "]次花费时间[" + (System.currentTimeMillis() - start) + "] ms\n");
                    break;
                }
            }
        end = System.currentTimeMillis();
        System.out.printf("从开始第[" + count + "]次花费时间[" + (end - start) + "ms] \n");
        return end - start;
    }




    public static void main(String[] args) {
//        new TestByteBuffer().testByteBufferRead();
//        new TestByteBuffer().testDirectByteBufferRead();

        new TestByteBuffer().testByteBufferWrite();
        new TestByteBuffer().testDirectByteBufferWrite();
    }
}
