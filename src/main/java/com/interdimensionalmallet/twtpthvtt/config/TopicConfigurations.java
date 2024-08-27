package com.interdimensionalmallet.twtpthvtt.config;

import com.interdimensionalmallet.twtpthvtt.model.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

import java.util.stream.Stream;

@Configuration
public class TopicConfigurations {

    @Bean
    public Sinks.Many<Message<Event>> eventTopic() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }

    @Bean
    public Sinks.Many<Message<Thing>> thingTopic() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }

    @Bean
    public Sinks.Many<Message<Resource>> resourceTopic() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }

    @Bean
    public Sinks.Many<Message<Link>> linkTopic() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }

    @Bean
    public Sinks.Many<Message<WorldItem>> worldItemTopic(
            Sinks.Many<Message<Event>> eventTopic,
            Sinks.Many<Message<Thing>> thingTopic,
            Sinks.Many<Message<Resource>> resourceTopic,
            Sinks.Many<Message<Link>> linkTopic
    ) {
        Sinks.Many<Message<WorldItem>> worldItemTopic = Sinks.many().multicast().onBackpressureBuffer();

        Stream.of(eventTopic, thingTopic, resourceTopic, linkTopic)
                .forEach(topic ->
                        topic.asFlux().subscribe(message ->
                                worldItemTopic.tryEmitNext(new Message<>(message.type(), (WorldItem)message.payload()))
                        )
                );

        return Sinks.many().multicast().onBackpressureBuffer();
    }

    @Bean
    public Sinks.Many<Message<Event>> eventQueue() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }

    @Bean
    public Sinks.Many<Message<Query>> queryTopic() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }

    @Bean
    public Sinks.Many<Message<QueryOption>> queryOptionTopic() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }

}
