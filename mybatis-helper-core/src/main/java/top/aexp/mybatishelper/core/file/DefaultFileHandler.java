package top.aexp.mybatishelper.core.file;

import top.aexp.mybatishelper.core.exception.MybatisHelperException;
import top.aexp.mybatishelper.core.utils.FileUtils;

import java.io.*;
import java.util.List;

/**
 * @author llt
 * @date 2020-08-16 10:00
 */
public class DefaultFileHandler implements FileHandler{
    @Override
    public List<String> getAllFilePath(String folder) {
        return FileUtils.getAllFilePath(folder);
    }

    @Override
    public String readJavaFileToString(String filePath, String charset) {
        return FileUtils.readJavaFileToString(filePath,charset);
    }

    @Override
    public String readFileToString(String filePath, String charset) {
        return FileUtils.readFileToString(filePath,charset);
    }

    @Override
    public void mkdir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new MybatisHelperException("创建文件夹失败");
            }
        }
    }

    @Override
    public void writerString2File(String fileName, String str, String charset) throws IOException {
        FileUtils.writerString2File(fileName,str,charset);
    }

    @Override
    public OutputStream getOutputStream(String path) throws FileNotFoundException {
        return new FileOutputStream(new File(path));
    }

    @Override
    public boolean exists(String path) {
        return new File(path).exists();
    }
}
