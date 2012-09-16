/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labclient.engine.types;

/**
 *
 * @author uqlpayne
 */
public class SetupInfo {

    private String id;
    private String name;
    private String description;
    private String xmlSetup;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getXmlSetup() {
        return xmlSetup;
    }

    public void setXmlSetup(String xmlSetup) {
        this.xmlSetup = xmlSetup;
    }

    public SetupInfo(String id) {
        this.id = id;
    }
}
