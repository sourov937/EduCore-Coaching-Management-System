# 🎓 EduCore – Coaching Management System

**EduCore** is a Java-based Coaching Management System designed to digitize and simplify the academic and administrative activities of coaching centers. It provides a centralized platform for managing students, teachers, guardians, batches, attendance, examinations, results, assignments, fees, notices, and academic performance.

The system supports four main user roles:

* 👨‍💼 **Director**
* 👨‍🏫 **Teacher**
* 👨‍🎓 **Student**
* 👨‍👩‍👦 **Guardian**

---

## 📌 Project Overview

Many coaching centers still rely on paper registers, spreadsheets, and manual communication for managing student information, attendance, examination results, fees, and other activities.

EduCore aims to replace these manual processes with a centralized and user-friendly management system that improves accuracy, reduces workload, minimizes errors, and makes important information easier to access.

---

## ✨ Features

### 🔐 Authentication & Role-Based Access

* Director Login
* Teacher Login
* Student Login
* Guardian Login
* Role-based access control

### 👨‍💼 Director Features

* Student Management
* Teacher Management
* Guardian Management
* Course Management
* Batch Management
* Fee Management
* Notice Management
* User Account Management
* Data Management

### 👨‍🏫 Teacher Features

* Attendance Management
* Examination Management
* Result Management
* Assignment Management
* Student Information Management
* Student Performance Monitoring

### 👨‍🎓 Student Features

* View Attendance
* View Examination Results
* View Assignments
* View Fee Status
* View Notices
* Monitor Academic Performance
* Provide Teacher Reviews

### 👨‍👩‍👦 Guardian Features

* Monitor Student Attendance
* View Examination Results
* Monitor Academic Progress
* View Fee Status
* View Notices

The project's documented feature set includes dashboards, attendance, results, assignments, fees, notices, batch management, and role-based access.

---

## 🛠️ Technologies Used

| Technology          | Purpose                         |
| ------------------- | ------------------------------- |
| **Java**            | Application Development         |
| **Java Swing**      | Graphical User Interface        |
| **MySQL**           | Database                        |
| **JDBC**            | Java–MySQL Connectivity         |
| **Apache NetBeans** | Development Environment         |
| **Git & GitHub**    | Version Control & Collaboration |
| **Draw.io**         | System Design & UML Diagrams    |

The system follows a modular structure consisting of **Model, UI, DAO, and Database** components.

---

## 🏗️ System Architecture

```text
                 ┌──────────────────────┐
                 │      Java Swing      │
                 │         UI           │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │        Model         │
                 │  Entities & Objects  │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │         DAO          │
                 │ Database Operations  │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │         JDBC         │
                 │ Database Connection  │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │        MySQL         │
                 │       Database       │
                 └──────────────────────┘
```

---

## 👥 User Roles

| Role              | Main Responsibilities                   |
| ----------------- | --------------------------------------- |
| 👨‍💼 Director    | Complete system administration          |
| 👨‍🏫 Teacher     | Attendance, exams, results, assignments |
| 👨‍🎓 Student     | Academic information and performance    |
| 👨‍👩‍👦 Guardian | Monitor student's academic progress     |

Each role receives a separate dashboard containing only the features authorized for that role.

---

## 📊 System Design

The project uses several system modeling diagrams:

* Use Case Diagram
* Level-1 Data Flow Diagram (DFD)
* Entity Relationship Diagram (ERD)
* Class Diagram
* Activity Diagram
* Sequence Diagram

These diagrams represent the system's structure, workflows, interactions, and data flow.

---

## 🔄 Development Methodology

EduCore follows the **Software Development Life Cycle (SDLC)** using the **Waterfall Model**.

### Waterfall Phases

```text
Planning
   ↓
Requirement Analysis
   ↓
System Design
   ↓
Implementation
   ↓
Testing
   ↓
Deployment
   ↓
Maintenance
```

The Waterfall model was selected because the project requirements are clearly defined and the development process requires systematic documentation.

---

## 📸 Screenshots

### Login Interface

