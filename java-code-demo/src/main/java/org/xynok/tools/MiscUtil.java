package org.xynok.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.UUID;

/**
 * 杂项方法
 */
public final class MiscUtil {
    /**
     * 获取调用方法名，调用该函数得到调用函数的名字 调用者压栈 getMethodName压栈 getStackTrace压栈 因此栈底[2]是调用者
     * 
     * @return
     */
    public static String getMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    /** 唯一Id */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /** 唯一Id */
    public static String uuidWithSub() {
        return UUID.randomUUID().toString();
    }

    /** 应用实例Id */
    public static String uuIdWithPid() {
        // getName()返回的串构成：PID@hostname
        return String.format("%s@%s", ManagementFactory.getRuntimeMXBean().getName(), uuid());
    }

    /** 取当前进程Id */
    public static String getPid() {
        return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }

    /**Java对象序列化成字节数组*/
    public static byte[] serialize(Object object) throws IOException {
        // 字节输出流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 基于字节的对象输出流
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        byte[] bytes = null;
        oos.writeObject(object);
        bytes = bos.toByteArray();
        oos.close();
        bos.close();
        return bytes;
    }

    /**序列化的字节数组反序列化成对象 */
    public static <T> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        // 字节输入流
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        // 基于字节的对象输入流
        ObjectInputStream ois = new ObjectInputStream(bis);
        ois.close();
        bis.close();
        return (T)ois.readObject();
    }
    
    /**
     * 4位2进制转16进制ASCII字符
     * @param bt          字节
     * @param lowBitFlag  低4位标志
     * @return
     */
    public static char fourBitsToChar(byte bt,boolean lowBitFlag){
        int btTrans;
        if(lowBitFlag){
            btTrans=bt&0xF;
        }else{
            btTrans=(bt>>4)&0xF;
        }
        if(btTrans>=0xa && btTrans<=0xf){
            // 65是'A'的ASCII码，97是'a'的ASCII码
            return (char)(btTrans-0xa+'A');
        }else{
            // 48是'0'的ASCII码
            return (char)(btTrans+48);
        }
    }

    /**
     * 字节转16进制ASCII字符
     * @param bt     字节
     * @param buf    字符缓存数组
     * @param offset 缓存偏移量
     */
    public static void byteToHexChar(byte bt,char[] buf,int offset) {
        buf[offset]=fourBitsToChar(bt,false);
        buf[offset+1]=fourBitsToChar(bt,true);
    }

    /**
     * 字节数组转16进制ASCII字符数组
     * @param bytes
     * @return
     */
    public static char[] bytesToHexChars(byte[] bytes){
        int len=bytes.length;
        char[] chars=new char[len*2];
        for(int i=0;i<len;i++){
            byteToHexChar(bytes[i],chars,i*2);
        }
        return chars;
    }

    /**
     * 深克隆，类必须实现序列化接口
     * @throws ClassNotFoundException
     */
    public static <T> T deepClone(T src) throws IOException, ClassNotFoundException {
        byte[] bytes = serialize(src);
        return deserialize(bytes);
    }

    /** 浅克隆 */
    public static<T> T clone(T src){
       return (T)copy(src,src.getClass());
    }

    /**
     * 反射复制对象，目标类必须有无参构造函数，
     * 源和目标类无需相同，只要相同的字段名和字段类型（或源字段是目标字段的父类）就可复制
     * @param source  源
     * @param clz  目标类
     */
    public static<T> T copy(Object source, Class<T> clz) {
        Class<?> sourceClass = source.getClass();
        T target;
        try {
            target = clz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("目标类没有缺省构造函数:"+e);
        }
        Field[] srcFields = sourceClass.getDeclaredFields();
        Field[] targetFields = clz.getDeclaredFields();
        String srcFieldName,targetFieldName;
        Class<?> srcFieldType,targetFieldType;
        for (int i = 0; i < srcFields.length; i++) {
            for (int j = 0; j < targetFields.length; j++) {
                srcFieldName = srcFields[i].getName();
                targetFieldName = targetFields[j].getName();
                srcFieldType = srcFields[i].getType();
                targetFieldType = targetFields[j].getType();
                //名字相同，类型相同或目标字段类型继承自源字段类
                if (srcFieldName.equals(targetFieldName) && 
                     (srcFieldType.equals(targetFieldType)||srcFieldType.isAssignableFrom(targetFieldType))) {
                    try {
                        srcFields[i].setAccessible(true);
                        Object value = srcFields[i].get(source);
                        targetFields[j].setAccessible(true);
                        targetFields[j].set(target, value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
            }
        }
        return target;
    }

}