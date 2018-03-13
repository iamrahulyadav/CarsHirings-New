package com.carshiring.models;

/**
 * Created by Rakhi.
 *
 */

public class QuotesModel {
    String BookingrefNumber,Rate,address;
   public QuotesModel()
    {

    }
public QuotesModel(String BookingrefNumber, String Rate, String address)
{
    this.BookingrefNumber=BookingrefNumber;
    this.Rate=Rate;
    this.address=address;
}
    public String getBookingrefNumber() {
        return BookingrefNumber;
    }

    public void setBookingrefNumber(String bookingrefNumber) {
        BookingrefNumber = bookingrefNumber;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
