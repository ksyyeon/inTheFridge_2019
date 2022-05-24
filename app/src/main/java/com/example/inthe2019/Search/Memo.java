package com.example.inthe2019.Search;

public class Memo { //데이터를 메모 단위로 왔닥가다
    private long id;
    private String memo;

    public Memo(String memo) {
        this.memo = memo;
    }

    public Memo(Long id, String memo) {
        this.id = id;
        this.memo = memo;
    }

    public Long getId() {
        return id;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return memo;
    }
}