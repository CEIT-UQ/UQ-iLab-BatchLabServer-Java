/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mit.ilab.rest;

/**
 *
 * @author uqlpayne
 */
public class SbAuthHeader {

    public static final String STR_CouponId = "couponID";
    public static final String STR_CouponPasskey = "couponPassKey";
    protected long couponID;
    protected String couponPassKey;

    public long getCouponID() {
        return couponID;
    }

    public void setCouponID(long couponID) {
        this.couponID = couponID;
    }

    public String getCouponPassKey() {
        return couponPassKey;
    }

    public void setCouponPassKey(String couponPassKey) {
        this.couponPassKey = couponPassKey;
    }

    public SbAuthHeader() {
    }
}
