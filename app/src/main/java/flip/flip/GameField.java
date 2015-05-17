package flip.flip;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by AntonioJoaquín on 17/05/2015.
 */
public class GameField extends Activity{

    private static final int []colors = new int[]{
            R.drawable.ic_1c,
            R.drawable.ic_2c,
            R.drawable.ic_3c,
            R.drawable.ic_4c,
            R.drawable.ic_5c,
            R.drawable.ic_6c
    };
    private static final int []numbers = new int[]{
            R.drawable.ic_1n,
            R.drawable.ic_2n,
            R.drawable.ic_3n,
            R.drawable.ic_4n,
            R.drawable.ic_5n,
            R.drawable.ic_6n
    };
    //mantener el array que el usuario ha decidido utilizar
    private int []pictures = null;
    //número máximo de celdas horizontales y verticales
    private int topTileX = 3;
    private int topTileY = 3;
    //número máximo de elementos a utilizar
    private int topElements = 2;
    //se ha decidido o no usar sonido y vibración
    private boolean hasSound = false;
    private boolean hasVibration = false;
    //array con los identificadores de las celdas cuando se añadan al layout,
    //para poder recuperarlos durante la partida
    private  int ids[][] = null;
    //arra para guardar los índices de cada una de las celdas
    //se utilizará para la comprobar si la partida ha terminado o no
    private int values[][] = null;
    //contador con el número de pulsaciones que ha realizado el jugador
    private int numberOfClicks = 0;
    //para reproducir sonido cuando el jugador pulse una tecla
    private MediaPlayer mp = null;
    //para hacer vibrar al dispositivo cuando el usuario pulse una tecla
    private Vibrator vibratorService = null;
    //mostrará en pantalla las veces que el usuario ha pulsado una tecla
    private TextView tvNumberOfClicks = null;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_field);
        vibratorService = (Vibrator) (getSystemService(Service.VIBRATOR_SERVICE));
        mp = MediaPlayer.create(this, R.raw.touch);
        tvNumberOfClicks = (TextView) findViewById(R.id.clicksTxt);
        //obtención de parámetros de configuración
        Bundle extras = getIntent().getExtras();
        topTileX = extras.getInt("xtiles") + 3;
        topTileY = extras.getInt("ytiles") + 3;

        topElements = extras.getInt("numcolors") + 2;
        //usar colores o números
        if("C".equals(extras.getString("tile"))){
            pictures = colors;
        }else{
            pictures = numbers;
        }
        hasSound = extras.getBoolean("hasSound");
        hasVibration = extras.getBoolean("hasVibration");
        //limpiamos celdas para la partida
        LinearLayout ll = (LinearLayout) findViewById(R.id.fieldLandscape);
        ll.removeAllViews();
        //obtenemos las dimensiones
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels / topTileX;
        int height = (dm.heightPixels - 180) / topTileY;
        //inicializamos las celdas con los identificadores
        ids = new int[topTileX][topTileY];
        values = new int[topTileX][topTileY];

        Random r = new Random(System.currentTimeMillis());
        int tilePictureToShow = r.nextInt(topElements);
        //inicializamos el tablero
        int ident = 0;
        for(int i = 0;i < topTileY; i++){
            LinearLayout l2 = new LinearLayout(this);
            l2.setOrientation(LinearLayout.HORIZONTAL);
            for(int j = 0; j < topTileX; j++){
                tilePictureToShow = r.nextInt(topElements);
                //guardamos la trama a mostrar
                values[j][i] = tilePictureToShow;
                TileView tv = new TileView(this, j, i, topElements, tilePictureToShow, pictures[tilePictureToShow]);
                ident++;
                //se asigna un identificdor al objeto creado
                tv.setId(ident);
                //se guarda el identificador en una matriz
                ids[j][i] = ident;
                tv.setHeight(height);
                tv.setWidth(width);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hasClick(((TileView) v).x, ((TileView) v).y);
                    }
                });
                l2.addView(tv);
            }
            ll.addView(l2);
        }
        Chronometer t = (Chronometer) findViewById(R.id.Chronometer);
        t.start();
    }

    protected void hasClick(int x, int y){
        if(hasVibration) vibratorService.vibrate(100);
        if(hasSound) mp.start();
        //otro metodo para cambiar las celdas
        changeView(x, y);
        //controlomas las teclas que deben cmabiar por clicar en (x, y)
        //esquinas del tablero
        if(x==0 && y==0){
            changeView(0, 1);
            changeView(1, 0);
            changeView(1, 1);
        }else if(x==0 && y==topTileY - 1){
            changeView(0, topTileY - 2);
            changeView(1, topTileY - 2);
            changeView(1, topTileY - 1);
        }else if(x==topTileX-1 && y==0){
            changeView(topTileX - 2, 0);
            changeView(topTileX - 2, 1);
            changeView(topTileX - 1, 1);
        }else if(x==topTileX - 1 && y==topTileY - 1){
            changeView(topTileX - 2, topTileY - 1);
            changeView(topTileX - 2, topTileY - 2);
            changeView(topTileX - 1, topTileY - 2);
        }
        //lados del tablero
        else if(x == 0){
            changeView(x, y-1);
            changeView(x, y+1);
            changeView(x+1, y);
        }else if(y == 0){
            changeView(x-1, y);
            changeView(x+1, y);
            changeView(x, y+1);
        }else if(x == topTileX - 1){
            changeView(x, y-1);
            changeView(x, y+1);
            changeView(x-1, y);
        }else if(y == topTileY-1){
            changeView(x-1, y);
            changeView(x+1, y);
            changeView(x, y-1);
        }
        //resto del tablero
        else{
            changeView(x-1, y);
            changeView(x+1, y);
            changeView(x, y-1);
            changeView(x, y+1);
        }

        //actualizar marcador de pulsaciones
        numberOfClicks++;
        tvNumberOfClicks.setText(getString(R.string.score_clicks) + numberOfClicks);
        //se ha acabado la partida?
        checkIfFinished();
    }

    private void changeView(int x, int y){
        TileView tt = (TileView) findViewById(ids[x][y]);
        int newIndex = tt.getNewIndex();
        values[x][y] = newIndex;
        tt.setBackgroundResource(pictures[newIndex]);
        tt.invalidate();
    }

    private void checkIfFinished(){
        int targetValue = values[0][0];
        for(int i = 0; i < topTileY; i++){
            for(int j = 0; j < topTileX; j++){
                if(values[j][i] != targetValue) return;
            }
        }
        Intent resultIntent = new Intent((String) null);
        resultIntent.putExtra("clicks", numberOfClicks);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}
