package org.devcon.android.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Ye Lin Aung on 13/11/15.
 */
public class RobotoCondensedBoldTV extends TextView {
    public RobotoCondensedBoldTV(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        createFont();
    }

    public RobotoCondensedBoldTV(Context context, AttributeSet attrs) {
        super(context, attrs);
        createFont();
    }

    public RobotoCondensedBoldTV(Context context) {
        super(context);
        createFont();
    }

    public void createFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/RobotoCondensed-Bold.ttf");
        setTypeface(font);
    }
}
