package org.xynok.tools;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 时间相关方法
 * 当前时间向后3天TimeData
 * nextCycle(null, 3, ChronoUnit.DAYS);
 * 返回2001-05-14 07:30:00过去20年的时间串
 * nextCycleTimeStr("2001-05-14 07:30:00", 20, ChronoUnit.YEARS);
 * 当前时间串
 * currentTimeStr();
 * currentTimeStr("yyyy-MM-dd HH");
 * 格式化时间串
 * formatTime(System.currentTimeMillis(),"yyyy-MM-dd HH");
 * formatTime(new Date(),"yyyy-MM-dd HH");
 * 转换时间串格式
 * transTimeFmt("2020/12/07 14:30:00","yyyy/MM/dd HH:mm:ss","yyyy-MM-dd HH:mm:ss")
 * 转换时间列表串格式
 * transTimeListFmt(timeStrList,"yyyy/MM/dd HH:mm:ss","yyyy-MM-dd HH:mm:ss")
 * 时间串转为毫秒
 * transTimeToLong(timeStr,"yyyy/MM/dd HH:mm:ss")
 * 时间串列表转为毫秒列表
 * transTimeListToLongs(timeStrList,"yyyy/MM/dd HH:mm:ss")
 * 合并时间列表，按时间升序返回
        List<String> list1=new ArrayList<>();
        list1.add("2020-12-07 13:00:00");
        list1.add("2020-12-07 10:00:00");
        list1.add("2020-12-07 14:00:00");
        list1.add("2020-12-07 19:00:00");
        List<String> list2=new ArrayList<>();
        list2.add("2020-12-06 05:00:00");
        list2.add("2020-12-06 13:00:00");
        list2.add("2020-12-06 08:00:00");
        list2.add("2020-12-06 20:00:00");
        list2.add("2020-12-06 10:00:00");
        List<String> list3=new ArrayList<>();
        list3.add("2020-12-06 07:00:00");
        list3.add("2020-12-06 02:00:00");
        list3.add("2020-12-06 22:00:00");
        list3.add("2020-12-06 21:00:00");
        list3.add("2020-12-06 09:00:00");
        List<String> combineList = combineTime(null, null,true, list1, list2, list3);
        combineList.forEach(item->{
            System.out.println(item);
        });
 * 合并时间列表，按时间降序返回
 * combineTime(fmtStrFrom,fmtStrTo,false,timeStrList1,timeStrList2,...)
 * @author xynok
 */
public class TimeUtil {

    /**
     * 给定“yyyy-MM-dd HH:mm:ss”格式的时间串，计算下一周期的时间，并返回“yyyy-MM-dd HH:mm:ss”格式的时间串
     * 
     * @param timeStr 起始时间串
     * @param cycle   周期
     * @param unit    周期单位（Seconds、Minutes、Our）
     * @return
     */
    public static String nextCycleTimeStr(String timeStr, long cycle, ChronoUnit unit) {
        return nextCycleTimeStr(timeStr, null, null, cycle, unit);
    }

    /**
     * 给定“yyyy-MM-dd HH:mm:ss”格式的时间串，计算下一周期的时间，并返回指定格式的时间串
     * 
     * @param timeStr 起始时间串
     * @param fmtTo   返回时间串格式，如果为空，使用默认格式“yyyy-MM-dd HH:mm:ss”
     * @param cycle   周期
     * @param unit    周期单位（Seconds、Minutes、Our）
     * @return
     */
    public static String nextCycleTimeStrByFmtTo(String timeStr, String fmtTo, long cycle, ChronoUnit unit) {
        return nextCycleTimeStr(timeStr, null, fmtTo, cycle, unit);
    }

    /**
     * 给定起始格式的时间串，计算下一周期的时间，并返回“yyyy-MM-dd HH:mm:ss”格式的时间串
     * 
     * @param timeStr 起始时间串
     * @param fmtFrom 起始时间串格式，如果为空，使用默认格式“yyyy-MM-dd HH:mm:ss”
     * @param cycle   周期
     * @param unit    周期单位（Seconds、Minutes、Our）
     * @return
     */
    public static String nextCycleTimeStrByFmtFrom(String timeStr, String fmtFrom, long cycle, ChronoUnit unit) {
        return nextCycleTimeStr(timeStr, fmtFrom, null, cycle, unit);
    }

