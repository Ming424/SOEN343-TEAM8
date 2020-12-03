package controller;

import entity.ConsoleComponents;
import entity.Room;
import entity.UserRole;
import entity.Window;
import interfaces.SubController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import observerPattern.Subject;
import observerPattern.UserLocationObserver;
import org.apache.commons.lang3.StringUtils;
import service.RoleService;
import java.io.IOException;
import java.net.URL;
import java.time.Month;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Controller responsible for the editing of user location
 */
public class EditSimulationController implements Initializable, SubController {
    /**
     * declaring variables
     */
    @FXML
    private ComboBox<String> roomsMove;
    @FXML
    private ComboBox<String> roomsBlock;
    @FXML
    private ComboBox<String> windows;
    @FXML
    private Label userToMove;
    @FXML
    private Label windowToBlock;
    @FXML
    private AnchorPane locationDisplay;
    @FXML
    private TextArea windowNote;
    @FXML
    private Label windowBlockStatus;
    @FXML
    private Button button;
    @FXML
    private ComboBox<String> summerMonthStart;
    @FXML
    private ComboBox<Integer> summerDayStart;
    @FXML
    private ComboBox<String> winterMonthStart;
    @FXML
    private ComboBox<Integer> winterDayStart;
    @FXML
    private ComboBox<String> summerMonthEnd;
    @FXML
    private ComboBox<Integer> summerDayEnd;
    @FXML
    private ComboBox<String> winterMonthEnd;
    @FXML
    private ComboBox<Integer> winterDayEnd;

    private Map<String, Room> house;
    private String username;
    private double xOffset = 0;
    private double yOffset = 0;
    private static Room room;

    private final ObservableList<String> li_month = FXCollections.observableArrayList(
            "January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December");

    private static Map<String, String> userLocations;

    private static Integer summerMonthStartCache;
    private static Integer winterMonthStartCache;
    private static Integer summerDayStartCache;
    private static Integer winterDayStartCache;


    private static Integer summerMonthEndCache;
    private static Integer winterMonthEndCache;
    private static Integer summerDayEndCache;
    private static Integer winterDayEndCache;

    static {
        summerMonthStartCache = 6;
        winterMonthStartCache = 0;
        summerDayStartCache = 1;
        winterDayStartCache = 1;
        summerMonthEndCache = 11;
        winterMonthEndCache = 5;
        summerDayEndCache = 31;
        winterDayEndCache = 31;
    }

    public static Map<String, String> getUserLocations() {
        return userLocations;
    }

