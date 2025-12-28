# 📝 EduPulse - Enrollment Management Service
> The registration hub for student-class associations, enrollment tracking, and lecturer-student relationship management.

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/)
[![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![OpenFeign](https://img.shields.io/badge/OpenFeign-00ADD8?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud-openfeign)
[![Microservices](https://img.shields.io/badge/Architecture-Microservices-blue?style=for-the-badge)](#)

---

## 📖 Project Overview

The **Enrollment Service** manages the critical relationship between students and classes in the EduPulse ecosystem. It handles course registration, tracks enrollment status, and provides lecturers with comprehensive student rosters and analytics.

Built with **Spring Cloud OpenFeign**, this service seamlessly integrates with the Class and User services to validate enrollments, retrieve class details, and provide rich student information for academic management.

### 🏗 Microservices Intercommunication

This service acts as a bridge between students and their academic journey:

* **📚 Class Integration:** Fetches complete class details from `Class-Service` for enrollment validation.
* **👤 Student Validation:** Verifies student identities and credentials via `User-Service`.
* **👨‍🏫 Lecturer Access:** Provides enrolled student lists with full user details for teaching staff.
* **📊 Analytics Feed:** Supplies enrollment statistics to `Admin-Service` for platform insights.
* **🎯 Quiz Eligibility:** Determines which students can access quizzes based on enrollment status.

---

## 🚀 Key Features

* **📝 Course Enrollment:** Students can register for classes based on their grade level.
* **🔄 Enrollment Management:** Unenroll functionality with complete audit trail.
* **📚 My Classes View:** Personalized dashboard showing all enrolled courses with details.
* **👨‍🏫 Student Rosters:** Lecturers can view complete lists of enrolled students per class.
* **📊 Enrollment Analytics:** Track student counts per class and per lecturer.
* **🔍 Student Details:** Comprehensive student profiles for lecturers including enrollment history.
* **🎓 Cross-Lecturer Insights:** Lecturers can see all unique students across their classes.
* **🔒 Security First:** Role-based access with ownership validation.

---

## 🛠 Tech Stack

* **Backend:** Java 21, Spring Boot 3.5.0
* **Security:** Spring Security 6.x, JWT Authentication
* **Database:** MySQL with JPA/Hibernate
* **Build Tool:** Maven
* **Inter-Service Comm:** OpenFeign (Class Service & User Service)
* **Validation:** Hibernate Validator
* **DevOps:** Spring DevTools

---

## 📡 API Documentation (V1)

### 📝 Enrollment Operations

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `POST` | `/api/enrollments` | Enroll current student in a class | Student |
| `DELETE` | `/api/enrollments/{classId}` | Unenroll from a specific class | Student |
| `GET` | `/api/enrollments/my-classes` | Get all enrolled classes with details | Student |

### 👨‍🏫 Lecturer Management

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `GET` | `/api/enrollments/class/{classId}/students` | Get all enrolled students for a class | Lecturer |
| `GET` | `/api/enrollments/class/{classId}/count` | Get student count for a specific class | Lecturer |
| `GET` | `/api/enrollments/lecturer/students/count` | Get total unique student count | Lecturer |
| `GET` | `/api/enrollments/lecturer/students` | Get all unique students across classes | Lecturer |
| `GET` | `/api/enrollments/lecturer/student/{studentId}/details` | Get detailed student information | Lecturer |

### 📊 Analytics & Admin

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `GET` | `/api/enrollments/count` | Get total platform enrollment count | Public |

---



## 🔗 Related Services

- [🌐 API Gateway](https://github.com/Bavinduyeshan/Edu-Pulse-Gateway)
- [📚 Class Service](https://github.com/Bavinduyeshan/Edu-Pulse_Class_Service)
- [📝 Enrollment Service](https://github.com/Bavinduyeshan/Edu-Pulse-Entrollment-Service)
- [🎯 Quiz Service](https://github.com/Bavinduyeshan/Edu-Pulse-Quiz_Service)
- [👨‍💼 Admin Service](https://github.com/Bavinduyeshan/Edu-Pulse_Admin_Service)


---

<div align="center">

**Built with ❤️ for better education management**

⭐ Star this repository if you find it helpful!

</div>
