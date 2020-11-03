package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import controller.UserPermissionsController;
import entity.CommandType;
import entity.PermissionType;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class PermissionService {

    public static void importRoles(final JSONObject object) throws JsonProcessingException {
        JSONObject jsonMap = object.getJSONObject("permissions");
        HashMap<String, Object> map = new ObjectMapper().readValue(jsonMap.toString(), HashMap.class);
        Map<String, Map<CommandType, PermissionType>> result = new HashMap<>();
        map.keySet().forEach(key -> {
            Map commandPermissions = new HashMap();
            Map userPermissions = (Map) map.get(key);
            userPermissions.keySet().forEach(command -> {
                commandPermissions.put(CommandType.valueOf(command.toString()), PermissionType.valueOf(userPermissions.get(command).toString()));
            });
            result.put(key, commandPermissions);
        });

        UserPermissionsController.setUserPermissions(result);
    }
}
