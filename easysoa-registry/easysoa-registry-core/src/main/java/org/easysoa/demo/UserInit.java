package org.easysoa.demo;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.usermanager.exceptions.GroupAlreadyExistsException;
import org.nuxeo.runtime.api.Framework;

/**
 * Users & groups initialization for the EasySOA demo.
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
    
    private static Log log = LogFactory.getLog(UserInit.class);

    private UserManager userManager;
    
   // private DocumentService docService;

    public UserInit(String repositoryName) {
        super(repositoryName);
    }

    /**
     * Initializes sample groups for the EasySOA Demo.
     * NOTE: Most of the work is actually done through CSV files + contributions.
     */
    @Override
    public void run() throws ClientException {

        try {
            userManager = Framework.getService(UserManager.class);
          //docService = Framework.getService(DocumentService.class);
            
            // Set new groups as childs of the default "members" group
            //setSubgroups(GROUP_DEFAULT_MEMBERS, new String[] { });
            setSubgroups(GROUP_DEFAULT_ADMINISTRATORS, new String[] { GROUP_ADMINISTRATOR, GROUP_BUSINESS_USER, GROUP_DEVELOPER,
            GROUP_ARCHITECT, GROUP_IT_STAFF });

            // Set write rights for all "members" members
            /*DocumentModel wsRootModel = docService.getWorkspaceRoot(session);
            ACP acp = wsRootModel.getACP();
            ACL acl = new ACLImpl("easysoa-demo-rights");
            acl.add(new ACE(GROUP_DEFAULT_MEMBERS, SecurityConstants.EVERYTHING, true));
            acp.addACL(acl);
            wsRootModel.setACP(acp, true);
            session.saveDocument(wsRootModel);*/
            session.save();

            log.info("Successfully updated demo groups.");

        } catch (Exception e) {
            log.error("Cannot acces user manager", e);
        }

    }

    /**
     * Creates a new user group or updates an existing one.
     * 
     * @param groupName The new group name/ID
     * @param subGroups The subgroups names
     * @return The new group, or the previously created one if it already exists.
     * @throws ClientException
     */
    private DocumentModel setSubgroups(String groupName, String[] subGroups) throws ClientException {

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
        
        // Save changes
        userManager.updateGroup(groupModel);

        return groupModel;

    }

}
