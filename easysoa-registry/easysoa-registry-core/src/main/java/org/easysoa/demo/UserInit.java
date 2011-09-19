package org.easysoa.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.DocumentService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.api.security.impl.ACLImpl;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.usermanager.exceptions.GroupAlreadyExistsException;
import org.nuxeo.ecm.platform.usermanager.exceptions.UserAlreadyExistsException;
import org.nuxeo.runtime.api.Framework;

/**
 * Users & groups initialisation for the EasySOA demo.
 * 
 * @author mkalam-alami
 * 
 */
public class UserInit extends UnrestrictedSessionRunner {

    private static final String GROUP_BUSINESS_USER = "Business user";
    private static final String GROUP_ARCHITECT = "Architect";
    private static final String GROUP_DEVELOPER = "Developer";
    private static final String GROUP_IT_STAFF = "IT Staff";
    private static final String GROUP_ADMINISTRATOR = "Administrator";
    
    private static final String GROUP_DEFAULT_MEMBERS = "members";
    private static final String GROUP_DEFAULT_ADMINISTRATORS = "administrators";

    private static final String USER_ADMINISTRATOR = "Administrator";
    private static final String USER_SOPHIE = "Sophie M.";
    private static final String USER_TED = "Ted M.";
    private static final String USER_GEORGE = "George C.";
    private static final String USER_ARNOLD = "Arnold S.";

    private static Log log = LogFactory.getLog(UserInit.class);

    private UserManager userManager;
    
    private DocumentService docService;

    public UserInit(String repositoryName) {
        super(repositoryName);
    }

    /**
     * Creates sample users and groups for the EasySOA Demo. NOTE: Most of the
     * work could actually be done through CSV files + contributions.
     */
    @Override
    public void run() throws ClientException {

        try {

            userManager = Framework.getService(UserManager.class);
            docService = Framework.getService(DocumentService.class);

            //resetUsersAndGroups(); 
            
            for (String group : userManager.getGroupIds()) { // XXX
                log.error(group);
            }

            // Create users & groups
            /*defineGroup(GROUP_BUSINESS_USER, new String[] { USER_SOPHIE });
            defineGroup(GROUP_DEVELOPER, new String[] { USER_TED });
            defineGroup(GROUP_ARCHITECT, new String[] { USER_GEORGE },
                    new String[] { GROUP_DEVELOPER });
            defineGroup(GROUP_IT_STAFF, new String[] { USER_ARNOLD });
            defineGroup(GROUP_ADMINISTRATOR, new String[] { USER_ADMINISTRATOR });*/

            // Set new groups as childs of the default "members" group
            defineGroup(GROUP_DEFAULT_MEMBERS, new String[] {}, new String[] {
                    GROUP_BUSINESS_USER, GROUP_DEVELOPER, GROUP_ARCHITECT,
                    GROUP_IT_STAFF });
            defineGroup(GROUP_DEFAULT_ADMINISTRATORS, new String[] {}, new String[] {
                    GROUP_ADMINISTRATOR });

            // Set write rights for all "members" members
            DocumentModel wsRootModel = docService.getWorkspaceRoot(session);
            ACP acp = wsRootModel.getACP();
            ACL acl = new ACLImpl("easysoa-demo-rights");
            acl.add(new ACE(GROUP_DEFAULT_MEMBERS, SecurityConstants.EVERYTHING, true));
            acp.addACL(acl);
            wsRootModel.setACP(acp, true);
            session.saveDocument(wsRootModel);
            session.save();

            log.info("Successfully updated demo groups.");

        } catch (Exception e) {
            log.error("Cannot acces user manager", e);
        }

    }

    /**
     * Creates a new user group or updates an existing one.
     * 
     * @param groupName
     *            The new group name/ID
     * @return The new group, or the previously created one if it already
     *         exists.
     * @throws ClientException
     */
    private DocumentModel defineGroup(String groupName, String[] userNames)
            throws ClientException {
        return defineGroup(groupName, userNames, new String[] {});
    }

