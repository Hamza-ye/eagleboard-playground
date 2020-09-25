package com.mass3d.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Sets;
import com.mass3d.EagleboardSpringTest;
import com.mass3d.datafield.DataField;
import com.mass3d.datafield.DataFieldService;
import com.mass3d.hibernate.exception.CreateAccessDeniedException;
import com.mass3d.hibernate.exception.DeleteAccessDeniedException;
import com.mass3d.hibernate.exception.UpdateAccessDeniedException;
import com.mass3d.security.acl.AccessStringHelper;
import com.mass3d.user.User;
import com.mass3d.user.UserGroup;
import com.mass3d.user.UserGroupAccess;
import com.mass3d.user.UserService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class IdentifiableObjectManagerTest
    extends EagleboardSpringTest
{
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private DataFieldService dataFieldService;

    @Autowired
    private IdentifiableObjectManager identifiableObjectManager;

    @Autowired
    private UserService _userService;

    @Override
    protected void setUpTest() throws Exception
    {
        this.userService = _userService;
    }

//    @Test
//    public void testGetObjectWithIdScheme()
//    {
//        DataField dataFieldA = createDataField( 'A' );
//
//        dataFieldService.addDataField( dataFieldA );
//
//        assertEquals( dataFieldA, identifiableObjectManager.get( DataDimensionItem.DATA_DIMENSION_CLASSES, IdScheme.CODE, dataFieldA.getCode() ) );
//        assertEquals( dataFieldA, identifiableObjectManager.get( DataDimensionItem.DATA_DIMENSION_CLASSES, IdScheme.UID, dataFieldA.getUid() ) );
//    }

    @Test
    public void testGetObject()
    {
        DataField dataFieldA = createDataField( 'A' );
        DataField dataFieldB = createDataField( 'B' );

        dataFieldService.addDataField( dataFieldA );
        Long dataFieldIdA = dataFieldA.getId();
        dataFieldService.addDataField( dataFieldB );
        Long dataFieldIdB = dataFieldB.getId();

        assertEquals( dataFieldA, identifiableObjectManager.getObject( dataFieldIdA, DataField.class.getSimpleName() ) );
        assertEquals( dataFieldB, identifiableObjectManager.getObject( dataFieldIdB, DataField.class.getSimpleName() ) );

    }

//    @Test
//    public void testGetWithClasses()
//    {
//        DataField dataFieldA = createDataField( 'A' );
//        DataField dataFieldB = createDataField( 'B' );
//
//        dataFieldService.addDataField( dataFieldA );
//        dataFieldService.addDataField( dataFieldB );
//
//        Set<Class<? extends IdentifiableObject>> classes = ImmutableSet.<Class<? extends IdentifiableObject>>builder().
//            add( Indicator.class ).add( DataField.class ).build();
//
//        assertEquals( dataFieldA, identifiableObjectManager.get( classes, dataFieldA.getUid() ) );
//        assertEquals( dataFieldB, identifiableObjectManager.get( classes, dataFieldB.getUid() ) );
//    }

    @Test
    public void publicAccessSetIfNoUser()
    {
        DataField dataField = createDataField( 'A' );
        identifiableObjectManager.save( dataField );

        assertNotNull( dataField.getPublicAccess() );
        assertFalse( AccessStringHelper.canRead( dataField.getPublicAccess() ) );
        assertFalse( AccessStringHelper.canWrite( dataField.getPublicAccess() ) );
    }

    @Test
    public void getCount()
    {
        identifiableObjectManager.save( createDataField( 'A' ) );
        identifiableObjectManager.save( createDataField( 'B' ) );
        identifiableObjectManager.save( createDataField( 'C' ) );
        identifiableObjectManager.save( createDataField( 'D' ) );

        assertEquals( 4, identifiableObjectManager.getCount( DataField.class ) );
    }

    @Test
    public void getEqualToName()
    {
        DataField dataField = createDataField( 'A' );
        identifiableObjectManager.save( dataField );

        assertNotNull( identifiableObjectManager.getByName( DataField.class, "DataElementA" ) );
        assertNull( identifiableObjectManager.getByName( DataField.class, "DataElementB" ) );
        assertEquals( dataField, identifiableObjectManager.getByName( DataField.class, "DataElementA" ) );
    }

    @Test
    public void getAllOrderedName()
    {
        identifiableObjectManager.save( createDataField( 'D' ) );
        identifiableObjectManager.save( createDataField( 'B' ) );
        identifiableObjectManager.save( createDataField( 'C' ) );
        identifiableObjectManager.save( createDataField( 'A' ) );

        List<DataField> dataFields = new ArrayList<>( identifiableObjectManager.getAllSorted( DataField.class ) );

        assertEquals( 4, dataFields.size() );
        assertEquals( "DataElementA", dataFields.get( 0 ).getName() );
        assertEquals( "DataElementB", dataFields.get( 1 ).getName() );
        assertEquals( "DataElementC", dataFields.get( 2 ).getName() );
        assertEquals( "DataElementD", dataFields.get( 3 ).getName() );
    }

    @Test
    public void userIsCurrentIfNoUserSet()
    {
        User user = createUserAndInjectSecurityContext( true );

        DataField dataField = createDataField( 'A' );
        identifiableObjectManager.save( dataField );

        assertNotNull( dataField.getUser() );
        assertEquals( user, dataField.getUser() );
    }

    @Test
    public void userCanCreatePublic()
    {
//        createUserAndInjectSecurityContext( false, "F_DATAELEMENT_PUBLIC_ADD" );
        createUserAndInjectSecurityContext( false, "F_DATAFIELD_PUBLIC_ADD" );
        DataField dataField = createDataField( 'A' );
        identifiableObjectManager.save( dataField );

        assertNotNull( dataField.getPublicAccess() );
        assertTrue( AccessStringHelper.canRead( dataField.getPublicAccess() ) );
        assertTrue( AccessStringHelper.canWrite( dataField.getPublicAccess() ) );
    }

    @Test
    public void userCanCreatePrivate()
    {
        createUserAndInjectSecurityContext( false, "F_DATAFIELD_PRIVATE_ADD" );

        DataField dataField = createDataField( 'A' );
        identifiableObjectManager.save( dataField );

        assertNotNull( dataField.getPublicAccess() );
        assertFalse( AccessStringHelper.canRead( dataField.getPublicAccess() ) );
        assertFalse( AccessStringHelper.canWrite( dataField.getPublicAccess() ) );
    }

    @Test( expected = CreateAccessDeniedException.class )
    public void userDeniedCreateObject()
    {
        createUserAndInjectSecurityContext( false );
        identifiableObjectManager.save( createDataField( 'A' ) );
    }

    @Test
    public void publicUserModifiedPublicAccessDEFAULT()
    {
        createUserAndInjectSecurityContext( false, "F_DATAFIELD_PUBLIC_ADD" );

        DataField dataField = createDataField( 'A' );
        dataField.setPublicAccess( AccessStringHelper.DEFAULT );

        identifiableObjectManager.save( dataField, false );

        assertFalse( AccessStringHelper.canRead( dataField.getPublicAccess() ) );
        assertFalse( AccessStringHelper.canWrite( dataField.getPublicAccess() ) );
    }

    @Test
    public void publicUserModifiedPublicAccessRW()
    {
        createUserAndInjectSecurityContext( false, "F_DATAFIELD_PUBLIC_ADD" );

        DataField dataField = createDataField( 'A' );
        dataField.setPublicAccess( AccessStringHelper.READ_WRITE );

        identifiableObjectManager.save( dataField, false );
    }

    @Test( expected = CreateAccessDeniedException.class )
    public void privateUserModifiedPublicAccessRW()
    {
        createUserAndInjectSecurityContext( false, "F_DATAFIELD_PRIVATE_ADD" );

        DataField dataField = createDataField( 'A' );
        dataField.setPublicAccess( AccessStringHelper.READ_WRITE );

        identifiableObjectManager.save( dataField, false );
    }

    @Test
    public void privateUserModifiedPublicAccessDEFAULT()
    {
        createUserAndInjectSecurityContext( false, "F_DATAFIELD_PRIVATE_ADD" );

        DataField dataField = createDataField( 'A' );
        dataField.setPublicAccess( AccessStringHelper.DEFAULT );

        identifiableObjectManager.save( dataField, false );
    }

    @Test( expected = UpdateAccessDeniedException.class )
    public void updateForPrivateUserDeniedAfterChangePublicAccessRW()
    {
        createUserAndInjectSecurityContext( false, "F_DATAFIELD_PRIVATE_ADD" );

        DataField dataField = createDataField( 'A' );
        dataField.setPublicAccess( AccessStringHelper.DEFAULT );

        identifiableObjectManager.save( dataField, false );

        dataField.setPublicAccess( AccessStringHelper.READ_WRITE );

        identifiableObjectManager.update( dataField );
    }

    @Test( expected = CreateAccessDeniedException.class )
    public void userDeniedForPublicAdd()
    {
        createUserAndInjectSecurityContext( false );

        DataField dataField = createDataField( 'A' );
        dataField.setPublicAccess( AccessStringHelper.READ_WRITE );

        identifiableObjectManager.save( dataField, false );
    }

    @Test( expected = DeleteAccessDeniedException.class )
    public void userDeniedDeleteObject()
    {
        createUserAndInjectSecurityContext( false, "F_DATAFIELD_PUBLIC_ADD", "F_USER_ADD" );

        User user = createUser( 'B' );
        identifiableObjectManager.save( user );

        DataField dataField = createDataField( 'A' );
        identifiableObjectManager.save( dataField );

        dataField.setUser( user );
        dataField.setPublicAccess( AccessStringHelper.DEFAULT );
        sessionFactory.getCurrentSession().update( dataField );

        identifiableObjectManager.delete( dataField );
    }

    @Test
    public void objectsWithNoUser()
    {
        identifiableObjectManager.save( createDataField( 'A' ) );
        identifiableObjectManager.save( createDataField( 'B' ) );
        identifiableObjectManager.save( createDataField( 'C' ) );
        identifiableObjectManager.save( createDataField( 'D' ) );

        assertEquals( 4, identifiableObjectManager.getCount( DataField.class ) );
        assertEquals( 4, identifiableObjectManager.getAll( DataField.class ).size() );
    }

    @Test
    public void readPrivateObjects()
    {
        createUserAndInjectSecurityContext( false, "F_DATAFIELD_PUBLIC_ADD", "F_USER_ADD" );

        User user = createUser( 'B' );
        identifiableObjectManager.save( user );

        identifiableObjectManager.save( createDataField( 'A' ) );
        identifiableObjectManager.save( createDataField( 'B' ) );
        identifiableObjectManager.save( createDataField( 'C' ) );
        identifiableObjectManager.save( createDataField( 'D' ) );

        assertEquals( 4, identifiableObjectManager.getAll( DataField.class ).size() );

        List<DataField> dataFields = new ArrayList<>( identifiableObjectManager.getAll( DataField.class ) );

        for ( DataField dataField : dataFields )
        {
            dataField.setUser( user );
            dataField.setPublicAccess( AccessStringHelper.DEFAULT );

            sessionFactory.getCurrentSession().update( dataField );
        }

        assertEquals( 0, identifiableObjectManager.getCount( DataField.class ) );
        assertEquals( 0, identifiableObjectManager.getAll( DataField.class ).size() );
    }

    @Test
    public void readUserGroupSharedObjects()
    {
        User loginUser = createUserAndInjectSecurityContext( false, "F_DATAFIELD_PUBLIC_ADD", "F_USER_ADD", "F_USERGROUP_PUBLIC_ADD" );

        User user = createUser( 'B' );
        identifiableObjectManager.save( user );

        UserGroup userGroup = createUserGroup( 'A', Sets.newHashSet( loginUser ) );
        identifiableObjectManager.save( userGroup );

        identifiableObjectManager.save( createDataField( 'A' ) );
        identifiableObjectManager.save( createDataField( 'B' ) );
        identifiableObjectManager.save( createDataField( 'C' ) );
        identifiableObjectManager.save( createDataField( 'D' ) );

        assertEquals( 4, identifiableObjectManager.getCount( DataField.class ) );
        assertEquals( 4, identifiableObjectManager.getAll( DataField.class ).size() );

        List<DataField> dataFields = new ArrayList<>( identifiableObjectManager.getAll( DataField.class ) );

        for ( DataField dataField : dataFields )
        {
            dataField.setUser( user );
            dataField.setPublicAccess( AccessStringHelper.newInstance().build() );

            UserGroupAccess userGroupAccess = new UserGroupAccess();
            userGroupAccess.setAccess( AccessStringHelper.READ );
            userGroupAccess.setUserGroup( userGroup );

            sessionFactory.getCurrentSession().save( userGroupAccess );

            dataField.getUserGroupAccesses().add( userGroupAccess );
            sessionFactory.getCurrentSession().update( dataField );
        }

        assertEquals( 4, identifiableObjectManager.getAll( DataField.class ).size() );
        assertEquals( 4, identifiableObjectManager.getCount( DataField.class ) );
    }

    @Test
    public void getByUidTest()
    {
        DataField dataFieldA = createDataField( 'A' );
        DataField dataFieldB = createDataField( 'B' );
        DataField dataFieldC = createDataField( 'C' );
        DataField dataFieldD = createDataField( 'D' );

        identifiableObjectManager.save( dataFieldA );
        identifiableObjectManager.save( dataFieldB );
        identifiableObjectManager.save( dataFieldC );
        identifiableObjectManager.save( dataFieldD );

        List<DataField> ab = identifiableObjectManager.getByUid( DataField.class, Arrays.asList( dataFieldA.getUid(), dataFieldB.getUid() ) );
        List<DataField> cd = identifiableObjectManager.getByUid( DataField.class, Arrays.asList( dataFieldC.getUid(), dataFieldD.getUid() ) );

        assertTrue( ab.contains( dataFieldA ) );
        assertTrue( ab.contains( dataFieldB ) );
        assertFalse( ab.contains( dataFieldC ) );
        assertFalse( ab.contains( dataFieldD ) );

        assertFalse( cd.contains( dataFieldA ) );
        assertFalse( cd.contains( dataFieldB ) );
        assertTrue( cd.contains( dataFieldC ) );
        assertTrue( cd.contains( dataFieldD ) );
    }

    @Test
    public void getByUidOrderedTest()
    {
        DataField dataFieldA = createDataField( 'A' );
        DataField dataFieldB = createDataField( 'B' );
        DataField dataFieldC = createDataField( 'C' );
        DataField dataFieldD = createDataField( 'D' );

        identifiableObjectManager.save( dataFieldA );
        identifiableObjectManager.save( dataFieldB );
        identifiableObjectManager.save( dataFieldC );
        identifiableObjectManager.save( dataFieldD );

        List<String> uids = Arrays.asList( dataFieldA.getUid(), dataFieldC.getUid(), dataFieldB.getUid(), dataFieldD.getUid() );

        List<DataField> expected = new ArrayList<>( Arrays.asList( dataFieldA, dataFieldC, dataFieldB, dataFieldD ) );

        List<DataField> actual = new ArrayList<>( identifiableObjectManager.getByUidOrdered( DataField.class, uids ) );

        assertEquals( expected, actual );
    }

    @Test
    public void getByCodeTest()
    {
        DataField dataFieldA = createDataField( 'A' );
        DataField dataFieldB = createDataField( 'B' );
        DataField dataFieldC = createDataField( 'C' );
        DataField dataFieldD = createDataField( 'D' );

        dataFieldA.setCode( "DE_A" );
        dataFieldB.setCode( "DE_B" );
        dataFieldC.setCode( "DE_C" );
        dataFieldD.setCode( "DE_D" );

        identifiableObjectManager.save( dataFieldA );
        identifiableObjectManager.save( dataFieldB );
        identifiableObjectManager.save( dataFieldC );
        identifiableObjectManager.save( dataFieldD );

        List<DataField> ab = identifiableObjectManager.getByCode( DataField.class, Arrays.asList( dataFieldA.getCode(), dataFieldB.getCode() ) );
        List<DataField> cd = identifiableObjectManager.getByCode( DataField.class, Arrays.asList( dataFieldC.getCode(), dataFieldD.getCode() ) );

        assertTrue( ab.contains( dataFieldA ) );
        assertTrue( ab.contains( dataFieldB ) );
        assertFalse( ab.contains( dataFieldC ) );
        assertFalse( ab.contains( dataFieldD ) );

        assertFalse( cd.contains( dataFieldA ) );
        assertFalse( cd.contains( dataFieldB ) );
        assertTrue( cd.contains( dataFieldC ) );
        assertTrue( cd.contains( dataFieldD ) );
    }

//    @Test
//    public void testGetObjects()
//    {
//        OrganisationUnit unit1 = createOrganisationUnit( 'A' );
//        OrganisationUnit unit2 = createOrganisationUnit( 'B' );
//        OrganisationUnit unit3 = createOrganisationUnit( 'C' );
//
//        identifiableObjectManager.save( unit1 );
//        identifiableObjectManager.save( unit2 );
//        identifiableObjectManager.save( unit3 );
//
//        Set<String> codes = Sets.newHashSet( unit2.getCode(), unit3.getCode() );
//
//        List<OrganisationUnit> units = identifiableObjectManager.getObjects( OrganisationUnit.class, IdentifiableProperty.CODE, codes );
//
//        assertEquals( 2, units.size() );
//        assertTrue( units.contains( unit2 ) );
//        assertTrue( units.contains( unit3 ) );
//    }

    @Test
    public void testGetIdMapIdScheme()
    {
        DataField dataFieldA = createDataField( 'A' );
        DataField dataFieldB = createDataField( 'B' );

        dataFieldService.addDataField( dataFieldA );
        dataFieldService.addDataField( dataFieldB );

        Map<String, DataField> map = identifiableObjectManager.getIdMap( DataField.class, IdScheme.CODE );

        assertEquals( dataFieldA, map.get( "DataElementCodeA" ) );
        assertEquals( dataFieldB, map.get( "DataElementCodeB" ) );
        assertNull( map.get( "DataElementCodeX" ) );
    }
}
