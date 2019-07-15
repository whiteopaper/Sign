package com.example.baizhi_xu.signin.unit;

public class Constant {
    public static final String urlBase = "http://134.175.28.101/ActiveServer/";
    public static final String urlLogin = urlBase + "LoginServlet";
    public static final String urlRefresh = urlBase + "RefreshServlet";
    public static final String urlCheckin = urlBase + "CheckinServlet";
    public static final String urlTeacherSearch = urlBase + "TeacherSearch";
    public static final String urlTeacherInput = urlBase + "TeacherInput";
    public static final String urlDeleteUuid = urlBase + "DeleteServlet";
    public static final String urlGetRecord = urlRefresh + "?action=getRecord";
    public static final String urlGetSign = urlRefresh + "?action=getSign";
}
