package com.example.generatepdf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText campoNome;
    private EditText campoDescricao;
    private EditText campoValor;
    private static final int CREATEPDF = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        campoNome = findViewById(R.id.nomeCar);
        campoDescricao = findViewById(R.id.descriptionCar);
        campoValor = findViewById(R.id.valorCar);

        FloatingActionButton fabGerar = findViewById(R.id.fab);

        fabGerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                criarPdf("Relatorio");
            }
        });
    }

    public void criarPdf(String title){
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, title);
        startActivityForResult(intent, CREATEPDF);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CREATEPDF){
            if(data.getData()!=null){
                if(!(TextUtils.isEmpty(campoNome.getText())) && !(TextUtils.isEmpty(campoValor.getText())) && !(TextUtils.isEmpty(campoDescricao.getText()))) {
                    Uri caminhDoArquivo = data.getData();

                    String nomeVeiculo = campoNome.getText().toString();
                    String valorVeiculo = campoValor.getText().toString();
                    String descricaoVeiculo = campoDescricao.getText().toString();

                    PdfDocument pdfDocument = new PdfDocument();
                    Paint paint = new Paint();
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1240, 1754, 1).create();
                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                    Canvas canvas = page.getCanvas();
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setTextSize(36f);
                    paint.setFakeBoldText(true);
                    canvas.drawText("Relatório Veicular", pageInfo.getPageWidth()/2, 50, paint);

                    paint.setTextAlign(Paint.Align.LEFT);
                    paint.setTextSize(24f);
                    paint.setFakeBoldText(false);

                    canvas.drawText("Nome veículo:"+nomeVeiculo, 50,75, paint);
                    canvas.drawText("Valor do veículo:"+valorVeiculo, 50, 105, paint);
                    canvas.drawText("Descrição do veículo:"+descricaoVeiculo, 50, 135, paint);

                    canvas.drawLine(48,80,pageInfo.getPageWidth()-100, 90, paint);
                    canvas.drawLine(48,110,pageInfo.getPageWidth()-100, 120, paint);
                    canvas.drawLine(48,140,pageInfo.getPageWidth()-100, 150, paint);
                    pdfDocument.finishPage(page);
                    gravarPdf(caminhDoArquivo, pdfDocument);
                }
            }
        }
    }

    private void gravarPdf(Uri caminhDoArquivo, PdfDocument pdfDocument) {
        try{
            BufferedOutputStream stream = new BufferedOutputStream(Objects.requireNonNull(getContentResolver().openOutputStream(caminhDoArquivo)));
            pdfDocument.writeTo(stream);
            pdfDocument.close();
            stream.flush();
            Toast.makeText(this, "PDF Gravado Com Sucesso", Toast.LENGTH_LONG).show();

        }catch (FileNotFoundException e){
            Toast.makeText(this, "Erro de arquivo não encontrado", Toast.LENGTH_LONG).show();
        }catch (IOException e){
            Toast.makeText(this, "Erro de entrada e saída", Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Toast.makeText(this, "Erro desconhecido"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}