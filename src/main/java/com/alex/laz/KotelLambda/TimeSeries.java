package com.alex.laz.KotelLambda;

import com.opencsv.CSVReader;
import org.jfree.data.time.Second;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jfree.data.time.Second.parseSecond;

/**
 * Created by alex on 20-Dec-16.
 */
public class TimeSeries {

    /**
     * Create a series
     *
     * @ return the series
     */
    private org.jfree.data.time.TimeSeries createSeries(int mean) {
        String sName;
        switch (mean) {
            case 0: sName = "Дата"; break;
            case 1: sName = "Час"; break;
            case 2: sName = "ДімПодача"; break;
            case 3: sName = "Триходовий"; break;
            case 4: sName = "Приміщення"; break;
            case 5: sName = "КотелПодача"; break;
            case 6: sName = "КотелЗворотня"; break;
            case 7: sName = "ДімЗворотня"; break;
            case 8: sName = "Вулиця";      break;
            default: sName = " ";          break;
        }
        final org.jfree.data.time.TimeSeries seriesX = new org.jfree.data.time.TimeSeries(sName);
        try {
            reader = new CSVReader(new FileReader(PATH));
//          Set up series
            // timeDoublicate для уникнення дублів,регулярка вибирає кожні 5 хвилин, а є ще секунди
            String timeDoublicate = "";
            String sTime = "";
//*****************************************
            Predicate<String> s5Min = (n) -> n.substring(15,16).contains("5");
            Predicate<String> s30Sec = (n) -> n.substring(17,18).matches("[012]");

            Stream<String> lines = Files.lines(Paths.get(PATH));

            java.util.List<String> resultList = lines
                    .filter(s5Min.and(s30Sec))
                    .collect(Collectors.toList());
            resultList.forEach(value -> System.out.println(value+ " 22222 "));
//*****************************************


            while ((readNextLine = reader.readNext()) != null) {
                sTime = readNextLine[1];
                // add values to series НИЖЧЕ МОЖЕ БУТИ ЧАС, АБО РЕГУЛЯРКА
                // "\\d\\d:\\d5:\\d\\d" - кожні 5 хвилин
                if (Pattern.matches("\\d\\d:\\d5:\\d\\d", sTime) && !sTime.substring(0,6).equals(timeDoublicate)){
                    timeDoublicate = sTime.substring(0,6);
                    try {
// тут конструюю дату з моєї і додаю час, для нормального відображення
                        String sDate = readNextLine[0];
                        sDate = sDate.trim();
//                        String[] sDateSplit = sDate.split("\\.");
//                        String sNewFormatData = sDateSplit[2] + "-" + sDateSplit[1] + "-" + sDateSplit[0];
                        String dayStr = sDate.substring(0, Math.min(10, sDate.length()));
                        String sNewFormatData = dayStr.substring(6,10) + "-" + dayStr.substring(3,5) + "-" + dayStr.substring(0,2);
                        Second Time = parseSecond(sNewFormatData+ " " + sTime);
// тут конструюю дату з моєї і додаю час, для нормального відображення
// mean вказує на номер стовбчика у файлі, з якого беру дані
                        double X = Double.valueOf(readNextLine[mean]);
                        seriesX.add(Time, X);
                    } catch (NumberFormatException ex) {System.out.println("Exception "+ex);}
                }
            }
        }
        catch (FileNotFoundException e) {System.out.println("File not found!");}
        catch (IOException e) {e.printStackTrace();}
        return seriesX;
    }
}
