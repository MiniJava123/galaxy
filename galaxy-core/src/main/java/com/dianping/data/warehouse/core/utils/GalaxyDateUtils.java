package com.dianping.data.warehouse.core.utils;

import com.dianping.data.warehouse.core.common.DateConst;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hongdi.tang on 14-3-5.
 */
public class GalaxyDateUtils {

    private static Logger logger = LoggerFactory.getLogger(GalaxyDateUtils.class);

    public static Date getDate(String str) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String clearnStr = str.replaceAll("[^0-9]", "");
        Date d = df.parse(clearnStr);
        return d;
    }

    public static String getFormatDateString(String str, int cal_field, int offset, SimpleDateFormat sdf)
            throws ParseException {
        java.util.Date d = getDate(str);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(cal_field, offset);
        return sdf.format(c.getTime());
    }

    public static String getFormatDate(Date date, int cal_field, int offset, SimpleDateFormat sdf)
            throws ParseException {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(cal_field, offset);
        return sdf.format(c.getTime());
    }

    public static Date getOffsetDate(Date date, int cal_field, int offset)
            throws ParseException {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(cal_field, offset);
        return c.getTime();
    }

    public static String genCalVariable(String dateType, String dateOffset) {
        StringBuilder builder = new StringBuilder();
        String type = StringUtils.substring(dateOffset, 0, 1);
        Integer number = Integer.valueOf(dateOffset.replaceAll("[^0-9]", ""));
        String head = "${";
        String tail = "}";
        builder.append(head).append(DateConst.COMMON_VARS_CAL_PREFIX).append("YYYYMMDD_P");
        if (dateType.equals("MTD")) {
            return head.concat(DateConst.COMMON_VARS_CAL_PREFIX.concat("YYYYMM_01")).concat(tail);
        } else {
            if (type.equalsIgnoreCase("D")) {
                if (number > 31) {
                    throw new NumberFormatException("number equals " + number + " is illegal when type is Day");
                }
                return builder.append(number).append("D").append(tail).toString();
            } else if (type.equalsIgnoreCase("M")) {
                if (number > 3) {
                    throw new NumberFormatException("number equals " + number + " is illegal when type is Month");
                }
                return builder.append(number).append("M").append(tail).toString();
            } else {
                throw new NumberFormatException("type equals " + type + " is illegal");
            }
        }
    }

    public static String transferVariable(String key, Long timeMills, String calOffset) {
        Date date = new Date(timeMills);
        String type = StringUtils.substring(calOffset, 0, 1);
        Integer num = Integer.parseInt(StringUtils.substring(calOffset, 1));
        Date offsetDate = null;
        try {
            if (type.equalsIgnoreCase("D")) {
                offsetDate = getOffsetDate(date, Calendar.DATE, -1 * num);
            } else if (type.equalsIgnoreCase("M")) {
                offsetDate = getOffsetDate(date, Calendar.DATE, -1 * num);
            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }

        String value = "";

        if ("YYYYMMDD_DEFAULT_HP_DT".equalsIgnoreCase(key)) {
            return DateConst.DEFAULT_HP_DT;
        } else if ("YYYYMM_01".equalsIgnoreCase(key)) {
            SimpleDateFormat format = DateConst.DD_DF;
            if (format.format(date).equals("01")) {
                date = DateUtils.addDays(date, -1);
            }
            format = DateConst.MONTH_DF;
            return format.format(date) + "-01";
        } else if ("YYYYMM8_01".equalsIgnoreCase(key)) {
            SimpleDateFormat format = DateConst.DD_DF;
            if (format.format(date).equals("01")) {
                date = DateUtils.addDays(date, -1);
            }
            format = DateConst.MONTH_DF8;
            return format.format(date) + "01";
        } else if ("CUR_YYYYMM_01".equalsIgnoreCase(key)) {
            SimpleDateFormat format = DateConst.DD_DF;
            if (!format.format(date).equals("01")) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.MONTH, 1);
                date = cal.getTime();
            }
            format = DateConst.MONTH_DF;
            return format.format(date) + "-01";
        } else if ("CUR_YYYYMM8_01".equalsIgnoreCase(key)) {
            SimpleDateFormat format = DateConst.DD_DF;
            if (!format.format(date).equals("01")) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.MONTH, 1);
                date = cal.getTime();
            }
            format = DateConst.MONTH_DF8;
            return format.format(date) + "01";
        } else if ("YYYYMM_LD".equalsIgnoreCase(key)) {
            SimpleDateFormat format = DateConst.DD_DF;
            if (format.format(date).equals("01")) {
                date = DateUtils.addDays(date, -1);
            }
            Date d = DateUtils.addDays(DateUtils.setDays(DateUtils.addMonths(date, 1), 1), -1);
            format = DateConst.DAY_DF;
            return format.format(d);
        } else if ("YYYYMM8_LD".equalsIgnoreCase(key)) {
            SimpleDateFormat format = DateConst.DD_DF;
            if (format.format(date).equals("01")) {
                date = DateUtils.addDays(date, -1);
            }
            Date d = DateUtils.addDays(DateUtils.setDays(DateUtils.addMonths(date, 1), 1), -1);
            format = DateConst.DAY_DF8;
            return format.format(d);
        }


        try {
            String[] v = key.split("_");
            int sign = 1;
            int offset = 0;
            SimpleDateFormat format = DateConst.LONG_DF;
            int field = Calendar.DATE;

            if (v.length > 0) {
                String frmt = v[0];
                if ("YYYYMMDD".equalsIgnoreCase(frmt))
                    format = DateConst.DAY_DF;
                else if ("YYYYMM".equalsIgnoreCase(frmt))
                    format = DateConst.MONTH_DF;
                else if ("YYYYMMDD8".equalsIgnoreCase(frmt))
                    format = DateConst.DAY_DF8;
                else if ("YYYY".equalsIgnoreCase(frmt))
                    format = DateConst.YEAR_DF;
                else if ("MM".equalsIgnoreCase(frmt))
                    format = DateConst.MM_DF;
                else if ("DD".equalsIgnoreCase(frmt))
                    format = DateConst.DD_DF;
            }

            if (v.length > 1) {
                String s = v[1];

                if (s.toUpperCase().endsWith("TODAY")) {
                    sign = 1;
                    offset = 0;
                    return GalaxyDateUtils.getFormatDateString(
                            DateConst.DAY_DF.format(new Date()), field, offset
                                    * sign, format
                    );
                } else if (s.toUpperCase().endsWith("YESTERDAY")) {
                    sign = 1;
                    offset = -1;
                    return GalaxyDateUtils.getFormatDateString(
                            DateConst.DAY_DF.format(new Date()), field, offset
                                    * sign, format
                    );
                } else {
                    sign = s.startsWith("P") ? -1 : 1;
                    offset = Integer.valueOf(s.replaceAll("[^0-9]", ""));
                    if (s.toUpperCase().endsWith("DOWIM"))
                        field = Calendar.DAY_OF_WEEK_IN_MONTH;
                    else if (s.toUpperCase().endsWith("DOW"))
                        field = Calendar.DAY_OF_WEEK;
                    else if (s.toUpperCase().endsWith("DOM"))
                        field = Calendar.DAY_OF_MONTH;
                    else if (s.toUpperCase().endsWith("DOY"))
                        field = Calendar.DAY_OF_YEAR;
                    else if (s.toUpperCase().endsWith("D"))
                        field = Calendar.DATE;
                    else if (s.toUpperCase().endsWith("M"))
                        field = Calendar.MONTH;
                    else if (s.toUpperCase().endsWith("Y"))
                        field = Calendar.YEAR;
                    else if (s.toUpperCase().endsWith("WOY"))
                        field = Calendar.WEEK_OF_YEAR;
                }

            }
            value = GalaxyDateUtils.getFormatDate(offsetDate, field, offset * sign, format);
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
        }
        return value;
    }

    public static Date strToDate(String strDate, String strFormat) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(strFormat);
        Date date = format.parse(strDate);
        return date;
    }

    public static String dateToStr(Date date, String strFormat) {
        SimpleDateFormat format = new SimpleDateFormat(strFormat);
        return format.format(date);
    }

    public static String getCurrentTimeStampStr() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTimeStamp = simpleDateFormat.format(new Date());
        return nowTimeStamp;
    }

}
