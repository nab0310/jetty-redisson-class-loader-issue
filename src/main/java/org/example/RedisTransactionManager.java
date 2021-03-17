package org.example;

import org.redisson.api.RTransaction;
import org.redisson.api.RedissonClient;

/**
 * This manages a {@link org.redisson.api.RTransaction}.
 *
 * The scope of a {@link org.redisson.api.RTransaction} is contained to a single thread. Be sure to always call
 * teardown() in a finally block to avoid leaking transactions across threads.
 */
public interface RedisTransactionManager {
  /**
   * Creates a transaction. This creates a new {@link RTransaction}.
   */
  void create(RedissonClient client);

  /** Gets the transaction. */
  RTransaction get();

  /** Teardown the active transaction. */
  void teardown();

  /** Returns true if there is an active transaction on the thread; false otherwise. */
  boolean hasActiveTransaction();
}
