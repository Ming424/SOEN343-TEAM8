package controller;

import constants.Position;
import entity.Door;
import entity.Room;
import entity.Window;
import javafx.animation.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import service.HouseLayoutService;
import service.RoleService;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

public class LoginInfoController implements Initializable {

    /**
     * declaring variables
     */
    @FXML
    private Label user;
    @FXML
    private Label date;
    @FXML
    private Canvas houseRender;
    @FXML
    private Label time;
    @FXML
    private Hyperlink loc;
    @FXML
    private TextArea console;
    @FXML
    private ComboBox<String> selectedUser;
    @FXML
    private Label userRole;
    @FXML
    private AnchorPane anchorToggle;

    private static String userParent;
    private static Map<String, Room> house;
    private static Room[] roomArray;
    private static String username;
    private Text toggleText = new Text();

    private GraphicsContext gc;
    private double xOffset = 0;
    private double yOffset = 0;
    private final int ROOM_SIZE = 90;
    private final int DOOR_SIZE = ROOM_SIZE - 66;
    private static long timeInMillis;
    private static Timeline clock;
    private static boolean firstLaunch = true;

    /**
     * Sets up the logged in user as the active user
     *
     * @param userParent The active user's username
     */
    public void setSelectedUser(String userParent) {
        selectedUser.getSelectionModel().select(userParent);
        username = userParent;
        setupCurrentUser();
    }

    /**
     * Function to set the user's location
     *
     * @param place the String that is the name of the location that will be passed to this function.
     */
    public void setLoc(String place) {
        loc.setText(place);
    }

    /**
     * Function to set the user
     *
     * @param username the username that will be passed to this function.
     */
    public void setUser(String username) {
        user.setText(username);
    }

    /**
     * Function to set the date
     *
     * @param dateTime the time and date that will be passed to this function
     */
    public void setDate(String dateTime) {
        date.setText(dateTime);
    }

    /**
     * Function to setting new time label
     *
     * @param dateTime the time and date that will be passed to this function
     */
    public void setTime(String dateTime) {
        SimpleDateFormat formatFull = new SimpleDateFormat("yyyy - MMMM - dd HH:mm:ss");
        try {
            Date d = formatFull.parse(dateTime);
            this.timeInMillis = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Function return the text for date label
     */
    public String getDate() { 
    	return this.date.getText(); 
    }

    /**
     * Function return the AnchorPane
     */
    public AnchorPane getAnc() {
        return anchorToggle;
    }

    /**
     * Function return the text for time label
     */
    public String getTime() { 
    	return this.time.getText(); 
    }

    public static void setUserParent(String userParent) {
        LoginInfoController.userParent = userParent;
    }

    public static String getUserParent() {
        return userParent;
    }

    public static String getUsername() {
        return username;
    }

    /**
     * Animation controller for the clock
     */
    private void moveClock() {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy - MMMM - dd");
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        this.timeInMillis += 1000;
        cal.setTimeInMillis(timeInMillis);
        this.date.setText(formatDate.format(cal.getTime()));
        this.time.setText(formatTime.format(cal.getTime()));
    }

    /**
     * Function from interface Initializable, initialize default actions
     *
     * @param location, resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	// any action at the first initialization
    	if(this.firstLaunch == true) {
    		this.firstLaunch = false;
    		
    		ChangeDateTimeController.setParentController(this);
    		
			SimpleDateFormat formatDate = new SimpleDateFormat("yyyy - MMMM - dd");
	        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
	        long sysmillis = System.currentTimeMillis();
	        this.timeInMillis = sysmillis;
	        Date d = new Date(sysmillis);
	        this.date.setText(formatDate.format(d));
	        this.time.setText(formatTime.format(d));
	        
    	}
    	
        // Clock animation
    	if(clock != null) clock.getKeyFrames().clear();
		this.clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {moveClock();}), 
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    	
        setupCurrentUser();
        
        if (Objects.nonNull(house)) {
            drawRoomFromCache();
        }
        selectedUser.getSelectionModel().select(username);
        Map userLocs = EditSimulationController.getUserLocations();
        loc.setText(Objects.isNull(userLocs) ? "Unknown" : userLocs.get(username).toString());

        // toggleSwitch
        Pane root = getAnc();

        ToggleSwitch toggle = new ToggleSwitch();
        toggle.setTranslateX(60);
        toggle.setTranslateY(30);

        toggleText.setFont(Font.font(13));
        toggleText.setFill(Color.WHITE);
        toggleText.setTranslateX(60);
        toggleText.setTranslateY(30);
        toggleText.textProperty().bind(Bindings.when(toggle.switchedOnProperty()).then("ON").otherwise("OFF"));

        root.getChildren().addAll(toggle, toggleText);

    }

    /**
     * This class creates a toggle switch.
     */
    private static class ToggleSwitch extends Parent {

        private BooleanProperty switchedOn = new SimpleBooleanProperty(false);

        private TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
        private FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));

        private ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);

