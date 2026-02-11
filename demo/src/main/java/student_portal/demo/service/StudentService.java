package student_portal.demo.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import student_portal.demo.entity.Student;
import student_portal.demo.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private JavaMailSender mailSender;

    // ================== STUDENT REGISTRATION ==================
    // Generates branch-wise UserID and sends mail

    public Student register(Student student) {

        long count = studentRepo.countByBranch(student.getBranch());
        String userId = student.getBranch() + (count + 1);

        student.setUserId(userId);

        Student savedStudent = studentRepo.save(student);

        sendUserIdMail(savedStudent.getEmail(), savedStudent.getUserId());

        return savedStudent;
    }

    // ================== EMAIL USER ID ==================

    private void sendUserIdMail(String toEmail, String userId) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Student Portal - Your User ID");
        message.setText("Welcome!\n\nYour User ID is: " + userId
                + "\n\nUse this User ID to login.");

        mailSender.send(message);
    }

    // ================== OTP GENERATION ==================

    public int generateOtp() {
        Random random = new Random();
        return random.nextInt(900000) + 100000; // 6-digit OTP
    }

    // ================== SEND OTP ==================

    public void sendOtp(String toEmail, int otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Student Portal - Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp);

        mailSender.send(message);
    }
}

