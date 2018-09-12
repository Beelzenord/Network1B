/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skeletonbroadcast;

import java.io.Serializable;

/**
 *
 * @author fno
 */
public class UserInfo implements Serializable{
    private int id;
    private String nickName;

    public UserInfo(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getFromWhoCommand(){
        if(nickName!=null){
            return nickName;
        }
        else{
            return ""+id;
        }
    }
    
}
