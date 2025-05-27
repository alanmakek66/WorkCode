package com.example.client;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Message> messages;

    public MessageAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) { // self message
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message2, parent, false);
            return new MessageViewHolder(view);
        } else { // other message
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
        if (message.isDeleted()) {
            // 如果消息已删除，则添加删除线效果
            SpannableString spannableString = new SpannableString(message.getText());
            StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
            spannableString.setSpan(strikethroughSpan, 0, message.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            messageViewHolder.messageBubble.setText(spannableString);
        } else {
            // 否则，直接设置文本
            messageViewHolder.messageBubble.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        // 返回自定义的视图类型
        return messages.get(position).getSelfOrNot().equals("1") ? 1 : 0;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageBubble;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageBubble = itemView.findViewById(R.id.messageBubble);
        }
    }
}
