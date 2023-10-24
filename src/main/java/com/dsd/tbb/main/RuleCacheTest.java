package com.dsd.tbb.main;

import com.dsd.tbb.rulehandling.RuleCache;
import com.dsd.tbb.util.TBBLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class RuleCacheTest {

    private static final Random random = new Random();
    private static final String[] dimensions = {"overworld", "nether", "end"};


    private static String getRandomDimension(){
        return dimensions[random.nextInt(dimensions.length)];
    }
    public static int getRandomTime() {
        int min = 9;  // 9000 / 1000
        int max = 24;  // 24000 / 1000
        int randomNum = random.nextInt((max - min) + 1) + min;
        return randomNum * 1000;
    }

    public static void testRuleCache() {
        // Get a reference to your rule cache
        RuleCache ruleCache = RuleCache.getInstance();
        StringBuilder sb = new StringBuilder();
        // Populate the cache
        // ... (assuming you have a method to do this)
        long startT;
        long endT;
        long elapsedT;
        int tTime;
        String tDimension;
        TBBLogger.getInstance().bulkLog("testRuleCache","************************ RUNNING RULE CACHE TESTS***************************");
        TBBLogger.getInstance().bulkLog("testRuleCache",String.format("Cache Contents:\n%s",ruleCache));
        TBBLogger.getInstance().bulkLog("testRuleCahce","-----------------------------------------------------------------------------\n");

        for(int i = 0; i < 500; i++) {
            tDimension = getRandomDimension();
            tTime = getRandomTime();
            // Measure the time it takes to retrieve data from the cache
            startT = System.nanoTime();
            List<RuleCache.ApplicableRule> rules = ruleCache.getApplicableRules(tDimension,tTime);
            endT = System.nanoTime();
            elapsedT = endT - startT;
            sb.append("[").append(i).append("]Test Dimension: [").append(tDimension).append("] Test Time: [").append(tTime).append("]\n");
            sb.append("TIme Taken: ").append(elapsedT).append("\n");
            if(rules !=null){
                sb.append("Number of Rules Matched: [").append(rules.size()).append("]\n");
                sb.append("Results:\n");
                for (RuleCache.ApplicableRule a: rules){
                    sb.append(a).append("\n");
                }
            }else{
                sb.append("No Rules Returned\n");
            }

        }
        TBBLogger.getInstance().bulkLog("testRuleCache",sb.toString());
        TBBLogger.getInstance().bulkLog("testRuleCache","-----------------------------------------------------------------------------\n");
    }


    public static void multiThreadedTestCache() {
        RuleCache ruleCache = RuleCache.getInstance();
        ExecutorService executorService = Executors.newFixedThreadPool(10);  // Create a thread pool with 10 threads
        List<Future<StringBuilder>> futures = new ArrayList<>();

        for (int i = 0; i < 500; i++) {
            Future<StringBuilder> future = executorService.submit(new CacheAccessTask(ruleCache, i));
            futures.add(future);
        }
        executorService.shutdown();  // Shut down the executor service after all tasks are submitted

        // Collect results
        StringBuilder finalResult = new StringBuilder();
        for (Future<StringBuilder> future : futures) {
            try {
                finalResult.append(future.get());  // Wait for each task to complete and collect the results
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        TBBLogger.getInstance().bulkLog("multiThreadedTestCache",finalResult.toString());
    }


}


// Define a separate class for the cache access task
class CacheAccessTask implements Callable<StringBuilder> {
    private RuleCache ruleCache;
    private int taskId;
    private static final Random random = new Random();
    private static final String[] dimensions = {"overworld", "nether", "end"};

    public CacheAccessTask(RuleCache ruleCache, int taskId) {
        this.ruleCache = ruleCache;
        this.taskId = taskId;
    }

    @Override
    public StringBuilder call() {
        StringBuilder sb = new StringBuilder();
        String tDimension = getRandomDimension();
        int tTime = getRandomTime();
        long startT = System.nanoTime();
        List<RuleCache.ApplicableRule> rules = ruleCache.getApplicableRules(tDimension, tTime);
        long endT = System.nanoTime();
        long elapsedT = endT - startT;
        sb.append("[").append(taskId).append("]Test Dimension: [").append(tDimension).append("] Test Time: [").append(tTime).append("]\n");
        sb.append("Time Taken: ").append(elapsedT).append("\n");
        if (rules != null) {
            sb.append("Number of Rules Matched: [").append(rules.size()).append("]\n");
            sb.append("Results:\n");
            for (RuleCache.ApplicableRule a : rules) {
                sb.append(a).append("\n");
            }
        } else {
            sb.append("No Rules Returned\n");
        }
        return sb;
    }

    private static String getRandomDimension(){
        return dimensions[random.nextInt(dimensions.length)];
    }
    public static int getRandomTime() {
        int min = 9;  // 9000 / 1000
        int max = 24;  // 24000 / 1000
        int randomNum = random.nextInt((max - min) + 1) + min;
        return randomNum * 1000;
    }

}