package java_http;
import java_http.server.*;

import java.net.InetAddress;

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
                serv.listenCommandLineFromBuffer();
            }
            case Role.CLIENT -> {
                System.out.println("You are a client.");
                Client client = new Client();
                client.writeCommandLineToBuffer();
            }
            default -> {
                System.err.println("Unknown role. Exiting");
                return;
            }
        }


    }

}