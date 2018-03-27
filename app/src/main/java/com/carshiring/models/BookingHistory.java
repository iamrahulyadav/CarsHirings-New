package com.carshiring.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class BookingHistory implements Parcelable {

            /**
             * Booking_id : DT1521023886494
             * bokking_date : 2018-03-14
             * booking_company_name : S S TRAVELS
             * booking_actual_price : 726.00
             * booking_supplier_price : 660.00
             * booking_currency : SAR
             * booking_bysupplier : S S TRAVELS
             * booking_supllier_log : https://static.carhire-solutions.com/images/supplier/logo/logo338.png
             * booking_car_model : Maruti Esteem
             * booking_car_category : 4
             * booking_car_image : https://static.carhire-solutions.com/images/car/SSTravel/large/CDMR.jpg
             * booking_from_location : Delhi Indira Gandhi Airport (DEL)
             * booking_from_date : 2018-03-16 10:15:00
             * booking_to_location : Delhi Indira Gandhi Airport (DEL)
             * booking_status : 2
             */

            private String Booking_id;
            private String bokking_date;
            private String booking_company_name;
            private String booking_actual_price;
            private String booking_supplier_price;
            private String booking_currency;
            private String booking_bysupplier;
            private String booking_supllier_log;
            private String booking_car_model;
            private String booking_car_category;
            private String booking_car_image;
            private String booking_from_location;
            private String booking_from_date;
            private String booking_to_location;
            private String booking_status;

    protected BookingHistory(Parcel in) {
        Booking_id = in.readString();
        bokking_date = in.readString();
        booking_company_name = in.readString();
        booking_actual_price = in.readString();
        booking_supplier_price = in.readString();
        booking_currency = in.readString();
        booking_bysupplier = in.readString();
        booking_supllier_log = in.readString();
        booking_car_model = in.readString();
        booking_car_category = in.readString();
        booking_car_image = in.readString();
        booking_from_location = in.readString();
        booking_from_date = in.readString();
        booking_to_location = in.readString();
        booking_status = in.readString();
    }

    public static final Creator<BookingHistory> CREATOR = new Creator<BookingHistory>() {
        @Override
        public BookingHistory createFromParcel(Parcel in) {
            return new BookingHistory(in);
        }

        @Override
        public BookingHistory[] newArray(int size) {
            return new BookingHistory[size];
        }
    };

    public String getBooking_id() {
                return Booking_id;
            }

            public void setBooking_id(String Booking_id) {
                this.Booking_id = Booking_id;
            }

            public String getBokking_date() {
                return bokking_date;
            }

            public void setBokking_date(String bokking_date) {
                this.bokking_date = bokking_date;
            }

            public String getBooking_company_name() {
                return booking_company_name;
            }

            public void setBooking_company_name(String booking_company_name) {
                this.booking_company_name = booking_company_name;
            }

            public String getBooking_actual_price() {
                return booking_actual_price;
            }

            public void setBooking_actual_price(String booking_actual_price) {
                this.booking_actual_price = booking_actual_price;
            }

            public String getBooking_supplier_price() {
                return booking_supplier_price;
            }

            public void setBooking_supplier_price(String booking_supplier_price) {
                this.booking_supplier_price = booking_supplier_price;
            }

            public String getBooking_currency() {
                return booking_currency;
            }

            public void setBooking_currency(String booking_currency) {
                this.booking_currency = booking_currency;
            }

            public String getBooking_bysupplier() {
                return booking_bysupplier;
            }

            public void setBooking_bysupplier(String booking_bysupplier) {
                this.booking_bysupplier = booking_bysupplier;
            }

            public String getBooking_supllier_log() {
                return booking_supllier_log;
            }

            public void setBooking_supllier_log(String booking_supllier_log) {
                this.booking_supllier_log = booking_supllier_log;
            }

            public String getBooking_car_model() {
                return booking_car_model;
            }

            public void setBooking_car_model(String booking_car_model) {
                this.booking_car_model = booking_car_model;
            }

            public String getBooking_car_category() {
                return booking_car_category;
            }

            public void setBooking_car_category(String booking_car_category) {
                this.booking_car_category = booking_car_category;
            }

            public String getBooking_car_image() {
                return booking_car_image;
            }

            public void setBooking_car_image(String booking_car_image) {
                this.booking_car_image = booking_car_image;
            }

            public String getBooking_from_location() {
                return booking_from_location;
            }

            public void setBooking_from_location(String booking_from_location) {
                this.booking_from_location = booking_from_location;
            }

            public String getBooking_from_date() {
                return booking_from_date;
            }

            public void setBooking_from_date(String booking_from_date) {
                this.booking_from_date = booking_from_date;
            }

            public String getBooking_to_location() {
                return booking_to_location;
            }

            public void setBooking_to_location(String booking_to_location) {
                this.booking_to_location = booking_to_location;
            }

            public String getBooking_status() {
                return booking_status;
            }

            public void setBooking_status(String booking_status) {
                this.booking_status = booking_status;
            }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Booking_id);
        dest.writeString(bokking_date);
        dest.writeString(booking_company_name);
        dest.writeString(booking_actual_price);
        dest.writeString(booking_supplier_price);
        dest.writeString(booking_currency);
        dest.writeString(booking_bysupplier);
        dest.writeString(booking_supllier_log);
        dest.writeString(booking_car_model);
        dest.writeString(booking_car_category);
        dest.writeString(booking_car_image);
        dest.writeString(booking_from_location);
        dest.writeString(booking_from_date);
        dest.writeString(booking_to_location);
        dest.writeString(booking_status);
    }
}
