package java_http.parser;

import java_http.http.HttpRequest;

public sealed interface Command permits ControlCommand, HttpRequest {}