        public BooleanProperty switchedOnProperty() {
            return switchedOn;
        }

        public ToggleSwitch() {
            Rectangle background = new Rectangle(60, 30);
            background.setArcWidth(30);
            background.setArcHeight(30);
            background.setFill(Color.WHITE);
            background.setStroke(Color.LIGHTGRAY);

            Circle trigger = new Circle(15);
            trigger.setCenterX(15);
            trigger.setCenterY(15);
            trigger.setFill(Color.WHITE);
            trigger.setStroke(Color.LIGHTGRAY);

            DropShadow shadow = new DropShadow();
            shadow.setRadius(2);
            trigger.setEffect(shadow);

            translateAnimation.setNode(trigger);
            fillAnimation.setShape(background);

            getChildren().addAll(background, trigger);

            switchedOn.addListener((obs, oldState, newState) -> {
                boolean isOn = newState.booleanValue();
                translateAnimation.setToX(isOn ? 60 - 30 : 0);
                fillAnimation.setFromValue(isOn ? Color.RED : Color.GREEN);
                fillAnimation.setToValue(isOn ? Color.GREEN : Color.RED);

                animation.play();
            });

            setOnMouseClicked(event -> {
                switchedOn.set(!switchedOn.get());
            });
        }
    }


    /**
     * Sets up the current active user and all possible options
     */
    private void setupCurrentUser() {
        Map<String, String> listOfUsers = RoleService.findRole(userParent);
        userRole.setText(listOfUsers.get(username));
        selectedUser.getItems().addAll(listOfUsers.keySet());
        selectedUser.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (Objects.nonNull(newValue) && !newValue.equals(oldValue)) {
                    username = newValue;
                    selectedUser.getSelectionModel().select(newValue);
                    userRole.setText(listOfUsers.get(newValue));
                    Map userLocs = EditSimulationController.getUserLocations();
                    loc.setText(Objects.isNull(userLocs) ? "Unknown" : userLocs.get(username).toString());
                }
            }
        });
    }

    /**
     * This function loads the login page(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the file cannot be read
     */
    public void goToLogin(ActionEvent event) throws IOException {
        Parent login = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Scene loginScene = new Scene(login);

        deleteHouse();
        EditSimulationController.deleteLocations();

        // stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }
    
    /**
     * This function appends text onto the console
     * @param str 	String to appened on console
     */
    public void consoleLog(String str) {
        this.console.appendText("[" + this.time.getText() + "] " + str + "\n");
    }

    /**
     * This function loads the user roles page(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the file cannot be read
     */
    public void goToUserSettings(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/userRoles.fxml"));
        Parent root = loader.load();

        UserRolesController controller = loader.getController();
        controller.setUsername(user.getText());

        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * This function allows the user to add a house layout text file, calls the function to parse the json
     * and calls the function to draw the house with rooms, doors, windows and lights
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the file cannot be read
     */
    public void addHouseLayout(ActionEvent event) throws IOException {
        if (toggleText.getText().equals("ON")) {
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setTitle("Open Resource File");
            File file = fileChooser.showOpenDialog(window);

            roomArray = HouseLayoutService.parseHouseLayout(file);

            HashMap<String, Room> rooms = new HashMap<>();
            for (Room room : roomArray) {
                rooms.put(room.getName(), room);
            }

            Set<Room> traversed = new HashSet<>();

            gc = houseRender.getGraphicsContext2D();
            gc.setFont(new Font(11));
            gc.setFill(Color.WHITE);
            int lastX = 130, lastY = 190;
            house = rooms;
            drawRoom(rooms, roomArray[0], traversed, Position.NONE, lastX, lastY);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please turn on the simulation first");
            alert.showAndWait();
        }
    }

    /**
     * Draws the room based on the cached static variables after this file has been submitted
     */
    public void drawRoomFromCache() {
        gc = houseRender.getGraphicsContext2D();
        gc.setFont(new Font(11));
        drawRoom(house, roomArray[0], new HashSet<>(), Position.NONE, 130, 190);
    }

    /**
     * This function draws doors on the house layout
     *
     * @param x  first x coordinate of the door
     * @param y  first y coordinate of the door
     * @param x2 second x coordinate of the door
     * @param y2 second y coordinate of the door
     */
    public void drawDoor(int x, int y, int x2, int y2) {
        gc.setLineWidth(3);
        gc.strokeLine(x, y, x2, y2);
        gc.setLineWidth(1);
    }

    /**
     * This function draws windows on the house layout
     *
     * @param room room where window is drawn
     * @param x    x coordinate of top left corner of the room
     * @param y    y coordinate top left corner of the room
     */
    public void drawWindows(Room room, int x, int y) {
        gc.setLineWidth(3);
        gc.setStroke(Color.LIGHTBLUE);
        for (Window window : room.getWindows()) {
            switch (window.getPosition()) {
                case NONE -> {
                }
                case BOTTOM -> {
                    gc.strokeLine(x + (ROOM_SIZE - DOOR_SIZE) / 2, y + ROOM_SIZE, x + (ROOM_SIZE - DOOR_SIZE) / 2 + DOOR_SIZE, y + ROOM_SIZE);
                }
                case RIGHT -> {
                    gc.strokeLine(x + ROOM_SIZE, y + (ROOM_SIZE - DOOR_SIZE) / 2, x + ROOM_SIZE, y + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2);
                }
                case TOP -> {
                    gc.strokeLine(x + (ROOM_SIZE - DOOR_SIZE) / 2, y, x + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2, y);
                }
                case LEFT -> {
                    gc.strokeLine(x, y + (ROOM_SIZE - DOOR_SIZE) / 2, x, y + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2);
                }
            }
        }
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
    }

    /**
     * Method responsible for drawing the lights in a room
     *
     * @param x  x coordinate of the light
     * @param y  y coordinate of the light
     * @param on boolean value for lights on/off
     */
    public void drawLight(int x, int y, boolean on) {
        if (on) {
            gc.setFill(Color.GOLD);
        }
    }

    /**
     * Method responsible for drawing the rooms in their correct location
     *
     * @param roomHashMap A map of the rooms and their names as the key
     * @param room        room to draw
     * @param visited     rooms that have been visited
     * @param previous    position of the previously visited room
     * @param x           x coordinate of the previously visited room
     * @param y           y coordinate of the previously visited room
     */
    public void drawRoom(Map<String, Room> roomHashMap, Room room, Set<Room> visited, Position previous, int x, int y) {
        visited.add(room);
        switch (previous) {
            case NONE -> {
                gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
                gc.fillText(room.getName(), x + 10, y + 15);
                drawDoor(x + (ROOM_SIZE - DOOR_SIZE) / 2, y + ROOM_SIZE, x + (ROOM_SIZE - DOOR_SIZE) / 2 + DOOR_SIZE, y + ROOM_SIZE);
            }
            case BOTTOM -> {
                drawDoor(x + (ROOM_SIZE - DOOR_SIZE) / 2, y, x + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2, y);
                y += ROOM_SIZE;
                gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
                gc.fillText(room.getName(), x + 10, y + 15);
            }
            case RIGHT -> {
                drawDoor(x + ROOM_SIZE, y + (ROOM_SIZE - DOOR_SIZE) / 2, x + ROOM_SIZE, y + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2);
                x += ROOM_SIZE;
                gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
                gc.fillText(room.getName(), x + 10, y + 15);
            }
            case TOP -> {
                drawDoor(x + (ROOM_SIZE - DOOR_SIZE) / 2, y, x + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2, y);
                y -= ROOM_SIZE;
                gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
                gc.fillText(room.getName(), x + 10, y + 15);
            }
            case LEFT -> {
                drawDoor(x, y + (ROOM_SIZE - DOOR_SIZE) / 2, x, y + DOOR_SIZE + (ROOM_SIZE - DOOR_SIZE) / 2);
                x -= ROOM_SIZE;
                gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
                gc.fillText(room.getName(), x + 10, y + 15);
            }
        }
        drawWindows(room, x, y);
        drawLight(x + ROOM_SIZE / 2 - 5, y + ROOM_SIZE / 2 - 5, false);
        for (Door child : room.getDoors()) {
            Room nextRoom = roomHashMap.get(child.getConnection());
            if (!visited.contains(nextRoom))
                drawRoom(roomHashMap, nextRoom, visited, child.getPosition(), x, y);
        }
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
     * @param event The event that called this function
     */
    public void getLocation(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    /**
     * Changes the location of the window(stage) based on the mouse location.
     *
     * @param event The event that called this function
     */
    public void move(MouseEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setX(event.getScreenX() - xOffset);
        window.setY(event.getScreenY() - yOffset);
    }

    /**
     * This function loads the change date and time page(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the scene file cannot be read
     */
    public void bt_changeDateTimeOnClick(ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/changeDateTime.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * This function loads the edit(scene) into the window(stage)
     *
     * @param event The event that called this function
     * @throws IOException Thrown if the file cannot be read
     */
    public void goToEdit(ActionEvent event) throws IOException {
        if (Objects.nonNull(house)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/editSimulation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(new Scene(root));
            stage.show();

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please input the house");
            alert.showAndWait();
        }

    }

    public static Map<String, Room> getHouse() {
        return house;
    }

    public static void deleteHouse() {
        LoginInfoController.house = null;
    }
}
