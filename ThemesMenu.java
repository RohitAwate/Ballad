import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ThemesMenu {
    private Stage mainWindow;
    private Scene ThemesScene;
    private final Label Instruction = new Label("Choose a theme:");
    private Button OK, Cancel;
    private ComboBox<String> ThemeBox;
    public static String SelectedTheme;

    public void display(String CurrentTheme){
        mainWindow = new Stage();
        mainWindow.initModality(Modality.APPLICATION_MODAL);
        mainWindow.setTitle("Select theme...");

        SelectedTheme = CurrentTheme;       // Remembers current theme in case user selects null or clicks 'Cancel'.
        ThemeBox = new ComboBox<>();
        ThemeBox.setPromptText(new StringBuffer(CurrentTheme).substring(0, CurrentTheme.length()-4));   // Removes the ".css" extension.
        ThemeBox.setId("ThemesBox");

        ThemeBox.getItems().clear();
        ThemeBox.getItems().addAll("Adreana", "Oasis");
        OK = new Button("OK");
        OK.setOnAction(e ->
        {
            if(ThemeBox.getValue() == null)     // In case user chooses nothing from ComboBox.
                mainWindow.close();
            else {
                SelectedTheme = ThemeBox.getValue() + ".css";
                mainWindow.close();
            }
        });

        Cancel = new Button("Cancel");
        Cancel.setOnAction(e -> mainWindow.close());

        HBox ThemeLabelAndSelector = new HBox(20);
        ThemeLabelAndSelector.getChildren().addAll(Instruction, ThemeBox);
        ThemeLabelAndSelector.setAlignment(Pos.CENTER);

        HBox ButtonHolder = new HBox(20);
        ButtonHolder.getChildren().addAll(OK, Cancel);
        ButtonHolder.setAlignment(Pos.CENTER);

        VBox VB = new VBox(20);
        VB.getChildren().addAll(ThemeLabelAndSelector, ButtonHolder);
        VB.setAlignment(Pos.CENTER);
        VB.setId("ThemesMenu");

        ThemesScene = new Scene(VB, 400, 120);
        mainWindow.setScene(ThemesScene);
        setTheme(UserInterface.CurrentTheme);
        mainWindow.getIcons().add(new Image(getClass().getResource("Assets/Logo.png").toExternalForm()));
        mainWindow.setResizable(false);
        mainWindow.showAndWait();
    }

    private void setTheme(String selectedTheme) {
        ThemesScene.getStylesheets().clear();
        ThemesScene.getStylesheets().add("Themes/" + selectedTheme);
    }
}
