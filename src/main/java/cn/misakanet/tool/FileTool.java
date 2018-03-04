package cn.misakanet.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileTool {
    public static String readFile(String file) throws FileNotFoundException {
        return readFile(new File(file));
    }

    public static String readFile(File file) throws FileNotFoundException {
        ByteBuffer buff = ByteBuffer.allocate(1024);
        try (
                FileInputStream fis = new FileInputStream(file);
                FileChannel channel = fis.getChannel()
        ) {
            StringBuffer sb = new StringBuffer("");
            int data = channel.read(buff);
            while (data != -1) {
                buff.flip();
                while (buff.hasRemaining()) {
                    //System.out.print((char) buff.get());
                    sb.append((char) buff.get());
                }
                buff.clear();
                data = channel.read(buff);
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            //找不到文件的话就把异常抛出
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
