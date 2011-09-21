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

    private static final String GROUP_BUSINESS_USER = "Business User";
    private static final String GROUP_ARCHITECT = "Architect";
    private static final String GROUP_DEVELOPER = "Developer";
    private static final String GROUP_IT_STAFF = "IT Staff";
    private static final String GROUP_ADMINISTRATOR = "Administrator";
    
   // private static final String GROUP_DEFAULT_MEMBERS = "members";
    private static final String GROUP_DEFAULT_ADMINISTRATORS = "administrators";
    
    private static Log log = LogFactory.getLog(UserInit.class);

    private UserManager userManager;

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
            
            // Set new groups as childs of the default "members" group
            addSubgroups(/*XXX: Should be GROUP_DEFAULT_MEMBERS*/ GROUP_DEFAULT_ADMINISTRATORS, 
                    new String[] { GROUP_BUSINESS_USER, GROUP_DEVELOPER, GROUP_IT_STAFF });
            addSubgroups(GROUP_DEVELOPER, new String[] { GROUP_ARCHITECT });
            addSubgroups(GROUP_DEFAULT_ADMINISTRATORS, new String[] { GROUP_ADMINISTRATOR }); // Just to 'rename' the group

            // Set write rights for all "members" members (FIXME)
            /*DocumentModel wsRootModel = session.getRootDocument();
            ACP acp = wsRootModel.getACP();
            ACL acl = new ACLImpl("easysoa-demo-rights");
            acl.add(new ACE(GROUP_DEFAULT_MEMBERS, SecurityConstants.EVERYTHING, true));
            acp.addACL(acl);
            wsRootModel.setACP(acp, true);
            session.saveDocument(wsRootModel);
            session.save();*/

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
    private DocumentModel addSubgroups(String groupName, String[] subGroups) throws ClientException {

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

        // Add subgroups
        List<?> subGroupsListPrev = (List<?>) groupModel.getProperty(userManager.getGroupSchemaName(),
                userManager.getGroupSubGroupsField());
        List<String> subGroupsListNew = Arrays.asList(subGroups);
        for (Object o : subGroupsListPrev) {
            subGroupsListNew.add((String) o);
        }
        groupModel.setProperty(userManager.getGroupSchemaName(),
                userManager.getGroupSubGroupsField(), subGroupsListNew);

        // Save changes
        userManager.updateGroup(groupModel);

        return groupModel;

    }

}
