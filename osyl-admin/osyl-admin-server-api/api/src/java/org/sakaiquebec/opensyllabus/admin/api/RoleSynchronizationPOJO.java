package org.sakaiquebec.opensyllabus.admin.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11091096 on 2017-09-12.
 */
public class RoleSynchronizationPOJO {
    String LIST_DELIMITER = ",";
    String role;
    String description;
    List<String> functions;
    List<String> removedFunctions;
    Boolean updatedGroup;
    List<String> toolParameter;

    public List<String> getToolParameter() {
        return toolParameter;
    }

    public void setToolParameter(String toolParameter) {
        this.toolParameter = new ArrayList<String>();
        if (toolParameter != null && toolParameter.length() > 0) {
            String[] toolsTable = toolParameter.split(LIST_DELIMITER);
            for (int i = 0; i < toolsTable.length; i++) {
                this.toolParameter.add(toolsTable[i].trim());
            }
        }
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getFunctions() {
        return functions;
    }

    public void setFunctions(String functions) {
        this.functions = new ArrayList<String>();
        if (functions != null && functions.length() > 0) {
            String[] permissionsTable = functions.split(LIST_DELIMITER);
            for (int i = 0; i < permissionsTable.length; i++) {
                this.functions.add(permissionsTable[i].trim());
            }
        }
    }

    public List<String> getRemovedFunctions() {
        return removedFunctions;
    }

    public void setRemovedFunctions(String removedFunctions) {
        this.removedFunctions = new ArrayList<String>();
        if (removedFunctions != null && removedFunctions.length() > 0) {
            String[] permissionsTable = removedFunctions.split(LIST_DELIMITER);
            for (int i = 0; i < permissionsTable.length; i++) {
                this.removedFunctions.add(permissionsTable[i].trim());
            }
        }
    }

    public Boolean getUpdatedGroup() {
        return updatedGroup;
    }

    public void setUpdatedGroup(Boolean updatedGroup) {
        this.updatedGroup = updatedGroup;
    }
}