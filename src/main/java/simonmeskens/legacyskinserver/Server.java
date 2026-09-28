package simonmeskens.legacyskinserver;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import net.freeutils.httpserver.HTTPServer;
import net.freeutils.httpserver.HTTPServer.ContextHandler;
import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;
import net.freeutils.httpserver.HTTPServer.VirtualHost;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;

public class Server implements ContextHandler {
    private String type = "skin";

    public static void main(String[] args) throws IOException {
        int port = 5444;

        try {
            int newPort = Integer.parseInt(args[0]);
            if (newPort > 1024) {
                port = newPort;
            }
        } catch (Exception e) {
        }

        HTTPServer server = new HTTPServer(port);

        VirtualHost host = server.getVirtualHost(null);
        host.addContext("/MinecraftSkins", new Server("skin"));
        host.addContext("/MinecraftCloaks", new Server("cape"));

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGui();
                System.out.println("\u6b63\u5728\u542f\u52a8\u76ae\u80a4\u670d\u52a1\u5668...");
            }
        });

        server.start();
    }

    private static void createAndShowGui() {
        JFrame frame = new JFrame("\u63a7\u5236\u53f0");
        JTextArea textArea = new JTextArea(15, 30);
        textArea.setFont(new java.awt.Font("Microsoft YaHei", java.awt.Font.PLAIN, 12));

        TextAreaOutputStream taOutputStream = new TextAreaOutputStream(textArea);
        System.setOut(new PrintStream(taOutputStream));

        JScrollPane scrollPane = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(scrollPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private HashMap<String, ByteArrayOutputStream> textures = new HashMap<String, ByteArrayOutputStream>();

    public Server() {
    }

    public Server(String t) {
        type = t;
    }

    public int serve(Request req, Response resp) throws IOException {
        String userName = new File(req.getPath(), "").getName().replaceFirst("[.][^.]+$", "");
        String typeName = type.equals("cape") ? "\u62ab\u98ce" : "\u76ae\u80a4";
        try {
            InputStream stream = getSkin(userName);
            resp.sendHeaders(200, stream.available(),
                    System.currentTimeMillis(), null, "image/png", null);
            resp.sendBody(stream, stream.available(), null);
            System.out.println("\u5df2\u4e3a\u60a8\u63d0\u4f9b" + typeName);
        } catch (Exception e) {
            resp.send(500, "Failed serving " + req.getPath() + ": " + e.getMessage());
            System.out.println("\u63d0\u4f9b" + typeName + "\u5931\u8d25: " + e.getMessage());
        }

        return 0;
    }

    public InputStream getSkin(String userName) throws Exception {
        String key = type + ":" + userName;
        String typeName = type.equals("cape") ? "\u62ab\u98ce" : "\u76ae\u80a4";
        if (!textures.containsKey(key)) {
            System.out.println("\u6b63\u5728\u4ece LittleSkin \u83b7\u53d6\u60a8\u7684" + typeName + "...");
            textures.put(key, fetchTexture(userName));
        }
        return new ByteArrayInputStream(textures.get(key).toByteArray());
    }

    private boolean isUuid(String s) {
        return s.matches("[0-9a-fA-F]{32}");
    }

    public ByteArrayOutputStream fetchTexture(String userName) throws Exception {
        String textureUrl = null;

        if (isUuid(userName)) {
            String apiUrl = "https://littleskin.cn/api/yggdrasil/sessionserver/session/minecraft/profile/" + userName;

            HttpURLConnection apiConn = (HttpURLConnection) new URL(apiUrl).openConnection();
            apiConn.setRequestMethod("GET");
            apiConn.setRequestProperty("User-Agent", "Mozilla/5.0");
            apiConn.connect();

            if (apiConn.getResponseCode() != 200) {
                throw new Exception("LittleSkin API returned " + apiConn.getResponseCode());
            }

            JsonObject profile = JsonParser.object().from(apiConn.getInputStream());
            apiConn.disconnect();

            String encoded = profile.getArray("properties").getObject(0).getString("value");
            String decoded = new String(Base64.getDecoder().decode(encoded));
            JsonObject texturesObj = JsonParser.object().from(decoded).getObject("textures");

            if (type.equals("cape")) {
                JsonObject capeObj = texturesObj.getObject("CAPE");
                if (capeObj != null) {
                    textureUrl = capeObj.getString("url");
                }
            } else {
                JsonObject skinObj = texturesObj.getObject("SKIN");
                if (skinObj != null) {
                    textureUrl = skinObj.getString("url");
                }
            }
        } else {
            String path = type.equals("cape") ? "/cape/" : "/skin/";
            textureUrl = "https://littleskin.cn" + path + userName + ".png";
        }

        if (textureUrl == null) {
            throw new Exception("\u672a\u627e\u5230" + (type.equals("cape") ? "\u62ab\u98ce" : "\u76ae\u80a4"));
        }

        HttpURLConnection conn = (HttpURLConnection) new URL(textureUrl).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.connect();

        if (conn.getResponseCode() != 200) {
            throw new Exception("LittleSkin returned " + conn.getResponseCode());
        }

        InputStream in = conn.getInputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = in.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        conn.disconnect();
        return buffer;
    }
}