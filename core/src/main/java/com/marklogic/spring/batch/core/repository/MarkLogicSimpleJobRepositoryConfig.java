package com.marklogic.spring.batch.core.repository;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.restapi.RestApi;
import com.marklogic.mgmt.api.security.*;

import java.util.ArrayList;
import java.util.List;

public class MarkLogicSimpleJobRepositoryConfig {

    private API api;

    public ManageClient getManageClient() {
        return manageClient;
    }

    private ManageClient manageClient;

    public String getName() {
        return NAME;
    }

    private final String NAME = "spring-batch";
    private final String DATABASE_JSON = "{\"database-name\": \"spring-batch-content\",  \"maintain-last-modified\": true,  \"uri-lexicon\": true,  \"collection-lexicon\": true,   \"triple-index\": true,    \"range-element-index\": [{\"scalar-type\": \"dateTime\",\"namespace-uri\": \"http://marklogic.com/spring-batch\",\"localname\": \"createDateTime\",\"collation\": \"\",\"range-value-positions\": false,\"invalid-values\": \"reject\"}, {\"scalar-type\": \"unsignedLong\",\"namespace-uri\": \"http://marklogic.com/spring-batch\",\"localname\": \"id\",\"collation\": \"\",\"range-value-positions\": false,\"invalid-values\": \"reject\"  }, {\"scalar-type\": \"string\",\"namespace-uri\": \"http://marklogic.com/spring-batch\",\"localname\": \"jobName\",\"collation\": \"http://marklogic.com/collation/codepoint\",\"range-value-positions\": false,\"invalid-values\": \"reject\"  }  ],  \"path-namespace\": [{\"prefix\": \"msb\",\"namespace-uri\": \"http://marklogic.com/spring-batch\"}],  \"range-path-index\": [{\"scalar-type\": \"unsignedLong\",\"collation\": \"\",\"path-expression\": \"/msb:mlJobInstance/msb:jobExecutions/msb:jobExecution/msb:id\",\"range-value-positions\": false,\"invalid-values\": \"reject\"}]}";

    public RestApi getRestApi(int port) {
        RestApi restApi = api.restApi(NAME, port);
        restApi.setGroup("Default");
        restApi.setDatabase(NAME + "-content");
        restApi.setModulesDatabase(NAME + "-modules");
        restApi.setXdbcEnabled(true);
        restApi.setForestsPerHost(1);
        restApi.setErrorFormat("json");
        return restApi;
    }

    public String getDatabase() {
        return DATABASE_JSON;
    }

    public MarkLogicSimpleJobRepositoryConfig(ManageClient manageClient) {
        this.manageClient = manageClient;
        this.api = new API(manageClient);
    }

    public ProtectedCollection getProtectedCollection() {
        ProtectedCollection coll = api.protectedCollection("spring-batch");
        Permission restAdminRead = new Permission();
        restAdminRead.setRoleName("rest-admin");
        restAdminRead.setCapability("read");

        Permission restAdminUpdate = new Permission();
        restAdminUpdate.setRoleName("rest-admin");
        restAdminUpdate.setCapability("update");
        coll.addPermission(restAdminRead);
        coll.addPermission(restAdminUpdate);
        return coll;
    }

    //Lazy - just using the JSON strings instead of building the Role objects
    public List<String> getRoles() {
        List roles = new ArrayList<String>();

        String springBatchReaderRole = "{  \"role-name\": \"spring-batch-reader\",  \"description\": \"Allow users to view Spring Batch job execution metrics\",  \"role\": [\"rest-reader\"],  \"collection\": [ \"spring-batch\" ],  \"privilege\": [    {      \"privilege-name\": \"xdbc-invoke\",      \"action\": \"http://marklogic.com/xdmp/privileges/xdbc-invoke\",      \"kind\": \"execute\"    }  ]}";

        String springBatchAdminRole = "{  \"role-name\": \"spring-batch-admin\",  \"description\": \"Allow users to view Spring Batch job execution metrics\",  \"role\": [\"spring-batch-reader\", \"rest-admin\", \"rest-extension-user\"],  \"privilege\": [    {      \"privilege-name\": \"any-uri\",      \"action\": \"http://marklogic.com/xdmp/privileges/any-uri\",      \"kind\": \"execute\"    },    {      \"privilege-name\": \"any-collection\",      \"action\": \"http://marklogic.com/xdmp/privileges/any-collection\",      \"kind\": \"execute\"    },    {      \"privilege-name\": \"xdbc:insert-in\",      \"action\": \"http://marklogic.com/xdmp/privileges/xdbc-insert-in\",      \"kind\": \"execute\"    },    {      \"privilege-name\": \"xdbc:insert\",      \"action\": \"http://marklogic.com/xdmp/privileges/xdbc-insert\",      \"kind\": \"execute\"    }  ],  \"permission\": [     {       \"role-name\": \"rest-admin\",       \"capability\": \"update\"     },     {       \"role-name\": \"rest-admin\",       \"capability\": \"read\"     },     {       \"role-name\": \"rest-extension-user\",       \"capability\": \"execute\"     }   ]}";

        String springBatchTestRole = "{  \"role-name\" : \"spring-batch-test\",  \"role\" : [ \"spring-batch-admin\"],  \"privilege\": [    {      \"privilege-name\": \"xdbc:eval-in\",      \"action\": \"http://marklogic.com/xdmp/privileges/xdbc-eval-in\",      \"kind\": \"execute\"    },    {      \"privilege-name\": \"xdbc:eval\",      \"action\": \"http://marklogic.com/xdmp/privileges/xdbc-eval\",      \"kind\": \"execute\"    },    {      \"privilege-name\": \"unprotected-collections\",      \"action\": \"http://marklogic.com/xdmp/privileges/unprotected-collections\",      \"kind\": \"execute\"    },    {      \"privilege-name\": \"xdbc:invoke\",      \"action\": \"http://marklogic.com/xdmp/privileges/xdbc-invoke\",      \"kind\": \"execute\"    },    {      \"privilege-name\": \"xdbc:invoke-in\",      \"action\": \"http://marklogic.com/xdmp/privileges/xdbc-invoke-in\",      \"kind\": \"execute\"    }  ]}";

        roles.add(springBatchReaderRole);
        roles.add(springBatchAdminRole);
        roles.add(springBatchTestRole);

        return roles;
    }

