package org.example;

import org.redisson.api.MapOptions;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

public interface RedissonObjectProvider {
/** Returns a new instance of an {@link org.redisson.api.RMapCache} **/
<K,V> RMapCache<K, V> getRMapCache(RedissonClient client, String cacheName, MapOptions<K,V> mapOptions);
}
