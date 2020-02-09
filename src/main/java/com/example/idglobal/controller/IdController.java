package com.example.idglobal.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RestController
@RequestMapping("/id")
public class IdController {

    @Resource
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 利用redis生成全局唯一id
     */
    @GetMapping("/getOrderId")
    public String getOrderId() {
        //生成id为当前日期（yyMMddHHmmss）+6位（从000000开始不足位数补0）
        LocalDateTime now = LocalDateTime.now();
        //生成yyyyMMddHHmmss
        String orderIdPrefix = getOrderIdPrefix(now);
        String orderId = orderIdPrefix + String.format("%1$06d", generate(orderIdPrefix, getExpireAtTime(now)));
        return orderId;

    }



    public static String getOrderIdPrefix(LocalDateTime now) {
        return now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    /**
     * 设置key的过期时间是20秒
     * @param now
     * @return
     */
    public Date getExpireAtTime(LocalDateTime now) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = now.plusSeconds(20);
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    public long generate(String key, Date expireTime) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        Long expire = counter.getExpire();
        if (expire == -1) {
            counter.expireAt(expireTime);
        }
        return counter.incrementAndGet();
    }


    public static void main(String[] args) {
        ZoneId zoneId = ZoneId.systemDefault();
        System.out.println(zoneId);
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        LocalDateTime localDateTime = now.plusSeconds(20);
        System.out.println(localDateTime);
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        System.out.println(zdt);
        Date date = Date.from(zdt.toInstant());
        System.out.println(date);
    }


    @GetMapping("/getBatchId")
    public String getBatchId() {
        for(int i =0; i<100;i++){
            //生成id为当前日期（yyMMddHHmmss）+6位（从000000开始不足位数补0）
            LocalDateTime now = LocalDateTime.now();
            //生成yyyyMMddHHmmss
            String orderIdPrefix = getOrderIdPrefix(now);
            String orderId = orderIdPrefix + String.format("%1$06d", generate(orderIdPrefix, getExpireAtTime(now)));
            System.out.println("i=" + i + ",orderId =" + orderId);
        }
        return "success";

    }

    @RequestMapping("/order")
    public String getOrderId(String key) {
        RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        for(int i =0; i<100;i++){
            long increamnetGet = redisAtomicLong.incrementAndGet();
            LocalDateTime now = LocalDateTime.now();
            String orderId = getOrderIdPrefix(now) +   String.format("%1$06d", increamnetGet);
            System.out.println(orderId);
        }
        return "success";

    }
}