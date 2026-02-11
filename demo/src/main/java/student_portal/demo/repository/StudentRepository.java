package student_portal.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import student_portal.demo.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    long countByBranch(String branch);

    Student findByUserId(String userId);

    Student findByUserIdAndPassword(String userId, String password);

    Student findByUserIdAndEmail(String userId, String email);
}

