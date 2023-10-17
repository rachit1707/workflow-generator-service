package com.camunda.bpmn.modelpoc;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class AutomationController {

    @Autowired
    AutomationService automationService;

    /*
     * @param query - input from help desk form this request is used to trigger two
     * workflows: 1) one workflow will ask ChaatGpt query which is asked by help
     * desk through Camunda tasklist form 2) another will be workflow to unlock
     * account
     */

    @PostMapping("/queryRequest")
    @ResponseBody
    public String QueryRequest(@RequestBody String query) throws IOException {
        String response = null;
        if (query.isBlank() || query.isEmpty()) {
            response = "No Query Received";
            return response;
        } else {
            response = automationService.query();
            return response;
        }
    }

    /*
     * @param query - updated query from ChatGpt with list of task name and task type
     * this will help to generate Single line camunda workflow
     */
    @PostMapping("/createBPMN")
    @ResponseBody
    public String createBPMN(@RequestBody Map<String, String> CreationDetails) throws IOException {
        String response = null;
        String query = null;
        System.out.println("Create BPMN " + CreationDetails);

        if (!CreationDetails.containsKey("query")) {
            query = CreationDetails.get("query");
            if (query.isBlank() || query.isEmpty()) {
                response = "No Query Received";
                response = automationService.createBPMN(query,null);
                System.out.println("response " + response);
            } else {

                response = automationService.createBPMN(query,CreationDetails.get("ticket_id"));
                System.out.println("response " + response);
            }
			return response;
        }else{
			response="No Query found in request";
			return response;
		}

    }
}
