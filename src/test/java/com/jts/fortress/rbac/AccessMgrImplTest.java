/*
 * Copyright (c) 2009-2012. Joshua Tree Software, LLC.  All Rights Reserved.
 */

package com.jts.fortress.rbac;

import com.jts.fortress.AccessMgr;
import com.jts.fortress.AccessMgrFactory;
import com.jts.fortress.PswdPolicyMgr;
import com.jts.fortress.constants.GlobalErrIds;
import com.jts.fortress.pwpolicy.PolicyTestData;
import com.jts.fortress.pwpolicy.PswdPolicyMgrImplTest;
import com.jts.fortress.SecurityException;
import com.jts.fortress.util.LogUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * AccessMgrImpl Tester.
 *
 * @author Shawn McKinney
 * @version 1.0
 * @since <pre>01/24/2010</pre>
 */
public class AccessMgrImplTest extends TestCase
{
    private static final String CLS_NM = AccessMgrImplTest.class.getName();
    final protected static Logger log = Logger.getLogger(CLS_NM);

    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTest(new AccessMgrImplTest("testDropActiveRole"));
        return suite;
    }

    public AccessMgrImplTest(String name)
    {
        super(name);
    }

    public void setUp() throws Exception
    {
        super.setUp();
    }

    public void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testGetSession() throws Exception
    {
        //TODO: Test goes here...
    }

    public void testGetToken() throws Exception
    {
        //TODO: Test goes here...
    }

    public void testGetUserId()
    {
        // public String getUserId(Session, session)
        getUsers("GET-USRIDS TU1_UPD", UserTestData.USERS_TU1_UPD);
        getUsers("GET-USRIDS TU3", UserTestData.USERS_TU3);
        getUsers("GET-USRIDS TU4", UserTestData.USERS_TU4);
    }

    /**
     * @param msg
     * @param uArray
     */
    public static void getUserIds(String msg, String[][] uArray)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            for (String[] usr : uArray)
            {
                User user = UserTestData.getUser(usr);
                Session session = accessMgr.authenticate(user.getUserId(), user.getPassword());
                assertNotNull(session);
                String userId = accessMgr.getUserId(session);
                assertTrue(CLS_NM + ".getUserIds failed compare found userId [" + userId + "] valid userId [" + UserTestData.getUserId(usr) + "]", userId.equalsIgnoreCase(UserTestData.getUserId(usr)));
            }
            log.debug(CLS_NM + ".getUserIds successful");
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".getUserIds: failed with SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }

    public void testGetUser()
    {
        // public User getUser(Session, session)
        getUsers("GET-USRS TU1_UPD", UserTestData.USERS_TU1_UPD);
        getUsers("GET-USRS TU3", UserTestData.USERS_TU3);
        getUsers("GET-USRS TU4", UserTestData.USERS_TU4);
    }

    /**
     * @param msg
     * @param uArray
     */
    public static void getUsers(String msg, String[][] uArray)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            for (String[] usr : uArray)
            {
                User user = UserTestData.getUser(usr);
                Session session = accessMgr.createSession(user, false);
                assertNotNull(session);
                user = accessMgr.getUser(session);
                UserTestData.assertEquals(user, usr);
            }
            log.debug(CLS_NM + ".getUsers successful");
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".getUsers: failed with SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }

    public void testAuthenticate()
    {
        // public Session authenticate(String userId, String password)
        authenticateUsers("AUTH-USRS TU1_UPD", UserTestData.USERS_TU1_UPD, 1);
        authenticateUsers("AUTH-USRS TU2_CHG", UserTestData.USERS_TU2_CHG, 1);
        authenticateUsers("AUTH-USRS TU3", UserTestData.USERS_TU3, 1);
        authenticateUsers("AUTH-USRS TU4", UserTestData.USERS_TU4, 1);
    }

    /**
     * @param msg
     * @param uArray
     * @param multiplier
     */
    public static void authenticateUsers(String msg, String[][] uArray, int multiplier)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            for (String[] usr : uArray)
            {
                User user = UserTestData.getUser(usr);
                Session session = accessMgr.authenticate(user.getUserId(), user.getPassword());
                assertNotNull(session);
                // todo: need to test to ensure roles are not added to session.
                // now try negative test case:
                try
                {
                    session = accessMgr.authenticate(user.getUserId(), "wrongpw".toCharArray());
                    fail(CLS_NM + ".authenticateUsers failed negative test");
                }
                catch (SecurityException se)
                {
                    assertTrue(CLS_NM + "authenticateUsers reset excep id check", se.getErrorId() == GlobalErrIds.USER_PW_INVLD);
                    // pass

                }
            }
            log.debug(CLS_NM + ".authenticateUsers successful");
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".authenticateUsers: failed with SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }

    /**
     *
     */
    public void testAuthenticateLocked()
    {
        // public Session authenticate(String userId, String password)
        authenticateLockedUsers("AUTH-L-USRS TU1_UPD", UserTestData.USERS_TU1_UPD);
        authenticateLockedUsers("AUTH-L-USRS TU3", UserTestData.USERS_TU3);
        authenticateLockedUsers("AUTH-L-USRS TU4", UserTestData.USERS_TU4);
    }

    /**
     * @param msg
     * @param uArray
     */
    public static void authenticateLockedUsers(String msg, String[][] uArray)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            for (String[] usr : uArray)
            {
                User user = UserTestData.getUser(usr);
                // now try negative test case:
                try
                {
                    Session session = accessMgr.authenticate(user.getUserId(), user.getPassword());
                    fail(CLS_NM + ".authenticateLockedUsers failed test");
                }
                catch (SecurityException se)
                {
                    assertTrue(CLS_NM + "authenticateLockedUsers reset excep id check", se.getErrorId() == GlobalErrIds.USER_PW_LOCKED);
                    // pass
                    //log.error("locked=" + se.getMsgid() + " msg=" + se.getMessage());
                }
            }
            log.debug(CLS_NM + ".authenticateLockedUsers successful");
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".authenticateLockedUsers: failed with SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }

    /**
     *
     */
    public void testAuthenticateReset()
    {
        // public Session authenticate(String userId, String password)
        authenticateResetUsers("AUTH-R-USRS TU2_RST", UserTestData.USERS_TU2_RST, PolicyTestData.POLICIES_TP2[0]);
    }

    /**
     * @param msg
     * @param uArray
     */
    public static void authenticateResetUsers(String msg, String[][] uArray, String[] plcy)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            PswdPolicyMgr policyMgr = PswdPolicyMgrImplTest.getManagedPswdMgr();
            for (String[] usr : uArray)
            {
                User user = UserTestData.getUser(usr);
                // update this user with pw policy that requires change after reset:
                policyMgr.updateUserPolicy(user.getUserId(), PolicyTestData.getName(plcy));
                // now try negative test case:
                try
                {
                    Session session = accessMgr.authenticate(user.getUserId(), user.getPassword());
                    fail(CLS_NM + ".authenticateResetUsers failed test");
                }
                catch (SecurityException se)
                {
                    assertTrue(CLS_NM + "authenticateResetUsers reset excep id check", se.getErrorId() == GlobalErrIds.USER_PW_RESET);
                    // pass
                }
            }
            log.debug(CLS_NM + ".authenticateResetUsers successful");
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".authenticateResetUsers: failed with SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }

    /**
     *
     */
    public void testCreateSession()
    {
        // public Session createSession(User user, boolean isTrusted)
        createSessions("CREATE-SESS TU1_UPD TR1", UserTestData.USERS_TU1_UPD, RoleTestData.ROLES_TR1);
        createSessions("CREATE-SESS TU3 TR3", UserTestData.USERS_TU3, RoleTestData.ROLES_TR3);
        createSessions("CREATE-SESS TU4 TR2", UserTestData.USERS_TU4, RoleTestData.ROLES_TR2);
    }

    /**
     * @param msg
     * @param uArray
     * @param rArray
     */
    public static void createSessions(String msg, String[][] uArray, String[][] rArray)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            for (String[] usr : uArray)
            {
                User user = UserTestData.getUser(usr);
                Session session = accessMgr.createSession(user, false);
                assertNotNull(session);
                String userId = accessMgr.getUserId(session);
                assertTrue(CLS_NM + ".createSessions failed compare found userId [" + userId + "] valid userId [" + UserTestData.getUserId(usr) + "]", userId.equalsIgnoreCase(UserTestData.getUserId(usr)));
                UserTestData.assertEquals(user, usr);
                List<UserRole> uRoles = session.getRoles();
                assertNotNull(uRoles);
                assertEquals(CLS_NM + ".createSessions user role check failed list size user [" + user.getUserId() + "]", rArray.length, uRoles.size());
                for (String[] rle : rArray)
                {
                    assertTrue(CLS_NM + ".createSessions failed role search USER [" + user.getUserId() + "] ROLE1 [" + RoleTestData.getName(rle) + "] should be present", uRoles.contains(RoleTestData.getUserRole(UserTestData.getUserId(usr), rle)));
                }

                // now try negative test case:
                try
                {
                    User userBad = new User(user.getUserId(), "badpw".toCharArray());
                    session = accessMgr.createSession(userBad, false);
                    fail(CLS_NM + ".createSessions failed negative test");
                }
                catch (SecurityException se)
                {
                    assertTrue(CLS_NM + "createSessions excep id check", se.getErrorId() == GlobalErrIds.USER_PW_INVLD);
                    // pass
                }
            }
            log.debug(CLS_NM + ".createSessions successful");
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".createSessions: failed with SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }

    /**
     *
     */
    public void testCreateSessionWithRole()
    {
        // public Session createSession(User user, boolean isTrusted)
        createSessionsWithRoles("CR-SESS-WRLS TU1_UPD TR1", UserTestData.USERS_TU1_UPD, RoleTestData.ROLES_TR1);
        createSessionsWithRoles("CR-SESS-WRLS TU3 TR3", UserTestData.USERS_TU3, RoleTestData.ROLES_TR3);
        createSessionsWithRoles("CR-SESS-WRLS TU4 TR2", UserTestData.USERS_TU4, RoleTestData.ROLES_TR2);
    }

    /**
     * @param msg
     * @param uArray
     * @param rArray
     */
    public static void createSessionsWithRoles(String msg, String[][] uArray, String[][] rArray)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            for (String[] usr : uArray)
            {
                User user = UserTestData.getUser(usr);
                List<UserRole> rlsRequested = new ArrayList<UserRole>();
                int cnt = 0;
                for (String[] rle : rArray)
                {
                    rlsRequested.add(RoleTestData.getUserRole(user.getUserId(), rle));
                    user.setRoles(rlsRequested);
                    Session session = accessMgr.createSession(user, false);
                    assertTrue(CLS_NM + ".createSessionsWithRoles failed role search USER [" + user.getUserId() + "] CNT [" + ++cnt + "] size [" + session.getRoles().size() + "]", cnt == session.getRoles().size());
                    String userId = accessMgr.getUserId(session);
                    assertTrue(CLS_NM + ".createSessionsWithRoles failed compare found userId [" + userId + "] valid userId [" + UserTestData.getUserId(usr) + "]", userId.equalsIgnoreCase(UserTestData.getUserId(usr)));
                    UserTestData.assertEquals(user, usr);
                }
            }
            log.debug(CLS_NM + ".createSessionsWithRoles successful");
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".createSessionsWithRoles: failed with SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }

    /**
     *
     */
    public void testCreateSessionWithRolesTrusted()
    {
        // public Session createSession(User user, boolean isTrusted)
        createSessionsWithRolesTrusted("CR-SESS-WRLS-TRST TU1_UPD TR1", UserTestData.USERS_TU1_UPD, RoleTestData.ROLES_TR1);
        createSessionsWithRolesTrusted("CR-SESS-WRLS-TRST TU3 TR3", UserTestData.USERS_TU3, RoleTestData.ROLES_TR3);
        createSessionsWithRolesTrusted("CR-SESS-WRLS-TRST TU4 TR2", UserTestData.USERS_TU4, RoleTestData.ROLES_TR2);
    }

    /**
     * @param msg
     * @param uArray
     * @param rArray
     */
    public static void createSessionsWithRolesTrusted(String msg, String[][] uArray, String[][] rArray)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            for (String[] usr : uArray)
            {
                User user = UserTestData.getUser(usr);
                List<UserRole> rlsRequested = new ArrayList<UserRole>();
                int cnt = 0;
                for (String[] rle : rArray)
                {
                    rlsRequested.add(RoleTestData.getUserRole(user.getUserId(), rle));
                    user.setRoles(rlsRequested);
                    Session session = accessMgr.createSession(user, true);
                    assertTrue(CLS_NM + ".createSessionsWithRolesTrusted failed role search USER [" + user.getUserId() + "] CNT [" + ++cnt + "] size [" + session.getRoles().size() + "]", cnt == session.getRoles().size());
                    String userId = accessMgr.getUserId(session);
                    assertTrue(CLS_NM + ".createSessionsWithRolesTrusted failed compare found userId [" + userId + "] valid userId [" + UserTestData.getUserId(usr) + "]", userId.equalsIgnoreCase(UserTestData.getUserId(usr)));
                    UserTestData.assertEquals(user, usr);
                }
            }
            log.debug(CLS_NM + ".createSessionsWithRolesTrusted successful");
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".createSessionsWithRolesTrusted: failed with SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }

    /**
     *
     */
    public void testCreateSessionTrusted()
    {
        // public Session createSession(User user, boolean isTrusted)
        createSessionsTrusted("CR-SESS-TRST TU1_UPD TR1", UserTestData.USERS_TU1_UPD, RoleTestData.ROLES_TR1);
        createSessionsTrusted("CR-SESS-TRST TU3 TR3", UserTestData.USERS_TU3, RoleTestData.ROLES_TR3);
        createSessionsTrusted("CR-SESS-TRST TU4 TR2", UserTestData.USERS_TU4, RoleTestData.ROLES_TR2);
    }

    /**
     * @param msg
     * @param uArray
     * @param rArray
     */
    public static void createSessionsTrusted(String msg, String[][] uArray, String[][] rArray)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            for (String[] usr : uArray)
            {
                User user = UserTestData.getUser(usr);
                Session session = accessMgr.createSession(user, true);
                assertNotNull(session);
                String userId = accessMgr.getUserId(session);
                assertTrue(CLS_NM + ".createSessionsTrusted failed compare found userId [" + userId + "] valid userId [" + UserTestData.getUserId(usr) + "]", userId.equalsIgnoreCase(UserTestData.getUserId(usr)));
                UserTestData.assertEquals(user, usr);
                List<UserRole> uRoles = session.getRoles();
                assertNotNull(uRoles);
                assertEquals(CLS_NM + ".createSessionsTrusted user role check failed list size user [" + user.getUserId() + "]", rArray.length, uRoles.size());
                for (String[] rle : rArray)
                {
                    assertTrue(CLS_NM + ".createSessionsTrusted failed role search USER [" + user.getUserId() + "] ROLE1 [" + RoleTestData.getName(rle) + "] should be present", uRoles.contains(RoleTestData.getUserRole(UserTestData.getUserId(usr), rle)));
                }

                // now try negative test case:
                try
                {
                    User badUser = new User(user.getUserId() + "wrong");
                    session = accessMgr.createSession(badUser, true);
                    fail(CLS_NM + ".createSessionsTrusted failed negative test");
                }
                catch (SecurityException se)
                {
                    assertTrue(CLS_NM + "createSessionsTrusted excep id check", se.getErrorId() == GlobalErrIds.USER_NOT_FOUND);
                    // pass
                }
            }
            log.debug(CLS_NM + ".createSessionsTrusted successful");
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".createSessionsTrusted: failed with SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }

    public void createSessionsDSD()
    {
        // public Session createSession(User user, boolean isTrusted)
        createSessionsDSD("CR-SESS-DSD TU12 DSD_T1", UserTestData.USERS_TU12_DSD, RoleTestData.DSD_T1);
        createSessionsDSD("CR-SESS-DSD TU13 DSD_T4_B", UserTestData.USERS_TU13_DSD_HIER, RoleTestData.DSD_T4_B);
        createSessionsDSD("CR-SESS-DSD TU14 DSD_T5_B", UserTestData.USERS_TU14_DSD_HIER, RoleTestData.DSD_T5_B);
        createSessionsDSD("CR-SESS-DSD TU15 DSD_T6_C", UserTestData.USERS_TU15_DSD_HIER, RoleTestData.DSD_T6_C);
    }
    /**
     *
     * @param msg
     * @param uArray
     * @param dsdArray
     */
    public static void createSessionsDSD(String msg, String[][] uArray, String[][] dsdArray)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            int i = 0;
            for (String[] usr : uArray)
            {
                SDSet dsd = RoleTestData.getSDSet(dsdArray[i++]);
                User user = UserTestData.getUser(usr);
                Session session = accessMgr.createSession(user, false);
                assertNotNull(session);
                String userId = accessMgr.getUserId(session);
                assertTrue(CLS_NM + ".createSessionsDSD failed compare found userId [" + userId + "] valid userId [" + UserTestData.getUserId(usr) + "]", userId.equalsIgnoreCase(UserTestData.getUserId(usr)));
                UserTestData.assertEquals(user, usr);
                List<UserRole> uRoles = session.getRoles();
                assertNotNull(uRoles);
                // was the number of members in test DSD greater than the cardinality?
                if(dsd.getMembers().size() < dsd.getCardinality())
                {
                    assertEquals(CLS_NM + ".createSessionsDSD role list size check failed user-role user [" + user.getUserId() + "]", dsd.getMembers().size(), uRoles.size());
                }
                else
                {
                    assertEquals(CLS_NM + ".createSessionsDSD role cardinality check failed user-role list size user [" + user.getUserId() + "] dsd set [" + dsd.getName() + "] card [" + dsd.getCardinality() + "] listsize [" + uRoles.size() + "]", dsd.getCardinality().intValue()-1, uRoles.size());
                }
            }
            log.debug(CLS_NM + ".createSessionsDSD successful");
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".createSessionsDSD: failed with SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }

    /**
     *
     */
    public void testCreateSessionHier()
    {
        // public Session createSession(User user, boolean isTrusted)
        createSessionsHier("CREATE-SESS-HIER TU18 TR6 DESC", UserTestData.USERS_TU18U_TR6_DESC);
        createSessionsHier("CREATE-SESS-HIER TU19U TR7 ASC", UserTestData.USERS_TU19U_TR7_ASC);
    }

    /**
     *
     * @param msg
     * @param uArray
     */
    public static void createSessionsHier(String msg, String[][] uArray)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            for (String[] usr : uArray)
            {
                User user = UserTestData.getUser(usr);
                Session session = accessMgr.createSession(user, false);
                assertNotNull(session);
                String userId = accessMgr.getUserId(session);
                assertTrue(CLS_NM + ".createSessionsHier failed compare found userId [" + userId + "] valid userId [" + UserTestData.getUserId(usr) + "]", userId.equalsIgnoreCase(UserTestData.getUserId(usr)));
                UserTestData.assertEquals(user, usr);

                // Get the authorized roles for this user:
                Set<String> authZRoles = UserTestData.getAuthorizedRoles(usr);

                // If there are any assigned roles, add them to list of authorized.
                Set<String> asgnRoles = UserTestData.getAssignedRoles(usr);
                assertNotNull(asgnRoles);
                assertTrue(asgnRoles.size() > 0);
                for (String asgnRole : asgnRoles)
                {
                    authZRoles.add(asgnRole);
                }

                Set<String> actualRoles = accessMgr.authorizedRoles(session);
                assertNotNull(actualRoles);
                assertEquals(CLS_NM + ".createSessionsHier authorized roles list size test case failed for [" + user.getUserId() + "]", authZRoles.size(), actualRoles.size());
                for (String name : authZRoles)
                {
                    assertTrue(CLS_NM + ".createSessionsHier authorized roles compare test case failed for USER [" + user.getUserId() + "] expect role [" + name + "] nout found", actualRoles.contains(name));
                }
            }
            log.debug(CLS_NM + ".createSessionsHier successful");
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".createSessionsHier: failed with SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }

    /**
     *
     */
    public void testCheckAccess()
    {
        // public boolean checkAccess(String object, String operation, Session session)
        checkAccess("CHCK-ACS TU1_UPD TO1 TOP1 ", UserTestData.USERS_TU1_UPD, PermTestData.OBJS_TOB1, PermTestData.OPS_TOP1, PermTestData.OBJS_TOB3, PermTestData.OPS_TOP3);
        checkAccess("CHCK-ACS TU3 TO3 TOP1 ", UserTestData.USERS_TU3, PermTestData.OBJS_TOB3, PermTestData.OPS_TOP3, PermTestData.OBJS_TOB2, PermTestData.OPS_TOP1);
        checkAccess("CHCK-ACS TU4 TO4 TOP1 ", UserTestData.USERS_TU4, PermTestData.OBJS_TOB2, PermTestData.OPS_TOP2, PermTestData.OBJS_TOB2, PermTestData.OPS_TOP1);
    }

    /**
     * @param msg
     * @param uArray
     * @param oArray
     * @param opArray
     * @param oArrayBad
     * @param opArrayBad
     */
    public static void checkAccess(String msg, String[][] uArray, String[][] oArray, String[][] opArray, String[][] oArrayBad, String[][] opArrayBad)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            for (String[] usr : uArray)
            {
                User user = UserTestData.getUser(usr);
                Session session = accessMgr.createSession(user, false);
                assertNotNull(session);
                int i = 0;
                for (String[] obj : oArray)
                {
                    int j = 0;
                    for (String[] op : opArray)
                    {
                        // Call checkAccess method
                        assertTrue(CLS_NM + ".checkAccess failed userId [" + user.getUserId() + "] Perm objectName [" + PermTestData.getName(obj) + "] operationName [" + PermTestData.getName(op) + "]",
                            accessMgr.checkAccess(session, new Permission(PermTestData.getName(obj), PermTestData.getName(op))));

                        // Call checkAccess method (this should fail):
                        assertTrue(CLS_NM + ".checkAccess failed userId [" + user.getUserId() + "] Perm objectName [" + PermTestData.getName(oArrayBad[i]) + "] operationName [" + PermTestData.getName(opArrayBad[j]) + "]",
                            !accessMgr.checkAccess(session, new Permission(PermTestData.getName(oArrayBad[i]),PermTestData.getName(opArrayBad[j]))));

                        j++;
                    }
                    i++;
                }
            }
            log.debug(CLS_NM + ".checkAccess successful");
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".checkAccess: failed with SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }

    /**
     *
     */
    public void testSessionPermission()
    {
        // public List<Permission> sessionPermissions(Session session)
        // public static void sessionPermissions(String msg, String[][] uArray, String[][] oArray, String[][] opArray)
        sessionPermissions("SESS-PRMS TU1_UPD TO1 TOP1 ", UserTestData.USERS_TU1_UPD, PermTestData.OBJS_TOB1, PermTestData.OPS_TOP1);
        sessionPermissionsH("SESS-PRMS_H USERS_TU7_HIER OBJS_TOB4 OPS_TOP4 ", UserTestData.USERS_TU7_HIER, PermTestData.OBJS_TOB4, PermTestData.OPS_TOP4);
    }

    /**
     * @param msg
     * @param uArray
     * @param oArray
     * @param opArray
     */
    public static void sessionPermissions(String msg, String[][] uArray, String[][] oArray, String[][] opArray)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            for (String[] usr : uArray)
            {
                User user = UserTestData.getUser(usr);
                Session session = accessMgr.createSession(user, false);
                assertNotNull(session);
                List<Permission> pOps = accessMgr.sessionPermissions(session);
                assertNotNull(pOps);
                // There should be objs * ops number of perms in the list returned from sessionPermissions method:
                assertEquals(CLS_NM +
                    ".sessionPermissions failed list size user[" + user.getUserId() + "]",
                    oArray.length * opArray.length, pOps.size());

                // Iterate over objs x ops, see if every expected valid permission is contained within the returned list:
                for (String[] obj : oArray)
                {
                    for (String[] op : opArray)
                    {
                        Permission validPOp = PermTestData.getOp(PermTestData.getName(obj), op);
                        assertTrue(CLS_NM +
                            ".sessionPermissions failed perm list compare USER [" + user.getUserId() +
                            "] PERM Obj [" + PermTestData.getName(obj) + "] " +
                            "OPER [" + PermTestData.getName(op) + "]",
                            pOps.contains(validPOp));
                    }
                }
            }
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".sessionPermissions: failed with SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }

    /**
     *
     * @param msg
     * @param uArray
     * @param oArray
     * @param opArray
     */
    public static void sessionPermissionsH(String msg, String[][] uArray, String[][] oArray, String[][] opArray)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            int i = 0;
            for (String[] usr : uArray)
            {
                i++;
                User user = UserTestData.getUser(usr);
                Session session = accessMgr.createSession(user, false);
                assertNotNull(session);
                List<Permission> pOps = accessMgr.sessionPermissions(session);
                assertNotNull(pOps);
                //log.warn(CLS_NM + ".sessionPermissionsH list size user [" + user.getUserId() + "] expected len=" +
                //    (11 - i) * opArray.length + " actual len=" + pOps.size());
                assertEquals(CLS_NM +
                    ".sessionPermissionsH failed list size user[" + user.getUserId() + "]",
                    (11 - i) * opArray.length, pOps.size());

                // Iterate over objs x ops, see if every expected valid permission is contained within the returned list:
                int j = 0;
                for (String[] obj : oArray)
                {
                    j++;
                    // role1 inherits 2 - 10, role 2 inherits 3 - 10, role 3 inherits 4 - 10, etc.
                    // obj1.op1 - 10 are granted to role1, obj2.op1 - 10 are granted to role2, etc...
                    // positive tests:
                    if(i == j || i < j)
                    {
                        int k = 0;
                        for (String[] op : opArray)
                        {
                            k++;
                            Permission validPOp = PermTestData.getOp(PermTestData.getName(obj), op);
                            assertTrue(CLS_NM +
                                ".sessionPermissionsH failed perm list compare USER [" + user.getUserId() +
                                "] PERM Obj [" + PermTestData.getName(obj) + "] " +
                                "OPER [" + PermTestData.getName(op) + "]",
                                pOps.contains(validPOp));

                            boolean result = accessMgr.checkAccess(session, new Permission(PermTestData.getName(obj), PermTestData.getName(op)));
                            assertTrue(CLS_NM +
                                ".sessionPermissionsH failed checkAccess USER [" + user.getUserId() +
                                "] PERM Obj [" + PermTestData.getName(obj) + "] " +
                                "OPER [" + PermTestData.getName(op) + "]",
                                result);
                        }
                    }
                    // negative tests:
                    else
                    {
                        int k = 0;
                        for (String[] op : opArray)
                        {
                            k++;
                            Permission validPOp = PermTestData.getOp(PermTestData.getName(obj), op);
                            assertTrue(CLS_NM +
                                ".sessionPermissionsH failed negative perm list compare USER [" + user.getUserId() +
                                "] PERM Obj [" + PermTestData.getName(obj) + "] " +
                                "OPER [" + PermTestData.getName(op) + "]",
                                !pOps.contains(validPOp));

                            boolean result = accessMgr.checkAccess(session, new Permission(PermTestData.getName(obj), PermTestData.getName(op)));
                            assertTrue(CLS_NM +
                                ".sessionPermissionsH failed negative checkAccess USER [" + user.getUserId() +
                                "] PERM Obj [" + PermTestData.getName(obj) + "] " +
                                "OPER [" + PermTestData.getName(op) + "]",
                                !result);
                        }
                    }
                }
            }
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".sessionPermissionsH: failed with SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }

    /**
     *
     */
    public void testSessionRole()
    {
        // public List<UserRole> sessionRoles(Session session)
        sessionRoles("SESS-RLS TU1_UPD TR1", UserTestData.USERS_TU1_UPD, RoleTestData.ROLES_TR1);
        sessionRoles("SESS-RLS TU3 TR3", UserTestData.USERS_TU3, RoleTestData.ROLES_TR3);
        sessionRoles("SESS-RLS TU4 TR2", UserTestData.USERS_TU4, RoleTestData.ROLES_TR2);
    }

    /**
     * @param msg
     * @param uArray
     * @param rArray
     */
    public static void sessionRoles(String msg, String[][] uArray, String[][] rArray)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            for (String[] usr : uArray)
            {
                User user = UserTestData.getUser(usr);
                Session session = accessMgr.createSession(user, false);
                assertNotNull(session);
                String userId = accessMgr.getUserId(session);
                assertTrue(CLS_NM + ".sessionRoles failed compare found userId [" + userId + "] valid userId [" + UserTestData.getUserId(usr) + "]", userId.equalsIgnoreCase(UserTestData.getUserId(usr)));
                UserTestData.assertEquals(user, usr);
                List<UserRole> uRoles = accessMgr.sessionRoles(session);
                assertNotNull(uRoles);
                assertEquals(CLS_NM + ".sessionRoles user role check failed list size user [" + user.getUserId() + "]", rArray.length, uRoles.size());
                for (String[] rle : rArray)
                {
                    assertTrue(CLS_NM + ".sessionRoles failed role search USER [" + user.getUserId() + "] ROLE1 [" + RoleTestData.getName(rle) + "] should be present", uRoles.contains(RoleTestData.getUserRole(UserTestData.getUserId(usr), rle)));
                }
            }
            log.debug(CLS_NM + ".sessionRoles successful");
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".sessionRoles: failed with SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }

    /**
     *
     */
    public void testAddActiveRole()
    {
        // public void addActiveRole(Session session, String role)
        addActiveRoles("ADD-ACT-RLS TU1_UPD TR1 bad:TR2", UserTestData.USERS_TU1_UPD, RoleTestData.ROLES_TR1, RoleTestData.ROLES_TR2);
        addActiveRoles("ADD-ACT-RLS TU3 TR3 bad:TR1:", UserTestData.USERS_TU3, RoleTestData.ROLES_TR3, RoleTestData.ROLES_TR1);
        addActiveRoles("ADD-ACT-RLS TU4 TR2 bad:TR1", UserTestData.USERS_TU4, RoleTestData.ROLES_TR2, RoleTestData.ROLES_TR1);
        addActiveRolesDSD("ADD-ACT-RLS-USRS_DSDT1 TU8 DSD_T1", UserTestData.USERS_TU8_SSD, RoleTestData.DSD_T1);
        addActiveRolesDSD("ADD-ACT-RLS-USRS_DSDT4B TU9 DSD_T4_B", UserTestData.USERS_TU9_SSD_HIER, RoleTestData.DSD_T4_B);
        addActiveRolesDSD("ADD-ACT-RLS-USRS_DSDT5B TU10 DSD_T5_B", UserTestData.USERS_TU10_SSD_HIER, RoleTestData.DSD_T5_B);
        addActiveRolesDSD("ADD-ACT-RLS-USRS_DSDT6B TU11 DSD_T6_B", UserTestData.USERS_TU11_SSD_HIER, RoleTestData.DSD_T6_B);
    }

    /**
     * @param msg
     * @param uArray
     * @param rPosArray
     * @param rNegArray
     */
    public static void addActiveRoles(String msg, String[][] uArray, String[][] rPosArray, String[][] rNegArray)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            for (String[] usr : uArray)
            {
                User user = UserTestData.getUser(usr);
                Session session = accessMgr.createSession(user, false);
                assertNotNull(session);
                List<UserRole> uRoles = session.getRoles();
                assertNotNull(uRoles);
                assertEquals(CLS_NM + ".addActiveRoles failed list size user[" + user.getUserId() + "]", rPosArray.length, uRoles.size());
                for (String[] rle : rPosArray)
                {
                    assertTrue(CLS_NM + ".addActiveRoles failed role search USER [" + user.getUserId() + "] ROLE [" + RoleTestData.getName(rle) + "] should be present", uRoles.contains(RoleTestData.getUserRole(UserTestData.getUserId(usr), rle)));
                }
                // Attempt to activate roles that aren't assigned to user:
                for (String[] badRle : rNegArray)
                {
                    try
                    {
                        // Add Role (this better fail):
                        accessMgr.addActiveRole(session, new UserRole(RoleTestData.getName(badRle)));
                        String error = CLS_NM + ".addActiveRoles failed negative test 1 User [" + user.getUserId() + "] Role [" + RoleTestData.getName(badRle) + "]";
                        log.info(error);
                        fail(error);
                    }
                    catch (SecurityException se)
                    {
                        assertTrue(CLS_NM + "addActiveRoles excep id check", se.getErrorId() == GlobalErrIds.URLE_ACTIVATE_FAILED);
                        // pass
                    }
                }
                // remove all roles from the user's session:
                int ctr = rPosArray.length;
                for (String[] rle : rPosArray)
                {
                    // Drop Role:
                    accessMgr.dropActiveRole(session, new UserRole(RoleTestData.getName(rle)));
                    assertEquals(CLS_NM + ".addActiveRoles failed list size user[" + user.getUserId() + "]", (--ctr), session.getRoles().size());
                    assertTrue(CLS_NM + ".addActiveRoles failed role search USER [" + user.getUserId() + "] ROLE [" + RoleTestData.getName(rle) + "] should not contain role", !session.getRoles().contains(RoleTestData.getUserRole(UserTestData.getUserId(usr), rle)));
                    // Drop Role again: (This better fail because role  has already been deactivated from user's session)
                    try
                    {
                        // Drop Role3 (this better fail):
                        accessMgr.dropActiveRole(session, new UserRole(RoleTestData.getName(rle)));
                        String error = CLS_NM + ".addActiveRoles failed negative test 2 User [" + user.getUserId() + "] Role [" + RoleTestData.getName(rle) + "]";
                        log.info(error);
                        fail(error);
                    }
                    catch (SecurityException se)
                    {
                        assertTrue(CLS_NM + "addActiveRoles excep id check", se.getErrorId() == GlobalErrIds.URLE_NOT_ACTIVE);
                    }
                }
                // Now activate the list of assigned roles:
                ctr = 0;
                for (String[] rle : rPosArray)
                {
                    // Activate Role(s):
                    accessMgr.addActiveRole(session, new UserRole(RoleTestData.getName(rle)));
                    uRoles = session.getRoles();
                    assertEquals(CLS_NM + ".addActiveRoles failed list size user [" + user.getUserId() + "]", ++ctr, uRoles.size());
                    assertTrue(CLS_NM + ".addActiveRoles failed role search USER [" + user.getUserId() + "] ROLE [" + RoleTestData.getName(rle) + "] should contain role", uRoles.contains(RoleTestData.getUserRole(UserTestData.getUserId(usr), rle)));
                    try
                    {
                        // Activate Role again (this should throw SecurityException):
                        accessMgr.addActiveRole(session, new UserRole(RoleTestData.getName(rle)));
                        String error = CLS_NM + ".addActiveRoles failed test 3 User [" + user.getUserId() + "] Role [" + RoleTestData.getName(rle) + "]";
                        log.info(error);
                        fail(error);
                    }
                    catch (SecurityException se)
                    {
                        assertTrue(CLS_NM + "addActiveRoles excep id check", se.getErrorId() == GlobalErrIds.URLE_ALREADY_ACTIVE);
                        // this is good
                    }
                }
            }
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".addActiveRoles: failed with SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }

    /**
     *
     */
    public void testDropActiveRole()
    {
        // public void dropActiveRole(Session session, String role)
        dropActiveRoles("DRP-ACT-RLS TU1_UPD TR1 bad:TR2", UserTestData.USERS_TU1_UPD, RoleTestData.ROLES_TR1);
        dropActiveRoles("DRP-ACT-RLS TU3 TR3 bad:TR1", UserTestData.USERS_TU3, RoleTestData.ROLES_TR3);
        dropActiveRoles("DRP-ACT-RLS TU4 TR2 bad:TR1", UserTestData.USERS_TU4, RoleTestData.ROLES_TR2);
    }

    /**
     * @param msg
     * @param uArray
     * @param rArray
     */
    public static void dropActiveRoles(String msg, String[][] uArray, String[][] rArray)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            for (String[] usr : uArray)
            {
                User user = UserTestData.getUser(usr);
                Session session = accessMgr.createSession(user, false);
                assertNotNull(session);
                List<UserRole> uRoles = session.getRoles();
                assertNotNull(uRoles);
                assertEquals(CLS_NM + ".dropActiveRoles failed list size user[" + user.getUserId() + "]", rArray.length, uRoles.size());
                for (String[] rle : rArray)
                {
                    assertTrue(CLS_NM + ".dropActiveRoles failed role search USER [" + user.getUserId() + "] ROLE [" + RoleTestData.getName(rle) + "] should be present", uRoles.contains(RoleTestData.getUserRole(UserTestData.getUserId(usr), rle)));
                }
                // remove all roles from the user's session:
                int ctr = rArray.length;
                for (String[] rle : rArray)
                {
                    // Drop Role:
                    accessMgr.dropActiveRole(session, new UserRole(RoleTestData.getName(rle)));
                    assertEquals(CLS_NM + ".dropActiveRoles failed list size user[" + user.getUserId() + "]", (--ctr), session.getRoles().size());
                    assertTrue(CLS_NM + ".dropActiveRoles failed role search USER [" + user.getUserId() + "] ROLE [" + RoleTestData.getName(rle) + "] should not contain role", !session.getRoles().contains(RoleTestData.getUserRole(UserTestData.getUserId(usr), rle)));
                    // Drop Role again: (This better fail because role  has already been deactivated from user's session)
                    try
                    {
                        // Drop Role3 (this better fail):
                        accessMgr.dropActiveRole(session, new UserRole(RoleTestData.getName(rle)));
                        String error = CLS_NM + ".dropActiveRoles failed negative test 2 User [" + user.getUserId() + "] Role [" + RoleTestData.getName(rle) + "]";
                        log.info(error);
                        fail(error);
                    }
                    catch (SecurityException se)
                    {
                        assertTrue(CLS_NM + "dropActiveRoles excep id check", se.getErrorId() == GlobalErrIds.URLE_NOT_ACTIVE);
                    }
                }
            }
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".dropActiveRoles: failed with SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }

    public void addActiveRolesDSD(String msg, String[][] uArray, String[][] sArray)
    {
        LogUtil.logIt(msg);
        try
        {
            AccessMgr accessMgr = AccessMgrFactory.createInstance();
            int i = 0;
            for (String[] usr : uArray)
            {
                SDSet dsd = RoleTestData.getSDSet(sArray[i++]);
                //Set<String> roles = dsd.getMembers().keySet();
                Set<String> roles = dsd.getMembers();
                User user = UserTestData.getUser(usr);
                Session session = accessMgr.authenticate(user.getUserId(), user.getPassword());
                int j = 0;
                for(String role : roles)
                {
                    j++;
                    try
                    {
                        assertNotNull(session);

                        // Activate Role(s):
                        accessMgr.addActiveRole(session, new UserRole(role));
                        if(j >= dsd.getCardinality())
                        {
                            fail(CLS_NM + ".addActiveRolesDSD user [" + user.getUserId() + "] role [" + role + "] ssd [" + dsd.getName() + "] cardinality [" + dsd.getCardinality() + "] count [" + j + "] failed");
                        }
                    }
                    catch (SecurityException ex)
                    {
                        assertTrue(CLS_NM + ".addActiveRolesDSD cardinality test failed user [" + user.getUserId() + "] role [" + role + "] ssd [" + dsd.getName() + "] cardinality [" + dsd.getCardinality() + "] count [" + j + "]", j >= (dsd.getCardinality()));
                        assertTrue(CLS_NM + ".addActiveRolesDSD cardinality test failed [" + UserTestData.getUserId(usr) + "]", ex.getErrorId() == GlobalErrIds.DSD_VALIDATION_FAILED);
                        // still good, break from loop, we're done here
                        break;
                    }
                }
            }
        }
        catch (SecurityException ex)
        {
            log.error(CLS_NM + ".addActiveRolesDSD caught SecurityException errCode=" + ex.getErrorId() + ", msg=" + ex.getMessage(), ex);
            fail(ex.getMessage());
        }
    }
}