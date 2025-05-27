package com.example.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Languagelist {
    private List<String> languageList = new ArrayList<>(Arrays.asList("عربي","Français","Deutsch","हिन्दी","Indonesia","Malaysia","Nederlands","Русский","แบบไทย","Tiếng Việt","English"));

    public List<String> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(List<String> languageList) {
        this.languageList = languageList;
    }


}
