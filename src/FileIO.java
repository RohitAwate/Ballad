import javafx.stage.FileChooser;
import java.io.*;

public class FileIO {
    protected File OpenFile(){
        FileChooser FC;
        File FileToOpen = null;
        FC = new FileChooser();
        FC.setTitle("Open file...");
        FC.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Source Files", "*.java", "*.py", "*.cpp", "*.c", "*.html", "*.css"),
                new FileChooser.ExtensionFilter("Plain Text Files", "*.txt")
        );
        try {
            FileToOpen = FC.showOpenDialog(null);
        } catch(NullPointerException NPE) {
            new DialogBox().showAlertBox("Error!", "File could not be opened. Try again.");
        }
        return FileToOpen;
    }

    protected void SaveFile(File FileToSave, String ContentToSave){
        BufferedWriter BW;
        try {
            BW = new BufferedWriter(new FileWriter(FileToSave));
            BW.write(ContentToSave);
            BW.close();
        } catch(IOException IOE) {
            new DialogBox().showAlertBox("Error!", "File could not be saved. Try again.");
        } catch(NullPointerException NPE) {}
    }

    protected File SaveFileAs(String ContentToSave){
        FileChooser FC;
        FC = new FileChooser();
        FC.setTitle("Save file as...");
        FC.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Source Files", "*.java", "*.py", "*.cpp", "*.c", "*.html", "*.css"),
                new FileChooser.ExtensionFilter("Plain Text Files", "*.txt", "*.rtf")
        );
        File FileToSave = FC.showSaveDialog(null);
        SaveFile(FileToSave, ContentToSave);
        return FileToSave;
    }

}
