package com.univhis.exception;

// 定义一个公共类 CustomException，它继承自 RuntimeException，表示这是一个运行时异常
public class CustomException extends RuntimeException {

    private String code; // 声明一个私有的 String 类型变量 code，用于存储自定义的错误代码
    private String msg; // 声明一个私有的 String 类型变量 msg，用于存储自定义的错误信息

    public CustomException(String code, String msg) { // 定义 CustomException 类的构造方法，接收两个参数：错误代码 (code) 和错误信息 (msg)
        this.code = code; // 将传入的 code 参数的值赋给当前对象的 code 成员变量
        this.msg = msg; // 将传入的 msg 参数的值赋给当前对象的 msg 成员变量
    }

    public String getCode() { // 定义一个公共方法 getCode，用于获取当前对象的 code 成员变量的值
        return code; // 返回当前对象的 code 成员变量的值
    }

    public void setCode(String code) { // 定义一个公共方法 setCode，用于设置当前对象的 code 成员变量的值
        this.code = code; // 将传入的 code 参数的值赋给当前对象的 code 成员变量
    }

    public String getMsg() { // 定义一个公共方法 getMsg，用于获取当前对象的 msg 成员变量的值
        return msg; // 返回当前对象的 msg 成员变量的值
    }

    public void setMsg(String msg) { // 定义一个公共方法 setMsg，用于设置当前对象的 msg 成员变量的值
        this.msg = msg; // 将传入的 msg 参数的值赋给当前对象的 msg 成员变量
    }
}