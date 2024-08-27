package com.interdimensionalmallet.twtpthvtt.topics;

import com.interdimensionalmallet.twtpthvtt.model.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

import java.util.stream.Stream;

@Configuration
public class TopicConfigurations {

    @Bean
    public Topic<Event> eventTopic() {
        return new SinkWrapper<>(Event.class, Sinks.many().multicast().onBackpressureBuffer());
    }

    @Bean
    public Topic<Thing> thingTopic() {
        return new SinkWrapper<>(Thing.class, Sinks.many().multicast().onBackpressureBuffer());
    }

    @Bean
    public Topic<Resource> resourceTopic() {
        return new SinkWrapper<>(Resource.class, Sinks.many().multicast().onBackpressureBuffer());
    }

    @Bean
    public Topic<Link> linkTopic() {
        return new SinkWrapper<>(Link.class, Sinks.many().multicast().onBackpressureBuffer());
    }

    @Bean
    public Topic<WorldItem> worldItemTopic(
            Topic<Thing> thingTopic,
            Topic<Resource> resourceTopic,
            Topic<Link> linkTopic
    ) {
        Topic<WorldItem> worldItemTopic = new SinkWrapper<>(WorldItem.class, Sinks.many().multicast().onBackpressureBuffer());

        Stream.of(thingTopic, resourceTopic, linkTopic)
                .forEach(topic ->
                        topic.asFlux().subscribe(message ->
                                worldItemTopic.tryEmitNext(new Message<>(message.type(), (WorldItem)message.payload()))
                        )
                );

        return worldItemTopic;
    }

    @Bean
    public Topic<Query> queryTopic() {
        return new SinkWrapper<>(Query.class, Sinks.many().multicast().onBackpressureBuffer());
    }

    @Bean
    public Topic<QueryOption> queryOptionTopic() {
        return new SinkWrapper<>(QueryOption.class, Sinks.many().multicast().onBackpressureBuffer());
    }

}
