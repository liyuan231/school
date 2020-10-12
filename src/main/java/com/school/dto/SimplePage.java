package com.school.dto;



public class SimplePage<T> {
    public SimplePage(Integer size, T data) {
        this.size = size;
        this.data = data;
    }

    private Integer size;
    private T data;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
