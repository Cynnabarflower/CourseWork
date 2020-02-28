package sample;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sample.Expressions.Expression;


import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;

public class StaticHandler implements HttpHandler
{
    private static Map<String, Asset> data = new HashMap<>();
    private final boolean caching, gzip;
    private final String pathToRoot;

    public StaticHandler(String pathToRoot, boolean caching, boolean gzip) throws IOException {
        this.caching = caching;
        this.pathToRoot = pathToRoot.endsWith("/") ? pathToRoot : pathToRoot + "/";
        this.gzip = gzip;

        File[] files = new File(pathToRoot).listFiles();
        if (files == null)
            throw new IllegalStateException("Couldn't find webroot: "+pathToRoot);
        for (File f: files)
            processFile("site/", f, gzip);
    }

    private static class Asset {
        public final byte[] data;

        public Asset(byte[] data) {
            this.data = data;
        }

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            Map<String, Object> parameters = new HashMap<String, Object>();
            URI requestedUri = httpExchange.getRequestURI();
            String query = requestedUri.getRawQuery();
            StringBuilder sb = new StringBuilder();
            InputStream ios = httpExchange.getRequestBody();
            int i;
            while ((i = ios.read()) != -1) {
                sb.append((char) i);
            }
            System.out.println("hm: " + URLDecoder.decode(sb.toString(), StandardCharsets.UTF_8));
            if (httpExchange.getRequestMethod().equals("POST")) {
                JSONObject jsonObject = (JSONObject) JSON.parse(URLDecoder.decode(sb.toString(), StandardCharsets.UTF_8));
                //Main.readIt(Integer.parseInt((String) jsonObject.get("id")), (String) jsonObject.get("element"), (String) jsonObject.get("varValues"));
                byte[] bytes = {};
                if (jsonObject.containsKey("id") && jsonObject.containsKey("element") && jsonObject.containsKey("varValues")) {
                    jsonObject = Main.readIt((String) jsonObject.get("id").toString(), (String) jsonObject.get("element").toString(), (String) jsonObject.get("varValues").toString());
                    bytes = jsonObject.toJSONString().getBytes();
                } else if (jsonObject.containsKey("defaultExpressions") && jsonObject.containsKey("settings_id")) {
                    String settingsId = jsonObject.get("settings_id").toString();
                    String defaultExpressions = jsonObject.get("defaultExpressions").toString();
                    double fromX = 0;
                    try {
                        fromX = Double.parseDouble(jsonObject.get("fromX").toString());
                    } catch (NumberFormatException e) {
                    }
                    double toX = 0;
                    try {
                        toX = Double.parseDouble(jsonObject.get("toX").toString());
                    } catch (NumberFormatException e) {
                    }
                    int optimizationLevel = 0;
                    try {
                        optimizationLevel = Integer.parseInt(jsonObject.get("optimization_level").toString());
                    } catch (NumberFormatException e) {
                    }
                    boolean extraOptimization = Boolean.parseBoolean(jsonObject.getString("extra_optimization"));

                    Main.setUserSettings(settingsId, defaultExpressions, fromX, toX, optimizationLevel, extraOptimization);
                    bytes = "Ok".getBytes();
                } else if (jsonObject.containsKey("settings_id")) {
                    String settingsId = jsonObject.get("settings_id").toString();
                    UserSettings userSettings = Main.getUserSettings(settingsId);
                    Map<String, Object> map = new HashMap<>();
                    map.put("defaultExpressions", userSettings.getDefaultExpressionsString());
                    map.put("fromX", userSettings.fromX);
                    map.put("toX", userSettings.toX);
                    bytes = new JSONObject(map).toJSONString().getBytes();
                }
                httpExchange.getResponseHeaders().set("Content-Type", "text/javascript; charset=UTF-8");
                httpExchange.sendResponseHeaders(200, bytes.length);
                httpExchange.getResponseBody().write(bytes);
                httpExchange.getResponseBody().close();
                System.out.println("Sent");
                return;
            } else if (httpExchange.getRequestMethod().equals("GET")) {
                if (requestedUri.toString().equals("/defaultExpressions.txt")) {
                    byte[] bytes = getClass().getResource("/defaultExpressions.txt").openStream().readAllBytes();
                    httpExchange.getResponseHeaders().set("Content-Type", "text/javascript");
                    httpExchange.getRequestHeaders().set("Access-Control-Allow-Origin", "no-cors");
                    httpExchange.sendResponseHeaders(200, bytes.length);
                    httpExchange.getResponseBody().write(bytes);
                    httpExchange.getResponseBody().close();
                    return;
                }
            }
            String path = httpExchange.getRequestURI().getPath();
            try {
                path = path.substring(1);
                path = path.replaceAll("//", "/");
                if (path.length() == 0)
                    path = "site/index.html";
                else path = "site/" + path;

                boolean fromFile = new File(pathToRoot + path).exists();
                InputStream in = fromFile ? new FileInputStream(pathToRoot + path)
                        : ClassLoader.getSystemClassLoader().getResourceAsStream(pathToRoot + path);
                Asset res = caching ? data.get(path) : new Asset(readResource(in, gzip));
                if (gzip)
                    httpExchange.getResponseHeaders().set("Content-Encoding", "gzip");
                if (path.endsWith(".js"))
                    httpExchange.getResponseHeaders().set("Content-Type", "text/javascript");
                else if (path.endsWith(".html")) {
                    httpExchange.getResponseHeaders().set("Content-Type", "text/html");
                }
                else if (path.endsWith(".css"))
                    httpExchange.getResponseHeaders().set("Content-Type", "text/css");
                else if (path.endsWith(".json"))
                    httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                else if (path.endsWith(".svg"))
                    httpExchange.getResponseHeaders().set("Content-Type", "image/svg+xml");
                if (httpExchange.getRequestMethod().equals("HEAD")) {
                    httpExchange.getResponseHeaders().set("Content-Length", "" + res.data.length);
                    httpExchange.sendResponseHeaders(200, -1);
                    return;
                }
                httpExchange.getResponseHeaders().set("Access-Control-Allow-Origin", "no-cors");
                httpExchange.sendResponseHeaders(200, res.data.length);
                httpExchange.getResponseBody().write(res.data);
                httpExchange.getResponseBody().close();
            } catch (NullPointerException t) {
                System.err.println("Error retrieving: " + path);
                httpExchange.getResponseBody().close();
            } catch (Throwable t) {
                System.err.println("Error retrieving: " + path);
                t.printStackTrace();
                httpExchange.getResponseBody().close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processFile(String path, File f, boolean gzip) throws IOException {
        if (!f.isDirectory())
            data.put(path + f.getName(), new Asset(readResource(new FileInputStream(f), gzip)));
        if (f.isDirectory())
            for (File sub: f.listFiles())
                processFile(path + f.getName() + "/", sub, gzip);
    }

    private static byte[] readResource(InputStream in, boolean gzip) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        OutputStream gout = gzip ? new GZIPOutputStream(bout) : new DataOutputStream(bout);
        byte[] tmp = new byte[4096];
        int r;
        while ((r=in.read(tmp)) >= 0)
            gout.write(tmp, 0, r);
        gout.flush();
        gout.close();
        in.close();
        return bout.toByteArray();
    }

