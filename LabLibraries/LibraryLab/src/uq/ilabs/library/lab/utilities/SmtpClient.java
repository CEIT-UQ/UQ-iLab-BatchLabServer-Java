/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.lab.utilities;

import java.util.*;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author uqlpayne
 */
public class SmtpClient {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = SmtpClient.class.getName();
    /*
     * String constants
     */
    private static final String STR_LocalhostIPAddress = "127.0.0.1";
    private static final String STR_MailHost = "mail.smtp.host";
    private static final String STR_ContentType = "text/plain";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Session session;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String from;
    private String subject;
    private String body;
    private ArrayList<String> to;
    private ArrayList<String> cc;
    private ArrayList<String> bcc;

    public void setBody(String body) {
        this.body = body;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public List<String> getCc() {
        return cc;
    }

    public List<String> getTo() {
        return to;
    }
    //</editor-fold>

    /**
     * Create a SMTP mail client using 'localhost' as the mail host
     */
    public SmtpClient() {
        this(STR_LocalhostIPAddress);
    }

    /**
     * Create a SMTP mail client using the specified mail host
     *
     * @param host - IP address of mail host to use
     */
    public SmtpClient(String host) {
        final String methodName = "SmtpClient";
        Logfile.WriteCalled(STR_ClassName, methodName);

        /*
         * Initialise email address lists
         */
        this.to = new ArrayList<>();
        this.cc = new ArrayList<>();
        this.bcc = new ArrayList<>();

        /*
         * Create an instance of the mail session
         */
        Properties properties = new Properties();
        properties.put(STR_MailHost, host);
        this.session = Session.getInstance(properties);

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }

    /**
     *
     * @return boolean
     */
    public boolean Send() {
        final String methodName = "Send";
        Logfile.WriteCalled(STR_ClassName, methodName);

        /*
         * Assume this will fail
         */
        boolean success = false;

        try {
            /*
             * Create the message
             */
            Message message = new MimeMessage(this.session);

            /*
             * Set the list of 'to' email addresses
             */
            ArrayList<InternetAddress> addresses = new ArrayList<>();
            Iterator iterator = this.to.iterator();
            while (iterator.hasNext()) {
                addresses.add(new InternetAddress((String) iterator.next()));
            }
            InternetAddress[] addressArray = addresses.toArray(new InternetAddress[0]);
            message.setRecipients(Message.RecipientType.TO, addressArray);

            /*
             * Set the list of 'cc' email addresses
             */
            addresses = new ArrayList<>();
            iterator = this.cc.iterator();
            while (iterator.hasNext()) {
                addresses.add(new InternetAddress((String) iterator.next()));
            }
            if (addresses.isEmpty() == false) {
                addressArray = addresses.toArray(new InternetAddress[0]);
                message.setRecipients(Message.RecipientType.CC, addressArray);
            }

            /*
             * Set the list of 'cc' email addresses
             */
            addresses = new ArrayList<>();
            iterator = this.bcc.iterator();
            while (iterator.hasNext()) {
                addresses.add(new InternetAddress((String) iterator.next()));
            }
            if (addresses.isEmpty() == false) {
                addressArray = addresses.toArray(new InternetAddress[0]);
                message.setRecipients(Message.RecipientType.BCC, addressArray);
            }

            /*
             * Set the remainder of the message information
             */
            message.setFrom(new InternetAddress(this.from));
            message.setSubject(this.subject);
            message.setSentDate(new Date());
            message.setContent(this.body, STR_ContentType);

            /*
             * Send the message
             */
            Transport.send(message);

            success = true;
        } catch (MessagingException mex) {
            Logfile.WriteError(mex.toString());
            Exception ex = mex.getNextException();
            if (ex != null) {
                Logfile.WriteError(ex.toString());
            }
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        return success;
    }
}
