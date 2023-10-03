package com.camunda.bpmn.modelpoc;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.bpmn.instance.BoundaryEvent;

public class Task {

	private String id;
	private String async;
	private Map<String, Object> output;
	private String type;
	private String calledElement;
	private BoundaryEvent boundaryEvent;
	private boolean multiInstance;
	private String messageName;
	private String eventGatewayId;
	private List<Task> subprocessTasks;
	public Task() {
		super();
	}

	public Task(String id, String async, Map<String, Object> output) {
		super();
		this.id = id;
		this.async = async;
		this.output = output;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAsync() {
		return async;
	}
	public void setAsync(String async) {
		this.async = async;
	}
	public Map<String, Object> getOutput() {
		return output;
	}
	public void setOutput(Map<String, Object> output) {
		this.output = output;
	}
	
	public String getCalledElement() {
		return calledElement;
	}
	public void setCalledElement(String calledElement) {
		this.calledElement = calledElement;
	}
	public BoundaryEvent getBoundaryEvent() {
		return boundaryEvent;
	}
	public void setBoundaryEvent(BoundaryEvent boundaryEvent) {
		this.boundaryEvent = boundaryEvent;
	}
	public boolean isMultiInstance() {
		return multiInstance;
	}
	public void setMultiInstance(boolean multiInstance) {
		this.multiInstance = multiInstance;
	}
	public String getMessageName() {
		return messageName;
	}
	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}
	public String getEventGatewayId() {
		return eventGatewayId;
	}
	public void setEventGatewayId(String eventGatewayId) {
		this.eventGatewayId = eventGatewayId;
	}
	public List<Task> getSubprocessTasks() {
		return subprocessTasks;
	}
	public void setSubprocessTasks(List<Task> subprocessTasks) {
		this.subprocessTasks = subprocessTasks;
	}


	@Override
	public String toString() {
		return "Task [id=" + id + ", async=" + async + ", output=" + output + ", type=" + type + ", calledElement="
				+ calledElement + ", boundaryEvent=" + boundaryEvent + ", multiInstance=" + multiInstance
				+ ", messageName=" + messageName + ", eventGatewayId=" + eventGatewayId + ", subprocessTasks="
				+ subprocessTasks + "]";
	}
}
