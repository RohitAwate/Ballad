import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AboutScreen {
    private Scene AboutScene;

    protected void display() {
        Stage aboutStage = new Stage();
        aboutStage.initModality(Modality.APPLICATION_MODAL);
        aboutStage.setTitle("About Ballad");

        Image logo = new Image(getClass().getResource("Assets/AboutLogo.png").toExternalForm());
        Text about = new Text("Built on JavaFX. Powered by RichTextFX.\n<> with ♥ by Rohit Awate.");
        about.setTextAlignment(TextAlignment.CENTER);
        about.setId("AboutText");

        VBox VB = new VBox(20);
        VB.getChildren().addAll(new ImageView(logo), about);
        VB.setId("AboutScreen");
        VB.setAlignment(Pos.CENTER);

        AboutScene = new Scene(VB, 450, 300);
        setTheme(UserInterface.CurrentTheme);
        aboutStage.setResizable(false);
        aboutStage.setScene(AboutScene);
        aboutStage.getIcons().add(new Image(getClass().getResource("Assets/Logo.png").toExternalForm()));
        aboutStage.showAndWait();
    }

    private void setTheme(String selectedTheme) {
        if(AboutScene.getStylesheets() == null)
            AboutScene.getStylesheets().add("Themes/" + selectedTheme);
        else {
            AboutScene.getStylesheets().clear();
            AboutScene.getStylesheets().add("Themes/" + selectedTheme);
        }
    }
}
