package com.example.baizhi_xu.signin.model;

public class Work {
    private Integer wid;
    private String wname;
    private String wcontent;

    private String principal;
    private String wstate;
    private String remark;
    private String pname;

    public Work(String wname, String wcontent,  String principal,
                String wstate, String remark, String pname) {
        super();
        this.wname = wname;
        this.wcontent = wcontent;

        this.principal = principal;
        this.wstate = wstate;
        this.remark = remark;
        this.pname = pname;
    }

    public Work() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Integer getWid() {
        return wid;
    }

    public void setWid(Integer wid) {
        this.wid = wid;
    }

    public String getWname() {
        return wname;
    }

    public void setWname(String wname) {
        this.wname = wname;
    }

    public String getWcontent() {
        return wcontent;
    }

    public void setWcontent(String wcontent) {
        this.wcontent = wcontent;
    }



    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getWstate() {
        return wstate;
    }

    public void setWstate(String wstate) {
        this.wstate = wstate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }
}
