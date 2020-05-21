# Hangman
<p>This game utilises Java's implementation of TCP Sockets to allow a Client to play a game of hangman against a Server.</p>

## Compile Instructions
<p>To compile the Server run <code>javac Server.java</code> from the terminal.</p>
<p>To compile the Client run <code>javac Client.java</code> from the terminal.</p>

## Run Instructions
<p>To run server enter <code>./startServer.sh &lt;port number&gt;</code> where port number is a integer between 1024 and 49151.</p>
<p>To run client enter <code>./clientServer.sh &lt;host name&gt; &lt;port number&gt;</code> where host is the host name of the server and port number is the port number the Server is using, between 1024 and 49151.</p>
<p>Note: in order to play the game the Server must be run first to allow it to listen to a Client's request to
set up a Socket over which the game can be played.</p>
<p>Once the Client and Server are running a Socket is set up for communication and the game begins.</p>

## How To Play
<p>Initially the Server will select a random word from its secret word list and sent the Client a hint consisting of the secret word with each unguessed character replaced by an _ character (since, initially, no letters are guessed, the first hint will be a string of as many _s as there are characters in the secret word).</p>
<p>The client will then send along guesses of either characters or words until it discovers the secret word. After each guess that doesn't reveal the secret word, the server will respond with a hint, consisting of the secret word with each unguessed character replaced by an _. If the guess is a single letter, each instance of that letter in the secret word will be revealed in the next hint. If the guess is a word (i.e. more than one character) that does not match the secret word, no new information is revealed (i.e. the previous hint is returned). Guesses are case insensitive.</p>
<p>Once the client has guessed the secret word, the server sends a score to the player calculated using the formula: 10 * (number of letters in secret word) - 2 * (number of characters guessed) - (number of words guessed), followed by the message GAME OVER.</p>

## Further Configuration
<p>The server selects a random word from the wordlist file provided. This file may be modified to add or remove words. When making changes ensure each new word appears on a new line by itself and that words only contain letters from a-z with no numbers, spaces or other special characters.</p>
