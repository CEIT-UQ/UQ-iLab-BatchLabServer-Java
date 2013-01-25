Dummy_ServiceBroker
===================

The Dummy ServiceBroker enables the development of the iLab Batch Labserver with its LabClient without all of the
complexities of having to log into a proper iLab ServiceBroker.

This ServiceBroker simply provides pass-through methods to allow the LabClient to communicate with the LabServer.
Only one method is not entirely pass-through and that is the 'Submit()' method where an experiment Id needs to be
generated.

It may be necessary for the ServiceBroker to communicate with more than one LabServer during development. An example
of this is when one LabServer is developed with the Java jax-ws framework and the other LabServer is developed with
the Microsoft DotNet framework.

The PostgreSQL database platform is used to generate experiment Ids as they are required and to provide a mapping from
the experiment Id to the LabServer's Id (guid). So, when the ServiceBroker sees a request for a particular experiment
Id, the request is passed on to the appropriate Labserver. The 'PostgreSQL' folder contains the database scripts which
create the table and the stored procedures (functions).

Configuration information for the Dummy ServiceBroker is contained in two files in the WEB-INF folder. The first file is
'web.xml' which contains the path and filename for the logging information. This is OS-specific and will need to be
changed whether running on Windows, Linux, or Mac-OS. The 'LogfilesPath' path is currently set for use on a Windows
platform. The 'web.xml' file also contains the path and filename for the second file which contains the configuration
properties. The paths for the logfile and configuration file are placed in the 'web.xml' file because the servlet
context is needed to determine the real and absolute path to these files.

The 'ConfigProperties.xml' contains the configuration information for the Dummy ServiceBroker. This information includes
the ServiceBroker's guid, database connection information and LabServer information.

The LabServer information needed by the ServiceBroker includes the LabServer's Id (guid), web service url and outgoing
passkey. This could have been placed in the database but is easier to use and/or change when placed in the xml file. The
information for each LabServer is stored as a comma-seperated-value string. Multiple LabServers can be specified by
appending consecutive numbers to the end of the 'LabServer' key starting with zero. Check the source code to see how
this works.

The 'CouponId' and 'CouponPasskey' are arbitrary strings that are used during development. In a proper ServiceBroker,
these values are determined for each launch of the LabClient.

SOAP Headers
------------

This was the most difficult part of the ServiceBroker to develop. The MIT iLab Shared Architecture uses SOAP headers to
pass security information to the web services. The LabClient passes the 'CouponId' and 'CouponPasskey' in the SOAP
header to the ServiceBroker where the information is processed to determine the authenticity of the LabClient making
the request. The Dummy ServiceBroker passes its guid and outgoing passkey to the LabServer where the information is
processed to determine the authenticity of the ServiceBroker making the request.

SOAP header processing is carried out in the message handler that is attached to the web service for incoming requests
or the web client for outgoing requests. Since each request is independent of any other request, the information in the
SOAP header has to be passed between the message handler and the application by means of the message context. The
ServiceBroker may make two consecutive requests to two different LabServers meaning the information in the SOAP header
will be different.

LabClient-to-ServiceBroker: This message handler which is attached to the ServiceBroker's web service extracts the
'CouponId' and 'CouponPasskey' from the SOAP header, places them in a new 'sbAuthHeader' object and passes this object
the web service for processing. This handler can be found in the file 'LabClientAuthenticator.java' in the package
'uq.ilabs.servicebroker.service'.

ServiceBroker-to-LabServer: This message handler which is attached to the ServiceBroker's LabServer web client receives
an 'AuthHeader' object through the message context from the ServiceBroker's LabServer API. The 'Identifier' which is the
ServiceBroker's guid and the outgoing 'PassKey' are inserted into the SOAP header before the request is passed to the
LabServer's web service.

If the web service receiving the request cannot authenticate the request, it throws a 'ProtocolException' to deny
access. This can be picked up by the sender and the appropriate action taken.

ServiceBroker EJB
-----------------

The ServiceBroker uses an Enterprise Bean to do the work of the web service. The web service simply processes the SOAP
header information that is received through the message context before passing the request on to the bean to do the
work.

Initialisation
--------------

The first point of contact with the web service is its message handler. When a LabClient sends a request to the
ServiceBroker, the message handler processes the request before the ServiceBroker's web service sees it. Now, the OS may
have just rebooted and the ServiceBroker is deployed but not yet running. This means that initialisation of the
ServiceBroker has to be carried out in the message handler. This is fine because a message context exists allowing
'init' parameters to be read from the 'web.xml' file. The configuration properties file can be read from its file and
and instance of the 'ConfigProperties' object created.

But how does the ServiceBroker's service bean get to see this information? The message handler places the newly created
object in a static variable in the ServiceBroker's web service and sets an 'initialised' boolean flag. The service bean
constructor gets the 'ConfigProperties' object from the static variable in the ServiceBroker's web service and carries
out all the necessary intialisation required by the ServiceBroker.

Why can't the ServiceBroker's bean get the 'init' parameters from the 'web.xml' file? A web service context does not
exist to enable that to happen. The context only exists during the web service request.

There was a problem when the ServiceBroker was being deployed. It seems that the EJB was being instantiated and the
beans' constructor called before the message handler was able to carry out any initialisation. The constructor checks
to see if the 'initialised' boolean flag has been set by the message handler before checking if it has carried out its
own initialisation.
