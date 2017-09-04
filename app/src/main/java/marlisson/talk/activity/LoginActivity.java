package marlisson.talk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import marlisson.talk.R;
import marlisson.talk.config.ConfiguracaoFarebase;
import marlisson.talk.helper.Base64Custom;
import marlisson.talk.helper.Preferencias;
import marlisson.talk.model.Usuario;


public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText senha;
    private Button botaoLogar;
    private Button botaoTelaCadastro;
    private Usuario usuario;
    private FirebaseAuth autenticacao;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerUsuario;
    private String identificadorUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verificarUsuarioLogado();//VERIFICA SE USUAIRO ESTÁ LOGADO E EM CASO POSITIVO ABRE A TELA PRINCIPAL DE CONVERSAS

        email = (EditText) findViewById(R.id.textoLogin);
        senha = (EditText) findViewById(R.id.textoSenha);
        botaoLogar = (Button) findViewById(R.id.botaoLogar);
        botaoTelaCadastro = (Button) findViewById(R.id.botaoTelaCadastro);

        botaoTelaCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    abrirTelaCadastro();
            }
        });

        botaoLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                usuario = new Usuario();
                usuario.setEmail(email.getText().toString());
                usuario.setSenha(senha.getText().toString());
                validarLogin();

            }
        });

    }


    private void validarLogin(){
        autenticacao = ConfiguracaoFarebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

//                if(task.isSuccessful()){
//
//                    Preferencias preferencias = new Preferencias(LoginActivity.this);
//                    String identificadorUsuarioLogado = Base64Custom.codificarBase64(usuario.getEmail());
//                    String nomeUsuario = usuario.getNome();
//                    preferencias.salvarDados(identificadorUsuarioLogado, nomeUsuario );
//
//                    abrirTelaPrincipal();
//                    Toast.makeText(LoginActivity.this, "Suceso ao fazer login! ", Toast.LENGTH_LONG).show();
//                }else{
//                    Toast.makeText(LoginActivity.this, "Erro ao fazer login! " , Toast.LENGTH_LONG).show();
//                }

                if( task.isSuccessful() ){


                    identificadorUsuarioLogado = Base64Custom.codificarBase64(usuario.getEmail());

                    firebase = ConfiguracaoFarebase.getFirebase()
                            .child("usuarios")
                            .child( identificadorUsuarioLogado );

                    valueEventListenerUsuario = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Usuario usuarioRecuperado = dataSnapshot.getValue( Usuario.class );

                            Preferencias preferencias = new Preferencias(LoginActivity.this);
                            preferencias.salvarDados( identificadorUsuarioLogado, usuarioRecuperado.getNome() );

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };

                    firebase.addListenerForSingleValueEvent( valueEventListenerUsuario );



                    abrirTelaPrincipal();
                    Toast.makeText(LoginActivity.this, "Sucesso ao fazer login!", Toast.LENGTH_LONG ).show();
                }else{
                    Toast.makeText(LoginActivity.this, "Erro ao fazer login!", Toast.LENGTH_LONG ).show();
                }

            }
        });
    }

    private void abrirTelaPrincipal(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void abrirTelaCadastro(){
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);
    }

    private void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFarebase.getFirebaseAutenticacao();
        if(autenticacao.getCurrentUser() != null){
            abrirTelaPrincipal();
        }
    }

}
