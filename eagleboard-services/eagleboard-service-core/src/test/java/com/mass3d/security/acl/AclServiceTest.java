package com.mass3d.security.acl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Sets;
import com.mass3d.EagleboardSpringTest;
import com.mass3d.common.IdentifiableObjectManager;
import com.mass3d.dataelement.DataElement;
import com.mass3d.feedback.ErrorReport;
import com.mass3d.user.CurrentUserService;
import com.mass3d.user.User;
import com.mass3d.user.UserGroup;
import com.mass3d.user.UserGroupAccess;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AclServiceTest
    extends EagleboardSpringTest {

  @Autowired
  private AclService aclService;

  @Autowired
  private IdentifiableObjectManager manager;

  @Autowired
  private CurrentUserService currentUserService;

  @Test
  public void testUpdateObjectWithPublicRWFail() {
    User user = createAdminUser("F_OPTIONSET_PUBLIC_ADD");
    DataElement dataElement = createDataElement('A');
    dataElement.setPublicAccess(AccessStringHelper.READ_WRITE);

    assertFalse(aclService.canUpdate(user, dataElement));
  }

  @Test
  public void testUpdateObjectWithPublicWFail() {
    User user = createAdminUser("F_OPTIONSET_PUBLIC_ADD");
    DataElement dataElement = createDataElement('A');
    dataElement.setPublicAccess(AccessStringHelper.WRITE);

    assertFalse(aclService.canUpdate(user, dataElement));
  }

  @Test
  public void testUpdateObjectWithPublicRFail() {
    User user = createAdminUser("F_OPTIONSET_PUBLIC_ADD");
    DataElement dataElement = createDataElement('A');
    dataElement.setPublicAccess(AccessStringHelper.READ);

    assertFalse(aclService.canUpdate(user, dataElement));
  }

  @Test
  public void testUpdateObjectWithPublicRUserOwner() {
    User user = createAdminUser("F_DATAFIELD_PUBLIC_ADD");
    DataElement dataElement = createDataElement('A');
    dataElement.setUser(user);
    dataElement.setPublicAccess(AccessStringHelper.READ);

    assertTrue(aclService.canUpdate(user, dataElement));
  }

  @Test
  public void testUpdateObjectWithPublicRWSuccessPublic() {
    User user = createAdminUser("F_DATAFIELD_PUBLIC_ADD");
    DataElement dataElement = createDataElement('A');
    dataElement.setPublicAccess(AccessStringHelper.READ_WRITE);

    assertTrue(aclService.canUpdate(user, dataElement));
  }

  @Test
  public void testUpdateObjectWithPublicRWSuccessPrivate1() {
    User user = createAdminUser("F_DATAFIELD_PRIVATE_ADD");
    DataElement dataElement = createDataElement('A');
    dataElement.setUser(user);
    dataElement.setPublicAccess(AccessStringHelper.READ_WRITE);

    assertFalse(aclService.canUpdate(user, dataElement));
  }

  @Test
  public void testUpdateObjectWithPublicRWSuccessPrivate2() {
    User user = createAdminUser("F_DATAFIELD_PRIVATE_ADD");
    DataElement dataElement = createDataElement('A');
    dataElement.setPublicAccess(AccessStringHelper.READ_WRITE);

    assertFalse(aclService.canUpdate(user, dataElement));
  }

  @Test
  public void testVerifyDataFieldPrivateRW() {
    User user = createAdminUser("F_DATAFIELD_PRIVATE_ADD");

    DataElement dataElement = createDataElement('A');
    dataElement.setPublicAccess(AccessStringHelper.READ_WRITE);

    assertFalse(aclService.verifySharing(dataElement, user).isEmpty());
  }

  @Test
  public void testVerifyDataFieldPrivate() {
    User user = createAdminUser("F_DATAFIELD_PRIVATE_ADD");

    DataElement dataElement = createDataElement('A');
    dataElement.setPublicAccess(AccessStringHelper.DEFAULT);

    assertTrue(aclService.verifySharing(dataElement, user).isEmpty());
  }

  @Test
  public void testVerifyDataFieldPublicRW() {
    User user = createAdminUser("F_DATAFIELD_PUBLIC_ADD");

    DataElement dataElement = createDataElement('A');
    dataElement.setPublicAccess(AccessStringHelper.READ_WRITE);

    assertTrue(aclService.verifySharing(dataElement, user).isEmpty());
  }

  @Test
  public void testVerifyDataFieldPublic() {
    User user = createAdminUser("F_DATAFIELD_PUBLIC_ADD");

    DataElement dataElement = createDataElement('A');
    dataElement.setPublicAccess(AccessStringHelper.DEFAULT);

    assertTrue(aclService.verifySharing(dataElement, user).isEmpty());
  }

  @Test
  public void testDataFieldSharingPrivateRW() {
    User user1 = createUser("user1", "F_DATAFIELD_PRIVATE_ADD");
    User user2 = createUser("user2", "F_DATAFIELD_PRIVATE_ADD");

    DataElement dataElement = createDataElement('A');
    dataElement.setUser(user1);
    manager.save(dataElement);

    assertFalse(aclService.canUpdate(user2, dataElement));
    assertEquals(AccessStringHelper.DEFAULT, dataElement.getPublicAccess());

    UserGroup userGroup = createUserGroup('A', new HashSet<>());
    userGroup.getMembers().add(user1);
    userGroup.getMembers().add(user2);

    manager.save(userGroup);

    dataElement.getUserGroupAccesses().add(new UserGroupAccess(userGroup, "rw------"));
    manager.update(dataElement);

    assertTrue(aclService.canUpdate(user2, dataElement));
  }

  @Test
  public void testReadPrivateDataFieldSharedThroughGroup() {
    User user1 = createUser("user1", "F_DATAFIELD_PRIVATE_ADD");
    User user2 = createUser("user2", "F_DATAFIELD_PRIVATE_ADD");

    manager.save(user1);
    manager.save(user2);

    UserGroup userGroup = createUserGroup('A', Sets.newHashSet(user1, user2));
    manager.save(userGroup);
    DataElement dataElement = createDataElement('A');
    dataElement.setPublicAccess(AccessStringHelper.DEFAULT);
    dataElement.setUser(user1);

    assertTrue(aclService.canWrite(user1, dataElement));
    manager.save(dataElement);

    UserGroupAccess userGroupAccess = new UserGroupAccess(userGroup, AccessStringHelper.READ);
    dataElement.getUserGroupAccesses().add(userGroupAccess);

    assertTrue(aclService.canUpdate(user1, dataElement));
    manager.update(dataElement);
    assertTrue(aclService.canRead(user1, dataElement));
    assertTrue(aclService.canWrite(user1, dataElement));
    assertTrue(aclService.canUpdate(user1, dataElement));
    assertFalse(aclService.canDelete(user1, dataElement));
    assertTrue(aclService.canManage(user1, dataElement));

    Access access = aclService.getAccess(dataElement, user2);
    assertFalse(access.isManage()); // 3
    assertFalse(access.isWrite());  // 1
    assertTrue(access.isRead());
    assertFalse(access.isUpdate()); // 2
    assertFalse(access.isDelete());

    assertFalse(aclService.canManage(user2, dataElement)); // 6
    assertFalse(aclService.canWrite(user2, dataElement)); // 4
    assertTrue(aclService.canRead(user2, dataElement));
    assertFalse(aclService.canUpdate(user2, dataElement)); // 5
    assertFalse(aclService.canDelete(user2, dataElement));
  }

  @Test
  public void testUpdatePrivateDataFieldSharedThroughGroup() {
    User user1 = createUser("user1", "F_DATAFIELD_PRIVATE_ADD");
    User user2 = createUser("user2", "F_DATAFIELD_PRIVATE_ADD");

    manager.save(user1);
    manager.save(user2);

    UserGroup userGroup = createUserGroup('A', Sets.newHashSet(user1, user2));
    manager.save(userGroup);

    DataElement dataElement = createDataElement('A');
    dataElement.setPublicAccess(AccessStringHelper.DEFAULT);
    dataElement.setUser(user1);

    manager.save(dataElement);

    UserGroupAccess userGroupAccess = new UserGroupAccess(userGroup, AccessStringHelper.READ_WRITE);
    dataElement.getUserGroupAccesses().add(userGroupAccess);
    manager.update(dataElement);

    assertTrue(aclService.canRead(user1, dataElement));
    assertTrue(aclService.canUpdate(user1, dataElement));
    assertFalse(aclService.canDelete(user1, dataElement));
    assertTrue(aclService.canManage(user1, dataElement));

    Access access = aclService.getAccess(dataElement, user2);
    assertTrue(access.isRead());
    assertTrue(access.isWrite());
    assertTrue(access.isUpdate());
    assertFalse(access.isDelete());
    assertTrue(access.isManage());

    assertTrue(aclService.canRead(user2, dataElement));
    assertTrue(aclService.canWrite(user2, dataElement));
    assertTrue(aclService.canUpdate(user2, dataElement));
    assertFalse(aclService.canDelete(user2, dataElement));
    assertTrue(aclService.canManage(user2, dataElement));
  }

  @Test
  public void testBlockMakePublic() {
    User user1 = createUser("user1", "F_DATAFIELD_PRIVATE_ADD");
    manager.save(user1);

    DataElement dataElement = createDataElement('A');
    dataElement.setPublicAccess(AccessStringHelper.DEFAULT);
    dataElement.setUser(user1);

    assertTrue(aclService.canWrite(user1, dataElement));
    manager.save(dataElement);

    dataElement.setPublicAccess(AccessStringHelper.READ_WRITE);
    assertFalse(aclService.canUpdate(user1, dataElement));
  }

  @Test
  public void testAllowSuperuserMakePublic1() {
    User user1 = createUser("user1", "F_DATAFIELD_PRIVATE_ADD");
    User user2 = createUser("user2", "ALL");
    manager.save(user1);
    manager.save(user2);

    DataElement dataElement = createDataElement('A');
    dataElement.setPublicAccess(AccessStringHelper.DEFAULT);
    dataElement.setUser(user1);

    assertTrue(aclService.canWrite(user1, dataElement));
    manager.save(dataElement);

    dataElement.setPublicAccess(AccessStringHelper.READ_WRITE);
    assertTrue(aclService.canUpdate(user2, dataElement));
  }

  @Test
  public void testAllowMakePublic() {
    User user1 = createUser("user1", "F_DATAFIELD_PUBLIC_ADD");
    manager.save(user1);

    DataElement dataElement = createDataElement('A');
    dataElement.setPublicAccess(AccessStringHelper.DEFAULT);
    dataElement.setUser(user1);

    Access access = aclService.getAccess(dataElement, user1);
    assertTrue(access.isRead());
    assertTrue(access.isWrite());
    assertTrue(access.isUpdate());
    assertFalse(access.isDelete());

    manager.save(dataElement);

    dataElement.setPublicAccess(AccessStringHelper.READ_WRITE);

    access = aclService.getAccess(dataElement, user1);
    assertTrue(access.isRead());
    assertTrue(access.isWrite());
    assertTrue(access.isUpdate());
    assertFalse(access.isDelete());

    assertTrue(aclService.canUpdate(user1, dataElement));
  }

  @Test
  public void testSuperuserOverride() {
    User user1 = createUser("user1", "F_DATAFIELD_PRIVATE_ADD");
    User user2 = createUser("user2", "F_DATAFIELD_PRIVATE_ADD");
    User user3 = createUser("user3", "ALL");

    UserGroup userGroup = createUserGroup('A', Sets.newHashSet(user1, user2));
    manager.save(userGroup);

    DataElement dataElement = createDataElement('A');
    dataElement.setPublicAccess(AccessStringHelper.DEFAULT);
    dataElement.setUser(user1);

    manager.save(dataElement);

    UserGroupAccess userGroupAccess = new UserGroupAccess(userGroup, AccessStringHelper.READ_WRITE);
    dataElement.getUserGroupAccesses().add(userGroupAccess);
    manager.update(dataElement);

    assertTrue(aclService.canRead(user1, dataElement));
    assertTrue(aclService.canUpdate(user1, dataElement));
    assertFalse(aclService.canDelete(user1, dataElement));
    assertTrue(aclService.canManage(user1, dataElement));

    Access access = aclService.getAccess(dataElement, user2);
    assertTrue(access.isRead());
    assertTrue(access.isWrite());
    assertTrue(access.isUpdate());
    assertFalse(access.isDelete());
    assertTrue(access.isManage());

    assertTrue(aclService.canRead(user2, dataElement));
    assertTrue(aclService.canWrite(user2, dataElement));
    assertTrue(aclService.canUpdate(user2, dataElement));
    assertFalse(aclService.canDelete(user2, dataElement));
    assertTrue(aclService.canManage(user2, dataElement));

    access = aclService.getAccess(dataElement, user3);
    assertTrue(access.isRead());
    assertTrue(access.isWrite());
    assertTrue(access.isUpdate());
    assertTrue(access.isDelete());
    assertTrue(access.isManage());

    assertTrue(aclService.canRead(user3, dataElement));
    assertTrue(aclService.canWrite(user3, dataElement));
    assertTrue(aclService.canUpdate(user3, dataElement));
    assertTrue(aclService.canDelete(user3, dataElement));
    assertTrue(aclService.canManage(user3, dataElement));
  }

  @Test
  public void shouldUseAuthoritiesIfSharingPropsAreNullOrEmptyWithPublicAuth() {
    User user1 = createUser("user1", "F_DATAFIELD_PUBLIC_ADD");
    User user2 = createUser("user2", "F_DATAFIELD_PUBLIC_ADD");

    injectSecurityContext(user1);

    DataElement dataElement = createDataElement('A');
    dataElement.setUser(user1);

    Access access = aclService.getAccess(dataElement, user1);
    assertTrue(access.isRead());
    assertTrue(access.isWrite());
    assertTrue(access.isUpdate());
    assertFalse(access.isDelete());

    assertTrue(aclService.canUpdate(user1, dataElement));

    manager.save(dataElement);

    dataElement.setPublicAccess(null);
    manager.update(dataElement);

    injectSecurityContext(user2);

    access = aclService.getAccess(dataElement, user2);
    assertTrue(access.isRead());
    assertTrue(access.isWrite());
    assertTrue(access.isUpdate());
    assertFalse(access.isDelete());

    assertTrue(aclService.canUpdate(user2, dataElement));

    List<ErrorReport> errorReports = aclService.verifySharing(dataElement, user2);
    assertTrue(errorReports.isEmpty());
  }

  @Test
  public void shouldUseAuthoritiesIfSharingPropsAreNullOrEmptyWithPrivateAuth() {
    User user1 = createUser("user1", "F_DATAFIELD_PRIVATE_ADD");
    User user2 = createUser("user2", "F_DATAFIELD_PRIVATE_ADD");

    injectSecurityContext(user1);

    DataElement dataElement = createDataElement('A');
    dataElement.setUser(user1);
    dataElement.setPublicAccess(AccessStringHelper.DEFAULT);

    Access access = aclService.getAccess(dataElement, user1);
    assertTrue(access.isRead());
    assertTrue(access.isWrite());
    assertTrue(access.isUpdate());
    assertFalse(access.isDelete());

    manager.save(dataElement);

    dataElement.setPublicAccess(AccessStringHelper.DEFAULT);
    manager.update(dataElement);

    injectSecurityContext(user2);

    access = aclService.getAccess(dataElement, user2);
    assertFalse(access.isRead());
    assertFalse(access.isWrite());
    assertFalse(access.isUpdate());
    assertFalse(access.isDelete());

    assertFalse(aclService.canUpdate(user2, dataElement));

    List<ErrorReport> errorReports = aclService.verifySharing(dataElement, user2);
    assertTrue(errorReports.isEmpty());
  }

  @Test
  public void testDefaultShouldBlockReadsFromOtherUsers() {
    User user1 = createUser("user1", "F_DATAFIELD_PUBLIC_ADD");
    User user2 = createUser("user2", "F_DATAFIELD_PUBLIC_ADD");

    injectSecurityContext(user1);

    DataElement dataElement = createDataElement('A');
    dataElement.setUser(user1);

    Access access = aclService.getAccess(dataElement, user1);
    assertTrue(access.isRead());
    assertTrue(access.isWrite());
    assertTrue(access.isUpdate());
    assertFalse(access.isDelete());

    manager.save(dataElement);

    dataElement.setPublicAccess(AccessStringHelper.DEFAULT);
    manager.update(dataElement);

    injectSecurityContext(user2);

    access = aclService.getAccess(dataElement, user2);
    assertFalse(access.isRead());
    assertFalse(access.isWrite());
    assertFalse(access.isUpdate());
    assertFalse(access.isDelete());

    assertFalse(aclService.canUpdate(user2, dataElement));

    List<ErrorReport> errorReports = aclService.verifySharing(dataElement, user2);
    assertTrue(errorReports.isEmpty());
  }
}
