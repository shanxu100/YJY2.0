package com.luluteam.yjy.base;

import android.os.Environment;

/**
 * Created by Guan on 2016/7/29.
 * 定义一些常用的字符串常量
 */
public class Constant {
    public static final String APKNAME = "YJY";
    public static String APKDIR = Environment.getExternalStorageDirectory().getPath() + "/" + Constant.APKNAME;
    public static String DOWNLOADDIR = Environment.getExternalStorageDirectory().getPath() + "/Download";


    private static String HOAST = "http://125.216.242.143:8080";

    public static String OWNERID = "02";//仅供测试，ownerid，所有者的ID

    public static String URL_UPLOAD = HOAST + "/YJYS/file/upload";
    public static String URL_COURSEWARELIST = HOAST + "/YJYS/file/list";
    public static String URL_UPDATEFILE = HOAST + "/YJYS/file/modify";
    public static String URL_DELETEFILE = HOAST + "/YJYS/file/delete";

    public static String URL_NEWFOLDER = HOAST + "/yjy/file/newfolder";//无效
    public static String URL_MOVE = HOAST + "/yjy/file/move";//无效


    //这的用于startActivityForResult——onResult系列方法中的RequestCode
    public static int CODE_DODETAIL = 101;
    public static int CODE_DOMOVE = 102;


    //静态字符串：用于测试“获取文件列表”的功能中包含“文件夹”、“文件”两类的功能
    //========以下内容，接口写好后要删除
    public static String staticJSON = "{\"result\": true,\"size\": 2,\"files\": [{\"fileId\": \"file36bf52f5-5136-4f0b-8937-bb6836197783\",\"fileName\": \"file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileOwner\": \"02\",\"fileOriginalName\": \"folder1\",\"fileType\": \"folder\",\"fileSize\": 780831,\"filePath\": \"D:/fs/02/file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileUrl\": \"http://125.216.242.143:8080/file/download?fileId=file36bf52f5-5136-4f0b-8937-bb6836197783\"},{\"fileId\": \"file36bf52f5-5136-4f0b-8937-bb6836197783\",\"fileName\": \"file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileOwner\": \"02\",\"fileOriginalName\": \"Koala.jpg\",\"fileType\": \"jpg\",\"fileSize\": 780831,\"filePath\": \"D:/fs/02/file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileUrl\": \"http://125.216.242.143:8080/file/download?fileId=file36bf52f5-5136-4f0b-8937-bb6836197783\"}]}";
    private static String staticJSON2 = "{\"result\": true,\"size\": 4,\"files\": [{\"fileId\": \"file36bf52f5-5136-4f0b-8937-bb6836197783\",\"fileName\": \"file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileOwner\": \"02\",\"fileOriginalName\": \"folder2\",\"fileType\": \"folder\",\"fileSize\": 780831,\"filePath\": \"D:/fs/02/file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileUrl\": \"http://125.216.242.143:8080/file/download?fileId=file36bf52f5-5136-4f0b-8937-bb6836197783\"},\n" +
            "{\"fileId\": \"file36bf52f5-5136-4f0b-8937-bb6836197783\", \"fileName\": \"file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileOwner\": \"02\",\"fileOriginalName\": \"folder3\",\"fileType\": \"folder\",\"fileSize\": 780831,\"filePath\": \"D:/fs/02/file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileUrl\": \"http://125.216.242.143:8080/file/download?fileId=file36bf52f5-5136-4f0b-8937-bb6836197783\"},{\"fileId\": \"file36bf52f5-5136-4f0b-8937-bb6836197783\",\"fileName\": \"file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileOwner\": \"02\",\"fileOriginalName\": \"baball\",\"fileType\": \"folder\",\"fileSize\": 780831,\"filePath\": \"D:/fs/02/file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileUrl\": \"http://125.216.242.143:8080/file/download?fileId=file36bf52f5-5136-4f0b-8937-bb6836197783\"},{\"fileId\": \"file36bf52f5-5136-4f0b-8937-bb6836197783\",\"fileName\": \"file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileOwner\": \"02\",\"fileOriginalName\": \"Koala.txt\",\"fileType\": \"txt\",\"fileSize\": 780831,\"filePath\": \"D:/fs/02/file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileUrl\": \"http://125.216.242.143:8080/file/download?fileId=file36bf52f5-5136-4f0b-8937-bb6836197783\"}]}";
    private static String staticJSON3 = "{\"result\": true,\"size\": 3,\"files\": [{\"fileId\": \"file36bf52f5-5136-4f0b-8937-bb6836197783\",\"fileName\": \"file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileOwner\": \"02\",\"fileOriginalName\": \"folder4\",\"fileType\": \"folder\",\"fileSize\": 780831,\"filePath\": \"D:/fs/02/file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileUrl\": \"http://125.216.242.143:8080/file/download?fileId=file36bf52f5-5136-4f0b-8937-bb6836197783\"},\n" +
            "{\"fileId\": \"file36bf52f5-5136-4f0b-8937-bb6836197783\", \"fileName\": \"file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileOwner\": \"02\",\"fileOriginalName\": \"folder5\",\"fileType\": \"folder\",\"fileSize\": 780831,\"filePath\": \"D:/fs/02/file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileUrl\": \"http://125.216.242.143:8080/file/download?fileId=file36bf52f5-5136-4f0b-8937-bb6836197783\"},{\"fileId\": \"file36bf52f5-5136-4f0b-8937-bb6836197783\",\"fileName\": \"file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileOwner\": \"02\",\"fileOriginalName\": \"baball.ppt\",\"fileType\": \"ppt\",\"fileSize\": 780831,\"filePath\": \"D:/fs/02/file36bf52f5-5136-4f0b-8937-bb6836197783.jpg\",\"fileUrl\": \"http://125.216.242.143:8080/file/download?fileId=file36bf52f5-5136-4f0b-8937-bb6836197783\"}]}";
    //private static String staticJSON4 = "{\"total\": 3,\"result\": true,\"documents\": [{\"docId\": \"8a04907055543bed015571ded3d90003\",\"originalName\": \"folder3_3\",\"saveName\": \"bababa\",\"savePath\": \"babab\",\"downPath\": \"http://i-test.com.cn/attachment/201606211531568734.prop\",\"type\": \"folder\",\"docSize\": 3937183,\"uploadTime\": \"2016-06-21\"},{\"docId\": \"8a04907055543bed015571ded3d90002\",\"originalName\": \"folder3_4\",\"saveName\": \"201606211531568734\",\"savePath\": \"/alidata/yjy/201606211531568734.mp3\",\"downPath\": \"http://i-test.com.cn/attachment/201606211531568734.mp3\",\"type\": \"folder\",\"docSize\": 3937183,\"uploadTime\": \"2016-06-21\"},{\"docId\": \"8a04907055543bed015571ded3d90002\",\"originalName\": \"123.ppt\",\"saveName\": \"201606211531568734.ppt\",\"savePath\": \"/alidata/yjy/201606211531568734.ppt\",\"downPath\": \"http://i-test.com.cn/attachment/201606211531568734.ppt\",\"type\": \"ppt\",\"docSize\": 3937183,\"uploadTime\": \"2016-06-21\"}]}";
    private static String[] staticJSONs = {staticJSON, staticJSON2, staticJSON3};
    private static int i = -1;

