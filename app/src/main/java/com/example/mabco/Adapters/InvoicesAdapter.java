package com.example.mabco.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mabco.Classes.Invoice_Hdr;
import com.example.mabco.Classes.Showroom;
import com.example.mabco.R;

import java.util.ArrayList;

public class InvoicesAdapter extends RecyclerView.Adapter<InvoicesAdapter.ViewHolder> {
    private Context context;
   public ArrayList<Invoice_Hdr> invoiceHdrs ;
    private int selectedPos = RecyclerView.NO_POSITION;
    private InvoicesAdapter.OnClickListener onClickListener;
    private int selectedItem;
    public InvoicesAdapter.OnClickListener getOnClickListener() {
        return onClickListener;
    }
    public void setOnClickListener(InvoicesAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public InvoicesAdapter(Context context, ArrayList<Invoice_Hdr> invoiceHdrs) {
        this.context = context;
        this.invoiceHdrs = invoiceHdrs;
    }

    @NonNull
    @Override
    public InvoicesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.invoice_item,parent,false);
        return new InvoicesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoicesAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            Invoice_Hdr InvoiceHdr = invoiceHdrs.get(position);
            holder.invoice_no.setText( InvoiceHdr.getInvoice_no());
            holder.invoice_ctrated_dt.setText( InvoiceHdr.getDate());
            holder.invoice_price.setText( InvoiceHdr.getTotalFinalPrice());
            holder.invoice_showroom.setText( InvoiceHdr.getLocation());
            holder.itemView .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("ShowroomAdapter", "Item clicked at position: " + position);
                    if (onClickListener != null) {
                        onClickListener.onClick(position, InvoiceHdr);
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {return invoiceHdrs != null ? invoiceHdrs.size() : 0; }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView invoice_no,invoice_ctrated_dt,invoice_price,invoice_showroom;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            invoice_no=itemView.findViewById(R.id.inv_no);
            invoice_ctrated_dt=itemView.findViewById(R.id.invoice_ctrated_dt);
            invoice_price=itemView.findViewById(R.id.invoice_price);
            invoice_showroom = itemView.findViewById(R.id.invoice_showroom);
        }
    }
    public interface OnClickListener {
        void onClick(int position, Invoice_Hdr Invoice);
    }
    private static Activity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        return (Activity) context;
    }
}
