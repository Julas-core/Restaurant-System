package com.javaproject;

public final class ProfileData {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String addrHome;
    private String addrWork;

    public ProfileData() {
    }

    public ProfileData(String firstName, String lastName, String email, String phone, String addrHome, String addrWork) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.addrHome = addrHome;
        this.addrWork = addrWork;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddrHome() {
        return addrHome;
    }

    public void setAddrHome(String addrHome) {
        this.addrHome = addrHome;
    }

    public String getAddrWork() {
        return addrWork;
    }

    public void setAddrWork(String addrWork) {
        this.addrWork = addrWork;
    }
}
