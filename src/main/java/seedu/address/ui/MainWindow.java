package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";
    private static final KeyCombination ESCAPE_KEY = KeyCombination.valueOf("Esc");

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;
    private Path dataFilePath;

    // Independent Ui parts residing in this Ui container
    private PersonListPanel personListPanel;
    private CommandListPanel commandListPanel;
    private ParentListPanel parentListPanel;
    private SchedulePanel schedulePanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;
    private CommandBox commandBox;
    private ReminderDialog reminderDialog;
    private ReminderAlertScheduler reminderAlertScheduler;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private Menu fileMenu;

    @FXML
    private Menu userGuideMenu;

    @FXML
    private MenuItem exitMenuItem;

    @FXML
    private StackPane personListPanelPlaceholder;

    @FXML
    private StackPane parentListPanelPlaceholder;

    @FXML
    private StackPane schedulePanelPlaceholder;

    @FXML
    private StackPane commandListPanelPlaceholder;

    @FXML
    private TabPane recordTabs;

    @FXML
    private Tab helpTab;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage}, {@code Logic},
     * and the data file path to show in the status bar.
     */
    public MainWindow(Stage primaryStage, Logic logic, Path dataFilePath) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.dataFilePath = dataFilePath;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
        // The filter sits on the window's content rather than the window, so the menu bar sees Esc first
        // and can still use it to close a menu the user is already in.
        getRoot().getScene().getRoot().addEventFilter(
                KeyEvent.KEY_PRESSED, this::handleMenuShortcuts);
        getRoot().getScene().getRoot().addEventFilter(KeyEvent.KEY_PRESSED, this::handleEscape);
        fileMenu.setOnShown(event -> selectExit());
        userGuideMenu.setOnShown(event -> selectHelpMenuItem());
    }

    /**
     * Handles keyboard shortcuts for opening menus.
     * Alt + F opens the File menu.
     * Alt + U opens the User Guide menu.
     */
    private void handleMenuShortcuts(KeyEvent event) {
        if (!event.isAltDown()) {
            return;
        }
        if (event.getCode() == KeyCode.F) {
            fileMenu.show();
            event.consume();
        } else if (event.getCode() == KeyCode.U) {
            userGuideMenu.show();
            event.consume();
        }
    }

    /**
     * Opens the File menu when Esc is pressed, wherever the focus is. Lists and text boxes use Esc
     * themselves, so this is checked before the key reaches them.
     */
    void handleEscape(KeyEvent event) {
        requireNonNull(event);
        if (!ESCAPE_KEY.match(event) || fileMenu.isShowing()) {
            return;
        }
        logger.fine("Esc pressed in " + event.getTarget() + ", opening the File menu");
        event.consume();
        fileMenu.show();
    }

    /**
     * Selects Exit in the open File menu, so a keyboard user can quit by pressing Esc and then Enter.
     */
    private void selectExit() {
        assert fileMenu.isShowing() : "Exit can only be selected while the File menu is open";
        Node exitEntry = exitMenuItem.getStyleableNode();
        if (exitEntry == null) {
            logger.warning("The File menu opened without an Exit entry to select");
            return;
        }
        exitEntry.requestFocus();
        logger.fine("Opened the File menu with Exit selected");
    }

    /**
     * Selects the Open User Guide menu item when the User Guide menu is opened.
     */
    private void selectHelpMenuItem() {
        assert userGuideMenu.isShowing() : "User Guide menu must be open";
        Node helpEntry = helpMenuItem.getStyleableNode();
        if (helpEntry == null) {
            logger.warning("The User Guide menu opened without an entry to select");
            return;
        }
        helpEntry.requestFocus();
        logger.fine("Opened the User Guide menu with Open User Guide selected");
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        assert menuItem.getOnAction() != null : "A menu item with a shortcut must have an action to run";
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in a later version of the SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField and
         * ResultDisplay contains a TextArea, some accelerators (e.g., F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add the following event filter to capture such key events and open
         * the help window purposely so as to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        personListPanel = new PersonListPanel(logic.getFilteredPersonList());
        personListPanelPlaceholder.getChildren().add(personListPanel.getRoot());
        commandListPanel = new CommandListPanel(HelpCommand.COMMAND_SUMMARIES);
        commandListPanelPlaceholder.getChildren().add(commandListPanel.getRoot());
        parentListPanel = new ParentListPanel(logic.getParentContactList(), logic);
        parentListPanelPlaceholder.getChildren().add(parentListPanel.getRoot());

        schedulePanel = new SchedulePanel(logic.getScheduleList(), logic.getPersonList(),
                logic.getFilteredPersonList());
        schedulePanelPlaceholder.getChildren().add(schedulePanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(dataFilePath);
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        reminderDialog = new ReminderDialog(primaryStage, logic.getReminderList(), logic.getClock(),
                commandBox::requestFocus);
        reminderDialog.showIfNeeded();
        reminderAlertScheduler = new ReminderAlertScheduler(logic::getReminderList, this::showDueReminders,
                logic.getClock());
        reminderAlertScheduler.start();
        primaryStage.setOnHidden(event -> reminderAlertScheduler.stop());
    }

    private boolean showDueReminders(java.util.List<seedu.address.model.reminder.Reminder> reminders) {
        if (reminderDialog != null && reminderDialog.isShowing()) {
            return false;
        }
        reminderDialog = new ReminderDialog(primaryStage, reminders, logic.getClock(), "Reminders due now",
                commandBox::requestFocus);
        return reminderDialog.showIfNeeded();
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    /**
     * Switches to the tab that lists every command.
     */
    private void showHelpTab() {
        assert helpTab != null : "Help tab was not injected from MainWindow.fxml";
        assert recordTabs.getTabs().contains(helpTab) : "Help tab must be one of the record tabs";
        logger.fine("Showing the Help tab.");
        recordTabs.getSelectionModel().select(helpTab);
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        if (reminderAlertScheduler != null) {
            reminderAlertScheduler.stop();
        }
        primaryStage.hide();
    }

    public PersonListPanel getPersonListPanel() {
        return personListPanel;
    }

    /**
     * Switches to the Students tab and selects the student with {@code studentId}.
     */
    private void selectStudent(String studentId) {
        recordTabs.getSelectionModel().select(0);
        personListPanel.selectStudent(studentId);
        personListPanel.setKeyword(null);
        logic.resetPersonList();
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            selectTabForCommand(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            personListPanel.setKeyword(commandResult.getKeyword());
            parentListPanel.setKeyword(commandResult.getKeyword());
            commandResult.getStudentIdToShow().ifPresent(this::selectStudent);

            if (commandResult.isShowHelp()) {
                showHelpTab();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }

    /** Applies tab navigation only after a command has executed successfully. */
    void selectTabForCommand(String commandText) {
        String word = commandText.trim().split("\\s+", 2)[0];
        if (word.equals("list-schedule") || word.equals("mark-attendance")) {
            recordTabs.getSelectionModel().select(2);
        } else if (word.startsWith("add-parent") || word.equals("list-parents")
                || word.equals("edit-parent") || word.startsWith("delete-parent")) {
            recordTabs.getSelectionModel().select(1);
        } else if (word.startsWith("list")) {
            recordTabs.getSelectionModel().select(0);
        }
    }
}
