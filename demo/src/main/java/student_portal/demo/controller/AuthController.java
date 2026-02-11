package student_portal.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import student_portal.demo.entity.*;
import student_portal.demo.repository.*;
import student_portal.demo.service.StudentService;

@Controller
public class AuthController {

    // ================== AUTOWIRED ==================

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private SkillRepository skillRepo;

    @Autowired
    private CertificationRepository certificationRepo;

    @Autowired
    private ResumeRepository resumeRepo;

    // ================== STUDENT AUTH ==================

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("student", new Student());
        return "register";
    }

    @PostMapping("/register")
    public String registerStudent(
            @Valid @ModelAttribute Student student,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "register";
        }

        studentService.register(student);

        return "login";
    }

    @PostMapping("/login")
    public String loginStudent(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        Student student = studentRepo.findByEmailAndPassword(email, password);

        if (student == null) {
            model.addAttribute("error", "Invalid Email or Password");
            return "login";
        }

        session.setAttribute("student", student);
        return "redirect:/dashboard";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ================== DASHBOARD & HOME ==================

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        Student student = (Student) session.getAttribute("student");
        if (student == null) return "redirect:/";

        model.addAttribute("student", student);
        model.addAttribute("projects", projectRepo.findByStudent(student));
        model.addAttribute("skills", skillRepo.findByStudent(student));
        model.addAttribute("certifications", certificationRepo.findByStudent(student));
        model.addAttribute("resume", resumeRepo.findByStudent(student));

        return "dashboard";
    }

    @GetMapping("/home")
    public String redirectHome(HttpSession session) {
        Student student = (Student) session.getAttribute("student");
        return "redirect:/home/" + student.getUserId();
    }

    @GetMapping("/home/{userId}")
    public String homePage(@PathVariable String userId, Model model) {

        Student student = studentRepo.findByUserId(userId);

        model.addAttribute("student", student);
        model.addAttribute("projects", projectRepo.findByStudent(student));
        model.addAttribute("skills", skillRepo.findByStudent(student));
        model.addAttribute("certifications", certificationRepo.findByStudent(student));
        model.addAttribute("resume", resumeRepo.findByStudent(student));

        return "home";
    }

    // ================== SKILLS ==================

    @PostMapping("/add-skill")
    public String addSkill(@RequestParam String skillName,
                           HttpSession session) {

        Student student = (Student) session.getAttribute("student");

        Skill skill = new Skill();
        skill.setSkillName(skillName);
        skill.setStudent(student);

        skillRepo.save(skill);
        return "redirect:/dashboard";
    }

    // ================== PROJECTS ==================

    @GetMapping("/add-project")
    public String addProjectPage(Model model) {
        model.addAttribute("project", new Project());
        return "add-project";
    }

    @PostMapping("/save-project")
    public String saveProject(Project project, HttpSession session) {

        Student student = (Student) session.getAttribute("student");
        project.setStudent(student);
        projectRepo.save(project);

        return "redirect:/dashboard";
    }

    @GetMapping("/project/{id}")
    public String projectDetails(@PathVariable int id, Model model) {

        Project project = projectRepo.findById(id).get();
        model.addAttribute("project", project);

        return "project-details";
    }

    // ================== CERTIFICATIONS ==================

    @GetMapping("/add-certification")
    public String addCertificationPage(Model model) {
        model.addAttribute("certification", new Certification());
        return "add-certification";
    }

    @PostMapping("/save-certification")
    public String saveCertification(
            Certification certification,
            @RequestParam("pdfFile") MultipartFile file,
            HttpSession session) throws IOException {

        Student student = (Student) session.getAttribute("student");

        String uploadDir = "uploads/student-certificates/";
Files.createDirectories(Paths.get(uploadDir));   // create folder if not exists
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + fileName);
        Files.write(path, file.getBytes());

        certification.setPdfPath(fileName);
        certification.setIssuedDate(LocalDate.now());
        certification.setStudent(student);

        certificationRepo.save(certification);
        return "redirect:/dashboard";
    }

    @GetMapping("/certification/pdf/{id}")
    public ResponseEntity<byte[]> viewCertificate(@PathVariable int id) throws IOException {

        Certification cert = certificationRepo.findById(id).orElse(null);

        if (cert == null) {
            return ResponseEntity.notFound().build();
        }

        Path fullPath = Paths.get("uploads/student-certificates/" + cert.getPdfPath());


        if (!Files.exists(fullPath)) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdfBytes = Files.readAllBytes(fullPath);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + fullPath.getFileName().toString() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }


    // ================== RESUME ==================

    @GetMapping("/add-resume")
    public String addResumePage() {
        return "add-resume";
    }

    @PostMapping("/save-resume")
public String saveResume(
        @RequestParam("resumeFile") MultipartFile file,
        HttpSession session) throws IOException {

    Student student = (Student) session.getAttribute("student");

    if (student == null) return "redirect:/";

    String uploadDir = "uploads/student-resumes/";
    Files.createDirectories(Paths.get(uploadDir));   // create folder if not exists

    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
    Path path = Paths.get(uploadDir + fileName);

    Files.write(path, file.getBytes());

    Resume resume = resumeRepo.findByStudent(student);
    if (resume == null) {
        resume = new Resume();
        resume.setStudent(student);
    }

    resume.setFileName(fileName);
    resume.setUploadedDate(LocalDate.now());
    resumeRepo.save(resume);

    return "redirect:/dashboard";
}

    @GetMapping("/resume/{studentId}")
public ResponseEntity<byte[]> viewResume(@PathVariable int studentId) throws IOException {

    Student student = studentRepo.findById(studentId).orElse(null);
    if (student == null) return ResponseEntity.notFound().build();

    Resume resume = resumeRepo.findByStudent(student);
    if (resume == null) return ResponseEntity.notFound().build();

    Path fullPath = Paths.get("uploads/student-resumes/" + resume.getFileName());

    if (!Files.exists(fullPath)) {
        return ResponseEntity.notFound().build();
    }

    byte[] fileBytes = Files.readAllBytes(fullPath);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + fullPath.getFileName().toString() + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(fileBytes);
}



    // ================== FORGOT PASSWORD ==================





    // ================== ADMIN ==================

    @GetMapping("/admin")
    public String adminLoginPage() {
        return "admin-login";
    }

    @PostMapping("/admin-login")
    public String adminLogin(@RequestParam String username,
                             @RequestParam String password,
                             HttpSession session,
                             Model model) {

        if ("admin".equals(username) && "admin123".equals(password)) {
            session.setAttribute("admin", "ADMIN");
            return "redirect:/admin-dashboard";
        }

        model.addAttribute("error", "Invalid admin credentials");
        return "admin-login";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session, Model model) {

        if (session.getAttribute("admin") == null) return "redirect:/admin";

        model.addAttribute("students", studentRepo.findAll());
        return "admin-dashboard";
    }

    @GetMapping("/admin/student/{id}")
    public String adminStudentDetails(@PathVariable int id,
                                      HttpSession session,
                                      Model model) {

        if (session.getAttribute("admin") == null) return "redirect:/admin";

        Student student = studentRepo.findById(id).get();

        model.addAttribute("student", student);
        model.addAttribute("projects", projectRepo.findByStudent(student));
        model.addAttribute("skills", skillRepo.findByStudent(student));
        model.addAttribute("certifications", certificationRepo.findByStudent(student));
        model.addAttribute("resume", resumeRepo.findByStudent(student));

        return "admin-student-details";
    }
}

