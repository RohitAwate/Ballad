import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import java.io.*;
import org.fxmisc.richtext.LineNumberFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.function.IntFunction;

public class UserInterface extends Application {
    private Stage mainWindow;
    private Scene UIScene;
    private CodeArea Editor;
    private SplitPane SP;
    private VirtualizedScrollPane VSP;
    private MenuBar MB;
    private File OpenedFile;
    private HBox StatusBar;
    private Label caretPosition, fileType;
    private boolean isModified, isFileOpen;
    private String FileContent;
    protected static String CurrentTheme;
    private TreeView<String> FileTree;
    private TreeItem<String> OpenedFileRoot;
    private HashMap<String, String> FileAddresses;
    private ArrayList<String> SupportedFileTypes;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainWindow = primaryStage;
        FileTree = new TreeView<>();
        FileAddresses = new HashMap<>();
        SupportedFileTypes = new ArrayList<>();
        SupportedFileTypes.add("java");
        SupportedFileTypes.add("cpp");
        SupportedFileTypes.add("c");
        SupportedFileTypes.add("py");
        SupportedFileTypes.add("html");
        SupportedFileTypes.add("css");
        SupportedFileTypes.add("txt");

        FileTree.getSelectionModel().selectedItemProperty().addListener(
            (v, oldVal, newVal) ->
            {
                FileSaveProcedure();        // Saves current file before opening new.
                File Temp;
                try {
                    Temp = new File(FileAddresses.get(newVal.getValue()));    // Calls ReadOpenFile only if tree item is a file.
                    if(Temp.isFile()) {
                        StringBuffer SB = new StringBuffer(Temp.getName());
                        int dotPosition = SB.lastIndexOf(".");
                        String FileType = SB.substring(dotPosition + 1, SB.length());
                        if(SupportedFileTypes.contains(FileType)) {
                            OpenedFile = Temp;
                            ReadOpenedFile();
                        }
                        else
                            new DialogBox().showAlertBox("Error!", "This file cannot be opened in Ballad.");
                    }
                } catch(NullPointerException NPE){}

            }
        );

        Font.loadFont(getClass().getResource("Fonts/LiberationMono.ttf").toExternalForm(), 17);
        Font.loadFont(getClass().getResource("Fonts/Roboto.ttf").toExternalForm(), 15);
        Font.loadFont(getClass().getResource("Fonts/RobotoBold.ttf").toExternalForm(), 15);
        EditorRefresh();
        setEditor();
        setMenuBar();
        setStatusBar();

        SP = new SplitPane(FileTree, VSP);
        SP.setDividerPositions(0.3);
        SplitPane.setResizableWithParent(FileTree, false);      // Prevents resizing of FileTree with window.

        BorderPane BP = new BorderPane();
        BP.setTop(MB);
        BP.setCenter(SP);
        BP.setBottom(StatusBar);

