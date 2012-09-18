/*
 * (Name, Guid, OutPasskey, InPasskey, ServiceUrl, Permitted)
 */
SELECT ServiceBrokers_Add ('localhost',  '196495303F294B13856D7E48872E51CC', 'FD3CF16CC855484FB06801379F475837', 'ls2sbPasskey', 'http://localhost:8080/DummyServiceBroker/ServiceBrokerService', TRUE);

/*
 * (Username, FirstName, LastName, ContactEmail, UserGroup, Password)  Default password is ilab
 */
SELECT Users_Add ('manager', 'LabServer', 'Manager', 'ilab-admin@itee.uq.edu.au', 'Manager', '3759F4FF14D8494DF3B58671FF9251A9D0C41D54');
