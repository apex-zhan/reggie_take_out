package com.zxw.test;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

/**
 * ʹ��jedis����redis
 */
public class JedisTest {
    @Test
    public void testredis() {
        //��ȡ����
        Jedis jedis = new Jedis("localhost", 6379);
        //ִ�о������
        jedis.set("name", "zxw");
        String value = jedis.get("name");
        System.out.println(value);
        //�ر�����
        jedis.close();
    }
}