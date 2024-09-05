package com.interdimensionalmallet.twtpthvtt.rules;

import com.interdimensionalmallet.twtpthvtt.db.IdCaches;
import com.interdimensionalmallet.twtpthvtt.topics.Topic;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

@Configuration
public class RuleEngineConfig {

    private static final String RULES_PATH = "classpath:rules/**/*.drl";

    private static byte[] getBytes(Resource resource) {
        try {
            return resource.getContentAsByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to get bytes from resource", e);
        }
    }

    private static String getUrlString(Resource resource) {
        try {
            return resource.getURL().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to get URL from resource", e);
        }
    }

    private static Stream<Tuple2<String, byte[]>> getRuleFileStreams(ResourcePatternResolver resourcePatternResolver) {
        try {
            return Arrays.stream(resourcePatternResolver.getResources(RULES_PATH))
                    .map(rsc -> Tuples.of(getUrlString(rsc), getBytes(rsc)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load rules", e);
        }
    }

    @Bean
    public KieContainer createContainer(ResourcePatternResolver resourcePatternResolver) {
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        getRuleFileStreams(resourcePatternResolver)
                .map(tuple -> ResourceFactory.newByteArrayResource(tuple.getT2()).setSourcePath(tuple.getT1()))
                .forEach(kfs::write);

        KieBuilder kb = KieServices.Factory.get().newKieBuilder(kfs);
        kb.buildAll();
        return KieServices.Factory.get().newKieContainer(kb.getKieModule().getReleaseId());
   }

    @Bean
    public KieSession createSession(KieContainer kieContainer, IdCaches idCaches) {
        KieSession kieSession = kieContainer.newKieSession();
        kieSession.setGlobal("idCaches", idCaches);
        return kieSession;
    }

}
