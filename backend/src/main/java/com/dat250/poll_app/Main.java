package com.dat250.poll_app;


import io.valkey.Transaction;
import io.valkey.UnifiedJedis;

import java.util.Map;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        UnifiedJedis jedis = new UnifiedJedis("redis://localhost:6379");

        // Code that interacts with Redis...
        useCase1_loggedInUsers(jedis);

        useCase2_pollWithOptions(jedis);

        jedis.close();
    }

    private static void useCase1_loggedInUsers(UnifiedJedis jedis) {
        System.out.println("Use case 1: track logged-in users with a Set");
        String loggedInKey = "app:auth:loggedin";
        jedis.del(loggedInKey); // Clear key

        System.out.println("\n-- Use case 1: logged-in users (Set) --");
        System.out.println("Initial: members=" + jedis.smembers(loggedInKey) + ", count=" + jedis.scard(loggedInKey));

        // alice logs in
        jedis.sadd(loggedInKey, "alice");
        System.out.println("After alice login: " + jedis.smembers(loggedInKey) + ", count=" + jedis.scard(loggedInKey));

        // bob logs in
        jedis.sadd(loggedInKey, "bob");
        System.out.println("After bob login: " + jedis.smembers(loggedInKey) + ", count=" + jedis.scard(loggedInKey));

        // alice logs off
        jedis.srem(loggedInKey, "alice");
        System.out.println("After alice logout: " + jedis.smembers(loggedInKey) + ", count=" + jedis.scard(loggedInKey));

        // eve logs in
        jedis.sadd(loggedInKey, "eve");
        System.out.println("After eve login: " + jedis.smembers(loggedInKey) + ", count=" + jedis.scard(loggedInKey));
    }

    private static void useCase2_pollWithOptions(UnifiedJedis jedis) {
        System.out.println("\n-- Use case 2: poll votes (Hashes) --");

        String pollId = "03ebcb7b-bd69-440b-924e-f5b7d664af7b";
        String pollKey   = "poll:" + pollId;
        String idxKey    = "poll:" + pollId + ":optidx";
        String opt0Key   = "poll:" + pollId + ":opt:0";
        String opt1Key   = "poll:" + pollId + ":opt:1";
        String opt2Key   = "poll:" + pollId + ":opt:2";
        String voteLog   = "poll:" + pollId + ":votelog";

        // reset the keys we will use
        jedis.del(pollKey, idxKey, opt0Key, opt1Key, opt2Key, voteLog);

        // question
        jedis.hset(pollKey, Map.of("question", "Pineapple on Pizza?"));
        System.out.println("Poll data: " + jedis.hgetAll(pollKey));

        // options (caption + starting votes)
        jedis.hset(opt0Key, Map.of("caption", "Yes, yammy!", "votes", "269"));
        jedis.hset(opt1Key, Map.of("caption", "Mamma mia, nooooo!", "votes", "268"));
        jedis.hset(opt2Key, Map.of("caption", "I do not really care ...", "votes", "42"));

        // list of option indices, so we know what to read back
        jedis.rpush(idxKey, "0", "1", "2");

        // read once
        System.out.println("Option indices: " + jedis.lrange(idxKey, 0, -1));
        System.out.println("opt:0 -> " + jedis.hgetAll(opt0Key));
        System.out.println("opt:1 -> " + jedis.hgetAll(opt1Key));
        System.out.println("opt:2 -> " + jedis.hgetAll(opt2Key));

        // simulate a vote for option 1
        Transaction tx = (Transaction) jedis.multi();
        tx.hincrBy(opt1Key, "votes", 1);
        tx.lpush(voteLog, "opt:1@" + System.currentTimeMillis());
        tx.exec();

        System.out.println("After one vote on opt:1 -> " + jedis.hgetAll(opt1Key));
        List<String> firstLog = jedis.lrange(voteLog, 0, 0);
        System.out.println("Latest vote log entry: " + (firstLog.isEmpty() ? null : firstLog.getFirst()));

        // read the poll back plainly
        System.out.println("\nRead-back:");
        Map<String, String> data = jedis.hgetAll(pollKey);
        List<String> indices = jedis.lrange(idxKey, 0, -1);
        System.out.println("question = " + data.get("question"));
        for (String idx : indices) {
            Map<String, String> opt = jedis.hgetAll("poll:" + pollId + ":opt:" + idx);
            System.out.println("  [" + idx + "] " + opt.get("caption") + " -> votes=" + opt.get("votes"));
        }
    }
}
