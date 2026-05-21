package bih.iths.sedina.orderservicewebshop.publisher;

import bih.iths.sedina.orderservicewebshop.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(String message) {

        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE, message);
    }
}
