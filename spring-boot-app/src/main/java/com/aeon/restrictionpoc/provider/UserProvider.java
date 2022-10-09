package com.aeon.restrictionpoc.provider;

import com.aeon.restrictionpoc.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.aeon.restrictionpoc.config.RedisConfig.USER_LOCAL_MAP;
import static com.aeon.restrictionpoc.config.RedisConfig.USER_MAP;

@Slf4j
@Service
public class UserProvider {
    private final RLocalCachedMap<String, User> userCacheMap;
    private final RMap<String, User> userMap;

    protected UserProvider(@Qualifier(USER_LOCAL_MAP) RLocalCachedMap<String, User> userCacheMap,
                           @Qualifier(USER_MAP) RMap<String, User> userMap) {
        this.userCacheMap = userCacheMap;
        this.userCacheMap.preloadCache();
        this.userMap = userMap;
    }

    public Optional<User> get(String id) {
        long start = System.currentTimeMillis();
//        final User user = userCacheMap.get(id);
        final User user = userMap.get(id);
        log.info("get({}) completed in {} ms", id, System.currentTimeMillis() - start);
        return Optional.ofNullable(user);
    }

    public void put(User user) {
        long start = System.currentTimeMillis();
//        userCacheMap.put(user.getId(), user);
        userMap.put(user.getId(), user);
        log.info("put({}) completed in {} ms", user, System.currentTimeMillis() - start);
    }

    public List<User> getAll() {
        long start = System.currentTimeMillis();
//        final Collection<User> result = userCacheMap.values();
        final Collection<User> result = userMap.values();
        log.info("getAll completed in {} ms", System.currentTimeMillis() - start);
        return new ArrayList<>(result);
    }

    public List<User> getAll(List<String> ids) {
        long start = System.currentTimeMillis();
        final Map<String, User> result = userMap.getAll(new HashSet<>(ids));
        log.info("getAll({}) completed in {} ms","ids", System.currentTimeMillis() - start);
        return new ArrayList<>(result.values());
    }
}
