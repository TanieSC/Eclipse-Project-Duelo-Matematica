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
            if (!Session.isLoggedIn()) {
                goToLogin();
            } else if (Session.hasSavedGame()) {
                goToResume();
            } else {
                goToTopicSelection();
            }
        });

        view.getRegisterButton().setOnAction(e -> goToRegister());
    }

    @Override
    public void goToMenu() {
        view.showMenu();
        if (Session.isLoggedIn()) {
            view.getLoginButton().setText(
                "Logged in: " + Session.getCurrentUser().getUsername()
            );
        } else {
            view.getLoginButton().setText("Login");
        }
    }

    @Override
    public void goToLogin() {
        LoginView v = new LoginView();
        new LoginController(v, authService, this);
        view.setContent(v.getRoot());
    }

    @Override
    public void goToRegister() {
        RegisterView v = new RegisterView();
        new RegisterController(v, authService, this);
        view.setContent(v.getRoot());
    }

    @Override
    public void goToForgotPassword() {
        ForgotPassView v = new ForgotPassView();
        new ForgotPassController(v, authService, this);
        view.setContent(v.getRoot());
    }

    @Override
    public void goToTopicSelection() {
        TopicView v = new TopicView();
        new TopicController(v, this);
        view.setContent(v.getRoot());
    }

    @Override
    public void goToResume() {
        if (!Session.hasSavedGame()) {
            goToTopicSelection();
            return;
        }
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
        if (service == null) {
            goToTopicSelection();
            return;
        }
        GameView v = new GameView();
        new GameController(v, service, this, true);
        view.setContent(v.getRoot());
    }
}