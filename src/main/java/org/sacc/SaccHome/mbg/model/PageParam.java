package org.sacc.SaccHome.mbg.model;

import lombok.Data;

@Data
public class PageParam {
    private int beginLine; //起始⾏

    private int pageSize =8; //每页条数，默认为8

    private int currentPage=0; // 当前⻚

    public int getBeginLine() {
        return pageSize*currentPage;//⾃动计算起始⾏
    }
}
