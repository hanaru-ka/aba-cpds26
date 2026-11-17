import java.io.IOException;
import java.util.Scanner;

/** Small console helpers: clear the screen and wait for Enter. Menus can call these too. */
public final class ConsoleUtil {
    private ConsoleUtil() { }

    /**
     * Clears the console. Works in a real terminal (Command Prompt, IntelliJ's Terminal tab).
     * IntelliJ's Run window cannot be cleared by a program, so a blank line is printed there.
     */
    public static void clear() {
        if (!AuthManager.isTerminal(System.console())) {
            System.out.println();
            return;
        }
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException e) {
            System.out.println();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Waits for Enter so the user can read a message before the screen is cleared. */
    public static void pause(Scanner in) {
        System.out.print("Press Enter to continue... ");
        if (in.hasNextLine()) in.nextLine();
    }
}
