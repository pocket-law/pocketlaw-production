package ca.ggolda.reference_criminal_code.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import ca.ggolda.reference_criminal_code.R;

public class DialogInfo extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog dialog;
    public Button btn_ok;

    public DialogInfo(Activity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.c = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_info);

//        yes = (Button) findViewById(R.id.btn_yes);
//        yes.setOnClickListener(this);

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
