package cn.misakanet.server.handler;

import cn.misakanet.server.ServerConfig;
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

public class DefaultPageHandler implements HttpHandler {
    private ServerConfig serverConfig = ServerConfig.getInstance();

    @Override
    public void handle(HttpExchange http) throws IOException {
        File reqFile;
        if (http.getRequestURI().getPath().equals("/")) {
            reqFile = new File((String) serverConfig.getConfig("resource"), serverConfig.getConfig("defaultPage"));
        } else {
            reqFile = new File((String) serverConfig.getConfig("resource"), http.getRequestURI().getPath());
        }
        if (reqFile.exists()) {
            fileHandler(http, reqFile);
        } else {
            sendError(http, 404);
        }
    }

    private void fileHandler(HttpExchange http, File file) throws IOException {
        String contentType = getContentType(file);
        String lastModified = new Date(file.lastModified()).toString();

        Headers headers = http.getResponseHeaders();
        headers.add("Content-Type", contentType + "; charset=utf-8");
        headers.add("Content-Encoding", "gzip");
        headers.add("Last-modified", lastModified);
        headers.add("Expires", Integer.MAX_VALUE + "");
        headers.add("Cache-Control", "public");
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

    private void sendError(HttpExchange http, int code) throws IOException {
        http.sendResponseHeaders(code, 0);
        http.close();
    }

    private String getContentType(File file) {
        return new MimetypesFileTypeMap().getContentType(file);
    }
}
