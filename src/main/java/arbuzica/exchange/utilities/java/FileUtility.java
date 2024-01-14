package arbuzica.exchange.utilities.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtility {
    public static File createFile(String name, CharSequence content) {
        File file = new File(name);
        FileWriter fileWriter;

        try {
            fileWriter = new FileWriter(file);

            fileWriter.write(content.toString());
            fileWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }

        return file;
    }
}
