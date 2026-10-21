package view;

//note questions are hard coded and will be changed once everypart of the app is merged
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameScreenStoryView {

  public static void main(String[] args) {
      Application.launch(BattleApp.class, args);
  }

  public static class BattleApp extends Application {
      @Override
      public void start(Stage stage) {
          DuelBattleScreen battleScreen = new DuelBattleScreen(
              "Calculus 1", 
              () -> showPopup(stage, "VICTORY!", "Red box fell off the platform! You Win!"),
              () -> showPopup(stage, "DEFEAT!", "Green box fell off the platform! You Lost!"),
              () -> showPopup(stage, "PAUSE", "Game Paused."),
              () -> showPopup(stage, "OPTIONS", "Settings / Options Menu Not Implemented Yet.")
          );

          // 800 x 500 default window size
          Scene scene = new Scene(battleScreen, 800, 500);
          stage.setTitle("Duelo Matematica - Battle Screen");
          stage.setScene(scene);
          stage.show();
      }

      private void showPopup(Stage stage, String title, String message) {
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.initOwner(stage);
          alert.setTitle(title);
          alert.setHeaderText(null);
          alert.setContentText(message);
          alert.show();
      }
  }
}

/**
* Tug-of-war Battle Screen with top pause button and bottom-left options gear button.
*/
class DuelBattleScreen extends BorderPane {

  // LOCAL EMBEDDED QUESTION BANK
  private static final String[][] LOCAL_MATH_QUESTIONS = {
      {"1", "Calculus 1", "Easy", "What is the derivative of f(x) = x⁵ - 3x² + 1?", "5x⁴ - 6x", "5x⁴ - 6x + 1", "x⁴ - 6x", "5x⁴ - 3x"},
      {"2", "Calculus 1", "Easy", "Evaluate the limit of (x² - 9) / (x - 3) as x → 3.", "6", "0", "3", "Undefined"},
      {"3", "Calculus 1", "Easy", "What is the derivative of f(x) = cos(4x)?", "-4sin(4x)", "4sin(4x)", "-sin(4x)", "-4cos(4x)"},
      {"4", "Calculus 1", "Easy", "Evaluate the limit of (eˣ - 1) / x as x → 0.", "1", "0", "∞", "e"},
      {"5", "Calculus 1", "Medium", "Find the critical points of f(x) = x³ - 6x² + 9x.", "x = 1 and x = 3", "x = 0 and x = 3", "x = -1 and x = -3", "x = 2 and x = 4"},
      {"6", "Statistics", "Easy", "What is the mean of the dataset {2, 4, 6, 8, 10}?", "6", "5", "7", "30"},
      {"7", "Statistics", "Easy", "Probability of rolling an even number on a fair 6-sided die?", "1/2", "1/6", "1/3", "2/3"},
      {"8", "Symbolic Logic", "Easy", "Truth value of P ∧ Q when P = True and Q = False?", "False", "True", "Undefined", "Tautology"},
      {"9", "Symbolic Logic", "Easy", "What is the contrapositive of P → Q?", "¬Q → ¬P", "Q → P", "¬P → ¬Q", "P ∧ ¬Q"},
      {"10", "Calculus 2", "Easy", "Evaluate the indefinite integral ∫ x³ dx.", "(1/4)x⁴ + C", "3x² + C", "(1/3)x⁴ + C", "x⁴ + C"}
  };

  public static class Question {
      String category;
      String difficulty;
      String prompt;
      List<String> options;
      int correctIndex;

      public Question(String category, String difficulty, String prompt, String[] rawOptions) {
          this.category = category;
          this.difficulty = difficulty;
          this.prompt = prompt;

          String correctAnswer = rawOptions[0];

          List<String> shuffled = new ArrayList<>(Arrays.asList(rawOptions));
          Collections.shuffle(shuffled);

          this.options = shuffled;
          this.correctIndex = shuffled.indexOf(correctAnswer);
      }
  }

  private static final double PLATFORM_WIDTH = 480.0;
  private static final double BOX_WIDTH = 35.0;
  private static final double BOX_HEIGHT = 50.0;
  private static final double BAR_HEIGHT = 20.0;
  private static final double BASE_STEP_DISTANCE = 40.0;

  private List<Question> questionQueue;
  private int currentQuestionIndex = 0;
  private Question currentQuestion;
  private long questionStartTime;
  private double totalScoreMultiplier = 0.0;

  private double greenX = -BOX_WIDTH - 5;
  private double redX = 5;

  private final Label categoryLabel;
  private final Label equationLabel;
  private final ScrollPane questionScrollPane;
  private final Button[] optionButtons = new Button[4];
  private final Rectangle greenBox;
  private final Rectangle redBox;

  private final Runnable onVictory;
  private final Runnable onDefeat;
  private final Runnable onPause;
  private final Runnable onOptions;

