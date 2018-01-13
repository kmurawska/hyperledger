package com.kmurawska.hyperledger.example.entity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class UsersStore {
    private final Path keyStorePath;

    public UsersStore(String keyStorePath) {
        this.keyStorePath = Paths.get(keyStorePath);
    }

    public void save(User user) throws IOException {
        try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(keyStorePath.resolve(user.getName()).toFile()))) {
            o.writeObject(user);
        }
    }

    public Optional<User> load(String userName) throws IOException, ClassNotFoundException {
        Path file = keyStorePath.resolve(userName);
        if (!Files.exists(file) || !Files.isRegularFile(file)) {
            return Optional.empty();
        }
        try (ObjectInputStream oi = new ObjectInputStream(new FileInputStream(file.toFile()))) {
            return Optional.of((User) oi.readObject());
        }
    }
}