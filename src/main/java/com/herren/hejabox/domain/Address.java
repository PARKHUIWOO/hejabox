package com.herren.hejabox.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address { // jpa 기본 스펙이 생성할 때 리플렉션 / 프록시 기술 그게 안됨 (기본생성자가 있어야함)
    // 값 타입은 불가능하게, 엔티티나 임베디드 타입은 기본 생성자를 public이나 protected로 설정해야함.
    private String city;
    private String street;
    private String zipcode;

    protected Address() { // 사람들이 호출할 수 없게 protected
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
