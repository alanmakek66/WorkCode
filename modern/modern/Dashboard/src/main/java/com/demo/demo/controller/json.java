package com.demo.demo.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.spring6.SpringTemplateEngine;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.demo.demo.dto.sample;
import com.demo.demo.dto.twoo;
import com.demo.demo.mapping.sampleMapper;
import com.demo.demo.model.Truck;
import com.demo.demo.service.getdata;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

import main.java.com.demo.demo.model.BatteryAlarm;
import main.java.com.demo.demo.model.Test1;

@RestController
@EnableScheduling
public class json {
        @Autowired
        getdata getdata;
        @Autowired
        RestTemplate r1;
        @Autowired
        private SpringTemplateEngine templateEngine;
        @Autowired
        sampleMapper sss;

        @CrossOrigin(origins = "*")

        @GetMapping("/truck")
        public List<sample> truck() {

                sample s1s = getdata.data2().stream().filter(s -> "WH2192".equals(s.getTruck()))
                                .sorted((o1, o2) -> Long.compare(Long.parseLong(o2.getTime()),
                                                Long.parseLong(o1.getTime())))
                                .map(s1 -> sss.truckmapper(s1)).findFirst().orElse(null);
                sample s2s = getdata.data2().stream().filter(s -> "HY8389".equals(s.getTruck()))
                                .sorted((o1, o2) -> Long.compare(Long.parseLong(o2.getTime()),
                                                Long.parseLong(o1.getTime())))
                                .map(s1 -> sss.truckmapper(s1)).findFirst().orElse(null);

                sample s3s = getdata.data2().stream().filter(s -> "HY1204".equals(s.getTruck()))
                                .sorted((o1, o2) -> Long.compare(Long.parseLong(o2.getTime()),
                                                Long.parseLong(o1.getTime())))
                                .map(s1 -> sss.truckmapper(s1)).findFirst().orElse(null);
                sample s4s = getdata.data2().stream().filter(s -> "XH7655".equals(s.getTruck()))
                                .sorted((o1, o2) -> Long.compare(Long.parseLong(o2.getTime()),
                                                Long.parseLong(o1.getTime())))
                                .map(s1 -> sss.truckmapper(s1)).findFirst().orElse(null);
                sample s5s = getdata.data2().stream().filter(s -> "SN3959".equals(s.getTruck()))
                                .sorted((o1, o2) -> Long.compare(Long.parseLong(o2.getTime()),
                                                Long.parseLong(o1.getTime())))
                                .map(s1 -> sss.truckmapper(s1)).findFirst().orElse(null);
                sample s6s = getdata.data2().stream().filter(s -> "GL5118".equals(s.getTruck()))
                                .sorted((o1, o2) -> Long.compare(Long.parseLong(o2.getTime()),
                                                Long.parseLong(o1.getTime())))
                                .map(s1 -> sss.truckmapper(s1)).findFirst().orElse(null);
                sample s7s = getdata.data2().stream().filter(s -> "YD2534".equals(s.getTruck()))
                                .sorted((o1, o2) -> Long.compare(Long.parseLong(o2.getTime()),
                                                Long.parseLong(o1.getTime())))
                                .map(s1 -> sss.truckmapper(s1)).findFirst().orElse(null);
                sample s8s = getdata.data2().stream().filter(s -> "LE985".equals(s.getTruck()))
                                .sorted((o1, o2) -> Long.compare(Long.parseLong(o2.getTime()),
                                                Long.parseLong(o1.getTime())))
                                .map(s1 -> sss.truckmapper(s1)).findFirst().orElse(null);
                sample s9s = getdata.data2().stream().filter(s -> "TR8093".equals(s.getTruck()))
                                .sorted((o1, o2) -> Long.compare(Long.parseLong(o2.getTime()),
                                                Long.parseLong(o1.getTime())))
                                .map(s1 -> sss.truckmapper(s1)).findFirst().orElse(null);
                sample s10s = getdata.data2().stream().filter(s -> "YD9681".equals(s.getTruck()))
                                .sorted((o1, o2) -> Long.compare(Long.parseLong(o2.getTime()),
                                                Long.parseLong(o1.getTime())))
                                .map(s1 -> sss.truckmapper(s1)).findFirst().orElse(null);
                sample s11s = getdata.data2().stream().filter(s -> "UR5278".equals(s.getTruck()))
                                .sorted((o1, o2) -> Long.compare(Long.parseLong(o2.getTime()),
                                                Long.parseLong(o1.getTime())))
                                .map(s1 -> sss.truckmapper(s1)).findFirst().orElse(null);
                sample s12s = getdata.data2().stream().filter(s -> "KR5053".equals(s.getTruck()))
                                .sorted((o1, o2) -> Long.compare(Long.parseLong(o2.getTime()),
                                                Long.parseLong(o1.getTime())))
                                .map(s1 -> sss.truckmapper(s1)).findFirst().orElse(null);
                sample s13s = getdata.data2().stream().filter(s -> "KR2547".equals(s.getTruck()))
                                .sorted((o1, o2) -> Long.compare(Long.parseLong(o2.getTime()),
                                                Long.parseLong(o1.getTime())))
                                .map(s1 -> sss.truckmapper(s1)).findFirst().orElse(null);

                List<sample> s123s = new ArrayList<>();
                s123s.add(s1s);
                s123s.add(s2s);
                s123s.add(s3s);
                s123s.add(s4s);
                s123s.add(s5s);
                s123s.add(s6s);
                s123s.add(s7s);
                s123s.add(s8s);
                s123s.add(s9s);
                s123s.add(s10s);
                s123s.add(s11s);
                s123s.add(s12s);
                s123s.add(s13s);

                List<sample> data = s123s.stream()
                                .filter(item -> item != null)
                                .collect(Collectors.toList());

                return data;

        }

        @GetMapping("/BatteryAlarm")
        public BatteryAlarm batteryAlarm() {
                List<Truck> t1 = getdata.data2();
                BatteryAlarm b1 = sss.alarmMapper(t1);
                return b1;
        }

        @GetMapping("/TwoTruckBattry")
        public twoo two() {

                Integer kr5053_power = getdata.data2().stream()
                                .filter(s -> "KR5053".equals(s.getTruck()))
                                .findFirst()
                                .map(s -> Integer.parseInt(s.getBat().substring(1))) // 使用 map 來處理可能的 null 值
                                .orElseThrow(() -> new RuntimeException("KR5053 not found"));

                // 獲取 XH7655 的電池電量
                Integer xh7655_power = getdata.data2().stream()
                                .filter(s -> "XH7655".equals(s.getTruck()))
                                .findFirst()
                                .map(s -> Integer.parseInt(s.getBat().substring(1))) // 使用 map 來處理可能的 null 值
                                .orElseThrow(() -> new RuntimeException("XH7655 not found"));

                return new twoo().builder().KR5053(kr5053_power).XH7655(xh7655_power).build();

        }

        @CrossOrigin(origins = "*")
        @GetMapping("/Test1")
        public Test1 test1() {
                return getdata.gettest1();
        }

}
