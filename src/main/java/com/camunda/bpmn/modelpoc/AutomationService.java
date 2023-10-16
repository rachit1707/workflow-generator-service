package com.camunda.bpmn.modelpoc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.camunda.bpm.model.bpmn.instance.CallActivity;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.IntermediateCatchEvent;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.ReceiveTask;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.SubProcess;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnLabel;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
import org.springframework.stereotype.Service;

import spinjar.com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Tushar.Panchbude 
 * Services use to generate BPMN and start Deployed
 *         camunda workflow's
 */
@Service
public class AutomationService {

	public static BpmnModelInstance modelInstance;
	public static int count = 0;

	/*
	 * @info here we toggle the response on perform generation of BPMN by parsing
	 * text and other starts deployed BPMN for unlock user account. here we call
	 * createBPMN() fpor auto generated BPMN design and make a rest call to camunda
	 * to start unlock process
	 */
	public String query() throws IOException {
		String response = null;
		String queryResponse = null;
		if (count == 0) {
			System.out.println("Response:  " + count);

			response = doHttpUrlConnectionAction(
					"http://localhost:8082/engine-rest/process-definition/key/Camunda_Workflow_Generator/start", "POST",
					"{\"variables\":\r\n"
							+ "    {\"topic\" : {\"value\" : \"Expiry Account extend\", \"type\": \"String\"} }}");
			queryResponse = "Your Account was expired, We have raised ticket for the same: INCGNO"
					+ Math.round(Math.random() * 10000) + ". will keep you updated on ticket status";
			System.out.println("ToggleCount:  " + count);
			count = 1;
		} else if (count == 1) {
			response = doHttpUrlConnectionAction(
					"http://localhost:8082/engine-rest/process-definition/key/Unlock_User_Account_Poc/start", "POST",
					null);
			queryResponse = "Your Account was locked, We have unlocked your account please check now!"
					+ " We have raised ticket for the same: INCGNO" + Math.round(Math.random() * 10000);
			count = 0;
			System.out.println("ToggleCount:  " + response);
		}
		System.out.println("ToggleCount:  " + count);
		return queryResponse;

	}

