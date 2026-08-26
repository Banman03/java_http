package java_http;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
public class NetworkScan {
    private ArrayList<NetworkInterface> networks;
    private ArrayList<ArrayList<InetAddress>> networkAddresses;
    private String networkDisplayName;

    public NetworkScan() {
        System.out.println("Would you like to perform a network scan? (y / n)");
        Scanner sc = new Scanner(System.in);

        networkAddresses = new ArrayList<>();

        if (sc.hasNext()) {
            String response = sc.nextLine();
            if (response.equals(Responses.YES)) {
                runScan();
            } else if (response.equals(Responses.NO)) {
                System.out.println("Skipping network scan.");
            } else {
                System.out.println("Unknown response.");
            }
        }
    }

    private void runScan() {
        try {
            networks = Collections.list(NetworkInterface.getNetworkInterfaces());
            System.out.format("There are %d networks detected.\n", networks.size());

            for (NetworkInterface network : networks) {
                StringBuilder sb = new StringBuilder();
                sb.append("Display Name: " + network.getDisplayName() + "\n");
                sb.append("\t\tIP Addresses:\n");
                networkAddresses.add(Collections.list(network.getInetAddresses()));
                for (InetAddress address : networkAddresses.get(networkAddresses.size() - 1)) {
                    sb.append("\t\t" + address.getHostAddress() + "\n");
                }
                System.out.println(sb);
            }
            
        } catch (SocketException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public ArrayList<ArrayList<InetAddress>> getAddresses() {
        return networkAddresses;
    }

    public String getNetworkDisplayName() {
        return networkDisplayName;
    }


}