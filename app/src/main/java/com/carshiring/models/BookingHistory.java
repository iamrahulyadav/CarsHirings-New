package com.carshiring.models;

import java.io.Serializable;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class BookingHistory implements Serializable {



            /**
             * Booking_id :
             * bokking_date : 2018-04-09
             * booking_company_name : InterRent Germany
             * booking_actual_price : 612.12
             * booking_total_price : 612.12
             * booking_supplier_price : 612.12
             * booking_fullprotection_value : 0.00
             * booking_currency : SAR
             * booking_bysupplier : InterRent Germany
             * booking_supllier_log : https://static.carhire-solutions.com/images/supplier/logo/logo247.png
             * booking_car_model : VW Up
             * booking_car_category : 1
             * booking_car_image : https://static.carhire-solutions.com/images/car/GlobalRentaCar/large/MBMN.jpg
             * booking_from_location : Duesseldorf International Airport (DUS)
             * booking_from_date : 2018-04-11 17:43:00
             * booking_to_location : Duesseldorf International Airport (DUS)
             * booking_to_date : 2018-04-13 17:43:00
             * booking_coupon_code :
             * booking_coupon_value : 0.00
             * booking_pont_value : 0.00
             * booking_wallet_value : 0.00
             * booking_payfort_value : 612.12
             * booking_status : 0
             * booking_canceldetail : {"booking_cancel_booking_id":"","booking_cancel_booking_amount":"","booking_cancel_cancel_charge":"","booking_cancel_refundable_amount":"","booking_cancel_credit_amount":"","booking_cancel_wallet_amount":"","booking_cancel_point_amount":""}
             */

            private String Booking_id;
            private String bokking_date;
            private String booking_company_name;
            private String booking_actual_price;
            private String booking_total_price;
            private String booking_supplier_price;
            private String booking_fullprotection_value;
            private String booking_currency;
            private String booking_bysupplier;
            private String booking_supllier_log;
            private String booking_car_model;
            private String booking_car_category;
            private String booking_car_image;
            private String booking_from_location;
            private String booking_from_date;
            private String booking_to_location;
            private String booking_to_date;
            private String booking_coupon_code;
            private String booking_coupon_value;
            private String booking_pont_value;
            private String booking_wallet_value;
            private String booking_payfort_value;
            private String booking_status;
            private BookingCanceldetailBean booking_canceldetail;

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

            public String getBooking_total_price() {
                return booking_total_price;
            }

            public void setBooking_total_price(String booking_total_price) {
                this.booking_total_price = booking_total_price;
            }

            public String getBooking_supplier_price() {
                return booking_supplier_price;
            }

            public void setBooking_supplier_price(String booking_supplier_price) {
                this.booking_supplier_price = booking_supplier_price;
            }

            public String getBooking_fullprotection_value() {
                return booking_fullprotection_value;
            }

            public void setBooking_fullprotection_value(String booking_fullprotection_value) {
                this.booking_fullprotection_value = booking_fullprotection_value;
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

            public String getBooking_to_date() {
                return booking_to_date;
            }

            public void setBooking_to_date(String booking_to_date) {
                this.booking_to_date = booking_to_date;
            }

            public String getBooking_coupon_code() {
                return booking_coupon_code;
            }

            public void setBooking_coupon_code(String booking_coupon_code) {
                this.booking_coupon_code = booking_coupon_code;
            }

            public String getBooking_coupon_value() {
                return booking_coupon_value;
            }

            public void setBooking_coupon_value(String booking_coupon_value) {
                this.booking_coupon_value = booking_coupon_value;
            }

            public String getBooking_pont_value() {
                return booking_pont_value;
            }

            public void setBooking_pont_value(String booking_pont_value) {
                this.booking_pont_value = booking_pont_value;
            }

            public String getBooking_wallet_value() {
                return booking_wallet_value;
            }

            public void setBooking_wallet_value(String booking_wallet_value) {
                this.booking_wallet_value = booking_wallet_value;
            }

            public String getBooking_payfort_value() {
                return booking_payfort_value;
            }

            public void setBooking_payfort_value(String booking_payfort_value) {
                this.booking_payfort_value = booking_payfort_value;
            }

            public String getBooking_status() {
                return booking_status;
            }

            public void setBooking_status(String booking_status) {
                this.booking_status = booking_status;
            }

            public BookingCanceldetailBean getBooking_canceldetail() {
                return booking_canceldetail;
            }

            public void setBooking_canceldetail(BookingCanceldetailBean booking_canceldetail) {
                this.booking_canceldetail = booking_canceldetail;
            }

            public static class BookingCanceldetailBean {
                /**
                 * booking_cancel_booking_id :
                 * booking_cancel_booking_amount :
                 * booking_cancel_cancel_charge :
                 * booking_cancel_refundable_amount :
                 * booking_cancel_credit_amount :
                 * booking_cancel_wallet_amount :
                 * booking_cancel_point_amount :
                 */

                private String booking_cancel_booking_id;
                private String booking_cancel_booking_amount;
                private String booking_cancel_cancel_charge;
                private String booking_cancel_refundable_amount;
                private String booking_cancel_credit_amount;
                private String booking_cancel_wallet_amount;
                private String booking_cancel_point_amount;

                public String getBooking_cancel_booking_id() {
                    return booking_cancel_booking_id;
                }

                public void setBooking_cancel_booking_id(String booking_cancel_booking_id) {
                    this.booking_cancel_booking_id = booking_cancel_booking_id;
                }

                public String getBooking_cancel_booking_amount() {
                    return booking_cancel_booking_amount;
                }

                public void setBooking_cancel_booking_amount(String booking_cancel_booking_amount) {
                    this.booking_cancel_booking_amount = booking_cancel_booking_amount;
                }

                public String getBooking_cancel_cancel_charge() {
                    return booking_cancel_cancel_charge;
                }

                public void setBooking_cancel_cancel_charge(String booking_cancel_cancel_charge) {
                    this.booking_cancel_cancel_charge = booking_cancel_cancel_charge;
                }

                public String getBooking_cancel_refundable_amount() {
                    return booking_cancel_refundable_amount;
                }

                public void setBooking_cancel_refundable_amount(String booking_cancel_refundable_amount) {
                    this.booking_cancel_refundable_amount = booking_cancel_refundable_amount;
                }

                public String getBooking_cancel_credit_amount() {
                    return booking_cancel_credit_amount;
                }

                public void setBooking_cancel_credit_amount(String booking_cancel_credit_amount) {
                    this.booking_cancel_credit_amount = booking_cancel_credit_amount;
                }

                public String getBooking_cancel_wallet_amount() {
                    return booking_cancel_wallet_amount;
                }

                public void setBooking_cancel_wallet_amount(String booking_cancel_wallet_amount) {
                    this.booking_cancel_wallet_amount = booking_cancel_wallet_amount;
                }

                public String getBooking_cancel_point_amount() {
                    return booking_cancel_point_amount;
                }

                public void setBooking_cancel_point_amount(String booking_cancel_point_amount) {
                    this.booking_cancel_point_amount = booking_cancel_point_amount;
                }
            }

}
