package com.mass3d.dxf2.metadata.objectbundle.hooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.mass3d.common.IdentifiableObject;
import com.mass3d.dxf2.metadata.objectbundle.ObjectBundle;
import com.mass3d.feedback.ErrorCode;
import com.mass3d.feedback.ErrorReport;
import com.mass3d.fileresource.FileResource;
import com.mass3d.fileresource.FileResourceService;
import com.mass3d.schema.MergeParams;
import com.mass3d.system.util.ValidationUtils;
import com.mass3d.user.User;
import com.mass3d.user.UserCredentials;
import com.mass3d.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserObjectBundleHook extends AbstractObjectBundleHook
{
    @Autowired
    private UserService userService;

    @Autowired
    private FileResourceService fileResourceService;

    @Override
    public <T extends IdentifiableObject> List<ErrorReport> validate( T object, ObjectBundle bundle )
    {
        if ( !(object instanceof User) )
        {
            return new ArrayList<>();
        }

        ArrayList<ErrorReport> errorReports = new ArrayList<>(  );
        User user = (User) object;

        if ( user.getWhatsApp() != null && !ValidationUtils.validateWhatsapp( user.getWhatsApp() ) )
        {
            errorReports.add( new ErrorReport( User.class, ErrorCode.E4027, user.getWhatsApp(), "Whatsapp" ) );
        }

        return errorReports;
    }


    @Override
    public void preCreate( IdentifiableObject object, ObjectBundle bundle )
    {
        if ( !User.class.isInstance( object ) || ((User) object).getUserCredentials() == null ) return;

        User user = (User) object;
        bundle.putExtras( user, "uc", user.getUserCredentials() );
        user.setUserCredentials( null );
    }

    @Override
    public void postCreate( IdentifiableObject persistedObject, ObjectBundle bundle )
    {
        if ( !User.class.isInstance( persistedObject ) || !bundle.hasExtras( persistedObject, "uc" ) ) return;

        User user = (User) persistedObject;
        final UserCredentials userCredentials = (UserCredentials) bundle.getExtras( persistedObject, "uc" );

        if ( !StringUtils.isEmpty( userCredentials.getPassword() ) )
        {
            userService.encodeAndSetPassword( userCredentials, userCredentials.getPassword() );
        }

        if ( user.getAvatar() != null )
        {
            FileResource fileResource = fileResourceService.getFileResource( user.getAvatar().getUid() );
            fileResource.setAssigned( true );
            fileResourceService.updateFileResource( fileResource );
        }

        preheatService.connectReferences( userCredentials, bundle.getPreheat(), bundle.getPreheatIdentifier() );
        sessionFactory.getCurrentSession().save( userCredentials );
        user.setUserCredentials( userCredentials );
        sessionFactory.getCurrentSession().update( user );
        bundle.removeExtras( persistedObject, "uc" );
    }

    @Override
    public void preUpdate( IdentifiableObject object, IdentifiableObject persistedObject, ObjectBundle bundle )
    {
        if ( !User.class.isInstance( object ) || ((User) object).getUserCredentials() == null ) return;
        User user = (User) object;
        bundle.putExtras( user, "uc", user.getUserCredentials() );

        User persisted = (User) persistedObject;

        if ( persisted.getAvatar() != null && (user.getAvatar() == null || !persisted.getAvatar().getUid().equals( user.getAvatar().getUid() ) ) )
        {
            FileResource fileResource = fileResourceService.getFileResource( persisted.getAvatar().getUid() );
            fileResource.setAssigned( false );
            fileResourceService.updateFileResource( fileResource );

            if ( user.getAvatar() != null )
            {
                fileResource = fileResourceService.getFileResource( user.getAvatar().getUid() );
                fileResource.setAssigned( true );
                fileResourceService.updateFileResource( fileResource );
            }
        }
    }

    @Override
    public void postUpdate( IdentifiableObject persistedObject, ObjectBundle bundle )
    {
        if ( !User.class.isInstance( persistedObject ) || !bundle.hasExtras( persistedObject, "uc" ) ) return;

        User user = (User) persistedObject;
        final UserCredentials userCredentials = (UserCredentials) bundle.getExtras( persistedObject, "uc" );
        final UserCredentials persistedUserCredentials = bundle.getPreheat().get( bundle.getPreheatIdentifier(), UserCredentials.class, user );

        if ( !StringUtils.isEmpty( userCredentials.getPassword() ) )
        {
            userService.encodeAndSetPassword( persistedUserCredentials, userCredentials.getPassword() );
        }

        mergeService.merge( new MergeParams<>( userCredentials, persistedUserCredentials ).setMergeMode( bundle.getMergeMode() ) );
        preheatService.connectReferences( persistedUserCredentials, bundle.getPreheat(), bundle.getPreheatIdentifier() );

        persistedUserCredentials.setUserInfo( user );
        user.setUserCredentials( persistedUserCredentials );

        sessionFactory.getCurrentSession().update( user.getUserCredentials() );
        bundle.removeExtras( persistedObject, "uc" );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void postCommit( ObjectBundle bundle )
    {
        if ( !bundle.getObjectMap().containsKey( User.class ) ) return;

        List<IdentifiableObject> objects = bundle.getObjectMap().get( User.class );
        Map<String, Map<String, Object>> userReferences = bundle.getObjectReferences( User.class );
        Map<String, Map<String, Object>> userCredentialsReferences = bundle.getObjectReferences( UserCredentials.class );

        if ( userReferences == null || userReferences.isEmpty() || userCredentialsReferences == null || userCredentialsReferences.isEmpty() )
        {
            return;
        }

        for ( IdentifiableObject identifiableObject : objects )
        {
            identifiableObject = bundle.getPreheat().get( bundle.getPreheatIdentifier(), identifiableObject );
            Map<String, Object> userReferenceMap = userReferences.get( identifiableObject.getUid() );

            if ( userReferenceMap == null || userReferenceMap.isEmpty() )
            {
                continue;
            }

            User user = (User) identifiableObject;
            UserCredentials userCredentials = user.getUserCredentials();

            if ( userCredentials == null )
            {
                continue;
            }

            Map<String, Object> userCredentialsReferenceMap = userCredentialsReferences.get( userCredentials.getUid() );

            if ( userCredentialsReferenceMap == null || userCredentialsReferenceMap.isEmpty() )
            {
                continue;
            }

            // Todo Eagle commenting out user orgunits
//            user.setOrganisationUnits( (Set<OrganisationUnit>) userReferenceMap.get( "organisationUnits" ) );
//            user.setDataViewOrganisationUnits( (Set<OrganisationUnit>) userReferenceMap.get( "dataViewOrganisationUnits" ) );
            userCredentials.setUser( (User) userCredentialsReferenceMap.get( "user" ) );
            userCredentials.setUserInfo( user );

            preheatService.connectReferences( user, bundle.getPreheat(), bundle.getPreheatIdentifier() );
            preheatService.connectReferences( userCredentials, bundle.getPreheat(), bundle.getPreheatIdentifier() );

            user.setUserCredentials( userCredentials );
            sessionFactory.getCurrentSession().update( user );
        }
    }
}
