/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.component.District
 *  com.etas.vaas.backend.dto.Centroid
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.backend.component;

import com.etas.vaas.backend.dto.Centroid;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="district")
public class District {
    private List<Centroid> centroids;

    public List<Centroid> getCentroids() {
        return this.centroids;
    }

    public void setCentroids(List<Centroid> centroids) {
        this.centroids = centroids;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof District)) {
            return false;
        }
        District other = (District)o;
        if (!other.canEqual(this)) {
            return false;
        }
        List this$centroids = this.getCentroids();
        List other$centroids = other.getCentroids();
        return !(this$centroids == null ? other$centroids != null : !((Object)this$centroids).equals(other$centroids));
    }

    protected boolean canEqual(Object other) {
        return other instanceof District;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List $centroids = this.getCentroids();
        result = result * 59 + ($centroids == null ? 43 : ((Object)$centroids).hashCode());
        return result;
    }

    public String toString() {
        return "District(centroids=" + this.getCentroids() + ")";
    }

    public District(List<Centroid> centroids) {
        this.centroids = centroids;
    }

    public District() {
    }
}

