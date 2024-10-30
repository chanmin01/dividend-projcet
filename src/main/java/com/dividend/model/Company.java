package com.dividend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Entity는 데이터베이스 테이블과 직접적으로 연관된 클래스이기 때문에
 * 로직이 들어가게 되면 원래 역할 범위를 넘어선다. 따라서 model을 따로 정의한다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    private String ticker;
    private String name;
}
