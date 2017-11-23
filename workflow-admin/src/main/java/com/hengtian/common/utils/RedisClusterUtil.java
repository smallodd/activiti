package com.hengtian.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ma on 2017/11/23.
 */
public class RedisClusterUtil {
    private static final Logger log = LoggerFactory.getLogger(RedisClusterUtil.class);
    private static JedisCluster jedis = null;
    private static String RICHGO_KEY = null;

    public RedisClusterUtil() {
    }

    public static boolean isOk(String ok) {
        return "OK".equals(ok.toUpperCase());
    }

    public static long expire(String key, int seconds) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);

        try {
            return jedis.expire(key, seconds).longValue();
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
            return -1L;
        }
    }

    public static <T> T get(String key, Class<T> clazz) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        String value = null;

        try {
            value = jedis.get(key);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return (T) JsonConverUtil.getObjectFromJsonString(value, clazz);
    }

    public static String get(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        String value = null;

        try {
            value = jedis.get(key);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
        }

        return value;
    }

    public static List<?> getList(String key, Class<?> clazz) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        String value = null;

        try {
            value = jedis.get(key);
            return value == null?null:JsonConverUtil.getObjectList(value, clazz);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
            return null;
        }
    }

    public static Boolean set(String key, String value) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);

        try {
            String res = jedis.set(key, value);
            return res.equals("OK")?Boolean.valueOf(true):Boolean.valueOf(false);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
            return Boolean.valueOf(false);
        }
    }

    public static Boolean set(String key, Object obj) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);

        try {
            String value = JsonConverUtil.getStrFromObject(obj);
            String res = jedis.set(key, value);
            return res.equals("OK")?Boolean.valueOf(true):Boolean.valueOf(false);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
            return Boolean.valueOf(false);
        }
    }

    public static Boolean set(String key, Object obj, int seconds) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);

        try {
            String value = JsonConverUtil.getStrFromObject(obj);
            String res = jedis.setex(key, seconds, value);
            return res.equals("OK")?Boolean.valueOf(true):Boolean.valueOf(false);
        } catch (Exception var5) {
            log.error(var5.getMessage(), var5);
            return Boolean.valueOf(false);
        }
    }

    public static long del(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);

        try {
            return jedis.del(key).longValue();
        } catch (Exception var2) {
            log.error(var2.getMessage(), var2);
            return -1L;
        }
    }

    public static Long append(String key, String str) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.append(key, str);
            return res;
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
            return Long.valueOf(0L);
        }
    }

    public static Boolean exists(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);

        try {
            return jedis.exists(key);
        } catch (Exception var2) {
            log.error(var2.getMessage(), var2);
            return Boolean.valueOf(false);
        }
    }

    public static Long setnx(String key, String value) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);

        try {
            return jedis.setnx(key, value);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
            return Long.valueOf(0L);
        }
    }

    public static String setex(String key, String value, int seconds) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        String res = null;

        try {
            res = jedis.setex(key, seconds, value);
        } catch (Exception var5) {
            log.error(var5.getMessage(), var5);
        }

        return res;
    }

    public static Long setrange(String key, String str, int offset) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);

        try {
            return jedis.setrange(key, (long)offset, str);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
            return Long.valueOf(0L);
        }
    }

    public static String getset(String key, String value) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        String res = null;

        try {
            res = jedis.getSet(key, value);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return res;
    }

    public static String getrange(String key, int startOffset, int endOffset) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        String res = null;

        try {
            res = jedis.getrange(key, (long)startOffset, (long)endOffset);
        } catch (Exception var5) {
            log.error(var5.getMessage(), var5);
        }

        return res;
    }

    public static Long incr(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.incr(key);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
        }

        return res;
    }

    public static Long incrBy(String key, Long integer) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.incrBy(key, integer.longValue());
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return res;
    }

    public static Long decr(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.decr(key);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
        }

        return res;
    }

    public static Long decrBy(String key, Long integer) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.decrBy(key, integer.longValue());
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return res;
    }

    public static Long hset(String key, String field, String value) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.hset(key, field, value);
        } catch (Exception var5) {
            log.error(var5.getMessage(), var5);
        }

        return res;
    }

    public static Long hset(String key, String field, Object obj) {
        String value = JsonConverUtil.getStrFromObject(obj);
        return hset(key, field, value);
    }

    public static Long hsetnx(String key, String field, String value) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.hsetnx(key, field, value);
        } catch (Exception var5) {
            log.error(var5.getMessage(), var5);
        }

        return res;
    }

    public static String hmset(String key, Map<String, String> hash) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        String res = null;

        try {
            res = jedis.hmset(key, hash);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return res;
    }

    public static String hmset(String key, Map<String, String> hash, int seconds) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        String res = null;

        try {
            res = jedis.hmset(key, hash);
            jedis.expire(key, seconds);
        } catch (Exception var5) {
            log.error(var5.getMessage(), var5);
        }

        return res;
    }

    public static String hget(String key, String field) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        String res = null;

        try {
            res = jedis.hget(key, field);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return res;
    }

    public static List<String> hmget(String key, String... fields) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        List res = null;

        try {
            res = jedis.hmget(key, fields);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return res;
    }

    public static Long hincrby(String key, String field, Long value) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.hincrBy(key, field, value.longValue());
        } catch (Exception var5) {
            log.error(var5.getMessage(), var5);
        }

        return res;
    }

    public static Boolean hexists(String key, String field) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Boolean res = Boolean.valueOf(false);

        try {
            res = jedis.hexists(key, field);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return res;
    }

    public static Long hlen(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.hlen(key);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
        }

        return res;
    }

    public static Long hdel(String key, String... fields) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.hdel(key, fields);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return res;
    }

    public static Set<String> hkeys(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Set res = null;

        try {
            res = jedis.hkeys(key);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
        }

        return res;
    }

    public static List<String> hvals(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        List res = null;

        try {
            res = jedis.hvals(key);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
        }

        return res;
    }

    public static Map<String, String> hgetall(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Map res = null;

        try {
            res = jedis.hgetAll(key);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
        }

        return res;
    }

    public static Long lpush(String key, String... strs) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.lpush(key, strs);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return res;
    }

    public static Long rpush(String key, String... strs) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.rpush(key, strs);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return res;
    }

    public static Long linsert(String key, BinaryClient.LIST_POSITION where, String pivot, String value) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.linsert(key, where, pivot, value);
        } catch (Exception var6) {
            log.error(var6.getMessage(), var6);
        }

        return res;
    }

    public static String lset(String key, Long index, String value) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        String res = null;

        try {
            res = jedis.lset(key, index.longValue(), value);
        } catch (Exception var5) {
            log.error(var5.getMessage(), var5);
        }

        return res;
    }

    public static Long lrem(String key, long count, String value) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.lrem(key, count, value);
        } catch (Exception var6) {
            log.error(var6.getMessage(), var6);
        }

        return res;
    }

    public static String ltrim(String key, long start, long end) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        String res = null;

        try {
            res = jedis.ltrim(key, start, end);
        } catch (Exception var7) {
            log.error(var7.getMessage(), var7);
        }

        return res;
    }

    public static synchronized String lpop(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        String res = null;

        try {
            res = jedis.lpop(key);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
        }

        return res;
    }

    public static synchronized String rpop(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        String res = null;

        try {
            res = jedis.rpop(key);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
        }

        return res;
    }

    public static String lindex(String key, long index) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        String res = null;

        try {
            res = jedis.lindex(key, index);
        } catch (Exception var5) {
            log.error(var5.getMessage(), var5);
        }

        return res;
    }

    public static Long llen(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.llen(key);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
        }

        return res;
    }

    public static List<String> lrange(String key, long start, long end) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        List res = null;

        try {
            res = jedis.lrange(key, start, end);
        } catch (Exception var7) {
            log.error(var7.getMessage(), var7);
        }

        return res;
    }

    public static Long sadd(String key, String... members) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.sadd(key, members);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return res;
    }

    public static Long srem(String key, String... members) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.srem(key, members);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return res;
    }

    public static String spop(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        String res = null;

        try {
            res = jedis.spop(key);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
        }

        return res;
    }

    public static Long scard(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.scard(key);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
        }

        return res;
    }

    public static Boolean sismember(String key, String member) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Boolean res = null;

        try {
            res = jedis.sismember(key, member);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return res;
    }

    public static String srandmember(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        String res = null;

        try {
            res = jedis.srandmember(key);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
        }

        return res;
    }

    public static Set<String> smembers(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Set res = null;

        try {
            res = jedis.smembers(key);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
        }

        return res;
    }

    public static Long zadd(String key, double score, String member) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.zadd(key, score, member);
        } catch (Exception var6) {
            log.error(var6.getMessage(), var6);
        }

        return res;
    }

    public static Long zrem(String key, String... members) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.zrem(key, members);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return res;
    }

    public static Double zincrby(String key, double score, String member) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Double res = null;

        try {
            res = jedis.zincrby(key, score, member);
        } catch (Exception var6) {
            log.error(var6.getMessage(), var6);
        }

        return res;
    }

    public static Long zrank(String key, String member) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.zrank(key, member);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return res;
    }

    public static Long zrevrank(String key, String member) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.zrevrank(key, member);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return res;
    }

    public static Set<String> zrevrange(String key, long start, long end) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Set res = null;

        try {
            res = jedis.zrevrange(key, start, end);
        } catch (Exception var7) {
            log.error(var7.getMessage(), var7);
        }

        return res;
    }

    public static Set<String> zrangebyscore(String key, String max, String min) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Set res = null;

        try {
            res = jedis.zrevrangeByScore(key, max, min);
        } catch (Exception var5) {
            log.error(var5.getMessage(), var5);
        }

        return res;
    }

    public static Set<String> zrangeByScore(String key, double max, double min) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Set res = null;

        try {
            res = jedis.zrevrangeByScore(key, max, min);
        } catch (Exception var7) {
            log.error(var7.getMessage(), var7);
        }

        return res;
    }

    public static Long zcount(String key, String min, String max) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.zcount(key, min, max);
        } catch (Exception var5) {
            log.error(var5.getMessage(), var5);
        }

        return res;
    }

    public static Long zcard(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.zcard(key);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
        }

        return res;
    }

    public static Double zscore(String key, String member) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Double res = null;

        try {
            res = jedis.zscore(key, member);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return res;
    }

    public static Long zremrangeByRank(String key, long start, long end) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.zremrangeByRank(key, start, end);
        } catch (Exception var7) {
            log.error(var7.getMessage(), var7);
        }

        return res;
    }

    public static Long zremrangeByScore(String key, double start, double end) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.zremrangeByScore(key, start, end);
        } catch (Exception var7) {
            log.error(var7.getMessage(), var7);
        }

        return res;
    }

    public static String type(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        String res = null;

        try {
            res = jedis.type(key);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
        }

        return res;
    }

    public static Long delKey(String key) {
        key = StringUtils.setPrefix(RICHGO_KEY, key);
        Long res = null;

        try {
            res = jedis.del(key);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
        }

        return res;
    }

    private static void getJedisCluster() {
        XmlConfigReader.getInstance();
        List<RedisConfigBean> list = XmlConfigReader.read();
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        Set<HostAndPort> nodes = new HashSet();
        if(list != null && list.size() > 0) {
            RICHGO_KEY = ((RedisConfigBean)list.get(0)).getProPrefix();

            for(int i = 0; i < list.size(); ++i) {
                HostAndPort hostAndPort = new HostAndPort(((RedisConfigBean)list.get(i)).getHost(), ((RedisConfigBean)list.get(i)).getPort().intValue());
                nodes.add(hostAndPort);
            }

            jedis = new JedisCluster(nodes, poolConfig);
        }

    }

    public static void main(String[] arg) {
        String a = get("alias_H000138");
        System.out.print(a);
    }

    static {
        if(jedis == null) {
            getJedisCluster();
        }

    }
}
