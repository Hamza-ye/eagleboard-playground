package com.mass3d.amqp;

import com.google.common.base.CaseFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.render.RenderService;
import com.mass3d.schema.audit.MetadataAudit;
import com.mass3d.system.RabbitMQ;
import com.mass3d.system.SystemService;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitMQAmqpService implements AmqpService
{
    private static final Log log = LogFactory.getLog( RabbitMQAmqpService.class );

    private final SystemService systemService;

    private final RenderService renderService;

    private AmqpTemplate amqpTemplate;

    private Boolean enabled;

    public RabbitMQAmqpService( SystemService systemService, RenderService renderService )
    {
        this.systemService = systemService;
        this.renderService = renderService;
    }

    @Override
    public boolean isEnabled()
    {
        if ( enabled == null )
        {
            enabled = getAmqpTemplate() != null;
        }

        return enabled;
    }

    @Override
    public void publish( String routingKey, Message message )
    {
        if ( !isEnabled() )
        {
            return;
        }

        RabbitMQ rabbitMQ = systemService.getSystemInfo().getRabbitMQ();

        amqpTemplate.convertAndSend( rabbitMQ.getExchange(), routingKey, message );
    }

    @Override
    public void publish( MetadataAudit audit )
    {
        String routingKey = "metadata."
            + CaseFormat.UPPER_CAMEL.to( CaseFormat.LOWER_CAMEL, audit.getKlass() )
            + "." + audit.getType().toString().toLowerCase()
            + "." + audit.getUid();

        String auditJson = renderService.toJsonAsString( audit );

        publish( routingKey, new Message( auditJson.getBytes(), new MessageProperties() ) );
    }

    private AmqpTemplate getAmqpTemplate()
    {
        if ( amqpTemplate == null )
        {
            RabbitMQ rabbitMQ = systemService.getSystemInfo().getRabbitMQ();

            if ( rabbitMQ == null || !rabbitMQ.isValid() )
            {
                return null;
            }

            CachingConnectionFactory connectionFactory = new CachingConnectionFactory( rabbitMQ.getHost(), rabbitMQ.getPort() );
            connectionFactory.setUsername( rabbitMQ.getUsername() );
            connectionFactory.setPassword( rabbitMQ.getPassword() );
            connectionFactory.setAddresses( rabbitMQ.getAddresses() );
            connectionFactory.setVirtualHost( rabbitMQ.getVirtualHost() );
            connectionFactory.setConnectionTimeout( rabbitMQ.getConnectionTimeout() );

            if ( !verifyConnection( connectionFactory ) )
            {
                log.warn( "Unable to connect to RabbitMQ message broker: " + connectionFactory );
                return null;
            }

            AmqpAdmin admin = new RabbitAdmin( connectionFactory );
            admin.declareExchange( new TopicExchange( rabbitMQ.getExchange(), true, false ) );

            amqpTemplate = new RabbitTemplate( connectionFactory );
        }

        return amqpTemplate;
    }

    private boolean verifyConnection( ConnectionFactory connectionFactory )
    {
        try
        {
            connectionFactory.createConnection().close();
        }
        catch ( Exception ignored )
        {
            return false;
        }

        return true;
    }
}
