package org.xynok.tools;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import lombok.Data;

/**
 * 日期时间相关数据结构
 * 
 * @author xynok
 */
@Data
public class TimeData {
    /** 时间串 */
    private String timeStr;
    /** 毫秒日期 */
    private Long timeLong;
    /** 日期 */
    private Date dateTime;
    /** Local格式日期 */
    private LocalDateTime localDateTime;
    /** 原时间串格式 */
    private String fmtFrom;
    /** 要转换的时间串格式 */
    private String fmtTo;
    /**默认时间格式串 */
    public final static String DEFAULT_FORMAT_STR="yyyy-MM-dd HH:mm:ss";
    /**默认时间格式 */
    public final static DateTimeFormatter DEFAULT_FORMAT=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**包构造，通过build来创建实例保证必要的数据 */
    TimeData(){}
    /**
     * 通过时间串和时间格式构建TimeData
     * 会自动将时间串转换为LocalDataTime格式
     */
    public static TimeData build(String timeStr,String fmtFrom,String fmtTo){
        TimeData timeData=new TimeData();
        DateTimeFormatter format;
        if(fmtFrom==null){
            fmtFrom="yyyy-MM-dd HH:mm:ss";
            format=DEFAULT_FORMAT;
        }else{
            format=DateTimeFormatter.ofPattern(fmtFrom);
            timeData.setFmtFrom(fmtFrom);
        }
        if(fmtTo!=null){
            timeData.setFmtTo(fmtTo);
        }
        // 如果时间串为空，则设定为当前时间
        if(timeStr==null){
            timeData.setLocalDateTime(LocalDateTime.now());
            timeData.setTimeStr(format.format(timeData.getLocalDateTime()));
        }else{
            timeData.setTimeStr(timeStr);
            timeData.setLocalDateTime(LocalDateTime.parse(timeStr, format));
        }
        return timeData;
    }

    /**
     * 通过毫秒日期构建TimeData
     * 会自动将时间串转换为LocalDataTime格式
     */
    public static TimeData build(Long timeLong,String fmtTo){
        // 如果毫秒日期为空，则设定为当前时间
        if(timeLong==null){
            timeLong=System.currentTimeMillis();
        }
        TimeData timeData=new TimeData();
        timeData.setTimeLong(timeLong);
        timeData.setLocalDateTime(Instant.ofEpochMilli(timeLong).
                   atZone(ZoneId.systemDefault()).toLocalDateTime());
        if(fmtTo!=null){
            timeData.setFmtTo(fmtTo);
        }           
        return timeData;
    }

    /**
     * 通过日期构建TimeData
     * 会自动将时间串转换为LocalDataTime格式
     */
    public static TimeData build(Date dateTime,String fmtTo){
        TimeData timeData=new TimeData();
        // 如果日期为空，则设定为当前时间
        if(dateTime==null){
            dateTime=new Date();
        }
        timeData.setDateTime(dateTime);
        timeData.setLocalDateTime(Instant.ofEpochMilli(dateTime.getTime()).
                   atZone(ZoneId.systemDefault()).toLocalDateTime());
        if(fmtTo!=null){
            timeData.setFmtTo(fmtTo);
        }           
        return timeData;
    }

    /**
     * 通过LocalDataTime构建TimeData
     * 会自动将时间串转换为LocalDataTime格式
     */
    public static TimeData build(LocalDateTime localDateTime,String fmtTo){
        TimeData timeData=new TimeData();
        // 如果日期为空，则设定为当前时间
        if(localDateTime==null){
            localDateTime=LocalDateTime.now();
        }
        timeData.setLocalDateTime(localDateTime);
        if(fmtTo!=null){
            timeData.setFmtTo(fmtTo);
        }           
        return timeData;
    }
}