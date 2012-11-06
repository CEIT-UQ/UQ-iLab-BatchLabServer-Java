/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labclient.engine;

/**
 *
 * @author uqlpayne
 */
public class LabConsts {

    /*
     * Initialisation parameters
     */
    public static final String STRPRM_LogFilesPath = "LogFilesPath";
    public static final String STRPRM_LogLevel = "LogLevel";
    public static final String STRPRM_XmlConfigPropertiesPath = "XmlConfigPropertiesPath";
    /*
     * Http request parameters are case sensitive (make them lowercase)
     */
    public static final String STRREQ_CouponId = "CouponId";
    public static final String STRREQ_Coupon_Id = "Coupon_Id";
    public static final String STRREQ_Passkey = "Passkey";
    public static final String STRREQ_ServiceUrl = "ServiceUrl";
    public static final String STRREQ_LabServerId = "LabServerId";
    public static final String STRREQ_MultiSubmit = "MultiSubmit";
    /*
     * Session variables
     */
    public static final String STRSSN_LabClient = "LabClient";
    public static final String STRSSN_SubmittedID = "SubmittedID";
    public static final String STRSSN_CompletedID = "CompletedID";
    public static final String STRSSN_SubmittedIDs = "SubmittedIDs";
    public static final String STRSSN_CompletedIDs = "CompletedIDs";
    /*
     * XML elements in the lab configuration string
     */
    public static final String STRXML_LabConfiguration = "labConfiguration";
    public static final String STRXML_ATTR_Title = "title";
    public static final String STRXML_ATTR_Version = "version";
    public static final String STRXML_NavmenuPhoto = "navmenuPhoto";
    public static final String STRXML_Image = "image";
    public static final String STRXML_LabCamera = "labCamera";
    public static final String STRXML_Url = "url";
    public static final String STRXML_LabInfo = "labInfo";
    public static final String STRXML_Text = "text";
    public static final String STRXML_Configuration = "configuration";
    public static final String STRXML_Setup = "setup";
    public static final String STRXML_ATTR_Id = "id";
    public static final String STRXML_Name = "name";
    public static final String STRXML_Description = "description";
    /*
     * XML elements in the experiment specification string
     */
    public static final String STRXML_ExperimentSpecification = "experimentSpecification";
    public static final String STRXML_SetupId = "setupId";
    public static final String STRXML_Validation = "validation";
    /*
     * XML elements in the experiment results string
     */
    public static final String STRXML_ExperimentResult = "experimentResult";
    public static final String STRXML_Timestamp = "timestamp";
    public static final String STRXML_Title = "title";
    public static final String STRXML_Version = "version";
    public static final String STRXML_ExperimentId = "experimentId";
    public static final String STRXML_UnitId = "unitId";
    public static final String STRXML_SetupName = "setupName";
    /*
     * Result string download response
     */
    public static final String STRRSP_ContentTypeCsv = "Application/x-msexcel";
    public static final String STRRSP_Disposition = "content-disposition";
    public static final String STRRSP_AttachmentCsv_arg = "attachment; filename=\"%s.csv\"";
    /*
     * Webpage URLs
     */
    public static final String STRURL_Faces = "/faces/";
    public static final String STRURL_Home = "Home.xhtml";
    public static final String STRURL_Setup = "Setup.xhtml";
    public static final String STRURL_Results = "Results.xhtml";
    public static final String STRURL_Expired = "Expired.html";
    /*
     * Webpage style classes
     */
    public static final String STRSTL_InfoMessage = "infomessage";
    public static final String STRSTL_WarningMessage = "warningmessage";
    public static final String STRSTL_ErrorMessage = "errormessage";
    /*
     * String constants
     */
    public static final String STR_CsvSplitter = ",";
}
