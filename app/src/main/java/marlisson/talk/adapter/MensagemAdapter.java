package marlisson.talk.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import marlisson.talk.R;
import marlisson.talk.helper.Preferencias;
import marlisson.talk.model.Mensagem;

/**
 * Created by marlisson on 29/08/17.
 */

public class MensagemAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<Mensagem> mensagens;

    public MensagemAdapter( Context c ,  ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.context = c;
        this.mensagens = objects;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        if(mensagens != null){

            //Recupera dados do remetente
            Preferencias preferencias = new Preferencias(context);
            String remetente = preferencias.getIdentificador();

            // Inicializa objeto para montagem do layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            // Recupera mensagem
            Mensagem mensagem = mensagens.get(position);

            // Monta view a partir do xml
            if(remetente.equals(mensagem.getIdUsuario())){
                view = inflater.inflate(R.layout.item_mensagem_direita, parent, false);
            }else {
                view = inflater.inflate(R.layout.item_mensagem_esquerda, parent, false);
            }


            //Recupera elemento para exibição
            TextView textoMensagem = view.findViewById(R.id.tv_mensagem);
            textoMensagem.setText(mensagem.getMensagem());
        }

        return view;
    }
}
