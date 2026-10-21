package controller;

import model.AuthService;
import model.GameService;
import model.MathQuestions;
import model.Session;
import view.*;

public class MainMenuController implements Navigator {

    private final MainMenuView view;
    private final AuthService authService;

    public MainMenuController(MainMenuView view, AuthService authService) {
        this.view = view;
        this.authService = authService;

        view.getLoginButton().setOnAction(e -> {
            if (!Session.isLoggedIn()) goToLogin();
            else goToGameMainMenu();
        });
        
        view.getRegisterButton().setOnAction(e -> goToRegister());
        view.getExitButton().setOnAction(e -> System.exit(0));
    }

    @Override
    public void goToMenu() {
        view.showMenu();
        if (Session.isLoggedIn()) {
            view.getLoginButton().setText("Logged in: " + Session.getCurrentUser().getUsername());
        } else {
            view.getLoginButton().setText("LOGIN");
        }
    }

    // ==========================================
    // OVERLAY VIEWS (Menu stays behind)
    // ==========================================

    @Override
    public void goToLogin() {
        LoginView v = new LoginView();
        new LoginController(v, authService, this);
        view.setOverlayContent(v.getRoot()); // <--- OVERLAY
    }

    @Override
    public void goToRegister() {
        RegisterView v = new RegisterView();
        new RegisterController(v, authService, this);
        view.setOverlayContent(v.getRoot()); // <--- OVERLAY
    }

    @Override
    public void goToForgotPassword() {
        ForgotPassView v = new ForgotPassView();
        new ForgotPassController(v, authService, this);
        view.setOverlayContent(v.getRoot()); // <--- OVERLAY
    }

    @Override
    public void goToNewPass(String email) {
        NewPassView v = new NewPassView(email);
        new NewPassController(v, authService, email, this);
        view.setOverlayContent(v.getRoot()); // <--- OVERLAY
    }

    @Override
    public void goToUserProfile() {
        // Get the current username from the session, or default to "Guest"
        String username = "Guest";
        if (model.Session.getCurrentUser() != null) {
            username = model.Session.getCurrentUser().getUsername();
        }

        // Create the profile model (Using dummy stats for now)
        model.UserProfileModel model = new model.UserProfileModel(username, "Gold", 1500);
        
        // Build view and controller
        UserProfileView v = new UserProfileView(model);
        new UserProfileController(v, this);
        
        // Use showPopup instead of setContent!
        view.showPopup(v.getRoot());
    }
    
    @Override
    public void goToCredits() {
        CreditsView v = new CreditsView();
        new CreditsController(v, this);
        
        // Show it as a popup over the current screen
        view.showPopup(v.getRoot());
    }
    
    @Override
    public void goToLeaderboard() {
        // 1. Create the Model
        model.LeaderBoardModel model = new model.LeaderBoardModel();
        
        // 2. Create the View (passing the model)
        LeaderBoardView v = new LeaderBoardView(model);
        
        // 3. Wire the Controller
        new LeaderBoardController(v, this);
        
        // 4. Show it as a popup
        view.showPopup(v.getRoot());
    }
    
    @Override
    public void goToSettings() {
        // 1. Build the inline settings view
        GameSettingsView v = new GameSettingsView();
        
        // 2. Create the controller (we pass 'this' so it can navigate back)
        new GameSettingsController(v, this);
        
        // 3. Show it in the main window
        // Note: Since GameSettingsView has its own dark background overlay, 
        // we use setContent instead of setOverlayContent.
        view.showPopup(v.getRoot()); 
    }
    
    // ==========================================
    // FULL SCREEN VIEWS (Menu is hidden)
    // ==========================================

    @Override
    public void goToGameMainMenu() {
        model.GameMenuModel model = new model.GameMenuModel("MAIN MENU");
        GameMainMenuView v = new GameMainMenuView(model.getTitle());
        new GameMainMenuController(v, this);
        view.setContent(v.getRoot()); // <--- FULL SCREEN
    }

    @Override
    public void goToGameStartMenu() {
        model.GameStartMenuModel model = new model.GameStartMenuModel("SELECT MODE");
        GameStartMenuView v = new GameStartMenuView(model.getTitle());
        new GameStartMenuController(v, this);
        view.setContent(v.getRoot()); // <--- FULL SCREEN
    }

    @Override
    public void goToTopicSelection() {
        TopicView v = new TopicView();
        new TopicController(v, this);
        view.setContent(v.getRoot());
    }

    
    @Override
    public void goToResume() {
        if (!Session.hasSavedGame()) { goToTopicSelection(); return; }
        ResumeView v = new ResumeView(Session.getSavedGame());
        new ResumeController(v, this);
        view.setContent(v.getRoot());
    }

    @Override
    public void goToGame(String category) {
        GameService service = new GameService(MathQuestions.loadAll(), category);
        GameView v = new GameView();
        new GameController(v, service, this, false);
        view.setContent(v.getRoot());
    }

    @Override
    public void resumeGame() {
        GameService service = Session.getSavedGame();
        if (service == null) { goToTopicSelection(); return; }
        GameView v = new GameView();
        new GameController(v, service, this, true);
        view.setContent(v.getRoot());
    }
    
    @Override
    public void goTo1v1Game() {
        model.Game1v1Model model = new model.Game1v1Model("All");
        view.Game1v1View v = new view.Game1v1View();
        new controller.Game1v1Controller(v, model, this);
        view.setContent(v.getRoot());
    }
    
    @Override
    public void closePopup() {
        view.closePopup();
    }
}