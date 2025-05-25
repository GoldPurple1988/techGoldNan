package com.univhis.dto;

import lombok.Data;

/**
 * 用作数据传输对象，目的是在不同层或组件之间传递文件相关的信息。
 * 它封装了文件的两个基本信息：URL 和名称
 */
@Data
public class FileVO {
    private String url;  // 代表文件的网络地址或路径，可以用来直接访问或下载文件
    private String name; // 文件的名字，可以帮助识别文件的内容或用途
}
