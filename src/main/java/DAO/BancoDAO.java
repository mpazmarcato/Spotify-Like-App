package DAO;

import java.util.ArrayList;

import model.Content;
import model.Song;
import model.User;

public class BancoDAO {

    private ArrayList<User> users;
    private ArrayList<Song> songs;
    private ArrayList<Content> contents;

    public static BancoDAO banco;

    private BancoDAO() {
        users = new ArrayList<>();
        songs = new ArrayList<>();
        contents = new ArrayList<>();
    }

    public static BancoDAO getIntance() {
        if (banco == null) {
            banco = new BancoDAO();
        }
        return banco;
    }

    public ArrayList<User> getArrayUsers() {
        return this.users;
    }

    public ArrayList<Song> getArraySongs() {
        return this.songs;
    }

    public ArrayList<Content> getArrayContents() {
        return this.contents;
    }
}
