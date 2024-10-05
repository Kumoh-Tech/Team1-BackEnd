package com.club_board.club_board_server.domain;

public enum Department {
    COMPUTER_ENGINEERING("컴퓨터공학과"),
    SOFTWARE_ENGINEERING("컴퓨터소프트웨어공학과"),
    ARTIFICIAL_INTELLIGENCE_ENGINEERING("인공지능공학과"),
    ARCHITECTURE("건축학과"),
    INDUSTRIAL_ENGINEERING("산업공학과"),
    OPTOELECTRONIC_ENGINEERING("광시스템공학과"),
    ADVANCED_MATERIALS_ENGINEERING("고분자공학과"),
    MEDICAL_BIOMEDICAL_ENGINEERING("메디컬융합공학과"),
    ENVIRONMENTAL_ENGINEERING("환경공학과"),
    MATERIALS_SCIENCE_ENGINEERING("소재디자인공학과"),
    CHEMICAL_ENGINEERING("화학공학과"),
    IT_CONVERGENCE("IT융합학과"),
    RENEWABLE_ENERGY("신소재공학부"),
    DATA_SCIENCE_ENGINEERING("수리빅데이터공학과"),
    BIOCHEMICAL_ENGINEERING("화학생명공학과"),
    MECHANICAL_ENGINEERING("기계공학과"),
    SMART_MOBILITY("스마트모빌리티전공"),
    SYSTEMS_ENGINEERING("기계시스템공학부"),
    BUSINESS_ADMINISTRATION("경영학과"),
    ELECTRICAL_ENGINEERING("전자공학부");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
