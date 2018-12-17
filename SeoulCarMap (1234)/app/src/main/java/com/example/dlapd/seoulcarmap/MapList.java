package com.example.dlapd.seoulcarmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MapList{
    private String latitude,
            longitude,
            address,
            parkingname,
            tel,
            capacity,
            weekdayBegin,
            weekdayEnd,
            weekendBegin,
            weekendEnd,
            holidayBegin,
            holidayEnd,
            fulltimeMonth,
            rate,
            timeRate,
            addRate,
            addtimeRate,
            dayMaximum;

    public MapList(String latitude,
                   String longtitude,
                   String address,
                   String parkingname,
                   String tel,
                   String capacity,
                   String weekdayBegin,
                   String weekdayEnd,
                   String weekendBegin,
                   String weekendEnd,
                   String holidayBegin,
                   String holidayEnd,
                   String fulltimeMonth,
                   String rate,
                   String timeRate,
                   String addRate,
                   String addtimeRate,
                   String dayMaximum) {

        this.latitude = latitude;
        this.longitude = longtitude;
        this.address = address;
        this.parkingname = parkingname;
        this.tel = tel;
        this.capacity = capacity;
        this.weekdayBegin = weekdayBegin;
        this.weekdayEnd = weekdayEnd;
        this.weekendBegin = weekendBegin;
        this.weekendEnd = weekendEnd;
        this.holidayBegin = holidayBegin;
        this.holidayEnd = holidayEnd;
        this.fulltimeMonth = fulltimeMonth;
        this.rate = rate;
        this.timeRate = timeRate;
        this.addRate = addRate;
        this.addtimeRate = addtimeRate;
        this.dayMaximum = dayMaximum;
    }


    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setParkingname(String parkingname) {
        this.parkingname = parkingname;
    }

    public String getParkingname() {
        return parkingname;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getTel() {
        return tel;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setWeekDayBegin(String weekdayBegin) {
        this.weekdayBegin =weekdayBegin ;
    }

    public String getwWeekDayBegin() {
        return weekdayBegin;
    }

    public void setWeekDayEnd(String weekdayEnd) {
        this.weekdayEnd =weekdayEnd ;
    }

    public String getWeekDayEnd() {
        return weekdayEnd;
    }

    public void setWeekEndBegin(String weekendBegin) {
        this.weekendBegin = weekendBegin;
    }

    public String getWeekEndBegin() {
        return weekendBegin;
    }

    public void setWeekEndEnd(String weekendEnd) {
        this.weekendEnd = weekendEnd;
    }

    public String getWeekEndEnd() {
        return weekendEnd;
    }

    public void setHolidayBegin(String holidayBegin) {
        this.holidayBegin = holidayBegin;
    }

    public String getHolidayBegin() {
        return holidayBegin;
    }

    public void setHolidayEnd(String holidayEnd) {
        this. holidayEnd=holidayEnd ;
    }

    public String getHolidayEnd() {
        return holidayEnd;
    }

    public void setFulltimeMonth(String fulltimeMonth) {
        this.fulltimeMonth = fulltimeMonth;
    }

    public String getFulltimeMonth() {
        return fulltimeMonth;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getRate() {
        return rate;
    }

    public void setTimeRate(String timeRate) {
        this.timeRate = timeRate;
    }

    public String getTimeRate() {
        return timeRate;
    }

    public void setAddRate(String addRate) {
        this.addRate =addRate ;
    }

    public String getAddRate() {
        return addRate;
    }

    public void setAddtimeRate(String addtimeRate) {
        this.addtimeRate = addtimeRate;
    }

    public String getAddtimeRate() {
        return addtimeRate;
    }

    public void setDayMaximum(String dayMaximum) {
        this.dayMaximum =dayMaximum ;
    }

    public String getDayMaximum() {
        return dayMaximum;
    }

}