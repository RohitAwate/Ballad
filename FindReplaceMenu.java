import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FindReplaceMenu {
    private Stage FindStage;
    private Scene FindScene;
    private TextField InputField;
    private Button FindButton, Replace;
    private Label FindWhat;
    public static int start, end;

    public FindReplaceMenu(){
        InputField = new TextField();
        FindButton = new Button("Find");
        FindWhat = new Label("Find what: ");
    }

    public void FindMenu(String FileContent){
        FindStage = new Stage();
        FindStage.setResizable(false);
        FindStage.setTitle("Find");
        InputField.setMaxWidth(150);
        FindButton.setAlignment(Pos.CENTER);

        HBox HB = new HBox(10);
        HB.getChildren().addAll(FindWhat, InputField);
        HB.setAlignment(Pos.CENTER);

        VBox VB = new VBox(10);
        VB.getChildren().addAll(HB, FindButton);
        VB.setAlignment(Pos.CENTER);

        FindButton.setOnAction(e ->
        {
            String Key = InputField.getText();
            StringBuilder Builder = new StringBuilder(FileContent);
            start = Builder.indexOf(Key);
            end = start + Key.length();
        });

        FindScene = new Scene(VB, 300, 100);
        FindStage.setScene(FindScene);
        FindStage.show();

    }
}
