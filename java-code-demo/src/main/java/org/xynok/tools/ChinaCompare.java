package org.xynok.tools;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * 中文比较
 * @author xynok
 * @since 2020-12-09
 */
public class ChinaCompare implements Comparator<Object>{
    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        Collator collator = Collator.getInstance(Locale.CHINA);
        return collator.compare(o1.toString(), o2.toString());
    }
}