package com.interdimensionalmallet.twtpthvtt.rules.objHandlers;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Item;
import org.kie.api.runtime.KieSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class RuleItemLoader {

    @Bean
    Mono<Void> allItemsLoaded(List<RuleItemHandlerMetadata<? extends Item>> ruleItemHandlerMetadataList, Repos repos, KieSession kieSession) {
        Mono<Void> otherItemsLoaded = Mono.when(
                ruleItemHandlerMetadataList.stream()
                    .map(RuleItemHandlerMetadata::allItems)
                    .map(flux -> flux.doOnNext(kieSession::insert))
                    .map(Flux::then)
                    .toList()
        );
        Mono<Void> linksLoaded = repos.links().findAll().doOnNext(kieSession::insert).then();
        return Mono.when(otherItemsLoaded, linksLoaded).cache();
    }

}
