package flip.flip;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by AntonioJoaquín on 17/05/2015.
 */
public class PlayFragment extends Fragment {
    public static final String ARG_ARTICLES_NUMBER = "articles_number";

    public PlayFragment() {
        // Constructor vacío obligatorio
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.game_config, container, false);
        int i = getArguments().getInt(ARG_ARTICLES_NUMBER);
        String article = getResources().getStringArray(R.array.Tags)[i];

        getActivity().setTitle(article);
        TextView headline = (TextView) rootView.findViewById(R.id.headline);
        headline.append(" " + article);

        GameConfig gameConfig = new GameConfig();
        return rootView;
    }
    /*
    *
    * Clase de configuración
    *
    */


    public static class GameConfig extends ActionBarActivity {



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.game_config);

            //botón de inicio de partida
            Button button = (Button) findViewById(R.id.startBtn);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPlay();
                }
            });
            //control número de celdas horizontales
            SeekBar xTiles = (SeekBar) findViewById(R.id.seekBarX);
            xTiles.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    updateXTiles(seekBar.getProgress());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            updateXTiles(xTiles.getProgress());
            //control número de celdas verticales
            SeekBar yTiles = (SeekBar) findViewById(R.id.seekBarY);
            yTiles.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    updateYTiles(seekBar.getProgress());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            updateYTiles(yTiles.getProgress());
            //barra para las tramas
            SeekBar colors = (SeekBar) findViewById(R.id.seekBarColors);
            colors.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    updateColors(seekBar.getProgress());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            updateColors(colors.getProgress());
        }
        private void updateXTiles(int progress){
            TextView tv = (TextView) findViewById(R.id.seekBarXtxt);
            tv.setText(getString(R.string.num_elem_x) + " " + (progress + 3));
        }

        private void updateYTiles(int progress){
            TextView tv = (TextView) findViewById(R.id.seekBarYtxt);
            tv.setText(getString(R.string.num_elem_y) + " " + (progress + 3));
        }

        private void updateColors(int progress){
            TextView tv = (TextView) findViewById(R.id.seekBarColorstxt);
            tv.setText(getString(R.string.num_colors) + " " + (progress + 2));
        }

        private static final int ACTION_PLAY = 1;
        protected void startPlay(){

            Intent i = new Intent(this, GameField.class);
            //recuperamos información para pasarla al tablero
            SeekBar sb = (SeekBar) findViewById(R.id.seekBarX);
            i.putExtra("xtiles", sb.getProgress());
            sb = (SeekBar) findViewById(R.id.seekBarY);
            i.putExtra("ytiles", sb.getProgress());

            sb = (SeekBar) findViewById(R.id.seekBarColors);
            i.putExtra("numcolors", sb.getProgress());

            RadioButton r = (RadioButton) findViewById(R.id.rdColors);
            i.putExtra("tile", r.isChecked()?"C":"N");
            //control del sonido
            CheckBox chSound = (CheckBox) findViewById(R.id.checkBoxSound);
            i.putExtra("hasSound", chSound.isChecked());
            //control de la vibración
            CheckBox chVibrate = (CheckBox) findViewById(R.id.checkBoxVibrate);
            i.putExtra("hasVibration", chVibrate.isChecked());
            //comenzar activity
            startActivityForResult(i, ACTION_PLAY);
        }

        protected void onActivityResult(int requestCode, int resultCode, Intent data){
            if(resultCode == RESULT_OK){
                switch (requestCode){
                    case ACTION_PLAY:
                        new AlertDialog.Builder(this).setMessage(getResources().getString(R.string.game_end_1)+ " " +
                                data.getIntExtra("clicks", 0) + " " + getResources().getString(R.string.game_end_2))
                                .setPositiveButton(android.R.string.ok, null).show();
                        break;
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}
