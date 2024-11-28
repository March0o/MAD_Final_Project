package com.example.mad_final_game;

public class Highscore {
    private long id;
    private String Name;
    private int Score;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setScore(int score) {
        this.Score = score;
    }
    public void setName(String name) {
        this.Name = name;
    }

    public int getHighscore() {
        return Score;
    }


    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return Name + " " + String.valueOf(Score);
    }

}
