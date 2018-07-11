package cn.misakanet.server.handler;

import cn.misakanet.server.ServerConfig;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

public class DefaultPageHandler implements HttpHandler {
    private ServerConfig serverConfig = ServerConfig.getInstance();

    Pattern numPattern = Pattern.compile("^[-\\+]?[\\d]*$");

    @Override
    public void handle(HttpExchange http) throws IOException {
        File reqFile;
        if (http.getRequestURI().getPath().equals("/")) {
            reqFile = new File((String) serverConfig.getConfig("resource"), serverConfig.getConfig("defaultPage"));
        } else {
            reqFile = new File((String) serverConfig.getConfig("resource"), http.getRequestURI().getPath());
        }
        if (reqFile.exists()) {
            Headers reqHead = http.getRequestHeaders();
            if (reqHead.containsKey("Range") || ("identity".equals(reqHead.getFirst("Accept-encoding")) && "*/*".equals(reqHead.getFirst("Accept")) && "*".equals(reqHead.getFirst("Accept-charset")))) {
                fileDownloadHandler(http, reqFile);
            } else {
                fileHandler(http, reqFile);
            }
//            fileHandler(http, reqFile);
        } else {
            sendError(http, 404);
        }
    }

    private void fileHandler(HttpExchange http, File file) throws IOException {
        String contentType = getContentType(file);
        boolean canGzip = canGzip(file);
        String lastModified = new Date(file.lastModified()).toString();

        Headers headers = http.getResponseHeaders();
        headers.add("Content-Type", contentType + "; charset=utf-8");
        if (canGzip) {
            headers.add("Content-Encoding", "gzip");
        } else {
            headers.add("Accept-Ranges", "bytes");
            headers.add("Content-Length", file.length() + "");
        }
        headers.add("Last-modified", lastModified);
        headers.add("Expires", Integer.MAX_VALUE + "");
        headers.add("Cache-Control", "public");
        headers.add("Server", "SunHttpServer");
        String eTag = file.getName() + "_" + file.length() + "_" + file.lastModified();
        headers.add("ETag", eTag);

        http.sendResponseHeaders(200, 0);

        OutputStream os = http.getResponseBody();
        if (canGzip) {
            os = new GZIPOutputStream(os);
        }
        FileChannel inFC = new RandomAccessFile(file.getAbsoluteFile(), "r").getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int bytesRead = inFC.read(buffer);
        while (bytesRead != -1) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                os.write(buffer.get());
            }
            buffer.clear();
            bytesRead = inFC.read(buffer);
        }
        inFC.close();

        os.close();
        http.close();
    }

    private void fileDownloadHandler(HttpExchange http, File file) throws IOException {
        long fileSize = file.length();
        long pos = 0;
        long end = 0;
        if (http.getRequestHeaders().containsKey("Range")) {
            http.sendResponseHeaders(206, 0);
            pos = Long.parseLong(http.getRequestHeaders().getFirst("Range").replaceAll("bytes=", "").replaceAll("-", ""));
        } else {
            http.sendResponseHeaders(206, 0);
        }
        Headers headers = http.getResponseHeaders();

//        String contentRange = new StringBuffer("bytes ").append(new Long(pos).toString()).append("-").append(new Long(fileSize - 1).toString()).append("/").append(new Long(fileSize).toString()).toString();
        String range = http.getRequestHeaders().getFirst("range");
        if (range != null) {
            //剖解range
            range = range.split("=")[1];
            String[] rs = range.split("-");
            if (numPattern.matcher(rs[0]).matches()) {
                pos = Integer.parseInt(rs[0]);
            }
            if (rs.length > 1 && (numPattern.matcher(rs[1]).matches())) {
                end = Integer.parseInt(rs[1]);
            }
        }
        headers.add("Accept-Ranges", "bytes");
        headers.add("Content-Range", "bytes " + pos + "-" + end + "/" + fileSize);
        headers.add("Content-Length", (end - pos + 1) + "");
        headers.add("Content-Disposition", "attachment;filename=" + file.getName());

        final OutputStream os = http.getResponseBody();
        final FileInputStream fis = new FileInputStream(file);
        fis.skip(pos);
        FileChannel fileChannel = fis.getChannel();
        fileChannel.position(pos);
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int data = fileChannel.read(buffer);
        while (data != -1) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                os.write(buffer.array());
            }
            buffer.clear();
            data = fileChannel.read(buffer);
        }

        fileChannel.close();
        os.close();
    }

    private void sendError(HttpExchange http, int code) throws IOException {
        http.sendResponseHeaders(code, 0);
        http.close();
    }

    private String getContentType(File file) {
        return new MimetypesFileTypeMap().getContentType(file);
    }

    private boolean canGzip(File file) {
        boolean flag = true;
        String filePrefix = file.getName();
        filePrefix = filePrefix.substring(filePrefix.lastIndexOf("."), filePrefix.length()).toLowerCase();
        switch (filePrefix) {
            case ".mp4":
                flag = false;
                break;
            case ".mp3":
                flag = false;
                break;
            case ".webp":
                flag = false;
                break;
        }
        return flag;
    }
}
