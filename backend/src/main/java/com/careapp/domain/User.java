package com.careapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    
    // Frontend required fields
    private String firstName;    // first name
    private String lastName;     // last name
    private String email;        // email
    private String role;         // role: POA, Worker, Admin
    private String password;     // password
    private String status  = "PENDING";

    // Authorization system fields
    private String userType;     // FM, POA, MANAGER, WORKER
    private String organizationId; // organization ID
    private String patientId;    // associated patient ID (for FM/POA)
    
    // keep uname field for backward compatibility
    private String uname;

    // Password reset fields
    private String passwordResetToken;   // reset token
    private Long passwordResetExpires;   // epoch millis when token expires

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Authorization system getters & setters
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getOrganizationId() { return organizationId; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    // Backward compatibility uname field
    public String getUname() { return uname; }
    public void setUname(String uname) { this.uname = uname; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPasswordResetToken() { return passwordResetToken; }
    public void setPasswordResetToken(String passwordResetToken) { this.passwordResetToken = passwordResetToken; }

    public Long getPasswordResetExpires() { return passwordResetExpires; }
    public void setPasswordResetExpires(Long passwordResetExpires) { this.passwordResetExpires = passwordResetExpires; }


}
