package org.techintheworld.www.edots;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import SecuGen.FDxSDKPro.JSGFPLib;
import SecuGen.FDxSDKPro.SGANSITemplateInfo;
import SecuGen.FDxSDKPro.SGAutoOnEventNotifier;
import SecuGen.FDxSDKPro.SGFDxDeviceName;
import SecuGen.FDxSDKPro.SGFDxErrorCode;
import SecuGen.FDxSDKPro.SGFDxSecurityLevel;
import SecuGen.FDxSDKPro.SGFDxTemplateFormat;
import SecuGen.FDxSDKPro.SGFingerInfo;
import SecuGen.FDxSDKPro.SGFingerPresentEvent;
import SecuGen.FDxSDKPro.SGISOTemplateInfo;
import SecuGen.FDxSDKPro.SGImpressionType;


public class RegisterFingerPrintActivity extends Activity
        implements View.OnClickListener, java.lang.Runnable, SGFingerPresentEvent {

    private static final String TAG = "SecuGen USB";

    private Button mCapture;
    private Button mButtonRegister;
    private Button mButtonMatch;
    private Button mButtonLed;
    private Button mSDKTest;
    private EditText mEditLog;
    private android.widget.TextView mTextViewResult;
    private android.widget.CheckBox mCheckBoxMatched;
    private android.widget.ToggleButton mToggleButtonSmartCapture;
    private android.widget.ToggleButton mToggleButtonAutoOn;
    private PendingIntent mPermissionIntent;
    private ImageView mImageViewFingerprint;
    private ImageView mImageViewRegister;
    private ImageView mImageViewVerify;
    private byte[] mRegisterImage;
    private byte[] mVerifyImage;
    private byte[] mRegisterTemplate;
    private byte[] mVerifyTemplate;
    private int[] mMaxTemplateSize;
    private int mImageWidth;
    private int mImageHeight;
    private int[] grayBuffer;
    private Bitmap grayBitmap;
    private IntentFilter filter; //2014-04-11
    private SGAutoOnEventNotifier autoOn;
    private boolean mLed;
    private boolean mAutoOnEnabled;

    private JSGFPLib sgfplib;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_finger_print, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void debugMessage(String message) {
        this.mEditLog.append(message);
        this.mEditLog.invalidate(); //TODO trying to get Edit log to update after each line written
    }

    //RILEY
    //This broadcast receiver is necessary to get user permissions to access the attached USB device
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //DEBUG Log.d(TAG,"Enter mUsbReceiver.onReceive()");
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //DEBUG Log.d(TAG, "Vendor ID : " + device.getVendorId() + "\n");
                            //DEBUG Log.d(TAG, "Product ID: " + device.getProductId() + "\n");
                            debugMessage("Vendor ID : " + device.getVendorId() + "\n");
                            debugMessage("Product ID: " + device.getProductId() + "\n");
                        } else
                            Log.e(TAG, "mUsbReceiver.onReceive() Device is null");
                    } else
                        Log.e(TAG, "mUsbReceiver.onReceive() permission denied for device " + device);
                }
            }
        }
    };

    //RILEY
    //This message handler is used to access local resources not
    //accessible by SGFingerPresentCallback() because it is called by
    //a separate thread.
    public Handler fingerDetectedHandler = new Handler() {
        // @Override
        public void handleMessage(Message msg) {
            //Handle the message
            CaptureFingerPrint();
            if (mAutoOnEnabled) {
                mToggleButtonAutoOn.toggle();
                EnableControls();
            }
        }
    };

    public void EnableControls() {
        this.mCapture.setClickable(true);
        this.mCapture.setTextColor(getResources().getColor(android.R.color.white));
        this.mButtonRegister.setClickable(true);
        this.mButtonRegister.setTextColor(getResources().getColor(android.R.color.white));
        this.mButtonMatch.setClickable(true);
        this.mButtonMatch.setTextColor(getResources().getColor(android.R.color.white));
    }

    public void DisableControls() {
        this.mCapture.setClickable(false);
        this.mCapture.setTextColor(getResources().getColor(android.R.color.black));
        this.mButtonRegister.setClickable(false);
        this.mButtonRegister.setTextColor(getResources().getColor(android.R.color.black));
        this.mButtonMatch.setClickable(false);
        this.mButtonMatch.setTextColor(getResources().getColor(android.R.color.black));
    }


    //RILEY
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_finger_print);
        mCapture = (Button) findViewById(R.id.buttonCapture);
        mCapture.setOnClickListener(this);
        mButtonRegister = (Button) findViewById(R.id.buttonRegister);
        mButtonRegister.setOnClickListener(this);
        mButtonMatch = (Button) findViewById(R.id.buttonMatch);
        mButtonMatch.setOnClickListener(this);
        mButtonLed = (Button) findViewById(R.id.buttonLedOn);
        mButtonLed.setOnClickListener(this);
        mSDKTest = (Button) findViewById(R.id.buttonSDKTest);
        mSDKTest.setOnClickListener(this);
        mEditLog = (EditText) findViewById(R.id.editLog);
        mTextViewResult = (android.widget.TextView) findViewById(R.id.textViewResult);
        mCheckBoxMatched = (android.widget.CheckBox) findViewById(R.id.checkBoxMatched);
        mToggleButtonSmartCapture = (android.widget.ToggleButton) findViewById(R.id.toggleButtonSmartCapture);
        mToggleButtonSmartCapture.setOnClickListener(this);
        mToggleButtonAutoOn = (android.widget.ToggleButton) findViewById(R.id.toggleButtonAutoOn);
        mToggleButtonAutoOn.setOnClickListener(this);
        mImageViewFingerprint = (ImageView) findViewById(R.id.imageViewFingerprint);
        mImageViewRegister = (ImageView) findViewById(R.id.imageViewRegister);
        mImageViewVerify = (ImageView) findViewById(R.id.imageViewVerify);
        grayBuffer = new int[JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES * JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES];
        for (int i = 0; i < grayBuffer.length; ++i)
            grayBuffer[i] = android.graphics.Color.GRAY;
        grayBitmap = Bitmap.createBitmap(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES, Bitmap.Config.ARGB_8888);
        grayBitmap.setPixels(grayBuffer, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, 0, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES);
        mImageViewFingerprint.setImageBitmap(grayBitmap);

        int[] sintbuffer = new int[(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES / 2) * (JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES / 2)];
        for (int i = 0; i < sintbuffer.length; ++i)
            sintbuffer[i] = android.graphics.Color.GRAY;
        Bitmap sb = Bitmap.createBitmap(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES / 2, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES / 2, Bitmap.Config.ARGB_8888);
        sb.setPixels(sintbuffer, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES / 2, 0, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES / 2, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES / 2);
        mImageViewRegister.setImageBitmap(grayBitmap);
        mImageViewVerify.setImageBitmap(grayBitmap);

        mMaxTemplateSize = new int[1];

        //USB Permissions
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
        sgfplib = new JSGFPLib((UsbManager) getSystemService(Context.USB_SERVICE));
        this.mToggleButtonSmartCapture.toggle();


        debugMessage("jnisgfplib version: " + sgfplib.Version() + "\n");
        mLed = false;
        autoOn = new SGAutoOnEventNotifier(sgfplib, this);
        mAutoOnEnabled = false;
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause()");
        autoOn.stop();
        EnableControls();
        sgfplib.CloseDevice();
        unregisterReceiver(mUsbReceiver);
        mRegisterImage = null;
        mVerifyImage = null;
        mRegisterTemplate = null;
        mVerifyTemplate = null;
        mImageViewFingerprint.setImageBitmap(grayBitmap);
        mImageViewRegister.setImageBitmap(grayBitmap);
        mImageViewVerify.setImageBitmap(grayBitmap);
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        registerReceiver(mUsbReceiver, filter);
        long error = sgfplib.Init(SGFDxDeviceName.SG_DEV_AUTO);
        if (error != SGFDxErrorCode.SGFDX_ERROR_NONE) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            if (error == SGFDxErrorCode.SGFDX_ERROR_DEVICE_NOT_FOUND)
                dlgAlert.setMessage("The attached fingerprint device is not supported on Android");
            else
                dlgAlert.setMessage("Fingerprint device initialization failed!");
            dlgAlert.setTitle("SecuGen Fingerprint SDK");
            dlgAlert.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                            return;
                        }
                    }
            );
            dlgAlert.setCancelable(false);
            dlgAlert.create().show();
        } else {
            UsbDevice usbDevice = sgfplib.GetUsbDevice();
            if (usbDevice == null) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage("SDU04P or SDU03P fingerprint sensor not found!");
                dlgAlert.setTitle("SecuGen Fingerprint SDK");
                dlgAlert.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                                return;
                            }
                        }
                );
                dlgAlert.setCancelable(false);
                dlgAlert.create().show();
            } else {
                sgfplib.GetUsbManager().requestPermission(usbDevice, mPermissionIntent);
                error = sgfplib.OpenDevice(0);
                debugMessage("OpenDevice() ret: " + error + "\n");
                SecuGen.FDxSDKPro.SGDeviceInfoParam deviceInfo = new SecuGen.FDxSDKPro.SGDeviceInfoParam();
                error = sgfplib.GetDeviceInfo(deviceInfo);
                debugMessage("GetDeviceInfo() ret: " + error + "\n");
                mImageWidth = deviceInfo.imageWidth;
                mImageHeight = deviceInfo.imageHeight;
                sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
                sgfplib.GetMaxTemplateSize(mMaxTemplateSize);
                debugMessage("TEMPLATE_FORMAT_SG400 SIZE: " + mMaxTemplateSize[0] + "\n");
                mRegisterTemplate = new byte[mMaxTemplateSize[0]];
                mVerifyTemplate = new byte[mMaxTemplateSize[0]];
                boolean smartCaptureEnabled = this.mToggleButtonSmartCapture.isChecked();
                if (smartCaptureEnabled)
                    sgfplib.WriteData((byte) 5, (byte) 1);
                else
                    sgfplib.WriteData((byte) 5, (byte) 0);
                if (mAutoOnEnabled) {
                    autoOn.start();
                    DisableControls();
                }
                //Thread thread = new Thread(this);
                //thread.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        sgfplib.CloseDevice();
        mRegisterImage = null;
        mVerifyImage = null;
        mRegisterTemplate = null;
        mVerifyTemplate = null;
        sgfplib.Close();
        super.onDestroy();
    }

    //Converts image to grayscale (NEW)
    public Bitmap toGrayscale(byte[] mImageBuffer) {
        byte[] Bits = new byte[mImageBuffer.length * 4];
        for (int i = 0; i < mImageBuffer.length; i++) {
            Bits[i * 4] = Bits[i * 4 + 1] = Bits[i * 4 + 2] = mImageBuffer[i]; // Invert the source bits
            Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
        }

        Bitmap bmpGrayscale = Bitmap.createBitmap(mImageWidth, mImageHeight, Bitmap.Config.ARGB_8888);
        //Bitmap bm contains the fingerprint img
        bmpGrayscale.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));
        return bmpGrayscale;
    }


    //Converts image to grayscale (NEW)
    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int color = bmpOriginal.getPixel(x, y);
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = color & 0xFF;
                int gray = (r + g + b) / 3;
                color = Color.rgb(gray, gray, gray);
                //color = Color.rgb(r/3, g/3, b/3);
                bmpGrayscale.setPixel(x, y, color);
            }
        }
        return bmpGrayscale;
    }

    //Converts image to binary (OLD)
    public Bitmap toBinary(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }


    public void DumpFile(String fileName, byte[] buffer) {
        //Uncomment section below to dump images and templates to SD card
        /*
        try {
            File myFile = new File("/sdcard/Download/" + fileName);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            fOut.write(buffer,0,buffer.length);
            fOut.close();
        } catch (Exception e) {
            debugMessage("Exception when writing file" + fileName);
        }
       */
    }

    public void SGFingerPresentCallback() {
        autoOn.stop();
        fingerDetectedHandler.sendMessage(new Message());
    }

    public void CaptureFingerPrint() {
        long dwTimeStart = 0, dwTimeEnd = 0, dwTimeElapsed = 0;
        this.mCheckBoxMatched.setChecked(false);
        byte[] buffer = new byte[mImageWidth * mImageHeight];
        dwTimeStart = System.currentTimeMillis();
        long result = sgfplib.GetImage(buffer);
        DumpFile("capture.raw", buffer);
        dwTimeEnd = System.currentTimeMillis();
        dwTimeElapsed = dwTimeEnd - dwTimeStart;
        debugMessage("getImage() ret:" + result + " [" + dwTimeElapsed + "ms]\n");
        mTextViewResult.setText("getImage() ret: " + result + " [" + dwTimeElapsed + "ms]\n"); 

/*      
 *  No longer used
 *  
        Bitmap b = Bitmap.createBitmap(mImageWidth,mImageHeight, Bitmap.Config.ARGB_8888);
        b.setHasAlpha(false);
        int[] intbuffer = new int[mImageWidth*mImageHeight];
        for (int i=0; i<intbuffer.length; ++i)
            intbuffer[i] = (int) buffer[i];
        b.setPixels(intbuffer, 0, mImageWidth, 0, 0, mImageWidth, mImageHeight);
        mImageViewFingerprint.setImageBitmap(this.toGrayscale(b));  
*/
        mImageViewFingerprint.setImageBitmap(this.toGrayscale(buffer));
    }

    public void onClick(View v) {
        long dwTimeStart = 0, dwTimeEnd = 0, dwTimeElapsed = 0;
        if (v == mToggleButtonSmartCapture) {
            if (mToggleButtonSmartCapture.isChecked())
                sgfplib.WriteData((byte) 5, (byte) 1); //Enable Smart Capture
            else
                sgfplib.WriteData((byte) 5, (byte) 0); //Disable Smart Capture          
        }

        if (v == mCapture) {
            //DEBUG Log.d(TAG, "Pressed CAPTURE");
            CaptureFingerPrint();
        }
        if (v == mToggleButtonAutoOn) {
            if (mToggleButtonAutoOn.isChecked()) {
                mAutoOnEnabled = true;
                autoOn.start(); //Enable Auto On
                DisableControls();
            } else {
                mAutoOnEnabled = false;
                autoOn.stop(); //Disable Auto On
                EnableControls();
            }

        }


        if (v == mButtonLed) {
            this.mCheckBoxMatched.setChecked(false);
            mLed = !mLed;
            dwTimeStart = System.currentTimeMillis();
            long result = sgfplib.SetLedOn(mLed);
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;
            debugMessage("setLedOn(" + mLed + ") ret:" + result + " [" + dwTimeElapsed + "ms]\n");
            mTextViewResult.setText("setLedOn(" + mLed + ") ret: " + result + " [" + dwTimeElapsed + "ms]\n");
        }
        if (v == mSDKTest) {
            SDKTest();
        }
        if (v == this.mButtonRegister) {
            //DEBUG Log.d(TAG, "Clicked REGISTER");
            debugMessage("Clicked REGISTER\n");
            if (mRegisterImage != null)
                mRegisterImage = null;
            mRegisterImage = new byte[mImageWidth * mImageHeight];

            this.mCheckBoxMatched.setChecked(false);
            ByteBuffer byteBuf = ByteBuffer.allocate(mImageWidth * mImageHeight);
            dwTimeStart = System.currentTimeMillis();
            long result = sgfplib.GetImage(mRegisterImage);
            DumpFile("register.raw", mRegisterImage);
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;
            debugMessage("GetImage() ret:" + result + " [" + dwTimeElapsed + "ms]\n");
            mImageViewFingerprint.setImageBitmap(this.toGrayscale(mRegisterImage));
            dwTimeStart = System.currentTimeMillis();
            result = sgfplib.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;
            debugMessage("SetTemplateFormat(SG400) ret:" + result + " [" + dwTimeElapsed + "ms]\n");
            SGFingerInfo fpInfo = new SGFingerInfo();
            for (int i = 0; i < mRegisterTemplate.length; ++i)
                mRegisterTemplate[i] = 0;
            dwTimeStart = System.currentTimeMillis();
            result = sgfplib.CreateTemplate(fpInfo, mRegisterImage, mRegisterTemplate);
            DumpFile("register.min", mRegisterTemplate);
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;
            debugMessage("CreateTemplate() ret:" + result + " [" + dwTimeElapsed + "ms]\n");
            mImageViewRegister.setImageBitmap(this.toGrayscale(mRegisterImage));
            mTextViewResult.setText("Click Verify");
            mTextViewResult.setText(Integer.toString(mRegisterTemplate.length));
        }
        if (v == this.mButtonMatch) {
            //DEBUG Log.d(TAG, "Clicked MATCH");
            debugMessage("Clicked MATCH\n");
            if (mVerifyImage != null)
                mVerifyImage = null;
            mVerifyImage = new byte[mImageWidth * mImageHeight];
            ByteBuffer byteBuf = ByteBuffer.allocate(mImageWidth * mImageHeight);
            dwTimeStart = System.currentTimeMillis();
            long result = sgfplib.GetImage(mVerifyImage);
            DumpFile("verify.raw", mVerifyImage);
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;
            debugMessage("GetImage() ret:" + result + " [" + dwTimeElapsed + "ms]\n");
            mImageViewFingerprint.setImageBitmap(this.toGrayscale(mVerifyImage));
            mImageViewVerify.setImageBitmap(this.toGrayscale(mVerifyImage));
            dwTimeStart = System.currentTimeMillis();
            result = sgfplib.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;
            debugMessage("SetTemplateFormat(SG400) ret:" + result + " [" + dwTimeElapsed + "ms]\n");
            SGFingerInfo fpInfo = new SGFingerInfo();
            for (int i = 0; i < mVerifyTemplate.length; ++i)
                mVerifyTemplate[i] = 0;
            dwTimeStart = System.currentTimeMillis();
            result = sgfplib.CreateTemplate(fpInfo, mVerifyImage, mVerifyTemplate);
            DumpFile("verify.min", mVerifyTemplate);
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;
            debugMessage("CreateTemplate() ret:" + result + " [" + dwTimeElapsed + "ms]\n");
            boolean[] matched = new boolean[1];
            dwTimeStart = System.currentTimeMillis();
            result = sgfplib.MatchTemplate(mRegisterTemplate, mVerifyTemplate, SGFDxSecurityLevel.SL_NORMAL, matched);
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;
            debugMessage("MatchTemplate() ret:" + result + " [" + dwTimeElapsed + "ms]\n");
            if (matched[0]) {
                mTextViewResult.setText("MATCHED!!\n");
                this.mCheckBoxMatched.setChecked(true);
                debugMessage("MATCHED!!\n");
            } else {
                mTextViewResult.setText("NOT MATCHED!!");
                this.mCheckBoxMatched.setChecked(false);
                debugMessage("NOT MATCHED!!\n");
            }
        }
    }


    private void SDKTest() {
        mTextViewResult.setText("");
        debugMessage("\n###############\n");
        debugMessage("### SDK Test  ###\n");
        debugMessage("###############\n");

        int X_SIZE = 248;
        int Y_SIZE = 292;

        long error = 0;
        byte[] sgTemplate1;
        byte[] sgTemplate2;
        byte[] sgTemplate3;
        byte[] ansiTemplate1;
        byte[] ansiTemplate2;
        byte[] isoTemplate1;
        byte[] isoTemplate2;

        int[] size = new int[1];
        int[] score = new int[1];
        int[] quality1 = new int[1];
        int[] quality2 = new int[1];
        int[] quality3 = new int[1];
        boolean[] matched = new boolean[1];

        byte[] finger1 = new byte[X_SIZE * Y_SIZE];
        byte[] finger2 = new byte[X_SIZE * Y_SIZE];
        byte[] finger3 = new byte[X_SIZE * Y_SIZE];
        try {
            InputStream fileInputStream = getResources().openRawResource(R.raw.finger_0_10_3);
            error = fileInputStream.read(finger1);
            fileInputStream.close();
        } catch (IOException ex) {
            debugMessage("Error: Unable to find fingerprint image R.raw.finger_0_10_3.\n");
            return;
        }
        try {
            InputStream fileInputStream = getResources().openRawResource(R.raw.finger_1_10_3);
            error = fileInputStream.read(finger2);
            fileInputStream.close();
        } catch (IOException ex) {
            debugMessage("Error: Unable to find fingerprint image R.raw.finger_1_10_3.\n");
            return;
        }
        try {
            InputStream fileInputStream = getResources().openRawResource(R.raw.finger_2_10_3);
            error = fileInputStream.read(finger3);
            fileInputStream.close();
        } catch (IOException ex) {
            debugMessage("Error: Unable to find fingerprint image R.raw.finger_2_10_3.\n");
            return;
        }

        JSGFPLib sgFplibSDKTest = new JSGFPLib((UsbManager) getSystemService(Context.USB_SERVICE));

        error = sgFplibSDKTest.InitEx(X_SIZE, Y_SIZE, 500);
        debugMessage("InitEx(" + X_SIZE + "," + Y_SIZE + ",500) ret:" + error + "\n");

        SGFingerInfo fpInfo1 = new SGFingerInfo();
        SGFingerInfo fpInfo2 = new SGFingerInfo();
        SGFingerInfo fpInfo3 = new SGFingerInfo();

        error = sgFplibSDKTest.GetImageQuality((long) X_SIZE, (long) Y_SIZE, finger1, quality1);
        debugMessage("GetImageQuality(R.raw.finger_0_10_3) ret:" + error + " Finger quality=" + quality1[0] + "\n");
        error = sgFplibSDKTest.GetImageQuality((long) X_SIZE, (long) Y_SIZE, finger2, quality2);
        debugMessage("GetImageQuality(R.raw.finger_1_10_3) ret:" + error + " Finger quality=" + quality2[0] + "\n");
        error = sgFplibSDKTest.GetImageQuality((long) X_SIZE, (long) Y_SIZE, finger3, quality3);
        debugMessage("GetImageQuality(R.raw.finger_2_10_3) ret:" + error + " Finger quality=" + quality3[0] + "\n");

        fpInfo1.FingerNumber = 1;
        fpInfo1.ImageQuality = quality1[0];
        fpInfo1.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
        fpInfo1.ViewNumber = 1;

        fpInfo2.FingerNumber = 1;
        fpInfo2.ImageQuality = quality2[0];
        fpInfo2.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
        fpInfo2.ViewNumber = 2;

        fpInfo3.FingerNumber = 1;
        fpInfo3.ImageQuality = quality3[0];
        fpInfo3.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
        fpInfo3.ViewNumber = 3;


        ///////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////        
        //TEST SG400
        debugMessage("#######################\n");
        debugMessage("TEST SG400\n");
        debugMessage("###\n###\n");
        error = sgFplibSDKTest.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
        debugMessage("SetTemplateFormat(SG400) ret:" + error + "\n");
        error = sgFplibSDKTest.GetMaxTemplateSize(size);
        debugMessage("GetMaxTemplateSize() ret:" + error + " SG400_MAX_SIZE=" + size[0] + "\n");

        sgTemplate1 = new byte[size[0]];
        sgTemplate2 = new byte[size[0]];
        sgTemplate3 = new byte[size[0]];

        //TEST DeviceInfo

        error = sgFplibSDKTest.CreateTemplate(null, finger1, sgTemplate1);
        debugMessage("CreateTemplate(finger3) ret:" + error + "\n");
        error = sgFplibSDKTest.GetTemplateSize(sgTemplate1, size);
        debugMessage("GetTemplateSize() ret:" + error + " size=" + size[0] + "\n");

        error = sgFplibSDKTest.CreateTemplate(null, finger2, sgTemplate2);
        debugMessage("CreateTemplate(finger2) ret:" + error + "\n");
        error = sgFplibSDKTest.GetTemplateSize(sgTemplate2, size);
        debugMessage("GetTemplateSize() ret:" + error + " size=" + size[0] + "\n");

        error = sgFplibSDKTest.CreateTemplate(null, finger3, sgTemplate3);
        debugMessage("CreateTemplate(finger3) ret:" + error + "\n");
        error = sgFplibSDKTest.GetTemplateSize(sgTemplate3, size);
        debugMessage("GetTemplateSize() ret:" + error + " size=" + size[0] + "\n");

        ///////////////////////////////////////////////////////////////////////////////////////////////
        error = sgFplibSDKTest.MatchTemplate(sgTemplate1, sgTemplate2, SGFDxSecurityLevel.SL_NORMAL, matched);
        debugMessage("MatchTemplate(sgTemplate1,sgTemplate2) ret:" + error + "\n");
        if (matched[0])
            debugMessage("MATCHED!!\n");
        else
            debugMessage("NOT MATCHED!!\n");

        error = sgFplibSDKTest.GetMatchingScore(sgTemplate1, sgTemplate2, score);
        debugMessage("GetMatchingScore(sgTemplate1, sgTemplate2) ret:" + error + ". Score:" + score[0] + "\n");


        ///////////////////////////////////////////////////////////////////////////////////////////////
        error = sgFplibSDKTest.MatchTemplate(sgTemplate1, sgTemplate3, SGFDxSecurityLevel.SL_NORMAL, matched);
        debugMessage("MatchTemplate(sgTemplate1,sgTemplate3) ret:" + error + "\n");
        if (matched[0])
            debugMessage("MATCHED!!\n");
        else
            debugMessage("NOT MATCHED!!\n");

        error = sgFplibSDKTest.GetMatchingScore(sgTemplate1, sgTemplate3, score);
        debugMessage("GetMatchingScore(sgTemplate1, sgTemplate3) ret:" + error + ". Score:" + score[0] + "\n");


        ///////////////////////////////////////////////////////////////////////////////////////////////
        error = sgFplibSDKTest.MatchTemplate(sgTemplate2, sgTemplate3, SGFDxSecurityLevel.SL_NORMAL, matched);
        debugMessage("MatchTemplate(sgTemplate2,sgTemplate3) ret:" + error + "\n");
        if (matched[0])
            debugMessage("MATCHED!!\n");
        else
            debugMessage("NOT MATCHED!!\n");

        error = sgFplibSDKTest.GetMatchingScore(sgTemplate2, sgTemplate3, score);
        debugMessage("GetMatchingScore(sgTemplate2, sgTemplate3) ret:" + error + ". Score:" + score[0] + "\n");


        ///////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////        
        //TEST ANSI378
        debugMessage("#######################\n");
        debugMessage("TEST ANSI378\n");
        debugMessage("###\n###\n");
        error = sgFplibSDKTest.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_ANSI378);
        debugMessage("SetTemplateFormat(ANSI378) ret:" + error + "\n");
        error = sgFplibSDKTest.GetMaxTemplateSize(size);
        debugMessage("GetMaxTemplateSize() ret:" + error + " ANSI378_MAX_SIZE=" + size[0] + "\n");

        ansiTemplate1 = new byte[size[0]];
        ansiTemplate2 = new byte[size[0]];

        error = sgFplibSDKTest.CreateTemplate(fpInfo1, finger1, ansiTemplate1);
        debugMessage("CreateTemplate(finger1) ret:" + error + "\n");
        error = sgFplibSDKTest.GetTemplateSize(ansiTemplate1, size);
        debugMessage("GetTemplateSize(ansi) ret:" + error + " size=" + size[0] + "\n");

        error = sgFplibSDKTest.CreateTemplate(fpInfo2, finger2, ansiTemplate2);
        debugMessage("CreateTemplate(finger2) ret:" + error + "\n");
        error = sgFplibSDKTest.GetTemplateSize(ansiTemplate2, size);
        debugMessage("GetTemplateSize(ansi) ret:" + error + " size=" + size[0] + "\n");

        error = sgFplibSDKTest.MatchTemplate(ansiTemplate1, ansiTemplate2, SGFDxSecurityLevel.SL_NORMAL, matched);
        debugMessage("MatchTemplate(ansi) ret:" + error + "\n");
        if (matched[0])
            debugMessage("MATCHED!!\n");
        else
            debugMessage("NOT MATCHED!!\n");

        error = sgFplibSDKTest.GetMatchingScore(ansiTemplate1, ansiTemplate2, score);
        debugMessage("GetMatchingScore(ansi) ret:" + error + ". Score:" + score[0] + "\n");

        error = sgFplibSDKTest.GetTemplateSizeAfterMerge(ansiTemplate1, ansiTemplate2, size);
        debugMessage("GetTemplateSizeAfterMerge(ansi) ret:" + error + ". Size:" + size[0] + "\n");

        byte[] mergedAnsiTemplate1 = new byte[size[0]];
        error = sgFplibSDKTest.MergeAnsiTemplate(ansiTemplate1, ansiTemplate2, mergedAnsiTemplate1);
        debugMessage("MergeAnsiTemplate() ret:" + error + "\n");

        error = sgFplibSDKTest.MatchAnsiTemplate(ansiTemplate1, 0, mergedAnsiTemplate1, 0, SGFDxSecurityLevel.SL_NORMAL, matched);
        debugMessage("MatchAnsiTemplate(0,0) ret:" + error + "\n");
        if (matched[0])
            debugMessage("MATCHED!!\n");
        else
            debugMessage("NOT MATCHED!!\n");

        error = sgFplibSDKTest.MatchAnsiTemplate(ansiTemplate1, 0, mergedAnsiTemplate1, 1, SGFDxSecurityLevel.SL_NORMAL, matched);
        debugMessage("MatchAnsiTemplate(0,1) ret:" + error + "\n");
        if (matched[0])
            debugMessage("MATCHED!!\n");
        else
            debugMessage("NOT MATCHED!!\n");

        error = sgFplibSDKTest.GetAnsiMatchingScore(ansiTemplate1, 0, mergedAnsiTemplate1, 0, score);
        debugMessage("GetAnsiMatchingScore(0,0) ret:" + error + ". Score:" + score[0] + "\n");

        error = sgFplibSDKTest.GetAnsiMatchingScore(ansiTemplate1, 0, mergedAnsiTemplate1, 1, score);
        debugMessage("GetAnsiMatchingScore(0,1) ret:" + error + ". Score:" + score[0] + "\n");

        SGANSITemplateInfo ansiTemplateInfo = new SGANSITemplateInfo();
        error = sgFplibSDKTest.GetAnsiTemplateInfo(ansiTemplate1, ansiTemplateInfo);
        debugMessage("GetAnsiTemplateInfo(ansiTemplate1) ret:" + error + "\n");
        debugMessage("   TotalSamples=" + ansiTemplateInfo.TotalSamples + "\n");
        for (int i = 0; i < ansiTemplateInfo.TotalSamples; ++i) {
            debugMessage("   Sample[" + i + "].FingerNumber=" + ansiTemplateInfo.SampleInfo[i].FingerNumber + "\n");
            debugMessage("   Sample[" + i + "].ImageQuality=" + ansiTemplateInfo.SampleInfo[i].ImageQuality + "\n");
            debugMessage("   Sample[" + i + "].ImpressionType=" + ansiTemplateInfo.SampleInfo[i].ImpressionType + "\n");
            debugMessage("   Sample[" + i + "].ViewNumber=" + ansiTemplateInfo.SampleInfo[i].ViewNumber + "\n");
        }

        error = sgFplibSDKTest.GetAnsiTemplateInfo(mergedAnsiTemplate1, ansiTemplateInfo);
        debugMessage("GetAnsiTemplateInfo(mergedAnsiTemplate1) ret:" + error + "\n");
        debugMessage("   TotalSamples=" + ansiTemplateInfo.TotalSamples + "\n");

        for (int i = 0; i < ansiTemplateInfo.TotalSamples; ++i) {
            debugMessage("   Sample[" + i + "].FingerNumber=" + ansiTemplateInfo.SampleInfo[i].FingerNumber + "\n");
            debugMessage("   Sample[" + i + "].ImageQuality=" + ansiTemplateInfo.SampleInfo[i].ImageQuality + "\n");
            debugMessage("   Sample[" + i + "].ImpressionType=" + ansiTemplateInfo.SampleInfo[i].ImpressionType + "\n");
            debugMessage("   Sample[" + i + "].ViewNumber=" + ansiTemplateInfo.SampleInfo[i].ViewNumber + "\n");
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////        
        //TEST ISO19794-2
        debugMessage("#######################\n");
        debugMessage("TEST ISO19794-2\n");
        debugMessage("###\n###\n");
        error = sgFplibSDKTest.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794);
        debugMessage("SetTemplateFormat(ISO19794) ret:" + error + "\n");
        error = sgFplibSDKTest.GetMaxTemplateSize(size);
        debugMessage("GetMaxTemplateSize() ret:" + error + " ISO19794_MAX_SIZE=" + size[0] + "\n");

        isoTemplate1 = new byte[size[0]];
        isoTemplate2 = new byte[size[0]];

        error = sgFplibSDKTest.CreateTemplate(fpInfo1, finger1, isoTemplate1);
        debugMessage("CreateTemplate(finger1) ret:" + error + "\n");
        error = sgFplibSDKTest.GetTemplateSize(isoTemplate1, size);
        debugMessage("GetTemplateSize(iso) ret:" + error + " size=" + size[0] + "\n");

        error = sgFplibSDKTest.CreateTemplate(fpInfo2, finger2, isoTemplate2);
        debugMessage("CreateTemplate(finger2) ret:" + error + "\n");
        error = sgFplibSDKTest.GetTemplateSize(isoTemplate2, size);
        debugMessage("GetTemplateSize(iso) ret:" + error + " size=" + size[0] + "\n");

        error = sgFplibSDKTest.MatchTemplate(isoTemplate1, isoTemplate2, SGFDxSecurityLevel.SL_NORMAL, matched);
        debugMessage("MatchTemplate(iso) ret:" + error + "\n");
        if (matched[0])
            debugMessage("MATCHED!!\n");
        else
            debugMessage("NOT MATCHED!!\n");

        error = sgFplibSDKTest.GetMatchingScore(isoTemplate1, isoTemplate2, score);
        debugMessage("GetMatchingScore(iso) ret:" + error + ". Score:" + score[0] + "\n");

        error = sgFplibSDKTest.GetIsoTemplateSizeAfterMerge(isoTemplate1, isoTemplate2, size);
        debugMessage("GetIsoTemplateSizeAfterMerge() ret:" + error + ". Size:" + size[0] + "\n");


        byte[] mergedIsoTemplate1 = new byte[size[0]];
        error = sgFplibSDKTest.MergeIsoTemplate(isoTemplate1, isoTemplate2, mergedIsoTemplate1);
        debugMessage("MergeIsoTemplate() ret:" + error + "\n");

        error = sgFplibSDKTest.MatchIsoTemplate(isoTemplate1, 0, mergedIsoTemplate1, 0, SGFDxSecurityLevel.SL_NORMAL, matched);
        debugMessage("MatchIsoTemplate(0,0) ret:" + error + "\n");
        if (matched[0])
            debugMessage("MATCHED!!\n");
        else
            debugMessage("NOT MATCHED!!\n");

        error = sgFplibSDKTest.MatchIsoTemplate(isoTemplate1, 0, mergedIsoTemplate1, 1, SGFDxSecurityLevel.SL_NORMAL, matched);
        debugMessage("MatchIsoTemplate(0,1) ret:" + error + "\n");
        if (matched[0])
            debugMessage("MATCHED!!\n");
        else
            debugMessage("NOT MATCHED!!\n");

        error = sgFplibSDKTest.GetIsoMatchingScore(isoTemplate1, 0, mergedIsoTemplate1, 0, score);
        debugMessage("GetIsoMatchingScore(0,0) ret:" + error + ". Score:" + score[0] + "\n");

        error = sgFplibSDKTest.GetIsoMatchingScore(isoTemplate1, 0, mergedIsoTemplate1, 1, score);
        debugMessage("GetIsoMatchingScore(0,1) ret:" + error + ". Score:" + score[0] + "\n");

        SGISOTemplateInfo isoTemplateInfo = new SGISOTemplateInfo();
        error = sgFplibSDKTest.GetIsoTemplateInfo(isoTemplate1, isoTemplateInfo);
        debugMessage("GetIsoTemplateInfo(isoTemplate1) ret:" + error + "\n");
        debugMessage("   TotalSamples=" + isoTemplateInfo.TotalSamples + "\n");
        for (int i = 0; i < isoTemplateInfo.TotalSamples; ++i) {
            debugMessage("   Sample[" + i + "].FingerNumber=" + isoTemplateInfo.SampleInfo[i].FingerNumber + "\n");
            debugMessage("   Sample[" + i + "].ImageQuality=" + isoTemplateInfo.SampleInfo[i].ImageQuality + "\n");
            debugMessage("   Sample[" + i + "].ImpressionType=" + isoTemplateInfo.SampleInfo[i].ImpressionType + "\n");
            debugMessage("   Sample[" + i + "].ViewNumber=" + isoTemplateInfo.SampleInfo[i].ViewNumber + "\n");
        }

        error = sgFplibSDKTest.GetIsoTemplateInfo(mergedIsoTemplate1, isoTemplateInfo);
        debugMessage("GetIsoTemplateInfo(mergedIsoTemplate1) ret:" + error + "\n");
        debugMessage("   TotalSamples=" + isoTemplateInfo.TotalSamples + "\n");
        for (int i = 0; i < isoTemplateInfo.TotalSamples; ++i) {
            debugMessage("   Sample[" + i + "].FingerNumber=" + isoTemplateInfo.SampleInfo[i].FingerNumber + "\n");
            debugMessage("   Sample[" + i + "].ImageQuality=" + isoTemplateInfo.SampleInfo[i].ImageQuality + "\n");
            debugMessage("   Sample[" + i + "].ImpressionType=" + isoTemplateInfo.SampleInfo[i].ImpressionType + "\n");
            debugMessage("   Sample[" + i + "].ViewNumber=" + isoTemplateInfo.SampleInfo[i].ViewNumber + "\n");
        }

        //Reset extractor/matcher for attached device opened in resume() method
        error = sgFplibSDKTest.InitEx(mImageWidth, mImageHeight, 500);
        debugMessage("InitEx(" + mImageWidth + "," + mImageHeight + ",500) ret:" + error + "\n");

        debugMessage("\n## END SDK TEST ##\n");
    }

    @Override
    public void run() {

        Log.d(TAG, "Enter run()");
        //ByteBuffer buffer = ByteBuffer.allocate(1);
        //UsbRequest request = new UsbRequest();
        //request.initialize(mSGUsbInterface.getConnection(), mEndpointBulk);
        //byte status = -1;
        while (true) {


            // queue a request on the interrupt endpoint
            //request.queue(buffer, 1);
            // send poll status command
            //  sendCommand(COMMAND_STATUS);
            // wait for status event
            /*
            if (mSGUsbInterface.getConnection().requestWait() == request) {
                byte newStatus = buffer.get(0);
                if (newStatus != status) {
                    Log.d(TAG, "got status " + newStatus);
                    status = newStatus;
                    if ((status & COMMAND_FIRE) != 0) {
                        // stop firing
                        sendCommand(COMMAND_STOP);
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            } else {
                Log.e(TAG, "requestWait failed, exiting");
                break;
            }
            */
        }
    }
}