package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
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

public class ProjectDao extends DaoBase {
  private static final String CATEGORY_TABLE = "category";
  private static final String MATERIAL_TABLE = "material";
  private static final String PROJECT_TABLE = "project";
  private static final String PROJECT_CATEGORY_TABLE = "project_category";
  private static final String STEP_TABLE = "step";


  /**
   * Inserts a project in the database then returns a Project object with that information. It sets
   * the values in the databse for the columns project_name, estimated_hours, actual_hours,
   * difficulty and notes but it fetches the Integer found in project_id (primary key, set
   * automatically by MySQL) from the database and sets it to the Project object Integer projectId.
   * 
   * @return a Project object
   */
  public Project insertProject(Project project) {
    // @formatter:off
    String sql = ""
        + "INSERT INTO " + PROJECT_TABLE + " "
        + "(project_name, estimated_hours, actual_hours, difficulty, notes) "
        + "VALUES "
        + "(?, ?, ?, ?, ?)";
    // @formatter:on

    try (Connection conn = DbConnection.getConnection()) {
      startTransaction(conn);

      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameter(stmt, 1, project.getProjectName(), String.class);
        setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
        setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
        setParameter(stmt, 4, project.getDifficulty(), Integer.class);
        setParameter(stmt, 5, project.getNotes(), String.class);

        stmt.executeUpdate();

        Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
        commitTransaction(conn);

        project.setProjectId(projectId);
        return project;
      } catch (Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    } catch (SQLException e) {
      throw new DbException(e);
    }
  }


  /**
   * Fetches all projects in the database. Returns a a list with all projects found in the
   * PROJECT_TABLE ordered by their names.
   * 
   * @return a list of objects of the class Project
   */
  public List<Project> fetchAllProjects() {
    String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";

    try (Connection conn = DbConnection.getConnection()) {
      startTransaction(conn);

      try (PreparedStatement stmt = conn.prepareStatement(sql)) {

        try (ResultSet rs = stmt.executeQuery()) {
          List<Project> projects = new LinkedList<>();

          while (rs.next()) {
            projects.add(extract(rs, Project.class));
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

  /**
   * Returns a row from the project table for a specific project by its project ID, and calls 3
   * methods to fetch the materials, steps and categories in their respective tables.
   * 
   * @param Integer projectId that was previously selected by the user
   * @return Optional project
   */

  public Optional<Project> fetchProjectById(Integer projectId) {
    String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";

    try (Connection conn = DbConnection.getConnection()) {
      startTransaction(conn);

      try {
        Project project = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
          setParameter(stmt, 1, projectId, Integer.class);

          try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
              project = extract(rs, Project.class);
            }
          }
        }

        if (Objects.nonNull(project)) {
          project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
          project.getSteps().addAll(fetchStepsForProject(conn, projectId));
          project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
        }

        commitTransaction(conn);

        return Optional.ofNullable(project);

      } catch (Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }


    } catch (SQLException e) {
      throw new DbException(e);
    }

  }


  /*
   * Fetches the categories of a specific project
   * 
   * @param current connection as well as the project id of the project for which the categories are
   * to be fetched
   * 
   * @return a list of the categories for that project.
   */
  private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId)
      throws SQLException {

    // @formatter off
    String sql = "SELECT c.* FROM " + CATEGORY_TABLE + " c " + "JOIN " + PROJECT_CATEGORY_TABLE
        + " pc USING (category_id) " + "WHERE project_id = ?";

    // @formatter on

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      setParameter(stmt, 1, projectId, Integer.class);

      try (ResultSet rs = stmt.executeQuery()) {
        List<Category> categories = new LinkedList<>();

        while (rs.next()) {
          categories.add(extract(rs, Category.class));
        }

        return categories;
      }
    }
  }


  /*
   * Fetches the steps needed for a specific project
   * 
   * @param current connection as well as the project id of the project for which the steps are to
   * be fetched
   * 
   * @return a list of the steps for that project.
   */
  private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
    String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      setParameter(stmt, 1, projectId, Integer.class);

      try (ResultSet rs = stmt.executeQuery()) {
        List<Step> steps = new LinkedList<>();

        while (rs.next()) {
          steps.add(extract(rs, Step.class));
        }

        return steps;
      }
    }
  }


  /*
   * Fetches the materials needed for a specific project
   * 
   * @param current connection as well as the project id of the project for which the materials are
   * to be fetched
   * 
   * @return a list of the materials for that project.
   */
  private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId)
      throws SQLException {
    String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      setParameter(stmt, 1, projectId, Integer.class);

      try (ResultSet rs = stmt.executeQuery()) {
        List<Material> materials = new LinkedList<>();

        while (rs.next()) {
          materials.add(extract(rs, Material.class));
        }

        return materials;
      }
    }

  }

  /**
   * Updates a project row in database for selected project. To achieve that this method passes the
   * integer found in the selected project object projectId in the WHERE clause for the column
   * project_id in the PROJECT_TABLE. It then returns true if a matching project was found and
   * updated (if the statement was executed and 1 row affected in the database) or false if not.
   * 
   * @param project
   * @return boolean
   */
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

      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameter(stmt, 1, project.getProjectName(), String.class);
        setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
        setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
        setParameter(stmt, 4, project.getDifficulty(), Integer.class);
        setParameter(stmt, 5, project.getNotes(), String.class);
        setParameter(stmt, 6, project.getProjectId(), Integer.class);

        boolean modified = stmt.executeUpdate() == 1;
        commitTransaction(conn);

        return modified;

      } catch (Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    } catch (SQLException e) {
      throw new DbException(e);
    }
  }

  /**
   * Deletes the project row in database for the selected project. This method passes the integer
   * projectId in the WHERE clause for the column project_id in the PROJECT_TABLE. It then returns
   * true if a matching project was found and deleted (if the statement was executed and 1 row
   * affected in the database) or false if not.
   * 
   * @param Integer projectId
   * @return boolean
   */
  public boolean deleteProject(Integer projectId) {
    String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";

    try (Connection conn = DbConnection.getConnection()) {
      startTransaction(conn);

      try (PreparedStatement stmt = conn.prepareStatement(sql)) {

        setParameter(stmt, 1, projectId, Integer.class);

        boolean deleted = stmt.executeUpdate() == 1;
        commitTransaction(conn);

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
