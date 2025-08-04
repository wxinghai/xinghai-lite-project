package com.lite.gym.component.cache;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 
 * @author dbg
 *
 */
@Component
public class CacheComponent {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 放入不过期不序列化缓存
     *
     * @param key
     * @param value
     */
    public void putRaw(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 放入不过期不序列化缓存
     *
     * @param key
     * @param value
     * @param expireSec
     */
    public void putRaw(String key, String value, Integer expireSec) {
        stringRedisTemplate.opsForValue().set(key, value, expireSec, TimeUnit.SECONDS);
    }

    /**
     * 直接获取不反序列化缓存
     *
     * @param key
     * @return
     */
    public String getRaw(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }


    /**
     * 放入对象/集合，进行序列化
     *
     * @param key
     * @param obj
     */
    public void putObj(String key, Object obj) {
        try {
            stringRedisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(obj));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object: " + e.getMessage());
        }
    }

    /**
     * 放入对象/集合，进行序列化，带过期时间
     *
     * @param key
     * @param obj
     * @param expireSec
     */
    public void putObj(String key, Object obj, Integer expireSec) {
        try {
            stringRedisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(obj), expireSec, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object: " + e.getMessage());
        }
    }

    /**
     * 获取对象进行序列化
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getObj(String key, Class<T> clazz) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize object: " + e.getMessage());
        }
    }

    /**
     * 获取对象列表
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> getObjList(String key, Class<T> clazz) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize list: " + e.getMessage());
        }
    }

    /**
     * 将值放入Hash里
     *
     * @param hashName
     * @param key
     * @param value
     */
    public void putHashRaw(String hashName, String key, String value) {
        stringRedisTemplate.opsForHash().put(hashName, key, value);
    }

    /**
     * 设置Hash对象，进行序列化
     *
     * @param hashName
     * @param key
     * @param obj
     */
    public void putHashObj(String hashName, String key, Object obj) {
        try {
            stringRedisTemplate.opsForHash().put(hashName, key, objectMapper.writeValueAsString(obj));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object: " + e.getMessage());
        }
    }

