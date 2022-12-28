package com.example.players.json;

import com.example.players.dto.Player;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class CashManager {
    public static List<Player> read(String path) {
        Type REVIEW_TYPE = new TypeToken<List<Player>>() {}.getType();
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader("cash/" + path));
            return gson.fromJson(reader, REVIEW_TYPE);
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
            return null;
        }
    }

    public static void write(String path, List<Player> players) {
        try (Writer writer = new FileWriter("cash/" + path)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(players, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
