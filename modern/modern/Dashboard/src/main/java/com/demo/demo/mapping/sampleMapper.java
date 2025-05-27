package com.demo.demo.mapping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.demo.dto.sample;
import com.demo.demo.model.Truck;
import com.demo.demo.service.getdata;

import main.java.com.demo.demo.model.BatteryAlarm;

@Component
public class sampleMapper {
    @Autowired
    getdata getdata;

    String outputFormat = "dd/MM HH:mm";

    public sample truckmapper(Truck t1) {

        String formattedTime = formatTime(t1.getTime());

        return new sample().builder().truck(t1.getTruck()).time(formattedTime)
                .bat(t1.getBat()).ig(t1.getIg()).nb(t1.getNb()).last_loc(t1.getLast_loc()).build();
    }

    private String formatTime(String time) {
        try {
            // 解析输入格式
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = inputFormat.parse(time); // 解析日期字符串

            // 格式化为所需输出格式
            SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat);
            return outputFormatter.format(date); // 返回格式化后的字符串
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // 或者返回一个默认值
        }
    }

    public BatteryAlarm alarmMapper(List<Truck> s1) {

        int counter = 0; // 使用 int 而不是 String 來計數

        for (int x = 0; x < s1.size(); x++) { // 修正循環條件
            String batValue = s1.get(x).getBat(); // 獲取字符串

            // 確保字符串格式正確
            if (batValue.length() > 1) {
                // 提取數字部分
                String numberPart = batValue.substring(1); // 去掉第一個字母
                try {
                    int batteryLevel = Integer.parseInt(numberPart); // 轉換為整數

                    // 根據數字進行判斷
                    if (batteryLevel < 50) {
                        counter++; // 如果低於50，則計數器加1
                    }
                } catch (NumberFormatException e) {
                    System.out.println("無法轉換字符串為整數: " + numberPart);
                }
            }
        }

        // 確保 BatteryAlarm 有正確的建造者模式
        return new BatteryAlarm().builder().alarm(counter).build();
    }

}
