package com.loanorigination.outbox.application.service;

import com.loanorigination.outbox.application.port.out.EventPublisherPort;
import com.loanorigination.outbox.application.port.out.OutboxRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.quarkus.scheduler.Scheduled;

import java.time.Clock;
import java.time.Duration;

@ApplicationScoped
public class OutboxPublishingService {
    private final OutboxRepository repository;
    private final EventPublisherPort publisher;
    private final IntegrationEventMapper eventMapper;
    private final Clock clock;
    @ConfigProperty(name="app.outbox.batch-size",defaultValue="50") int batchSize;
    @Inject public OutboxPublishingService(OutboxRepository repository,EventPublisherPort publisher,IntegrationEventMapper eventMapper,Clock clock){this.repository=repository;this.publisher=publisher;this.eventMapper=eventMapper;this.clock=clock;}

    @Scheduled(every="5s",concurrentExecution=Scheduled.ConcurrentExecution.SKIP)
    void publishPending(){
        var now=clock.instant();
        var ids=repository.claimBatch(now,now.minus(Duration.ofMinutes(2)),batchSize);
        for(var id:ids){
            try{var event=repository.findById(id);publisher.publish(eventMapper.from(event));repository.markPublished(id,clock.instant());}
            catch(RuntimeException failure){repository.markFailed(id,failure.getMessage());}
        }
    }
}
