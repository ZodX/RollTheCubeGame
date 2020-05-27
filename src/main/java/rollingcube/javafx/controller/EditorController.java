package rollingcube.javafx.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import rollingcube.results.GameResult;
import rollingcube.results.GameResultDao;
import rollingcube.state.RollingCubesState;

import javax.inject.Inject;
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class EditorController {

    @Inject
    private FXMLLoader fxmlLoader;

    @Inject
    private GameResultDao gameResultDao;

    private String playerName;
    private RollingCubesState gameState;
    private IntegerProperty steps = new SimpleIntegerProperty();
    private Instant startTime;
    private List<Image> cubeImages;

    @FXML
    private Label messageLabel;

    @FXML
    private GridPane gameGrid;

    @FXML
    private VBox vBox;

    @FXML
    private ImageView empty, blocked, goal, starter;

    @FXML
    private Label stepsLabel;

    @FXML
    private Label stopWatchLabel;

    private Timeline stopWatchTimeline;

    @FXML
    private Button resetButton;

    @FXML
    private Button giveUpButton;

    private boolean selectedEmpty = false,
                    selectedBlocked = false,
                    selectedGoal = false,
                    selectedStarter = false;

    private int goalcount = 1;

    private BooleanProperty gameOver = new SimpleBooleanProperty();

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    private int[][] editorTray = {
            {8, 6, 6, 6, 6, 6, 6},
            {6, 6, 6, 6, 6, 6, 6},
            {6, 6, 6, 6, 6, 6, 6},
            {6, 6, 6, 6, 6, 6, 6},
            {6, 6, 6, 6, 6, 6, 6},
            {6, 6, 6, 6, 6, 6, 6},
            {6, 6, 6, 6, 6, 6, 0}
    };

    @FXML
    public void initialize() {
        cubeImages = List.of(
                new Image(getClass().getResource("/images/cube1RED.png").toExternalForm()),
                new Image(getClass().getResource("/images/editorEmptyField.png").toExternalForm()),
                new Image(getClass().getResource("/images/blockedfield.png").toExternalForm()),
                new Image(getClass().getResource("/images/goalfield.png").toExternalForm()),
                new Image(getClass().getResource("/images/cube0.png").toExternalForm()),
                new Image(getClass().getResource("/images/editorEmptyHighlighted.png").toExternalForm()),
                new Image(getClass().getResource("/images/editorBlockedFieldHighlighted.png").toExternalForm()),
                new Image(getClass().getResource("/images/editorGoalfieldHighlighted.png").toExternalForm()),
                new Image(getClass().getResource("/images/editorCubeRedHighlighted.png").toExternalForm())

        );
        resetGame();
    }

    private void resetGame() {
        displayEditor();
        Platform.runLater(() -> messageLabel.setText("Map editor"));
    }

    private void displayEditor() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                ImageView view = (ImageView) gameGrid.getChildren().get(i * 7 + j);
                if (view.getImage() != null) {
                    log.trace("Image({}, {}) = {}", i, j, view.getImage().getUrl());
                }
                switch (editorTray[i][j]) {
                    case 0:
                        view.setImage(cubeImages.get(0));
                        break;
                    case 6:
                        view.setImage(cubeImages.get(4));
                        break;
                    case 7:
                        view.setImage(cubeImages.get(2));
                        break;
                    case 8:
                        view.setImage(cubeImages.get(3));
                        break;
                }
            };
        }

        empty.setImage(cubeImages.get(1));

        blocked.setImage(cubeImages.get(2));

        goal.setImage(cubeImages.get(3));

        starter.setImage(cubeImages.get(0));

    }

    public void handleClickOnField(MouseEvent mouseEvent) {
        int row = GridPane.getRowIndex((Node) mouseEvent.getSource());
        int col = GridPane.getColumnIndex((Node) mouseEvent.getSource());
        log.debug("Field ({}, {}) is pressed", row, col);

        ImageView view = (ImageView) gameGrid.getChildren().get(row * 7 + col);
        ImageView viewHelper;

        if (selectedEmpty) {
            if (view.getImage() == cubeImages.get(0)) {
            }
            if (view.getImage() == cubeImages.get(3) && goalcount > 1) {
                view.setImage(cubeImages.get(4));
                goalcount--;
            }
            if (view.getImage() == cubeImages.get(2))
                view.setImage(cubeImages.get(4));
        } else if (selectedBlocked) {
            if (view.getImage() == cubeImages.get(0)) {
            }
            if (view.getImage() == cubeImages.get(3) && goalcount > 1) {
                view.setImage(cubeImages.get(2));
                goalcount--;
            }
            if (view.getImage() == cubeImages.get(4))
                view.setImage(cubeImages.get(2));
        } else if (selectedGoal) {
            if (view.getImage() == cubeImages.get(0)) {
            } else {
                view.setImage(cubeImages.get(3));
                goalcount++;
            }
        } else if (selectedStarter) {
            if (view.getImage() == cubeImages.get(3)) {
                if (goalcount > 1) {
                    for (int i = 0; i < 7; i++)
                        for (int j = 0; j < 7; j++) {
                            viewHelper = (ImageView) gameGrid.getChildren().get(i * 7 + j);
                            if (viewHelper.getImage() == cubeImages.get(0)) {
                                viewHelper.setImage(cubeImages.get(4));

                                view.setImage(cubeImages.get(0));
                                break;
                            }
                        }
                    goalcount--;
                }
            } else {
                for (int i = 0; i < 7; i++)
                    for (int j = 0; j < 7; j++) {
                        viewHelper = (ImageView) gameGrid.getChildren().get(i * 7 + j);
                        if (viewHelper.getImage() == cubeImages.get(0)) {
                            viewHelper.setImage(cubeImages.get(4));

                            view.setImage(cubeImages.get(0));
                            break;
                        }
                    }
            }
        }
    }

    public void handleSaveExitButton(ActionEvent actionEvent) throws IOException {
        String url = getClass().getResource("/map/map.txt").toExternalForm().toString();
        String newUrl = "";

        for (int i = 5; i < url.length(); i++)
            newUrl = newUrl + url.charAt(i);

        ImageView viewHelper;
        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 7; j++) {
                viewHelper = (ImageView) gameGrid.getChildren().get(i * 7 + j);
                if (viewHelper.getImage() == cubeImages.get(4))
                    editorTray[i][j] = 6;
                else if (viewHelper.getImage() == cubeImages.get(2))
                    editorTray[i][j] = 7;
                else if (viewHelper.getImage() == cubeImages.get(3))
                    editorTray[i][j] = 8;
                else if (viewHelper.getImage() == cubeImages.get(0))
                    editorTray[i][j] = 0;
            }

        File f = new File(newUrl);
            if (f.exists() && f.isFile()) {
                f.delete();
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        FileWriter writer = new FileWriter(newUrl);
        String currentLine = "";
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                currentLine += editorTray[i][j] + " ";
            }
            writer.write(currentLine + System.getProperty("line.separator"));
            currentLine = "";
        }
        log.info("Saving the new map...");
        writer.close();

        fxmlLoader.setLocation(getClass().getResource("/fxml/launch.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
        log.info("Loading menu page...");
    }

    public void handleEmptyClicked (MouseEvent mouseEvent) {
        selectedEmpty = true;
        selectedBlocked = false;
        selectedGoal = false;
        selectedStarter = false;

        empty.setImage(cubeImages.get(5));
        blocked.setImage(cubeImages.get(2));
        goal.setImage(cubeImages.get(3));
        starter.setImage(cubeImages.get(0));
    }

    public void handleBlockedClicked (MouseEvent mouseEvent) {
        selectedEmpty = false;
        selectedBlocked = true;
        selectedGoal = false;
        selectedStarter = false;

        empty.setImage(cubeImages.get(1));
        blocked.setImage(cubeImages.get(6));
        goal.setImage(cubeImages.get(3));
        starter.setImage(cubeImages.get(0));
    }

    public void handleGoalClicked (MouseEvent mouseEvent) {
        selectedEmpty = false;
        selectedBlocked = false;
        selectedGoal = true;
        selectedStarter = false;

        empty.setImage(cubeImages.get(1));
        blocked.setImage(cubeImages.get(2));
        goal.setImage(cubeImages.get(7));
        starter.setImage(cubeImages.get(0));
    }

    public void handleStarterClicked (MouseEvent mouseEvent) {
        selectedEmpty = false;
        selectedBlocked = false;
        selectedGoal = false;
        selectedStarter = true;

        empty.setImage(cubeImages.get(1));
        blocked.setImage(cubeImages.get(2));
        goal.setImage(cubeImages.get(3));
        starter.setImage(cubeImages.get(8));
    }
}