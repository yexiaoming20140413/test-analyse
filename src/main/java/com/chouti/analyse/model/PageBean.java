package com.chouti.analyse.model;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-9.
 *******************************************************************************/
public class PageBean {

    private long start;

    private int pageSize = 20;

    private long totalPage;

    private long page;

    private long totalSize;

    public PageBean(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
        this.start = (page - 1) * pageSize;
    }

    public PageBean(Long count) {
        this.totalSize = count;
        if (count % this.pageSize == 0) {
            this.totalPage = count / this.pageSize;
        } else {
            this.totalPage = count / this.pageSize + 1;
        }

    }


    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }


    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        if (this.totalSize % this.pageSize == 0) {
            this.totalPage = this.totalSize / this.pageSize;
        } else {
            this.totalPage = this.totalSize / this.pageSize + 1;
        }
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
        this.start = (page - 1) * this.pageSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
}
