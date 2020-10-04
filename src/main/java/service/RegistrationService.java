package service;

import constants.RegistrationStatus;

import java.sql.SQLException;
import java.util.List;

public class RegistrationService
{
    public static RegistrationStatus registration(final String username, final String password, final String passwordVerification)
    {
        try
        {
            if (!password.equals(passwordVerification))
            {
                return RegistrationStatus.PASSWORD_NOT_EQUAL;
            }

            if (DatabaseService.verifyUniqueUsername(username).size() < 1)
            {
                // TODO add label for firstname and lastname here
                DatabaseService.createNewUser(username, password, username, username);
                return RegistrationStatus.USER_CREATED;
            }
            else
            {
                return RegistrationStatus.NOT_UNIQUE_USERNAME;
            }
        }
        catch (SQLException e)
        {
            System.out.println("No users found for given combination");
        }
        return null;
    }
}
