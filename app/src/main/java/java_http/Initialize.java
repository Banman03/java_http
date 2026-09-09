package java_http;
import java.util.NoSuchElementException;
import java.util.Scanner;

enum Role {
    SERVER, CLIENT_MANAGER, UNKNOWN;
}

public class Initialize {
    private Role role;
    
    public Initialize() {
        System.out.println("Welcome to http_java");
        Scanner sc = new Scanner(System.in);

        instantiateRole(sc);
    }

    public Role getRole() {
        return this.role;
    }
    
    private void instantiateRole(Scanner sc) throws NoSuchElementException, IllegalStateException {
        System.out.println("\nWill you be playing the role of the client manager or server today? (cm/s)");
        try {
            if (sc.hasNext()) {
                String response = sc.nextLine();
                System.out.println(response);
                this.role = switch (response) {
                    case "cm" -> {
                        yield Role.CLIENT_MANAGER;
                    }
                    case "s" -> {
                        yield Role.SERVER;
                    }
                    default -> {
                        yield Role.UNKNOWN;
                    }
                };

                if (role == Role.UNKNOWN) {
                    System.out.format("%s is not a valid selection.", response);
                    instantiateRole(sc);
                }
            } else {
                role = Role.UNKNOWN;
            }
        }
        catch (NoSuchElementException | IllegalStateException e) {
            System.err.format("%s", e.getMessage());
        }
    }
}
