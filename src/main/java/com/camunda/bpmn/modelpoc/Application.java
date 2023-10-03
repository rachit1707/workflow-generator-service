package com.camunda.bpmn.modelpoc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration;


@SpringBootApplication(exclude = {
GroovyTemplateAutoConfiguration.class})
public class Application {
	public static BpmnModelInstance modelInstance;

	public static void main(String... args) throws IOException {
		SpringApplication.run(Application.class, args);
		
	}

	public static void generateProcess(Workflow workflow) throws IOException {
		modelInstance = Bpmn.createEmptyModel();
		// bpmn.createExecutableProcess();
		Definitions definitions = modelInstance.newInstance(Definitions.class);
		// definitions.setTargetNamespace("http://camunda.org/examples");
		definitions.setTargetNamespace("http://camunda.org/schema/1.0/bpmn");
		modelInstance.setDefinitions(definitions);
		System.out.println("=========modelInstance" + modelInstance.toString());
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
			System.out.println("=========elementType" + elementtype);
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
				// System.out.println("===== ==== Sequence
				// Flow"+flowDimension.get("sequenceStartPoint") +"END
				// point"+flowDimension.get("sequenceEndPoint"));
               //System.out.println("==== ===== Spoint : "+node.get(0).getId()+" another node: "+node.get(1).getId());
				Iterator<FlowNode> iterateNode = node.iterator();
				createSequenceFlow(process, iterateNode.next(), iterateNode.next(), plane,
						flowDimension.get("sequenceStartPoint"), elementDimension.get("y").intValue() + 25,
						flowDimension.get("sequenceStartPoint") + 100, elementDimension.get("y").intValue() + 25);
				// clear sequence flow dimensions and reset
				flowDimension.clear();
				// Adding start point of sequence flow
				// X axis value updated after last element creation minus 100(default distance
				// between elements)
				flowDimension.putIfAbsent("sequenceStartPoint", elementDimension.get("x").intValue() - 100);
				node.remove(0);
			}
		});
		// validate and write model to file
		Bpmn.validateModel(modelInstance);
		File file = File.createTempFile(workflow.getWorkflowName(), ".bpmn",
				new File("C:\\Users\\Tushar.Panchbude\\git\\gno\\src\\main\\resources\\bpmn\\automatedBPMN\\"));
		Bpmn.writeModelToFile(file, modelInstance);
		System.out.println("=========modelInstance" + file.getPath());

	}

	static <T extends BpmnModelElementInstance> T createElement(BpmnModelElementInstance parentElement, String name,
			Class<T> elementClass, BpmnPlane plane, double x, double y, double heigth, double width,
			boolean withLabel) {
		T element = modelInstance.newInstance(elementClass);
		// element.setAttributeValue("id", id, true);
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