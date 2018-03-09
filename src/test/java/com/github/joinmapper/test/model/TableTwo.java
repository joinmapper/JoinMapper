package com.github.joinmapper.test.model;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "table_two")
public class TableTwo {
    @Id
    private String id;
    private String code;
    @Transient
    private TableThree tableThree;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TableThree getTableThree() {
        return tableThree;
    }

    public void setTableThree(TableThree tableThree) {
        this.tableThree = tableThree;
    }
}
