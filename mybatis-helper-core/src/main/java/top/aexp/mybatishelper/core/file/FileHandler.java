package top.aexp.mybatishelper.core.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author llt
 * @date 2020-08-16 10:00
 */
public interface FileHandler {

    List<String> getAllFilePath(String folder);

    String readJavaFileToString(String filePath, String charset);

    String readFileToString(String filePath, String charset);

    void mkdir(String path);

    void writerString2File(String fileName, String toString, String charset) throws IOException;

    OutputStream getOutputStream(String path)throws FileNotFoundException;

    boolean exists(String path);
}
