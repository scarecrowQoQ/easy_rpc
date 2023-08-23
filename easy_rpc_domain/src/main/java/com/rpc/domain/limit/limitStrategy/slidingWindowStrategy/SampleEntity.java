package com.rpc.domain.limit.limitStrategy.slidingWindowStrategy;

import lombok.Data;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

@Data
public class SampleEntity {

    private ConcurrentHashMap<Object,Integer> passMap;

    private ConcurrentHashMap<Object,Integer> blockMap;
    
    private AtomicInteger pass;

    private AtomicInteger block;

    public SampleEntity() {
        passMap = new ConcurrentHashMap<>();
        blockMap = new ConcurrentHashMap<>();
        pass = new AtomicInteger(0);
        block = new AtomicInteger(0);
    }

    public void init(){
        passMap = new ConcurrentHashMap<>();
        blockMap = new ConcurrentHashMap<>();
        pass.set(0);
        block.set(0);
    }

    public void addPass(Object obj){
        passMap.put(obj, passMap.getOrDefault(obj,0)+1);
        addPass();
    }

    public void addBlock(Object obj){
        blockMap.put(obj, blockMap.getOrDefault(obj,0)+1);
        addPass();
    }

    public void addPass(){
        pass.incrementAndGet();
    }

    public void addBlock(){
        block.incrementAndGet();
    }

    public int getPassCountByKey(Object obj){
        return passMap.getOrDefault(obj,0);
    }

    public int getBlockCountByKey(Object obj){
        return passMap.getOrDefault(obj,0);
    }
    
    public int getPassCount(){
        return passMap.reduceValuesToInt(1, (v) -> v, 0, Integer::sum);
    }
    
    public int getBlockCount(){
        return blockMap.reduceValuesToInt(1, (v) -> v, 0, Integer::sum);

    }




}
