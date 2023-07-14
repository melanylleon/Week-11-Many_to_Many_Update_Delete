package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

/*
 * This class reads and writes to the tables in the projects database (the DAO layer of the application). It can insert a project into the project table  
 * and fetch all of the projects in the table. It can also fetch a project using the project's ID, update a project's data and delete a project.
 */

public class ProjectDao extends DaoBase {
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	//Inserts a row into the project table
	public Project insertProject(Project project) {
		// @formatter:off
		// The sql String is used to create a SQL statement
		String sql = ""
			+ "INSERT INTO " + PROJECT_TABLE + " "
			+ "(project_name, estimated_hours, actual_hours, difficulty, notes)"
			+ "VALUES "
			+ "(?, ?, ?, ?, ?)";
		// @formatter:on
		
		//Creates a connection with the database
		try (Connection conn = DbConnection.getConnection()) {	
			startTransaction(conn);
			
			//Creates a SQL statement using the sql String. It sets the parameters with the values input by the users 
			try (PreparedStatement statement = conn.prepareStatement(sql)) {	
				setParameter(statement, 1, project.getProjectName(), String.class);
				setParameter(statement, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(statement, 3, project.getActualHours(), BigDecimal.class);
				setParameter(statement, 4, project.getDifficulty(), Integer.class);
				setParameter(statement, 5, project.getNotes(), String.class);
				
				//Executes the SQL statement
				statement.executeUpdate();			
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				
				commitTransaction(conn);
				
				project.setProjectId(projectId);
				
				return project; //Returns the project with the project ID
				
			//Rolls back the transaction and throws a DbException if there is an exception. 
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		//Throws a DbException if there is a SQLException. The DbException class turns a checked exception into an unchecked exception.
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	//Fetches all of the projects from the project table
	public List<Project> fetchAllProjects() {
		String sql = "SELECT * FROM " 
					 + PROJECT_TABLE 
					 + " ORDER BY project_name"; 
		
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try (PreparedStatement statement = conn.prepareStatement(sql)) {
				
				// Fetches the projects when it executes the query and returns a result set. The result set contains the rows of the project table.
				try (ResultSet resultSet = statement.executeQuery()) {
					List<Project> projects = new LinkedList<>();
					
					// Sets the values of the Project objects' fields using the data retrieved from the result set
					while (resultSet.next()) {
						projects.add(extract(resultSet, Project.class)); //Adds each object to the projects list
					}
					
					return projects;
			
				}
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}		
	}

	//Fetches the selected project from the project table using its project ID. It also fetches the project's categories, steps and materials.
	public Optional<Project> fetchProjectById(Integer projectId) {
		String sql = "SELECT * FROM "
					+ PROJECT_TABLE 
					+ " WHERE project_id = ?";
		
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try {
				Project project = null;
				
				try (PreparedStatement statement = conn.prepareStatement(sql)) {
					setParameter(statement, 1, projectId, Integer.class);
					
				
					// Sets the values of the Project object's fields using the data retrieved from the result set. The result set only 
					// contains one row with the data of the project selected by the user. 
					try (ResultSet resultSet = statement.executeQuery()) {
						if (resultSet.next()) {
							project = extract(resultSet, Project.class);
									
						}
					}
				}
				
				// Fetches the material, step, and category data of the project if the Project object is not null
				if (Objects.nonNull(project)) {
					project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
					project.getSteps().addAll(fetchStepsForProject(conn, projectId));
					project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
				}
				
				commitTransaction(conn);
				
				return Optional.ofNullable(project);

			} catch (Exception e){
				rollbackTransaction(conn);
				throw new DbException(e);
			}
			
		} catch (SQLException e) {
			throw new DbException(e);
		}
	
	}

	//Fetches the category data of a project using the category and project category tables 
	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
		// @formatter:off
		String sql = "SELECT c.* FROM " + CATEGORY_TABLE + " c " 
					+ "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
					+ "WHERE project_id = ?";
		// @formatter:on 

		try (PreparedStatement statement = conn.prepareStatement(sql)) {
			setParameter(statement, 1, projectId, Integer.class);

			try (ResultSet resultSet = statement.executeQuery()) {
				List<Category> categories = new LinkedList<>();

				// Sets the values of the Category objects' fields using the data retrieved from the
				// result set. It adds the objects to the categories list.
				while (resultSet.next()) {
					categories.add(extract(resultSet, Category.class));
				}

				return categories;
			}
		}
	}

	// Fetches the steps of a project from the step table
	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
		// @formatter:off
		String sql = "SELECT * FROM " + STEP_TABLE
				   + " WHERE project_id = ?";
		// @formatter:on 

		try (PreparedStatement statement = conn.prepareStatement(sql)) {
			setParameter(statement, 1, projectId, Integer.class);

			try (ResultSet resultSet = statement.executeQuery()) {
				List<Step> steps = new LinkedList<>();

				// Sets the values of the Step objects' fields using the data retrieved from the result
				// set. It adds the objects to the steps list.
				while (resultSet.next()) {
					steps.add(extract(resultSet, Step.class));
				}

				return steps;
			}
		}
	}

	//Fetches the materials used for a project from the material table
	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
		// @formatter:off
		String sql = "SELECT * FROM " + MATERIAL_TABLE
				   + " WHERE project_id = ?";
		// @formatter:on 

		try (PreparedStatement statement = conn.prepareStatement(sql)) {
			setParameter(statement, 1, projectId, Integer.class);

			try (ResultSet resultSet = statement.executeQuery()) {
				List<Material> materials = new LinkedList<>();

				// Sets the values of the Material objects' fields using the data retrieved from the
				// result set. It adds the objects to the materials list.
				while (resultSet.next()) {
					materials.add(extract(resultSet, Material.class));
				}

				return materials;
			}
		}
	}

	//Updates a project's data in the project table
	public boolean modifyProjectDetails(Project project) {
		// @formatter:off
		String sql = "UPDATE " + PROJECT_TABLE + " SET "
				+ "project_name = ?, "
				+ "estimated_hours = ?, "
				+ "actual_hours = ?, "
				+ "difficulty = ?, "
				+ "notes = ? "
				+ "WHERE project_id = ?";
		// @formatter:on

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement statement = conn.prepareStatement(sql)) {
				setParameter(statement, 1, project.getProjectName(), String.class);
				setParameter(statement, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(statement, 3, project.getActualHours(), BigDecimal.class);
				setParameter(statement, 4, project.getDifficulty(), Integer.class);
				setParameter(statement, 5, project.getNotes(), String.class);
				setParameter(statement, 6, project.getProjectId(), Integer.class);

				// Returns the number of rows where data was changed after the SQL statement was
				// executed. If it returns 1, the project was updated and the condition is true.
				boolean updated = statement.executeUpdate() == 1;

				commitTransaction(conn);

				// Returns a boolean value to confirm that the project data was updated. If it was not, it
				// returns false and an exception is thrown in the ProjectService class.
				return updated;

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}

		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	//Deletes a project in the project table
	public boolean deleteProject(int projectId) {
		String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement statement = conn.prepareStatement(sql)) {
				setParameter(statement, 1, projectId, Integer.class);

				// Returns the number of rows where data was changed after the SQL statement was
				// executed. If it returns 1, the project was deleted and the condition is true.
				boolean deleted = statement.executeUpdate() == 1;

				commitTransaction(conn);
				
				// Returns a boolean value to confirm that the project data was deleted. If it was not, it
				// returns false and an exception is thrown in the ProjectService class.
				return deleted;

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}

		} catch (SQLException e) {
			throw new DbException(e);
		}
	}
}