	public static String doHttpUrlConnectionAction(String desiredUrl, String requestType, String body)
			throws IOException {
		BufferedReader reader = null;
		StringBuilder stringBuilder;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(desiredUrl).openConnection();
			connection.setRequestMethod(requestType);// Can be "GET","POST","DELETE",etc
			connection.setReadTimeout(3 * 1000);
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			OutputStream os = connection.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
			if (body == null) {
				osw.write("");
			} else {
				osw.write(body);
			}

			osw.flush();
			osw.close();
			os.close();
			connection.connect();// Make call
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));// Reading Responce
			stringBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			// logger.info("response: ==" + stringBuilder.toString());
			return stringBuilder.toString();
		} catch (IOException e) {
			throw new IOException("Problem in connection : ", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
					throw new IOException("Problem in closing reader : ", ioe);
				}
			}
		}
	}

	/*
	 * @info here we parse text with task name and type and map it to workflow map
	 */
	public String createBPMN(String chatGPTResponse) throws IOException {
		String instruction = null;
		int taskAdded = 0;
		System.out.println("=========chatGPTResponse==" + chatGPTResponse);
		if (chatGPTResponse.isEmpty() || chatGPTResponse == null || chatGPTResponse.isBlank()) {
			instruction = "Camunda is a popular open-source workflow and decision automation platform that supports various task types and names. Here are some suggestions for the task types and names for the given steps:\r\n"
					+ "\r\n" + "Task Type: Service Task\r\n" + "Task Name: Check User Account Expiry Status\r\n"
					+ "\r\n" + "Task Type: User Task\r\n"
					+ "Task Name: Request Manager Approval for Account Extension\r\n" + "\r\n"
					+ "Task Type: Service Task\r\n" + "Task Name: Update Account Expiry in Database\r\n" + "\r\n"
					+ "Task Type: Service Task\r\n" + "Task Name: Notify User about Account Status\r\n" + "\r\n"
					+ "These task types and names can be customized according to your specific Camunda implementation and business requirements.";
		} else {

			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map = mapper.readValue(chatGPTResponse, Map.class);
			System.out.println("Create BPMN " + map.get("choices"));
			List<Object> choices = (List<Object>) map.get("choices");
			HashMap<String, Object> listOfContent = (HashMap<String, Object>) choices.iterator().next();
			System.out.println("Create BPMN =====" + listOfContent.get("message"));
			HashMap<String, String> content = (HashMap<String, String>) listOfContent.get("message");
			System.out.println("Create BPMN =====" + content.get("content"));
			instruction = content.get("content");
		}
		List<Task> taskList = new ArrayList<Task>();
		Task startEvent = new Task();
		startEvent.setId("start");
		startEvent.setType("Start");
		taskList.add(startEvent);
		Workflow workflow = new Workflow();
		Iterator<String> instructionSplit = instruction.stripTrailing().lines().iterator();
		Task task = new Task();
		while (instructionSplit.hasNext()) {
			String line = instructionSplit.next().toString();

			// condition for parsing taskname and type from one line
			if (line.toUpperCase().contains("TASK NAME:") && line.toUpperCase().contains("TASK TYPE:")) {
				String[] taskInOne = line.stripLeading().split("[;,.]");
				for (String taskDetails : taskInOne) {
					if (taskDetails.toUpperCase().contains("TASK NAME:")) {
						String[] taskId = taskDetails.stripLeading().split(":");
						if (!taskId[0].isBlank()) {
							task.setId(taskId[1].trim().replaceAll("[^A-Za-z0-9\\s]", ""));
							taskAdded++;
						}
					} // condition for parsing taskname and type from mutli-line
					else if (taskDetails.toUpperCase().contains("TASK TYPE:")) {
						String[] taskType = taskDetails.stripLeading().split(":");
						if (!taskType[0].isBlank()) {
							task.setType(taskType[1].trim().replaceAll("[^A-Za-z0-9]", ""));
							taskAdded++;
						}
					}
				}

			} else if (line.toUpperCase().contains("TASK NAME:")) {
				String[] taskId = line.stripLeading().split(":");
				if (!taskId[0].isBlank()) {
					task.setId(taskId[1].trim().replaceAll("[^A-Za-z0-9\\s]", ""));
					taskAdded++;
				}
			} else if (line.toUpperCase().contains("TASK TYPE:")) {

				String[] taskType = line.stripLeading().split(":");
				System.out.println("-==================" + taskType);
				if (!taskType[0].isBlank()) {
					task.setType(taskType[1].trim().replaceAll("[^A-Za-z0-9]", ""));
					taskAdded++;
				}
			}

			if (taskAdded == 2) {
				taskList.add(task);
				taskAdded = 0;
				task = new Task();
			}
		}
		Task endEvent = new Task();
		endEvent.setId("end");
		endEvent.setType("End");
		taskList.add(endEvent);
		System.out.println("-==================" + taskList.toString());
		workflow.setWorkflowName("Automated_BPMN_" + Math.random() * 10);
		workflow.setTasks(taskList);
		return generateProcess(workflow);
	}

	/*
	 * @param Workflow
	 * 
	 * @info workflow contains workflow name,list of task to be generated logic for
	 * creating element from list of task
	 */
	public static String generateProcess(Workflow workflow) throws IOException {
		modelInstance = Bpmn.createEmptyModel();
		// bpmn.createExecutableProcess();
		Definitions definitions = modelInstance.newInstance(Definitions.class);
		// definitions.setTargetNamespace("http://camunda.org/examples");
		definitions.setTargetNamespace("http://camunda.org/schema/1.0/bpmn");
		modelInstance.setDefinitions(definitions);
		// System.out.println("=========modelInstance" + modelInstance.toString());
		List<Task> bpmnElement = workflow.getTasks();
		// create the process
		Process process = modelInstance.newInstance(Process.class);
		process.setAttributeValue("id", workflow.getWorkflowName(), true);
		process.setExecutable(true);
		definitions.addChildElement(process);
		BpmnDiagram diagram = modelInstance.newInstance(BpmnDiagram.class);
		BpmnPlane plane = modelInstance.newInstance(BpmnPlane.class);
		plane.setBpmnElement(process);
		diagram.setBpmnPlane(plane);
		definitions.addChildElement(diagram);
		// initial values of X-axis,Y-axis,Height,Width for event and Activity
		HashMap<String, Double> elementDimension = new HashMap<String, Double>();
		elementDimension.put("x", 50.00);
		elementDimension.put("y", 100.00);
		elementDimension.put("height", 30.00);
		elementDimension.put("width", 30.00);
		elementDimension.put("taskHeight", 80.00);
		elementDimension.put("taskWidth", 100.00);
		// flowDimension - used for sequence flow
		HashMap<String, Integer> flowDimension = new HashMap<String, Integer>();
		// node -used for
		List<FlowNode> node = new ArrayList<FlowNode>(2);
		// Loop to design each element of workflow
		bpmnElement.forEach(task -> {
			String elementtype = task.getType().toUpperCase();
			switch (elementtype) {
			case "START":
				StartEvent startEvent = createElement(process, task.getId(), StartEvent.class, plane,
						elementDimension.get("x"), elementDimension.get("y"), elementDimension.get("height"),
						elementDimension.get("width"), true);
				// Assignee start point of Sequence flow for start event
				flowDimension.putIfAbsent("sequenceStartPoint",
						(int) (elementDimension.get("x") + elementDimension.get("width")));
				elementDimension.replace("x", elementDimension.get("x") + elementDimension.get("width") + 100);
				node.add(startEvent);
				break;
			case "USERTASK":
				UserTask userTask = createElement(process, task.getId(), UserTask.class, plane,
						elementDimension.get("x"), elementDimension.get("y") - 20, elementDimension.get("taskHeight"),
						elementDimension.get("taskWidth"), true);
				// update X axis for next element
				elementDimension.replace("x", elementDimension.get("x") + elementDimension.get("taskWidth") + 100);
				node.add(userTask);
				break;
			case "SERVICETASK":
				ServiceTask servicetask = createElement(process, task.getId(), ServiceTask.class, plane,
						elementDimension.get("x"), elementDimension.get("y") - 20, elementDimension.get("taskHeight"),
						elementDimension.get("taskWidth"), true);
				elementDimension.replace("x", elementDimension.get("x") + elementDimension.get("taskWidth") + 100);
				node.add(servicetask);
				break;
			case "SCRIPTTASK":
				ScriptTask scriptTask = createElement(process, task.getId(), ScriptTask.class, plane,
						elementDimension.get("x"), elementDimension.get("y") - 20, elementDimension.get("taskHeight"),
						elementDimension.get("taskWidth"), true);
				elementDimension.replace("x", elementDimension.get("x") + elementDimension.get("taskWidth") + 100);
				node.add(scriptTask);
				break;
			case "SENDTASK":
				SendTask sendTask = createElement(process, task.getId(), SendTask.class, plane,
						elementDimension.get("x"), elementDimension.get("y") - 20, elementDimension.get("taskHeight"),
						elementDimension.get("taskWidth"), true);
				elementDimension.replace("x", elementDimension.get("x") + elementDimension.get("taskWidth") + 100);
				node.add(sendTask);
				break;
			case "RECEIVETASK":
				ReceiveTask receiveTask = createElement(process, task.getId(), ReceiveTask.class, plane,
						elementDimension.get("x"), elementDimension.get("y") - 20, elementDimension.get("taskHeight"),
						elementDimension.get("taskWidth"), true);
				elementDimension.replace("x", elementDimension.get("x") + elementDimension.get("taskWidth") + 100);
				node.add(receiveTask);
				break;
			case "CALLACTIVITY":
				CallActivity callActivity = createElement(process, task.getId(), CallActivity.class, plane,
						elementDimension.get("x"), elementDimension.get("y") - 20, elementDimension.get("taskHeight"),
						elementDimension.get("taskWidth"), true);
				elementDimension.replace("x", elementDimension.get("x") + elementDimension.get("taskWidth") + 100);
				node.add(callActivity);
				break;
			case "SUBPROCESS":
				SubProcess subprocess = createElement(process, task.getId(), SubProcess.class, plane,
						elementDimension.get("x"), elementDimension.get("y") - 20, elementDimension.get("taskHeight"),
						elementDimension.get("taskWidth"), true);
				elementDimension.replace("x", elementDimension.get("x") + elementDimension.get("taskWidth") + 100);
				node.add(subprocess);
				break;
			case "BUSINESSRULETASK":
				BusinessRuleTask businessRuleTask = createElement(process, task.getId(), BusinessRuleTask.class, plane,
						elementDimension.get("x"), elementDimension.get("y") - 20, elementDimension.get("taskHeight"),
						elementDimension.get("taskWidth"), true);
				elementDimension.replace("x", elementDimension.get("x") + elementDimension.get("taskWidth") + 100);
				node.add(businessRuleTask);
				break;
			case "MESSAGECATCH":
				IntermediateCatchEvent messageEvent = createElement(process, task.getId(), IntermediateCatchEvent.class,
						plane, elementDimension.get("x"), elementDimension.get("y") - 20,
						elementDimension.get("height"), elementDimension.get("width"), true);
				elementDimension.replace("x", elementDimension.get("x") + elementDimension.get("width") + 100);
				messageEvent.builder().message(task.getMessageName());
				node.add(messageEvent);
				break;
			case "END":
				EndEvent endEvent = createElement(process, task.getId(), EndEvent.class, plane,
						elementDimension.get("x"), elementDimension.get("y"), elementDimension.get("height"),
						elementDimension.get("width"), true);
				node.add(endEvent);
				break;
			}
			if (node.size() == 2) {
				Iterator<FlowNode> iterateNode = node.iterator();
				createSequenceFlow(process, iterateNode.next(), iterateNode.next(), plane,
						flowDimension.get("sequenceStartPoint"), elementDimension.get("y").intValue() + 25,
						flowDimension.get("sequenceStartPoint") + 100, elementDimension.get("y").intValue() + 25);
				// clear sequence flow dimensions and reset
				flowDimension.clear();
				/*
				 * Adding start point of sequence flow X axis value updated after last element
				 * creation minus 100(default distance between elements)
				 */
				flowDimension.putIfAbsent("sequenceStartPoint", elementDimension.get("x").intValue() - 100);
				node.remove(0);
			}
		});
		// validate and write model to file
		Bpmn.validateModel(modelInstance);
		File file = File.createTempFile(workflow.getWorkflowName(), ".bpmn", new File(
				"C:\\Users\\SharmaR59\\Documents\\BPMN"));
		Bpmn.writeModelToFile(file, modelInstance);
		System.out.println("=========modelInstance" + file.getPath());
		HttpResponse<JsonNode> response = Unirest.post("https://your-domain.atlassian.net/rest/api/3/issue//attachments")
				.basicAuth("email@example.com", "Basic dHlwZXRvbWFoYW50ZXNoQGdtYWlsLmNvbTpBVEFUVDN4RmZHRjB4U3I0YXVOdmN2QXRDYk5Xa3NPem1PcDFGS25kaWNEVUtkR3FQM3FDaVVUZF9TTFAzYVJHWlBaWHRnZHhJZFhDMm05MVE3ZjdDMzlXTHRZeDJHam5oMDNUcEZlOXdNSURqd1lrbDVYMm5nckZrdnRrT1p6TkJ3Qm1HdjEzWWpCaTVLaTlxZ1hCbnJ1ajk4MnZHSjNnTDN0RU15dWJ2REFYMC1CQ0RXUEl6Vm89MzEwREJFMzg=")
				.header("Accept", "application/json")
				.header("X-Atlassian-Token", "no-check")
				.field("file", new File(file.getPath()))
				.asJson();

		System.out.println("Attached file to jira : "+response.getBody());
		return file.getPath();

	}

	/*
	 * @param parentElement,name,elementClass,plane,x,y,heigth,width,withLabel
	 * 
	 * @info parentElement- process defined name-element name/id elementClass -
	 * activity/event class plane - bpmn plane where we design x- x-axis of element
	 * Y- y-axis of element height -height of element width - width of element
	 * withLabel - this adds label to element
	 * 
	 * 
	 */
	static <T extends BpmnModelElementInstance> T createElement(BpmnModelElementInstance parentElement, String name,
			Class<T> elementClass, BpmnPlane plane, double x, double y, double heigth, double width,
			boolean withLabel) {
		T element = modelInstance.newInstance(elementClass);
		name = name.trim().replaceAll("[^A-Za-z0-9\\s]", "");
		element.setAttributeValue("id", name.replace(" ", ""), true);
		element.setAttributeValue("name", name, false);
		parentElement.addChildElement(element);

		BpmnShape bpmnShape = modelInstance.newInstance(BpmnShape.class);
		bpmnShape.setBpmnElement((BaseElement) element);

		Bounds bounds = modelInstance.newInstance(Bounds.class);
		bounds.setX(x);
		bounds.setY(y);
		bounds.setHeight(heigth);
		bounds.setWidth(width);
		bpmnShape.setBounds(bounds);

		if (withLabel) {
			BpmnLabel bpmnLabel = modelInstance.newInstance(BpmnLabel.class);
			Bounds labelBounds = modelInstance.newInstance(Bounds.class);
			labelBounds.setX(x);
			labelBounds.setY(y + heigth);
			labelBounds.setHeight(heigth);
			labelBounds.setWidth(width);
			bpmnLabel.addChildElement(labelBounds);
			bpmnShape.addChildElement(bpmnLabel);
		}
		plane.addChildElement(bpmnShape);

		return element;
	}

	/*
	 * @param process,from,to,plane,waypoints
	 * 
	 * @info process - model instance defined from - flowable node of element from
	 * where we need to create sequence to - flowable node of element to where we
	 * need to create sequence plane - defined plane waypoints - x, y axis of start
	 * and end of sequence flow
	 */
	public static SequenceFlow createSequenceFlow(org.camunda.bpm.model.bpmn.instance.Process process, FlowNode from,
			FlowNode to, BpmnPlane plane, int... waypoints) {
		String identifier = from.getId() + "-" + to.getId();
		SequenceFlow sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
		sequenceFlow.setAttributeValue("id", identifier, true);
		process.addChildElement(sequenceFlow);
		sequenceFlow.setSource(from);
		from.getOutgoing().add(sequenceFlow);
		sequenceFlow.setTarget(to);
		to.getIncoming().add(sequenceFlow);

		BpmnEdge bpmnEdge = modelInstance.newInstance(BpmnEdge.class);
		bpmnEdge.setBpmnElement(sequenceFlow);
		for (int i = 0; i < waypoints.length / 2; i++) {
			double waypointX = waypoints[i * 2];
			double waypointY = waypoints[i * 2 + 1];
			Waypoint wp = modelInstance.newInstance(Waypoint.class);
			wp.setX(waypointX);
			wp.setY(waypointY);
			bpmnEdge.addChildElement(wp);
		}
		plane.addChildElement(bpmnEdge);
		return sequenceFlow;
	}

}
