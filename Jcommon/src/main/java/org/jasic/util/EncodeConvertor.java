package org.jasic.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;


/**
 * 编码转换工具类
 */
public class EncodeConvertor {
    /**
     * 修改文本类型的编码格式，如：将ut8转成gbk
     *
     * @param srcDir     源目录
     * @param srcEncode  源编码
     * @param destDir    目标目录
     * @param destEncode 目标编码
     * @param fileType   文件类型(如:java,xml)
     * @return
     */
    @SuppressWarnings("unchecked")
    public int encodeConverted(String srcDir, String srcEncode, String destDir, String destEncode, String fileType) {
        //获取所有fileType文件
        Collection<File> fileCollection = FileUtils.listFiles(new File(srcDir), new String[]{fileType}, true);

        for (File srcFile : fileCollection) {
            //最终的格式文件路径
            String destFilePath = destDir + srcFile.getAbsolutePath().substring(srcDir.length());
            //使用srcEncode读，destEncode写入; 如：使用GBK读取数据，然后用UTF-8写入数据
            try {
                FileUtils.writeLines(new File(destFilePath), destEncode, FileUtils.readLines(srcFile, srcEncode));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return fileCollection.size();
    }
}
