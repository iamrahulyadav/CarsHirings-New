package com.carshiring.models;

/**
 * Created by lenovo on 3/15/2018.
 */

public class BookingRequest {
    String name="",sarname="",number="",email="",address="",city="",zipcode="",countrycode="",car_id="",
            type="",rtype="",fullprotection="",flight_no="",extradata="",dob="",user_id="",pick_date="",
            drop_date="", pick_city="",drop_city="",protection_val="",booking_point="",booking_wallet="",
            booking_payfort="";

    public BookingRequest(String name, String sarname, String number, String email,
                          String address, String city, String zipcode, String countrycode,
                          String car_id, String type, String rtype, String fullprotection,
                          String flight_no, String extradata, String dob, String user_id,
                          String pick_date, String drop_date, String pick_city, String drop_city,
                          String protection_val, String booking_point, String booking_wallet,
                          String booking_payfort) {
        this.name = name;
        this.sarname = sarname;
        this.number = number;
        this.email = email;
        this.address = address;
        this.city = city;
        this.zipcode = zipcode;
        this.countrycode = countrycode;
        this.car_id = car_id;
        this.type = type;
        this.rtype = rtype;
        this.fullprotection = fullprotection;
        this.flight_no = flight_no;
        this.extradata = extradata;
        this.dob = dob;
        this.user_id = user_id;
        this.pick_date = pick_date;
        this.drop_date = drop_date;
        this.pick_city = pick_city;
        this.drop_city = drop_city;
        this.protection_val = protection_val;
        this.booking_point = booking_point;
        this.booking_wallet = booking_wallet;
        this.booking_payfort = booking_payfort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSarname() {
        return sarname;
    }

    public void setSarname(String sarname) {
        this.sarname = sarname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRtype() {
        return rtype;
    }

    public void setRtype(String rtype) {
        this.rtype = rtype;
    }

    public String getFullprotection() {
        return fullprotection;
    }

    public void setFullprotection(String fullprotection) {
        this.fullprotection = fullprotection;
    }

    public String getFlight_no() {
        return flight_no;
    }

    public void setFlight_no(String flight_no) {
        this.flight_no = flight_no;
    }

    public String getExtradata() {
        return extradata;
    }

    public void setExtradata(String extradata) {
        this.extradata = extradata;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPick_date() {
        return pick_date;
    }

    public void setPick_date(String pick_date) {
        this.pick_date = pick_date;
    }

    public String getDrop_date() {
        return drop_date;
    }

    public void setDrop_date(String drop_date) {
        this.drop_date = drop_date;
    }

    public String getPick_city() {
        return pick_city;
    }

    public void setPick_city(String pick_city) {
        this.pick_city = pick_city;
    }

    public String getDrop_city() {
        return drop_city;
    }

    public void setDrop_city(String drop_city) {
        this.drop_city = drop_city;
    }

    public String getProtection_val() {
        return protection_val;
    }

    public void setProtection_val(String protection_val) {
        this.protection_val = protection_val;
    }

    public String getBooking_point() {
        return booking_point;
    }

    public void setBooking_point(String booking_point) {
        this.booking_point = booking_point;
    }

    public String getBooking_wallet() {
        return booking_wallet;
    }

    public void setBooking_wallet(String booking_wallet) {
        this.booking_wallet = booking_wallet;
    }

    public String getBooking_payfort() {
        return booking_payfort;
    }

    public void setBooking_payfort(String booking_payfort) {
        this.booking_payfort = booking_payfort;
    }
}
