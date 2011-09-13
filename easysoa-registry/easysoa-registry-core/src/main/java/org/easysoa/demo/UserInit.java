package org.easysoa.demo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.schema.types.Field;
import org.nuxeo.ecm.core.schema.types.Schema;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.usermanager.exceptions.GroupAlreadyExistsException;
import org.nuxeo.runtime.api.Framework;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * TODO Test & Debug
 * 
 * @author mkalam-alami
 *
 */
public class UserInit extends UnrestrictedSessionRunner {

    private static final String GROUP_BUSINESS_USER = "Business user";
    private static final String GROUP_ARCHITECT = "Architect";
    private static final String GROUP_DEVELOPER = "Developer";
    private static final String GROUP_IT_STAFF = "IT Staff";
    
    private static final String USER_SOPHIE = "Sophie";
    private static final String USER_TED = "Ted";
    private static final String USER_GEORGE = "George";
    private static final String USER_ARNOLD = "Arnold";
    
    private static Log log = LogFactory.getLog(UserInit.class);
    
    private UserManager userManager;
    
    public UserInit(String repositoryName) {
        super(repositoryName);
    }
    
    /**
     * Creates sample users and groups for the EasySOA Demo.
     */
    @Override
    public void run() throws ClientException {
        
        try {
            userManager = Framework.getService(UserManager.class);
            
            resetUsersAndGroups(); // TODO Remove
            
            // Create users & groups
             createGroup(GROUP_BUSINESS_USER, new String[]{ USER_SOPHIE });
             createGroup(GROUP_DEVELOPER, new String[]{ USER_TED });
             createGroup(GROUP_ARCHITECT, new String[]{ USER_GEORGE }, new String[]{ GROUP_DEVELOPER });
             createGroup(GROUP_IT_STAFF, new String[]{ USER_ARNOLD });

             log.info("Successfully reated demo groups and users.");
             
        } catch (Exception e) {
            log.error("Cannot acces user manager", e);
        }
        
        
    }

    /**
     * Creates a new user group.
     * @param groupName The new group name/ID
     * @return The new group, or the previously created one if it already exists.
     * @throws ClientException
     */
    private DocumentModel createGroup(String groupName, String[] userNames) throws ClientException {
        return createGroup(groupName, userNames, new String[]{});
    }
    
    /**
     * Creates a new user group with subgroups.
     * @param groupName The new group name/ID
     * @param subGroups The subgroups names
     * @return The new group, or the previously created one if it already exists.
     * @throws ClientException
     */
    private DocumentModel createGroup(String groupName, String[] userNames, String[] subGroups) throws ClientException {
        DocumentModel existingGroup = userManager.getGroupModel(groupName);
        if (existingGroup == null) {
            try {
                
                // Create users
                List<String> userList = new ArrayList<String>();
                for (String userName : userNames) {
                    createUser(userName);
                    userList.add(userName);
                }
                
                // Set group ID/name
                DocumentModel groupModel = userManager.getBareGroupModel();
                groupModel.setProperty(
                        userManager.getGroupSchemaName(),
                        userManager.getGroupIdField(),
                        groupName);
                
                // Set subgroups
                List<String> subGroupsList = new ArrayList<String>();
                Collections.addAll(subGroupsList, subGroups);
                groupModel.setProperty(
                        userManager.getGroupSchemaName(),
                        userManager.getGroupSubGroupsField(),
                        subGroupsList);
                
                // Set users
                groupModel.setProperty(
                        userManager.getGroupSchemaName(),
                        userManager.getGroupMembersField(),
                        userList);
                
                return userManager.createGroup(groupModel);
            }
            catch (GroupAlreadyExistsException e) {
                return userManager.getGroupModel(groupName);
            }
        }
        else {
            return existingGroup;
        }
    }

    /**
     * Creates a new user.
     * @param name The user name (also used as password)
     * @return The new user, or the previously created one if it already exists.
     * @throws ClientException
     */
    private DocumentModel createUser(String name) throws ClientException {
        DocumentModel existingUser = userManager.getUserModel(name);
        if (existingUser == null) {
            
            // Set user name
            DocumentModel userModel = userManager.getBareUserModel();
            userModel.setProperty(
                    userManager.getUserSchemaName(),
                    userManager.getUserIdField(),
                    name);
            
            // Set user password
            for (Schema schema : userModel.getDocumentType().getSchemas()) {
                log.info(schema.getName());
                for (Field field : schema.getFields()) {
                    log.info(">"+field.getName());
                }
            }
            // TODO (How?)
            
            return userManager.createUser(userModel);
        }
        else {
            return existingUser;
        }
    }

    /**
     * Resets all demo users and groups.
     * For test purposes.
     */
    private void resetUsersAndGroups() {
        try {
            // Delete custom groups
            String[] customGroups =  new String[]{GROUP_BUSINESS_USER, GROUP_DEVELOPER, GROUP_ARCHITECT, GROUP_IT_STAFF};
            for (String groupName : customGroups) {
                DocumentModel groupModel = userManager.getGroupModel(groupName);
                if (groupModel != null) {
                    userManager.deleteGroup(groupModel);
                }
            }
            
            // Delete all users except Administrator
            for (String userName : userManager.getUserIds()) {
                if (!userName.equals("Administrator")) {
                    userManager.deleteUser(userName);
                }
            }
        }
        catch (ClientException e) {
            log.error("Failed to reset users and groups", e);
        }
    }

}
