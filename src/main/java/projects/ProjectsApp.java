package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
  private Scanner scanner = new Scanner(System.in);
  private ProjectService projectService = new ProjectService();
  private Project curProject;

  //@formatter:off 
  private List<String> operations = List.of(
        "1) Add a project",
        "2) List projects",
        "3) Select a project"
  );
  // @formatter:on   


  /**
   * Main method, entry point for the application.
   * 
   * @param args
   */
  public static void main(String[] args) {

    new ProjectsApp().processUserSelections();
  }


  /**
   * Displays available options and calls the getUserSelection() method to collect the user
   * selection and execute it.
   */
  private void processUserSelections() {
    boolean done = false;
    while (!done) {
      try {
        int selection = getUserSelection();

        switch (selection) {
          case -1:
            done = exitMenu();
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

          default:
            System.out.println("\n" + selection + " is not a valid selection. Try again.");
        }
      } catch (Exception e) {
        System.out.println("\nError: " + e + " Try again.");
      }
    }
  }


  /**
   * Used to print a prompt to the user in the console to make a selection and calls the
   * getIntInput() method.
   * 
   * @return int with user selection.
   */
  private int getUserSelection() {
    printOperations();

    Integer input = getIntInput("Enter a menu selection");
    return Objects.isNull(input) ? -1 : input;
  }


  /**
   * This method prints available selections in the menu or prints information about a project that was previously selected.
   */
  private void printOperations() {
    System.out.println("\nThese are the available selections. Press the Enter key to quit:");
    for (String line : operations) {
      System.out.println("  " + line);
    }
    
    if (Objects.isNull(curProject)) {
      System.out.println("\nYou are not working with a project.");
    } else {
      System.out.println("\nYou are working with project: " + curProject);
    }
  }


  /*
   * Method called in the processUserSelections() method to exit the loop that keeps the menu open
   * and the application running.
   */
  private boolean exitMenu() {
    System.out.println("Exiting the menu...");
    return true;
  }


  /**
   * Creates a project in the database without categories, materials or steps.
   */
  private void createProject() {
    String projectName = getStringInput("Enter the project name");
    BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
    BigDecimal actualHours = getDecimalInput("Enter the actual hours");
    Integer difficulty = getIntInput("Enter the project difficulty(1-5)");
    String notes = getStringInput("Enter the project notes");

    Project project = new Project();

    project.setProjectName(projectName);
    project.setEstimatedHours(estimatedHours);
    project.setActualHours(actualHours);
    project.setDifficulty(difficulty);
    project.setNotes(notes);

    Project dbProject = projectService.addProject(project);
    System.out.println("Project " + dbProject + " successfully created.");
  }

  /**
   * Lists projects in the database without categories, materials or steps.
   */
  private void listProjects() {
    List<Project> projects = projectService.fetchAllProjects();
    System.out.println("\nProjects:");
    projects.forEach(project -> System.out
        .println("  " + project.getProjectId() + ": " + project.getProjectName()));
  }


  /**
   * Calls listProjects() to List all projects IDs and names and lets the user select a specific
   * project by the project ID.
   */
  private void selectProject() {
    listProjects();
    Integer projectId = getIntInput("Enter a project ID to select a project");
    curProject = null;
    curProject = projectService.fetchProjectById(projectId);
    
    /*Not sure if this is required. TDB*/
    if (Objects.isNull(curProject)) {
      System.out.println("\nInvalid project ID selected.");
    }

  }


  /**
   * Used to collect an alphanumerical input from the user via the console. User inputs are stored
   * as a String.
   * 
   * @param String containing the prompt to be printed to the console.
   * @return String with input from user.
   */
  private String getStringInput(String prompt) {
    System.out.print(prompt + ": ");
    String input = scanner.nextLine();

    return input.isBlank() ? null : input.trim();
  }

  /**
   * Converts a string into an Integer. Used to convert user inputs that were originally stored as a
   * string into an integer and enable the user to make numerical selections from the menu.
   * 
   * @param String with the prompt to be displayed to the user
   * @return integer with the user input number selection
   */
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


  /**
   * Converts the string collected from user by the method getStringInput() into a decimal number.
   * 
   * @param String with prompt to be printed to the console.
   * @return BigDecimal
   */
  private BigDecimal getDecimalInput(String prompt) {
    String input = getStringInput(prompt);
    if (Objects.isNull(input)) {
      return null;
    }
    try {
      return new BigDecimal(input).setScale(2);
    } catch (NumberFormatException e) {
      throw new DbException(input + " is not a valid decimal number.");
    }
  }



}
