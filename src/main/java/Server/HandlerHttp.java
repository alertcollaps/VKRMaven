package Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class HandlerHttp implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("handle start");
        switch (exchange.getHttpContext().getPath()){
            case "/":
                handleRoot(exchange);
                break;
        }
    }
    public void handleRoot(HttpExchange exchange){
        if ("POST".equals(exchange.getRequestMethod())){
            System.out.println("Post method");
            String bodyMessage = ReadBody(exchange);
            System.out.println("Response: " + bodyMessage);
            SendResponse(exchange);
        } else if ("GET".equals(exchange.getRequestMethod())){
            SendResponse(exchange);
            System.out.println("This get method");
        }
    }
    public String ReadBody(HttpExchange exchange){
        DataInputStream dis = new DataInputStream(exchange.getRequestBody());
        BufferedReader br = new BufferedReader(new InputStreamReader(dis));
        StringBuilder response = new StringBuilder();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }
    public String GetResponse(HttpExchange exchange){
        return "OK";
    }
    public void SendResponse(HttpExchange exchange){
        OutputStream outputStream = exchange.getResponseBody();
        String response = GetResponse(exchange);
        try {
            exchange.sendResponseHeaders(200, response.length());
            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ParseBody(String response){
        String message = response.toString();
        JSONObject jo = new JSONObject(message);
        int id = jo.getInt("Id");

        switch (id){
            case 1:

                break;
            case 2:

                break;
        }

    }
}
