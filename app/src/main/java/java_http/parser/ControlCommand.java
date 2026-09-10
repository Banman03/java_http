package java_http.parser;

public record ControlCommand (String action, String id, String host, Integer port) implements Command {}