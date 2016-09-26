package com.luluteam.yjy.manager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by Guan on 2016/7/28.
 */
public class OkHttpManager {

    private static OkHttpManager mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private Gson mGson;



    //int id=0;

    private static String TAG="OkHttpManager";

    private OkHttpManager()
    {
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.newBuilder().connectTimeout(30, TimeUnit.SECONDS);
        mOkHttpClient.newBuilder().readTimeout(30, TimeUnit.SECONDS);
        mOkHttpClient.newBuilder().writeTimeout(30, TimeUnit.SECONDS);
        //okhttp3.0之后有了变化。这个方法以后再改。
        //mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mDelivery = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }


    public static OkHttpManager getInstance()
    {
        if (mInstance == null)
        {
            synchronized (OkHttpManager.class)
            {
                if (mInstance == null)
                {
                    mInstance = new OkHttpManager();
                }
            }
        }
        return mInstance;
    }


    public static void Upload(String url,  File[] files, String[] fileKeys, HashMap<String,String> params,myProgressListener myprogresslistener)
    {
        getInstance().uploadpostAsyn(url, files, fileKeys, params,myprogresslistener);
    }

    public static void Download(String url,String destDir,final String filename,myProgressListener myprogresslistener)
    {
        getInstance().downloadAsyn(url, destDir, filename,myprogresslistener);
    }

    public static void CommonPostAsyn(String url,HashMap<String,String>param, myCallback callback)
    {
        getInstance().postAsyn(url,param,callback);
    }



    private void postAsyn(String url, HashMap<String,String>param, final myCallback callback)
    {
        //OkHttpClient client = new OkHttpClient();

        FormBody.Builder builder=new FormBody.Builder();

        for(String key:param.keySet())
        {
            builder.add(key,param.get(key));

        }

        RequestBody body = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,"onFailure");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                if (response.isSuccessful())
                {
                    callback.onCallBack(response.body().string());
                }else
                {
                    Log.e(TAG,"服务器错误。response is failed.仅为测试");
                    //下面这行仅供测试，以后要删
                    //callback.onCallBack(response.body().string());
                }

            }
        });

    }


    /**
     * 异步基于post的文件上传
     *
     * @param url
     * @param files
     * @param fileKeys
     * @throws IOException
     */
    private void uploadpostAsyn(String url, File[] files, String[] fileKeys, HashMap<String,String> params, myProgressListener myprogresslistener)
    {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params,myprogresslistener);
        //deliveryResult(callback, request);


        //使用execute同步调用，使用enqueue异步调用
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,"onFailure");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //非UI进程
                if(response.isSuccessful())
                {
                    //System.out.println("response.body().string() = " + response.body().string());
                    Log.v(TAG,"response.body().string() = " + response.body().string());
                }else
                {
                    Log.e(TAG,"response is failed");
                }

            }
        });


    }

    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     * @param listener
     */
    private void downloadAsyn(final String url, final String destFileDir, final String filename,final myProgressListener listener)
    {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e) {
                //sendFailedStringCallback(request, e, callback);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                int count=0;
                FileOutputStream fos = null;
                try
                {
                    is = response.body().byteStream();
                    File file = new File(destFileDir, filename);
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1)
                    {
                        fos.write(buf, 0, len);
                        count+=len;
                        //文件总长度从外部获得；
                        //因为是单文件下载，所以id为-1，表示不起作用
                        //false写死，在这里并不计算是否已经传输完毕。交给TransFileAdapter来计算
                        //所以这个进度监听器在这里只有count是有意义的。
                        listener.onProgress(-1,count,false,-1);
                    }
                    fos.flush();
                    Log.e(TAG,"file read or write Done");
                } catch (IOException e)
                {
                    e.printStackTrace();
                } finally
                {
                    try
                    {
                        if (is != null) is.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

            }




        });
    }

    private String getFileName(String path)
    {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }




    //方法中通过定制的RequestBody，获取Request
    private Request buildMultipartFormRequest(String url, File[] files,
                                              String[] fileKeys, HashMap<String,String> params,myProgressListener myprogresslistener)
    {
        //params = validateParam(params);

        if(null==params)
        {
            params=new HashMap<String,String>();
        }


        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        //加参数
        for(String key: params.keySet())
        {
            builder.addFormDataPart(key,params.get(key));
        }

        //按顺序加文件
        if (files != null)
        {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++)
            {
                File file = files[i];
                String fileName = file.getName();
//                fileBody=myRequestBody(MediaType.parse("application/octet-stream"), file,
//                        myprogresslistener);
                fileBody=new GuanRequestBody(MediaType.parse("application/octet-stream"), file,
                        myprogresslistener,i);
                builder.addFormDataPart(fileKeys[i],fileName,fileBody);
            }
        }

        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }


//    private RequestBody myRequestBody(final MediaType contentType, final File file,
//                                            final myProgressListener listener)
//    {
//
//        //重写RequestBody，实现进度监听
//        return new RequestBody() {
//
//            @Override
//            public MediaType contentType() {
//                return contentType;
//            }
//
//            @Override
//            public long contentLength() throws IOException {
//                return file.length();
//            }
//
//            @Override
//            public void writeTo(BufferedSink sink) throws IOException {
//
//                Source source;
//
//                long len;
//                long count=0;
//                try {
//                    source = Okio.source(file);
//                    Buffer buf = new Buffer();
//                    Long total = contentLength();
//
//                    while ((len = source.read(buf, 2048)) != -1)
//                    {
//                        sink.write(buf, len);
//                        sink.flush();
//                        count+=len;
//                        listener.onProgress(total, count, total == count,id);
//                    }
//                    id++;
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//
//
//        };
//    }
//

    //重写RequestBody，实现进度监听
    private class GuanRequestBody extends RequestBody
    {
        MediaType contentType;
        File file;
        myProgressListener listener;
        int id;

        public GuanRequestBody( MediaType contentType,  File file,
                                myProgressListener listener,int id)
        {
            this.contentType=contentType;
            this.file=file;
            this.listener=listener;
            this.id =id;
        }

        @Override
        public MediaType contentType() {
            return contentType;
        }

        @Override
        public long contentLength() throws IOException {
            return file.length();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {

            Source source;

            long len;//文件总长度
            long count=0;//已传输的长度
            try {
                source = Okio.source(file);
                Buffer buf = new Buffer();
                Long total = contentLength();

                while ((len = source.read(buf, 2048)) != -1)
                {
                    sink.write(buf, len);
                    sink.flush();
                    count+=len;
                    //进度监听器的调用
                    listener.onProgress(total, count, total == count, id);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public interface myProgressListener {
        /**
         *
         * @param totalBytes 文件总长度
         * @param count 已传输的长度
         * @param done 是否传输完毕
         * @param id 此次传输过程中的第几个文件
         */
        void onProgress(long totalBytes, long count, boolean done,int id);
    }

    public interface myCallback
    {
        void onCallBack(String result);
    }




}
