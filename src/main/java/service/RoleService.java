package service;

import constants.UserRoles;
import entity.UserRole;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.EnumUtils;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for the updating and selection of roles
 */
public class RoleService {

    /**
     * Method returning all possible roles in JavaFX readable format
     *
     * @return A list in {@link ObservableList} of all possible user roles
     */
    public static ObservableList<String> getAllRoles() {
        return FXCollections.observableArrayList(
                UserRoles.PARENT.toString(),
                UserRoles.CHILD.toString(),
                UserRoles.GUEST.toString(),
                UserRoles.STRANGER.toString()
        );
    }

    /**
     * Queries the database for all user and user role combinations
     *
     * @return A list of username and their user roles in {@link UserRole}
     */
    public static List<UserRole> getRoles(final String userParent) {
        try {
            return DatabaseService.getAllUserRoles(userParent);
        } catch (SQLException e) {
            System.out.println("There are no user roles");
        }
        return null;
    }

    /**
     * Validates chosen user role and updates the user role of a User Profile shown as their username
     *
     * @param username User Profile's username whose role will change
     * @param role     The chosen role to change into. Must be of type {@link UserRoles}
     */
    public static void changeRole(final String userParent, final String username, final String role) {
        try {
            if (EnumUtils.isValidEnum(UserRoles.class, role)) {
                DatabaseService.updateUserRole(userParent, username, role);
            } else {
                System.out.println("Invalid Enum Used");
            }
        } catch (SQLException e) {
            System.out.println("Invalid values used");
        }
    }

    /**
     * Finds the role of the given username
     *
     * @param parentUser Username's main account
     * @return Map of username as keys and roles as values
     */
    public static Map<String, String> findRole(final String parentUser) {
        try {
            List<UserRole> listOfUsers = DatabaseService.getAllUserRoles(parentUser);
            return listOfUsers.stream().collect(Collectors.toMap(
                    UserRole::getUsername,
                    user -> user.getRole().toString()
            ));
        } catch (SQLException e) {
            System.out.println("There are no user roles");
        }
        return null;
    }
}
