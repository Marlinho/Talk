package marlisson.talk.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Looper;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import marlisson.talk.R;
import marlisson.talk.adapter.MensagemAdapter;
import marlisson.talk.config.ConfiguracaoFarebase;
import marlisson.talk.helper.Base64Custom;
import marlisson.talk.helper.Preferencias;
import marlisson.talk.model.Conversa;
import marlisson.talk.model.Mensagem;


public class ConversaActivity extends AppCompatActivity {

    private EditText editMensagem;
    private ImageButton btMensagem;
    private Toolbar toolbar;
    private ListView listView;
    private DatabaseReference firebase;

    public ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;
    private ValueEventListener valueEventListenerMensagem;

    //dados do destinatario
    private String nomeUsuarioDestino;
    private String idUsuarioDestino;

    //dados do remetente
    private String idUsuarioRemetente;
    private String nomeUsuarioRemetente;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = (Toolbar) findViewById(R.id.tb_conversa);
        editMensagem = (EditText) findViewById(R.id.edit_mensagem);
        btMensagem = (ImageButton) findViewById(R.id.bt_enviar);
        listView = (ListView) findViewById(R.id.lv_conversa);

        //dados do usuario logado
        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        idUsuarioRemetente = preferencias.getIdentificador();
        nomeUsuarioRemetente = preferencias.getNome();


        Bundle extra = getIntent().getExtras();

        if(extra != null){
            nomeUsuarioDestino = extra.getString("nome");
            String emailDestinatario = extra.getString("email");
            idUsuarioDestino = Base64Custom.codificarBase64( emailDestinatario );
        }

        //Configura Toolbar
        toolbar.setTitle(nomeUsuarioDestino);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);


        // Monta listview e adapter
        mensagens = new ArrayList<>();
        adapter = new MensagemAdapter(ConversaActivity.this, mensagens);
        listView.setAdapter( adapter );

        // Recuperar mensagens do Firebase
        firebase = ConfiguracaoFarebase.getFirebase()
                .child("mensagens")
                .child( idUsuarioRemetente )
                .child( idUsuarioDestino );

        // Cria listener para mensagens
        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Limpar mensagens
                mensagens.clear();

                // Recupera mensagens
                for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                    Mensagem mensagem = dados.getValue( Mensagem.class );
                    mensagens.add( mensagem );
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener( valueEventListenerMensagem );


        // Enviar mensagem
        btMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textoMensagem = editMensagem.getText().toString();

                if(textoMensagem.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Digite uma mensagem", Toast.LENGTH_LONG).show();
                }else{

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRemetente);
                    mensagem.setMensagem(textoMensagem);

                    // salvamos mensagem para o remetente
                    Boolean retornoMensagemRemetente = salvarMensagem(idUsuarioRemetente, idUsuarioDestino , mensagem );
                    if( !retornoMensagemRemetente ){
                        Toast.makeText(
                                ConversaActivity.this,
                                "Problema ao salvar mensagem, tente novamente!",
                                Toast.LENGTH_LONG
                        ).show();
                    }else {

                        // salvamos mensagem para o destinatario
                        Boolean retornoMensagemDestinatario = salvarMensagem(idUsuarioDestino, idUsuarioRemetente , mensagem );
                        if( !retornoMensagemDestinatario ){
                            Toast.makeText(
                                    ConversaActivity.this,
                                    "Problema ao enviar mensagem para o destinatário, tente novamente!",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    }

                    // salvamos Conversa para o remetente
                    Conversa conversa = new Conversa();
                    conversa.setIdUsuario( idUsuarioDestino);
                    conversa.setNome( nomeUsuarioDestino);
                    conversa.setMensagem( textoMensagem );
                    Boolean retornoConversaRemetente = salvarConversa(idUsuarioRemetente, idUsuarioDestino, conversa);
                    if( !retornoConversaRemetente ){
                        Toast.makeText(
                                ConversaActivity.this,
                                "Problema ao salvar conversa, tente novamente!",
                                Toast.LENGTH_LONG
                        ).show();
                    }else {
                        // salvamos Conversa para o Destinatario

                        conversa = new Conversa();
                        conversa.setIdUsuario( idUsuarioRemetente );
                        conversa.setNome( nomeUsuarioRemetente );
                        conversa.setMensagem(textoMensagem);

                        Boolean retornoConversaDestinatario = salvarConversa(idUsuarioDestino, idUsuarioRemetente, conversa );
                        if( !retornoConversaDestinatario ){
                            Toast.makeText(
                                    ConversaActivity.this,
                                    "Problema ao salvar conversa para o destinatário, tente novamente!",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    }

                    editMensagem.setText("");

                }
            }
        });

    }

    private boolean salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem){
        try {

            firebase = ConfiguracaoFarebase.getFirebase().child("mensagens");

            firebase.child( idRemetente )
                    .child( idDestinatario )
                    .push()
                    .setValue( mensagem );

            return true;

        }catch ( Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean salvarConversa(String idRemetente, String idDestinatario, Conversa conversa){
        try {
            firebase = ConfiguracaoFarebase.getFirebase().child("conversas");
            firebase.child( idRemetente )
                    .child( idDestinatario )
                    .setValue( conversa );

            return true;

        }catch ( Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerMensagem);
    }





}
