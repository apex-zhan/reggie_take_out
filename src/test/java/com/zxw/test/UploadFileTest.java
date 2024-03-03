package com.zxw.test;

import org.junit.jupiter.api.Test;

public class UploadFileTest {
    @Test
    public void testUploadFile() {
        String fileName ="eeeeeeeeee.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);

    }
}