package org.sacc.SaccHome.mbg.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Page<E> implements Serializable {
    private int currentPage = 0; //当前⻚数

    private long totalPage; //总⻚数

    private long totalNumber; //总记录数

    private List<E> list; //数据集
}