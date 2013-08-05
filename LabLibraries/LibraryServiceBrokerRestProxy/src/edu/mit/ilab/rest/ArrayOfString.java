/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mit.ilab.rest;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author uqlpayne
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfString", propOrder = {
    "string"
})
public class ArrayOfString {

    @XmlElement(nillable = true)
    protected List<String> string;

    /**
     * Gets the value of the string property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the string property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStringList().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link String }
     *
     *
     */
    public List<String> getStringList() {
        if (string == null) {
            string = new ArrayList<>();
        }
        return this.string;
    }
}
