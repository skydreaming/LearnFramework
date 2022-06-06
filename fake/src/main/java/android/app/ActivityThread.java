package android.app;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

public class ActivityThread {
    class H extends Handler {

        private Handler target;

        public H(Handler target) {
            this.target = target;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
            Log.e("test", "handleMessage " + msg.what);
            target.handleMessage(msg);
        }
    }
}
