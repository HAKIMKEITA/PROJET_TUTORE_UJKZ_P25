package com.ujkz.memoire.GestionMemoiresBackend;

import com.ujkz.memoire.GestionMemoiresBackend.entity.*;
import com.ujkz.memoire.GestionMemoiresBackend.enums.*;
import com.ujkz.memoire.GestionMemoiresBackend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        ApplicationRepository applicationRepository,
        MemoireRepository memoireRepository,
        MilestoneRepository milestoneRepository,
        DocumentRepository documentRepository,
        ObservationRepository observationRepository,
        CampagneRepository campagneRepository,
        DefenseSessionRepository defenseSessionRepository,
        DefenseRepository defenseRepository,
        JuryRepository juryRepository,
        JuryMemberRepository juryMemberRepository,
        DefenseObservationRepository defenseObservationRepository,
        GradeMemoireRepository gradeMemoireRepository,
        PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // ========================================
            // 1. CRÉATION DE L'ADMINISTRATEUR
            // ========================================
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

            // ========================================
            // 2. CRÉATION D'UN ENSEIGNANT
            // ========================================
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

            // ========================================
            // 3. CRÉATION D'UN ÉTUDIANT
            // ========================================
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

            // ========================================
            // 4. CRÉATION D'UN AUTRE ENSEIGNANT POUR SUPERVISEUR
            // ========================================
            Teacher superviseur = null;
            if (teacherRepository.count() < 2) {
                User superviseurUser = new User();
                superviseurUser.setNom("OUEDRAOGO");
                superviseurUser.setPrenom("Moussa");
                superviseurUser.setEmail("moussa.ouedraogo@ujkz.bf");
                superviseurUser.setPassword(passwordEncoder.encode("password123"));
                superviseurUser.setRole(UserRole.ENSEIGNANT);
                superviseurUser.setActif(true);
                userRepository.save(superviseurUser);

                superviseur = new Teacher();
                superviseur.setUser(superviseurUser);
                superviseur.setGrade(Grade.PROFESSEUR);
                superviseur.setSpecialite("Informatique");
                superviseur.setActif(true);
                teacherRepository.save(superviseur);

                System.out.println("✅ Superviseur créé : " + superviseurUser.getPrenom() + " " + superviseurUser.getNom() + " (Grade: Professeur)");
            } else {
                superviseur = teacherRepository.findAll().get(1);
            }

            // ========================================
            // 5. CRÉATION DE L'ANNÉE ACADÉMIQUE
            // ========================================
            if (academicYearRepository.count() == 0) {
                AcademicYear year = new AcademicYear();
                year.setLibelle("2025-2026");
                year.setDateDebut(LocalDate.of(2025, 10, 1));
                year.setDateFin(LocalDate.of(2026, 6, 30));
                year.setActif(true);
                academicYearRepository.save(year);
                System.out.println("✅ Année académique créée : 2025-2026");
            }

            // ========================================
            // 6. CRÉATION DU SEMESTRE
            // ========================================
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

            // ========================================
            // 7. CRÉATION D'UN SUJET DE MÉMOIRE
            // ========================================
            if (subjectRepository.count() == 0) {
                Teacher encadrant = teacherRepository.findAll().get(0);
                Teacher sup = superviseur != null ? superviseur : teacherRepository.findAll().get(0);
                Semester semester = semesterRepository.findAll().get(0);

                Subject subject = new Subject();
                subject.setTitre("Développement d'une plateforme de gestion de mémoires");
                subject.setResume("Conception et développement d'une application web pour gérer le cycle de vie des mémoires de Master");
                subject.setObjectifs("Centraliser la gestion des sujets, candidatures, suivis et soutenances");
                subject.setCompetencesRequises("Java, Spring Boot, React, PostgreSQL");
                subject.setMotsCles("API REST, Spring Boot, Mémoire, Soutenance");
                subject.setEncadrant(encadrant);
                subject.setSuperviseur(sup);
                subject.setSemestre(semester);
                subject.setCapaciteMax(1);
                subject.setStatut(SubjectStatus.OUVERT);
                subject.setPublie(true);
                subjectRepository.save(subject);
                System.out.println("✅ Sujet de mémoire créé : " + subject.getTitre());
            }

            // ========================================
            // 8. CRÉATION D'UNE CANDIDATURE ET D'UN MÉMOIRE
            // ========================================
            if (subjectRepository.count() > 0 && applicationRepository.count() == 0) {
                Student student = studentRepository.findAll().get(0);
                Subject subject = subjectRepository.findAll().get(0);

                // Créer une candidature acceptée
                Application application = new Application();
                application.setStudent(student);
                application.setSubject(subject);
                application.setMotivation("Je suis très motivé pour travailler sur ce sujet de recherche innovant.");
                application.setStatut(ApplicationStatus.ACCEPTED);
                applicationRepository.save(application);
                System.out.println("✅ Candidature acceptée créée pour l'étudiant");

                // Créer le mémoire
                Memoire memoire = new Memoire();
                memoire.setApplication(application);
                memoire.setStudent(student);
                memoire.setSubject(subject);
                memoire.setStatutAvancement(AvancementStatus.EN_COURS);
                memoire.setSoutenable(false);
                memoireRepository.save(memoire);

                System.out.println("✅ Mémoire créé pour l'étudiant : " + student.getUser().getPrenom() + " " + student.getUser().getNom());
            }

            // ========================================
            // 9. CRÉATION DES JALONS DE SUIVI (MILESTONES)
            // ========================================
            if (milestoneRepository.count() == 0) {
                Memoire memoire = memoireRepository.findAll().stream().findFirst().orElse(null);
                if (memoire != null) {
                    Milestone milestone1 = new Milestone();
                    milestone1.setMemoire(memoire);
                    milestone1.setLibelle("Validation du plan");
                    milestone1.setDescription("Soumettre le plan détaillé du mémoire pour validation par l'encadrant");
                    milestone1.setEcheance(LocalDateTime.now().plusDays(30));
                    milestone1.setRealise(false);
                    milestone1.setOrdre(1);
                    milestoneRepository.save(milestone1);

                    Milestone milestone2 = new Milestone();
                    milestone2.setMemoire(memoire);
                    milestone2.setLibelle("Dépôt du rapport intermédiaire");
                    milestone2.setDescription("Soumettre la première version complète du rapport d'avancement");
                    milestone2.setEcheance(LocalDateTime.now().plusDays(60));
                    milestone2.setRealise(false);
                    milestone2.setOrdre(2);
                    milestoneRepository.save(milestone2);

                    Milestone milestone3 = new Milestone();
                    milestone3.setMemoire(memoire);
                    milestone3.setLibelle("Dépôt du mémoire final");
                    milestone3.setDescription("Soumettre la version finale du mémoire prête pour la soutenance");
                    milestone3.setEcheance(LocalDateTime.now().plusDays(90));
                    milestone3.setRealise(false);
                    milestone3.setOrdre(3);
                    milestoneRepository.save(milestone3);

                    System.out.println("✅ 3 jalons de suivi créés pour le mémoire");
                }
            }

            // ========================================
            // 10. CRÉATION D'UN DOCUMENT DE TEST
            // ========================================
            if (documentRepository.count() == 0) {
                Memoire memoire = memoireRepository.findAll().stream().findFirst().orElse(null);
                if (memoire != null) {
                    Document document = new Document();
                    document.setMemoire(memoire);
                    document.setType(DocumentType.PLAN_MEMOIRE);
                    document.setNomFichier("plan_memoire_v1.pdf");
                    document.setCheminFichier("/documents/memoire_" + memoire.getId() + "/plan_memoire_v1.pdf");
                    document.setVersion(1);
                    document.setDescription("Plan détaillé du mémoire de Master");
                    document.setValideParEncadrant(false);
                    documentRepository.save(document);

                    System.out.println("✅ Document de test créé : plan_memoire_v1.pdf");
                }
            }

            // ========================================
            // 11. CRÉATION D'UNE OBSERVATION
            // ========================================
            if (observationRepository.count() == 0) {
                Memoire memoire = memoireRepository.findAll().stream().findFirst().orElse(null);
                User encadrant = userRepository.findByEmail("flavien.somda@ujkz.bf").orElse(null);
                if (memoire != null && encadrant != null) {
                    Observation observation = new Observation();
                    observation.setMemoire(memoire);
                    observation.setAuteur(encadrant);
                    observation.setContenu("Le plan est bien structuré. Veuillez ajouter plus de détails sur la méthodologie.");
                    observation.setTypeObservation(ObservationType.RECOMMANDATION);
                    observation.setVuParEtudiant(false);
                    observationRepository.save(observation);

                    System.out.println("✅ Observation créée pour le mémoire");
                }
            }

            // ========================================
            // 12. CRÉATION D'UNE CAMPAGNE
            // ========================================
            if (campagneRepository.count() == 0) {
                Semester semester = semesterRepository.findAll().get(0);
                
                Campagne campagne = new Campagne();
                campagne.setLibelle("Campagne de sujets - Semestre 1 2025-2026");
                campagne.setSemestre(semester);
                campagne.setDateDebutPropositionSujets(LocalDateTime.now().minusDays(15));
                campagne.setDateFinPropositionSujets(LocalDateTime.now().plusDays(15));
                campagne.setDateDebutPositionnementEtudiants(LocalDateTime.now());
                campagne.setDateFinPositionnementEtudiants(LocalDateTime.now().plusDays(30));
                campagne.setDateLimiteDepotDocuments(LocalDateTime.now().plusDays(90));
                campagne.setDateDebutSoutenances(LocalDateTime.now().plusMonths(3));
                campagne.setDateFinSoutenances(LocalDateTime.now().plusMonths(4));
                campagne.setOuverte(true);
                campagne.setActive(true);
                campagneRepository.save(campagne);
                
                System.out.println("✅ Campagne créée : " + campagne.getLibelle());
            }

            // ========================================
            // 13. CRÉATION D'UNE SESSION DE SOUTENANCE
            // ========================================
            if (defenseSessionRepository.count() == 0) {
                Semester semester = semesterRepository.findAll().get(0);
                User admin = userRepository.findByEmail("admin@ujkz.bf").orElse(null);

                DefenseSession session = new DefenseSession();
                session.setLibelle("Session de soutenance Juin 2026");
                session.setDateSession(LocalDateTime.now().plusMonths(3));
                session.setHeureDebut(LocalDateTime.now().plusMonths(3).withHour(8).withMinute(0));
                session.setHeureFin(LocalDateTime.now().plusMonths(3).withHour(18).withMinute(0));
                session.setSalle("Amphithéâtre A");
                session.setSemestre(semester);
                session.setResponsable(admin);
                session.setActive(true);
                session.setDescription("Session principale de soutenance pour le semestre 1 - Année 2025-2026");
                session.setNombreMaxSoutenances(10);
                defenseSessionRepository.save(session);

                System.out.println("✅ Session de soutenance créée : " + session.getLibelle());
            }

            // ========================================
            // 14. CRÉATION D'UNE SOUTENANCE
            // ========================================
            if (defenseRepository.count() == 0) {
                Memoire memoire = memoireRepository.findAll().stream().findFirst().orElse(null);
                DefenseSession session = defenseSessionRepository.findAll().stream().findFirst().orElse(null);
                
                if (memoire != null && session != null) {
                    // Marquer le mémoire comme soutenable
                    memoire.setSoutenable(true);
                    memoire.setDateValidationSoutenabilite(LocalDateTime.now());
                    memoire.setStatutAvancement(AvancementStatus.SOUTENABLE);
                    memoireRepository.save(memoire);
                    
                    Defense defense = new Defense();
                    defense.setMemoire(memoire);
                    defense.setSession(session);
                    defense.setDateHeure(session.getDateSession().withHour(9).withMinute(0));
                    defense.setStatut(DefenseStatus.PROGRAMMEE);
                    defense.setValidee(false);
                    defense.setDureeMinutes(30);
                    defense.setLienVisio("https://meet.google.com/abc-def-ghi");
                    defenseRepository.save(defense);
                    
                    System.out.println("✅ Soutenance programmée pour le mémoire");
                }
            }

            // ========================================
            // 15. CRÉATION D'UN JURY
            // ========================================
            if (juryRepository.count() == 0) {
                Defense defense = defenseRepository.findAll().stream().findFirst().orElse(null);
                if (defense != null) {
                    // S'assurer qu'il y a au moins 2 enseignants
                    if (teacherRepository.count() >= 2) {
                        Teacher teacher1 = teacherRepository.findAll().get(0);
                        Teacher teacher2 = teacherRepository.findAll().get(1);
                        
                        Jury jury = new Jury();
                        jury.setDefense(defense);
                        jury.setNombreMembresMinimal(3);
                        jury.setConstitue(true);
                        jury.setComplet(false);
                        juryRepository.save(jury);
                        
                        // Ajouter les membres du jury
                        JuryMember member1 = new JuryMember();
                        member1.setJury(jury);
                        member1.setTeacher(teacher1);
                        member1.setRole(JuryRole.PRESIDENT);
                        member1.setPresent(false);
                        juryMemberRepository.save(member1);
                        
                        JuryMember member2 = new JuryMember();
                        member2.setJury(jury);
                        member2.setTeacher(teacher2);
                        member2.setRole(JuryRole.ENCADRANT);
                        member2.setPresent(false);
                        juryMemberRepository.save(member2);
                        
                        System.out.println("✅ Jury constitué pour la soutenance");
                    }
                }
            }

            // ========================================
            // 16. CRÉATION D'UNE OBSERVATION DE SOUTENANCE
            // ========================================
            if (defenseObservationRepository.count() == 0) {
                Defense defense = defenseRepository.findAll().stream().findFirst().orElse(null);
                User encadrant = userRepository.findByEmail("flavien.somda@ujkz.bf").orElse(null);
                
                if (defense != null && encadrant != null) {
                    DefenseObservation observation = new DefenseObservation();
                    observation.setDefense(defense);
                    observation.setAuteur(encadrant);
                    observation.setContenu("Le candidat a bien présenté son travail. La qualité scientifique est satisfaisante.");
                    observation.setCategorie("qualite_scientifique");
                    defenseObservationRepository.save(observation);
                    
                    System.out.println("✅ Observation de soutenance créée");
                }
            }

            // ========================================
            // 17. CRÉATION D'UNE NOTE DE MÉMOIRE
            // ========================================
            if (gradeMemoireRepository.count() == 0) {
                Defense defense = defenseRepository.findAll().stream().findFirst().orElse(null);
                User admin = userRepository.findByEmail("admin@ujkz.bf").orElse(null);
                
                if (defense != null && admin != null) {
                    // Marquer la soutenance comme terminée
                    defense.setStatut(DefenseStatus.TERMINEE);
                    defense.setValidee(true);
                    defenseRepository.save(defense);
                    
                    GradeMemoire grade = new GradeMemoire();
                    grade.setDefense(defense);
                    grade.setNoteFinale(15);
                    grade.setMention(Mention.BIEN);
                    grade.setCommentaires("Excellent travail, bonne maîtrise du sujet. La soutenance était de qualité.");
                    grade.setQualiteDocument(14);
                    grade.setTravailRealise(16);
                    grade.setPresentationOrale(15);
                    grade.setReponsesQuestions(15);
                    grade.setValidee(true);
                    grade.setDateValidation(LocalDateTime.now());
                    grade.setValidePar(admin);
                    gradeMemoireRepository.save(grade);
                    
                    // Mettre à jour le mémoire avec la note
                    Memoire memoire = defense.getMemoire();
                    memoire.setNoteFinale(15);
                    memoire.setMention(Mention.BIEN);
                    memoire.setSoutenu(true);
                    memoire.setDateSoutenance(defense.getDateHeure());
                    memoireRepository.save(memoire);
                    
                    System.out.println("✅ Note finale enregistrée : 15/20 (Bien)");
                }
            }

            // ========================================
            // RÉSUMÉ FINAL
            // ========================================
            System.out.println("\n========================================");
            System.out.println("📊 RÉSUMÉ DES DONNÉES DE TEST CRÉÉES");
            System.out.println("========================================");
            System.out.println("👤 Utilisateurs : " + userRepository.count());
            System.out.println("👨‍🏫 Enseignants : " + teacherRepository.count());
            System.out.println("👨‍🎓 Étudiants : " + studentRepository.count());
            System.out.println("📚 Années académiques : " + academicYearRepository.count());
            System.out.println("📖 Semestres : " + semesterRepository.count());
            System.out.println("📝 Sujets : " + subjectRepository.count());
            System.out.println("📋 Candidatures : " + applicationRepository.count());
            System.out.println("📄 Mémoires : " + memoireRepository.count());
            System.out.println("🎯 Jalons : " + milestoneRepository.count());
            System.out.println("📎 Documents : " + documentRepository.count());
            System.out.println("💬 Observations : " + observationRepository.count());
            System.out.println("📅 Campagnes : " + campagneRepository.count());
            System.out.println("🎤 Sessions de soutenance : " + defenseSessionRepository.count());
            System.out.println("⚖️ Soutenances : " + defenseRepository.count());
            System.out.println("👥 Jurys : " + juryRepository.count());
            System.out.println("👤 Membres de jury : " + juryMemberRepository.count());
            System.out.println("📝 Observations de soutenance : " + defenseObservationRepository.count());
            System.out.println("🏆 Notes : " + gradeMemoireRepository.count());
            System.out.println("========================================");
            System.out.println("✅ Application démarrée avec succès !");
            System.out.println("========================================");
        };
    }
}