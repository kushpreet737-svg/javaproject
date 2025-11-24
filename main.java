import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Project Builder - Single File Version
 * Fully runnable in any Java compiler.
 * Generates README.md & statement.md under /output/<ProjectName>
 */

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Project Builder (Single File Version) ===");

        System.out.print("Enter your name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter your roll number: ");
        String roll = sc.nextLine().trim();

        User user = new User(name, roll);

        ProjectService projectService = new ProjectService(user, sc);
        Project project = projectService.createProject();

        projectService.addModules(project);
        projectService.addRequirements(project);

        ReportGenerator.generate(project);

        System.out.println("\nProject files generated successfully!");
        System.out.println("Check the folder: output/" + project.getTitle().replace(" ", "_"));
    }

    // ============================= MODELS =============================

    static class User {
        private final String name;
        private final String roll;
        private final String course = "Computer Engineering 3rd Semester";

        public User(String name, String roll) {
            this.name = name;
            this.roll = roll;
        }

        public String getName() { return name; }
        public String getRoll() { return roll; }
        public String getCourse() { return course; }
    }

    static class ModuleItem {
        private final String title;
        private final String description;

        public ModuleItem(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
    }

    static class Project {
        private String title;
        private String problemStatement;
        private String scope;
        private final User user;

        private final List<ModuleItem> modules = new ArrayList<>();
        private final List<String> functionalReq = new ArrayList<>();
        private final List<String> nonFunctionalReq = new ArrayList<>();

        public Project(User user) {
            this.user = user;
        }

        public String getTitle() { return title; }
        public void setTitle(String t) { this.title = t; }

        public String getProblemStatement() { return problemStatement; }
        public void setProblemStatement(String ps) { this.problemStatement = ps; }

        public String getScope() { return scope; }
        public void setScope(String scope) { this.scope = scope; }

        public List<ModuleItem> getModules() { return modules; }
        public List<String> getFunctionalReq() { return functionalReq; }
        public List<String> getNonFunctionalReq() { return nonFunctionalReq; }

        public User getUser() { return user; }
    }

    // ============================= SERVICES =============================

    static class ProjectService {

        private final User user;
        private final Scanner sc;

        public ProjectService(User user, Scanner sc) {
            this.user = user;
            this.sc = sc;
        }

        public Project createProject() {
            Project p = new Project(user);

            System.out.println("\n--- Enter Project Details ---");

            System.out.print("Project Title: ");
            p.setTitle(sc.nextLine().trim());

            System.out.print("Problem Statement: ");
            p.setProblemStatement(sc.nextLine().trim());

            System.out.print("Project Scope: ");
            p.setScope(sc.nextLine().trim());

            return p;
        }

        public void addModules(Project p) {
            System.out.println("\n--- Add Modules ---");
            while (true) {
                System.out.print("Module Title (or 'done'): ");
                String title = sc.nextLine().trim();

                if (title.equalsIgnoreCase("done")) break;

                System.out.print("Module Description: ");
                String desc = sc.nextLine().trim();

                p.getModules().add(new ModuleItem(title, desc));
            }

            // Ensure minimum 3 modules
            if (p.getModules().size() < 3) {
                p.getModules().add(new ModuleItem("User Management", "Handles basic user details"));
                p.getModules().add(new ModuleItem("Project Management", "Stores project information"));
                p.getModules().add(new ModuleItem("Report Generator", "Creates README & statement files"));
            }
        }

        public void addRequirements(Project p) {
            System.out.println("\n--- Add Functional Requirements ---");

            while (true) {
                System.out.print("Functional Requirement (or 'done'): ");
                String r = sc.nextLine().trim();

                if (r.equalsIgnoreCase("done")) break;
                if (!r.isEmpty()) p.getFunctionalReq().add(r);
            }

            if (p.getFunctionalReq().isEmpty()) {
                p.getFunctionalReq().add("User can create/view project details");
                p.getFunctionalReq().add("System generates README.md & statement.md");
            }

            System.out.println("\n--- Add Non-Functional Requirements ---");

            while (true) {
                System.out.print("Non-Functional Requirement (or 'done'): ");
                String r = sc.nextLine().trim();

                if (r.equalsIgnoreCase("done")) break;
                if (!r.isEmpty()) p.getNonFunctionalReq().add(r);
            }

            if (p.getNonFunctionalReq().isEmpty()) {
                p.getNonFunctionalReq().add("Simple console-based application");
                p.getNonFunctionalReq().add("Generates structured markdown files");
            }
        }
    }

    // ============================= REPORT GENERATOR =============================

    static class ReportGenerator {

        public static void generate(Project p) {
            String folderName = p.getTitle().replaceAll("[^a-zA-Z0-9_ ]", "").replace(" ", "_");
            Path base = Path.of("output", folderName);

            try {
                Files.createDirectories(base);

                Files.writeString(
                        base.resolve("README.md"),
                        buildReadme(p),
                        StandardCharsets.UTF_8
                );

                Files.writeString(
                        base.resolve("statement.md"),
                        buildStatement(p),
                        StandardCharsets.UTF_8
                );

            } catch (IOException e) {
                System.out.println("ERROR WRITING FILES: " + e.getMessage());
            }
        }

        private static String buildReadme(Project p) {
            StringBuilder sb = new StringBuilder();

            sb.append("# ").append(p.getTitle()).append("\n\n");
            sb.append("*Author:* ").append(p.getUser().getName()).append("\n");
            sb.append("*Roll No:* ").append(p.getUser().getRoll()).append("\n");
            sb.append("*Course:* ").append(p.getUser().getCourse()).append("\n\n");

            sb.append("## Problem Statement\n").append(p.getProblemStatement()).append("\n\n");
            sb.append("## Project Scope\n").append(p.getScope()).append("\n\n");

            sb.append("## Modules\n");
            for (ModuleItem m : p.getModules()) {
                sb.append("- *").append(m.getTitle()).append("*: ").append(m.getDescription()).append("\n");
            }

            sb.append("\n## Functional Requirements\n");
            for (String r : p.getFunctionalReq()) sb.append("- ").append(r).append("\n");

            sb.append("\n## Non-Functional Requirements\n");
            for (String r : p.getNonFunctionalReq()) sb.append("- ").append(r).append("\n");

            return sb.toString();
        }

        private static String buildStatement(Project p) {
            return  "# Project Statement\n\n" +
                    "*Title:* " + p.getTitle() + "\n\n" +
                    "*Problem Statement:*\n" + p.getProblemStatement() + "\n\n" +
                    "*Scope:*\n" + p.getScope() + "\n\n" +
                    "*Modules:*\n" +
                    listModules(p.getModules()) +
                    "\n*Author:* " + p.getUser().getName() +
                    " (" + p.getUser().getRoll() + ")";
        }

        private static String listModules(List<ModuleItem> list) {
            StringBuilder sb = new StringBuilder();
            for (ModuleItem m : list) {
                sb.append("- ").append(m.getTitle()).append(": ").append(m.getDescription()).append("\n");
            }
            return sb.toString();
        }
    }
}
