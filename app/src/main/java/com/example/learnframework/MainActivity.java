package com.example.learnframework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.android.dx.Code;
import com.android.dx.DexMaker;
import com.android.dx.FieldId;
import com.android.dx.Local;
import com.android.dx.MethodId;
import com.android.dx.TypeId;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    final Date date = new Date();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Log.e("test", date.toString());

        Log.e("test", getClass().toString());
        hook();

//        init();
        findViewById(R.id.tv).postDelayed(new Runnable() {
            @Override
            public void run() {
//                init();
            }
        }, 4000);
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));

//                try {
//                    Field fDate = MainActivity.class.getDeclaredField("date");
//                    fDate.setAccessible(true);
//                    fDate.set(MainActivity.this, new Date());
//                    Log.e("test", date.toString());
//                } catch (NoSuchFieldException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    public class Abc {

    }


    private void hook() {
        DexMaker dexMaker = new DexMaker();
        TypeId<?> tMyH = TypeId.get("Lcom/example/learnframework/MyH;");
        Class clzH = null;
        try {
//            clzH = Class.forName("android.app.ActivityThread$H");
            clzH = Class.forName("com.example.learnframework.MyThread");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        TypeId<?> tH = TypeId.get("Landroid/app/ActivityThread$H");
        TypeId<?> tH = TypeId.get(clzH);
        dexMaker.declare(tMyH, "MyH.generated", Modifier.PUBLIC, tH);

        TypeId<Message> tMessage = TypeId.get(Message.class);
        MethodId<?, Void> mHandleMessage = tMyH.getMethod(TypeId.VOID, "handleMessage", tMessage);
        Code cHandleMessage = dexMaker.declare(mHandleMessage, Modifier.PUBLIC);
        Local<String> lMsg = cHandleMessage.newLocal(TypeId.STRING);

        Local<Message> lMessage = cHandleMessage.getParameter(0, tMessage);
        Local lThis = cHandleMessage.getThis(tMyH);
//        Local<Void> lVoid = cHandleMessage.newLocal(TypeId.VOID);


        TypeId<System> tSystem = TypeId.get(System.class);
        TypeId<PrintStream> tPrintStream = TypeId.get(PrintStream.class);
        FieldId<System, PrintStream> fOut = tSystem.getField(tPrintStream, "out");
        Local<PrintStream> lPrintStream = cHandleMessage.newLocal(tPrintStream);
        cHandleMessage.sget(fOut, lPrintStream);
        MethodId<PrintStream, Void> mPrintln = tPrintStream.getMethod(TypeId.VOID, "println", TypeId.STRING);

        cHandleMessage.loadConstant(lMsg, "handleMessage");
        cHandleMessage.invokeVirtual(mPrintln, null, lPrintStream, lMsg);

        cHandleMessage.invokeSuper(mHandleMessage, null, lThis, lMessage);


        //------------------------------------
        File dataDir = getDataDir();
        File dm = new File(dataDir, "dm");
        if (!dm.exists()) {
            dm.mkdirs();
        }
        File outputDir = dm;
        ClassLoader loader = null;
        try {
            loader = dexMaker.generateAndLoad(MainActivity.class.getClassLoader(),
                    outputDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Class<?> clzMyH = null;
        try {
            clzMyH = loader.loadClass("com.example.learnframework.MyH");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Log.e("test", "clz: " + clzMyH);

    }

    private static class MyHadler extends Handler {
        private Handler target;

        public MyHadler(Handler target) {
            this.target = target;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
            Log.e("test", "handleMessage " + msg.what);
            target.handleMessage(msg);
        }
    }

    private void init() {



        try {
            Class<?> clzActivityThread = Class.forName("android.app.ActivityThread");
            Field fCurrentActivityThread = clzActivityThread.getDeclaredField("sCurrentActivityThread");
            fCurrentActivityThread.setAccessible(true);
            Object activityThread = fCurrentActivityThread.get(clzActivityThread);
            Field fH = clzActivityThread.getDeclaredField("mH");
            fH.setAccessible(true);
            Handler mH = (Handler) fH.get(activityThread);
//            fH.set(activityThread, new MyHadler(mH));

//            File data = Environment.getDataDirectory();
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String sourceDir = packageInfo.applicationInfo.sourceDir;
            MyClassLoader classLoader = new MyClassLoader(sourceDir, null, null, getClassLoader());
            Class<?> aClass = classLoader.loadClass("android.app.ActivityThread$H");
//            Class<?> clzActivityThread = Class.forName("android.app.ActivityThread");

            Class<?> aClass1 = Class.forName("android.app.ActivityThread$H");



            Log.e("test", "== ? " + (aClass == aClass1));
            Constructor<?> constructor = aClass.getConstructor(clzActivityThread, Handler.class);
            constructor.setAccessible(true);
            Object proxy = constructor.newInstance(activityThread, mH);


            Field fClassLoader = Class.class.getDeclaredField("classLoader");
            fClassLoader.setAccessible(true);
            fClassLoader.set(aClass, aClass1.getClassLoader());
            fH.set(activityThread, proxy);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        ClassLoader cl;

    }
}