package me.tweirtx.streamlinetv;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class StreamlineServer implements Runnable {
    StreamlineDNS dns;
    static String targetURL;
    HttpServer server;

    public StreamlineServer(String name) throws IOException {
        dns = new StreamlineDNS(name);
        server = HttpServer.create(new InetSocketAddress(3030), 0);
        server.createContext("/navigate", new urlSetter());
    }

    @Override
    public void run() {
        dns.start();
        server.start();
    }

    static class urlSetter implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            byte[] responseBytes = new byte[exchange.getRequestBody().available()];
            exchange.getRequestBody().read(responseBytes);
            String responseData = new String(responseBytes);
            targetURL = responseData.substring(responseData.indexOf("http"), responseData.lastIndexOf('"'));
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
            System.out.println(targetURL);
        }
    }

    public String getTargetURL() {
        return targetURL;
    }
}