  public DuelBattleScreen(String selectedCategory, Runnable onVictory, Runnable onDefeat, Runnable onPause, Runnable onOptions) {
      this.onVictory = onVictory;
      this.onDefeat = onDefeat;
      this.onPause = onPause;
      this.onOptions = onOptions;

      this.setStyle("-fx-background-color: #ffffff;");

      // 1. TOP BAR (Pause Button)
      Button pauseButton = new Button("II");
      pauseButton.setPrefSize(42, 42);
      pauseButton.setStyle(
          "-fx-font-size: 16px; " +
          "-fx-font-weight: bold; " +
          "-fx-background-color: #e6e6e6; " +
          "-fx-border-color: #000000; " +
          "-fx-border-width: 2px; " +
          "-fx-cursor: hand;"
      );
      pauseButton.setOnAction(e -> {
          if (onPause != null) onPause.run();
      });

      HBox topBar = new HBox(pauseButton);
      topBar.setPadding(new Insets(10, 15, 0, 15));
      topBar.setAlignment(Pos.TOP_LEFT);

      // 2. BOTTOM BAR (Options/Gear Button at Bottom Left)
      Button gearButton = createGearButton();
      gearButton.setOnAction(e -> {
          if (onOptions != null) onOptions.run();
      });

      HBox bottomBar = new HBox(gearButton);
      bottomBar.setPadding(new Insets(0, 15, 12, 15));
      bottomBar.setAlignment(Pos.BOTTOM_LEFT);

      // 3. TOP-ALIGNED QUESTION CARD
      categoryLabel = new Label("CATEGORY");
      categoryLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555555;");

      equationLabel = new Label();
      equationLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #000000;");
      equationLabel.setWrapText(true);
      equationLabel.setAlignment(Pos.TOP_CENTER);

      questionScrollPane = new ScrollPane(equationLabel);
      questionScrollPane.setFitToWidth(true);
      questionScrollPane.setPrefHeight(115);
      questionScrollPane.setMaxHeight(125);
      questionScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
      questionScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
      questionScrollPane.setStyle(
          "-fx-background-color: transparent; " +
          "-fx-background: transparent; " +
          "-fx-viewport-background: transparent; " +
          "-fx-padding: 0;"
      );

      VBox equationHeaderBox = new VBox(4, categoryLabel, questionScrollPane);
      equationHeaderBox.setAlignment(Pos.TOP_CENTER);

      // 2x2 GRID OF OPTION BUTTONS
      GridPane optionsGrid = new GridPane();
      optionsGrid.setHgap(15);
      optionsGrid.setVgap(10);
      optionsGrid.setAlignment(Pos.CENTER);

      for (int i = 0; i < 4; i++) {
          final int optionIndex = i;
          Button btn = createOptionButton();
          btn.setOnAction(e -> handleAnswer(optionIndex));
          optionButtons[i] = btn;

          int row = i / 2;
          int col = i % 2;
          optionsGrid.add(btn, col, row);
      }

      VBox questionCard = new VBox(10, equationHeaderBox, optionsGrid);
      questionCard.setAlignment(Pos.TOP_CENTER);
      questionCard.setStyle("-fx-background-color: #dcdcdc; -fx-padding: 12px 25px; -fx-border-color: #b0b0b0; -fx-border-width: 2px;");
      questionCard.setPrefSize(720, 290);
      questionCard.setMaxSize(720, 290);

      // 4. TUG-OF-WAR PLATFORM
      Rectangle platformBar = new Rectangle(PLATFORM_WIDTH, BAR_HEIGHT);
      platformBar.setFill(Color.web("#888888"));

      greenBox = new Rectangle(BOX_WIDTH, BOX_HEIGHT);
      greenBox.setFill(Color.web("#00a800"));

      redBox = new Rectangle(BOX_WIDTH, BOX_HEIGHT);
      redBox.setFill(Color.web("#d00000"));

      double onTopOffset = -(BOX_HEIGHT / 2.0) - (BAR_HEIGHT / 2.0);
      greenBox.setTranslateY(onTopOffset);
      redBox.setTranslateY(onTopOffset);

      updateBoxTranslationsInstant();

      Pane platformPane = new Pane();
      platformPane.setPrefSize(PLATFORM_WIDTH, 70);
      platformPane.setMaxSize(PLATFORM_WIDTH, 70);

      platformBar.layoutXProperty().bind(platformPane.widthProperty().subtract(platformBar.widthProperty()).divide(2));
      platformBar.layoutYProperty().bind(platformPane.heightProperty().subtract(platformBar.heightProperty()).divide(2));

      greenBox.layoutXProperty().bind(platformPane.widthProperty().divide(2));
      greenBox.layoutYProperty().bind(platformPane.heightProperty().divide(2));

      redBox.layoutXProperty().bind(platformPane.widthProperty().divide(2));
      redBox.layoutYProperty().bind(platformPane.heightProperty().divide(2));

      platformPane.getChildren().addAll(platformBar, greenBox, redBox);

      VBox centerContent = new VBox(15, questionCard, platformPane);
      centerContent.setAlignment(Pos.CENTER);

      this.setTop(topBar);
      this.setCenter(centerContent);
      this.setBottom(bottomBar);

      initQuestionBank(selectedCategory);
      displayNextQuestion();
  }

