package com.example.haball.Payment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haball.Distribution_Login.Distribution_Login;
import com.example.haball.Distributor.ui.payments.PaymentRequestDashboard;
import com.example.haball.Distributor.ui.payments.Payments_Fragment;
import com.example.haball.Distributor.ui.payments.ProofOfPaymentForm;
import com.example.haball.Distributor.ui.shipments.DistributorShipment_ViewDashboard;
import com.example.haball.Distributor.ui.shipments.Shipments_Fragments;
import com.example.haball.Payment.Consolidate_Fragment;
import com.example.haball.R;
import com.example.haball.Registration.Registration_Activity;
import com.example.haball.Retailor.ui.Dashboard.DashBoardFragment;

import java.text.DecimalFormat;
import java.util.List;
public class Consolidate_Fragment_View_Adapter extends RecyclerView.Adapter<Consolidate_Fragment_View_Adapter.ViewHolder> {

    private Context context;
    private List<Consolidate_Fragment_Model> consolidatePaymentsDetailList;
    private FragmentTransaction fragmentTransaction;

    public Consolidate_Fragment_View_Adapter(Context context, List<Consolidate_Fragment_Model> consolidatePaymentsDetailsList) {
        this.context = context;
        this.consolidatePaymentsDetailList = consolidatePaymentsDetailsList;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view_inflate = LayoutInflater.from(context).inflate(R.layout.payments_consolidate_view_recycler,parent,false);
        return new Consolidate_Fragment_View_Adapter.ViewHolder(view_inflate);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.consolidate_invoice_no_view.setText(consolidatePaymentsDetailList.get(position).getInvoiceNumber());
        DecimalFormat formatter1 = new DecimalFormat("#,###,##0.00");
        String yourFormattedString1 = formatter1.format(Integer.parseInt(consolidatePaymentsDetailList.get(position).getTotalPrice()));
        holder.consolidate_amount_value.setText(yourFormattedString1);
        if(consolidatePaymentsDetailList.get(position).getInvoiceType().equals("0"))
            holder.consolidate_status_view_current.setText("Pending");
        else if (consolidatePaymentsDetailList.get(position).getInvoiceType().equals("1"))
            holder.consolidate_status_view_current.setText("Perfoma Invoice");
        else if (consolidatePaymentsDetailList.get(position).getInvoiceType().equals("2"))
            holder.consolidate_status_view_current.setText("Partially Paid");
        else if (consolidatePaymentsDetailList.get(position).getInvoiceType().equals("3"))
            holder.consolidate_status_view_current.setText("Paid");
        else if (consolidatePaymentsDetailList.get(position).getInvoiceType().equals("-1"))
            holder.consolidate_status_view_current.setText("Payment Processing");

        holder.menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popup = new PopupMenu(context, view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.cosolidate_payment_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.consiladate_view:
//                                Consolidate_Fragment_View_Payment consolidateFragment = new Consolidate_Fragment_View_Payment();
//                                Bundle args = new Bundle();
//                                args.putString("ConsolidateInvoiceId", consolidatePaymentsDetailList.get(position).getID());
//                                consolidateFragment.setArguments(args);
//                                fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
//                                fragmentTransaction.replace(R.id.main_container, consolidateFragment);
//                                fragmentTransaction.commit();
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return consolidatePaymentsDetailList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView consolidate_invoice_no_view ,consolidate_amount_value,consolidate_status_view_current;
        public ImageButton menu_btn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            consolidate_invoice_no_view = itemView.findViewById(R.id.consolidate_invoice_no_view);
            consolidate_amount_value = itemView.findViewById(R.id.consolidate_amount_value);
            consolidate_status_view_current = itemView.findViewById(R.id.consolidate_status_view_current);
            menu_btn = itemView.findViewById(R.id.consolidate_menu_btn);
        }
    }
}