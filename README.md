# DIY Projects
![GitHub contributors](https://img.shields.io/github/contributors/melanylleon/DIY-Projects)
![GitHub commit activity (branch)](https://img.shields.io/github/commit-activity/t/melanylleon/DIY-Projects)
![GitHub issues](https://img.shields.io/github/issues/melanylleon/DIY-Projects)
![GitHub](https://img.shields.io/github/license/melanylleon/DIY-Projects)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](CODE_OF_CONDUCT.md)


##  :star2: About the Project
This is menu-driven application used to create DIY project instructions. 

### :space_invader: Tech Stack
- Java
- MySQL

### :dart: Features
- Allows the user to select an option from a menu. They can choose to create, list, select, update, or delete a project.
- Lists all of the DIY projects in the database including the project steps, materials, and categories.

## 	:toolbox: Getting Started

### :gear: Installation
- Any database management tool, such as [DBeaver](https://github.com/advanced-rest-client/arc-electron/releases), can be used along with this project. 

### :running: Run Locally
Clone the project

``` 
git clone https://github.com/melanylleon/DIY-Projects.git
```
Run on IDE

## :eyes: Usage
This program can create DIY project instructions. It includes the project information such as the project id, name, estimated hours to complete, actual hours needed to complete the project, and some notes.  It lists the project information along with the steps and the materials needed to complete the project. There are also categories to classify the type of project being created. For example, a bird house project would be in the Outdoors category.  

In this project, you can create, read, update, and delete a project and a list of all the projects can be retrieved. A SQL file is used to create the tables and to insert the steps, materials, and categories into the database. 

**The user is asked to input data to create a project**
```java

private void createProject() {
		int i = 0;

		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer validateDifficulty = getIntInput("Enter the project difficulty (1-5)");
		 
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


```
</br>

**Updates a project in the database**
```java

public boolean modifyProjectDetails(Project project) {
		String sql = "UPDATE " + PROJECT_TABLE + " SET "
				+ "project_name = ?, "
				+ "estimated_hours = ?, "
				+ "actual_hours = ?, "
				+ "difficulty = ?, "
				+ "notes = ? "
				+ "WHERE project_id = ?";

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement statement = conn.prepareStatement(sql)) {
				setParameter(statement, 1, project.getProjectName(), String.class);
				setParameter(statement, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(statement, 3, project.getActualHours(), BigDecimal.class);
				setParameter(statement, 4, project.getDifficulty(), Integer.class);
				setParameter(statement, 5, project.getNotes(), String.class);
				setParameter(statement, 6, project.getProjectId(), Integer.class);


				boolean updated = statement.executeUpdate() == 1;

				commitTransaction(conn);

				
				return updated;

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}

		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

```

## :compass: Roadmap

* [ ] Write the methods to create the steps, materials, and categories. It would ask the user to input the data rather than using the SQL file to insert the data into the database. 

## :wave: Contributing
Please feel free to contribute to the project!  

Please see the `CONTRIBUTING.md` file for more information.

## :warning: License
Please see the `LICENSE.txt` file for more information.

# :handshake: Contact

Melany Landaverde Leon - melany.leon0199@gmail.com

Project Link: [https://github.com/melanylleon/DIY-Projects.git](https://github.com/melanylleon/DIY-Projects.git)