    /**
     * This function loads the login info page(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the view file cannot be read
     */
    public void goToLoginInfo(ActionEvent event) throws IOException {
        Parent loginInfo = FXMLLoader.load(getClass().getResource("/view/loginInfo.fxml"));
        Scene loginInfoScene = new Scene(loginInfo);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginInfoScene);
        window.show();
    }

    /**
     * This function will close the application
     *
     * @param event The event that called this function
     */
    public void close(MouseEvent event) {
        System.exit(0);
    }

    /**
     * Gets the location of a mouse.
     *
     * @param event The event triggering this function
     */
    public void getLocation(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    /**
     * Changes the location of the window(stage) based on the mouse location..
     *
     * @param event The event that called this function
     */
    public void move(MouseEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setX(event.getScreenX() - xOffset);
        window.setY(event.getScreenY() - yOffset);
    }

    /**
     * This function will add the rooms to the combobox
     *
     * @param location  The URL of the resource file
     * @param resources The set of resources used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        username = LoginInfoController.getUsername();
        userToMove.setText("Move " + username + " to:");
        house = LoginInfoController.getHouse();
        if (Objects.nonNull(house)) {
            roomsMove.getItems().addAll(house.keySet());
            roomsMove.getItems().add("Outside");
            roomsMove.getSelectionModel().select("Outside");
        } else {
            roomsMove.getItems().add("Unknown");
        }
        locationDisplay.getChildren().add(processRows());

        windowToBlock.setText("Select Window: ");
        if (Objects.nonNull(house)) {
            roomsBlock.getItems().addAll(house.keySet());
            roomsBlock.getSelectionModel().selectFirst();

            roomsBlock.setOnAction(event -> {
                String roomName = roomsBlock.getValue();
                room = house.get(roomName);
                windows.getItems().clear();
                for (Window window : room.getWindows()) {
                    windows.getItems().add(window.getPosition().toString());
                }
            });
            windows.getSelectionModel().selectFirst();
        }
        setComboBoxValue();
    }

    /**
     * This function will return a list containing blocking status of windows
     *
     * @param room the room passed to function
     * @return list of blocked status of windows
     */
    public String[] windowsList(Room room) {
        String[] list = new String[room.getWindows().size()];
        for (int i = 0; i < room.getWindows().size(); i++) {
            if (room.getWindows().get(i).getBlocking()) {
                list[i] = room.getWindows().get(i).getPosition().toString() + ": " + "true";
            } else {
                list[i] = room.getWindows().get(i).getPosition().toString() + ": " + "false";
            }
        }
        return list;
    }

    /**
     * Function responsible for unblocking/blocking windows
     *
     * @param event The event that called this function
     */
    public void windowsBlocked(ActionEvent event) {
    	int selectedWindow = 0;
        for (int i = 0; i < room.getWindows().size(); i++) {
            if (room.getWindows().get(i).getPosition().toString().equals(windows.getValue())) {
                selectedWindow = i;
                String message = "";
                if(!room.getWindows().get(selectedWindow).getBlocking()) {
                	room.getWindows().get(selectedWindow).setBlocking(true);
                	message = "The window at the " + windows.getValue() + " in the " + room.getName() + " has been blocked.\n";
                	updateWindowBlockStatus(true);
                } else {
                	room.getWindows().get(selectedWindow).setBlocking(false);
                	message = "The window at the " + windows.getValue() + " in the " + room.getName() + " has been unblocked.\n";
                	updateWindowBlockStatus(false);
                }
                String log = windowNote.getText();
                windowNote.setText(log + message);
                LoginInfoController.consoleLogFile("The window at the " + windows.getValue() + " in the " + room.getName() + " has been blocked.", ConsoleComponents.SHS);
                break;
            }
        }
    }

    /**
     * Action event on change dynamic content with combobox for window list
     * @param event The event that called this function
     */
    public void cb_onWindowChange(ActionEvent event) {
    	int selectedWindow = 0;
        for (int i = 0; i < room.getWindows().size(); i++) {
            if (room.getWindows().get(i).getPosition().toString().equals(windows.getValue())) {
            	selectedWindow = i;
            	updateWindowBlockStatus(room.getWindows().get(selectedWindow).getBlocking());
        	}
        }
    }
    
    /**
     * Action event on change combobox for room list
     * @param event The event that called this function
     */
    public void cb_selectWindowOnChange(MouseEvent event) {
    	this.windowBlockStatus.setText(" ");
    	this.button.setText("Action");
    }
    
    /**
     * Update windows status label
     * @param bool The status of windows (blocked/unblocked)
     */
    public void updateWindowBlockStatus(boolean bool) {
    	this.windowBlockStatus.setText(bool ? "Blocked" : " ");
    	this.button.setText(bool? "Unblock" : "Block");
    }

    /**
     * Method that will create the grid placed in the {@link AnchorPane}.
     * Also calls the {@link RoleService} to obtain the Users and Roles.
     *
     * @return The grid pane used by the display
     */
    private GridPane processRows() {
        AtomicInteger index = new AtomicInteger();
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        if (Objects.isNull(userLocations)) {
            userLocations = RoleService.getRoles(LoginInfoController.getUserParent()).stream().collect(Collectors.toMap(
                    UserRole::getUsername,
                    userRole -> "Outside"
            ));
        }
        userLocations.forEach((key, value) -> {
            gridPane.addRow(gridPane.getRowCount(), createUserLabel(key, key), createUserLabel("Location:", ""), createUserLabel(value, key + "Location"));
            index.getAndIncrement();
        });
        return gridPane;
    }

    /**
     * Creates a Label with the username
     *
     * @param value The value placed in the label
     * @param id    The value used for the label's ID
     * @return The label with the value and ID
     */
    private Node createUserLabel(final String value, final String id) {
        Label userLabel = new Label();
        userLabel.setMinWidth(100);
        userLabel.setId(id);
        userLabel.setText(value);
        return userLabel;
    }

    /**
     * Function responsible for updating the user's location
     *
     * @param event The event that called this function
     */
    public void changeLocation(ActionEvent event) {
        String chosenLocation = roomsMove.getSelectionModel().getSelectedItem();
        if (StringUtils.isNotEmpty(chosenLocation)) {
            userLocations.put(username, chosenLocation);
            if (locationDisplay.lookup("#" + username + "Location") != null) {
                ((Label) locationDisplay.lookup("#" + username + "Location")).setText(chosenLocation);
            }
            Subject subject = new Subject();
            new UserLocationObserver(subject);
            subject.setUserLocations(username, chosenLocation);
            subject.notifyObserver();
            LoginInfoController.consoleLogFile("Moved " + username + " to " + chosenLocation, ConsoleComponents.SHS);
            windowNote.appendText("Moved " + username + " to " + chosenLocation+"\n");
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Location is empty");
            alert.showAndWait();
        }
    }

    /**
     * Get the roomsMove ComboBox
     *
     * @return roomsMove
     */
    public ComboBox getRoomsMove(){
        return roomsMove;
    }

    /**
     * Deletes the location cached variable
     */
    public static void deleteLocations() {
        userLocations = null;
    }

    /**
     * Sets the username for the user to move
     *
     * @param username The username to set it to
     */
    protected void setUsername(String username) {
        this.username = username;
    }

    /**
     * This function assigns the proper date range to List
     */
    private ObservableList<Integer> getDateList(ComboBox<String> monthBox) {
        int length;
        int month = Month.valueOf(monthBox.getValue().toUpperCase()).getValue();
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            length = 30;
        } else if (month == 2) {
            length = 29;
        } else {
            length = 31;
        }
        Integer[] arr = IntStream.of(IntStream.range(1, length + 1).toArray()).boxed().toArray(Integer[]::new);
        return FXCollections.observableArrayList(arr);
    }

    /**
     * This function will assign items to combo box
     */
    private void setComboBoxValue() {
        winterMonthStart.setItems(li_month);
        summerMonthStart.setItems(li_month);
        winterMonthEnd.setItems(li_month);
        summerMonthEnd.setItems(li_month);

        winterMonthStart.getSelectionModel().select(winterMonthStartCache);
        summerMonthStart.getSelectionModel().select(summerMonthStartCache);
        winterMonthEnd.getSelectionModel().select(winterMonthEndCache);
        summerMonthEnd.getSelectionModel().select(summerMonthEndCache);

        updateMonthDays();
    }

    private void updateMonthDays() {
        ObservableList<Integer> listOfDaySummerStart = getDateList(summerMonthStart);
        ObservableList<Integer> listOfDayWinterStart = getDateList(winterMonthStart);
        ObservableList<Integer> listOfDaySummerEnd = getDateList(summerMonthEnd);
        ObservableList<Integer> listOfDayWinterEnd = getDateList(winterMonthEnd);

        winterDayStart.setItems(listOfDayWinterStart);
        summerDayStart.setItems(listOfDaySummerStart);
        winterDayEnd.setItems(listOfDayWinterEnd);
        summerDayEnd.setItems(listOfDaySummerEnd);

        winterDayStart.getSelectionModel().select(winterDayStartCache);
        summerDayStart.getSelectionModel().select(summerDayStartCache);
        winterDayEnd.getSelectionModel().select(winterDayEndCache);
        summerDayEnd.getSelectionModel().select(summerDayEndCache);
    }

    public void onChangeSummerStartMonth() {
        int month = Month.valueOf(summerMonthStart.getSelectionModel().getSelectedItem().toUpperCase()).getValue();
        if (month > winterMonthEndCache) {
            summerMonthStartCache = month;
        } else if (!(summerDayStartCache > winterDayEndCache)) {
            String message = "Summer can only start after the end of winter";
            Alert alert = new Alert(Alert.AlertType.WARNING, message);
            alert.showAndWait();
            LoginInfoController.consoleLogFile(message, ConsoleComponents.SHH);
            summerMonthStart.getSelectionModel().select(summerMonthStartCache);
        } else {
            summerMonthStartCache = month;
        }
    }

    public void onChangeSummerEndMonth() {
        int month = Month.valueOf(summerMonthEnd.getSelectionModel().getSelectedItem().toUpperCase()).getValue();
        if (month < winterMonthStartCache) {
            summerMonthEndCache = month;
        } else if (!(summerDayEndCache < winterDayEndCache)) {
            String message = "Summer can only end before the start of winter";
            Alert alert = new Alert(Alert.AlertType.WARNING, message);
            alert.showAndWait();
            LoginInfoController.consoleLogFile(message, ConsoleComponents.SHH);
            summerMonthEnd.getSelectionModel().select(summerMonthEndCache);
        } else {
            summerMonthEndCache = month;
        }
    }

    public void onChangeSummerStartDay() {
        int day = summerDayStart.getSelectionModel().getSelectedItem();
        if (summerMonthStartCache.equals(winterMonthStartCache) && !(day > winterDayEndCache)) {
            String message = "Summer can only start after the end of winter";
            Alert alert = new Alert(Alert.AlertType.WARNING, message);
            alert.showAndWait();
            LoginInfoController.consoleLogFile(message, ConsoleComponents.SHH);
            summerDayStart.getSelectionModel().select(summerDayStartCache);
        } else {
            summerDayStartCache = day;
        }
    }

    public void onChangeSummerEndDay() {
        int day = summerDayEnd.getSelectionModel().getSelectedItem();
        if (summerMonthStartCache.equals(winterMonthStartCache) && !(day < winterDayStartCache)) {
            String message = "Summer can only end before the start of winter";
            Alert alert = new Alert(Alert.AlertType.WARNING, message);
            alert.showAndWait();
            LoginInfoController.consoleLogFile(message, ConsoleComponents.SHH);
            summerDayEnd.getSelectionModel().select(summerDayEndCache);
        } else {
            summerDayStartCache = day;
        }
    }

    public void onChangeWinterStartMonth() {
        int month = Month.valueOf(winterMonthStart.getSelectionModel().getSelectedItem().toUpperCase()).getValue();
        if (month > summerMonthEndCache) {
            winterMonthStartCache = month;
        } else if (!(winterDayEndCache > summerDayEndCache)) {
            String message = "Winter can only start after the end of Summer";
            Alert alert = new Alert(Alert.AlertType.WARNING, message);
            alert.showAndWait();
            LoginInfoController.consoleLogFile(message, ConsoleComponents.SHH);
            winterMonthStart.getSelectionModel().select(winterMonthStartCache);
        } else {
            winterMonthStartCache = month;
        }
    }

    public void onChangeWinterEndMonth() {
        int month = Month.valueOf(winterMonthEnd.getSelectionModel().getSelectedItem().toUpperCase()).getValue();
        if (month < summerMonthStartCache) {
            winterMonthEndCache = month;
        } else if (!(summerDayEndCache < winterDayEndCache)) {
            String message = "Winter can only end after the start of Summer";
            Alert alert = new Alert(Alert.AlertType.WARNING, message);
            alert.showAndWait();
            LoginInfoController.consoleLogFile(message, ConsoleComponents.SHH);
            winterMonthEnd.getSelectionModel().select(winterMonthEndCache);
        } else {
            winterMonthEndCache = month;
        }
    }

    public void onChangeWinterStartDay() {
        int day = winterDayStart.getSelectionModel().getSelectedItem();
        if (summerMonthStartCache.equals(winterMonthStartCache) && !(day > summerDayEndCache)) {
            String message = "Winter can only start after the end of summer";
            Alert alert = new Alert(Alert.AlertType.WARNING, message);
            alert.showAndWait();
            LoginInfoController.consoleLogFile(message, ConsoleComponents.SHH);
            winterDayStart.getSelectionModel().select(winterDayStartCache);
        } else {
            winterDayStartCache = day;
        }
    }

    public void onChangeWinterEndDay() {
        int day = winterDayEnd.getSelectionModel().getSelectedItem();
        if (summerMonthStartCache.equals(winterMonthStartCache) && !(day < summerDayStartCache)) {
            String message = "Winter can only end before the start of summer";
            Alert alert = new Alert(Alert.AlertType.WARNING, message);
            alert.showAndWait();
            LoginInfoController.consoleLogFile(message, ConsoleComponents.SHH);
            winterDayEnd.getSelectionModel().select(winterDayEndCache);
        } else {
            winterDayEndCache = day;
        }
    }
}
