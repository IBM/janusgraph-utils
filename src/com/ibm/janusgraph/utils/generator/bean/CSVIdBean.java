package com.ibm.janusgraph.utils.generator.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.RandomUtils;


public class CSVIdBean {
    private Map<String, IdRange> idMap = null;
    private Map<String, Map<String,Integer>> _idMap = null;
    
    public CSVIdBean(List<VertexTypeBean> vertexTypes){
        this.idMap = new HashMap();
        int bot = 0;
        int top = 0;
        for (VertexTypeBean vertexType: vertexTypes){
            bot = top + 1;
            top = bot + vertexType.row - 1;
            this.idMap.put(vertexType.name, new IdRange(bot, top));
        }
    }
    public int getRandomIdForVertexType(String name){
        return RandomUtils.nextInt(idMap.get(name).minId, idMap.get(name).maxId);
    }
    
    public int getMinId(String name){
        return idMap.get(name).minId;
    }
    public int getMaxId(String name){
        return idMap.get(name).maxId;
    }    
    
    class IdRange{
        int minId = 0;
        int maxId = 0;
        public IdRange(int minId, int maxId){
            this.minId = minId;
            this.maxId = maxId;
        }
    }



   
   }    
