package com.kmurawska.hyperledger.example.colors.entity.colors;

import javax.json.JsonObject;

import static javax.json.Json.createObjectBuilder;

public class Color {
    private final String identifier, family, name, rgb, hex, example;

    public Color(JsonObject payload) {
        this.identifier = payload.getString("hex");
        this.family = payload.getString("family");
        this.name = payload.getString("name");
        this.rgb = payload.getString("rgb");
        this.hex = payload.getString("hex");
        this.example = payload.getString("example");
    }

    public Color(String family, String name, String rgb, String hex, String example) {
        this.identifier = hex;
        this.family = family;
        this.name = name;
        this.rgb = rgb;
        this.hex = hex;
        this.example = example;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getFamily() {
        return family;
    }

    public String getName() {
        return name;
    }

    public String getRgb() {
        return rgb;
    }

    public String getHex() {
        return hex;
    }

    public String getExample() {
        return example;
    }

    public JsonObject toJson() {
        return createObjectBuilder()
                .add("family", this.family)
                .add("name", this.name)
                .add("rgb", this.rgb)
                .add("hex", this.hex)
                .add("example", this.example)
                .build();
    }
}