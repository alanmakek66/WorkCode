package com.demo.demo.viewcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.demo.demo.controller.json;
import com.demo.demo.dto.sample;
import com.demo.demo.model.Truck;

import main.java.com.demo.demo.model.BatteryAlarm;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@EnableScheduling
public class Deshboard {
    @Autowired
    private json j1;

    @GetMapping("/phone")
    public String ajax(Model model) {
        List<com.demo.demo.dto.sample> t1 = j1.truck();
        model.addAttribute("statusList", t1);
        return "Mobile.html";

    }

    @GetMapping("/dashboard")
    public String dash_ajax(Model model) {
        List<com.demo.demo.dto.sample> t1 = j1.truck();
        BatteryAlarm b1 = j1.batteryAlarm();
        model.addAttribute("statusList", t1);
        model.addAttribute("BatteryAlarm", b1);
        return "Dashboard.html";

    }

    @GetMapping("/dynamic")
    public String dynamic(Model model) {
        List<com.demo.demo.dto.sample> t1 = j1.truck();
        model.addAttribute("statusList", t1);
        return "Dynamic.html";

    }

    @CrossOrigin(origins = "*")

    @GetMapping("/LineChart2")
    public String linechart(Model model) {
        return "LineChart2.html";
    }

}
