package com.club_board.club_board_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ClubBoardServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClubBoardServerApplication.class, args);
	}

}
