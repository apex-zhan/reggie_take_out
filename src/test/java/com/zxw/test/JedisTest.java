package com.zxw.test;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

/**
 * 使用jedis操作redis
 */
public class JedisTest {
    @Test
    public void testredis() {
        //获取连接
        Jedis jedis = new Jedis("localhost", 6379);
        //执行具体操作
        jedis.set("name", "zxw");
        String value = jedis.get("name");
        System.out.println(value);
        //关闭连接
        jedis.close();
    }
}