    /**
     * 给定起始格式的时间串，计算下一周期的时间，并返回指定格式的时间串
     * 
     * @param timeStr 起始时间串
     * @param fmtFrom 起始时间串格式，如果为空，使用默认格式“yyyy-MM-dd HH:mm:ss”
     * @param fmtTo   返回时间串格式，如果为空，使用默认格式“yyyy-MM-dd HH:mm:ss”
     * @param cycle   周期
     * @param unit    周期单位（Seconds、Minutes、Our）
     * @return
     */
    public static String nextCycleTimeStr(String timeStr, String fmtFrom, String fmtTo, long cycle, ChronoUnit unit) {
        TimeData timeData = TimeData.build(timeStr, fmtFrom, fmtTo);
        DateTimeFormatter format;
        if (fmtTo == null) {
            fmtTo = "yyyy-MM-dd HH:mm:ss";
            format = TimeData.DEFAULT_FORMAT;
        } else {
            format = DateTimeFormatter.ofPattern(fmtTo);
        }
        LocalDateTime localDataTime = timeData.getLocalDateTime().plus(cycle, unit);
        return format.format(localDataTime);
    }

    /**
     * 计算下一个时间周期 关于时间数据参数： 起始时间可以是字符串日期、毫秒日期、Date、LocalDateTime
     * fmtFrom限定原始日期串格式、fmtTo用于限定转换后的日期格式 如果时间数据参数为空，会使用当前时间作为起始时间
     * 计算结果以日期串、毫秒日期、Date和LocalDateTime个格式返回
     * 
     * @param timeData 起始时间数据
     * @param cycle    周期
     * @param unit     周期单位
     * @return
     */
    public static TimeData nextCycle(TimeData timeData, long cycle, ChronoUnit unit) {
        if (timeData == null) {
            timeData = TimeData.build(System.currentTimeMillis(), null);
        }
        TimeData result = new TimeData();
        String fmtTo = timeData.getFmtTo();
        if (fmtTo == null) {
            fmtTo = "yyyy-MM-dd HH:mm:ss";
        }
        DateTimeFormatter format = DateTimeFormatter.ofPattern(fmtTo);
        result.setFmtTo(fmtTo);
        result.setLocalDateTime(timeData.getLocalDateTime().plus(cycle, unit));
        result.setDateTime(Date.from(result.getLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()));
        result.setTimeStr(format.format(result.getLocalDateTime()));
        result.setTimeLong(result.getDateTime().getTime());
        return result;
    }

    /** 当前时间串，格式“yyyy-MM-dd HH:mm:ss” */
    public static String currentTimeStr() {
        return currentTimeStr(null);
    }

    /** 当前时间串 */
    public static String currentTimeStr(String fmt) {
        DateTimeFormatter formatter;
        if (fmt == null) {
            formatter = TimeData.DEFAULT_FORMAT;
        } else {
            formatter = DateTimeFormatter.ofPattern(fmt);
        }
        return formatter.format(LocalDateTime.now());
    }

    /** 格式化时间，返回格式“yyyy-MM-dd HH:mm:ss” */
    public static String formatTime(long timeLong) {
        return formatTime(timeLong, null);
    }

    /** 格式化时间串,fmt空使用默认格式“yyyy-MM-dd HH:mm:ss” */
    public static String formatTime(Long timeLong, String fmt) {
        DateTimeFormatter formatter;
        if (fmt == null) {
            formatter = TimeData.DEFAULT_FORMAT;
        } else {
            formatter = DateTimeFormatter.ofPattern(fmt);
        }
        LocalDateTime locaDateTime = Instant.ofEpochMilli(timeLong).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return formatter.format(locaDateTime);
    }

    /** 格式化时间，返回格式“yyyy-MM-dd HH:mm:ss” */
    public static String formatTime(Date dateTime) {
        return formatTime(dateTime, null);
    }

    /** 格式化时间串 */
    public static String formatTime(Date dateTime, String fmt) {
        return formatTime(dateTime.getTime(), fmt);
    }

