package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {

  private ProjectDao projectDao = new ProjectDao();

  public Project addProject(Project project) {
    return projectDao.insertProject(project);
  }

  public List<Project> fetchAllProjects() {
    return projectDao.fetchAllProjects();
  }

  /**
   * Calls on the DAO layer to fetch a project by the project_id value. If a project with a matching
   * project_id value is not found, throws a NoSuchElementException.
   * 
   * @param projectId
   * @return
   */
  public Project fetchProjectById(Integer projectId) {
    return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException(
        "Project with project ID = " + projectId + " does not exist."));
  }

  /**
   * Calls the DAO to update the project details. It checks for a true or false value because it
   * calls on the DAO layer projectDao.modifyProjectDetails() method, which returns a boolean true
   * if the update was successfull or a false if it was not. If the value is false, it throws a
   * DbException.
   * 
   * @param project
   */
  public void modifyProjectDetails(Project project) {
    if (!projectDao.modifyProjectDetails(project)) {
      throw new DbException(
          "Project with ID= " + project.getProjectId() + " does not exist in the database.");
    } ;
  }

  /**
   * Calls the DAO to delete the project matching the project ID. It checks for a true or false
   * value since it calls on the DAO layer projectDao.deleteProject method, which returns a boolean
   * true if the delete was successfull or a false if it was not. If the value is false, it throws a
   * DbException.
   * 
   * @param project
   */
  public void deleteProject(Integer projectId) {
    if (!projectDao.deleteProject(projectId)) {
      throw new DbException("Project with ID= " + projectId + " does not exist in the database.");
    } ;
  }

}
