package java_http.client;

import java.net.http.HttpRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java_http.parser.*;

public class ConnectionManager {
    private HashMap<String, Client> clientIdMap;
    private HashMap<String, ArrayList<String>> HostNameIdMap;
    private static final String baseString = ">> ";
    private static final Pattern bracketPattern = Pattern.compile("\\[([^\\]]*)\\]");

    public ConnectionManager() {
        clientIdMap = new HashMap<>();
    }

    public void start() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the java http connection manager.\n");
        printHelp();
        
        while (true) {
            System.out.println(baseString);
            sc.useDelimiter("\n\n\n");
            String userCommand = sc.nextLine();

            Command command = Parser.parse(userCommand);
            
            switch (command) {
                case ControlCommand cc -> {
                    handleControlCommand(cc);
                }
                case HttpRequest hr -> {
                    handleHttpRequest(hr);
                }
                case null -> {
                    System.out.println("Goodbye!\n");
                    break;
                }
                default -> {
                    System.out.format("%s is an unknown command. Please input correctly.\n", userCommand);
                }
            }
        }
    }

    private void printHelp() {
        System.out.format("Informational Commands:\n");
        System.out.format("\tget-active [hostname] (leave empty to get all active connections)\tReturns list of active connections\n\n");

        System.out.format("Connection Manager Commands:\n");
        System.out.format("\tnew [ID] [hostname] (leave blank to connect to loopback) [port number]\tReturns ID that can be used to delete connections\n");
        System.out.format("\tremove [ID]\tReturns successfull or not\n\n");

        System.out.format("Connection Specific Commands:\n");
        System.out.format("\t[ID] HTTP-Request Route HTTP-Version*enter*\n"); // Slightly different syntax to standard http request header, but that's because I am routing the client requests
        System.out.format("\tHTTP Headers *0x0000*\n");
        System.out.format("\tBody *0x0000*\n");
    }

    private void handleControlCommand(ControlCommand command) {
        switch (command.action().toLowerCase()) {
            case "get-active" -> {
                listActiveConnections(command.host());
            }
            case "remove" -> {
                removeConnection(command.id());
            }
            case "new" -> {
                createNewConnection(command.id(), command.host(), command.port());
            }
        }
    }
    
    private void createNewConnection(String id, String host, Integer port) {
        if (host.isBlank()) host = "loopback";
        if (id == null || host == null || port == null) {
            System.out.format("The command with id: %s, host: %s, and port: %d could not be parsed.\n", id, host, port);
        }
        Client newClient = new Client(host, port, id);
        clientIdMap.put(id, newClient);
    }

    private void removeConnection(String id) {
        clientIdMap.get(id).closeSocketLocally();
        if (clientIdMap.get(id).getClientSocket().isClosed()) {
            System.out.format("Successfully closed socket!\n");
            clientIdMap.remove(id);
        } else {
            System.out.format("Socket failed to close.\n");
        }
    }

    private void listActiveConnections(String host) {
        if (host.isBlank()) {
            System.out.format("All connections:\n\n");
            for (HashMap.Entry<String, Client> entry : clientIdMap.entrySet()) {
                System.out.format("%s : %s\n", entry.getKey(), entry.getValue().getHostName());
            }
        } else {
            System.out.format("All connections to %s:\n\n", host);
            for (String id : HostNameIdMap.get(host)) {
                System.out.format("%s\n", id);
            }
        }
    }

    private void handleHttpRequest(String id, HttpRequest request) {
        clientIdMap.get(id).writeHttpRequest(request);
    }
}