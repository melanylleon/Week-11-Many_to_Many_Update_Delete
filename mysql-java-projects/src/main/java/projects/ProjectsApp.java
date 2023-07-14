package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

/*
 * A menu-driven application that creates, reads, updates, and deletes data in the projects database using user input. The projects in the database are 
 * DIY projects.
 */

public class ProjectsApp {

	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject;
	Integer difficulty;

	// @formatter:off
	private List<String> operations = List.of(
		"1) Add a project",
		"2) List projects",
		"3) Select a project",
		"4) Update project details",
		"5) Delete a project"
	);
	// @formatter:on
	
	public static void main(String[] args) {
		new ProjectsApp().processUserSelections();	
	}
	
	/* 
	 * Uses a selection input by the user to perform different operations on the projects database. The user can create a project,
	 * list the projects in the database, and select a project to display it's information. The user can also update a selected project
	 * and delete a project. If the user does not select an operation and presses the Enter key, it terminates the program.
	 */
	private void processUserSelections() {
		boolean done = false;
		
		while(!done) {
			try {
				int selection = getUserSelection();
				
				switch (selection) {
					case -1:
						done = menuExit();
						break;
						
					case 1:
						createProject();
						break;
						
					case 2:
						listProjects();
						break;
						
					case 3: 
						selectProject();
						break;
						
					case 4:
						updateProjectDetails();
						break;
						
					case 5:
						deleteProject();
						break;
					
					default: 
						System.out.println("\n" + selection + " is not a vaild selection. Try again.");
				}

			} catch (Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			}
		}
	}
 
	/*
	 * Deletes a project using the project ID input by the user. It calls the deleteProject() method from the ProjectService class and 
	 * passes the project ID to the method. The project ID will be used to find and delete the project in the project table. 
	 */
	private void deleteProject() {
		listProjects();
	 	
		Integer projectId = getIntInput("Enter the project ID of the project you want to delete");
	
		projectService.deleteProject(projectId);
		
		System.out.println("The project with a project ID=" + projectId + " was deleted.");
		
		//If the current project (the selected project) is the project that was deleted, it assigns a null value to the current project 
		if (Objects.nonNull(curProject) && curProject.getProjectId() == projectId) {
			curProject = null;
		}
	}

	/*
	 *  Updates the information of the current project using the user input. It calls the modifyProjectDetails() method from the ProjectService class and passes
	 * a Project object to the method. This object with the updated information will be used to update the project data in the project table. It then calls
	 * the fetchProjectById() method in the ProjectService class and passes the project ID to the method. This method will retrieve the updated project information
	 * using the project ID.
	*/ 
	private void updateProjectDetails() {
		//If a project is not selected, the user is required to select a project to update 
		if (Objects.isNull(curProject)) {
			System.out.println("\nSelect the project you want to update");
		}
		
		String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours [" + curProject.getEstimatedHours() + "]");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours [" + curProject.getActualHours() + "]");
		Integer difficulty = getIntInput("Enter the project difficulty [" + curProject.getDifficulty() + "]");
		String notes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");
		
		Project project = new Project();
		
		// If the user does not input data for one of the Project class fields, it sets the value of the field to the current project's field value.
		// If the user did input data for a specific field, it sets the value of the field to the value input by the user.
		project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours );
		project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
		project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
		project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
		
		project.setProjectId(curProject.getProjectId());

		projectService.modifyProjectDetails(project);
	
		curProject = projectService.fetchProjectById(curProject.getProjectId());
	}

	
	/* 
	 * The user selects a project by entering the project ID number. The selected project is called the current project (variable name curProject). It then calls the
	 * fetchProjectById() method in the ProjectService class and passes the project ID to the method. This method will retrieve the current project from the project table
	 * using the project ID.
	 */
	private void selectProject() {
		listProjects();
		
		Integer projectId = getIntInput("Enter a project ID to select a project");

		//Used to unselect a project that could already be selected
		curProject = null;
		
		curProject = projectService.fetchProjectById(projectId);
	}

	/* 
	 * Retrieves all projects and prints their names and IDs. It calls the fetchAllProjects() method in the ProjectService class to retrieve all of the
	 * projects in the project table.
	 */
	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		projects.forEach(project -> System.out.println("   " + project.getProjectId() + ": " + project.getProjectName()));
	}

	// Creates a project using the user input. It calls the addProject() method from the ProjectService class to insert the project into the project table.
	private void createProject() {
		int i = 0;

		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer validateDifficulty = getIntInput("Enter the project difficulty (1-5)");
		//Validates that the project difficulty level input by the user is between 1 and 5 
		while (i >= 0) {
			if (validateDifficulty >= 1 && validateDifficulty <= 5) {
				difficulty = validateDifficulty;
				i = -1;
			} else {
				validateDifficulty = getIntInput("Enter a valid project difficulty value (1-5)");
			}
		}
		String notes = getStringInput("Enter the project notes");
		
		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
		
	}

	//Converts the String input by the user into a BigDecimal
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		
		//If the user did not input data, it returns null
		if (Objects.isNull(input)) {
			return null;
		}
		
		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}
	}

	//This method is used to exit the while loop in the processUserSelections() method. This will terminate the program. 
	private boolean menuExit() {
		System.out.println("Exiting the menu.");
		return true;
	}

	//Prompts the user to make a selection and gets the operation selected by the user
	private int getUserSelection() {
		printOperations();
		Integer input = getIntInput("Enter a menu selection");
		
		//Returns -1 if the user did not make a selection. If they did make a selection, it returns the selection.
		return Objects.isNull(input) ? -1 : input; 
	}

	//Converts the String input by the user into an Integer
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		
		if (Objects.isNull(input)) {
			return null;
		}
		
		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}

	//Prompts the user for input about a selection or a project and gets the user's input
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		
		return input.isBlank() ? null : input.trim();
	}

	//Prints the operations
	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");
		
		for (String operation : operations) {
			System.out.println(operation);
		}
		
		//Checks if a project is selected. If it is not selected, it prints a message. If it is selected, it prints the project information.
		if (Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		} else { 
			System.out.println("\nYou are working with project: " + curProject);
		}
	}

}
