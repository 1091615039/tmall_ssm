package com.lc.tmall.util;

public class Page {
    private int start; //开始页数
    private int count; //每页显示个数
    private int total; //总个数
    private String param; //参数（属性列表分页用到）

    private static final int defaultCount = 5;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Page(){
        count = defaultCount;
    }

    public Page(int start, int count){
        this();
        this.start = start;
        this.count = count;
    }

    public boolean isHasPrevious(){
        if (start == 0)
            return false;
        return true;
    }

    public boolean isHasNext() {
        if (start == getLast())
            return false;
        return true;
    }

    public int getLast() {
        int last; //最后一页的第一条数据
        if (0 == total % count)
            last = total - count;
        else last = total - total % count;
        last = last < 0 ? 0:last;
        return last;
    }

    public int getTotalPage(){
        int totalPage;
        if (0 == total % count)
            totalPage = total/count;
        else
            totalPage = total/count + 1;
        if (0 == totalPage)
            totalPage = 1;
        return totalPage;
    }

    @Override
    public String toString() {
        return "Page [start=" + start + ", count=" + count + ", total=" + total + ", getStart()=" + getStart()
                + ", getCount()=" + getCount() + ", isHasPreviouse()=" + isHasPrevious() + ", isHasNext()="
                + isHasNext() + ", getTotalPage()=" + getTotalPage() + ", getLast()=" + getLast() + "]";
    }
}
