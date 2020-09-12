package com.gulfam.newsapp;

import org.json.JSONException;
import org.json.JSONObject;

public class NewsDataModel {

    private String mTitel;
    private String mAuthor;
    private String mTime;
    private String mOtherNews1,mOtherNews2;



    public String getOtherNews2() {
        return mOtherNews2;
    }

    public static NewsDataModel fromJSON(JSONObject jsonObject){
        try{
            NewsDataModel newsData = new NewsDataModel();
            newsData.mAuthor = jsonObject.getJSONArray("articles").getJSONObject(0).getString("author");
            newsData.mTime = jsonObject.getJSONArray("articles").getJSONObject(0).getString("publishedAt");
            newsData.mTitel = jsonObject.getJSONArray("articles").getJSONObject(0).getString("title");

            newsData.mOtherNews1 = jsonObject.getJSONArray("articles").getJSONObject(1).getString("title");
            newsData.mOtherNews2 = jsonObject.getJSONArray("articles").getJSONObject(2).getString("title");
            return newsData;

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }

    }

    public String getTitel() {
        return mTitel;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getTime() {
        return mTime;
    }

    public String getOtherNews1() {
        return mOtherNews1;
    }
}
