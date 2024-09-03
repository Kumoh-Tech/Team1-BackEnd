package com.club_board.club_board_server.domain;

import jakarta.persistence.*;



@Entity
public class Club {
    @Id @GeneratedValue
    @Column(name="club_id")
    private Long id;

    @Column(name="club_name")
    private String name;

    @ManyToOne
    @JoinColumn(name="category_id")
    private ClubCategory clubCategory;



    // helper method to add accession

}