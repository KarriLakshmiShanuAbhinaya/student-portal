package student_portal.demo.service;



import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import student_portal.demo.entity.Student;
import student_portal.demo.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepo;



    // ================== STUDENT REGISTRATION ==================
    // Generates branch-wise UserID and sends mail

    public Student register(Student student) {

        long count = studentRepo.countByBranch(student.getBranch());
        String userId = student.getBranch() + (count + 1);

        student.setUserId(userId);

        Student savedStudent = studentRepo.save(student);


        return savedStudent;
    }

    // ================== EMAIL USER ID ==================



    // ================== OTP GENERATION ==================


    // ================== SEND OTP ==================


}

