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
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import rollingcube.results.GameResult;
import rollingcube.results.GameResultDao;
import rollingcube.state.RollingCubesState;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
public class GameController {

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
    private Label stepsLabel;

    @FXML
    private Label stopWatchLabel;

    private Timeline stopWatchTimeline;

    @FXML
    private Button resetButton;

    @FXML
    private Button giveUpButton;

    private BooleanProperty gameOver = new SimpleBooleanProperty();

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @FXML
    public void initialize() {
        cubeImages = List.of(
                new Image(getClass().getResource("/images/cube1RED.png").toExternalForm()),
                new Image(getClass().getResource("/images/cube2l.png").toExternalForm()),
                new Image(getClass().getResource("/images/cube3u.png").toExternalForm()),
                new Image(getClass().getResource("/images/cube4r.png").toExternalForm()),
                new Image(getClass().getResource("/images/cube5d.png").toExternalForm()),
                new Image(getClass().getResource("/images/blockedfield.png").toExternalForm()),
                new Image(getClass().getResource("/images/goalfield.png").toExternalForm()),
                new Image(getClass().getResource("/images/cube0.png").toExternalForm()),
                new Image(getClass().getResource("/images/hoveredField.png").toExternalForm()),
                new Image(getClass().getResource("/images/mouseHoveredField.png").toExternalForm()),
                new Image(getClass().getResource("/images/goalfieldHovered.png").toExternalForm()),
                new Image(getClass().getResource("/images/goalfieldMouseHovered.png").toExternalForm())
        );
        stepsLabel.textProperty().bind(steps.asString());
        gameOver.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                log.info("Game is over");
                log.debug("Saving result to database...");
                gameResultDao.persist(createGameResult());
                stopWatchTimeline.stop();
            }
        });
        resetGame();
    }

    private void resetGame() {
        gameState = new RollingCubesState();
        steps.set(0);
        startTime = Instant.now();
        gameOver.setValue(false);
        displayGameState();
        createStopWatch();
        Platform.runLater(() -> messageLabel.setText("Good luck, " + playerName + "!"));
    }

    private void displayGameState() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                ImageView view = (ImageView) gameGrid.getChildren().get(i * 7 + j);
                if (view.getImage() != null) {
                    log.trace("Image({}, {}) = {}", i, j, view.getImage().getUrl());
                }
                if (gameState.getTray()[i][j] == 0) {
                    view.setImage(cubeImages.get(0));
                } else {
                    if (gameState.getTray()[i][j] == 1 || gameState.getTray()[i][j] == 2 || gameState.getTray()[i][j] == 3 || gameState.getTray()[i][j] == 4) {
                        if (gameState.getPlayer_leftside() == 0)
                            view.setImage(cubeImages.get(1));
                        if (gameState.getPlayer_upside() == 0)
                            view.setImage(cubeImages.get(2));
                        if (gameState.getPlayer_rightside() == 0)
                            view.setImage(cubeImages.get(3));
                        if (gameState.getPlayer_downside() == 0)
                            view.setImage(cubeImages.get(4));
                    }
                    if (gameState.getTray()[i][j] == 6)
                        if (gameOver.getValue() == false && gameState.canRollToField(i, j))
                            view.setImage(cubeImages.get(8));
                        else
                            view.setImage(cubeImages.get(7));
                    if (gameState.getTray()[i][j] == 7)
                        view.setImage(cubeImages.get(5));
                    if (gameState.getTray()[i][j] == 8 )
                        if (gameState.isFieldNear(i, j))
                            view.setImage(cubeImages.get(10));
                        else
                            view.setImage(cubeImages.get(6));
                };
            }
        }
    }

    public void handleClickOnField(MouseEvent mouseEvent) {
        int row = GridPane.getRowIndex((Node) mouseEvent.getSource());
        int col = GridPane.getColumnIndex((Node) mouseEvent.getSource());
        log.debug("Field ({}, {}) is pressed", row, col);

        // Check whether the field is near to the player's cube.
        if (!gameState.isSolved() && gameState.isFieldNear(row, col)) {
            // Check whether the cube can be rolled to the specific field.
            if (gameState.canRollToField(row, col)) {
                steps.set(steps.get() + 1);
                gameState.rollToEmptySpace(row, col);
                if (gameState.isSolved()) {
                    gameOver.setValue(true);
                    log.info("Player {} has solved the game in {} steps", playerName, steps.get());
                    messageLabel.setText("Congratulations, " + playerName + "!");
                    resetButton.setDisable(true);
                    giveUpButton.setText("Finish");
                }
            }
        }
        displayGameState();
    }

    public void handleMouseEntered(MouseEvent mouseEvent) {
        int row = GridPane.getRowIndex((Node) mouseEvent.getSource());
        int col = GridPane.getColumnIndex((Node) mouseEvent.getSource());

        ImageView view = (ImageView) gameGrid.getChildren().get(row * 7 + col);
        if (gameState.canRollToField(row, col) && gameOver.getValue() == false) {
            if (gameState.isGoalField(row, col))
                view.setImage(cubeImages.get(11));
            else
                view.setImage(cubeImages.get(9));
        }
    }

    public void handleMouseExited(MouseEvent mouseEvent) {
        int row = GridPane.getRowIndex((Node) mouseEvent.getSource());
        int col = GridPane.getColumnIndex((Node) mouseEvent.getSource());

        ImageView view = (ImageView) gameGrid.getChildren().get(row * 7 + col);
        if (gameState.isGoalField(row, col) && gameState.isFieldNear(row, col))
            view.setImage(cubeImages.get(10));
        else
            if (view.getImage() == cubeImages.get(9))
                view.setImage(cubeImages.get(8));
    }

    public void handleResetButton(ActionEvent actionEvent)  {
        log.debug("{} is pressed", ((Button) actionEvent.getSource()).getText());
        log.info("Resetting game...");
        stopWatchTimeline.stop();
        resetGame();
    }

    public void handleGiveUpButton(ActionEvent actionEvent) throws IOException {
        String buttonText = ((Button) actionEvent.getSource()).getText();
        log.debug("{} is pressed", buttonText);
        if (buttonText.equals("Give Up")) {
            log.info("The game has been given up");
        }
        gameOver.setValue(true);
        log.info("Loading high scores scene...");
        fxmlLoader.setLocation(getClass().getResource("/fxml/highscores.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private GameResult createGameResult() {
        GameResult result = GameResult.builder()
                .player(playerName)
                .solved(gameState.isSolved())
                .duration(Duration.between(startTime, Instant.now()))
                .steps(steps.get())
                .build();
        return result;
    }

    private void createStopWatch() {
        stopWatchTimeline = new Timeline(new KeyFrame(javafx.util.Duration.ZERO, e -> {
            long millisElapsed = startTime.until(Instant.now(), ChronoUnit.MILLIS);
            stopWatchLabel.setText(DurationFormatUtils.formatDuration(millisElapsed, "HH:mm:ss"));
        }), new KeyFrame(javafx.util.Duration.seconds(1)));
        stopWatchTimeline.setCycleCount(Animation.INDEFINITE);
        stopWatchTimeline.play();
    }

}
