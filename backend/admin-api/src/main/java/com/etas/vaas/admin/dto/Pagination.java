/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.dto.Pagination
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  lombok.Generated
 */
package com.etas.vaas.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pagination {
    @JsonProperty(value="total")
    private Long total;
    @JsonProperty(value="page_size", defaultValue="10")
    private Long pageSize = 10L;
    @JsonProperty(value="current_page", defaultValue="10")
    private Long currentPage = 1L;

    public Pagination(Long pageSize, Long currentPage) {
        this.pageSize = pageSize;
        this.currentPage = currentPage;
    }

    public Long getTotal() {
        return this.total;
    }

    public Long getPageSize() {
        return this.pageSize;
    }

    public Long getCurrentPage() {
        return this.currentPage;
    }

    @JsonProperty(value="total")
    public void setTotal(Long total) {
        this.total = total;
    }

    @JsonProperty(value="page_size", defaultValue="10")
    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    @JsonProperty(value="current_page", defaultValue="10")
    public void setCurrentPage(Long currentPage) {
        this.currentPage = currentPage;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Pagination)) {
            return false;
        }
        Pagination other = (Pagination)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$total = this.getTotal();
        Long other$total = other.getTotal();
        if (this$total == null ? other$total != null : !((Object)this$total).equals(other$total)) {
            return false;
        }
        Long this$pageSize = this.getPageSize();
        Long other$pageSize = other.getPageSize();
        if (this$pageSize == null ? other$pageSize != null : !((Object)this$pageSize).equals(other$pageSize)) {
            return false;
        }
        Long this$currentPage = this.getCurrentPage();
        Long other$currentPage = other.getCurrentPage();
        return !(this$currentPage == null ? other$currentPage != null : !((Object)this$currentPage).equals(other$currentPage));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Pagination;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $total = this.getTotal();
        result = result * 59 + ($total == null ? 43 : ((Object)$total).hashCode());
        Long $pageSize = this.getPageSize();
        result = result * 59 + ($pageSize == null ? 43 : ((Object)$pageSize).hashCode());
        Long $currentPage = this.getCurrentPage();
        result = result * 59 + ($currentPage == null ? 43 : ((Object)$currentPage).hashCode());
        return result;
    }

    public String toString() {
        return "Pagination(total=" + this.getTotal() + ", pageSize=" + this.getPageSize() + ", currentPage=" + this.getCurrentPage() + ")";
    }

    public Pagination(Long total, Long pageSize, Long currentPage) {
        this.total = total;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
    }

    public Pagination() {
    }
}

