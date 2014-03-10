package appulse.simple.dictionary;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


//NOT USED
public class CustomTouchListener implements View.OnTouchListener {     
    public boolean onTouch(View view, MotionEvent motionEvent) {
    switch(motionEvent.getAction()){            
            case MotionEvent.ACTION_DOWN:
            ((TextView)view).setTextColor(Color.parseColor("#27ae60")); //white
                break;          
            case MotionEvent.ACTION_CANCEL:             
            case MotionEvent.ACTION_UP:
            ((TextView)view).setTextColor(Color.parseColor("#212221"));  //black
                break;
    } 
        return false;   
    } 
}
