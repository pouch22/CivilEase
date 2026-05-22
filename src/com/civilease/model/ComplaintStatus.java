package com.civilease.model;

public enum ComplaintStatus {
    PENDING("접수대기"),
    RECEIVED("접수"),
    PROCESSING("처리 중"),
    SUPPLEMENT("보완필요"),
    COMPLETED("완료"),
    REJECTED("반려");

    private final String name;
    ComplaintStatus(String name) { this.name = name; }
    public String getName() { return name; }

    @Override
    public String toString() { return name; }
}
