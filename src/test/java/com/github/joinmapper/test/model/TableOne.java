package com.github.joinmapper.test.model;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Table(name = "table_one")
public class TableOne {
    @Id
    private String id;
    private String code;
    @Transient
    private TableTwo tableTwo;
    @Transient
    private List<TableTwo> tableTwoList;
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

    public TableTwo getTableTwo() {
        return tableTwo;
    }

    public void setTableTwo(TableTwo tableTwo) {
        this.tableTwo = tableTwo;
    }

    public TableThree getTableThree() {
        return tableThree;
    }

    public void setTableThree(TableThree tableThree) {
        this.tableThree = tableThree;
    }

    public List<TableTwo> getTableTwoList() {
        return tableTwoList;
    }

    public void setTableTwoList(List<TableTwo> tableTwoList) {
        this.tableTwoList = tableTwoList;
    }
}
