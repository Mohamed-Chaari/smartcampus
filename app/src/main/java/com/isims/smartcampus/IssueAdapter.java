package com.isims.smartcampus;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.isims.smartcampus.network.EcoIssueDto;

import java.util.ArrayList;
import java.util.List;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.IssueViewHolder> {

    private List<EcoIssueDto> issueList = new ArrayList<>();
    private final OnIssueResolveListener resolveListener;

    public interface OnIssueResolveListener {
        void onResolveClick(Long issueId);
    }

    public IssueAdapter(OnIssueResolveListener resolveListener) {
        this.resolveListener = resolveListener;
    }

    public void setIssues(List<EcoIssueDto> issues) {
        this.issueList = issues;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_issue, parent, false);
        return new IssueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueViewHolder holder, int position) {
        EcoIssueDto issue = issueList.get(position);

        holder.textEquipment.setText(issue.getEquipmentType() != null ? issue.getEquipmentType() : issue.getCategory());
        holder.textLocation.setText("Location: " + (issue.getLocation() != null ? issue.getLocation() : "Unknown"));
        holder.textDescription.setText(issue.getDescription());
        holder.textPriority.setText("Priority: " + (issue.getPriority() != null ? issue.getPriority() : "MEDIUM"));
        
        String status = issue.getStatus() != null ? issue.getStatus() : "PENDING";
        holder.textStatus.setText(status);
        
        if ("RESOLVED".equals(status)) {
            holder.textStatus.setBackgroundColor(Color.parseColor("#388E3C")); // Green
            holder.btnResolve.setVisibility(View.GONE);
        } else {
            holder.textStatus.setBackgroundColor(Color.parseColor("#FFA000")); // Amber
            holder.btnResolve.setVisibility(View.VISIBLE);
        }
        
        holder.btnResolve.setOnClickListener(v -> {
            if (resolveListener != null) {
                resolveListener.onResolveClick(issue.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return issueList.size();
    }

    static class IssueViewHolder extends RecyclerView.ViewHolder {
        TextView textEquipment, textLocation, textDescription, textPriority, textStatus;
        Button btnResolve;

        public IssueViewHolder(@NonNull View itemView) {
            super(itemView);
            textEquipment = itemView.findViewById(R.id.text_equipment);
            textLocation = itemView.findViewById(R.id.text_location);
            textDescription = itemView.findViewById(R.id.text_description);
            textPriority = itemView.findViewById(R.id.text_priority);
            textStatus = itemView.findViewById(R.id.text_status);
            btnResolve = itemView.findViewById(R.id.btn_resolve);
        }
    }
}
