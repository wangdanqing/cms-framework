package net.pusuo.cms.client.util;

import java.util.*;

public class Pageable {

    private int totalNum = 0;
    private int numPerPage = 0;
    private int page = 0;
    private int showPageNum = 0;


    public Pageable(int totalNum, int numPerPage, int page, int showPageNum) {
        this.totalNum = totalNum;
        this.numPerPage = numPerPage;
        this.page = page;
        this.showPageNum = showPageNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public void setNumPerPage(int numPerPage) {
        this.numPerPage = numPerPage;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setShowPageNum(int showPageNum) {
        this.showPageNum = showPageNum;
    }

    public int getTotalNum() {
        return this.totalNum;
    }

    public int getNumPerPage() {
        return this.numPerPage;
    }

    public int getPage() {
        return this.page;
    }
    public int getShowPageNum() {
        return this.showPageNum;
    }

    /**
     * ȡ����ҳ��
     */
    public int getTotalPage() {
        int totalPage = 0;
        if (numPerPage == 0) {
            return 0;
        }
        totalPage = (totalNum % numPerPage) == 0 ?
                    totalNum / numPerPage :
                    totalNum / numPerPage + 1;
        return totalPage;
    }

    /**
     * ȡ�ÿ�ʼ��¼��
     */
    public int getStartNumber() {

        return (page - 1) * numPerPage + 1;
    }

    /**
     * ȡ�ý����¼��
     */
    public int getEndNumber() {
        return (page * numPerPage) > totalNum ? totalNum : page * numPerPage;
    }

    /**
     * ȡ����һҳҳ��
     */
    public int getPrevPage() {
        return (page - 1) <= 0 ? 0 : page - 1;
    }

    /**
     * ȡ����һҳҳ��
     */
    public int getNextPage() {
        return (page + 1) > getTotalPage() ? 0 : page + 1;
    }

    /**
     * ȡ��ǰpageNum boat ��ҳ��
     */
    public int getPrevPageNum() {
        if (page <= showPageNum) {
            return 0;
        }

        int tmp = (page % showPageNum) == 0 ?
                    page / showPageNum - 1 : page /showPageNum;

        return tmp * showPageNum;
    }

    /**
     * ȡ�ú�pageNum boat ��ҳ��
     */
    public int getNextPageNum() {
        int tmp = getPrevPageNum() + showPageNum + 1;

        return tmp > getTotalPage() ? 0 : tmp;
    }

    /**
     * ȡ�õ�ǰpage boat List
     */
    public Collection getCurrentPageList() {
        Collection c = new ArrayList();

        int forStart = getPrevPageNum() + 1;
        int forEnd = (getNextPageNum() == 0) ?
                        getTotalPage() + 1 : getNextPageNum();

        for (int i = forStart; i < forEnd; i++) {
            c.add(Integer.toString(i));
        }
        return c;
    }

}
