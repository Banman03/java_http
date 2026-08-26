package java_http;

public class App {    
    public static void main(String[] args) {
        Initialize init = new Initialize();
        NetworkScan ns = new NetworkScan();

        switch(init.getRole()) {
            case Role.SERVER ->
                System.out.println("You are a server.");
            case Role.CLIENT ->
                System.out.println("You are a client.");
            default -> {
                System.err.println("Unknown role. Exiting");
                return;
            }
        }


    }

}