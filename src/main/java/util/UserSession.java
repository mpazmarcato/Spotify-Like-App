package util;

import model.User;

public class UserSession {
    private static User loggedInUser;

    // Método para obter o usuário logado
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    // Método para definir o usuário logado
    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }
}
