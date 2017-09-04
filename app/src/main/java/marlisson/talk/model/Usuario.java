package marlisson.talk.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import marlisson.talk.config.ConfiguracaoFarebase;

/**
 * Created by marlisson on 28/08/17.
 */

public class Usuario {

    private String id;
    private String nome;
    private String email;
    private String senha;

    public Usuario() {
    }

    public void salvar(){
        DatabaseReference referenciaRirebase = ConfiguracaoFarebase.getFirebase();
        referenciaRirebase.child("usuarios").child(getId()).setValue(this);
    }
    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
