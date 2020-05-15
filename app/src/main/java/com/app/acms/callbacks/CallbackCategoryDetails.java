package com.app.acms.callbacks;

import com.app.acms.models.Category;
import com.app.acms.models.Video;

import java.util.ArrayList;
import java.util.List;

public class CallbackCategoryDetails {

    public String status = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public Category category = null;
    public List<Video> posts = new ArrayList<>();

}