  private Button createGearButton() {
      Button gearBtn = new Button("\u2699");
      gearBtn.setPrefSize(42, 42);
      gearBtn.setStyle(
          "-fx-font-size: 20px; " +
          "-fx-font-weight: bold; " +
          "-fx-border-color: #000000; " +
          "-fx-border-width: 2px; " +
          "-fx-border-radius: 50%; " +
          "-fx-background-radius: 50%; " +
          "-fx-background-color: #f9f9f9; " +
          "-fx-cursor: hand;"
      );
      return gearBtn;
  }

  private Button createOptionButton() {
      Button button = new Button();
      button.setPrefWidth(320);
      button.setPrefHeight(42);
      button.setStyle(
          "-fx-font-size: 14px; " +
          "-fx-font-weight: bold; " +
          "-fx-background-color: #ffffff; " +
          "-fx-text-fill: #000000; " +
          "-fx-border-color: #222222; " +
          "-fx-border-width: 2px; " +
          "-fx-cursor: hand;"
      );
      button.setOnMouseEntered(e -> {
          if (!button.isDisabled()) {
              button.setStyle(
                  "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #e0e0e0; -fx-text-fill: #000000; -fx-border-color: #222222; -fx-border-width: 2px; -fx-cursor: hand;"
              );
          }
      });
      button.setOnMouseExited(e -> {
          if (!button.isDisabled()) {
              button.setStyle(
                  "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-border-color: #222222; -fx-border-width: 2px; -fx-cursor: hand;"
              );
          }
      });
      return button;
  }

  private void initQuestionBank(String categoryFilter) {
      questionQueue = new ArrayList<>();
      
      for (String[] row : LOCAL_MATH_QUESTIONS) {
          String cat = row[1];
          String diff = row[2];
          String prompt = row[3];
          String[] options = Arrays.copyOfRange(row, 4, 8);

          questionQueue.add(new Question(cat, diff, prompt, options));
      }

      Collections.shuffle(questionQueue);
  }

  private void displayNextQuestion() {
      if (questionQueue.isEmpty()) return;

      if (currentQuestionIndex >= questionQueue.size()) {
          Collections.shuffle(questionQueue);
          currentQuestionIndex = 0;
      }

      currentQuestion = questionQueue.get(currentQuestionIndex++);
      categoryLabel.setText(currentQuestion.category.toUpperCase() + " (" + currentQuestion.difficulty + ")");
      equationLabel.setText(currentQuestion.prompt);

      questionScrollPane.setVvalue(0.0);

      char prefix = 'A';
      for (int i = 0; i < 4; i++) {
          optionButtons[i].setText((char)(prefix + i) + ".  " + currentQuestion.options.get(i));
          optionButtons[i].setDisable(false);
          optionButtons[i].setStyle(
              "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-border-color: #222222; -fx-border-width: 2px; -fx-cursor: hand;"
          );
      }

      questionStartTime = System.currentTimeMillis();
  }

  private void handleAnswer(int selectedOptionIndex) {
      for (Button btn : optionButtons) {
          btn.setDisable(true);
      }

      long elapsedMs = System.currentTimeMillis() - questionStartTime;
      double elapsedSeconds = elapsedMs / 1000.0;

      double speedBonus = 0.0;
      if (elapsedSeconds >= 1.0 && elapsedSeconds <= 2.0) {
          speedBonus = 0.25;
      } else if (elapsedSeconds > 2.0 && elapsedSeconds <= 4.0) {
          speedBonus = 0.05;
      }

      boolean isCorrect = (selectedOptionIndex == currentQuestion.correctIndex);
      double actualStep = BASE_STEP_DISTANCE * (1.0 + speedBonus);

      if (isCorrect) {
          totalScoreMultiplier += speedBonus;
          greenX += actualStep;
          redX += actualStep;
      } else {
          greenX -= actualStep;
          redX -= actualStep;
      }

      animatePlatformMovement(() -> checkGameConditions());
  }

  private void animatePlatformMovement(Runnable onComplete) {
      TranslateTransition greenAnim = new TranslateTransition(Duration.millis(350), greenBox);
      greenAnim.setToX(greenX);
      greenAnim.setInterpolator(Interpolator.EASE_OUT);

      TranslateTransition redAnim = new TranslateTransition(Duration.millis(350), redBox);
      redAnim.setToX(redX);
      redAnim.setInterpolator(Interpolator.EASE_OUT);

      ParallelTransition syncAnim = new ParallelTransition(greenAnim, redAnim);
      syncAnim.setOnFinished(e -> {
          if (onComplete != null) onComplete.run();
      });
      syncAnim.play();
  }

  private void checkGameConditions() {
      double leftPlatformEdge = -PLATFORM_WIDTH / 2.0;
      double rightPlatformEdge = PLATFORM_WIDTH / 2.0;

      if (greenX < leftPlatformEdge) {
          if (onDefeat != null) onDefeat.run();
          return;
      }

      if (redX + BOX_WIDTH > rightPlatformEdge) {
          if (onVictory != null) onVictory.run();
          return;
      }

      displayNextQuestion();
  }

  private void updateBoxTranslationsInstant() {
      greenBox.setTranslateX(greenX);
      redBox.setTranslateX(redX);
  }
}