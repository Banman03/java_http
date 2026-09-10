package java_http.http.httpUtils;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HttpHeader {
    private final HashMap<String, String> kvp;
    private String kvpString;

    public HttpHeader(HashMap<String, String> header) {
        kvp = header;
        StringBuilder sb = new StringBuilder();
        for (HashMap.Entry<String, String> entry : kvp.entrySet()) {
            sb.append(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
        sb.append("\r\n\r\n");
        kvpString = sb.toString();
    }

    public HashMap<String, String> getHttpKVP() {
        return kvp;
    }

    public String getHttpKVPAsString() {
        return kvpString;
    }

    public byte[] getHttpKVPAsByte() {
        return kvpString.getBytes(StandardCharsets.US_ASCII);
    }
}