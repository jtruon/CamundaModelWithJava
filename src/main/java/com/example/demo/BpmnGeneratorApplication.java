package com.example.demo;

import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.model.bpmn.builder.ProcessBuilder;
import io.camunda.zeebe.model.bpmn.instance.Definitions;
import io.camunda.zeebe.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BpmnGeneratorApplication {
	public static void main(String[] args) {
		// Initialize BPMN model
		BpmnModelInstance modelInstance = createBpmnModel();

		// Ensure modeler namespace is set correctly
		addModelerAttributes(modelInstance);

		// Save the BPMN file
		saveBpmnModel(modelInstance, "generated.bpmn");
	}

	private static BpmnModelInstance createBpmnModel() {
		ProcessBuilder processBuilder = Bpmn.createProcess()
				.id("demoProcess")
				.name("Demo Process")
				.executable();

		return processBuilder
				.startEvent()
				.name("Start Event")
				.userTask()
				.name("User Task")
				.endEvent()
				.name("End Event")
				.done();
	}

	private static void addModelerAttributes(BpmnModelInstance modelInstance) {
		Definitions definitions = modelInstance.getDefinitions();
		DomDocument document = modelInstance.getDocument();

		// Get root XML element
		DomElement rootElement = document.getRootElement();

		// Define namespace URI
		String modelerNamespace = "http://camunda.org/schema/modeler/1.0";

		// Register namespace
		rootElement.registerNamespace("modeler", modelerNamespace);

		// Add modeler attributes
		rootElement.setAttribute(modelerNamespace, "executionPlatform", "Camunda Cloud");
		rootElement.setAttribute(modelerNamespace, "executionPlatformVersion", "8.6.7");

		// Ensure BPMN diagram exists
		if (modelInstance.getModelElementsByType(BpmnDiagram.class).isEmpty()) {
			modelInstance.newInstance(BpmnDiagram.class);
		}
	}

	private static void saveBpmnModel(BpmnModelInstance modelInstance, String filename) {
		File file = new File(filename);
		try (FileOutputStream fos = new FileOutputStream(file)) {
			Bpmn.writeModelToStream(fos, modelInstance);
			System.out.println("BPMN model saved to " + file.getAbsolutePath());
		} catch (IOException e) {
			System.err.println("Error saving BPMN file: " + e.getMessage());
		}
	}
}