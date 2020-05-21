import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/** The Server component responsible for the game logic of the hangman game. Listens on a Socket for a Client
 *  to connect. Once connected the Server chooses a random word from the wordList and sends the Client hints. Each
 *  time the client sends a guess the Server determines if the game needs to continue or if the hint needs to be
 *  updated. Once complete the Server sends a game score and closes the connection. */

public class Server {

    private static String gameWord;
    private static StringBuilder wordHint = new StringBuilder();
    private static int numCharsGuessed = 0;
    private static int numWordsGuessed = 0;
    private static final String LIST_PATH = "wordlist";

    /** initialiseWordList - loads the wordlist found in LIST_PATH populates a List<String> with these words*/
    private static List<String> initialiseWordList() throws IOException {
        String line;
        List<String> wordList = new ArrayList<>();
        try {
            File f = new File(LIST_PATH);
            BufferedReader br = new BufferedReader(new FileReader(f));
            while ((line = br.readLine()) != null) {
                wordList.add(line);
            }
        }
        catch (IOException e){
            System.err.println("Error retrieving loading words from list.");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return wordList;
    }

    /** getWord - randomly selects a word from the list of possible words and returns it */
    private static String getWord() throws IOException {
        List<String> words = initialiseWordList();
        Random r = new Random();
        return words.get(r.nextInt(words.size()));
    }
    /** startGame sets up a game of hangman by selecting a word and initialising the first hint */
    private static void startGame() throws IOException {
        try {
            gameWord = getWord();
        }
        catch (Exception e) {
            System.err.println("Error reading wordlist file.");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        for (int i = 0; i < gameWord.length(); i++)
            wordHint.append('_');
    }
    /** Updates the progress of the word hint. Fills any blank position that contains char c*/
    private static void updateWordProgress(char c) {
        for (int i = 0; i < gameWord.length(); i++) {
            if (gameWord.charAt(i) == c)
                wordHint.setCharAt(i,c);
        }
    }

    public static void main (String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String clientMessage; // stores correspondence from Client
            clientMessage = in.readLine();

            // Check validity of client message
            if (!Security.verifyHash(clientMessage)) {
                System.err.println("Error! Failed hash check. Program exiting");
                System.exit(1);
            }
            // Check that client has sent the START GAME message, otherwise exit
            if (!Objects.equals(Security.decrypt(Security.extractStr(clientMessage)), "START GAME")) {
                System.exit(1);
            }
            startGame();
            // Encrypt, hash and send hint to client
            out.println(Security.combineStrAndHash(Security.encrypt(wordHint.toString())));
            // LOOP TO RECEIVE GUESSES
            while ((clientMessage = in.readLine()) != null) {
                if (!Security.verifyHash(clientMessage)) {
                    System.err.println("Error! Failed hash check. Program exiting");
                    System.exit(1);
                }
                clientMessage = Security.decrypt(Security.extractStr(clientMessage));
                System.out.println("Client sent: " + clientMessage);
                assert clientMessage != null;
                // LETTER GUESS RECEIEVED
                if (clientMessage.length() == 1) {
                    numCharsGuessed++;
                    // Check if letter has been guessed before
                    if (!wordHint.toString().contains(clientMessage)) {
                        updateWordProgress(clientMessage.charAt(0));
                        if (gameWord.equals(wordHint.toString())) // all letters correctly guessed
                            break;
                    }
                }
                // WORD GUESS RECEIVED
                else
                    numWordsGuessed++;
                if (clientMessage.equals(gameWord))
                    break;
                out.println(Security.combineStrAndHash(Security.encrypt(wordHint.toString())));
            }
            // Calculate score
            int score = 10 * gameWord.length() - 2 * numCharsGuessed - numWordsGuessed;
            out.println(Security.combineStrAndHash(Security.encrypt(Integer.toString(score))));
            out.println(Security.combineStrAndHash(Security.encrypt("GAME OVER")));

        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
