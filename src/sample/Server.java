package http;

import com.sun.net.httpserver.*;
import sample.StaticHandler;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Server {


    public Server(String index, int port) {

        try {
            HttpServer httpServer = HttpServer.create();
            httpServer.createContext("/", new StaticHandler(index, false, false));
            httpServer.bind(new InetSocketAddress("localhost", port), 100);
            httpServer.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
