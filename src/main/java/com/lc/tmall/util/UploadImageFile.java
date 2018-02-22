package com.lc.tmall.util;

import org.springframework.web.multipart.MultipartFile;

public class UploadImageFile { //接受上传文件的注入
    MultipartFile image; //属性名称image须和页面中的增加分类部分(listCategory.jsp、form)中的type="file"的name值保持一致

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
