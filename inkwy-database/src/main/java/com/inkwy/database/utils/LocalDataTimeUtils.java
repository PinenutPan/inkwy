package com.inkwy.database.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDataTimeUtils {

    public static LocalDateTime getCurrentDateTime(){
        LocalDateTime localDateTime = LocalDateTime.now();

        return localDateTime;
    }

    public static void main(String[] args) {
        System.out.println(LocalDate.now());
    }

}
