package Tetris.Model.TetrisInstanceComponent;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * This class is used to write and read from a file.
 */
public class FileWriterAndReader {
    private final String path;
    /// path the path to the file

    /**
     * Constructor for the FileWriterAndReader class.
     *
     * @param path the path to the file
     */
    public FileWriterAndReader(String path) {
        this.path = path;
    }

    public void writeToFile(List<String> content) {
        try {
            FileWriter file = new FileWriter(this.path);
            for (int i = 0; i < Math.min(5, content.size()); i++) {
                file.write(content.get(i) + "\n");
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
            System.out.println("Création du fichier score.txt");
            writeToFile(List.of());
            return new String[]{};
        }
    }
}

