package com.kmurawska.hyperledger.example.blockchain.entity;

import org.hyperledger.fabric.sdk.Enrollment;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class User implements org.hyperledger.fabric.sdk.User, Serializable {
    private static final long serialVersionUID = 1L;
    private String name, mspId, enrollmentSecret, affiliation, organization, account;
    private Enrollment enrollment;
    private Set<String> roles = new HashSet<>();

    public static User admin(String name, String enrollmentSecret, String mspId) {
        User user = new User();
        user.name = name;
        user.enrollmentSecret = enrollmentSecret;
        user.mspId = mspId;
        return user;
    }

    public static User user(String name, String mspId, String affiliation) {
        User user = new User();
        user.name = name;
        user.mspId = mspId;
        user.affiliation = affiliation;
        return user;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getRoles() {
        return roles;
    }

    @Override
    public String getAccount() {
        return null;
    }

    @Override
    public String getAffiliation() {
        return affiliation;
    }

    @Override
    public Enrollment getEnrollment() {
        return enrollment;
    }

    @Override
    public String getMspId() {
        return mspId;
    }

    public void setEnrollmentSecret(String enrollmentSecret) {
        this.enrollmentSecret = enrollmentSecret;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public String getEnrollmentSecret() {
        return enrollmentSecret;
    }
}
