package java_http.parser;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Set;
import java_http.http.HttpRequest;
import java_http.http.httpUtils.HttpMethod;
import java_http.http.httpUtils.HttpVersion;

public class Parser {
    private static final Set<String> HTTP_METHODS = Set.of(
        "GET", "POST", "PUT", "DELETE", "HEAD",
        "get", "post", "put", "delete", "head"
    );

    public static Command parse(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Input command cannot be empty.");
        }

        String normalized = input.replace("\r\n", "\n");
        if (!normalized.startsWith(":")) {
            throw new IllegalArgumentException("Commands must begin with ':'");
        }

        String payload = normalized.substring(1).stripLeading();
        String[] lines = payload.split("\n", -1);
        String firstLine = lines[0].trim();

        String[] firstLineTokens = firstLine.split("\\s+");
        String leadToken = firstLineTokens[0];
        if (leadToken.toLowerCase().equals("exit")) return null;

        if (HTTP_METHODS.contains(leadToken)) {
            return parseHttpCommand(lines);
        } else {
            return parseControlCommand(firstLineTokens);
        }
    }

    private static ControlCommand parseControlCommand(String[] tokens) {
        String action = tokens[0];

        switch (action) {
            case "get-active" -> {
                String host = (tokens.length > 1) ? tokens[1] : null;
                validateHost(host);
                return new ControlCommand(action, null, host, null);
            }
            case "remove" -> {
                if (tokens.length < 2) {
                    throw new IllegalArgumentException("Usage: :remove <id>");
                }
                String id = tokens[1];
                validateId(id);
                return new ControlCommand(action, id, null, null);
            }
            case "add" -> {
                if (tokens.length < 3) {
                    throw new IllegalArgumentException("Usage: :add <id> [host] <port>");
                }
                String id = tokens[1];
                validateId(id);

                String host = null;
                int port;

                if (tokens.length == 3) {
                    port = parseAndValidatePort(tokens[2]);
                } else {
                    host = tokens[2];
                    validateHost(host);
                    port = parseAndValidatePort(tokens[3]);
                }
                return new ControlCommand(action, id, host, port);
            }
            default -> throw new IllegalArgumentException("Unknown control command: " + action);
        }
    }

    private static HttpRequest parseHttpCommand(String[] lines) {
        String[] reqLineTokens = lines[0].trim().split("\\s+");
        if (reqLineTokens.length < 3) {
            throw new IllegalArgumentException("Malformed request line: " + lines[0]);
        }

        HttpMethod method = HttpMethod.parseMethodSafe(reqLineTokens[0].toUpperCase()).get();
        String route = reqLineTokens[1];
        HttpVersion version = new HttpVersion(reqLineTokens[2]);

        if (!route.startsWith("/")) {
            throw new IllegalArgumentException("Route must begin with '/': " + route);
        }

        HashMap<String, String> headers = new HashMap<>();
        int lineIdx = 1;

        while (lineIdx < lines.length && !lines[lineIdx].isBlank()) {
            String line = lines[lineIdx];
            int colonIdx = line.indexOf(':');
            if (colonIdx == -1) {
                throw new IllegalArgumentException("Malformed header format: " + line);
            }
            String key = line.substring(0, colonIdx).trim();
            String value = line.substring(colonIdx + 1).trim();
            validateId(key);
            headers.put(key, value);
            lineIdx++;
        }

        if (lineIdx < lines.length && lines[lineIdx].isBlank()) {
            lineIdx++;
        }

        StringBuilder bodyBuilder = new StringBuilder();
        while (lineIdx < lines.length) {
            bodyBuilder.append(lines[lineIdx]);
            if (lineIdx < lines.length - 1) {
                bodyBuilder.append("\n");
            }
            lineIdx++;
        }

        String body = bodyBuilder.toString().strip();
        return new HttpRequest(method, route, version, headers, body.isEmpty() ? null : body.getBytes(StandardCharsets.US_ASCII));
    }

    private static void validateId(String id) {
        if (id == null || !id.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("Invalid ID format: " + id);
        }
    }

    private static void validateHost(String host) {
        if (host != null && !host.matches("^[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
            throw new IllegalArgumentException("Invalid Host format: " + host);
        }
    }

    private static int parseAndValidatePort(String portStr) {
        try {
            int port = Integer.parseInt(portStr);
            if (port < 2000 || port > 65000) {
                throw new IllegalArgumentException("Port must be between 2000 and 65000. Got: " + port);
            }
            return port;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric port: " + portStr);
        }
    }
}