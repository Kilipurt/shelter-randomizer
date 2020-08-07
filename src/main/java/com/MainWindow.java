package com;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MainWindow extends Application {
    private ChoiceBox<String> timeUnit;
    private TextField timeField;
    private Label timerLbl;
    private boolean isTimerRan;
    private boolean isTimerOnPause;
    private Timeline timeline;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ObservableList<String> fileNames = FXCollections.observableList(InfoFileReader.readFileNames());
        ListView<String> filesListView = new ListView<>(fileNames);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(createGenerateMenu(filesListView));

        //timer
        timeField = new TextField();

        timeUnit = new ChoiceBox<>();
        timeUnit.getItems().addAll("Минута", "Секунда");
        timeUnit.setValue("Минута");

        HBox timeInputBox = new HBox(5, timeField, timeUnit);
        timerLbl = new Label();
        timerLbl.setFont(new Font(18));
        timerLbl.setMaxSize(50,10);
        timerLbl.setAlignment(Pos.CENTER);
        timerLbl.setStyle("-fx-border-color: black");
        HBox timerBtnBox = new HBox(5, createStartTimerBtn(), createPauseTimerBtn());
        VBox timerBox = new VBox(5, new Label("Timer"), timeInputBox, timerLbl, timerBtnBox);
        //timer

        VBox btnBox = new VBox(5, createReadPropertyBtn(filesListView), createShowInFolderBtn(filesListView), timerBox);
        HBox hBox = new HBox(5, filesListView, btnBox);
        hBox.setAlignment(Pos.TOP_CENTER);
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(hBox);

        primaryStage.setTitle("Shelter");
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    private Button createPauseTimerBtn() {
        Button btn = new Button("Pause/Continue");
        btn.setOnAction(e -> {
            if (!isTimerRan) {
                return;
            }

            if (isTimerOnPause) {
                isTimerOnPause = false;
                startTimer(Integer.parseInt(timerLbl.getText()));
            } else {
                isTimerOnPause = true;
                timeline.stop();
            }
        });
        return btn;
    }

    private Button createStartTimerBtn() {
        Button btn = new Button("Пуск");
        btn.setOnAction(e -> {
            if (isTimerRan) {
                timeline.stop();
            }
            isTimerRan = true;
            String timeValueStr = timeField.getText();
            if (StringUtils.isBlank(timeValueStr) || !StringUtils.isNumeric(timeValueStr)) {
                return;
            }
            String timeUnitStr = timeUnit.getValue();
            int value = Integer.parseInt(timeValueStr);
            if (timeUnitStr.startsWith("М")) {
                value *= 60;
            }
            int[] finalTime = {value};
            timeline = createTimeline(value, finalTime);
            timeline.play();
        });

        return btn;
    }

    private void startTimer(int value) {
        if (isTimerRan) {
            timeline.stop();
        }
        isTimerRan = true;
        int[] finalTime = {value};
        timeline = createTimeline(value, finalTime);
        timeline.play();
    }

    private Timeline createTimeline(int value, int[] finalTime) {
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.millis(1000),
                        ae -> {
                            finalTime[0]--;
                            if (finalTime[0] == 0) {
                                timerLbl.setText("Время вышло!!!");
                            } else {
                                timerLbl.setText(String.valueOf(finalTime[0]));
                            }
                        }
                )
        );

        timeline.setCycleCount(value);
        timeline.setOnFinished(e -> isTimerRan = false);
        return timeline;
    }

    private Button createShowInFolderBtn(ListView<String> filesListView) {
        Button btn = new Button("Показать в папке");
        btn.setOnAction(e -> {
            MultipleSelectionModel<String> filesSelectionModel = filesListView.getSelectionModel();
            String fileName = filesSelectionModel.selectedItemProperty().get();
            openFileLocation("..\\..\\shelter\\" + fileName);
        });
        return btn;
    }

    private Button createReadPropertyBtn(ListView<String> filesListView) {
        Button readPropertyBtn = new Button("Прочитать характеристику");
        readPropertyBtn.setOnAction(e -> {
            MultipleSelectionModel<String> filesSelectionModel = filesListView.getSelectionModel();
            String fileName = filesSelectionModel.selectedItemProperty().get();

            if (!StringUtils.isNumeric(fileName.replace(".txt", StringUtils.EMPTY))) {
                try {
                    String line = InfoFileReader.readFile(fileName.replace(".txt", StringUtils.EMPTY));
                    createInfoAlertAndShow(line);
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }

                return;
            }

            List<String> choices = CharacterProperty.toList();

            ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
            dialog.setTitle("Узнать характеристику");
            dialog.setContentText("Выберите характеристику:");
            dialog.setHeaderText(null);
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(property -> {
                try {
                    String line = InfoFileReader.readLine(CharacterProperty.getByName(property).getId(), fileName.replace(".txt", StringUtils.EMPTY));
                    createInfoAlertAndShow(line);
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            });
        });

        return readPropertyBtn;
    }

    private void createInfoAlertAndShow(String line) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(line);
        alert.showAndWait();
    }

    private void refreshFiles(ListView<String> filesListView) {
        filesListView.getItems().clear();
        ObservableList<String> fileNames = FXCollections.observableList(InfoFileReader.readFileNames());
        filesListView.getItems().addAll(fileNames);
    }

    private Menu createGenerateMenu(ListView<String> filesListView) {
        Menu menu = new Menu("Сгенерировать");
        menu.getItems().addAll(createGenerateCharactersMenuItem(filesListView), createGeneratePropertyMenuItem(filesListView));
        return menu;
    }

    private MenuItem createGeneratePropertyMenuItem(ListView<String> filesListView) {
        MenuItem menuItem = new MenuItem("Характеристика");
        menuItem.setOnAction(e -> {
            List<String> choices = CharacterProperty.toList();

            ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
            dialog.setTitle("Сгенерировать характеристику");
            dialog.setContentText("Выберите характеристику:");
            dialog.setHeaderText(null);

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(property -> {
                try {
                    String character = CharacterGenerator.getInstance().generate();
                    CharacterProperty characterProperty = CharacterProperty.getByName(property);
                    InfoFileWriter.writeLine(character, characterProperty.getId(), characterProperty.toString().toLowerCase());
                    refreshFiles(filesListView);
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            });
        });
        return menuItem;
    }

    private MenuItem createGenerateCharactersMenuItem(ListView<String> filesListView) {
        MenuItem menuItem = new MenuItem("Персонаж");
        menuItem.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Сгенерировать персонажей");
            dialog.setContentText("Введите количество игроков:");
            dialog.setHeaderText(null);

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> {
                InfoFileWriter.removeAllFiles();
                for (int i = 0; i < Integer.parseInt(s); i++) {
                    try {
                        String character = CharacterGenerator.getInstance().generate();
                        InfoFileWriter.write(character, i + 1);
                        refreshFiles(filesListView);
                    } catch (Exception ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            });
        });
        return menuItem;
    }

    private void openFileLocation(String path) {
        try {
            Runtime.getRuntime().exec("explorer.exe /select," + path);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
