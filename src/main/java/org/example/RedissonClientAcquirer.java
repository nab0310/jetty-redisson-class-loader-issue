package org.example;

import java.util.concurrent.*;
import java.util.function.Supplier;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Redisson client acquirer that tries to acquire a {@link RedissonClient} on it's own thread.
 * This client acquirer starts a background task to acquire a redis connection in the constructor.
 * Only one instance of this class should be created.
 */
public class RedissonClientAcquirer {
  private static final Logger logger = LoggerFactory.getLogger(RedissonClientAcquirer.class);

  private volatile RedissonClient client;

  /**
   * Creates a {@link RedissonClientAcquirer} instance that starts a task to acquire a {@link
   * RedissonClient} on a background thread.
   *
   * @param redissonConfig a redisson Config object.
   */
  public RedissonClientAcquirer(Config redissonConfig) {
    Supplier<?> supplier = (Supplier<Object>) () -> {
      while (client == null) {
        try {
          client = Redisson.create(redissonConfig);
        } catch (RedisException e) {
          logger.error(
              "Failed to connect to redis.",
              e);
        }
      }
      return null;
    };

    CompletableFuture.supplyAsync(supplier);
  }

  /**
   * Gets the client instance.
   *
   * @return the configured client object or null if the client isn't configured yet.
   */
  public RedissonClient client() {
    return client;
  }
}
