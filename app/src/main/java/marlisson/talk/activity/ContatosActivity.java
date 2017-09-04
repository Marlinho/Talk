package marlisson.talk.activity;

import android.app.Activity;
import android.os.StrictMode;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import marlisson.talk.R;

public class ContatosActivity extends Activity {

    private ListView listaContatos;

    private String[] contatos = {
            "Alexandre seu lerdo lerdo grande urso lerdo idiota AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Igor", "Iago",
            "Luis Henrique", "Mau", "Poconé",
            "Vinicius", "Vicente", "Savio",
            "Thais Justi",
            "Alexandre", "Igor", "Iago",
            "Luis Henrique", "Mau", "Poconé",
            "Vinicius", "Vicente", "Savio",
            "Thais Justi"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contatos);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        listaContatos = (ListView) findViewById(R.id.listaContatos);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                contatos
        );

        listaContatos.setAdapter(adapter);

        listaContatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicao, long l) {
//                int codigoPosicao = posicao;
//                String valorClicado = (String) listaContatos.getItemAtPosition(codigoPosicao);
                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
