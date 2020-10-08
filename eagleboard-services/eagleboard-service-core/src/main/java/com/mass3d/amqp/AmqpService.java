package com.mass3d.amqp;

import com.mass3d.schema.audit.MetadataAudit;
import org.springframework.amqp.core.Message;

public interface AmqpService
{
    boolean isEnabled();

    void publish(String routingKey, Message message);

    void publish(MetadataAudit audit);
}
