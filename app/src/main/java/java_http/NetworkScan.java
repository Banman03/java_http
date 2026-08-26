package java_http;
import java.net.InetAddress;
import java.util.Scanner;

public class Network {
    private InetAddress address;

    public Network() {
        System.out.println("Would you like to perform a network scan?");
        Scanner sc = new Scanner(System.in);
        if (sc.hasNext()) {
            String response = sc.nextLine();
            if (response.equals(Responses.YES)) {
                
            }
        }
    }
    
    public InetAddress getAddress() {
        return address;
    }


}