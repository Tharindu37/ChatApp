package com.seekerscloud.chat_app.util;

import com.seekerscloud.chat_app.controller.ClientFormController;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import sun.awt.windows.ThemeReader;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;
    private VBox vBox;
    private BufferedImage bufferedImage;
    private Image fxImage;
    private VBox sendersVBox;

    public Client(Socket socket, String userName, VBox vBox){
        this.socket=socket;
        try {
            this.bufferedReader=new BufferedReader(new InputStreamReader(
                    socket.getInputStream()
            ));
            this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream()
            ));
            this.userName=userName;
            this.vBox=vBox;
        } catch (IOException e) {
            //close all
            closeAll(this.socket,this.bufferedReader,this.bufferedWriter);
        }
    }

    public void listenForMessage(VBox vBox, String userName){
        new Thread(()->{
            String msgFromChat=null;
            String imgFromChat=null;
            while (socket.isConnected() && !userName.equals("SERVER")){
                try{
                    msgFromChat=bufferedReader.readLine();
                    if (msgFromChat.contains(".jpg") || msgFromChat.contains(".png")){
                        imgFromChat=msgFromChat;
                        //===>
                    }else {
                        String[] strings=msgFromChat.split(":");
                        String sendersName=strings[0].trim();
                        if (strings.length==2 || msgFromChat.contains(" has joined") || msgFromChat.contains("left")){
                            if (sendersName.equals("sender")){
                                //client form controller => display => static method // right side
                                ClientFormController.displayMessageOnRight(msgFromChat.split(":")[1], vBox);
                            }else {
                                //client from controller => display left side
                                ClientFormController.displayMessageOnLeft(msgFromChat, vBox);
                            }
                        }
                    }
                }catch (Exception e){

                }
            }
        }).start();
    }

    public void sendMessage(String msgToSend, VBox vBox, String sender){
        this.sendersVBox=vBox;
        new Thread(()->{
            try {
                this.bufferedWriter.write(userName);
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();
                if (msgToSend.contains("left")){
                    this.bufferedWriter.write(msgToSend);
                }else if (msgToSend.contains("has joined")){
                    this.bufferedWriter.write(msgToSend);
                }else {
                    this.bufferedWriter.flush();
                    this.bufferedWriter.write(userName+" : "+msgToSend);
                }
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try {
            if (bufferedReader!=null) bufferedReader.close();
            if (bufferedWriter!=null) bufferedWriter.close();
            if (socket!=null) socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
