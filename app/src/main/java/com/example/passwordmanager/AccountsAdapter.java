package com.example.passwordmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.List;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<Account> accountList;
    private List<Account> accountListFiltered;
    private AccountsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, password;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            password = view.findViewById(R.id.password);
            thumbnail = view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onAccountSelected(accountListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public AccountsAdapter(Context context, List<Account> accountList, AccountsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.accountList = accountList;
        this.accountListFiltered = accountList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.account_row, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Account account = accountListFiltered.get(position);
        String letter = String.valueOf(account.accountTitle.charAt(0));
        TextDrawable drawable = TextDrawable.builder().buildRound(letter, account.color);
        holder.thumbnail.setImageDrawable(drawable);
        holder.title.setText(account.accountTitle);
        holder.password.setText(account.accountPassword);

//        Glide.with(context)
//                .load(account.getImage())
//                .apply(RequestOptions.circleCropTransform())
//                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return accountListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    accountListFiltered = accountList;
                } else {
                    List<Account> filteredList = new ArrayList<>();
                    for (Account row : accountList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.accountTitle.toLowerCase().contains(charString.toLowerCase()) || row.accountPassword.contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    accountListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = accountListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                accountListFiltered = (ArrayList<Account>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface AccountsAdapterListener {
        void onAccountSelected(Account account);
    }
}
