package com.yimao.cloud.water.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * 描述：TDS
 *
 * @Author Zhang Bo
 * @Date 2019/5/8
 */
@Table(name = "tds")
@Getter
@Setter
public class Tds {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double k;
    private Double t;

    public Tds() {
    }

    public Tds(Double k, Double t) {
        this.k = k;
        this.t = t;
    }

    public boolean isSame(Tds o) {
        if (this == o) {
            return true;
        } else if (o == null) {
            return false;
        } else {
            return Objects.equals(this.k, o.k) && Objects.equals(this.t, o.t);
        }
    }
}
