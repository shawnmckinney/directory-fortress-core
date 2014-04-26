/*
 * This work is part of OpenLDAP Software <http://www.openldap.org/>.
 *
 * Copyright 1998-2014 The OpenLDAP Foundation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted only as authorized by the OpenLDAP
 * Public License.
 *
 * A copy of this license is available in the file LICENSE in the
 * top-level directory of the distribution or, alternatively, at
 * <http://www.OpenLDAP.org/license.html>.
 */

package org.openldap.fortress.rbac;

import org.openldap.fortress.DelAccessMgr;
import org.openldap.fortress.GlobalErrIds;
import org.openldap.fortress.SecurityException;
import org.openldap.fortress.DelAccessMgrFactory;

/**
 * This class supplies static wrapper utilities to provide ARBAC functionality to Fortress internal Manager APIs.
 * The utilities within this class are all static and can not be called by code outside of Fortress.
 * </p>
 * This class is thread safe.
 *
 * @author Shawn McKinney
 */
final class AdminUtil
{
    private static final String CLS_NM = AdminUtil.class.getName();

    /**
     * Wrapper function to call {@link DelAccessMgrImpl#canAssign(org.openldap.fortress.rbac.Session, org.openldap.fortress.rbac.User, org.openldap.fortress.rbac.Role)}.
     * This will determine if the user contains an AdminRole that is authorized assignment control over User-Role Assignment (URA).  This adheres to the ARBAC02 functional specification for can-assign URA.
     *
     * @param session This object must be instantiated by calling {@link org.openldap.fortress.AccessMgr#createSession} method before passing into the method.  No variables need to be set by client after returned from createSession.
     * @param user    Instantiated User entity requires only valid userId attribute set.
     * @param role    Instantiated Role entity requires only valid role name attribute set.
     * @param contextId maps to sub-tree in DIT, for example ou=contextId, dc=jts, dc = com.
     * @throws org.openldap.fortress.SecurityException In the event of data validation error (i.e. invalid userId or role name) or system error.
     */
    static void canAssign(Session session, User user, Role role, String contextId) throws SecurityException
    {
        if (session != null)
        {
            DelAccessMgr dAccessMgr = DelAccessMgrFactory.createInstance(contextId);
            boolean result = dAccessMgr.canAssign(session, user, role);
            if (!result)
            {
                String warning = "canAssign Role [" + role.getName() + "] User [" + user.getUserId() + "] Admin [" + session.getUserId() + "] failed check.";
                throw new SecurityException(GlobalErrIds.URLE_ADMIN_CANNOT_ASSIGN, warning);
            }
        }
    }

    /**
     * Wrapper function to call {@link DelAccessMgrImpl#canDeassign(org.openldap.fortress.rbac.Session, org.openldap.fortress.rbac.User, org.openldap.fortress.rbac.Role)}.
     * This function will determine if the user contains an AdminRole that is authorized revoke control over User-Role Assignment (URA).  This adheres to the ARBAC02 functional specification for can-revoke URA.
     *
     * @param session This object must be instantiated by calling {@link org.openldap.fortress.AccessMgr#createSession} method before passing into the method.  No variables need to be set by client after returned from createSession.     * @param user    Instantiated User entity requires only valid userId attribute set.
     * @param user    Instantiated User entity requires userId attribute set.
     * @param role    Instantiated Role entity requires only valid role name attribute set.
     * @param contextId maps to sub-tree in DIT, for example ou=contextId, dc=jts, dc = com.
     * @throws org.openldap.fortress.SecurityException In the event of data validation error (i.e. invalid userId or role name) or system error.
     */
    static void canDeassign(Session session, User user, Role role, String contextId) throws SecurityException
    {
        if (session != null)
        {
            DelAccessMgr dAccessMgr = DelAccessMgrFactory.createInstance(contextId);
            boolean result = dAccessMgr.canDeassign(session, user, role);
            if (!result)
            {
                String warning = "canDeassign Role [" + role.getName() + "] User [" + user.getUserId() + "] Admin [" + session.getUserId() + "] failed check.";
                throw new org.openldap.fortress.SecurityException(GlobalErrIds.URLE_ADMIN_CANNOT_DEASSIGN, warning);

            }
        }
    }

    /**
     * Wrapper function to call {@link DelAccessMgrImpl#canGrant(org.openldap.fortress.rbac.Session, org.openldap.fortress.rbac.Role, org.openldap.fortress.rbac.Permission)}.
     * This function will determine if the user contains an AdminRole that is authorized assignment control over
     * Permission-Role Assignment (PRA).  This adheres to the ARBAC02 functional specification for can-assign-p PRA.
     *
     * @param session This object must be instantiated by calling {@link org.openldap.fortress.AccessMgr#createSession} method before passing into the method.  No variables need to be set by client after returned from createSession.     * @param perm    Instantiated Permission entity requires valid object name and operation name attributes set.
     * @param role    Instantiated Role entity requires only valid role name attribute set.
     * @param perm    Instantiated Permission entity requires {@link org.openldap.fortress.rbac.Permission#objName} and {@link org.openldap.fortress.rbac.Permission#opName}.
     * @param contextId maps to sub-tree in DIT, for example ou=contextId, dc=jts, dc = com.
     * @return boolean value true indicates access allowed.
     * @throws SecurityException In the event of data validation error (i.e. invalid perm or role name) or system error.
     */
    static void canGrant(Session session, Role role, Permission perm, String contextId) throws org.openldap.fortress.SecurityException
    {
        if (session != null)
        {
            DelAccessMgr dAccessMgr = DelAccessMgrFactory.createInstance(contextId);
            boolean result = dAccessMgr.canGrant(session, role, perm);
            if (!result)
            {
                String warning = "canGrant Role [" + role.getName() + "] Perm object [" + perm.getObjName() + "] Perm Operation [" + perm.getOpName() + "] Admin [" + session.getUserId() + "] failed check.";
                throw new org.openldap.fortress.SecurityException(GlobalErrIds.URLE_ADMIN_CANNOT_GRANT, warning);
            }
        }
    }

