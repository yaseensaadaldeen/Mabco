package com.mabcoApp.mabco.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mabcoApp.mabco.Classes.Invoice_Hdr;
import com.mabcoApp.mabco.R;

import java.util.ArrayList;

public class InvoicesAdapter extends RecyclerView.Adapter<InvoicesAdapter.ViewHolder> {
    private Context context;
    public ArrayList<Invoice_Hdr> invoiceHdrs;
    private InvoicesAdapter.OnClickListener onClickListener;

    public InvoicesAdapter(Context context, ArrayList<Invoice_Hdr> invoiceHdrs) {
        this.context = context;
        this.invoiceHdrs = invoiceHdrs;
    }

    public InvoicesAdapter.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(InvoicesAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public InvoicesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.invoice_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            Invoice_Hdr invoiceHdr = invoiceHdrs.get(position);
            holder.invoice_no.setText(invoiceHdr.getInvoice_no());
            holder.invoice_ctrated_dt.setText(invoiceHdr.getDate());
            holder.invoice_price.setText(invoiceHdr.getTotalFinalPrice());
            holder.invoice_showroom.setText(invoiceHdr.getLocation());
            holder.itemView.setTag(invoiceHdrs.get(position));
            holder.invoice_loading.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("InvoiceAdapter", "Item clicked at position: " + position);
                    if (onClickListener != null) {
                        onClickListener.onClick(position, invoiceHdr);
                        holder.invoice_loading.setVisibility(View.VISIBLE);
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return invoiceHdrs.size() ;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView invoice_no, invoice_ctrated_dt, invoice_price, invoice_showroom;
        RelativeLayout invoice_loading;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            invoice_no = itemView.findViewById(R.id.inv_no);
            invoice_ctrated_dt = itemView.findViewById(R.id.invoice_ctrated_dt);
            invoice_price = itemView.findViewById(R.id.invoice_price);
            invoice_showroom = itemView.findViewById(R.id.invoice_showroom);
            invoice_loading= itemView.findViewById(R.id.invoice_loading);
        }
    }
    public interface OnClickListener {
        void onClick(int position, Invoice_Hdr invoice_hdr);
    }
    private static Activity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        return (Activity) context;
    }
}