package java_http.client;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum UserCommand {
    get_active, new_connection, remove_connection, exit, http_request;
}

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
                    handleHttpRequest(userCommand);
            }
        }
    }

    public void printHelp() {
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

    private UserCommand parseCommand(String userCommand) {
        if (userCommand.startsWith("get-active")) return UserCommand.get_active;
        if (userCommand.startsWith("new")) return UserCommand.new_connection;
        if (userCommand.startsWith("remove")) return UserCommand.remove_connection;
        if (userCommand.startsWith("exit")) return UserCommand.exit;
        return UserCommand.http_request;
    }
    
    private void createNewConnection(String userCommand) {
        Matcher matcher = bracketPattern.matcher(userCommand);
        String id = null;
        String host = null;
        String port = null;
        
        if (matcher.find()) id = matcher.group(1);
        if (matcher.find()) host = matcher.group(1);
        if (matcher.find()) port = matcher.group(1);
        
        if (id == null || host == null || port == null) {
            System.out.format("The command \"%s\" could not be parsed.\n", userCommand);
        }
        Client newClient = new Client(host, Integer.parseInt(port), id);
        clientIdMap.put(id, newClient);
    }

    private void removeConnection(String userCommand) {
        Matcher matcher = bracketPattern.matcher(userCommand);
        if (matcher.find()) {
            clientIdMap.get(matcher.group(1)).closeSocketLocally();
            if (clientIdMap.get(matcher.group(1)).getClientSocket().isClosed()) {
                System.out.format("Successfully closed socket!\n");
                clientIdMap.remove(matcher.group(1));
            } else {
                System.out.format("Socket failed to close.\n");
            }
        }
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
                for (String id : HostNameIdMap.get(hostName)) {
                    System.out.format("%s\n", id);
                }
            }
        } else {
            System.out.format("Invalid Command.\n");
        }
    }

    private void handleHttpRequest(String userCommand) {
        Matcher matcher = bracketPattern.matcher(userCommand);
        String id = null;

        if (matcher.find()) id = matcher.group(1);
        userCommand = userCommand.substring(matcher.end(1));
        System.out.format("\nuser command: %s\n", userCommand);

        clientIdMap.get(id).writeHttpRequest(userCommand);
    }
}