package sample;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {


    public MyServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client accepted");
                new Thread(new SocketProcessor(socket)).start();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static class SocketProcessor implements Runnable {

        private Socket socket;
        private InputStream is;
        private OutputStream os;

        private SocketProcessor(Socket socket) throws Throwable {
            this.socket = socket;
            this.is = socket.getInputStream();
            this.os = socket.getOutputStream();
        }

        public void run() {
            try {
                readInputHeaders();
                String s = new String(getClass().getResource("/index.html").openStream().readAllBytes());

                writeResponse(s);
               //writeResponse("<html><body><h1>Hello from Habrahabr</h1></body></html>");
            } catch (Throwable t) {
                /*do nothing*/
            } finally {
                try {
                    socket.close();
                } catch (Throwable t) {
                    /*do nothing*/
                    t.printStackTrace();
                }
            }
            System.err.println("Client processing finished");
        }

        private void writeResponse(String s) throws Throwable {
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Server: YarServer/2009-09-09\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + s.length() + "\r\n" +
                    "Connection: close\r\n\r\n";
            String result = response + s;
            os.write(result.getBytes());
            os.flush();
        }

        private void readInputHeaders() throws Throwable {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String s = br.readLine();
                if (s == null || s.trim().length() == 0) {
                    break;
                }
            }
        }
    }
}

