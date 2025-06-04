package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bId;

    private String bRequireNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uId", nullable = false)
    private UserBy userBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mCode", nullable = false)
    private Material material; // 자재 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pCode", nullable = false)
    private Product product; // 제품 외래키

    public void change(String bRequireNum, Material material){
        this.bRequireNum = bRequireNum;
        this.material = material;
    }
}
