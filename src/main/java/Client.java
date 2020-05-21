import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

/** The Client component of the hangman game is used as a means to interface with a Server and play a game of hangman.
 *  It starts by initiating a connection with the Server. Once started the Server will choose a word and send a hint.
 *  The Client takes user input in the form of letter or word guesses which it sends to the Server. Each time a guess
 *  is made the Server responds with a new hint or by telling the Client they have correctly guessed the word */
public class Client {

    public static void main (String[] args) throws IOException {
        // Command-line arg verification
        if (args.length != 2) {
            System.err.println("Usage: java Client <hostName> <portNumber>");
            System.exit(1);
        }
        String hostName = args[0];
        int portNumber = 0;

        try {
            portNumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid input. Port number must be an integer between 1024 and 49151");
        }

        try (
                Socket socket = new Socket(hostName, portNumber); // try connect to Server
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String userInput;
            String serverResponseString;

            // indicate to server to start game
            out.println(Security.combineStrAndHash(Security.encrypt("START GAME")));
            // loop to read in messages from server during game
            while ((serverResponseString = in.readLine()) != null) {
                // Verify integrity via hash
                if (!Security.verifyHash(serverResponseString)) {
                    System.err.println("Error! Failed hash check. Program exiting");
                    System.exit(1);
                }
                // Hash passed, extract message and decrypt
                serverResponseString = Security.decrypt(Security.extractStr(serverResponseString));

                assert serverResponseString != null;
                // Check for server non-compliance
                if (!serverResponseString.matches("^[+-]?[a-z0-9_]+$")) {
                    System.err.println("Unknown communication received from server. Exiting.");
                    System.exit(1);
                }
                System.out.println(serverResponseString);
                // Check if server has sent score (indicating game over)
                if (serverResponseString.matches("^[+-]?[0-9]+$"))
                    break;
                // Loop until user enters valid input
                while(!(userInput = stdIn.readLine()).matches("^[a-zA-Z]+$"))
                    System.out.println("Guesses may only contain characters in the range a-z");
                out.println(Security.combineStrAndHash(Security.encrypt(userInput)));
            }
            serverResponseString = in.readLine();
            if (!serverResponseString.equals("GAME OVER"))
                System.exit(1);
            System.out.println(serverResponseString);

            // Close readers, writers and socket
            out.close();
            in.close();
            stdIn.close();
            socket.close();
            System.exit(0);

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error initialising SHA algorithm");
            e.printStackTrace();
        }
    }
}