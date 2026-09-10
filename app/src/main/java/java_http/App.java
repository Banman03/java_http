package java_http;
import java_http.server.*;
import java_http.client.*;

public class App {    
    public static void main(String[] args) {
        Initialize init = new Initialize();
        NetworkScan ns = new NetworkScan();

        switch(init.getRole()) {
            case Role.SERVER -> {
                System.out.println("You are a server.");
                Server serv = new Server();
                serv.bindLocally();
                serv.acceptLocally();
                serv.httpRequestListener();
            }
            case Role.CLIENT_MANAGER -> {
                System.out.println("You are the client manager.");
                ConnectionManager manager = new ConnectionManager();
                manager.start();
            }
            default -> {
                System.err.println("Unknown role. Exiting");
                return;
            }
        }


    }

}