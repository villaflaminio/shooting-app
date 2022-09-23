package org.rconfalonieri.nzuardi.shootingapp.config;
import org.rconfalonieri.nzuardi.shootingapp.exception.UtilsException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.rconfalonieri.nzuardi.shootingapp.exception.UtilsException.utilsExceptionCode.DATA_WRONG_FORMAT;


public class Utils {

    public static Date stringToDate(String dateString) {
        Date date;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
        } catch (ParseException e) {
            throw new UtilsException(DATA_WRONG_FORMAT);
        }
        return date;
    }

    public static String dateToStringFormatted(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        return formatter.format(date);
    }

}
