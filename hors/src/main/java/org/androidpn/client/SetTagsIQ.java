package org.androidpn.client;

import java.util.List;

import org.jivesoftware.smack.packet.IQ;

import android.util.Log;

/*
 * 自定义回复IQ
 */

public class SetTagsIQ extends IQ {
	private String username;
	private List<String> tagList;



	@Override
	public String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<").append("settags").append(" xmlns=\"").append(
                "androidpn:iq:settags").append("\">");
       
        if(username != null){
        	 buf.append("<username>").append(username).append("</username>");
        }
        Log.d("TAG", "tags prepare add");
        
        if(tagList != null && !tagList.isEmpty()){
            Log.d("TAG", "tags add now "+ tagList.get(0));
        	boolean needSeperate = false;
        	buf.append("<tags>");
        	
        	for(String tag: tagList){
        		
        		if(needSeperate){
        			buf.append(",");
        		}
        		buf.append(tag);
        		needSeperate = true;
        	}
        	buf.append("</tags>");
        	
        }
        
        buf.append("</").append("settags").append("> ");
        return buf.toString();
	}
	
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getTagList() {
		return tagList;
	}


	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}


}
