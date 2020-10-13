package com.mass3d.message;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mass3d.commons.util.TextUtils.LN;

import com.google.api.client.util.Sets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.commons.util.DebugUtils;
import com.mass3d.configuration.ConfigurationService;
import com.mass3d.fileresource.FileResource;
import com.mass3d.i18n.I18nManager;
import com.mass3d.i18n.locale.LocaleManager;
import com.mass3d.setting.SettingKey;
import com.mass3d.setting.SystemSettingManager;
import com.mass3d.user.CurrentUserService;
import com.mass3d.user.User;
import com.mass3d.user.UserGroup;
import com.mass3d.user.UserSettingKey;
import com.mass3d.user.UserSettingService;
import com.mass3d.util.ObjectUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service( "com.mass3d.message.MessageService")
@Transactional
public class DefaultMessageService
    implements MessageService
{
    private static final Log log = LogFactory.getLog( DefaultMessageService.class );

    private static final String COMPLETE_SUBJECT = "Form registered as complete";
    private static final String COMPLETE_TEMPLATE = "completeness_message";
    private static final String MESSAGE_EMAIL_FOOTER_TEMPLATE = "message_email_footer";
    private static final String MESSAGE_PATH = "/dhis-web-messaging/readMessage.action";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MessageConversationStore messageConversationStore;

    private CurrentUserService currentUserService;

    private ConfigurationService configurationService;

    private UserSettingService userSettingService;

    private I18nManager i18nManager;

    private SystemSettingManager systemSettingManager;

    private List<MessageSender> messageSenders;

    // -------------------------------------------------------------------------
    // MessageService implementation
    // -------------------------------------------------------------------------

    public DefaultMessageService( MessageConversationStore messageConversationStore,
        CurrentUserService currentUserService, ConfigurationService configurationService,
        UserSettingService userSettingService, I18nManager i18nManager, SystemSettingManager systemSettingManager,
        List<MessageSender> messageSenders)
    {
        checkNotNull( messageConversationStore );
        checkNotNull( currentUserService );
        checkNotNull( configurationService );
        checkNotNull( userSettingService );
        checkNotNull( i18nManager );
        checkNotNull( systemSettingManager );
//        checkNotNull( configurationProvider );
        checkNotNull( messageSenders );

        this.messageConversationStore = messageConversationStore;
        this.currentUserService = currentUserService;
        this.configurationService = configurationService;
        this.userSettingService = userSettingService;
        this.i18nManager = i18nManager;
        this.systemSettingManager = systemSettingManager;
        this.messageSenders = messageSenders;
//        this.configurationProvider = configurationProvider;
    }


    @Override
    public long sendTicketMessage( String subject, String text, String metaData )
    {
        User currentUser = currentUserService.getCurrentUser();

        MessageConversationParams params = new MessageConversationParams.Builder()
            .withRecipients( getFeedbackRecipients() )
            .withSender( currentUser )
            .withSubject( subject )
            .withText( text )
            .withMessageType( MessageType.TICKET )
            .withMetaData( metaData )
            .withStatus( MessageConversationStatus.OPEN ).build();
        
        return sendMessage( params );        
    }

    @Override
    public long sendPrivateMessage( Set<User> recipients, String subject, String text, String metaData, Set<FileResource> attachments )
    {
        User currentUser = currentUserService.getCurrentUser();

        MessageConversationParams params = new MessageConversationParams.Builder()
            .withRecipients( recipients )
            .withSender( currentUser )
            .withSubject( subject )
            .withText( text )
            .withMessageType( MessageType.PRIVATE )
            .withMetaData( metaData )
            .withAttachments( attachments ).build();
        
        return sendMessage( params );
    }
    
    @Override
    public long sendSystemMessage( Set<User> recipients, String subject, String text )
    {
        MessageConversationParams params = new MessageConversationParams.Builder()
            .withRecipients( recipients )
            .withSubject( subject )
            .withText( text )
            .withMessageType( MessageType.SYSTEM ).build();
        
        return sendMessage( params );
    }

    @Override
    public long sendValidationMessage( Set<User> recipients, String subject, String text, MessageConversationPriority priority )
    {
        MessageConversationParams params = new MessageConversationParams.Builder()
            .withRecipients( recipients )
            .withSubject( subject )
            .withText( text )
            .withMessageType( MessageType.VALIDATION_RESULT )
            .withStatus( MessageConversationStatus.OPEN )
            .withPriority( priority ).build();

        return sendMessage( params );
    }
    
    @Override
    public long sendMessage( MessageConversationParams params )
    {
        MessageConversation conversation = params.createMessageConversation();
        Long id = saveMessageConversation( conversation );

        Message message = new Message( params.getText(), params.getMetadata(), params.getSender() );

        message.setAttachments( params.getAttachments() );
        conversation.addMessage( message );

        params.getRecipients().stream().filter( r -> !r.equals( params.getSender() ) )
            .forEach( ( recipient ) -> conversation.addUserMessage( new UserMessage( recipient, false ) ) );

        if ( params.getSender() != null )
        {
            conversation.addUserMessage( new UserMessage( params.getSender(), true ) );
        }

        String footer = "";//getMessageFooter( conversation );

        invokeMessageSenders( params.getSubject(), params.getText(), footer, params.getSender(),
            params.getRecipients(), params.isForceNotification() );

        return id;
    }

    @Override
    public long sendSystemErrorNotification( String subject, Throwable t )
    {
        String title = (String) systemSettingManager.getSystemSetting( SettingKey.APPLICATION_TITLE );
        String baseUrl = (String) systemSettingManager.getSystemSetting( SettingKey.INSTANCE_BASE_URL );

        String text = new StringBuilder()
            .append( subject + LN + LN )
            .append( "System title: " + title + LN )
            .append( "Base URL: " + baseUrl + LN )
            .append( "Time: " + new DateTime().toString() + LN )
            .append( "Message: " + t.getMessage() + LN + LN )
            .append( "Cause: " + DebugUtils.getStackTrace( t.getCause() ) ).toString();

        MessageConversationParams params = new MessageConversationParams.Builder()
            .withRecipients( getFeedbackRecipients() )
            .withSubject( subject )
            .withText( text )
            .withMessageType( MessageType.SYSTEM ).build();
        
        return sendMessage( params );
    }

    @Override
    public void sendReply( MessageConversation conversation, String text, String metaData, boolean internal, Set<FileResource> attachments )
    {
        User sender = currentUserService.getCurrentUser();

        Message message = new Message( text, metaData, sender, internal );

        message.setAttachments( attachments );

        conversation.markReplied( sender, message );

        updateMessageConversation( conversation );

        Set<User> users = conversation.getUsers();

        if ( conversation.getMessageType().equals( MessageType.TICKET ) && internal )
        {
            users = users.stream().filter( this::hasAccessToManageFeedbackMessages )
                .collect( Collectors.toSet() );
        }

        invokeMessageSenders( conversation.getSubject(), text, null, sender, new HashSet<>( users ), false );
    }

//    @Override
//    public int sendCompletenessMessage( CompleteDataSetRegistration registration )
//    {
//        DataSet dataSet = registration.getDataSet();
//
//        if ( dataSet == null )
//        {
//            return 0;
//        }
//
//        UserGroup userGroup = dataSet.getNotificationRecipients();
//
//        User sender = currentUserService.getCurrentUser();
//
//        // data set completed through sms
//        if ( sender == null )
//        {
//            return 0;
//        }
//
//        Set<User> recipients = new HashSet<>();
//
//        if ( userGroup != null )
//        {
//            recipients.addAll( new HashSet<>( userGroup.getMembers() ) );
//        }
//
//        if ( dataSet.isNotifyCompletingUser() )
//        {
//            recipients.add( sender );
//        }
//
//        if ( recipients.isEmpty() )
//        {
//            return 0;
//        }
//
//        String text = new VelocityManager().render( registration, COMPLETE_TEMPLATE );
//
//        MessageConversation conversation = new MessageConversation( COMPLETE_SUBJECT, sender, MessageType.SYSTEM );
//
//        conversation.addMessage( new Message( text, null, sender ) );
//
//        for ( User user : recipients )
//        {
//            conversation.addUserMessage( new UserMessage( user ) );
//        }
//
//        if ( !conversation.getUserMessages().isEmpty() )
//        {
//            int id = saveMessageConversation( conversation );
//
//            invokeMessageSenders( COMPLETE_SUBJECT, text, null, sender,
//                new HashSet<>( conversation.getUsers() ), false );
//
//            return id;
//        }
//
//        return 0;
//    }

    @Override
    public long saveMessageConversation( MessageConversation conversation )
    {
        messageConversationStore.save( conversation );
        return conversation.getId();
    }

    @Override
    public void updateMessageConversation( MessageConversation conversation )
    {
        messageConversationStore.update( conversation );
    }

    @Override
    public MessageConversation getMessageConversation( long id )
    {
        return messageConversationStore.get( id );
    }

    @Override
    public MessageConversation getMessageConversation( String uid )
    {
        MessageConversation mc = messageConversationStore.getByUid( uid );

        if ( mc == null )
        {
            return null;
        }

        User user = currentUserService.getCurrentUser();

        mc.setFollowUp( mc.isFollowUp( user ) );
        mc.setRead( mc.isRead( user ) );

        return messageConversationStore.getByUid( uid );
    }

    @Override
    public long getUnreadMessageConversationCount()
    {
        return messageConversationStore.getUnreadUserMessageConversationCount( currentUserService.getCurrentUser() );
    }

    @Override
    public long getUnreadMessageConversationCount( User user )
    {
        return messageConversationStore.getUnreadUserMessageConversationCount( user );
    }

    @Override
    public List<MessageConversation> getMessageConversations()
    {
        return messageConversationStore
            .getMessageConversations( currentUserService.getCurrentUser(), null, false, false,
                null, null );
    }

    @Override
    public List<MessageConversation> getMessageConversations( int first, int max )
    {
        return messageConversationStore
            .getMessageConversations( currentUserService.getCurrentUser(), null, false, false,
                first, max );
    }

    @Override
    public List<MessageConversation> getMessageConversations( User user, Collection<String> uid )
    {
        List<MessageConversation> conversations = messageConversationStore
            .getMessageConversations( uid );

        // Set transient properties

        for ( MessageConversation mc : conversations )
        {
            mc.setFollowUp( mc.isFollowUp( user ) );
            mc.setRead( mc.isRead( user ) );
        }

        return conversations;
    }

    @Override
    public void deleteMessages( User user )
    {
        messageConversationStore.deleteMessages( user );
        messageConversationStore.deleteUserMessages( user );
        messageConversationStore.removeUserFromMessageConversations( user );
    }

    @Override
    public List<UserMessage> getLastRecipients( int first, int max )
    {
        return messageConversationStore.getLastRecipients( currentUserService.getCurrentUser(), first, max );
    }

    @Override
    public boolean hasAccessToManageFeedbackMessages( User user )
    {
        user = (user == null ? currentUserService.getCurrentUser() : user);
        
        return configurationService.isUserInFeedbackRecipientUserGroup( user ) || user.isAuthorized( "ALL" );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Set<User> getFeedbackRecipients()
    {
        UserGroup feedbackRecipients = configurationService.getConfiguration().getFeedbackRecipients();

        if ( feedbackRecipients != null )
        {
            return feedbackRecipients.getMembers();
        }

        return Sets.newHashSet();
    }

    private void invokeMessageSenders( String subject, String text, String footer, User sender, Set<User> users,
        boolean forceSend )
    {
        for ( MessageSender messageSender : messageSenders )
        {
            log.debug( "Invoking message sender: " + messageSender.getClass().getSimpleName() );

            messageSender.sendMessageAsync( subject, text, footer, sender, new HashSet<>( users ), forceSend );
        }
    }

//    private String getMessageFooter( MessageConversation conversation )
//    {
//        HashMap<String, Object> values = new HashMap<>( 2 );
//
//        String baseUrl = systemSettingManager.getInstanceBaseUrl();
//
//        if ( baseUrl == null )
//        {
//            return StringUtils.EMPTY;
//        }
//
//        Locale locale = (Locale) userSettingService.getUserSetting( UserSettingKey.UI_LOCALE, conversation.getUser() );
//
//        locale = ObjectUtils.firstNonNull( locale, LocaleManager.DEFAULT_LOCALE );
//
//        values.put( "responseUrl", baseUrl + MESSAGE_PATH + "?id=" + conversation.getUid() );
//        values.put( "i18n", i18nManager.getI18n( locale ) );
//
//        return new VelocityManager().render( values, MESSAGE_EMAIL_FOOTER_TEMPLATE );
//    }
}
