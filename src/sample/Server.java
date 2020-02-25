package sample;

import com.sun.net.httpserver.*;
import javafx.application.Application;
import sample.JavaFXBrowser;
import sample.StaticHandler;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Server {


    public Server(String index, int port) {

        try {
            HttpServer httpServer = HttpServer.create();
            httpServer.createContext("/", new StaticHandler(index, false, false));
            httpServer.bind(new InetSocketAddress(port), 100);
            httpServer.start();



        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
