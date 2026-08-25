package java_http;
import java.io.EOFException;
import java.util.NoSuchElementException;
import java.util.Scanner;

enum Role {
    SERVER, CLIENT, UNKNOWN;
}

public class Initialize {
    private Role role;
    
    public Initialize() {
        System.out.println("Welcome to http_java");
        Scanner sc = new Scanner(System.in);

        getRole(sc);
    }

    public Role getRole() {
        return this.role;
    }
    
    private void getRole(Scanner sc) throws NoSuchElementException, IllegalStateException {
        System.out.println("\nWill you be playing the role of the client or server today? (c/s)");
        try {
            if (sc.hasNext()) {
                String response = sc.nextLine();
                this.role = switch (response) {
                    case "c" -> {
                        yield Role.CLIENT;
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
                    getRole(sc);
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