    /**
     * Creates a new user group or updates an existing one.
     * 
     * @param groupName
     *            The new group name/ID
     * @param subGroups
     *            The subgroups names
     * @return The new group, or the previously created one if it already
     *         exists.
     * @throws ClientException
     */
    private DocumentModel defineGroup(String groupName, String[] userNames,
            String[] subGroups) throws ClientException {

        // Create or get group
        DocumentModel groupModel = null;
        try {
            groupModel = userManager.getBareGroupModel();
            groupModel.setProperty(userManager.getGroupSchemaName(),
                    userManager.getGroupIdField(), groupName);
            groupModel = userManager.createGroup(groupModel);
        } catch (GroupAlreadyExistsException e) {
            groupModel = userManager.getGroupModel(groupName);
        }

        // Set subgroups
        List<String> subGroupsList = Arrays.asList(subGroups);
        groupModel.setProperty(userManager.getGroupSchemaName(),
                userManager.getGroupSubGroupsField(), subGroupsList);

        // Create users
        List<String> userList = new ArrayList<String>();
        for (String userName : userNames) {
            defineUser(userName, groupName);
            userList.add(userName);
        }
        
        // Add users
        groupModel.setProperty(userManager.getGroupSchemaName(),
                userManager.getGroupMembersField(),
                userList);

        // Save changes
        userManager.updateGroup(groupModel);

        return groupModel;

    }

    /**
     * Creates a new user.
     * 
     * @param name
     *            The user name (also used as password)
     * @return The new user, or the previously created one if it already exists.
     * @throws ClientException
     */
    private DocumentModel defineUser(String name, String groupName)
            throws ClientException {

        // Create or get user
        DocumentModel userModel = null;
        String[] nameParts = name.split(" ");
        try {
            userModel = userManager.getBareUserModel();
            userModel.setProperty(userManager.getUserSchemaName(),
                    userManager.getUserIdField(), nameParts[0]);
            userModel = userManager.createUser(userModel);
        } catch (UserAlreadyExistsException e) {
            userModel = userManager.getUserModel(name);
        }

        // Set name
        userModel.setProperty(userManager.getUserSchemaName(), "firstName", nameParts[0]);
        if (nameParts.length > 1) {
            userModel.setProperty(userManager.getUserSchemaName(), "lastName", nameParts[1]);
        }

        // Set mail
        userModel.setProperty(userManager.getUserSchemaName(), 
                userManager.getUserEmailField(), nameParts[0].toLowerCase()+"@easysoa.org");
        
        // Set password
        userModel.setProperty(userManager.getUserSchemaName(), "password", nameParts[0]);
        
        
        userModel.setProperty(userManager.getUserSchemaName(), "groups", Arrays.asList(groupName));
        
        // Save changes
        userManager.updateUser(userModel);
        
        // Set groups
        NuxeoPrincipal principal = userManager.getPrincipal(nameParts[0]);
        principal.setGroups(Arrays.asList(groupName));
        userManager.updateUser(principal.getModel());
        
        
        return principal.getModel();
    }

    /**
     * Resets all demo users and groups. For test purposes.
     */
    private void resetUsersAndGroups() {
        try {
            // Delete custom groups
            String[] customGroups = new String[] { GROUP_BUSINESS_USER,
                    GROUP_DEVELOPER, GROUP_ARCHITECT, GROUP_IT_STAFF, GROUP_ADMINISTRATOR };
            for (String groupName : customGroups) {
                DocumentModel groupModel = userManager.getGroupModel(groupName);
                if (groupModel != null) {
                    userManager.deleteGroup(groupModel);
                }
            }

            // Delete all users except Administrator
            for (String userName : userManager.getUserIds()) {
                if (!userName.equals(USER_ADMINISTRATOR)) {
                    userManager.deleteUser(userName);
                }
            }
        } catch (ClientException e) {
            log.error("Failed to reset users and groups", e);
        }
    }

}
