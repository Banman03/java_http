package java_http;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java_http.client.*;

enum UserCommand {
    get_active, new_connection, remove_connection, exit, http_request;
}

public class ConnectionManager {
    private HashMap<UUID, Client> clientIdMap;
    private HashMap<String, ArrayList<UUID>> HostNameIdMap;
    private static final String baseString = ">> ";
    private static final Pattern bracketPattern = Pattern.compile("\\[([^\\]]*)\\]");

    public ConnectionManager() {}

    public void start() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the java http connection manager.\n");
        printHelp();
        
        while (true) {
            System.out.println(baseString);
            String userCommand = sc.nextLine();

            UserCommand command = parseCommand(userCommand);
            switch (command) {
                case get_active:
                    listActiveConnections(userCommand);
                    break;
                case new_connection:
                    createNewConnection(userCommand);
                    break;
                case remove_connection:
                    removeConnection(userCommand);;
                    break;
                case exit:
                    System.out.format("Goodbye!\n");
                    return;
                case http_request:
                    return;
            }
        }
    }

    public void printHelp() {
        System.out.format("Informational Commands:\n");
        System.out.format("\tget-active [hostname] (leave empty to get all active connections)\tReturns list of active connections\n");

        System.out.format("Connection Manager Commands:\n");
        System.out.format("\tnew [hostname] (leave blank to connect to loopback) [port number]\tReturns ID that can be used to delete connections\n");
        System.out.format("\remove [ID]\tReturns void\n");

        System.out.format("Connection Specific Commands:\n");
        System.out.format("\t[HTTP Request Line] *enter*\n");
        System.out.format("\t[HTTP Headers] *0x0000*\n");
        System.out.format("\t[Body] *0x0000*\n");
    }

    private UserCommand parseCommand(String userCommand) {
        if (userCommand.startsWith("get-active")) return UserCommand.get_active;
        if (userCommand.startsWith("new")) return UserCommand.new_connection;
        if (userCommand.startsWith("remove")) return UserCommand.remove_connection;
        if (userCommand.startsWith("exit")) return UserCommand.exit;
        return UserCommand.http_request;
    }
    
    private void createNewConnection(String userCommand) {
        Matcher matcher = bracketPattern.matcher(userCommand);
        String host = null;
        String port = null;
        
        while (matcher.find()) {
            if (host == null) host = matcher.group(1);
            else port = matcher.group(1);
        }
        
        if (host == null || port == null) {
            System.out.format("The command \"%s\" could not be parsed.\n", userCommand);
        }
        UUID newId = UUID.randomUUID();
        Client newClient = new Client(host, Integer.parseInt(port), newId);
        clientIdMap.put(newId, newClient);
    }

    private void removeConnection(String userCommand) {

    }

    private void listActiveConnections(String userCommand) {
        Matcher matcher = bracketPattern.matcher(userCommand);
        if (matcher.find()) {
            String hostName = matcher.group(1);
            if (hostName.isBlank()) {
                System.out.format("All connections:\n\n");
                for (HashMap.Entry<String, Client> entry : clientIdMap.entrySet()) {
                    System.out.format("%s : %s\n", entry.getKey(), entry.getValue().getHostName());
                }
            } else {
                System.out.format("All connections to %s:\n\n", hostName);
                for (String entry : HostNameIdMap.get(hostName)) {
                    System.out.format("%s\n", entry);
                }
            }
        } else {
            System.out.format("Invalid Command.\n");
        }
    }
}