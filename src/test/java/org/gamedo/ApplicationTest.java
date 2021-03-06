package org.gamedo;

import org.gamedo.config.Config;
import org.gamedo.db.TestData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(classes = Config.class)
@EnableAutoConfiguration
public class ApplicationTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testConcurrentModificationException() {

        Assertions.assertNotNull(mongoTemplate);

        mongoTemplate.dropCollection(TestData.class);

        final TestData testData = new TestData();
        final List<Integer> synchronizedList = testData.getSynchronizedList();
        final AtomicInteger elementValue = new AtomicInteger(10001);

        final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            while (true) {
                synchronizedList.add(elementValue.getAndIncrement());
            }
        });

        Assertions.assertDoesNotThrow(() -> {
            CompletableFuture.runAsync(() -> mongoTemplate.save(testData)).join();
        });

        try {
            future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
        }
    }
}
