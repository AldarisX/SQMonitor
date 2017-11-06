package com.crocoro.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

public class FileHandler implements HttpHandler {
    private File file;
    private String lastModified;
    private String contentType = "text/plain";

    public FileHandler(String fileName) {
        this(new File(fileName));
    }

    public FileHandler(File file) {
        this.file = file;
        lastModified = new Date(file.lastModified()).toString();
        this.contentType = getContentType(file);
    }

    @Override
    public void handle(HttpExchange http) throws IOException {
        Headers headers = http.getResponseHeaders();
        headers.add("Content-Type", contentType + "; charset=utf-8");
        headers.add("Content-Encoding", "gzip");
        headers.add("Last-modified", lastModified);
        headers.add("Expires", Integer.MAX_VALUE + "");
        headers.add("Cache-Control", "public");
        headers.add("Pragma", "Pragma");
        headers.add("Server", "Sun HttpServer");
        http.sendResponseHeaders(200, 0);

        OutputStream os = http.getResponseBody();
        GZIPOutputStream gzip = new GZIPOutputStream(os);
        FileChannel inFC = new RandomAccessFile(file.getAbsoluteFile(), "r").getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int bytesRead = inFC.read(buffer);
        while (bytesRead != -1) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                gzip.write(buffer.get());
//                os.write(buffer.get());
            }
            buffer.clear();
            bytesRead = inFC.read(buffer);
        }

        gzip.close();
        os.close();
    }

    private String getContentType(File file) {
        return new MimetypesFileTypeMap().getContentType(file);
    }
}
