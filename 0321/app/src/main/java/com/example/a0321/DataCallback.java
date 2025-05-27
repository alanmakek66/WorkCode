package com.example.a0321;

import java.util.List;

public interface DataCallback {
    void onDataFetched(List<FqaModel> fqaModels);

    void onDataFetched2(List<TranslateList> translateLists);
}
