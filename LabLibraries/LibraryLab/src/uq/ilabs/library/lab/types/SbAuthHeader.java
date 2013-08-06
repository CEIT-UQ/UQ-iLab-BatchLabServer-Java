/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.lab.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author uqlpayne
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sbAuthHeader", propOrder = {
    "couponID",
    "couponPassKey"
})
public class SbAuthHeader {

    public static final String STR_CouponId = "couponID";
    public static final String STR_CouponPasskey = "couponPassKey";
    //
    @XmlElement(name = "couponID")
    protected long couponId;
    @XmlElement(name = "couponPassKey")
    protected String couponPasskey;

    public long getCouponId() {
        return couponId;
    }

    public void setCouponId(long couponId) {
        this.couponId = couponId;
    }

    public String getCouponPasskey() {
        return couponPasskey;
    }

    public void setCouponPasskey(String couponPasskey) {
        this.couponPasskey = couponPasskey;
    }
}
