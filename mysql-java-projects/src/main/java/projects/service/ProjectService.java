package projects.service;

import java.util.List; 
import java.util.NoSuchElementException;
import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

/*
 * This is the service layer used to pass the data between the ProjectsApp class and the ProjectDao class
 */

public class ProjectService {

	private ProjectDao projectDao = new ProjectDao();


	// Calls a method in the ProjectDao class to insert a project into the project table.
	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}

	// Retrieves all of the projects when it calls a method in the ProjectDao class. It does not retrieve the projects' categories, steps, or materials.
	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
	}

	/* 
	 * Retrieves a project when it calls a method in the ProjectDao class. The project's materials, steps, and categories are also retrieved.
	 * It throws an exception if the project does not exist in the project table.
	 */
	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectById(projectId).orElseThrow(
				() -> new NoSuchElementException("Project with project ID=" + projectId + " doesn't exist."));
	}

	/* 
	 * Calls a method in the ProjectDao class to update the project data. It throws an exception if the project does not exist 
	 * in the project table.
	 */
	public void modifyProjectDetails(Project project) {
		if (!projectDao.modifyProjectDetails(project)) {
			throw new DbException("Project with project ID=" + project.getProjectId() + " doesn't exist.");
		}

	}

	/* 
	 * Calls a method in the ProjectDao class to delete a project. It throws an exception if the project does not exist
	 * in the project table.
	 */
	public void deleteProject(Integer projectId) {
		if (!projectDao.deleteProject(projectId)) {
			throw new DbException("Project with project ID=" + projectId + " doesn't exist.");
		}
	}

}
