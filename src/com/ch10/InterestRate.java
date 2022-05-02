package com.ch10;

import java.util.Date;

/**
 * Chapter 10: Application to the Financial Markets
 *
 * InterestRate: Hold the prime interest rate for the specified
 * date.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class InterestRate implements Comparable<InterestRate> {
    private Date effectiveDate;
    private double rate;

    public InterestRate(final Date effectiveDate, final double rate) {
        this.effectiveDate = effectiveDate;
        this.rate = rate;
    }

    public int compareTo(final InterestRate other) {
        return getEffectiveDate().compareTo(other.getEffectiveDate());
    }

    /**
     * @return the effectiveDate
     */
    public Date getEffectiveDate() {
        return this.effectiveDate;
    }

    /**
     * @return the rate
     */
    public double getRate() {
        return this.rate;
    }

    /**
     * @param effectiveDate
     *            the effectiveDate to set
     */
    public void setEffectiveDate(final Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    /**
     * @param rate
     *            the rate to set
     */
    public void setRate(final double rate) {
        this.rate = rate;
    }

}
