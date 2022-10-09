package com.aeon.restrictionpoc;

import org.redisson.RedissonNode;
import org.redisson.config.RedissonNodeConfig;

import java.util.Collections;
import java.util.Map;

public class RedissonNodeTest {
    public static void main(String[] args) throws Exception {
        RedissonNodeConfig nodeConfig = new RedissonNodeConfig();
        Map<String, Integer> workers = Collections.singletonMap("test", 1);
        nodeConfig.setExecutorServiceWorkers(workers);

        RedissonNode node = RedissonNode.create(nodeConfig);
        node.start();

        System.out.println("Redisson node started");

        node.shutdown();
        Thread.sleep(1000L);
        System.out.println("Redisson node shutdown");
    }
}