        UIScene = new Scene(BP, 1280, 720);
        StartupSettingsLoader();
        setTheme(CurrentTheme);
        mainWindow.setScene(UIScene);
        mainWindow.getIcons().add(new Image(getClass().getResource("Assets/Logo.png").toExternalForm()));
        mainWindow.show();
        mainWindow.setOnCloseRequest(this::ExitProcedure);
        Editor.requestFocus();
    }

    private void StartupSettingsLoader() {
        BufferedReader BR ;
        try {
            File SettingsFile = new File("Settings.properties");
            Properties Settings = new Properties();
            BR = new BufferedReader(new FileReader(SettingsFile.getAbsolutePath()));
            Settings.load(BR);

            if(Settings.getProperty("OpenedFile").equals("NoFileOpen"))
                throw new IOException();
            else {
                OpenedFile = new File(Settings.getProperty("OpenedFile"));
                ReadOpenedFile();
                setFileTree(OpenedFile);
                CurrentTheme = Settings.getProperty("CurrentTheme");
            }
            BR.close();
        } catch (IOException IOE){
            CurrentTheme = "Adreana.css";
        }
    }

    private void setFileTree(File FileOpened){
        if(OpenedFile != null) {
            OpenedFileRoot = new TreeItem<>(FileOpened.getParentFile().getName());
            OpenedFileRoot.setExpanded(true);
            FileTree.setRoot(OpenedFileRoot);
            OpenedFileRoot.getChildren().addAll(getDirectoryContents(FileOpened.getParentFile()));
        }
        else
            FileTree.setRoot(null);
    }

    private TreeItem<String>[] getDirectoryContents(File RootDir) {
        int RootLength = RootDir.listFiles().length;
        TreeItem[] RootNodes;

        if(RootLength == 0){
            RootNodes = new TreeItem[1];
            RootNodes[0] = new TreeItem("Directory empty");
        }
        else{
            RootNodes = new TreeItem[RootLength];
            File[] RootFiles = RootDir.listFiles();
            int i = 0;
            for(File RF: RootFiles){
                if(RF.isFile()) {
                    RootNodes[i] = new TreeItem();
                    RootNodes[i].setValue(RF.getName());
                    FileAddresses.put(RF.getName(), RF.getAbsolutePath());
                }
                else if(RF.isDirectory()){
                    RootNodes[i] = new TreeItem(RF.getAbsoluteFile().getName());
                    RootNodes[i].getChildren().addAll(getDirectoryContents(RF));
                }
                i++;
            }
        }
        return RootNodes;
    }

    private void setEditor(){
        Editor = new CodeArea();
        VSP = new VirtualizedScrollPane(Editor);
        VSP.setId("VSP");
        Editor.setId("Editor");
        IntFunction<Node> numberFactory = LineNumberFactory.get(Editor);
        IntFunction<Node> graphicFactory = line -> {
            HBox LineBox = new HBox(
                    numberFactory.apply(line));
            LineBox.setAlignment(Pos.CENTER_LEFT);
            LineBox.setId("LineBox");
            return LineBox;
        };

        Editor.setParagraphGraphicFactory(graphicFactory);
        Editor.setOnKeyPressed(e ->
        {
            updateCaretPosition();
            if(isModified == false)
                isModified = CheckForModifications();
        });
        Editor.setOnMouseClicked(e -> updateCaretPosition());
    }

    private boolean CheckForModifications() {
        if(isFileOpen){
            if(Editor.getText() == FileContent)
                return false;
            else
                return true;
        }
        else {
            if(Editor.getText().equals(null))
                return false;
            else
                return true;
        }
    }

    private void setMenuBar(){
        MB = new MenuBar();
        Menu[] options = new Menu[4];

        // File Menu
        options[0] = new Menu("File");
        MenuItem[] fileItems = new MenuItem[5];
        fileItems[0] = new MenuItem("New File...");
        fileItems[0].setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN ));
        fileItems[0].setOnAction(e ->
            {
                if(isModified && isFileOpen){       // File opened and modified
                    DialogBox New = new DialogBox();
                    New.showOptionBox("Save...", "Do you wish to save your current file before opening a new one?");
                    int option = New.getOption();
                    if(option == 1) {
                        new FileIO().SaveFile(OpenedFile, Editor.getText());
                        EditorRefresh();
                    }
                    else if(option == -1)
                        EditorRefresh();
                }
                else if(isModified && !isFileOpen){     // No file opened but modified
                    DialogBox New = new DialogBox();
                    New.showOptionBox("Save...", "Do you wish to save your current file before opening a new one?");
                    int option = New.getOption();
                    if(option == 1) {
                        new FileIO().SaveFileAs(Editor.getText());
                        EditorRefresh();
                    }
                    else if(option == -1)
                        EditorRefresh();
                }
                else
                    EditorRefresh();
                isFileOpen = false;
                OpenedFile = null;
                setFileTree(OpenedFile);
            });

        fileItems[1] = new MenuItem("Open...");
        fileItems[1].setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN ));
        fileItems[1].setOnAction(e ->
            {
                if(isModified && isFileOpen){       // File is opened and modified. Will need 'Save'.
                    DialogBox Open = new DialogBox();
                    Open.showOptionBox("Save...", "Do you wish to save your current file before opening a new one?");
                    int option = Open.getOption();
                    if(option == 1){
                        new FileIO().SaveFile(OpenedFile, Editor.getText());
                        OpenedFile = new FileIO().OpenFile();
                        ReadOpenedFile();
                    }
                    else if(option == -1){
                        OpenedFile = new FileIO().OpenFile();
                        ReadOpenedFile();
                    }
                }
                else if(isModified && !isFileOpen){     // File not opened but modified. Will need 'Save As'.
                    DialogBox Open = new DialogBox();
                    Open.showOptionBox("Save As...", "Do you wish to save your current file before opening a new one?");
                    int option = Open.getOption();
                    if(option == 1){
                        new FileIO().SaveFileAs(Editor.getText());
                        OpenedFile = new FileIO().OpenFile();
                        ReadOpenedFile();
                    }
                    else if(option == -1){
                        OpenedFile = new FileIO().OpenFile();
                        ReadOpenedFile();
                    }
                }
                else{
                    OpenedFile = new FileIO().OpenFile();
                    ReadOpenedFile();
                }
                if(OpenedFile != null)
                    setFileTree(OpenedFile);
            });
        fileItems[2] = new MenuItem("Save");
        fileItems[2].setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN ));
        fileItems[2].setOnAction(e -> FileSaveProcedure());
        fileItems[3] = new MenuItem("Save As...");
        fileItems[3].setOnAction(e ->
        {
            OpenedFile = new FileIO().SaveFileAs(Editor.getText());
            ReadOpenedFile();
            setFileTree(OpenedFile);
        }
        );

        fileItems[4] = new MenuItem("Exit");
        fileItems[4].setOnAction(this::ExitProcedure);
        options[0].getItems().addAll(fileItems[0], fileItems[1], new SeparatorMenuItem(), fileItems[2], fileItems[3], new SeparatorMenuItem(), fileItems[4]);

        // Edit Menu
        Clipboard CB = Clipboard.getSystemClipboard();
        ClipboardContent CopyContent = new ClipboardContent();
        options[1] = new Menu("Edit");
        MenuItem[] editItems = new MenuItem[5];
        editItems[0] = new MenuItem("Cut");
        editItems[0].setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN ));
        editItems[0].setOnAction(e -> Editor.cut());

        editItems[1] = new MenuItem("Copy");
        editItems[1].setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN ));
        editItems[1].setOnAction(e -> Editor.copy());

        editItems[2] = new MenuItem("Paste");
        editItems[2].setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN ));
        editItems[2].setOnAction(e -> Editor.paste());

        editItems[3] = new MenuItem("Find");
        editItems[3].setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN ));
        editItems[3].setOnAction(e ->
        {
            FindReplaceMenu Find = new FindReplaceMenu();
            Find.FindMenu(Editor.getText());
        });

        editItems[4] = new MenuItem("Replace");
        editItems[4].setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN ));
        options[1].getItems().addAll(editItems[0], editItems[1], editItems[2], new SeparatorMenuItem(), editItems[3], editItems[4]);

        options[2] = new Menu("View");
        MenuItem[] viewItems = new MenuItem[1];
        viewItems[0] = new MenuItem("Themes");
        viewItems[0].setOnAction(e ->
        {
            new ThemesMenu().display(CurrentTheme);
            CurrentTheme = ThemesMenu.SelectedTheme;
            setTheme(CurrentTheme);
        }
        );
        options[2].getItems().add(viewItems[0]);

        options[3] = new Menu("Help");
        MenuItem[] helpItems = new MenuItem[3];
        helpItems[0] = new MenuItem("FAQs...");
        helpItems[1] = new MenuItem("Check for updates...");
        helpItems[2] = new MenuItem("About");
        helpItems[2].setOnAction(e -> new AboutScreen().display());
        options[3].getItems().addAll(helpItems[0], helpItems[1], helpItems[2]);

        MB.getMenus().addAll(options[0], options[1], options[2], options[3]);
    }

    private void setTheme(String selectedTheme) {
        UIScene.getStylesheets().clear();
        UIScene.getStylesheets().add("Themes/" + selectedTheme);
    }

    private void EditorRefresh(String filename) {
        try {
            Editor.clear();
        } catch(Exception E) {}
        String WindowTitle = "Ballad - " + filename;
        mainWindow.setTitle(WindowTitle);
    }

    private void EditorRefresh() {
        EditorRefresh("Untitled");
    }

    private void setStatusBar(){
        StatusBar = new HBox(15);
        caretPosition = new Label();
        caretPosition.setId("CaretPosition");
        fileType = new Label();
        StatusBar.setId("StatusBar");
        updateCaretPosition();
        StatusBar.getChildren().addAll(caretPosition, fileType);
        HBox.setMargin(caretPosition, new Insets(5, 10, 5, 10));
        HBox.setMargin(fileType, new Insets(5, 10, 5, 10));
    }

    private void updateCaretPosition(){
        caretPosition.setText(Integer.toString(Editor.getCurrentParagraph() + 1) + " | " + Integer.toString(Editor.getCaretColumn() + 1));
    }

    private void ReadOpenedFile(){
        if(OpenedFile != null) {
            BufferedReader BR = null;
            FileContent = "";
            try {
                BR = new BufferedReader(new FileReader(OpenedFile));
            } catch (IOException IOE) {
                new DialogBox().showAlertBox("Error!", "File not found.");
            } catch (NullPointerException NPE) {
            }
            try {
                String Line;
                while ((Line = BR.readLine()) != null)
                    FileContent += Line + "\n";
                EditorRefresh(OpenedFile.getPath());
            } catch (IOException IOE) {
                new DialogBox().showAlertBox("Error!", "File not found.");
            } catch (NullPointerException NPE) {
            }
            Editor.appendText(FileContent);
            updateCaretPosition();      // No event happens, hence needs to be manually called.
            StringBuffer SB = new StringBuffer(OpenedFile.getName());
            int dotPosition = SB.lastIndexOf(".");
            String FileType = SB.substring(dotPosition + 1, SB.length());
            switch (FileType) {
                case "c":
                    fileType.setText("File type: C");
                    break;
                case "cpp":
                    fileType.setText("File type: C++");
                    break;
                case "java":
                    fileType.setText("File type: Java");
                    break;
                case "py":
                    fileType.setText("File type: Python");
                    break;
                case "html":
                    fileType.setText("File type: HyperText Markup Language");
                    break;
                case "css":
                    fileType.setText("File type: Cascading Style Sheets");
                    break;
                case "txt":
                    fileType.setText("File type: Plain Text");
                    break;
                default:
                    fileType.setText("");
            }
            if (OpenedFile != null)
                isFileOpen = true;
            else
                isFileOpen = false;
        }
    }

    private void FileSaveProcedure() {
        if (isFileOpen)
            new FileIO().SaveFile(OpenedFile, Editor.getText());
        else {
            OpenedFile = new FileIO().SaveFileAs(Editor.getText());
            ReadOpenedFile();
            setFileTree(OpenedFile);
        }
    }

    private void ExitProcedure(Event e) {
        e.consume();
        try {
            BufferedWriter BW;
            File ThemesConfig = new File("Settings.properties");
            BW = new BufferedWriter(new FileWriter(ThemesConfig.getAbsolutePath()));
            Properties Settings = new Properties();
            if(isFileOpen)
                Settings.setProperty("OpenedFile", OpenedFile.getAbsolutePath());
            else
                Settings.setProperty("OpenedFile", "NoFileOpen");
            Settings.setProperty("CurrentTheme", CurrentTheme);
            Settings.store(BW, null);
            BW.close();
        } catch (IOException IOE) {
        } catch (NullPointerException NPE) {
        }
        if (isModified && isFileOpen) {
            FileSaveProcedure();
            mainWindow.close();
        }
        else if(isModified) {
            DialogBox Exit = new DialogBox();
            Exit.showOptionBox("Save...", "Do you wish to save your current file before exiting?");
            int option = Exit.getOption();
            if(option == 1) {
                new FileIO().SaveFile(OpenedFile, Editor.getText());
                mainWindow.close();
            }
            else if(option == -1)
                mainWindow.close();
        }
        else
            mainWindow.close();
    }

    public static void main(String[] args) { launch(args); }
}
