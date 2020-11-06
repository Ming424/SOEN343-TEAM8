package observerPattern;

import controller.LoginInfoController;

import java.util.Map;

/**
 * class UserLocationObserver
 */
public class UserLocationObserver{

    /**
     * declaring variables
     */
    protected Subject subject;

    /**
     * @param subject The subject
     */
    public UserLocationObserver(Subject subject){
        this.subject = subject;
        this.subject.attach(this);
    }

    /**
     * This function does the update when the the subject is changed
     */
    public void update() {
        Map<String, String> userLocations  = subject.getUserLocation();
        String timeBeforeAlert = LoginInfoController.getTimeBeforeAlert();
        if (timeBeforeAlert == null){
            timeBeforeAlert = "0";
        }
        for (String person : userLocations.keySet()){
            if (LoginInfoController.isAwayMode() && !userLocations.get(person).equals("Outside")) {
                LoginInfoController.consoleLogFile(person + " was detected in the " +
                        userLocations.get(person)+ " during Away Mode. Authorities will be alerted in " +
                        timeBeforeAlert + " minutes.");
            }
        }
    }
}

