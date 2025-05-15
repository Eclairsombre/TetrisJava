package Tetris.model;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileWriterAndReader {
    private final String path;

    public FileWriterAndReader(String path) {
        this.path = path;
    }

    public void writeToFile(List<String> content) {
        try {
            FileWriter file = new FileWriter(this.path);
            for (String line : content) {
                file.write(line + "\n");
            }
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] readFromFile() {
        try {
            FileReader fileReader = new FileReader(this.path);
            StringBuilder stringBuilder = new StringBuilder();
            int character;
            while ((character = fileReader.read()) != -1) {
                stringBuilder.append((char) character);
            }
            fileReader.close();
            return stringBuilder.toString().split("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

