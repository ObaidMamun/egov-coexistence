package org.egov.infra.microservice.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeInfo {
    @JsonProperty("user")
    User UserObject;
    private float id;
    private String uuid;
    private String code;
    private String employeeStatus;
    private String employeeType;
    private float dateOfAppointment;
    List < Assignment > assignments = new ArrayList < Assignment > ();
    
    // Getter Methods 

    public User getUser() {
     return UserObject;
    }

    public float getId() {
     return id;
    }

    public String getUuid() {
     return uuid;
    }

    public String getCode() {
     return code;
    }

    public String getEmployeeStatus() {
     return employeeStatus;
    }

    public String getEmployeeType() {
     return employeeType;
    }

    public float getDateOfAppointment() {
     return dateOfAppointment;
    }

   
    // Setter Methods 

    public void setUser(User userObject) {
     this.UserObject = userObject;
    }

    public void setId(float id) {
     this.id = id;
    }

    public void setUuid(String uuid) {
     this.uuid = uuid;
    }

    public void setCode(String code) {
     this.code = code;
    }

    public void setEmployeeStatus(String employeeStatus) {
     this.employeeStatus = employeeStatus;
    }

    public void setEmployeeType(String employeeType) {
     this.employeeType = employeeType;
    }

    public void setDateOfAppointment(float dateOfAppointment) {
     this.dateOfAppointment = dateOfAppointment;
    }

    public User getUserObject() {
        return UserObject;
    }

    public void setUserObject(User userObject) {
        UserObject = userObject;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

         
}