    public List<User> getUsers() {
        User springBatchAdmin = api.user("spring-batch-admin");
        springBatchAdmin.setDescription("Admin user for Spring Batch application");
        springBatchAdmin.setPassword("password");
        springBatchAdmin.addRole("spring-batch-admin");

        User springBatchTest = api.user("spring-batch-test");
        springBatchTest.setDescription("Test user for Spring Batch application - delete this user for deployments");
        springBatchTest.setPassword("password");
        springBatchTest.addRole("spring-batch-test");

        User springBatchUser = api.user("spring-batch-user");
        springBatchUser.setDescription("Sample user for Spring Batch application");
        springBatchUser.setPassword("password");
        springBatchUser.addRole("spring-batch-reader");

        List<User> users = new ArrayList<User>();
        users.add(springBatchAdmin);
        users.add(springBatchTest);
        users.add(springBatchUser);
        return users;
    }

    public String getSpringBatchOptions() {
        return "<options xmlns=\"http://marklogic.com/appservices/search\">\n" +
                "    <constraint name=\"jobInstance\">\n" +
                "        <element-query name=\"jobInstance\" ns=\"http://marklogic.com/spring-batch\" />\n" +
                "    </constraint>\n" +
                "    <constraint name=\"jobInstanceId\">\n" +
                "        <value>\n" +
                "            <element name=\"id\" ns=\"http://marklogic.com/spring-batch\" />\n" +
                "        </value>\n" +
                "    </constraint>\n" +
                "    <constraint name=\"jobName\">\n" +
                "        <value>\n" +
                "            <element name=\"jobName\" ns=\"http://marklogic.com/spring-batch\" />  \n" +
                "        </value>\n" +
                "    </constraint>\n" +
                "    <constraint name=\"jobKey\">\n" +
                "        <value>\n" +
                "            <element name=\"jobKey\" ns=\"http://marklogic.com/spring-batch\" />  \n" +
                "        </value>\n" +
                "    </constraint>\n" +
                "    <constraint name=\"jobParameter\">\n" +
                "        <value>\n" +
                "            <element name=\"jobParameter\" ns=\"http://marklogic.com/spring-batch/job-parameter\" />  \n" +
                "        </value>\n" +
                "    </constraint>\n" +
                "    <constraint name=\"status\">\n" +
                "        <value>\n" +
                "            <element name=\"status\" ns=\"http://marklogic.com/spring-batch\" />  \n" +
                "        </value>\n" +
                "    </constraint>\n" +
                "    <constraint name=\"jobExecutionId\">\n" +
                "        <range type=\"xs:unsignedLong\" facet=\"false\">\n" +
                "              <path-index xmlns:msb=\"http://marklogic.com/spring-batch\">/msb:mlJobInstance/msb:jobExecutions/msb:jobExecution/msb:id</path-index>\n" +
                "        </range>\n" +
                "    </constraint>\n" +
                "    <constraint name=\"endDateTime\">\n" +
                "        <container>\n" +
                "            <element name=\"endDateTime\" ns=\"http://marklogic.com/spring-batch\" />  \n" +
                "        </container>\n" +
                "    </constraint>\n" +
                "    <constraint name=\"type\">\n" +
                "        <collection prefix=\"http://marklogic.com/spring-batch/\" facet=\"false\" />\n" +
                "    </constraint>\n" +
                "    <constraint name=\"jobExecution-createDateTime\">\n" +
                "        <range>\n" +
                "            <element ns=\"http://marklogic.com/spring-batch\" name=\"createDateTime\"/>\n" +
                "        </range>\n" +
                "    </constraint>\n" +
                "    <constraint name=\"jobInstance-createDateTime\">\n" +
                "        <range>\n" +
                "            <element ns=\"http://marklogic.com/spring-batch\" name=\"createDateTime\"/>\n" +
                "        </range>\n" +
                "    </constraint>\n" +
                "    <values name=\"jobExecutionId\">\n" +
                "        <range type=\"xs:unsignedLong\">\n" +
                "              <element ns=\"http://marklogic.com/spring-batch\" name=\"id\" />\n" +
                "        </range>\n" +
                "      </values>\n" +
                "    <values name=\"jobInstanceId\">\n" +
                "        <range type=\"xs:unsignedLong\">\n" +
                "              <element ns=\"http://marklogic.com/spring-batch\" name=\"id\" />\n" +
                "        </range>\n" +
                "      </values>\n" +
                "      <values name=\"jobName\">\n" +
                "        <range type=\"xs:string\">\n" +
                "              <element ns=\"http://marklogic.com/spring-batch\" name=\"jobName\" />\n" +
                "        </range>\n" +
                "      </values>\n" +
                "    <operator name=\"sort\">\n" +
                "           <state name=\"date\">\n" +
                "              <sort-order direction=\"descending\" type=\"xs:dateTime\">\n" +
                "                 <element ns=\"http://marklogic.com/spring-batch\" name=\"createDateTime\"/>\n" +
                "              </sort-order>\n" +
                "        </state>\n" +
                "     </operator>\n" +
                "    <transform-results apply=\"raw\" />\n" +
                "</options>";

    }

}
