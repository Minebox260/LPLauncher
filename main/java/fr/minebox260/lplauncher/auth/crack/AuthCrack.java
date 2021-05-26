package fr.minebox260.lplauncher.auth.crack;

public class AuthCrack {
    private boolean connected = false;
    private String pseudo;

    public AuthCrack(String pseudo) {
        if (pseudo.length()>=3 && pseudo.length()<=16 && pseudo.matches("^[a-zA-Z0-9_]*$")) {
            setPseudo(pseudo);
            setConnected(true);
        }
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getPseudo() {
        return pseudo;
    }
}
