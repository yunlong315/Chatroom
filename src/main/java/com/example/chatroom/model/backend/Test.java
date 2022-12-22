package com.example.chatroom.model.backend;

import javafx.scene.image.Image;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Test {
    public static void main(String[] args) throws IOException {
        //把imagePath中的图片移到path，尝试能否显示paht中的图片
        String imagePath = "C:\\Users\\86155\\Desktop\\mkm.jpg";//此处替换路径
        byte[] image = Files.readAllBytes(Path.of(imagePath));
        String path = "testImage";
        if(image!=null)
        {
            //更新头像
            File trg = new File(path);
            if(!trg.exists())
            {
                trg.mkdirs();
            }
            try {
                OutputStream os= new FileOutputStream(trg);
                os.write(image,0, image.length);
                os.flush();
                os.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
//        Image image = new Image(path);
//        headImage.setImage(image);
    }
    }