    private static List<String> getResources(String directory)
    {
        ClassLoader context = Thread.currentThread().getContextClassLoader();

        List<String> resources = new ArrayList<>();

        ClassLoader cl = StaticHandler.class.getClassLoader();
        if (!(cl instanceof URLClassLoader))
            throw new IllegalStateException();
        URL[] urls = ((URLClassLoader) cl).getURLs();

        int slash = directory.lastIndexOf("/");
        String dir = directory.substring(0, slash + 1);
        for (int i=0; i<urls.length; i++)
        {
            if (!urls[i].toString().endsWith(".jar"))
                continue;
            try
            {
                JarInputStream jarStream = new JarInputStream(urls[i].openStream());
                while (true)
                {
                    ZipEntry entry = jarStream.getNextEntry();
                    if (entry == null)
                        break;
                    if (entry.isDirectory())
                        continue;

                    String name = entry.getName();
                    slash = name.lastIndexOf("/");
                    String thisDir = "";
                    if (slash >= 0)
                        thisDir = name.substring(0, slash + 1);

                    if (!thisDir.startsWith(dir))
                        continue;
                    resources.add(name);
                }

                jarStream.close();
            }
            catch (IOException e) { e.printStackTrace();}
        }
        InputStream stream = context.getResourceAsStream(directory);
        try
        {
            if (stream != null)
            {
                StringBuilder sb = new StringBuilder();
                char[] buffer = new char[1024];
                try (Reader r = new InputStreamReader(stream))
                {
                    while (true)
                    {
                        int length = r.read(buffer);
                        if (length < 0)
                        {
                            break;
                        }
                        sb.append(buffer, 0, length);
                    }
                }

                for (String s : sb.toString().split("\n"))
                {
                    if (s.length() > 0 && context.getResource(directory + s) != null)
                    {
                        resources.add(s);
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return resources;
    }
}