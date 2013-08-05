/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mit.ilab.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author uqlpayne
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "ExperimentStatus", propOrder = {
    "statusCode",
    "wait",
    "estRuntime",
    "estRemainingRuntime"
})
public class ExperimentStatus {

    protected int statusCode;
    protected WaitEstimate wait;
    protected double estRuntime;
    protected double estRemainingRuntime;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int value) {
        this.statusCode = value;
    }

    public WaitEstimate getWait() {
        return wait;
    }

    public void setWait(WaitEstimate value) {
        this.wait = value;
    }

    public double getEstRuntime() {
        return estRuntime;
    }

    public void setEstRuntime(double value) {
        this.estRuntime = value;
    }

    public double getEstRemainingRuntime() {
        return estRemainingRuntime;
    }

    public void setEstRemainingRuntime(double value) {
        this.estRemainingRuntime = value;
    }
}
