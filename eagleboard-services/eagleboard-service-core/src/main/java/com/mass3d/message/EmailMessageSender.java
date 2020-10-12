package com.mass3d.message;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import com.mass3d.common.DeliveryChannel;
import com.mass3d.commons.util.DebugUtils;
import com.mass3d.commons.util.TextUtils;
import com.mass3d.email.EmailConfiguration;
import com.mass3d.email.EmailResponse;
import com.mass3d.outboundmessage.OutboundMessageBatch;
import com.mass3d.outboundmessage.OutboundMessageBatchStatus;
import com.mass3d.outboundmessage.OutboundMessageResponse;
import com.mass3d.outboundmessage.OutboundMessageResponseSummary;
import com.mass3d.setting.SettingKey;
import com.mass3d.setting.SystemSettingManager;
import com.mass3d.system.util.ValidationUtils;
import com.mass3d.user.User;
import com.mass3d.user.UserSettingKey;
import com.mass3d.user.UserSettingService;
import com.mass3d.util.ObjectUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

@Component( "emailMessageSender" )
@Scope( proxyMode = ScopedProxyMode.TARGET_CLASS )
public class EmailMessageSender
    implements MessageSender
{
    private static final Log log = LogFactory.getLog( EmailMessageSender.class );

    private static final String DEFAULT_APPLICATION_TITLE = "DHIS 2";
    private static final String LB = System.getProperty( "line.separator" );
    private static final String MESSAGE_EMAIL_TEMPLATE = "message_email";
    private static final String HOST = "Host: ";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }

    // -------------------------------------------------------------------------
    // MessageSender implementation
    // -------------------------------------------------------------------------
    
    @Override
    public OutboundMessageResponse sendMessage( String subject, String text, String footer, User sender, Set<User> users, boolean forceSend )
    {
        EmailConfiguration emailConfig = getEmailConfiguration();
        OutboundMessageResponse status = new OutboundMessageResponse();

        String errorMessage = "No recipient found";

        if ( emailConfig.getHostName() == null )
        {
            status.setOk( false );
            status.setResponseObject( EmailResponse.NOT_CONFIGURED );
            return status;
        }

        String serverBaseUrl = systemSettingManager.getInstanceBaseUrl();
        String plainContent = renderPlainContent( text, sender );
        String htmlContent = renderHtmlContent( text, footer, serverBaseUrl != null ? HOST + serverBaseUrl : "", sender );

        try
        {
            HtmlEmail email = getHtmlEmail( emailConfig.getHostName(), emailConfig.getPort(), emailConfig.getUsername(),
                emailConfig.getPassword(), emailConfig.isTls(), emailConfig.getFrom() );
            email.setSubject( getPrefixedSubject( subject ) );
            email.setTextMsg( plainContent );
            email.setHtmlMsg( htmlContent );

            boolean hasRecipients = false;

            for ( User user : users )
            {
                boolean doSend = forceSend
                    || (Boolean) userSettingService.getUserSetting( UserSettingKey.MESSAGE_EMAIL_NOTIFICATION, user );

                if ( doSend && ValidationUtils.emailIsValid( user.getEmail() ) )
                {
                    if ( isEmailValid( user.getEmail() ) )
                    {
                        email.addBcc( user.getEmail() );
                        hasRecipients = true;

                        log.info( "Sending email to user: " + user.getUsername() + " with email address: " + user.getEmail() );
                    }
                    else
                    {
                        log.warn( user.getEmail() + " is not a valid email for user: " + user.getUsername() );
                        errorMessage = "No valid email address found";
                    }
                }
            }

            if ( hasRecipients )
            {
                email.send();

                log.info( "Email sent using host: " + emailConfig.getHostName() + ":" + emailConfig.getPort() + " with TLS: " + emailConfig.isTls() );
                status = new OutboundMessageResponse( "Email sent", EmailResponse.SENT, true );
            }
            else
            {
                status = new OutboundMessageResponse( errorMessage, EmailResponse.ABORTED, false );
            }
        }
        catch ( Exception ex )
        {
            log.error( "Error while sending email: " + ex.getMessage() + ", " + DebugUtils.getStackTrace( ex ) );
            status = new OutboundMessageResponse( "Email not sent: " + ex.getMessage(), EmailResponse.FAILED, false );
        }

        return status;
    }

    @Async
    @Override
    public Future<OutboundMessageResponse> sendMessageAsync( String subject, String text, String footer, User sender, Set<User> users, boolean forceSend )
    {
        OutboundMessageResponse response = sendMessage( subject, text, footer, sender, users, forceSend );
        return new AsyncResult<OutboundMessageResponse>( response );
    }
    
    @Override
    public OutboundMessageResponse sendMessage( String subject, String text, Set<String> recipients )
    {
        EmailConfiguration emailConfig = getEmailConfiguration();
        OutboundMessageResponse status = new OutboundMessageResponse();

        String errorMessage = "No recipient found";

        if ( emailConfig.getHostName() == null )
        {
            status.setOk( false );
            status.setResponseObject( EmailResponse.NOT_CONFIGURED );
            return status;
        }

        try
        {
            HtmlEmail email = getHtmlEmail( emailConfig.getHostName(), emailConfig.getPort(), emailConfig.getUsername(),
                emailConfig.getPassword(), emailConfig.isTls(), emailConfig.getFrom() );
            email.setSubject( getPrefixedSubject( subject ) );
            email.setTextMsg( text );

            boolean hasRecipients = false;

            for ( String recipient : recipients )
            {
                if ( isEmailValid( recipient ) )
                {
                    email.addBcc( recipient );
                    hasRecipients = true;

                    log.info( "Sending email to : " + recipient );
                }
                else
                {
                    log.warn( recipient + " is not a valid email" );
                    errorMessage = "No valid email address found";
                }
            }

            if ( hasRecipients )
            {
                email.send();

                log.info( "Email sent using host: " + emailConfig.getHostName() + ":" + emailConfig.getPort() + " with TLS: " + emailConfig.isTls() );
                return new OutboundMessageResponse( "Email sent", EmailResponse.SENT, true );
            }
            else
            {
                status = new OutboundMessageResponse( errorMessage, EmailResponse.ABORTED, false );
            }
        }
        catch ( Exception ex )
        {
            log.error( "Error while sending email: " + ex.getMessage() + ", " + DebugUtils.getStackTrace( ex ) );
            status = new OutboundMessageResponse( "Email not sent: " + ex.getMessage(), EmailResponse.FAILED, false );
        }

        return status;
    }

    @Override
    public OutboundMessageResponse sendMessage( String subject, String text, String recipient )
    {
        return sendMessage( subject, text, Sets.newHashSet( recipient ) );
    }

    @Override
    public OutboundMessageResponseSummary sendMessageBatch( OutboundMessageBatch batch )
    {
        List<OutboundMessageResponse> statuses = batch.getMessages().stream()
            .map( m -> sendMessage( m.getSubject(), m.getText(), m.getRecipients() ) )
            .collect( Collectors.toList() );

        return generateSummary( statuses );
    }

    @Override
    public boolean isConfigured()
    {
        return getEmailConfiguration().isOk();
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private HtmlEmail getHtmlEmail( String hostName, int port, String username, String password, boolean tls,
        String sender ) throws EmailException
    {
        HtmlEmail email = new HtmlEmail();
        email.setHostName( hostName );
        email.setFrom( sender, getEmailName() );
        email.setSmtpPort( port );
        email.setStartTLSEnabled( tls );
        
        if ( username != null && password != null )
        {
            email.setAuthenticator( new DefaultAuthenticator( username, password ) );
        }

        return email;
    }

    private String renderPlainContent( String text, User sender )
    {
        return sender == null ? text
            : (text + LB + LB + sender.getName() + LB

//                + (sender.getOrganisationUnitsName() != null ? (sender.getOrganisationUnitsName() + LB)
//                    : StringUtils.EMPTY)
                + StringUtils.EMPTY

                + (sender.getEmail() != null ? (sender.getEmail() + LB) : StringUtils.EMPTY)
                + (sender.getPhoneNumber() != null ? (sender.getPhoneNumber() + LB) : StringUtils.EMPTY));
    }

    private String renderHtmlContent( String text, String footer, String serverBaseUrl, User sender )
    {
        Map<String, Object> content = new HashMap<>();

        if ( !Strings.isNullOrEmpty( text ) )
        {
            content.put( "text", text.replaceAll( "\\r?\\n", "<br>" ) );
        }

        if ( !Strings.isNullOrEmpty( footer ) )
        {
            content.put( "footer", footer );
        }

        if ( !Strings.isNullOrEmpty( serverBaseUrl ) )
        {
            content.put("serverBaseUrl", serverBaseUrl );
        }

        if ( sender != null )
        {
            content.put( "senderName", sender.getName() );

//            if ( sender.getOrganisationUnitsName() != null )
//            {
//                content.put( "organisationUnitsName", sender.getOrganisationUnitsName() );
//            }

            if ( sender.getEmail() != null )
            {
                content.put( "email", sender.getEmail() );
            }

            if ( sender.getPhoneNumber() != null )
            {
                content.put( "phoneNumber", sender.getPhoneNumber() );
            }
        }

        return null; //new VelocityManager().render( content, MESSAGE_EMAIL_TEMPLATE );
    }

    private String getPrefixedSubject( String subject )
    {
        String title = (String) systemSettingManager.getSystemSetting( SettingKey.APPLICATION_TITLE, DEFAULT_APPLICATION_TITLE );
        return "[" + title + "] " + subject;
    }
    
    private String getEmailName()
    {
        String appTitle = (String) systemSettingManager.getSystemSetting( SettingKey.APPLICATION_TITLE );
        appTitle = ObjectUtils.firstNonNull( StringUtils.trimToNull( emailNameEncode( appTitle ) ), DEFAULT_APPLICATION_TITLE );
        return appTitle + " message [No reply]";
    }
    
    private String emailNameEncode( String name )
    {
        return name != null ? TextUtils.removeNewlines( name ) : null;
    }

    private boolean isEmailValid( String email )
    {
        return ValidationUtils.emailIsValid( email );
    }

    private EmailConfiguration getEmailConfiguration()
    {
        String hostName = (String) systemSettingManager.getSystemSetting( SettingKey.EMAIL_HOST_NAME );
        String username = (String) systemSettingManager.getSystemSetting( SettingKey.EMAIL_USERNAME );
        String password = (String) systemSettingManager.getSystemSetting( SettingKey.EMAIL_PASSWORD );
        String from = (String) systemSettingManager.getSystemSetting( SettingKey.EMAIL_SENDER );
        int port = (int) systemSettingManager.getSystemSetting( SettingKey.EMAIL_PORT );
        boolean tls = (boolean) systemSettingManager.getSystemSetting( SettingKey.EMAIL_TLS );

        return new EmailConfiguration( hostName, username, password, from, port, tls );
    }

    private OutboundMessageResponseSummary generateSummary( List<OutboundMessageResponse> statuses )
    {
        OutboundMessageResponseSummary summary = new OutboundMessageResponseSummary();

        int total, sent = 0;

        boolean ok = true;

        String errorMessage = StringUtils.EMPTY;

        total = statuses.size();

        for ( OutboundMessageResponse status : statuses )
        {
            if ( EmailResponse.SENT.equals( status.getResponseObject() ) )
            {
                sent++;
            }
            else
            {
                ok = false;

                errorMessage = status.getDescription();
            }
        }

        summary.setTotal( total );
        summary.setChannel( DeliveryChannel.EMAIL );
        summary.setSent( sent );
        summary.setFailed( total - sent );

        if ( !ok )
        {
            summary.setBatchStatus( OutboundMessageBatchStatus.FAILED );
            summary.setErrorMessage( errorMessage );

            log.error( errorMessage );
        }
        else
        {
            summary.setBatchStatus( OutboundMessageBatchStatus.COMPLETED );
            summary.setResponseMessage( "SENT" );

            log.info( "EMAIL batch processed successfully" );
        }

        return summary;
    }
}
