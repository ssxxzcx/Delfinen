import controller.Controller;
import ui.UserInterface;

public class Main {
    public static void main(String[] args) {
        UserInterface ui = new UserInterface(new Controller());
        ui.start();
    }
}