import java.util.Scanner;
public class DummyBot {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextLine()) scanner.nextLine(); // Init 1
        if (scanner.hasNextLine()) scanner.nextLine(); // Init 2
        while (scanner.hasNextLine()) {
            for (int i = 0; i < 6; i++) { if (scanner.hasNextLine()) scanner.nextLine(); }
            System.out.println("MOVE NORTH");
        }
    }
}