    public static String getStaticJSON() {

        if (i < staticJSONs.length - 1) {
            i++;
            return staticJSONs[i];
        } else {
            i = 0;
            return staticJSONs[i];
        }
    }
    //=========================

    //定义一些参数传递时使用的String串
    public static String STRING_WHAT = "what";
    public static String STRING_DOWNLOAD = "DOWNLOAD";
    public static String STRING_UPLOAD = "UPLOAD";
    public static String STRING_PATHS = "paths";
    public static String STRING_ID = "id";
    public static String STRING_CURSIZE = "curSize";

    public static String STRING_OWNERID = "ownerId";
    public static String STRING_RESPONSE = "response";
    public static String STRING_FOLDER = "folder";
    public static String STRING_POSITION = "position";
    public static String STRING_JSON = "json";
    public static String STRING_COURSEWARE = "courseware";

    public static String STRING_ROOTJSON = "rootJSON";
    public static String STRING_PARENTFOLDER = "parentfolder";

    public static String STRING_FILEID = "fileId";
    public static String STRING_FILEOWNER = "fileOwner";
    public static String STRING_FILEORIGINALNAME = "fileOriginalName";
    public static String STRING_FILETYPE = "fileType";
    public static String STRING_FILESIZE = "fileSize";
    public static String STRING_FILEUrl = "fileUrl";

    public static String STRING_CLICKIN="clickIn";


}
