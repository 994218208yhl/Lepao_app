package com.liuzozo.stepdemo;

/**
 * <pre>
 *     author : lisheny
 *     e-mail : 1020044519@qq.com
 *     time   : 2017/08/22
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class HomeListBeen {
    //讲话人
    private int teller = Constant.APP;
    //内容
    private String content;
    //日期
    private String data;

    public int getTeller() {
        return teller;
    }

    public void setTeller(int teller) {
        this.teller = teller;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
