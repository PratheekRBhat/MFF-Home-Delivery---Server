package com.example.mffhomedeliveryserver.Common;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.TextView;

import com.example.mffhomedeliveryserver.Model.Category;
import com.example.mffhomedeliveryserver.Model.ServerUser;

public class Common {
    public static final String SERVER_REF = "Server";
    public static final String CATEGORY_REF = "Category";

    public static final int DEFAULT_COLUMN_COUNT = 0;
    public static final int FULL_WIDTH_COLUMN = 1;

    public static ServerUser currentServerUser;
    public static Category categorySelected;

    //To set the header of the navigation bar.
    public static void setSpanString(String s, String name, TextView txt_user) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(s);
        SpannableString spannableString = new SpannableString(name);
        StyleSpan boldspan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldspan, 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(spannableString);
        txt_user.setText(builder, TextView.BufferType.SPANNABLE);
    }
}
