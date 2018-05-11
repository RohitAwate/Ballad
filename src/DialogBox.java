import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DialogBox implements EventHandler<ActionEvent> {
    private int option;
    private Stage OptionStage, AlertStage;
    private Scene OptionScene, AlertScene;
    private Button Yes;
    private Button No;
    private Button Cancel;
    private Label messageToDisplay;

    protected void showOptionBox(String title, String message){
        OptionStage = new Stage();
        OptionStage.setTitle(title);
        OptionStage.initModality(Modality.APPLICATION_MODAL);

        messageToDisplay = new Label(message);
        Yes = new Button("Yes");
        Yes.setOnAction(this);
        Yes.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                option = 1;
                OptionStage.close();
            }
        });

        No = new Button("No");
        No.setOnAction(this);
        No.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                option = -1;
                OptionStage.close();
            }
        });

        Cancel = new Button("Cancel");
        Cancel.setOnAction(this);
        Cancel.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                option = 0;
                OptionStage.close();
            }
        });

        HBox HB = new HBox(20);
        HB.getChildren().addAll(Yes, No, Cancel);
        HB.setAlignment(Pos.CENTER);

        VBox VB = new VBox(15);
        VB.getChildren().addAll(messageToDisplay, HB);
        VB.setAlignment(Pos.CENTER);
        VB.setId("OptionBox");

        OptionScene = new Scene(VB, 500, 85);
        setOptionTheme(UserInterface.CurrentTheme);
        OptionStage.setScene(OptionScene);
        OptionStage.setResizable(false);
        OptionStage.getIcons().add(new Image(getClass().getResource("Assets/Logo.png").toExternalForm()));
        OptionStage.showAndWait();
    }

    protected void showAlertBox(String title, String message){
        AlertStage = new Stage();
        AlertStage.setTitle(title);
        AlertStage.initModality(Modality.APPLICATION_MODAL);

        messageToDisplay = new Label(message);
        Button OK = new Button("OK");
        OK.setOnAction(e -> AlertStage.close());

        VBox VB = new VBox(30);
        VB.getChildren().addAll(messageToDisplay, OK);
        messageToDisplay.setAlignment(Pos.CENTER);
        OK.setAlignment(Pos.CENTER);
        VB.setAlignment(Pos.CENTER);
        VB.setId("AlertBox");

        AlertScene = new Scene(VB, 500, 100);
        setAlertTheme(UserInterface.CurrentTheme);
        AlertStage.setScene(AlertScene);
        AlertStage.setResizable(false);
        AlertStage.getIcons().add(new Image(getClass().getResource("Assets/Logo.png").toExternalForm()));
        AlertStage.showAndWait();
    }

    public int getOption() {
        return option;
    }

    @Override
    public void handle(ActionEvent event){
        if(event.getSource() == Yes){
            option = 1;
            OptionStage.close();
        }

        if(event.getSource() == No){
            option = -1;
            OptionStage.close();
        }

        if(event.getSource() == Cancel){
            option = 0;
            OptionStage.close();
        }
    }

    private void setOptionTheme(String selectedTheme) {
        if(OptionScene.getStylesheets() == null)
            OptionScene.getStylesheets().add("Themes/" + selectedTheme);

        else
            OptionScene.getStylesheets().add("Themes/" + selectedTheme);
    }

    private void setAlertTheme(String selectedTheme) {
        if(AlertScene.getStylesheets() == null)
            AlertScene.getStylesheets().add("Themes/" + selectedTheme);

        else
            AlertScene.getStylesheets().add("Themes/" + selectedTheme);
    }
}
