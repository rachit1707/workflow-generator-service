package com.camunda.bpmn.modelpoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Workflow {
	private String workflowName;
	private List<String> deployments;
	private Map<String, Object> input;
	private List<Task> tasks;
	
	public String getWorkflowName() {
		return workflowName;
	}
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
	public Map<String, Object> getInput() {
		return input;
	}
	public void setInput(Map<String, Object> input) {
		this.input = input;
	}
	public List<Task> getTasks() {
		return tasks;
	}
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	public List<String> getDeployments() {
		return deployments;
	}
	public void setDeployments(List<String> deployments) {
		this.deployments = deployments;
	}
	public void addTask(Task task) {
		if(tasks == null) {
			tasks = new ArrayList<Task>();
		}
		tasks.add(task);
	}
	
	@Override
	public String toString() {
		return "Workflow [workflowName=" + workflowName + ", deployments=" + deployments + ", input="
				+ input + ", taskList=" + tasks + "]";
	}
}
