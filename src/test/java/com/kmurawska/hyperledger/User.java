package com.kmurawska.hyperledger;

import org.hyperledger.fabric.sdk.Enrollment;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

class User implements org.hyperledger.fabric.sdk.User, Serializable {
    private static final long serialVersionUID = 1L;
    private String name, mspId, enrollmentSecret, affiliation, organization, account;
    private Enrollment enrollment;
    private Set<String> roles;

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
        return new HashSet<>();
    }

    @Override
    public String getAccount() {
        return null;
    }

    @Override
    public String getAffiliation() {
        return affiliation;
    }

    void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    @Override
    public Enrollment getEnrollment() {
        return enrollment;
    }

    @Override
    public String getMspId() {
        return mspId;
    }

    String getEnrollmentSecret() {
        return enrollmentSecret;
    }

    boolean isEnrolled() {
        return this.enrollment != null;
    }

    boolean isRegistered() {
        return enrollmentSecret != null && !"".equals(enrollmentSecret);
    }

    void setEnrollmentSecret(String enrollmentSecret) {
        this.enrollmentSecret = enrollmentSecret;
    }

}
