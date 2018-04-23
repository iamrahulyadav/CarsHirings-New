package com.carshiring.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 3/15/2018.
 */

public class BookingRequest {
    private String name="",sarname="",number="",email="",address="",city="",zipcode="",countrycode="",car_id="",
            type="",rtype="",fullprotection="",flight_no="",dob="",user_id="",pick_date="",
            drop_date="", pick_city="",drop_city="",protection_val="",booking_point="",booking_wallet="",
            booking_payfort="", transaction_id="",discountCoupon="",discountvalue="",merchant_reference="",
            booking_one_way_fee="", driver_charge="";
    List<ExtraAdded> extraData = new ArrayList<>();

    public String getBooking_one_way_fee() {
        return booking_one_way_fee;
    }

    public void setBooking_one_way_fee(String booking_one_way_fee) {
        this.booking_one_way_fee = booking_one_way_fee;
    }

    public String getDriver_charge() {
        return driver_charge;
    }

    public void setDriver_charge(String driver_charge) {
        this.driver_charge = driver_charge;
    }

    public String getMerchant_reference() {
        return merchant_reference;
    }

    public void setMerchant_reference(String merchant_reference) {
        this.merchant_reference = merchant_reference;
    }

    public String getDiscountCoupon() {
        return discountCoupon;
    }

    public void setDiscountCoupon(String discountCoupon) {
        this.discountCoupon = discountCoupon;
    }

    public String getDiscountvalue() {
        return discountvalue;
    }

    public void setDiscountvalue(String discountvalue) {
        this.discountvalue = discountvalue;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public List<ExtraAdded> getExtraData() {
        return extraData;
    }

    public void setExtraData(List<ExtraAdded> extraData) {
        this.extraData = extraData;
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
