package com.esdemo.frame.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum  TaskRecordStatusEnum {

    INIT("init", "初始化"),
    RUNNING("running", "运行中"),
    COMPLETE("complete", "完成");

    private String code;
    private String name;

    public static TaskRecordStatusEnum getByCode(String code) {
        for (TaskRecordStatusEnum taskRecordStatusEnum : values()) {
            if (taskRecordStatusEnum.getCode().equalsIgnoreCase(code)) {
                return taskRecordStatusEnum;
            }
        }
        return null;
    }
}
