package org.xynok.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 建立双向关联关系
 * 为后续获取相关数据提供支持
 * 根据需要覆盖主从类的equals、hashCode、compareTo
 * add方法增加主从关联关系
 * totalMainObjects主对象数
 * totalSubObjects从对象数
 * getMainCollection主对象集合
 * getSubCollection从对象集合
 * getMainSortedList主对象排序列表
 * getSubSortedList从对象排序列表
 * getMainMapWithSortedValues取主从映射，从是经过排序的列表
 * getSubMapWithSortedValues取从主映射，主是经过排序的列表
 * @author xynok
 * @since 2020-12-09
 */
public class DualRelation<M, S> {
    /** 正向关系 */
    private Map<M, Set<S>> positiveMap = new HashMap<>();
    /** 反向关系 */
    private Map<S, Set<M>> reverseMap = new HashMap<>();
    /** 主对象集合 */
    private Set<M> mainSet = new HashSet<>();
    /** 从对象集合 */
    private Set<S> subSet = new HashSet<>();

    /** 是否空 */
    public boolean isEmpty() {
        return positiveMap.size() == 0;
    }

    /**
     * 向主对象中增加一个关联对象
     * 同时会增加反向关联
     * @param mainObject 主对象
     * @param subObject  关联对象
     */
    public void add(M mainObject, S subObject) {
        if (mainObject == null || subObject == null) {
            return;
        }
        Set<S> subSet = positiveMap.get(mainObject);
        if (subSet == null) {
            subSet = new HashSet<>();
            positiveMap.put(mainObject, subSet);
        }
        subSet.add(subObject);
        this.subSet.add(subObject);
        Set<M> mainSet = reverseMap.get(subObject);
        if (mainSet == null) {
            mainSet = new HashSet<>();
            reverseMap.put(subObject, mainSet);
        }
        mainSet.add(mainObject);
        this.mainSet.add(mainObject);
    }

    /**
     * 向主对象中增加一组关联对象
     * 同时会增加一组反向关联
     * @param mainObject
     * @param subObjects
     */
    public void add(M mainObject, Collection<S> subObjects) {
        if(mainObject==null || subObjects==null || subObjects.size()<=0){
            return;
        }
        Set<S> subSet=positiveMap.get(mainObject);
        if(subSet==null){
            subSet=new HashSet<>();
            positiveMap.put(mainObject,subSet);
        }
        subSet.addAll(subObjects);
        this.subSet.addAll(subObjects);
        Set<M> mainSet;
        for(S subObject:subObjects){
            mainSet=reverseMap.get(subObject);
            if(mainSet==null){
                mainSet=new HashSet<>();
                reverseMap.put(subObject,mainSet);
            }
            mainSet.add(mainObject);
        }
        this.mainSet.add(mainObject);
    }

    /**主对象数量*/
    public int totalMainObjects(){
        return mainSet.size();
    }

    /**子对象数量*/
    public int totalSubObjects(){
        return subSet.size();
    }

    /**主对象集合*/
    public Set<M> getMainCollection(){
        return mainSet;
    }

    /**子对象集合*/
    public Set<S> getSubCollection(){
        return subSet;
    }

    /**获取主排序列表*/
    public List<M> getMainSortedList(Comparator<Object> comparator){
        ArrayList<M> list=new ArrayList<>(this.mainSet);
        if(comparator!=null){
            list.sort(comparator);
        }else{
            list.sort(new ChinaCompare());
        }
        return list;
    }
    
    
    /**获从排序列表*/
    public List<S> getSubSortedList(Comparator<Object> comparator){
        ArrayList<S> list=new ArrayList<>(this.subSet);
        if(comparator!=null){
            list.sort(comparator);
        }else{
            list.sort(new ChinaCompare());
        }
        return list;
    }
    


    /**
     * 取主从映射,且从列表排序
     * @param comparator 排序对象,如果空使用默认的ChinaCompare
     * @return
     */
    public Map<M,List<S>> getMainMapWithSortedValues(Comparator<Object> comparator){
        HashMap<M,List<S>> mapList=new HashMap<>(64);
        Iterator<Map.Entry<M, Set<S>>> iter = positiveMap.entrySet().iterator();
        Map.Entry<M, Set<S>> item;
        List<S> sortedMapList;
        if(comparator==null){
            comparator=new ChinaCompare();
        }
        while(iter.hasNext()){
            item = iter.next();
            sortedMapList=new ArrayList<>(item.getValue());
            sortedMapList.sort(comparator);
            mapList.put(item.getKey(),sortedMapList);
        }
        return mapList;
    }


    /**
     * 取主从映射,且从列表排序
     * @param comparator 排序对象,如果空使用默认的ChinaCompare
     * @return
     */
    public Map<S,List<M>> getSubMapWithSortedValues(Comparator<Object> comparator){
        HashMap<S,List<M>> mapList=new HashMap<>(64);
        Iterator<Map.Entry<S, Set<M>>> iter = reverseMap.entrySet().iterator();
        Map.Entry<S, Set<M>> item;
        List<M> sortedMapList;
        if(comparator==null){
            comparator=new ChinaCompare();
        }
        while(iter.hasNext()){
            item = iter.next();
            sortedMapList=new ArrayList<>(item.getValue());
            sortedMapList.sort(comparator);
            mapList.put(item.getKey(),sortedMapList);
        }
        return mapList;
    }
}