package com.civilease.model;

public enum Category {
    SCHOLARSHIP("장학"), 
    FACILITY("시설물"), 
    GRADUATE("대학원"), 
    OTHERS("기타");
	/***
	 * 카테고리 추가시 예시
	 * SCHOLARSHIP("장학"), 
     * FACILITY("시설물"), 
     * GRADUATE("대학원"), 
     * OTHERS("기타"), <  콤마주의
	 * CLASS("수업");
	 * 
	 * 이후 DB에
	 * ALTER TABLE complaints
	 * MODIFY COLUMN category ENUM('SCHOLARSHIP', 'FACILITY', 'GRADUATE', 'OTHER', 'CLASS') NOT NULL;
	 * 2개 줄 기입하기
	 */

    private final String name;
    Category(String name) { this.name = name; }
    public String getName() { return name; }

    @Override
    public String toString() { return name; }
}
