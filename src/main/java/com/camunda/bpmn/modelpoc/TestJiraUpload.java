package com.camunda.bpmn.modelpoc;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;

import java.io.File;

public class TestJiraUpload {
    public static void main(String []args){
        HttpResponse<JsonNode> response = Unirest.post("https://gno-poc.atlassian.net/rest/api/2/issue/GNO-54/attachments")
                .basicAuth("email@example.com", "Basic dHlwZXRvbWFoYW50ZXNoQGdtYWlsLmNvbTpBVEFUVDN4RmZHRjB4U3I0YXVOdmN2QXRDYk5Xa3NPem1PcDFGS25kaWNEVUtkR3FQM3FDaVVUZF9TTFAzYVJHWlBaWHRnZHhJZFhDMm05MVE3ZjdDMzlXTHRZeDJHam5oMDNUcEZlOXdNSURqd1lrbDVYMm5nckZrdnRrT1p6TkJ3Qm1HdjEzWWpCaTVLaTlxZ1hCbnJ1ajk4MnZHSjNnTDN0RU15dWJ2REFYMC1CQ0RXUEl6Vm89MzEwREJFMzg=")
                .header("Accept", "application/json")
                .header("X-Atlassian-Token", "no-check")
                .field("file", new File("D:/Poc/workflow-generator-service/src/main/resources/service_desk_task.form"))
                .asJson();

        System.out.println("Attached file to jira : "+response.getBody());
    }

}
