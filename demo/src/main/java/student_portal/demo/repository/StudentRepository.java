package student_portal.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import student_portal.demo.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    Student findByEmailAndPassword(String email, String password);

    Student findByUserId(String userId);

    long countByBranch(String branch);
}