    /**
     * 设置Hash对象，进行序列化
     *
     * @param hashName
     * @param key
     * @param obj
     * @param expireSec
     */
    public void putHashObj(String hashName, String key, Object obj, Integer expireSec) {
        try {
            boolean hasKey = stringRedisTemplate.hasKey(key);
            stringRedisTemplate.opsForHash().put(hashName, key, objectMapper.writeValueAsString(obj));
            if (!hasKey) {
                stringRedisTemplate.expire(key, expireSec, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object: " + e.getMessage());
        }
    }

    /**
     * 增加Hash表中键的字面数值
     *
     * @param hashName
     * @param key
     * @param delta
     * @return
     */
    public long incrementHashKey(String hashName, String key, long delta) {
        return stringRedisTemplate.opsForHash().increment(hashName, key, delta);
    }

    /**
     * 减少Hash表中字面的数值
     *
     * @param hashName
     * @param key
     * @param delta
     * @return
     */
    public long decrementHashKey(String hashName, String key, long delta) {
        return stringRedisTemplate.opsForHash().increment(hashName, key, -delta);
    }

    /**
     * 获取Hash值，不进行序列化
     *
     * @param hashName
     * @param key
     * @return
     */
    public String getHashRaw(String hashName, String key) {
        String o = (String) stringRedisTemplate.opsForHash().get(hashName, key);
        if (StringUtils.isEmpty(o)) {
            return null;
        }
        return o;
    }

    /**
     * 获取Hash值，带反序列化
     *
     * @param hashName
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getHashObj(String hashName, String key, Class<T> clazz) {
        String o = (String) stringRedisTemplate.opsForHash().get(hashName, key);
        if (StringUtils.isEmpty(o)) {
            return null;
        }
        try {
            return objectMapper.readValue(o, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize object: " + e.getMessage());
        }
    }

    /**
     * 获取Hash值，以数组的形式反序列化
     *
     * @param hashName
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> getHashList(String hashName, String key, Class<T> clazz) {
        String o = (String) stringRedisTemplate.opsForHash().get(hashName, key);
        if (StringUtils.isEmpty(o)) {
            return null;
        }
        try {
            return objectMapper.readValue(o, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize list: " + e.getMessage());
        }
    }

    /**
     * 批量获取Hash表里面的值
     *
     * @param hashName
     * @param keyCollection String类型键集合 Collection<String>
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> List<T> getHashMultiAsList(String hashName, Collection keyCollection, Class<T> clazz) {
        List<String> list = stringRedisTemplate.opsForHash().multiGet(hashName, keyCollection);
        return list.stream().map(item -> {
            try {
                return objectMapper.readValue(item, clazz);
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize object: " + e.getMessage());
            }
        }).collect(Collectors.toList());
    }

    /**
     * 删除Hash值
     *
     * @param hashName
     * @param key
     */
    public void delHashKey(String hashName, String key) {
        stringRedisTemplate.opsForHash().delete(hashName, key);
    }

    public void delHashKeyList(String hashName, List<String> keys) {
        stringRedisTemplate.opsForHash().delete(hashName, keys.toArray());
    }

    public void putHashAll(String key, Map<String, String> map, Integer expireSec) {
        stringRedisTemplate.opsForHash().putAll(key, map);
        stringRedisTemplate.expire(key, expireSec, TimeUnit.SECONDS);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String,String> getHashAll(String key) {
        if (!stringRedisTemplate.hasKey(key)) {
            return null;
        }
        return (Map)stringRedisTemplate.opsForHash().entries(key);
    }


    /**
     * 向有序集合中添加元素
     *
     * @param setName
     * @param source
     * @param value
     */
    public void putSortedSet(String setName, double source, String value) {
        stringRedisTemplate.opsForZSet().add(setName, value, source);
    }

    public void putSortedSetMulti(String setName, Set<ZSetOperations.TypedTuple<String>> values) {
        stringRedisTemplate.opsForZSet().add(setName, values);
    }

    /**
     * 从有序集合中移除数据
     *
     * @param setName
     * @param value
     */
    public void delSortedSet(String setName, String value) {
        stringRedisTemplate.opsForZSet().remove(setName, value);
    }

    /**
     * 设置Lru，最后进来的排最前面
     * @param setName
     * @param value
     * @param max
     * @param exceed 可允许超出范围，清理缓存区。
     */
    public void putSortedSetLru(String setName, String value, int max, int exceed) {
        Long size = stringRedisTemplate.opsForZSet().size(setName);
        if (size > max + exceed - 1) {
            //超过了。淘汰了
            stringRedisTemplate.opsForZSet().removeRange(setName, size - exceed, size);
        }
        stringRedisTemplate.opsForZSet().add(setName, value, -System.currentTimeMillis());
    }

    /**
     * 增加ZSet分数
     * @param setName
     * @param value
     * @param delta
     */
    public Double incSortedSetSource(String setName, String value, double delta) {
        return stringRedisTemplate.opsForZSet().incrementScore(setName, value, delta);
    }

    /**
     * 获取前N个
     * @param setName
     * @param n
     * @return
     */
    public Set<String> getSortedSetLruTopN(String setName, int n) {
        return stringRedisTemplate.opsForZSet().range(setName, 0 , n);
    }


    /**
     * 向一个set中添加数据
     * @param key
     * @param member
     * @param expireSec
     */
    public void putSetRaw(String key, String member, Integer expireSec) {
        stringRedisTemplate.opsForSet().add(key, member);
        stringRedisTemplate.expire(key, expireSec, TimeUnit.SECONDS);
    }

    public void putSetRawAll(String key, String[] set, Integer expireSec) {
        stringRedisTemplate.opsForSet().add(key, set);
        stringRedisTemplate.expire(key, expireSec, TimeUnit.SECONDS);
    }

    public void removeSetRaw(String key, String member) {
        stringRedisTemplate.opsForSet().remove(key, member);
    }

    public boolean isSetMember(String key, String member) {
        return stringRedisTemplate.opsForSet().isMember(key, member);
    }


    /**
     * 删除键 / 桶 / hash 表等
     *
     * @param key
     */
    public void del(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 判断是否包含键
     *
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }


    /**
     * 获取指定前缀的Key
     *
     * @param prefix
     * @return
     */
    public Set<String> getPrefixKeySet(String prefix) {
        return stringRedisTemplate.keys(prefix + "*");
    }

    public void delPrefixKey(String prefix) {
        Set<String> prefixKeySet = getPrefixKeySet(prefix);
        for (String key : prefixKeySet) {
            stringRedisTemplate.delete(key);
        }
    }

    /**
     * 获取redis中键的过期时间
     * 返回秒
     * @param key
     * @return
     */
    public Long getKeyExpire(String key){
        return stringRedisTemplate.getExpire(key);
    }


}
