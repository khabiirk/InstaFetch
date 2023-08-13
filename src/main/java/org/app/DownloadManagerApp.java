package main.java.org.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DownloadManagerApp extends Application {

    private static final Logger logger = LogManager.getLogger(DownloadManagerApp.class);
    int index = -1;
    private final List<HBox> urlBoxes = new ArrayList<>();
    private DownloadManager downloadManager;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("InstaFetch");

        ProgressBar pb = new ProgressBar();
        ProgressIndicator pi = new ProgressIndicator(0.6);
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        MenuBar menuBar = createMenuBar();
        HBox mainUrlBox = createUrlBox(stage, pb);

        Separator separator = new Separator();
        separator.setMaxWidth(Double.MAX_VALUE);

        urlBoxes.add(mainUrlBox);

        Button addUrlBoxButton = new Button("Add URL Box");
        addUrlBoxButton.setOnAction(event -> {
            HBox newUrlBox = createUrlBox(stage, pb);
            urlBoxes.add(newUrlBox);
            root.getChildren().add(root.getChildren().size() - 3, separator);
            root.getChildren().add(root.getChildren().size() - 3, newUrlBox);
        });

        root.getChildren().addAll(menuBar, separator);
        root.getChildren().addAll(urlBoxes);
        root.getChildren().addAll(addUrlBoxButton);

        Scene scene = new Scene(root, 400, 350);
        stage.setScene(scene);
        stage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(event -> Platform.exit());
        fileMenu.getItems().addAll(exitMenuItem);

        Menu optionMenu = new Menu("Option");


        menuBar.getMenus().addAll(fileMenu, optionMenu);
        return menuBar;
    }

    private HBox createUrlBox(Stage stage, ProgressBar pb) {
        HBox urlBox = new HBox();
        urlBox.setSpacing(10);
        index += 1;

        TextField urlTextField = new TextField();
        urlTextField.setPromptText("Enter URL");


        Button downloadButton = new Button("Download");


        downloadButton.setOnAction(event -> {
            String url = urlTextField.getText();
            //Check if the text in the textfield is a url
            if(!url.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"))
            {
                urlTextField.setText("");
                urlTextField.setPromptText("Not a valid URL");
            }
            else {
                //Open file explorer to choose a destination directory
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedFolder = directoryChooser.showDialog(stage);


                //Add the progress bar once Download has started
                urlBox.getChildren().add(pb);
                //Start the download
                downloadManager = new DownloadManager(3);
                downloadManager.addDownload(url, selectedFolder.getAbsolutePath());

                downloadManager.startDownloads();
                //Background thread to update the progress bar
                Task<Void> task = createTask(urlBox, pb, index);
                pb.progressProperty().unbind();
                pb.progressProperty().bind(task.progressProperty());
                new Thread(task).start();
            }

        });

        urlBox.getChildren().addAll(urlTextField, downloadButton);
        return urlBox;
    }

    private Task<Void> createTask(HBox urlBox, ProgressBar pb, int index) {
        return new Task<>() {
            @Override
            protected Void call() {
                while (downloadManager.getDownloadStatus(index) != Status.COMPLETED) {

                    updateProgress(downloadManager.getDownloadProgress(index), 100);
                }

                //Remove the progress bar once the download is finished
                Platform.runLater(() -> urlBox.getChildren().remove(pb));
                //Open the downloaded file location in file explorer if its possible.
                if(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE_FILE_DIR))
                {

                    File dir = new File(downloadManager.getDownloadPath());
                    Desktop.getDesktop().browseFileDirectory(dir);
                }

                return null;
            }
        };
    }



    public static void main(String[] args) {
        launch(args);
    }
}

