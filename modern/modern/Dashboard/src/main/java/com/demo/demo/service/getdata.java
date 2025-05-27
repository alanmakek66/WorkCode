package com.demo.demo.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.demo.demo.model.Truck;

import main.java.com.demo.demo.model.Test1;

import org.thymeleaf.context.Context;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class getdata {

    @Autowired
    private RestTemplate restTemplate;

    private static final String URL2 = "http://192.168.10.30:8005/data2";

    private static final String URL3 = "http://192.168.10.30:8005/data3";

    private static final int RETRY_INTERVAL = 20000; // 20 seconds

    public List<Truck> data2() {
        while (true) { // Infinite loop for retries
            try {
                Truck[] t1 = restTemplate.getForObject(URL2, Truck[].class);
                return Arrays.asList(t1);
            } catch (Exception e) {
                System.err.println("Failed to retrieve data: " + e.getMessage());

                try {
                    Thread.sleep(RETRY_INTERVAL);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    throw new RuntimeException("Thread was interrupted while waiting to retry.", ie);
                }
            }
        }
    }

    public Test1 gettest1() {
        while (true) {
            try {
                Test1 t1 = restTemplate.getForObject(URL3, Test1.class);
                return t1;
            } catch (Exception e) {
                System.err.println("Failed to retrieve data: " + e.getMessage());
                try {
                    Thread.sleep(RETRY_INTERVAL);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    throw new RuntimeException("Thread was interrupted while waiting to retry.", ie);
                }
            }

        }
    }

}
