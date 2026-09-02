package java_http.http.httpUtils;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HttpHeader {
    private final HashMap<String, String> kvp;
    private String kvpString;

    public HttpHeader(String[] fields) {
        kvp = new HashMap<>();
        for (String pair : fields) {
            String[] tuple = pair.split(": ", 2);
            if (tuple.length != 2) System.out.format("This kvp failed to parse: %s\n", pair);
            kvp.put(tuple[0], tuple[1]);
        }
        StringBuilder sb = new StringBuilder();
        for (HashMap.Entry<String, String> entry : kvp.entrySet()) {
            sb.append(entry.getKey() + ": " + entry.getValue() + ",\n");
        }
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