    /**
     * Wrapper function to call {@link DelAccessMgrImpl#canRevoke(org.openldap.fortress.rbac.Session, org.openldap.fortress.rbac.Role, org.openldap.fortress.rbac.Permission)}.
     * This function will determine if the user contains an AdminRole that is authorized revoke control over
     * Permission-Role Assignment (PRA).  This adheres to the ARBAC02 functional specification for can-revoke-p PRA.
     *
     * @param session This object must be instantiated by calling {@link org.openldap.fortress.AccessMgr#createSession} method before passing into the method.  No variables need to be set by client after returned from createSession.     * @param perm    Instantiated Permission entity requires valid object name and operation name attributes set.
     * @param role    Instantiated Role entity requires only valid role name attribute set.
     * @param perm    Instantiated Permission entity requires {@link org.openldap.fortress.rbac.Permission#objName} and {@link org.openldap.fortress.rbac.Permission#opName}.
     * @param contextId maps to sub-tree in DIT, for example ou=contextId, dc=jts, dc = com.
     * @throws org.openldap.fortress.SecurityException In the event of data validation error (i.e. invalid perm or role name) or system error.
     */
    static void canRevoke(Session session, Role role, Permission perm, String contextId) throws org.openldap.fortress.SecurityException
    {
        if (session != null)
        {
            DelAccessMgr dAccessMgr = DelAccessMgrFactory.createInstance(contextId);
            boolean result = dAccessMgr.canRevoke(session, role, perm);
            if (!result)
            {
                String warning = "canRevoke Role [" + role.getName() + "] Perm object [" + perm.getObjName() + "] Perm Operation [" + perm.getOpName() + "] Admin [" + session.getUserId() + "] failed check.";
                throw new SecurityException(GlobalErrIds.URLE_ADMIN_CANNOT_REVOKE, warning);
            }
        }
    }

    /**
     * Method is called by Manager APIs to load contextual information on {@link FortEntity} and perform checkAccess on Administrative permission.
     * </p>
     * The information is used to
     * <ol>
     * <li>Load the administrative User's {@link Session} object into entity.  This is used for checking to ensure administrator has privilege to perform administrative operation.</li>
     * <li>Load the target operation's permission into the audit context.  This is used for Fortress audit log stored in OpenLDAP</li>
     * </ol>
     *
     * @param session object contains the {@link org.openldap.fortress.rbac.User}'s RBAC, {@link org.openldap.fortress.rbac.UserRole}, and Administrative Roles {@link UserAdminRole}.
     * @param perm    contains the permission object name, {@link org.openldap.fortress.rbac.Permission#objName}, and operation name, {@link org.openldap.fortress.rbac.Permission#opName}
     * @param entity  used to pass contextual information through Fortress layers for administrative security checks and audit.
     * @param contextId maps to sub-tree in DIT, for example ou=contextId, dc=jts, dc = com.
     * @throws org.openldap.fortress.SecurityException in the event of system error.
     */
    static void setEntitySession(Session session, Permission perm, FortEntity entity, String contextId) throws org.openldap.fortress.SecurityException
    {
        if (session != null)
        {
            entity.setAdminSession(session);
            entity.setModCode(getObjName(perm.getObjName()) + "." + perm.getOpName());
            checkAccess(session, perm, contextId);
        }
    }

    /**
     * Wrapper function to call {@link org.openldap.fortress.rbac.DelAccessMgrImpl#checkAccess(org.openldap.fortress.rbac.Session, org.openldap.fortress.rbac.Permission)}.
     * Perform user arbac authorization.  This function returns a Boolean value meaning whether the subject of a given session is
     * allowed or not to perform a given operation on a given object. The function is valid if and
     * only if the session is a valid Fortress session, the object is a member of the OBJS data set,
     * and the operation is a member of the OPS data set. The session's subject has the permission
     * to perform the operation on that object if and only if that permission is assigned to (at least)
     * one of the session's active roles. This implementation will verify the roles or userId correspond
     * to the subject's active roles are registered in the object's access control list.
     *
     * @param session This object must be instantiated by calling {@link org.openldap.fortress.AccessMgr#createSession} method before passing into the method.  No variables need to be set by client after returned from createSession.
     * @param perm    object contains obj attribute which is a String and contains the name of the object user is trying to access;
     *                perm object contains operation attribute which is also a String and contains the operation name for the object.
     * @param contextId maps to sub-tree in DIT, for example ou=contextId, dc=jts, dc = com.
     * @throws SecurityException in the event of data validation failure, security policy violation or DAO error.
     */
    static void checkAccess(Session session, Permission perm, String contextId) throws org.openldap.fortress.SecurityException
    {
        if (session != null)
        {
            DelAccessMgr dAccessMgr = DelAccessMgrFactory.createInstance(contextId);
            boolean result = dAccessMgr.checkAccess(session, perm);
            if (!result)
            {
                String info = "checkAccess failed for user [" + session.getUserId() + "] object [" + perm.getObjName() + "] operation [" + perm.getOpName() + "]";
                throw new org.openldap.fortress.AuthorizationException(GlobalErrIds.USER_ADMIN_NOT_AUTHORIZED, info);
            }
        }
    }

    /**
     * Utility will parse a String containing objName.operationName and return the objName only.
     *
     * @param szObj contains raw data format.
     * @return String containing objName.
     */
    static String getObjName(String szObj)
    {
        return szObj.substring(szObj.lastIndexOf('.') + 1);
    }
}