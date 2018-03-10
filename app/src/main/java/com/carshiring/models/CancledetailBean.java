package com.carshiring.models;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class CancledetailBean {

    /**
     * Coverage : {"@attributes":{"CoverageType":"Cancellation fee","Code":"CF"},"Details":[{"Coverage":{"@attributes":{"CoverageType":"2018-02-16T10:08:29_2018-02-26T10:15:00"}},"Charge":{"@attributes":{"CurrencyCode":"EUR","Amount":"0.00","Description":"Up to 48 hours before pick-up, cancellation free of charge. Within 48 hours prior to pick-up a fee equal to the total rental price with a maximum amount of USD 125 will be charged. In the event of a no-show 100% of the sales price will be charged.","IncludedInRate":"true"}}},{"Coverage":{"@attributes":{"CoverageType":"2018-02-26T10:15:00_2018-02-28T10:15:00"}},"Charge":{"@attributes":{"CurrencyCode":"EUR","Amount":"158.60","Description":"Up to 48 hours before pick-up, cancellation free of charge. Within 48 hours prior to pick-up a fee equal to the total rental price with a maximum amount of USD 125 will be charged. In the event of a no-show 100% of the sales price will be charged."},"Calculation":{"@attributes":{"UnitCharge":"198.3452","UnitName":"PreferedCurrencyPrice: CurrencyCode=USD"}}}}]}
     * Charge : {"@attributes":{"IncludedInRate":"true"}}
     */
    public CoverageBeanX Coverage;
    public ChargeBeanX Charge;

    public class CoverageBeanX {
    }

    // FIXME generate failure  field _$Attributes4
    public class ChargeBeanX {
        
    }
}
