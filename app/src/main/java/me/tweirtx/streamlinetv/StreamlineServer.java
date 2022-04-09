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
            //String responseData = new String(exchange.getRequestBody().readAllBytes());
            //targetURL = responseData.substring(responseData.indexOf("http"), responseData.lastIndexOf('"'));
            targetURL = "https://web.tweirtx.me";
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
            System.out.println(exchange.getRequestBody().read());

        }
    }

    public String getTargetURL() {
        return targetURL;
    }
}
