package view;

//NOTE RANKINGS ARE HARDCODED SO THIS WILL BE CHANGED ONCE PROJECT IS FINALLIZED
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardScreenView {

  public static void main(String[] args) {
      Application.launch(LeaderboardApp.class, args);
  }

  public static class LeaderboardApp extends Application {
      @Override
      public void start(Stage stage) {
          LeaderboardScreen leaderboardScreen = new LeaderboardScreen(
              () -> showPopup(stage, "NAVIGATION", "Returned to Previous Screen."),
              () -> showPopup(stage, "OPTIONS", "Opened Options / Settings.")
          );

          // 800 x 500 default window size
          Scene scene = new Scene(leaderboardScreen, 800, 500);
          stage.setTitle("Duelo Matematica - Leaderboard");
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
* Leaderboard Screen matching the UI wireframe spec:
* - Centered gray leaderboard card
* - Columns: RANK | USER | TIER
* - Left-side vertical scrollbar
* - 10-character limit on usernames with '...' truncation
* - Bottom-left gear button & centered bottom BACK button
*/
class LeaderboardScreen extends BorderPane {

  public static class LeaderboardEntry {
      int rank;
      String user;
      String tier;

      public LeaderboardEntry(int rank, String user, String tier) {
          this.rank = rank;
          this.user = user;
          this.tier = tier;
      }
  }

  private final VBox entryListVBox;
  private final Runnable onBack;
  private final Runnable onOptions;

  public LeaderboardScreen(Runnable onBack, Runnable onOptions) {
      this(onBack, onOptions, getDummyLeaderboardData());
  }

  public LeaderboardScreen(Runnable onBack, Runnable onOptions, List<LeaderboardEntry> entries) {
      this.onBack = onBack;
      this.onOptions = onOptions;

      this.setStyle("-fx-background-color: #ffffff;");

      // 1. CENTER CARD (GRAY LEADERBOARD CONTAINER)
      Label titleLabel = new Label("LEADERBOARD");
      titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #000000;");

      // Header Row (RANK - USER - TIER)
      HBox headerRow = createHeaderRow();

      // Entry List Container
      entryListVBox = new VBox(12);
      entryListVBox.setAlignment(Pos.TOP_CENTER);

      // ScrollPane with Left-Side Scrollbar Transform
      ScrollPane scrollPane = new ScrollPane(entryListVBox);
      scrollPane.setFitToWidth(true);
      scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
      scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
      scrollPane.setStyle(
          "-fx-background-color: transparent; " +
          "-fx-background: transparent; " +
          "-fx-viewport-background: transparent; " +
          "-fx-padding: 0 0 0 10px;"
      );

      // Flip scrollpane horizontally to place scrollbar on left side
      scrollPane.setScaleX(-1);
      entryListVBox.setScaleX(-1);

      VBox leaderboardCard = new VBox(18, titleLabel, headerRow, scrollPane);
      leaderboardCard.setAlignment(Pos.TOP_CENTER);
      leaderboardCard.setStyle("-fx-background-color: #d0d0d0; -fx-padding: 25px 30px;");
      leaderboardCard.setPrefSize(440, 360);
      leaderboardCard.setMaxSize(440, 360);

      // 2. BOTTOM BAR (Gear Icon on Left + Centered BACK Button)
      Button gearButton = createGearButton();
      gearButton.setOnAction(e -> {
          if (onOptions != null) onOptions.run();
      });

      Button backButton = new Button("BACK");
      backButton.setPrefSize(130, 42);
      backButton.setStyle(
          "-fx-font-size: 16px; " +
          "-fx-font-weight: bold; " +
          "-fx-background-color: #d0d0d0; " +
          "-fx-text-fill: #000000; " +
          "-fx-border-color: #000000; " +
          "-fx-border-width: 0px; " +
          "-fx-cursor: hand;"
      );
      backButton.setOnAction(e -> {
          if (onBack != null) onBack.run();
      });

      // Left gear container
      HBox leftContainer = new HBox(gearButton);
      leftContainer.setAlignment(Pos.BOTTOM_LEFT);

      // Center BACK button container
      HBox centerContainer = new HBox(backButton);
      centerContainer.setAlignment(Pos.BOTTOM_CENTER);

      // Spacer to balance layout and keep BACK perfectly centered
      Region rightSpacer = new Region();
      rightSpacer.setPrefWidth(42);

      BorderPane bottomBar = new BorderPane();
      bottomBar.setLeft(leftContainer);
      bottomBar.setCenter(centerContainer);
      bottomBar.setRight(rightSpacer);
      bottomBar.setPadding(new Insets(0, 15, 15, 15));

      VBox centerLayout = new VBox(leaderboardCard);
      centerLayout.setAlignment(Pos.CENTER);

      this.setCenter(centerLayout);
      this.setBottom(bottomBar);

      // Load data into leaderboard
      updateLeaderboard(entries);
  }

  /**
   * Dynamically updates the leaderboard entry list.
   */
  public void updateLeaderboard(List<LeaderboardEntry> entries) {
      entryListVBox.getChildren().clear();
      if (entries == null) return;

      for (LeaderboardEntry entry : entries) {
          HBox row = createDataRow(
              entry.rank + ".",
              formatName(entry.user),
              entry.tier
          );
          entryListVBox.getChildren().add(row);
      }
  }

  /**
   * Hard limit of 10 characters for usernames before appending '...'.
   */
  private String formatName(String name) {
      if (name == null) return "";
      if (name.length() > 10) {
          return name.substring(0, 10) + "...";
      }
      return name;
  }

  private HBox createHeaderRow() {
      HBox header = new HBox();
      header.setAlignment(Pos.CENTER);
      header.setPadding(new Insets(0, 10, 0, 10));

      Label rankHeader = new Label("RANK");
      rankHeader.setPrefWidth(90);
      rankHeader.setAlignment(Pos.CENTER_LEFT);
      rankHeader.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #000000;");

      Label userHeader = new Label("USER");
      userHeader.setPrefWidth(160);
      userHeader.setAlignment(Pos.CENTER);
      userHeader.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #000000;");

      Label tierHeader = new Label("TIER");
      tierHeader.setPrefWidth(110);
      tierHeader.setAlignment(Pos.CENTER_RIGHT);
      tierHeader.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #000000;");

      header.getChildren().addAll(rankHeader, userHeader, tierHeader);
      return header;
  }

  private HBox createDataRow(String rankText, String userText, String tierText) {
      HBox row = new HBox();
      row.setAlignment(Pos.CENTER);
      row.setPadding(new Insets(2, 10, 2, 10));

      Label rankLbl = new Label(rankText);
      rankLbl.setPrefWidth(90);
      rankLbl.setAlignment(Pos.CENTER_LEFT);
      rankLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #000000;");

      Label userLbl = new Label(userText);
      userLbl.setPrefWidth(160);
      userLbl.setAlignment(Pos.CENTER);
      userLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #000000;");

      Label tierLbl = new Label(tierText);
      tierLbl.setPrefWidth(110);
      tierLbl.setAlignment(Pos.CENTER_RIGHT);
      tierLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #000000;");

      row.getChildren().addAll(rankLbl, userLbl, tierLbl);
      return row;
  }

  private Button createGearButton() {
      Button gearBtn = new Button("\u2699");
      gearBtn.setPrefSize(42, 42);
      gearBtn.setStyle(
          "-fx-font-size: 20px; " +
          "-fx-font-weight: bold; " +
          "-fx-border-color: #000000; " +
          "-fx-border-width: 2px; " +
          "-fx-background-color: #ffffff; " +
          "-fx-cursor: hand;"
      );
      return gearBtn;
  }

  private static List<LeaderboardEntry> getDummyLeaderboardData() {
      List<LeaderboardEntry> data = new ArrayList<>();
      data.add(new LeaderboardEntry(1, "CIRE", "ROYAL"));
      data.add(new LeaderboardEntry(2, "MathWizard99", "DIAMOND"));  // Truncates to: MathWizard9...
      data.add(new LeaderboardEntry(3, "EulerKing", "GOLD"));
      data.add(new LeaderboardEntry(4, "GaussProExtra", "GOLD"));    // Truncates to: GaussProExt...
      data.add(new LeaderboardEntry(5, "LogicMaster", "SILVER"));   // Truncates to: LogicMaste...
      data.add(new LeaderboardEntry(6, "StatGenius", "SILVER"));
      data.add(new LeaderboardEntry(7, "DerivDemon", "BRONZE"));
      data.add(new LeaderboardEntry(8, "IntegralHero", "BRONZE"));  // Truncates to: IntegralHe...
      data.add(new LeaderboardEntry(9, "NewtonApprentice", "BRONZE")); // Truncates to: NewtonAppr...
      return data;
  }
}