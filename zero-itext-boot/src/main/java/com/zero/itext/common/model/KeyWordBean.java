package com.zero.itext.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyWordBean implements Comparable<KeyWordBean>{

    private float 	x;
    private float 	y;
    private int    	page;
    private String 	text;

    @Override
    public int compareTo(KeyWordBean o) {
        //先按照Y轴排序
        int i = (int) (o.getY() - this.getY());
        if(i == 0){
            //如果Y轴相等了再按X轴进行排序
            return (int) (this.x - o.getX());
        }
        return i;
    }

}