(https://github.com/sourov937/EduCore-Coaching-Management-System/blob/7c188257af27795096e2a165b07caf5a8e82ed61/screenshots/Login%20page.png)



### Director Dashboard
https://github.com/sourov937/EduCore-Coaching-Management-System/blob/7c188257af27795096e2a165b07caf5a8e82ed61/screenshots/Dashboards.jpeg



### Teacher Dashboard
https://github.com/sourov937/EduCore-Coaching-Management-System/blob/7c188257af27795096e2a165b07caf5a8e82ed61/screenshots/Teacher%20Dashborads.png




### Student Dashboard

https://github.com/sourov937/EduCore-Coaching-Management-System/blob/7c188257af27795096e2a165b07caf5a8e82ed61/screenshots/Student%20Dashboards.png

### Guardian Dashboard
https://github.com/sourov937/EduCore-Coaching-Management-System/blob/7c188257af27795096e2a165b07caf5a8e82ed61/screenshots/Guardian%20Dashboards.png

The project report documents interfaces for login, director dashboard, teacher/student/guardian management, batch management, fees, notices, exams, results, and role-specific dashboards.

---

## 📂 Project Structure

```text
EduCore/
│
├── src/
│   ├── model/
│   ├── dao/
│   ├── ui/
│   └── database/
│
├── screenshots/
│
├── database/
│   └── educore.sql
│
├── diagrams/
│   ├── use-case-diagram.png
│   ├── class-diagram.png
│   ├── activity-diagram.png
│   ├── sequence-diagram.png
│   ├── dfd-level-1.png
│   └── erd.png
│
├── README.md
└── LICENSE
```

> Adjust the folder names according to your actual NetBeans project structure.

---

## ⚙️ Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/EduCore.git
```

### 2. Open the Project

Open the project using **Apache NetBeans**.

### 3. Setup MySQL Database

Create a MySQL database:

```sql
CREATE DATABASE educore;
```

Import the provided SQL file:

```text
database/educore.sql
```

### 4. Configure Database Connection

Update the JDBC connection settings according to your MySQL configuration.

Example:

```java
String url = "jdbc:mysql://localhost:3306/educore";
String username = "root";
String password = "your_password";
```

### 5. Run the Project

Open the main Java class in NetBeans and run the project.

---

## 🗄️ Main Database Modules

The database manages information related to:

* Students
* Teachers
* Guardians
* Courses
* Batches
* Attendance
* Examinations
* Results
* Assignments
* Fees
* Notices
* Teacher Reviews

---

## 🎯 Project Objectives

The main objectives of EduCore are:

* Replace manual coaching-center management.
* Reduce human errors.
* Centralize student and academic information.
* Simplify attendance and result management.
* Improve fee management.
* Improve communication between stakeholders.
* Provide secure role-based access.
* Reduce administrative workload.
* Improve overall coaching-center efficiency.

---

## 🚀 Future Improvements

Planned future enhancements include:

* 📱 Android/iOS Mobile Application
* 💳 Online Fee Payment
* 📧 SMS & Email Notifications
* 📝 Online Examination
* 📊 Advanced Academic Reports
* 📷 QR/Biometric Attendance
* ☁️ Cloud Deployment
* 🔐 Enhanced Security
* 📈 Performance Analytics
* 🏫 Multi-Branch Support
* 🤖 AI-Based Student Performance Analysis

---

👨‍💻 Author
Sourov chandra Das
Computer Science & Engineering (CSE)

**Course:** CSE 318 – System Analysis and Design Lab
**Institution:** Bangladesh University of Business and Technology (BUBT)
**Semester:** Summer 2026

[---](https://github.com/sourov937/EduCore-Coaching-Management-System/blob/master/screenshots/Dashboards.jpeg)

## 📚 References

* Ian Sommerville — *Software Engineering*
* R. S. Pressman & B. R. Maxim — *Software Engineering: A Practitioner's Approach*
* Booch, Rumbaugh & Jacobson — *The Unified Modeling Language User Guide*
* Oracle Java Documentation
* MySQL Documentation
* Apache NetBeans Documentation
* GitHub Documentation
* Draw.io / diagrams.net Documentation

---

## 📄 License

This project was developed as an academic project for **CSE 318 – System Analysis and Design Lab**.



