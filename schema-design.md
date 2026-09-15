# Smart Clinic Management System - Database Schema Design

## MySQL Database Design

MySQL stores the core structured and relational data of the Smart Clinic Management System, including patients, doctors, appointments, and administrators.

### Table: patients

- id: INT, Primary Key, Auto Increment
- name: VARCHAR(100), NOT NULL
- email: VARCHAR(255), NOT NULL, UNIQUE
- phone: VARCHAR(20), NOT NULL, UNIQUE
- password: VARCHAR(255), NOT NULL
- date_of_birth: DATE
- gender: VARCHAR(20)
- created_at: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP

The patient ID uniquely identifies each patient. Email and phone number are unique so that duplicate patient accounts are prevented.

### Table: doctors

- id: INT, Primary Key, Auto Increment
- name: VARCHAR(100), NOT NULL
- email: VARCHAR(255), NOT NULL, UNIQUE
- phone: VARCHAR(20), UNIQUE
- password: VARCHAR(255), NOT NULL
- specialty: VARCHAR(100), NOT NULL
- available_times: TEXT
- created_at: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP

Each doctor has a unique ID and email address. The available_times field stores information about the doctor's available appointment slots.

### Table: appointments

- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id), NOT NULL
- patient_id: INT, Foreign Key → patients(id), NOT NULL
- appointment_time: DATETIME, NOT NULL
- status: INT, NOT NULL (0 = Scheduled, 1 = Completed, 2 = Cancelled)
- reason: VARCHAR(255)
- created_at: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP

Relationships:

- doctor_id references doctors(id)
- patient_id references patients(id)
- One doctor can have many appointments.
- One patient can have many appointments.

Appointments should be checked by the application to prevent a doctor from having overlapping bookings.

### Table: admin

- id: INT, Primary Key, Auto Increment
- name: VARCHAR(100), NOT NULL
- email: VARCHAR(255), NOT NULL, UNIQUE
- password: VARCHAR(255), NOT NULL
- created_at: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP

Administrators manage doctors, users, and other administrative functionality within the clinic system.

### Table: prescriptions

- id: INT, Primary Key, Auto Increment
- appointment_id: INT, Foreign Key → appointments(id), NOT NULL
- doctor_id: INT, Foreign Key → doctors(id), NOT NULL
- patient_id: INT, Foreign Key → patients(id), NOT NULL
- medication: VARCHAR(255), NOT NULL
- dosage: VARCHAR(100), NOT NULL
- instructions: TEXT
- issued_at: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP

Relationships:

- appointment_id references appointments(id)
- doctor_id references doctors(id)
- patient_id references patients(id)

A prescription is associated with a specific appointment, doctor, and patient.

### Relationship and Data Integrity Decisions

- Patients and doctors use unique email addresses for account identification.
- Foreign keys maintain relationships between appointments, doctors, patients, and prescriptions.
- Historical appointment information should be retained rather than automatically deleted when possible.
- Appointment scheduling logic should prevent overlapping appointments for the same doctor.
- Email and phone formats will be validated by the application layer.

## MongoDB Collection Design

MongoDB is used for flexible data that may contain optional, nested, or evolving fields.

### Collection: prescriptions

Example document:

```json
{
  "_id": "ObjectId('64abc123456')",
  "patientId": 101,
  "doctorId": 12,
  "appointmentId": 51,
  "medication": "Paracetamol",
  "dosage": "500mg",
  "doctorNotes": "Take 1 tablet every 6 hours when required.",
  "refillCount": 2,
  "tags": [
    "pain-relief",
    "oral-medication"
  ],
  "pharmacy": {
    "name": "Smart Clinic Pharmacy",
    "location": "Main Clinic"
  },
  "metadata": {
    "createdBy": "doctor",
    "status": "active"
  }
}
