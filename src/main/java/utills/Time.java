/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utills;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author POS
 */
public class Time {

    public static String getTime() {

        return new SimpleDateFormat("HH:mm:ss").format(new Date());

    }

    public static String getDate() {
        return new SimpleDateFormat("dd.MM.yyyy HH.mm").format(new Date());

    }

    //В отображение добавляются миллисекунды
    public static String getTimeM() {
        return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());

    }

    public static String formatSecondsToTime(long sec) {

        long min = sec / 60;
        long hour = sec / 3600;
        sec = sec % 60;
        min = min % 60;

        String secS;
        String minS;
        String hourS;

        secS = (sec <= 9) ? "0" + String.valueOf(sec) : String.valueOf(sec);
        minS = (min <= 9) ? "0" + String.valueOf(min) : String.valueOf(min);
        hourS = (hour <= 9) ? "0" + String.valueOf(hour) : String.valueOf(hour);

        return (hourS + ":" + minS + ":" + secS);
    }

    public static String formatMillisecondsToTime(long time) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(time),
                TimeUnit.MILLISECONDS.toMinutes(time)- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }

  

}
