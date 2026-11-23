package ui;

import controller.Controller;
import java.util.Scanner;

public class UserInterface {
    private final Controller controller;
    private final Scanner scanner = new Scanner(System.in);

    public UserInterface(Controller controller) {
        this.controller = controller;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== SVØMMEKLUBBEN DELFINEN ===");
            System.out.println("1. Formand");
            System.out.println("2. Kasserer");
            System.out.println("3. Træner");
            System.out.println("0. Afslut");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> System.out.println("Formandens menu kommer senere.");
                case "2" -> System.out.println("Kassererens menu kommer senere.");
                case "3" -> System.out.println("Trænerens menu kommer senere.");
                case "0" -> running = false;
                default -> System.out.println("Ugyldigt valg.");
            }
        }
    }
}