package com.mass3d.message;

import com.mass3d.system.deletion.DeletionHandler;
import com.mass3d.user.User;
import org.springframework.stereotype.Component;

@Component( "com.mass3d.message.MessageConversationDeletionHandler" )
public class MessageConversationDeletionHandler
    extends DeletionHandler
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MessageService messageService;

    public void setMessageService( MessageService messageService )
    {
        this.messageService = messageService;
    }

    // -------------------------------------------------------------------------
    // DeletionHandler implementation
    // -------------------------------------------------------------------------

    @Override
    public String getClassName()
    {
        return MessageConversation.class.getSimpleName();
    }

    @Override
    public void deleteUser( User user )
    {
        messageService.deleteMessages( user );
    }
}
