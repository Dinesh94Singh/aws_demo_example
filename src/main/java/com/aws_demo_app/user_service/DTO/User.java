package com.aws_demo_app.user_service.DTO;

// Object used for transpiling data sent from front-end to service and service-service communication
// We do this, so that, we do not expose, how we store the database internals
// If we direct make this an Entity, hackers will be able to guess the table structure and it is bad code.
public class User {
    private String firstName;
    private String lastName;
    private String dob; // to differentiate i have taken this as dob as String and saving it as Date object in DAO layer
    private String email;
    private String password;
    private String passwordHint;
    private String phoneNumber;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordHint() {
        return passwordHint;
    }

    public void setPasswordHint(String passwordHint) {
        this.passwordHint = passwordHint;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
