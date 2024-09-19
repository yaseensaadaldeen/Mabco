package com.mabcoApp.mabco.ui.Invoices;


import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.mabcoApp.mabco.Classes.Invoice_Hdr;
import com.mabcoApp.mabco.HttpsTrustManager;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InvoiceDetailsDialog extends Dialog {
    public String invoice_no;
    private Font arabic, arabicSmall, normal, normalSmall;
    SharedPreferences InvoicesPreference, PersonalPreference, UserPreferance;
    Context context;
    public RequestQueue requestQueue;
    private File pdfFile, orginalPdfFile;
    Image image;
    public static int PAGECount;
    String local = "en";
    private BaseFont arial;
    private BaseFont bfBold;
    private String filename = "Sample.pdf";
    private String filepath = "MyInvoices";
    private static final int REQUEST_WRITE_STORAGE = 112;
    View view;
    private static Font bold = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    private RequestQueue queue;
    float textSize;
    TableLayout tableLayout, tableLayout2, tableLayout3;
    ImageButton btnCancel;
    ImageView warranty_terms_img;
    TextView loc, total;
    Boolean IsEco = false;
    Button save_as_PDF;
    String username , phone_no ,trn_dt,mobile_slno,total_final_price,loc_name;
    JSONArray showrooms;
    Invoice_Hdr invoiceHdr;
    public InvoiceDetailsDialog(@NonNull Context context, String invoice_no) {
        super(context);
        this.invoice_no = invoice_no;
    }

    public InvoiceDetailsDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected InvoiceDetailsDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            view = LayoutInflater.from(getContext()).inflate(R.layout.invoice_details_dialog, null);
            context = getContext();
            textSize = context.getResources().getDimension(R.dimen.second_text_size);
            btnCancel = view.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(v -> cancel());
            PersonalPreference = context.getSharedPreferences("PersonalData", Context.MODE_PRIVATE);
            UserPreferance = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
            InvoicesPreference = context.getSharedPreferences("InvoiceData", Context.MODE_PRIVATE);
            local = PersonalPreference.getString("Language", "ar");
            tableLayout = view.findViewById(R.id.detailtable1);
            tableLayout2 = view.findViewById(R.id.detailtable);
            tableLayout3 = view.findViewById(R.id.allshowrooms);
            save_as_PDF = view.findViewById(R.id.pdf);
            loc = view.findViewById(R.id.loc);
            total = view.findViewById(R.id.total);
            warranty_terms_img = view.findViewById(R.id.warranty_terms_img);
            filename = invoice_no +"Sample.pdf";
            Glide.with(context).asBitmap() // This ensures that Glide returns a Bitmap
                    .load("https://mabcoonline.com/images/warranty/WARRANTY%20NOTE.jpg").listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, com.bumptech.glide.request.target.Target<Bitmap> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            try {
                                image = Image.getInstance(stream.toByteArray());
                            } catch (BadElementException | IOException e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    }).into(warranty_terms_img);
             invoiceHdr = GetInvoiceHdr();
            total.setText(invoiceHdr.getTotalFinalPrice());
            loc.setText(invoiceHdr.getLocation());

            pdfFile = new File(context.getExternalFilesDir(null), "Mabco/" + filename );

            File directory = new File(context.getExternalFilesDir(null), "Mabco");
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    Log.e("PDFCreation", "Failed to create directory");

                }
            }

            createHeader(invoiceHdr);
            String InvoiceDet = InvoicesPreference.getString("invoice_det_" + invoice_no, "");
            createTableLayout(InvoiceDet);
            getAllShowrooms();
            save_as_PDF.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    //if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Permission granted.", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (convertToPDF() != null) {
                            PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
                            try {
                                File file = new File(context.getExternalFilesDir(null), "Mabco/" + filename );

                                PrintDocumentAdapter printAdapter = new PdfDocumentAdapter(context, file.getAbsolutePath());
                                printManager.print("Document", printAdapter, new PrintAttributes.Builder().build());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setTitle("Invoice Settings").setItems(R.array.send_choise_arrays, new DialogInterface.OnClickListener() {
//                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//                        public void onClick(DialogInterface dialog, int which) {
//                            switch (which) {
//                                case 0: {
//
//                                    break;
//
//                                }
//                                case 1: {
//                                    generateDirectPDFDownload();
//                                    break;
//
//                                }
//                            }
//
//                        }
//
//                });
//                AlertDialog sendListChoise = builder.create();
//                        sendListChoise.show();
                //  } else {
                //     Toast.makeText(context, "Permission not granted.", Toast.LENGTH_SHORT).show();
                // }
            }
        });
        setContentView(view);
        setCanceledOnTouchOutside(true);
    } catch(
    Resources.NotFoundException e)

    {
        Toast.makeText(context, "not available", Toast.LENGTH_LONG).show();
        cancel();
    }

}

    private Invoice_Hdr GetInvoiceHdr() {
        Invoice_Hdr invoiceHdr = null;
        String invoices = InvoicesPreference.getString("Invoices", "");
        if (!invoices.equals("")) {
            JSONObject jsonResponse = null;
            try {
                jsonResponse = new JSONObject(invoices);
                JSONArray InvoicesHdr = jsonResponse.optJSONArray("GetInvoiceHdrResult");
                if (InvoicesHdr == null) {
                    Toast.makeText(context, "not available", Toast.LENGTH_LONG).show();
                    cancel();
                    return null;
                } else {
                    try {
                        for (int i = 0; i < InvoicesHdr.length(); i++) {
                            JSONObject pp = InvoicesHdr.getJSONObject(i);
                            String inv_no = "#" + pp.optString("inv_no");

                            trn_dt = pp.optString("trn_dt");
                            String offer_discount = pp.optString("offer_discount");
                            String total_price = pp.optString("total_price").replace(".000", "") + (local.equals("ar") ? " ل.س " : " SP");
                            total_final_price = pp.optString("total_final_price").replace(".000", "") + (local.equals("ar") ? " ل.س " : " SP");
                            loc_name = pp.optString("loc_name");
                            if (pp.optString("inv_no").equals(invoice_no.substring(1))) {
                                invoiceHdr = new Invoice_Hdr(inv_no, total_price, total_final_price, offer_discount, "", loc_name, "", trn_dt);
                                return invoiceHdr;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        cancel();
                        return null;
                    }
                }
            } catch (JSONException e) {
                cancel();
                throw new RuntimeException(e);

            }
        }
        cancel();
        return null;
    }


    private void createHeader(Invoice_Hdr invoiceHdr) {
        String custm_name = UserPreferance.getString("UserName", "");
        username = UserPreferance.getString("UserName", "");
         phone_no = UserPreferance.getString("PhoneNO", "");
        TableRow tableRow1 = new TableRow(context);
        tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        //inv_NO
        TextView textView1 = new TextView(context);
        textView1.setText("Invoice Num: " + invoiceHdr.getInvoice_no());
        textView1.setTextColor(Color.BLACK);
        textView1.setBackgroundResource(R.drawable.bright_gray_cell);
        textView1.setGravity(Gravity.CENTER);
        textView1.setTextSize(textSize);


        //CustName
        TextView textView2 = new TextView(context);
        textView2.setText("Custm Name : " + custm_name);
        textView2.setTextColor(Color.BLACK);
        textView2.setBackgroundResource(R.drawable.bright_gray_cell);
        textView2.setGravity(Gravity.CENTER);
        textView2.setTextSize(textSize);


        tableRow1.addView(textView1, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
        tableRow1.addView(textView2, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));

        //Date
        TextView textView3 = new TextView(context);
        textView3.setText("Date : " + invoiceHdr.getDate());
        textView3.setTextColor(Color.BLACK);
        textView3.setBackgroundResource(R.drawable.bright_gray_cell);
        textView3.setGravity(Gravity.CENTER);
        textView3.setTextSize(textSize);


        //row3
        TableRow tableRow2 = new TableRow(context);
        tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));


        //custPhone
        TextView textView7 = new TextView(context);
        textView7.setText("Custmer Phone : " + phone_no);
        textView7.setTextColor(Color.BLACK);
        textView7.setBackgroundResource(R.drawable.bright_gray_cell);
        textView7.setGravity(Gravity.CENTER);
        textView7.setTextSize(textSize);


        tableRow2.addView(textView7, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));

        tableRow2.addView(textView3, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));

        tableLayout2.addView(tableRow1, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

        tableLayout2.addView(tableRow2, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

    }

    private void createTableLayout(String invoice_detRes) {
        TableRow tableRow1 = new TableRow(context);
        tableRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        //Desc
        TextView textView1 = new TextView(context);
        textView1.setText("Desc");
        textView1.setTextColor(Color.BLACK);
        textView1.setBackgroundResource(R.drawable.dark_gray_cell);
        textView1.setGravity(Gravity.CENTER);
        textView1.setTextSize(textSize);


        //Mobile sl.No.
        TextView textView2 = new TextView(context);
        textView2.setText("Mobile sl.No.");
        textView2.setTextColor(Color.BLACK);
        textView2.setBackgroundResource(R.drawable.dark_gray_cell);
        textView2.setGravity(Gravity.CENTER);
        textView2.setTextSize(textSize);


        //Unit price
        TextView textView3 = new TextView(context);
        textView3.setText("Unit price");
        textView3.setTextColor(Color.BLACK);
        textView3.setBackgroundResource(R.drawable.dark_gray_cell);
        textView3.setGravity(Gravity.CENTER);
        textView3.setTextSize(textSize);

        //Final Price
        TextView textView5 = new TextView(context);
        textView5.setText("Final Price");
        textView5.setTextColor(Color.BLACK);
        textView5.setBackgroundResource(R.drawable.dark_gray_cell);
        textView5.setGravity(Gravity.CENTER);
        textView5.setTextSize(textSize);


        tableRow1.addView(textView1, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
        tableRow1.addView(textView2, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
        tableRow1.addView(textView3, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));

        tableRow1.addView(textView5, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));

        tableLayout2.addView(tableRow1, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));


        try {

            JSONObject jsonResponse = new JSONObject(invoice_detRes);
            JSONArray jsonArray2 = jsonResponse.optJSONArray("GetInvoiceDetResult");
            // JSONArray jsonArray2 = new JSONArray(detResult);
            for (int i = 0; i < jsonArray2.length(); i++) {

                TableRow tableRow10 = new TableRow(context);
                tableRow10.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                //Desc
                TextView textView10 = new TextView(context);
                textView10.setText(jsonArray2.getJSONObject(i).getString("stk_desc"));
                textView10.setTextColor(Color.BLACK);
                textView10.setBackgroundResource(R.drawable.dark_gray_cell);
                textView10.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                textView10.setSingleLine(false);
                textView10.setTextSize(textSize);


                //Mobile sl.No.
                TextView textView20 = new TextView(context);
                textView20.setText(jsonArray2.getJSONObject(i).getString("mobile_slno"));
                textView20.setTextColor(Color.BLACK);
                textView20.setBackgroundResource(R.drawable.dark_gray_cell);
                textView20.setTextSize(textSize);


                //Unit price
                TextView textView30 = new TextView(context);
                textView30.setText(jsonArray2.getJSONObject(i).getString("final_price"));
                textView30.setTextColor(Color.BLACK);
                textView30.setBackgroundResource(R.drawable.dark_gray_cell);
                textView30.setGravity(Gravity.CENTER);
                textView30.setTextSize(textSize);

                //Final Price
                TextView textView50 = new TextView(context);
                textView50.setText(jsonArray2.getJSONObject(i).getString("final_price"));
                textView50.setTextColor(Color.BLACK);
                textView50.setBackgroundResource(R.drawable.dark_gray_cell);
                textView50.setGravity(Gravity.CENTER);
                textView50.setTextSize(textSize);


                tableRow10.addView(textView10, new TableRow.LayoutParams(100, TableRow.LayoutParams.WRAP_CONTENT, 1));
                tableRow10.addView(textView20, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
                tableRow10.addView(textView30, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));

                tableRow10.addView(textView50, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));

                tableLayout2.addView(tableRow10, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                if (jsonArray2.getJSONObject(i).getString("cat_code").equals("09")) IsEco = true;
            }
            if (IsEco.equals(true)) {
                Glide.with(context).asBitmap() // This ensures that Glide returns a Bitmap
                        .load("https://mabcoonline.com/images/warranty/EcoFlow_warranty.bmp").listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, com.bumptech.glide.request.target.Target<Bitmap> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {

                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                try {
                                    image = Image.getInstance(stream.toByteArray());
                                } catch (BadElementException | IOException e) {
                                    e.printStackTrace();
                                }
                                return true;
                            }
                        }).into(warranty_terms_img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void getAllShowrooms() {
        requestQueue = Volley.newRequestQueue(context);

        HttpsTrustManager.allowAllSSL();
        String url = UrlEndPoint.General + "Service1.svc/GetAllShowrooms";
        StringRequest strRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
//                            ArrayList<String> invoice_detailses = new ArrayList<>();
                    JSONObject jsonResponse = new JSONObject(response);
                     showrooms = jsonResponse.optJSONArray("GetAllShowroomsResult");
                    if (showrooms == null) {
                        Toast.makeText(context, "not available", Toast.LENGTH_LONG).show();
                    } else {

                        for (int i = 0; i < showrooms.length(); i++) {
                            JSONObject pp = showrooms.getJSONObject(i);
                            String Loc1 = pp.optString("Loc1");
                            String Phone1 = pp.optString("Phone1");
                            String Loc2 = pp.optString("Loc2");
                            String Phone2 = pp.optString("Phone2");
                            String Loc3 = pp.optString("Loc3");
                            String Phone3 = pp.optString("Phone3");
                            TableRow tableRow10 = new TableRow(context);
                            tableRow10.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                            //Loc1
                            TextView textView10 = new TextView(context);
                            textView10.setText(Loc1);
                            textView10.setTextColor(Color.BLACK);
                            textView10.setBackgroundResource(R.drawable.dark_pink_cell);
                            textView10.setGravity(Gravity.CENTER);
                            textView10.setTextSize(textSize);


                            //Phone1
                            TextView textView20 = new TextView(context);
                            textView20.setText(Phone1);
                            textView20.setTextColor(Color.BLACK);
                            textView20.setBackgroundResource(R.drawable.dark_pink_cell);
                            textView20.setGravity(Gravity.CENTER);
                            textView20.setTextSize(textSize);


                            //Loc2
                            TextView textView30 = new TextView(context);
                            textView30.setText(Loc2);
                            textView30.setTextColor(Color.BLACK);
                            textView30.setBackgroundResource(R.drawable.dark_pink_cell);
                            textView30.setGravity(Gravity.CENTER);
                            textView30.setTextSize(textSize);

                            //Phone2
                            TextView textView40 = new TextView(context);
                            textView40.setText(Phone2);
                            textView40.setTextColor(Color.BLACK);
                            textView40.setBackgroundResource(R.drawable.dark_pink_cell);
                            textView40.setGravity(Gravity.CENTER);
                            textView40.setTextSize(textSize);


                            //Loc3
                            TextView textView50 = new TextView(context);
                            textView50.setText(Loc3);
                            textView50.setTextColor(Color.BLACK);
                            textView50.setBackgroundResource(R.drawable.dark_pink_cell);
                            textView50.setGravity(Gravity.CENTER);
                            textView50.setTextSize(textSize);


                            //Phone3
                            TextView textView60 = new TextView(context);
                            textView60.setText(Phone3);
                            textView60.setTextColor(Color.BLACK);
                            textView60.setBackgroundResource(R.drawable.dark_pink_cell);
                            textView60.setGravity(Gravity.CENTER);
                            textView60.setTextSize(textSize);


                            tableRow10.addView(textView10, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
                            tableRow10.addView(textView20, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
                            tableRow10.addView(textView30, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
                            tableRow10.addView(textView40, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
                            tableRow10.addView(textView50, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
                            tableRow10.addView(textView60, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));


                            tableLayout3.addView(tableRow10, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));


                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   Handle Error
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("X-Content-Type-Options", "nosniff");
                params.put("X-XSS-Protection", "0");
                params.put("X-Frame-Options", "DENY");
                //..add other headers
                return params;
            }
        };

        requestQueue.add(strRequest);

    }

    public String convertToPDF() {
        Document document = new Document(PageSize.A4);
        try {


            PdfWriter docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();


            PdfContentByte cb = docWriter.getDirectContent();
            //initialize fonts for text printing
            initializeFonts();

            PdfPTable title = new PdfPTable(1);
            title.setLockedWidth(true);
            title.setTotalWidth(PageSize.A4.getWidth() - 15);

            PdfPCell cellTitle = new PdfPCell(new Phrase(loc_name));
            cellTitle.setBorder(Rectangle.NO_BORDER);
            cellTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
            title.addCell(cellTitle);

            cellTitle = new PdfPCell(new Phrase("Proof Of Purchase"));
            cellTitle.setBorder(Rectangle.NO_BORDER);
            cellTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
            title.addCell(cellTitle);

            title.setHorizontalAlignment(Element.ALIGN_LEFT);
            document.add(title);

            document.add(Chunk.NEWLINE);


            PdfPTable tableHeader = new PdfPTable(2);
            tableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            //tableHeader.setWidthPercentage(100);
            tableHeader.setLockedWidth(true);
            tableHeader.setTotalWidth(PageSize.A4.getWidth());


            //inv no
            PdfPCell cellHeader = new PdfPCell();
            Paragraph p = new Paragraph(context.getResources().getString(R.string.arabicNum) + " " + invoice_no, arabicSmall);
            p.setAlignment(PdfPCell.ALIGN_LEFT);
            cellHeader.addElement(p);
            cellHeader.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            tableHeader.addCell(cellHeader);


            // custm name
            cellHeader = new PdfPCell();
            p = new Paragraph(context.getResources().getString(R.string.thanks_for_customer) + "\n" + context.getResources().getString(R.string.arabicName) + " " + username, arabicSmall);
            p.setAlignment(PdfPCell.ALIGN_RIGHT);
            cellHeader.addElement(p);
            cellHeader.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            tableHeader.addCell(cellHeader);

            // date inv
            cellHeader = new PdfPCell();
            p = new Paragraph(context.getResources().getString(R.string.arabicDate) + " " + trn_dt, arabicSmall);
            p.setAlignment(PdfPCell.ALIGN_LEFT);
            cellHeader.addElement(p);
            cellHeader.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            tableHeader.addCell(cellHeader);

            //custm phone
            cellHeader = new PdfPCell();
            p = new Paragraph(context.getResources().getString(R.string.arabicPhoneCustm) + " " +   phone_no, arabicSmall);
            p.setAlignment(PdfPCell.ALIGN_RIGHT);
            cellHeader.addElement(p);
            cellHeader.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            tableHeader.addCell(cellHeader);

            tableHeader.setHorizontalAlignment(Element.ALIGN_RIGHT);
            document.add(tableHeader);

            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);


            float[] columnWidths = {15f, 6f, 4f, 2f, 4f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(5);
            table.setLockedWidth(true);
            table.setTotalWidth(PageSize.A4.getWidth());
            table.setWidths(columnWidths);

            // set table width a percentage of the page width
            //table.setTotalWidth(PageSize.A5.getWidth());
            table.setWidthPercentage(90);

            PdfPCell cell = new PdfPCell(new Phrase("Desc", normal));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Mobile Sl.No", normal));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Unit Price", normal));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Disc", normal));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Final Price", normal));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            table.setHeaderRows(1);
           String invoice_detRes=InvoicesPreference.getString("invoice_det_"+ invoice_no,"[]");
            JSONObject jsonResponse = new JSONObject(invoice_detRes);
            JSONArray jsonArray2 = jsonResponse.optJSONArray("GetInvoiceDetResult");


            for (int i = 0; i < jsonArray2.length(); i++) {

                if (jsonArray2.getJSONObject(i).getString("cat_code").equals("06"))//this is warranty item
                    continue;

                String desc = jsonArray2.getJSONObject(i).getString("stk_desc");

                cell = new PdfPCell(new Phrase(desc, normalSmall));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                table.addCell(cell);

                //head
                cell = new PdfPCell(new Phrase(jsonArray2.getJSONObject(i).getString("mobile_slno"), normal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                table.addCell(cell);

                //unit price
                cell = new PdfPCell(new Phrase(jsonArray2.getJSONObject(i).getString("final_price"), normal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("0", normal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                table.addCell(cell);

                //final price
                cell = new PdfPCell(new Phrase(jsonArray2.getJSONObject(i).getString("final_price"), normal));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                table.addCell(cell);

            }
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            document.add(table);


            PdfPTable tableTotal = new PdfPTable(2);
            // tableTotal.setWidthPercentage(100);
            tableTotal.setLockedWidth(true);
            tableTotal.setTotalWidth(PageSize.A4.getWidth());

            //total
            PdfPCell cellTotal = new PdfPCell(new Phrase("Total", bold));
            cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTotal.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            tableTotal.addCell(cellTotal);

            //total value
            cellTotal = new PdfPCell(new Phrase(total_final_price, normal));
            cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTotal.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            tableTotal.addCell(cellTotal);

            tableTotal.setHorizontalAlignment(Element.ALIGN_LEFT);
            document.add(tableTotal);

            document.add(Chunk.NEWLINE);


            if (showrooms != null) {


                PdfPTable showroomsTable = new PdfPTable(6);
                float[] columnWidth = {3f, 3f, 3f, 3f, 3f, 3f};
                showroomsTable.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                showroomsTable.setLockedWidth(true);
                showroomsTable.setTotalWidth(PageSize.A4.getWidth());
                showroomsTable.setWidths(columnWidth);


                for (int i = 0; i < showrooms.length(); i++) {
//                    Showrooms showrooms = all_showrooms.get(i);

                    JSONObject pp = showrooms.getJSONObject(i);
                    String Loc1 = pp.optString("Loc1");
                    String Phone1 = pp.optString("Phone1");
                    String Loc2 = pp.optString("Loc2");
                    String Phone2 = pp.optString("Phone2");
                    String Loc3 = pp.optString("Loc3");
                    String Phone3 = pp.optString("Phone3");


                    if (Phone1 != null) {

                        //first
                        cellHeader = new PdfPCell();
                        p = new Paragraph(Phone1, arabicSmall);
                        p.setAlignment(PdfPCell.ALIGN_LEFT);
                        cellHeader.setBorder(com.itextpdf.text.Rectangle.LEFT | com.itextpdf.text.Rectangle.RIGHT);
                        cellHeader.addElement(p);
                        showroomsTable.addCell(cellHeader);

                        cellHeader = new PdfPCell();
                        p = new Paragraph(Loc1, arabicSmall);
                        cellHeader.setBorder(com.itextpdf.text.Rectangle.LEFT | com.itextpdf.text.Rectangle.RIGHT);
                        p.setAlignment(PdfPCell.ALIGN_RIGHT);
                        cellHeader.addElement(p);
                        showroomsTable.addCell(cellHeader);


                        //first
                        cellHeader = new PdfPCell();
                        p = new Paragraph(Phone2, arabicSmall);
                        p.setAlignment(PdfPCell.ALIGN_LEFT);
                        cellHeader.setBorder(com.itextpdf.text.Rectangle.LEFT | com.itextpdf.text.Rectangle.RIGHT);
                        cellHeader.addElement(p);
                        showroomsTable.addCell(cellHeader);

                        cellHeader = new PdfPCell();
                        p = new Paragraph(Loc2, arabicSmall);
                        cellHeader.setBorder(com.itextpdf.text.Rectangle.LEFT | com.itextpdf.text.Rectangle.RIGHT);
                        p.setAlignment(PdfPCell.ALIGN_RIGHT);
                        cellHeader.addElement(p);
                        showroomsTable.addCell(cellHeader);


                        //first
                        cellHeader = new PdfPCell();
                        p = new Paragraph(Phone3, arabicSmall);
                        p.setAlignment(PdfPCell.ALIGN_LEFT);
                        cellHeader.setBorder(com.itextpdf.text.Rectangle.LEFT | com.itextpdf.text.Rectangle.RIGHT);
                        cellHeader.addElement(p);
                        showroomsTable.addCell(cellHeader);

                        cellHeader = new PdfPCell();
                        p = new Paragraph(Loc3, arabicSmall);
                        cellHeader.setBorder(com.itextpdf.text.Rectangle.LEFT | com.itextpdf.text.Rectangle.RIGHT);
                        p.setAlignment(PdfPCell.ALIGN_RIGHT);
                        cellHeader.addElement(p);
                        showroomsTable.addCell(cellHeader);


                    }
                }

                showroomsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
                showroomsTable.writeSelectedRows(0, -1, document.leftMargin(), showroomsTable.getTotalHeight() + document.bottom(document.bottomMargin()), docWriter.getDirectContent());

                PdfPTable titletable = new PdfPTable(1);
                titletable.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                titletable.setLockedWidth(true);
                titletable.setTotalWidth(PageSize.A4.getWidth());

                cellHeader = new PdfPCell();
                p = new Paragraph(" صالات مابكو ", arabic);
                p.setAlignment(PdfPCell.ALIGN_CENTER);
                cellHeader.addElement(p);
                cellHeader.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                titletable.addCell(cellHeader);
                titletable.setHorizontalAlignment(Element.ALIGN_RIGHT);
                titletable.writeSelectedRows(0, -1, document.leftMargin(), titletable.getTotalHeight() + document.bottom(document.bottomMargin() + showroomsTable.getTotalHeight()), docWriter.getDirectContent());


                if (image != null) {
                    image.scaleToFit(PageSize.A4.getWidth(), PageSize.A5.getHeight());
                    image.setAbsolutePosition(document.leftMargin(), titletable.getTotalHeight() + document.bottom(document.bottomMargin() + showroomsTable.getTotalHeight()));
                }


                document.add(image);
            }

            PAGECount = document.getPageNumber();
            document.close();

        } catch (Exception e1) {
            e1.printStackTrace();

        }
        return String.valueOf(document);
    }
    private void initializeFonts() {

        try {
            bfBold = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            arial = BaseFont.createFont("assets/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            arabic = new Font(arial, 14);
            arabicSmall = new Font(arial, 8);
            normal = FontFactory.getFont(FontFactory.HELVETICA, 8);
            normalSmall = FontFactory.getFont(FontFactory.HELVETICA, 8);

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateDirectPDFDownload() {

        Document document = new Document(PageSize.A4.rotate(), PageSize.A5.getWidth() + 5, 5, 5, 5);
        try {


            PdfWriter docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();


            PdfContentByte cb = docWriter.getDirectContent();
            //initialize fonts for text printing
            initializeFonts();

            PdfPTable title = new PdfPTable(1);
            title.setLockedWidth(true);
            title.setTotalWidth(PageSize.A4.getWidth());

            PdfPCell cellTitle = new PdfPCell(new Phrase(loc_name));
            cellTitle.setBorder(Rectangle.NO_BORDER);
            cellTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
            title.addCell(cellTitle);

            cellTitle = new PdfPCell(new Phrase("Proof Of Purchase"));
            cellTitle.setBorder(Rectangle.NO_BORDER);
            cellTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
            title.addCell(cellTitle);

            title.setHorizontalAlignment(Element.ALIGN_LEFT);
            document.add(title);

            document.add(Chunk.NEWLINE);


            PdfPTable tableHeader = new PdfPTable(2);
            tableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            //tableHeader.setWidthPercentage(100);
            tableHeader.setLockedWidth(true);
            tableHeader.setTotalWidth(PageSize.A4.getWidth());


            //inv no
            PdfPCell cellHeader = new PdfPCell();
            Paragraph p = new Paragraph(context.getResources().getString(R.string.arabicNum) + " " + invoice_no, arabicSmall);
            p.setAlignment(PdfPCell.ALIGN_LEFT);
            cellHeader.addElement(p);
            cellHeader.setBorder(Rectangle.NO_BORDER);
            tableHeader.addCell(cellHeader);

            // custm name
            cellHeader = new PdfPCell();
            p = new Paragraph(context.getResources().getString(R.string.thanks_for_customer) + "\n" + context.getResources().getString(R.string.arabicName) + " " + username, arabicSmall);
            p.setAlignment(PdfPCell.ALIGN_RIGHT);
            cellHeader.addElement(p);
            cellHeader.setBorder(Rectangle.NO_BORDER);
            tableHeader.addCell(cellHeader);

            // date inv
            cellHeader = new PdfPCell();
            p = new Paragraph(context.getResources().getString(R.string.arabicDate) + " " + trn_dt, arabicSmall);
            p.setAlignment(PdfPCell.ALIGN_LEFT);
            cellHeader.addElement(p);
            cellHeader.setBorder(Rectangle.NO_BORDER);
            tableHeader.addCell(cellHeader);

            //custm phone
            cellHeader = new PdfPCell();
            p = new Paragraph(context.getResources().getString(R.string.arabicPhoneCustm) + " " + phone_no, arabicSmall);
            p.setAlignment(PdfPCell.ALIGN_RIGHT);
            cellHeader.addElement(p);
            cellHeader.setBorder(Rectangle.NO_BORDER);
            tableHeader.addCell(cellHeader);

            tableHeader.setHorizontalAlignment(Element.ALIGN_RIGHT);
            document.add(tableHeader);

            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);


            float[] columnWidths = {15f, 6f, 4f, 2f, 4f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(5);
            table.setLockedWidth(true);
            table.setTotalWidth(PageSize.A4.getWidth());
            table.setWidths(columnWidths);

            // set table width a percentage of the page width
            //table.setTotalWidth(PageSize.A5.getWidth());
            table.setWidthPercentage(90);

            PdfPCell cell = new PdfPCell(new Phrase("Desc", normal));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Mobile Sl.No", normal));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Unit Price", normal));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Disc", normal));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Final Price", normal));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            table.setHeaderRows(1);

            String invoice_detRes=InvoicesPreference.getString("invoice_det_"+ invoice_no,"[]");
            JSONObject jsonResponse = new JSONObject(invoice_detRes);
            JSONArray jsonArray2 = jsonResponse.optJSONArray("GetInvoiceDetResult");
            for (int i = 0; i < jsonArray2.length(); i++) {

                if (jsonArray2.getJSONObject(i).getString("cat_code").equals("06"))//this is warranty item
                    continue;

                String desc = jsonArray2.getJSONObject(i).getString("stk_desc");

                cell = new PdfPCell(new Phrase(desc, normalSmall));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                //head
                cell = new PdfPCell(new Phrase(jsonArray2.getJSONObject(i).getString("mobile_slno"), normal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                //unit price
                cell = new PdfPCell(new Phrase(jsonArray2.getJSONObject(i).getString("final_price"), normal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("0", normal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                //final price
                cell = new PdfPCell(new Phrase(jsonArray2.getJSONObject(i).getString("final_price"), normal));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

            }
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            document.add(table);


            PdfPTable tableTotal = new PdfPTable(2);
            // tableTotal.setWidthPercentage(100);
            tableTotal.setLockedWidth(true);
            tableTotal.setTotalWidth(PageSize.A4.getWidth());

            //total
            PdfPCell cellTotal = new PdfPCell(new Phrase("Total", bold));
            cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTotal.setBorder(Rectangle.NO_BORDER);
            tableTotal.addCell(cellTotal);

            //total value
            cellTotal = new PdfPCell(new Phrase(total_final_price, normal));
            cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTotal.setBorder(Rectangle.NO_BORDER);
            tableTotal.addCell(cellTotal);

            tableTotal.setHorizontalAlignment(Element.ALIGN_LEFT);
            document.add(tableTotal);

            document.add(Chunk.NEWLINE);


            if (showrooms != null) {


                PdfPTable showroomsTable = new PdfPTable(6);
                float[] columnWidth = {3f, 3f, 3f, 3f, 3f, 3f};
                showroomsTable.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                showroomsTable.setLockedWidth(true);
                showroomsTable.setTotalWidth(PageSize.A4.getWidth());
                showroomsTable.setWidths(columnWidth);


                for (int i = 0; i < showrooms.length(); i++) {

                    JSONObject pp = showrooms.getJSONObject(i);
                    String Loc1 = pp.optString("Loc1");
                    String Phone1 = pp.optString("Phone1");
                    String Loc2 = pp.optString("Loc2");
                    String Phone2 = pp.optString("Phone2");
                    String Loc3 = pp.optString("Loc3");
                    String Phone3 = pp.optString("Phone3");

                    if (Phone1 != null) {

                        //first
                        cellHeader = new PdfPCell();
                        p = new Paragraph(Phone1, arabicSmall);
                        p.setAlignment(PdfPCell.ALIGN_LEFT);
                        cellHeader.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                        cellHeader.addElement(p);
                        showroomsTable.addCell(cellHeader);

                        cellHeader = new PdfPCell();
                        p = new Paragraph(Loc1, arabicSmall);
                        cellHeader.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                        p.setAlignment(PdfPCell.ALIGN_RIGHT);
                        cellHeader.addElement(p);
                        showroomsTable.addCell(cellHeader);


                        //first
                        cellHeader = new PdfPCell();
                        p = new Paragraph(Phone2, arabicSmall);
                        p.setAlignment(PdfPCell.ALIGN_LEFT);
                        cellHeader.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                        cellHeader.addElement(p);
                        showroomsTable.addCell(cellHeader);

                        cellHeader = new PdfPCell();
                        p = new Paragraph(Loc2, arabicSmall);
                        cellHeader.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                        p.setAlignment(PdfPCell.ALIGN_RIGHT);
                        cellHeader.addElement(p);
                        showroomsTable.addCell(cellHeader);


                        //first
                        cellHeader = new PdfPCell();
                        p = new Paragraph(Phone3, arabicSmall);
                        p.setAlignment(PdfPCell.ALIGN_LEFT);
                        cellHeader.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                        cellHeader.addElement(p);
                        showroomsTable.addCell(cellHeader);

                        cellHeader = new PdfPCell();
                        p = new Paragraph(Loc3, arabicSmall);
                        cellHeader.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                        p.setAlignment(PdfPCell.ALIGN_RIGHT);
                        cellHeader.addElement(p);
                        showroomsTable.addCell(cellHeader);
                    }
                }

                showroomsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
                showroomsTable.writeSelectedRows(0, -1, document.leftMargin(), showroomsTable.getTotalHeight() + document.bottom(document.bottomMargin()), docWriter.getDirectContent());

                // document.add(showroomsTable);
                PdfPTable titletable = new PdfPTable(1);
                titletable.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                titletable.setLockedWidth(true);
                titletable.setTotalWidth(PageSize.A4.getWidth());

                cellHeader = new PdfPCell();
                p = new Paragraph(" صالات مابكو ", arabic);
                p.setAlignment(PdfPCell.ALIGN_CENTER);
                cellHeader.addElement(p);
                cellHeader.setBorder(Rectangle.NO_BORDER);
                titletable.addCell(cellHeader);
                titletable.setHorizontalAlignment(Element.ALIGN_RIGHT);
                titletable.writeSelectedRows(0, -1, document.leftMargin(), titletable.getTotalHeight() + document.bottom(document.bottomMargin() + showroomsTable.getTotalHeight()), docWriter.getDirectContent());


                if (image != null) {
                    image.scaleToFit(PageSize.A4.getWidth(), PageSize.A5.getHeight());
                    image.setAbsolutePosition(document.leftMargin(), titletable.getTotalHeight() + document.bottom(document.bottomMargin() + showroomsTable.getTotalHeight()));
                }


                document.add(image);
            }

            PAGECount = document.getPageNumber();
            document.close();
            //cancel();


            NotificationCompat.Builder b = new NotificationCompat.Builder(context);
            Intent intent = new Intent(context, PdfViewerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            intent.setData(data);
            intent.putExtra("file",  filename);
           // intent.putExtra("key", "clicked");
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

//            processIntent(getIntent());
            int notifyID = 1;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String CHANNEL_ID = "my_channel_01";// The id of the channel.
                CharSequence name = "channel_name";// The user-visible name of the channel.
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.createNotificationChannel(mChannel);
                b.setChannelId(CHANNEL_ID);
            }
            b.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.mabco)
                    .setTicker("Downloading PDf .......")
                    .setContentTitle("Download PDF")
                    .setContentText("Click To open the PDf")
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                    .setContentIntent(contentIntent)
                    .setContentInfo("Info");

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, b.build());
            Intent intent1 = new Intent(context, PdfViewerActivity.class);

            context.startActivity(intent1);
        } catch (Exception e1) {
            e1.printStackTrace();

        }
    }

}