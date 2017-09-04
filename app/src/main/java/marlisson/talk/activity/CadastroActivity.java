package marlisson.talk.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import marlisson.talk.R;
import marlisson.talk.config.ConfiguracaoFarebase;
import marlisson.talk.helper.Base64Custom;
import marlisson.talk.helper.Preferencias;
import marlisson.talk.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText nome;
    private EditText email;
    private EditText senha;
    private Button botaoCadastrarUsuario;
    private Usuario usuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        nome = (EditText) findViewById(R.id.CadastroNome);
        email = (EditText) findViewById(R.id.CadastroEmail);
        senha = (EditText) findViewById(R.id.CadastroSenha);
        botaoCadastrarUsuario = (Button) findViewById(R.id.botaoCadastrar);

        botaoCadastrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuario = new Usuario();
                usuario.setNome(nome.getText().toString());
                usuario.setEmail(email.getText().toString());
                usuario.setSenha(senha.getText().toString());
                cadastrarUsuario();


//                if (true){
//                    Toast.makeText(getApplicationContext(), "Usuario criado com sucesso!", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
//                    Toast.makeText(getApplicationContext(), "Faça login para entrar no sistema!", Toast.LENGTH_LONG).show();
//                    startActivity(intent);
//                }else{
//                    Toast.makeText(getApplicationContext(), "Mensagem de ERRO!", Toast.LENGTH_LONG).show();
//                } //DAR UM JEITO DE INFROMAR O ERRO    EX: ERRO NO SERVIDOR, EMAIL JÁ CADASTRADO
            }
        });

    }

    private void cadastrarUsuario(){
        autenticacao = ConfiguracaoFarebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar usuário!", Toast.LENGTH_LONG).show();

                    String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setId(identificadorUsuario);
                    usuario.salvar();

                    Preferencias preferencias = new Preferencias(CadastroActivity.this);
                    String nomeUsuario = usuario.getNome();
                    preferencias.salvarDados(identificadorUsuario, nomeUsuario );

                    abrirLoginUsuario();
                }else{

                    String erroExcecao = "";

                    try{
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erroExcecao = "Digite uma senha mais forte, contendo mais caracteres e com letras e numeros!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "E-mail inválido! Digite um novo e-mail";
                    } catch (FirebaseAuthUserCollisionException e) {
                        erroExcecao = "Esse e-mail já está em uso no app!!";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this, "Erro ao cadastrar usuário: " + erroExcecao, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void abrirLoginUsuario(){
        Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
