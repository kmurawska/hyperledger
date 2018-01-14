package com.kmurawska.hyperledger.example.blockchain;

import com.kmurawska.hyperledger.example.blockchain.entity.User;
import com.kmurawska.hyperledger.example.blockchain.entity.UsersStore;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;

import java.io.IOException;
import java.net.MalformedURLException;

class UserEnroller {
    private final UsersStore usersStore;
    private final HFCAClient caClient;

    UserEnroller(String caServiceLocation, String userStorePath) throws MalformedURLException {
        this.caClient = createHFCAClient(caServiceLocation);
        this.usersStore = new UsersStore(userStorePath);
    }

    private HFCAClient createHFCAClient(String caServiceLocation) throws MalformedURLException {
        HFCAClient caClient = HFCAClient.createNewInstance(caServiceLocation, null);
        caClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        return caClient;
    }

    User registerAndEnroll(String name, String mspId, String affiliation) throws Exception {
        User admin = enrollAdmin("admin", "adminpw");
        return registerAndEnrollUser(admin, name, mspId, affiliation);
    }

    private User enrollAdmin(String name, String enrollmentSecret) throws IOException, ClassNotFoundException {
        return usersStore.load(name)
                .orElseGet(() -> {
                    try {
                        User admin = User.admin(name, enrollmentSecret, "");
                        Enrollment enrollment = caClient.enroll(name, admin.getEnrollmentSecret());
                        admin.setEnrollment(enrollment);
                        return admin;
                    } catch (EnrollmentException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private User registerAndEnrollUser(User registrar, String name, String mspId, String affiliation) throws Exception {
        return usersStore.load(name).orElseGet(() -> {
            User user = User.user(name, mspId, affiliation);
            try {
                user = registerUser(registrar, user);
                user = enrollUser(user);
                usersStore.save(user);
                return user;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private User registerUser(User registrar, User user) throws Exception {
        RegistrationRequest rr = new RegistrationRequest(user.getName(), user.getAffiliation());
        String enrollmentSecret = caClient.register(rr, registrar);
        user.setEnrollmentSecret(enrollmentSecret);

        return user;
    }

    private User enrollUser(User user) throws Exception {
        Enrollment enrollment = caClient.enroll(user.getName(), user.getEnrollmentSecret());
        user.setEnrollment(enrollment);
        return user;
    }
}
