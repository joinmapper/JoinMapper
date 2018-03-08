package com.github.joinmapper.model;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Table(name = "table_three")
public class TableThree {
    @Id
    private String id;
    private String code;
    @Transient
    private List<TableFour> tableFourList;

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

    public List<TableFour> getTableFourList() {
        return tableFourList;
    }

    public void setTableFourList(List<TableFour> tableFourList) {
        this.tableFourList = tableFourList;
    }
}