    /**
     * 转换时间格式
     * 
     * @param timeStr    时间串
     * @param fmtStrFrom 原始格式
     * @param fmtStrTo   目标格式
     * @return
     */
    public static String transTimeFmt(String timeStr, String fmtStrFrom, String fmtStrTo) {
        if (timeStr == fmtStrFrom) {
            return timeStr;
        }
        DateTimeFormatter formatterFrom, formatterTo;
        if (fmtStrFrom == null) {
            formatterFrom = TimeData.DEFAULT_FORMAT;
        } else {
            formatterFrom = DateTimeFormatter.ofPattern(fmtStrFrom);
        }
        if (fmtStrTo == null) {
            formatterTo = TimeData.DEFAULT_FORMAT;
        } else {
            formatterTo = DateTimeFormatter.ofPattern(fmtStrTo);
        }
        if (formatterFrom == formatterTo) {
            return timeStr;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(timeStr, formatterFrom);
        return formatterTo.format(localDateTime);
    }

    /**
     * 批量转换时间格式
     * 
     * @param timeStrList 要转换的时间串列表
     * @param fmtStrFrom  要转换的时间串格式
     * @param fmtStrTo    转换后格式
     * @return
     */
    public static List<String> transTimeListFmt(List<String> timeStrList, String fmtStrFrom, String fmtStrTo) {
        if (fmtStrFrom == fmtStrTo) {
            return timeStrList;
        }
        List<String> resultList = new ArrayList<>();
        for (String timeStr : timeStrList) {
            resultList.add(transTimeFmt(timeStr, fmtStrFrom, fmtStrTo));
        }
        return resultList;
    }

    /** 时间串转整型 */
    public static Long transTimeToLong(String timeStr, String fmtStr) {
        DateTimeFormatter formatterFrom;
        if (fmtStr == null) {
            formatterFrom = TimeData.DEFAULT_FORMAT;
        } else {
            formatterFrom = DateTimeFormatter.ofPattern(fmtStr);
        }
        LocalDateTime localDateTime = LocalDateTime.parse(timeStr, formatterFrom);
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /** 时间串转整型 */
    public static List<Long> transTimeListToLongs(List<String> timeStrList, String fmtStr) {
        List<Long> timeLongList = new ArrayList<>();
        for (String timeStr : timeStrList) {
            timeLongList.add(transTimeToLong(timeStr, fmtStr));
        }
        return timeLongList;
    }

    /**
     * 合并时间串数组，升序排序
     * @param fmtStrFrom 要合并串的格式，空使用默认格式“yyyy-MM-dd HH:mm:ss”
     * @param fmtStrTo   合并后的格式，空使用默认格式“yyyy-MM-dd HH:mm:ss”
     * @param timeLists  要合并的时间串列表，可以多个
     * @return
     */
    @SafeVarargs
    public static List<String> combineTime(String fmtStrFrom, String fmtStrTo, List<String>... timeLists) {
        return combineTime(fmtStrFrom, fmtStrTo,false, timeLists);
    }
 
    /**
     * 合并时间串数组
     * @param fmtStrFrom 要合并串的格式，空使用默认格式“yyyy-MM-dd HH:mm:ss”
     * @param fmtStrTo   合并后的格式，空使用默认格式“yyyy-MM-dd HH:mm:ss”
     * @param descFlag   true降序 
     * @param timeLists  要合并的时间串列表，可以多个
     * @return
     */
    @SafeVarargs
    public static List<String> combineTime(String fmtStrFrom, String fmtStrTo,boolean descFlag, List<String>... timeLists) {
        int len = timeLists.length;
        if (len == 0) {
            return new ArrayList<>();
        }
        int total=0,listLen;
        //转换后日期列表数组
        List<String> strList;
        //日期转换为毫秒列表数组
        List<Long> longList;
        //日期排序Map
        Map<Long,String> sortTimeMap;
        //降序
        if(descFlag){
            sortTimeMap=new TreeMap<>(new Comparator<Long>(){
				@Override
				public int compare(Long o1, Long o2) {
					return o2.compareTo(o1);
				}
            });
        }else{
            sortTimeMap=new TreeMap<>();
        }
        for (int i = 0; i < len; i++) {
            strList = transTimeListFmt(timeLists[i], fmtStrFrom, fmtStrTo);
            longList=transTimeListToLongs(strList, fmtStrTo);
            listLen=strList.size();
            total+=listLen;
            for(int j=0;j<listLen;j++){
                sortTimeMap.put(longList.get(j),strList.get(j));
            }
        }
        List<String> combineList = new ArrayList<>(total);
        sortTimeMap.forEach((time,timeStr)->{
           combineList.add(timeStr);
        });
        return combineList;
    }

    public static void main(String[] args) {
    }
}