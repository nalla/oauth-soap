# oauth-soap
Proof of concept of creating a client / service application secured by jwt tokens in java.

## Authority
The authority needs to be configured for client and service inside the `config.properties` file.

### Client
The client properties file contains some more information thant the file from the service. `clientId`, `clientSecret` and `scope` elements are needed for token generation. The `url` is the endpoint the client will call. It defaults to the one that will be available if the service is being run via docker as it is seen inside the `run.ps1`. The client contains the authority in form of the tokens generation endpoint.

~~~properties
clientId=Example.Client
clientSecret=L8aCv1MuxAA1p1/6/LBdIbVCmRTv9IG3o8MwlUBbhy4=
scope=Example.Scope
authority=https://example.com/connect/token
url=http://localhost:8080/services/Message?wsdl
~~~

### Service
The service just contains the authority in form of the `jwk` endpoint.

~~~properties
authority=https://example.com/.well-known/openid-configuration/jwks
~~~

## Build
Both projects, client and service are `maven` projects and can easily be built by invoking the `package` command.

### Client
~~~ps1
$> cd oauth-soap-client
$> mvn clean package
~~~

### Service
~~~ps1
$> cd oauth-soap-service
$> mvn clean package
$> .\run.ps1
~~~
