package com.example.dlapd.seoulcarmap;

import com.google.firebase.database.Exclude;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class Upload {
    private String mName;
    private String mImageUrl;
    private String mPhoneNumb;
    private String mParkingAddress;
    private Double mLon;
    private Double mLat;
    private String mParkingSize;
    private String mParkingCond;
    private String mParkingTimeStart;
    private String mParkingTimeFinish;
    public String muserId;
    private String mKey;
    private String mprice;
    private String mfcmtoken;

    public Upload() {

        getParkingAddress();

        getParkingAddress();
        getName();
        getParkingTimeStart();
        getParkingTimeFinish();
        //empty constructor needed
    }

    public Upload(String fcmToken, String name, String phonenumb, String parkingaddress, Double lat, Double lon, String parkingsize, String parkingcond,
                  String parkingtimestart, String parkingtimefinish, String price, String userId, String imageUrl ) {
        if (name.trim().equals("")) {
            name = "상호명 없음";
            //description = "No Description";
        }
        if (phonenumb.trim().equals("")){
            phonenumb = "";
        }
        if (parkingsize.trim().equals("")){
            parkingsize = "";
        }
        if (parkingcond.trim().equals("")){
            parkingcond = "";
        }
        if (parkingtimestart.trim().equals("")){
            parkingtimestart = "00";
        }
        if (parkingtimefinish.trim().equals("")){
            parkingtimefinish = "00";
        }

        mName = name;
        mImageUrl = imageUrl;
        mPhoneNumb = phonenumb;
        mParkingAddress = parkingaddress;
        mParkingSize = parkingsize;
        mParkingCond = parkingcond;
        mParkingTimeStart = parkingtimestart;
        mParkingTimeFinish = parkingtimefinish;
        mLat = lat;
        mLon = lon;
        mprice = price;
        muserId = userId;
        mfcmtoken = fcmToken;

        /*mDescription = description;
        mLon = lon;
        mLat = lat;
        muserId = userId;*/
    }

    public String getName() { return mName; }
    public void setName(String name) { mName = name; }

    public String getImageUrl() { return mImageUrl; }
    public void setImageUrl(String imageUrl) { mImageUrl = imageUrl; }

    public String getPhoneNumb(){ return mPhoneNumb;}
    public void setPhoneNumb(String phonenumb) {mPhoneNumb = phonenumb;}

    public String getParkingAddress() {return mParkingAddress;}
    public void setParkingAddress(String parkingaddress) {mParkingAddress = parkingaddress;}

    public String getParkingSize() {return mParkingSize;}
    public void setParkingSize(String parkingsize) {mParkingSize = parkingsize;}

    public String getParkingCond() {return mParkingCond;}
    public void setParkingCond(String parkingcond) {mParkingCond = parkingcond;}

    public String getParkingTimeStart() {return mParkingTimeStart;}
    public void setParkingTimeStart(String parkingtimestart) {mParkingTimeStart = parkingtimestart;}

    public String getParkingTimeFinish() {return mParkingTimeFinish;}
    public void setParkingTimeFinish(String parkingtimefinish) {mParkingTimeFinish = parkingtimefinish;}

    public Double getLon() {return mLon;}
    public void setLon(Double lon) {mLon = lon;}

    public Double getLat() {return mLat;}
    public void setLat(Double lat) {mLat = lat;}

    public String getuserId() {return muserId;}
    public void setuserId(String userId) {muserId = userId;}

    public String getprice() { return mprice; }
    public void setprice(String price) { mprice = price; }

    public String getUserToken() { return mfcmtoken; }
    public void setUserToken(String fcmToken) { mfcmtoken = fcmToken; }

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }

}