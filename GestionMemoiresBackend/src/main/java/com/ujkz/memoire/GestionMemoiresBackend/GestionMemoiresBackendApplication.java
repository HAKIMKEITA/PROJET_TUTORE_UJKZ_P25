package com.ujkz.memoire.GestionMemoiresBackend;

import com.ujkz.memoire.GestionMemoiresBackend.entity.User;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Teacher;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Student;
import com.ujkz.memoire.GestionMemoiresBackend.entity.AcademicYear;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Semester;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Subject;
import com.ujkz.memoire.GestionMemoiresBackend.enums.UserRole;
import com.ujkz.memoire.GestionMemoiresBackend.enums.Grade;
import com.ujkz.memoire.GestionMemoiresBackend.enums.SubjectStatus;
import com.ujkz.memoire.GestionMemoiresBackend.repository.UserRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.TeacherRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.StudentRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.AcademicYearRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.SemesterRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@SpringBootApplication(scanBasePackages = "com.ujkz.memoire.GestionMemoiresBackend")
@EnableJpaRepositories(basePackages = "com.ujkz.memoire.GestionMemoiresBackend.repository")
@EntityScan(basePackages = "com.ujkz.memoire.GestionMemoiresBackend.entity")
public class GestionMemoiresBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionMemoiresBackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(
		UserRepository userRepository, 
		TeacherRepository teacherRepository, 
		StudentRepository studentRepository,
		AcademicYearRepository academicYearRepository,
		SemesterRepository semesterRepository,
		SubjectRepository subjectRepository,
		PasswordEncoder passwordEncoder
	) {
		return args -> {
			// Création de l'admin
			if (userRepository.count() == 0) {
				User admin = new User();
				admin.setNom("Admin");
				admin.setPrenom("System");
				admin.setEmail("admin@ujkz.bf");
				admin.setPassword(passwordEncoder.encode("admin123"));
				admin.setRole(UserRole.ADMINISTRATEUR);
				admin.setActif(true);
				userRepository.save(admin);
				System.out.println("✅ Compte admin créé : admin@ujkz.bf / admin123");
			}
			
			// Création d'un enseignant de test
			if (teacherRepository.count() == 0) {
				User teacherUser = new User();
				teacherUser.setNom("SOMDA");
				teacherUser.setPrenom("Flavien");
				teacherUser.setEmail("flavien.somda@ujkz.bf");
				teacherUser.setPassword(passwordEncoder.encode("password123"));
				teacherUser.setRole(UserRole.ENSEIGNANT);
				teacherUser.setActif(true);
				userRepository.save(teacherUser);
				
				Teacher teacher = new Teacher();
				teacher.setUser(teacherUser);
				teacher.setGrade(Grade.MAITRE_DE_CONFERENCES);
				teacher.setSpecialite("Informatique");
				teacher.setActif(true);
				teacherRepository.save(teacher);

				System.out.println("✅ Enseignant créé : " + teacherUser.getPrenom() + " " + teacherUser.getNom() + " (Grade: Maître de Conférences)");
			}
			
			// Création d'un étudiant de test
			if (studentRepository.count() == 0) {
				User studentUser = new User();
				studentUser.setNom("Traoré");
				studentUser.setPrenom("Amadou");
				studentUser.setEmail("amadou.traore@ujkz.bf");
				studentUser.setPassword(passwordEncoder.encode("password123"));
				studentUser.setRole(UserRole.ETUDIANT);
				studentUser.setActif(true);
				userRepository.save(studentUser);
				
				Student student = new Student();
				student.setUser(studentUser);
				student.setMatricule("2025M001");
				student.setPromotion("2025-2026");
				student.setMasterSpecialite("Informatique");
				student.setSemestre("Semestre 1");
				student.setActif(true);
				studentRepository.save(student);
				
				System.out.println("✅ Étudiant créé : " + studentUser.getPrenom() + " " + studentUser.getNom() + " (Matricule: 2025M001)");
			}
			
			// Création de l'année académique
			if (academicYearRepository.count() == 0) {
				AcademicYear year = new AcademicYear();
				year.setLibelle("2025-2026");
				year.setDateDebut(LocalDate.of(2025, 10, 1));
				year.setDateFin(LocalDate.of(2026, 6, 30));
				year.setActif(true);
				academicYearRepository.save(year);
				System.out.println("✅ Année académique créée : 2025-2026");
			}
			
			// Création du semestre
			if (semesterRepository.count() == 0) {
				AcademicYear year = academicYearRepository.findByActifTrue().orElse(null);
				if (year != null) {
					Semester semester = new Semester();
					semester.setLibelle("Semestre 1");
					semester.setAcademicYear(year);
					semester.setActif(true);
					semesterRepository.save(semester);
					System.out.println("✅ Semestre créé : Semestre 1 (2025-2026)");
				}
			}
			
			// Création d'un sujet de test
			if (subjectRepository.count() == 0) {
				Teacher encadrant = teacherRepository.findAll().get(0);
				Teacher superviseur = teacherRepository.findAll().get(0);
				Semester semester = semesterRepository.findAll().get(0);
				
				Subject subject = new Subject();
				subject.setTitre("Développement d'une plateforme de gestion de mémoires");
				subject.setResume("Conception et développement d'une application web pour gérer le cycle de vie des mémoires de Master");
				subject.setObjectifs("Centraliser la gestion des sujets, candidatures, suivis et soutenances");
				subject.setCompetencesRequises("Java, Spring Boot, React, PostgreSQL");
				subject.setMotsCles("API REST, Spring Boot, Mémoire, Soutenance");
				subject.setEncadrant(encadrant);
				subject.setSuperviseur(superviseur);
				subject.setSemestre(semester);
				subject.setCapaciteMax(1);
				subject.setStatut(SubjectStatus.OUVERT);
				subject.setPublie(true);
				subjectRepository.save(subject);
				System.out.println("✅ Sujet de mémoire créé : " + subject.getTitre());
			}
		};
	}
}