package Syntac.utils.recursion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    public List<Token> tokens;
    int currentTokenIndex;

    public Lexer(String filename) throws IOException {
        tokens = new ArrayList<>();
        currentTokenIndex = 0;
        loadTokens(filename);
    }

    private void loadTokens(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\s+");
            if (parts.length == 2) {
                tokens.add(new Token(parts[0], parts[1]));
            }
        }
        reader.close();
    }

    public Token nextToken() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex++);
        }
        return null; // End of token stream
    }

    public void rewind() {
        if (currentTokenIndex > 0) {
            currentTokenIndex--;
        }
    }
}


