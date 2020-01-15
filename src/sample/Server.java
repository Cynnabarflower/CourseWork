package http;

import com.sun.net.httpserver.*;
import sample.StaticHandler;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Server {


    public Server(String[] args) {
        if (args.length < 1 || args[0].equals("-help") || args[0].equals("--help")) {
            System.out.println("Usage: java -jar HttpServer.jar $webroot [$port]");
            return;
        }
        try {
            HttpServer httpServer = HttpServer.create();
            httpServer.createContext("/", new StaticHandler(args[0], false, false));
            int port = args.length > 1 ? Integer.parseInt(args[1]) : 8000;
            httpServer.bind(new InetSocketAddress("localhost", port), 100);
            httpServer.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
