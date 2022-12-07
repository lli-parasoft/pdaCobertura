package com.parasoft.demoapp.config.activemq;

import com.parasoft.demoapp.config.MQConfig;
import com.parasoft.demoapp.config.RefreshableMessageListener;
import com.parasoft.demoapp.model.global.preferences.MqType;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.Message;
import javax.jms.MessageListener;

public abstract class ActiveMQRefreshableMessageListener extends RefreshableMessageListener<MessageListenerContainer> implements MessageListener {

    private static final String ID_PREFIX = "id.";

    protected final CachingConnectionFactory cachingConnectionFactory;

    protected final MessageConverter jmsMessageConverter;
    private final JmsListenerEndpointRegistry jmsListenerEndpointRegistry;
    private final DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory;

    public ActiveMQRefreshableMessageListener(MessageConverter jmsMessageConverter,
                                              JmsListenerEndpointRegistry jmsListenerEndpointRegistry,
                                              DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory,
                                              String destinationName, CachingConnectionFactory cachingConnectionFactory) {
        this.jmsMessageConverter = jmsMessageConverter;
        this.jmsListenerEndpointRegistry = jmsListenerEndpointRegistry;
        this.jmsQueueListenerContainerFactory = jmsQueueListenerContainerFactory;
        this.cachingConnectionFactory = cachingConnectionFactory;

        if(MQConfig.currentMQType == MqType.ACTIVE_MQ) {
            registerListener(destinationName);
        }
    }

    private void registerListener(String destinationName) {
        SimpleJmsListenerEndpoint jmsListenerEndpoint = new SimpleJmsListenerEndpoint();
        jmsListenerEndpoint.setMessageListener(this);
        jmsListenerEndpoint.setDestination(destinationName);
        jmsListenerEndpoint.setId(ID_PREFIX + destinationName);
        jmsListenerEndpointRegistry.
                registerListenerContainer(jmsListenerEndpoint, jmsQueueListenerContainerFactory, true);
        listenedListenerContainers.put(destinationName, jmsListenerEndpointRegistry.getListenerContainer(ID_PREFIX + destinationName));
    }

    public void refreshDestination(String destinationName) {
        stopAllListenedDestinations();

        MessageListenerContainer targetMessageListenerContainer = listenedListenerContainers.get(destinationName);
        if(targetMessageListenerContainer == null) {
            registerListener(destinationName);
        } else {
            targetMessageListenerContainer.start();
        }

        cachingConnectionFactory.resetConnection();
    }

    public abstract void onMessage(Message message);

    public void stopAllListenedDestinations() {
        for(MessageListenerContainer container : listenedListenerContainers.values()) {
            container.stop();
        }
    }
}
