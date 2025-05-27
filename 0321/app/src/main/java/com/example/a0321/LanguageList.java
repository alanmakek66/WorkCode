package com.example.a0321;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LanguageList {
    private List<String> languageList = new ArrayList<>(Arrays.asList("阿拉伯語","法語","德語","印度語","印尼語","馬來西亞語","荷蘭語","俄語","泰語","越南語","英語"));

    public List<String> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(List<String> languageList) {
        this.languageList = languageList;
    }


}